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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanData;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
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
	public BuildServerConfiguration refreshConfiguration(IOperationMonitor monitor) throws CoreException {
		try {
			client.getJobs(monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
		return getConfiguration();
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

	public IBuildPlanData parseJob(HudsonModelJob job) {
		IBuildPlanWorkingCopy plan = createBuildPlan();
		plan.setId(job.getName());
		if (job.getDisplayName() != null && job.getDisplayName().length() > 0) {
			plan.setName(job.getDisplayName());
		} else {
			plan.setName(job.getName());
		}
		plan.setSummary(job.getDescription());
		plan.setUrl(job.getUrl());
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
		List<HudsonModelHealthReport> report = job.getHealthReport();
		if (report.size() > 0) {
			plan.setHealth(report.get(0).getScore());
			for (HudsonModelHealthReport healthReport : report) {
				if (healthReport.getScore() < plan.getHealth()) {
					plan.setHealth(healthReport.getScore());
				}
			}
		} else {
			plan.setHealth(-1);
		}
		return plan;
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

	@Override
	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		try {
			return client.validate(monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

}
