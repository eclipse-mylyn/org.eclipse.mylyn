/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.core.BuildRequest;
import org.eclipse.mylyn.builds.core.BuildRequest.Kind;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanData;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildWorkingCopy;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.core.spi.BuildServerConfiguration;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.core.util.RepositoryWebLocation;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelHealthReport;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;

/**
 * @author Markus Knittig
 */
public class HudsonServerBehaviour extends BuildServerBehaviour {

	private final RestfulHudsonClient client;

	public HudsonServerBehaviour(AbstractWebLocation location) {
		this.client = new RestfulHudsonClient(location);
	}

	public HudsonServerBehaviour(RepositoryLocation location) {
		this.client = new RestfulHudsonClient(new RepositoryWebLocation(location));
	}

	@Override
	public List<IBuild> getBuilds(BuildRequest request, IOperationMonitor monitor) throws CoreException {
		if (request.getKind() != Kind.LAST) {
			throw new UnsupportedOperationException();
		}
		try {
			HudsonModelJob job = new HudsonModelJob();
			job.setName(request.getPlan().getId());
			HudsonModelBuild build = new HudsonModelBuild();
			build.setNumber(request.getPlan().getLastBuild().getBuildNumber());
			build = client.getBuild(job, build, monitor);
			return Collections.singletonList(parseBuild(build));
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	public HudsonConfigurationCache getCache() {
		return client.getCache();
	}

	@Override
	public BuildServerConfiguration getConfiguration() {
		Map<String, String> jobNameById = client.getConfiguration().jobNameById;
		List<IBuildPlan> plans = new ArrayList<IBuildPlan>(jobNameById.size());
		for (Entry<String, String> entry : jobNameById.entrySet()) {
			IBuildPlanWorkingCopy plan = createBuildPlan();
			plan.setId(entry.getKey());
			plan.setName(entry.getValue());
			plans.add(plan.toBuildPlan());
		}
		return new BuildServerConfiguration(plans);
	}

	@Override
	public InputStream getConsole(IBuild build, IOperationMonitor monitor) throws CoreException {
		try {
			HudsonModelBuild hudsonBuild = new HudsonModelBuild();
			hudsonBuild.setId(build.getId());
			return client.getConsole(hudsonBuild, monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	@Override
	public List<IBuildPlanData> getPlans(IOperationMonitor monitor) throws CoreException {
		try {
			List<HudsonModelJob> jobs = client.getJobs(monitor);
			List<IBuildPlanData> plans = new ArrayList<IBuildPlanData>(jobs.size());
			for (HudsonModelJob job : jobs) {
				plans.add(parseJob(job));
			}
			return plans;
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	private IBuild parseBuild(HudsonModelBuild hudsonBuild) {
		IBuildWorkingCopy build = createBuild();
		build.setId(hudsonBuild.getId());
		build.setBuildNumber(hudsonBuild.getNumber());
		build.setLabel(hudsonBuild.getNumber() + "");
		build.setDuration(hudsonBuild.getDuration());
		build.setTimestamp(hudsonBuild.getTimestamp());
		return build;
	}

	public IBuildPlanData parseJob(HudsonModelJob job) {
		IBuildPlanWorkingCopy plan = createBuildPlan();
		plan.setId(job.getName());
		if (job.getDisplayName() != null && job.getDisplayName().length() > 0) {
			plan.setName(job.getDisplayName());
		} else {
			plan.setName(job.getName());
		}
		plan.setDescription(job.getDescription());
		plan.setUrl(job.getUrl());
		updateStateAndStatus(job, plan);
		updateHealth(job, plan);
		if (job.getLastBuild() != null) {
			IBuildWorkingCopy build = createBuild();
			build.setId(job.getLastBuild().getNumber() + "");
			build.setBuildNumber(job.getLastBuild().getNumber());
			build.setUrl(job.getLastBuild().getUrl());
			plan.setLastBuild(build);
		}
		return plan;
	}

	@Override
	public BuildServerConfiguration refreshConfiguration(IOperationMonitor monitor) throws CoreException {
		try {
			client.getJobs(monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
		return getConfiguration();
	}

	@Override
	public void runBuild(IBuildPlanData plan, IOperationMonitor monitor) throws CoreException {
		try {
			HudsonModelJob job = new HudsonModelJob();
			job.setUrl(plan.getUrl());
			client.runBuild(job, monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	public void setCache(HudsonConfigurationCache cache) {
		client.setCache(cache);
	}

	protected void updateHealth(HudsonModelJob job, IBuildPlanWorkingCopy plan) {
		String testResult = null;
		String buildResult = null;
		String result = null;
		List<HudsonModelHealthReport> report = job.getHealthReport();
		if (report.size() > 0) {
			plan.setHealth(report.get(0).getScore());
			for (HudsonModelHealthReport healthReport : report) {
				if (healthReport.getScore() < plan.getHealth()) {
					plan.setHealth(healthReport.getScore());
				}
				String description = healthReport.getDescription();
				if (description != null) {
					if (healthReport.getDescription().startsWith("Test Result: ")) {
						testResult = description.substring(13);
					} else if (healthReport.getDescription().startsWith("Build stability: ")) {
						buildResult = description.substring(17);
					} else {
						int i = description.indexOf(": ");
						if (i != -1) {
							result = description.substring(i + 2);
						} else {
							result = description;
						}
					}
				}
			}
			if (testResult != null) {
				plan.setSummary(testResult);
			} else if (buildResult != null) {
				plan.setSummary(buildResult);
			} else {
				plan.setSummary(result);
			}
		} else {
			plan.setHealth(-1);
		}
	}

	protected void updateStateAndStatus(HudsonModelJob job, IBuildPlanWorkingCopy plan) {
		if (job.getColor().equals(HudsonModelBallColor.BLUE)) {
			plan.setStatus(BuildStatus.SUCCESS);
			plan.setState(BuildState.STOPPED);
		} else if (job.getColor().equals(HudsonModelBallColor.BLUE_ANIME)) {
			plan.setStatus(BuildStatus.SUCCESS);
			plan.setState(BuildState.RUNNING);
		} else if (job.getColor().equals(HudsonModelBallColor.RED)) {
			plan.setStatus(BuildStatus.FAILED);
			plan.setState(BuildState.STOPPED);
		} else if (job.getColor().equals(HudsonModelBallColor.RED_ANIME)) {
			plan.setStatus(BuildStatus.FAILED);
			plan.setState(BuildState.RUNNING);
		} else if (job.getColor().equals(HudsonModelBallColor.YELLOW)) {
			plan.setStatus(BuildStatus.UNSTABLE);
			plan.setState(BuildState.STOPPED);
		} else if (job.getColor().equals(HudsonModelBallColor.YELLOW_ANIME)) {
			plan.setStatus(BuildStatus.UNSTABLE);
			plan.setState(BuildState.RUNNING);
		} else if (job.getColor().equals(HudsonModelBallColor.GREY)) {
			plan.setStatus(BuildStatus.DISABLED);
			plan.setState(BuildState.STOPPED);
		} else if (job.getColor().equals(HudsonModelBallColor.GREY_ANIME)) {
			plan.setStatus(BuildStatus.DISABLED);
			plan.setState(BuildState.RUNNING);
		} else if (job.getColor().equals(HudsonModelBallColor.DISABLED)) {
			plan.setStatus(BuildStatus.DISABLED);
			plan.setState(BuildState.STOPPED);
		} else if (job.getColor().equals(HudsonModelBallColor.DISABLED_ANIME)) {
			plan.setStatus(BuildStatus.DISABLED);
			plan.setState(BuildState.RUNNING);
		} else {
			plan.setStatus(null);
			plan.setState(null);
		}
	}

	@Override
	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		try {
			return client.validate(monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

}
