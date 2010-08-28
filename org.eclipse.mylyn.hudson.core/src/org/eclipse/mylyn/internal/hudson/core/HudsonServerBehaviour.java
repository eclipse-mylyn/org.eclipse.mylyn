/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Eike Stepper - improvements for bug 323759
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBooleanParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildFactory;
import org.eclipse.mylyn.builds.core.IBuildParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IChoiceParameterDefinition;
import org.eclipse.mylyn.builds.core.IFileParameterDefinition;
import org.eclipse.mylyn.builds.core.IParameterDefinition;
import org.eclipse.mylyn.builds.core.IPasswordParameterDefinition;
import org.eclipse.mylyn.builds.core.IStringParameterDefinition;
import org.eclipse.mylyn.builds.core.spi.BuildPlanRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Kind;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.core.spi.BuildServerConfiguration;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.builds.internal.core.util.RepositoryWebLocation;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient.BuildId;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelHealthReport;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 * @author Eike Stepper
 */
public class HudsonServerBehaviour extends BuildServerBehaviour {

	private final RestfulHudsonClient client;

	public HudsonServerBehaviour(AbstractWebLocation location) {
		this.client = new RestfulHudsonClient(location);
	}

	public HudsonServerBehaviour(RepositoryLocation location) {
		this.client = new RestfulHudsonClient(new RepositoryWebLocation(location));
	}

	protected HudsonModelBuild createBuildParameter(IBuild build) {
		HudsonModelBuild hudsonBuild = new HudsonModelBuild();
		hudsonBuild.setNumber(build.getBuildNumber());
		return hudsonBuild;
	}

	protected HudsonModelJob createJobParameter(IBuildPlan plan) {
		HudsonModelJob job = new HudsonModelJob();
		job.setName(plan.getId());
		return job;
	}

	@Override
	public List<IBuild> getBuilds(GetBuildsRequest request, IOperationMonitor monitor) throws CoreException {
		if (request.getKind() != Kind.LAST) {
			throw new UnsupportedOperationException();
		}
		try {
			HudsonModelJob job = createJobParameter(request.getPlan());
			HudsonModelBuild build = client.getBuild(job, BuildId.LAST.getBuild(), monitor);
			return Collections.singletonList(parseBuild(build));
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	private IBuild parseBuild(HudsonModelBuild hudsonBuild) {
		IBuild build = createBuild();
		build.setId(hudsonBuild.getId());
		build.setBuildNumber(hudsonBuild.getNumber());
		build.setLabel(hudsonBuild.getNumber() + "");
		build.setDuration(hudsonBuild.getDuration());
		build.setTimestamp(hudsonBuild.getTimestamp());
		return build;
	}

	public HudsonConfigurationCache getCache() {
		return client.getCache();
	}

	@Override
	public BuildServerConfiguration getConfiguration() {
		Map<String, String> jobNameById = client.getConfiguration().jobNameById;
		List<IBuildPlan> plans = new ArrayList<IBuildPlan>(jobNameById.size());
		for (Entry<String, String> entry : jobNameById.entrySet()) {
			IBuildPlan plan = createBuildPlan();
			plan.setId(entry.getKey());
			plan.setName(entry.getValue());
			plans.add(plan);
		}
		return new BuildServerConfiguration(plans);
	}

	@Override
	public Reader getConsole(IBuild build, IOperationMonitor monitor) throws CoreException {
		try {
			HudsonModelJob job = createJobParameter(build.getPlan());
			HudsonModelBuild hudsonBuild = createBuildParameter(build);
			return client.getConsole(job, hudsonBuild, monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	@Override
	public List<IBuildPlan> getPlans(BuildPlanRequest request, IOperationMonitor monitor) throws CoreException {
		try {
			List<HudsonModelJob> jobs = client.getJobs(request.getPlanIds(), monitor);
			List<IBuildPlan> plans = new ArrayList<IBuildPlan>(jobs.size());
			for (HudsonModelJob job : jobs) {
				org.eclipse.mylyn.builds.internal.core.BuildPlan plan = (org.eclipse.mylyn.builds.internal.core.BuildPlan) parseJob(job); // TODO Bad cast ;-(
				plans.add(plan);

				// TODO Do this in parallel for multiple jobs
				try {
					Document document = client.getJobConfig(job, monitor);
					parseParameters(document, plan.getParameterDefinitions());
				} catch (HudsonException e) {
					// ignore, might not have permission to read config
				}
			}
			return plans;
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e); // TODO Why must e be a HUDSONexception?
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, HudsonCorePlugin.PLUGIN_ID, "Unexpected error: "
					+ e.getMessage(), e));
		}
	}

	private void parseParameters(Document document, List<IParameterDefinition> definitions)
			throws ParserConfigurationException, SAXException, IOException, HudsonException {

		NodeList containers = document.getElementsByTagName("parameterDefinitions"); //$NON-NLS-1$
		for (int i = 0; i < containers.getLength(); i++) {
			Element container = (Element) containers.item(i);
			NodeList elements = container.getChildNodes();
			for (int j = 0; j < elements.getLength(); j++) {
				Node node = elements.item(j);
				if (node instanceof Element) {
					Element element = (Element) elements.item(j);
					IParameterDefinition definition = parseParameter(element);
					definitions.add(definition);
				}
			}
		}
	}

	private IParameterDefinition parseParameter(Element element) throws HudsonException {
		String tagName = element.getTagName();
		if ("hudson.model.ChoiceParameterDefinition".equals(tagName)) { //$NON-NLS-1$
			IChoiceParameterDefinition definition = BuildFactory.eINSTANCE.createChoiceParameterDefinition();
			definition.setName(getElementContent(element, "name", true));
			definition.setDescription(getElementContent(element, "description", false));
			NodeList options = element.getElementsByTagName("string"); //$NON-NLS-1$
			for (int i = 0; i < options.getLength(); i++) {
				Element option = (Element) options.item(i);
				definition.getOptions().add(option.getTextContent());
			}

			return definition;
		}

		if ("hudson.model.BooleanParameterDefinition".equals(tagName)) { //$NON-NLS-1$
			IBooleanParameterDefinition definition = IBuildFactory.INSTANCE.createBooleanParameterDefinition();
			definition.setName(getElementContent(element, "name", true));
			definition.setDescription(getElementContent(element, "description", false));
			String defaultValue = getElementContent(element, "defaultValue", false);
			if (defaultValue != null) {
				definition.setDefaultValue(Boolean.parseBoolean(defaultValue));
			}

			return definition;
		}

		if ("hudson.model.StringParameterDefinition".equals(tagName)) { //$NON-NLS-1$
			IStringParameterDefinition definition = IBuildFactory.INSTANCE.createStringParameterDefinition();
			definition.setName(getElementContent(element, "name", true));
			definition.setDescription(getElementContent(element, "description", false));
			definition.setDefaultValue(getElementContent(element, "defaultValue", false));

			return definition;
		}

		if ("hudson.model.PasswordParameterDefinition".equals(tagName)) { //$NON-NLS-1$
			IPasswordParameterDefinition definition = IBuildFactory.INSTANCE.createPasswordParameterDefinition();
			definition.setName(getElementContent(element, "name", true));
			definition.setDescription(getElementContent(element, "description", false));
			definition.setDefaultValue(getElementContent(element, "defaultValue", false));

			return definition;
		}

		if ("hudson.model.RunParameterDefinition".equals(tagName)) { //$NON-NLS-1$
			IBuildParameterDefinition definition = IBuildFactory.INSTANCE.createBuildParameterDefinition();
			definition.setName(getElementContent(element, "name", true));
			definition.setDescription(getElementContent(element, "description", false));
			definition.setBuildPlanId(getElementContent(element, "projectName", false));

			return definition;
		}

		if ("hudson.model.FileParameterDefinition".equals(tagName)) { //$NON-NLS-1$
			IFileParameterDefinition definition = IBuildFactory.INSTANCE.createFileParameterDefinition();
			definition.setName(getElementContent(element, "name", true));
			definition.setDescription(getElementContent(element, "description", false));

			return definition;
		}

		throw new HudsonException("Unexpected parameter type: " + tagName);
	}

	private String getElementContent(Element element, String name, boolean required) throws HudsonException {
		NodeList elements = element.getElementsByTagName(name);
		if (required && elements.getLength() == 0) {
			throw new HudsonException("No " + name + " element"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (elements.getLength() > 1) {
			throw new HudsonException("More than one " + name + " element"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return ((Element) elements.item(0)).getTextContent();
	}

	public IBuildPlan parseJob(HudsonModelJob job) {
		IBuildPlan plan = createBuildPlan();
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
			IBuild build = createBuild();
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
			client.getJobs(null, monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
		return getConfiguration();
	}

	@Override
	public void runBuild(RunBuildRequest request, IOperationMonitor monitor) throws CoreException {
		try {
			HudsonModelJob job = createJobParameter(request.getPlan());
			client.runBuild(job, request.getParameters(), monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	public void setCache(HudsonConfigurationCache cache) {
		client.setCache(cache);
	}

	protected void updateHealth(HudsonModelJob job, IBuildPlan plan) {
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

	protected void updateStateAndStatus(HudsonModelJob job, IBuildPlan plan) {
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
