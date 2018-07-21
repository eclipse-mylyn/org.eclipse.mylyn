/*******************************************************************************
 * Copyright (c) 2010, 2016 Markus Knittig and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Eike Stepper - improvements for bug 323759
 *     Benjamin Muskalla - 324039: [build] tests fail with NPE
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core;

import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.EditType;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBooleanParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildCause;
import org.eclipse.mylyn.builds.core.IBuildFactory;
import org.eclipse.mylyn.builds.core.IBuildParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildReference;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.core.IChoiceParameterDefinition;
import org.eclipse.mylyn.builds.core.IFileParameterDefinition;
import org.eclipse.mylyn.builds.core.IHealthReport;
import org.eclipse.mylyn.builds.core.IParameterDefinition;
import org.eclipse.mylyn.builds.core.IPasswordParameterDefinition;
import org.eclipse.mylyn.builds.core.IStringParameterDefinition;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.IUser;
import org.eclipse.mylyn.builds.core.TestCaseResult;
import org.eclipse.mylyn.builds.core.spi.BuildPlanRequest;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.core.spi.BuildServerConfiguration;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Kind;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Scope;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonResourceNotFoundException;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonServerInfo;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonTestReport;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonUrlUtil;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient.BuildId;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelAbstractBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelHealthReport;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelRun;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelRunArtifact;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelUser;
import org.eclipse.mylyn.internal.hudson.model.HudsonScmChangeLogSet;
import org.eclipse.mylyn.internal.hudson.model.HudsonTasksJunitCaseResult;
import org.eclipse.mylyn.internal.hudson.model.HudsonTasksJunitSuiteResult;
import org.eclipse.mylyn.internal.hudson.model.HudsonTasksJunitTestResult;
import org.eclipse.mylyn.internal.hudson.model.HudsonTasksTestAggregatedTestResultAction;
import org.eclipse.mylyn.internal.hudson.model.HudsonTasksTestAggregatedTestResultActionChildReport;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 * @author Eike Stepper
 */
public class HudsonServerBehaviour extends BuildServerBehaviour {

	private final RestfulHudsonClient client;

	private final RepositoryLocation location;

	private final HudsonUrlUtil hudsonUrlUtil;

	public HudsonServerBehaviour(RepositoryLocation location, HudsonConfigurationCache cache) {
		this.location = location;
		this.client = new RestfulHudsonClient(location, cache);
		this.hudsonUrlUtil = new HudsonUrlUtil(this.location);
	}

	public RepositoryLocation getLocation() {
		return location;
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
		try {
			if (request.getKind() == Kind.LAST || request.getKind() == Kind.SELECTED) {
				HudsonModelJob job = createJobParameter(request.getPlan());
				// FIXME this is way too complicated and brittle
				HudsonModelBuild requestBuild;
				if (request.getKind() == Kind.LAST) {
					requestBuild = BuildId.LAST.getBuild();
				} else {
					requestBuild = new HudsonModelBuild();
					requestBuild.setNumber(Integer.parseInt(request.getIds().iterator().next()));
				}
				HudsonModelBuild hudsonBuild = client.getBuild(job, requestBuild, monitor);
				IBuild build = parseBuild(job, hudsonBuild);
				try {
					HudsonTestReport hudsonTestReport = client.getTestReport(job, hudsonBuild, monitor);
					ITestResult testResult;
					if (hudsonTestReport.getJunitResult() != null) {
						testResult = parseTestResult(hudsonTestReport.getJunitResult());
					} else {
						testResult = parseTestResult(hudsonTestReport.getAggregatedResult());
					}
					testResult.setBuild(build); // FIXME remove, should not be necessary
					build.setTestResult(testResult);
				} catch (HudsonResourceNotFoundException e) {
					// ignore
				}
				return Collections.singletonList(build);
			}

			if (request.getKind() == Kind.ALL && request.getScope() == Scope.HISTORY) {
				HudsonModelJob job = createJobParameter(request.getPlan());
				List<HudsonModelRun> hudsonBuilds = client.getBuilds(job, monitor);
				ArrayList<IBuild> builds = new ArrayList<IBuild>(hudsonBuilds.size());
				for (HudsonModelRun hudsonBuild : hudsonBuilds) {
					builds.add(parseBuild(job, hudsonBuild));
				}
				return builds;
			}
		} catch (HudsonResourceNotFoundException e) {
			return null;
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}

		// unsupported kind
		throw new UnsupportedOperationException("Unsupported request kind and scope combination: kind=" //$NON-NLS-1$
				+ request.getKind() + ",scope=" + request.getScope()); //$NON-NLS-1$
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

	/**
	 * Looks for a child named <code>name</code> that has a text value or a nested <code>value</code> tag.
	 */
	private String getDefaultValue(Element element) throws HudsonException {
		// parsed from job config
		String value = getElementContent(element, "defaultValue", false); //$NON-NLS-1$
		if (value != null) {
			return value;
		}
		// parsed from job actions
		NodeList nodes = element.getElementsByTagName("defaultParameterValue"); //$NON-NLS-1$
		if (nodes.getLength() == 1) {
			NodeList children = nodes.item(0).getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String tagName = child.getTagName();
				if ("value".equals(tagName)) { //$NON-NLS-1$
					return child.getTextContent();
				}
			}
		}
		return null;
	}

	private String getElementContent(Element element, String name, boolean required) throws HudsonException {
		NodeList elements = element.getElementsByTagName(name);
		if (elements.getLength() == 0) {
			if (required) {
				throw new HudsonException("No " + name + " element"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				return null;
			}
		}

		if (elements.getLength() > 1) {
			throw new HudsonException("More than one " + name + " element"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return ((Element) elements.item(0)).getTextContent();
	}

	@Override
	public List<IBuildPlan> getPlans(BuildPlanRequest request, IOperationMonitor monitor) throws CoreException {
		try {
			List<HudsonModelJob> jobs = client.getJobs(request.getPlanIds(), monitor);
			List<IBuildPlan> plans = new ArrayList<IBuildPlan>(jobs.size());
			for (HudsonModelJob job : jobs) {
				IBuildPlan plan = parseJob(job);
				plans.add(plan);

				// parsed from job now, see bug 328204
//				if (client.getInfo(monitor).getType() == Type.JENKINS) {
//					try {
//						Document document = client.getJobConfig(job, monitor);
//						parseParametersFromJobConfig(document, plan.getParameterDefinitions());
//					} catch (HudsonException e) {
//						// ignore, might not have permission to read config
//					} catch (ParserConfigurationException e) {
//						// ignore
//					} catch (SAXException e) {
//						// ignore
//					} catch (IOException e) {
//						// ignore
//					}
//				}
			}
			return plans;
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	private IArtifact parseArtifact(HudsonModelRunArtifact hudsonArtifact) {
		IArtifact artifact = createArtifact();
		artifact.setName(hudsonArtifact.getFileName());
		artifact.setRelativePath(hudsonArtifact.getRelativePath());
		return artifact;
	}

	private IBuild parseBuild(HudsonModelJob hudsonJob, HudsonModelRun hudsonBuild) {
		IBuild build = createBuild();
		build.setId(hudsonBuild.getId());
		build.setName(hudsonBuild.getFullDisplayName());
		build.setBuildNumber(hudsonBuild.getNumber());
		build.setLabel(hudsonBuild.getNumber() + ""); //$NON-NLS-1$
		build.setDuration(hudsonBuild.getDuration());
		build.setTimestamp(hudsonBuild.getTimestamp());
		build.setUrl(hudsonBuild.getUrl());
		build.setState(hudsonBuild.isBuilding() ? BuildState.RUNNING : BuildState.STOPPED);
		build.setStatus(parseResult((Node) hudsonBuild.getResult()));
		build.setSummary(parseActions(build.getCause(), hudsonBuild.getAction()));
		if (hudsonBuild instanceof HudsonModelAbstractBuild) {
			for (HudsonModelUser hudsonUser : ((HudsonModelAbstractBuild) hudsonBuild).getCulprit()) {
				build.getCulprits().add(parseUser(hudsonUser));
			}
		}
		for (HudsonModelRunArtifact hudsonArtifact : hudsonBuild.getArtifact()) {
			IArtifact artifact = parseArtifact(hudsonArtifact);
			try {
				artifact.setUrl(client.getArtifactUrl(hudsonJob, hudsonBuild, hudsonArtifact));
			} catch (HudsonException e) {
				// ignore
			}
			build.getArtifacts().add(artifact);
		}
		if (hudsonBuild instanceof HudsonModelAbstractBuild) {
			HudsonScmChangeLogSet changeSet = ((HudsonModelAbstractBuild) hudsonBuild).getChangeSet();
			if (changeSet != null) {
				build.setChangeSet(parseChangeSet(changeSet));
			}
		}
		return build;
	}

	private String parseActions(List<IBuildCause> causes, List<Object> actions) {
		int failCount = 0;
		int skipCount = 0;
		int totalCount = 0;
		Set<String> causeDescriptions = new LinkedHashSet<String>();
		for (Object action : actions) {
			Node node = (Node) action;
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String tagName = child.getTagName();
				try {
					if ("cause".equals(tagName)) { //$NON-NLS-1$
						IBuildCause cause = parseCause(child);
						causes.add(cause);
						if (cause.getDescription() != null) {
							causeDescriptions.add(cause.getDescription());
						}
					} else if ("failCount".equals(tagName)) { //$NON-NLS-1$
						failCount = Integer.parseInt(child.getTextContent());
					} else if ("skipCount".equals(tagName)) { //$NON-NLS-1$
						skipCount = Integer.parseInt(child.getTextContent());
					} else if ("totalCount".equals(tagName)) { //$NON-NLS-1$
						totalCount = Integer.parseInt(child.getTextContent());
					}
				} catch (NumberFormatException e) {
					// ignore
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String string : causeDescriptions) {
			append(sb, string);
		}
		if (failCount != 0 || totalCount != 0 || skipCount != 0) {
			append(sb,
					NLS.bind("{0} tests: {1} failed, {2} skipped", new Object[] { totalCount, failCount, skipCount }));
		}
		if (sb.length() > 0) {
			return sb.toString();
		}
		return null;
	}

	private void append(StringBuilder sb, String text) {
		if (text != null) {
			if (sb.length() > 0) {
				sb.append(", "); //$NON-NLS-1$
			}
			sb.append(text);
		}
	}

	private IBuildCause parseCause(Node node) {
		IBuildCause cause = createBuildCause();
		String userName = null;
		String upstreamBuild = null;
		String upstreamProject = null;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.item(i);
			String tagName = child.getTagName();
			if ("shortDescription".equals(tagName)) { //$NON-NLS-1$
				cause.setDescription(child.getTextContent());
			} else if ("userName".equals(tagName)) { //$NON-NLS-1$
				userName = child.getTextContent();
			} else if ("upstreamProject".equals(tagName)) { //$NON-NLS-1$
				upstreamProject = child.getTextContent();
			} else if ("upstreamBuild".equals(tagName)) { //$NON-NLS-1$
				upstreamBuild = child.getTextContent();
			}
		}
		if (userName != null) {
			IUser user = createUser();
			user.setId(userName);
			cause.setUser(user);
		}
		if (upstreamProject != null && upstreamBuild != null) {
			IBuildReference reference = createBuildReference();
			reference.setPlan(upstreamProject);
			reference.setBuild(upstreamBuild);
			cause.setBuild(reference);
		}
		return cause;
	}

	private IChange parseChange(Node node) {
		// addedPath* [M]
		// author [C, G, M]
		// comment [G]: full commit message
		// date [C:2010-09-02, G:2010-08-26 17:43:17 -0700, M:1283761613.0-7200, S:2010-07-28T09:11:55.720801Z]
		// file*: dead, editType, fullName, name, prerevision?, revision [C]
		// id [G] (SHA1)
		// merge [M] (Boolean)
		// modifiedPath* [M]
		// msg [C, G, M, S]: shortened commit message
		// node [M] (SHA1?)
		// path*: editType, file [G, S]
		// rev [M]
		// revision [S]
		// time [C:05:15]
		// user [C, S]
		IChange change = createChange();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.item(i);
			String tagName = child.getTagName();
			if ("addedPath".equals(tagName)) { //$NON-NLS-1$
				IChangeArtifact artifact = createChangeArtifact();
				artifact.setFile(child.getTextContent());
				artifact.setEditType(EditType.ADD);
				change.getArtifacts().add(artifact);
			} else if ("author".equals(tagName)) { //$NON-NLS-1$
				change.setAuthor(parseUser(child));
			} else if ("comment".equals(tagName)) { //$NON-NLS-1$
				change.setMessage(child.getTextContent());
			} else if ("date".equals(tagName)) { //$NON-NLS-1$
				change.setDate(parseDate(child));
			} else if ("file".equals(tagName)) { //$NON-NLS-1$
				change.getArtifacts().add(parseArtifact(child));
			} else if ("id".equals(tagName)) { //$NON-NLS-1$
				change.setRevision(child.getTextContent());
			} else if ("modifiedPath".equals(tagName)) { //$NON-NLS-1$
				IChangeArtifact artifact = createChangeArtifact();
				artifact.setFile(child.getTextContent());
				artifact.setEditType(EditType.EDIT);
				change.getArtifacts().add(artifact);
			} else if ("msg".equals(tagName)) { //$NON-NLS-1$
				// check if full comment was already retrieved from "comment" tag
				if (change.getMessage() == null) {
					change.setMessage(child.getTextContent());
				}
			} else if ("node".equals(tagName)) { //$NON-NLS-1$
				change.setRevision(child.getTextContent());
			} else if ("rev".equals(tagName)) { //$NON-NLS-1$
				change.setRevision(child.getTextContent());
			} else if ("path".equals(tagName)) { //$NON-NLS-1$
				change.getArtifacts().add(parseArtifact(child));
			} else if ("revision".equals(tagName)) { //$NON-NLS-1$
				change.setRevision(child.getTextContent());
			} else if ("user".equals(tagName) && change.getAuthor() == null) { //$NON-NLS-1$
				IUser user = createUser();
				user.setId(child.getTextContent());
				change.setAuthor(user);
			}
		}
		return change;
	}

	private IUser parseUser(Node node) {
		if (node != null) {
			IUser user = createUser();
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String tagName = child.getTagName();
				if ("absoluteUrl".equals(tagName)) { //$NON-NLS-1$
					user.setUrl(child.getTextContent());
				} else if ("fullName".equals(tagName)) { //$NON-NLS-1$
					user.setName(child.getTextContent());
					user.setId(child.getTextContent());
				}
			}
			return user;
		}
		return null;
	}

	private IChangeArtifact parseArtifact(Node node) {
		IChangeArtifact artifact = createChangeArtifact();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.item(i);
			String tagName = child.getTagName();
			if ("editType".equals(tagName)) { //$NON-NLS-1$
				artifact.setEditType(parseEditType(child));
			} else if ("file".equals(tagName)) { //$NON-NLS-1$
				artifact.setFile(child.getTextContent());
			} else if ("fullName".equals(tagName)) { //$NON-NLS-1$
				artifact.setFile(child.getTextContent());
			} else if ("prevrevision".equals(tagName)) { //$NON-NLS-1$
				artifact.setPrevRevision(child.getTextContent());
			} else if ("revision".equals(tagName)) { //$NON-NLS-1$
				artifact.setRevision(child.getTextContent());
			}
		}
		return artifact;
	}

	private EditType parseEditType(Element node) {
		if (node != null) {
			String text = node.getTextContent();
			if ("add".equals(text)) { //$NON-NLS-1$
				return EditType.ADD;
			} else if ("edit".equals(text)) { //$NON-NLS-1$
				return EditType.EDIT;
			}
			if ("delete".equals(text)) { //$NON-NLS-1$
				return EditType.DELETE;
			}
		}
		return null;
	}

	private long parseDate(Element node) {
		if (node != null) {
			String[] patterns = { //
					"yyyy-MM-dd", // cvs //$NON-NLS-1$
					"yyyy-MM-dd HH:mm:ss Z", // git		 //$NON-NLS-1$
					//"1283761613.0-7200" // mercurial
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" // svn //$NON-NLS-1$
			};
			String text = node.getTextContent();
			for (String pattern : patterns) {
				try {
					return new SimpleDateFormat(pattern).parse(text).getTime();
				} catch (ParseException e) {
					// fall through
				}
			}
		}
		return 0L;
	}

	private IChangeSet parseChangeSet(HudsonScmChangeLogSet hudsonChangeSet) {
		IChangeSet changeSet = createChangeSet();
		// none (git), cvs, hg, svn
		changeSet.setKind(hudsonChangeSet.getKind());
		// changeSet.getRevisions() [S]
		for (Object item : hudsonChangeSet.getItem()) {
			changeSet.getChanges().add(parseChange((Node) item));
		}
		return changeSet;
	}

	private long parseDuration(Node node) {
		if (node != null) {
			String text = node.getTextContent();
			try {
				return (long) (Double.parseDouble(text) * 1000);
			} catch (NumberFormatException e) {
				// fall through
			}
		}
		return -1L;
	}

	private IHealthReport parseHealthReport(HudsonModelHealthReport hudsonHealthReport) {
		IHealthReport healthReport = createHealthReport();
		healthReport.setHealth(hudsonHealthReport.getScore());
		healthReport.setDescription(hudsonHealthReport.getDescription());
		return healthReport;
	}

	public IBuildPlan parseJob(HudsonModelJob job) throws HudsonException {
		IBuildPlan plan = createBuildPlan();

		String jobUrl = job.getUrl();
		if (this.hudsonUrlUtil.isNestedJob(jobUrl)) {
			plan.setId(jobUrl);
		} else {
			plan.setId(job.getName());
		}
		plan.setName(this.hudsonUrlUtil.getDisplayName(jobUrl));
		plan.setDescription(job.getDescription());
		plan.setUrl(job.getUrl());
		updateStateAndStatus(job, plan);
		updateHealth(job, plan);
		if (job.getLastBuild() != null) {
			IBuild build = createBuild();
			build.setId(job.getLastBuild().getNumber() + ""); //$NON-NLS-1$
			build.setLabel(job.getLastBuild().getNumber() + ""); //$NON-NLS-1$
			build.setBuildNumber(job.getLastBuild().getNumber());
			build.setUrl(job.getLastBuild().getUrl());
			plan.setLastBuild(build);
		}
		parseJobActions(plan.getParameterDefinitions(), job.getAction());
		// TODO parse up/down stream projects from HudsonModelAbstractProject
		return plan;
	}

	private void parseJobActions(List<IParameterDefinition> parameterDefinitions, List<Object> actions) {
		for (Object action : actions) {
			Node node = (Node) action;
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String tagName = child.getTagName();
				if ("parameterDefinition".equals(tagName)) { //$NON-NLS-1$
					try {
						IParameterDefinition parameterDefinition = parseParameter(child);
						parameterDefinitions.add(parameterDefinition);
					} catch (HudsonException e) {
						// unknown parameter type, ignore
					}
				}
			}
		}
	}

	private IParameterDefinition parseParameter(Element element) throws HudsonException {
		String tagName = element.getTagName();
		if ("hudson.model.ChoiceParameterDefinition".equals(tagName) //$NON-NLS-1$
				|| "ChoiceParameterDefinition".equals(getElementContent(element, "type", false))) { //$NON-NLS-1$ //$NON-NLS-2$
			IChoiceParameterDefinition definition = BuildFactory.eINSTANCE.createChoiceParameterDefinition();
			definition.setName(getElementContent(element, "name", true)); //$NON-NLS-1$
			definition.setDescription(getElementContent(element, "description", false)); //$NON-NLS-1$
			// parsed from job config
			NodeList options = element.getElementsByTagName("string"); //$NON-NLS-1$
			for (int i = 0; i < options.getLength(); i++) {
				Element option = (Element) options.item(i);
				definition.getOptions().add(option.getTextContent());
			}
			// parsed from job actions
			options = element.getElementsByTagName("choice"); //$NON-NLS-1$
			for (int i = 0; i < options.getLength(); i++) {
				Element option = (Element) options.item(i);
				definition.getOptions().add(option.getTextContent());
			}
			definition.setDefaultValue(getDefaultValue(element));
			return definition;
		}

		if ("hudson.model.BooleanParameterDefinition".equals(tagName) //$NON-NLS-1$
				|| "BooleanParameterDefinition".equals(getElementContent(element, "type", false))) { //$NON-NLS-1$ //$NON-NLS-2$
			IBooleanParameterDefinition definition = IBuildFactory.INSTANCE.createBooleanParameterDefinition();
			definition.setName(getElementContent(element, "name", true)); //$NON-NLS-1$
			definition.setDescription(getElementContent(element, "description", false)); //$NON-NLS-1$
			String defaultValue = getDefaultValue(element);
			if (defaultValue != null) {
				definition.setDefaultValue(Boolean.parseBoolean(defaultValue));
			}
			return definition;
		}

		if ("hudson.model.StringParameterDefinition".equals(tagName) //$NON-NLS-1$
				|| "StringParameterDefinition".equals(getElementContent(element, "type", false))) { //$NON-NLS-1$ //$NON-NLS-2$
			IStringParameterDefinition definition = IBuildFactory.INSTANCE.createStringParameterDefinition();
			definition.setName(getElementContent(element, "name", true)); //$NON-NLS-1$
			definition.setDescription(getElementContent(element, "description", false)); //$NON-NLS-1$
			definition.setDefaultValue(getDefaultValue(element));
			return definition;
		}

		if ("hudson.model.PasswordParameterDefinition".equals(tagName) //$NON-NLS-1$
				|| "PasswordParameterDefinition".equals(getElementContent(element, "type", false))) { //$NON-NLS-1$ //$NON-NLS-2$
			IPasswordParameterDefinition definition = IBuildFactory.INSTANCE.createPasswordParameterDefinition();
			definition.setName(getElementContent(element, "name", true)); //$NON-NLS-1$
			definition.setDescription(getElementContent(element, "description", false)); //$NON-NLS-1$
			definition.setDefaultValue(getDefaultValue(element));
			return definition;
		}

		if ("hudson.model.RunParameterDefinition".equals(tagName) //$NON-NLS-1$
				|| "RunParameterDefinition".equals(getElementContent(element, "type", false))) { //$NON-NLS-1$ //$NON-NLS-2$
			IBuildParameterDefinition definition = IBuildFactory.INSTANCE.createBuildParameterDefinition();
			definition.setName(getElementContent(element, "name", true)); //$NON-NLS-1$
			definition.setDescription(getElementContent(element, "description", false)); //$NON-NLS-1$
			definition.setBuildPlanId(getElementContent(element, "projectName", false)); //$NON-NLS-1$
			return definition;
		}

		if ("hudson.model.FileParameterDefinition".equals(tagName) //$NON-NLS-1$
				|| "FileParameterDefinition".equals(getElementContent(element, "type", false))) { //$NON-NLS-1$ //$NON-NLS-2$
			IFileParameterDefinition definition = IBuildFactory.INSTANCE.createFileParameterDefinition();
			definition.setName(getElementContent(element, "name", true)); //$NON-NLS-1$
			definition.setDescription(getElementContent(element, "description", false)); //$NON-NLS-1$
			return definition;
		}

		throw new HudsonException(NLS.bind("Unexpected parameter ''{0}'' with type ''{1}''", tagName,
				getElementContent(element, "type", false))); //$NON-NLS-1$
	}

//	private void parseParametersFromJobConfig(Document document, List<IParameterDefinition> definitions)
//			throws ParserConfigurationException, SAXException, IOException, HudsonException {
//		NodeList containers = document.getElementsByTagName("parameterDefinitions"); //$NON-NLS-1$
//		for (int i = 0; i < containers.getLength(); i++) {
//			Element container = (Element) containers.item(i);
//			NodeList elements = container.getChildNodes();
//			for (int j = 0; j < elements.getLength(); j++) {
//				Node node = elements.item(j);
//				if (node instanceof Element) {
//					Element element = (Element) elements.item(j);
//					IParameterDefinition definition = parseParameter(element);
//					definitions.add(definition);
//				}
//			}
//		}
//	}

	private BuildStatus parseResult(Node node) {
		if (node != null) {
			String text = node.getTextContent();
			if ("FAILURE".equals(text)) { //$NON-NLS-1$
				return BuildStatus.FAILED;
			}
			try {
				return BuildStatus.valueOf(text);
			} catch (IllegalArgumentException e) {
				// fall through
			}
		}
		return null;
	}

	private ITestResult parseTestResult(HudsonTasksJunitTestResult hudsonTestReport) {
		ITestResult testResult = createTestResult();
		testResult.setFailCount(hudsonTestReport.getFailCount());
		testResult.setIgnoredCount(hudsonTestReport.getSkipCount());
		testResult.setPassCount(hudsonTestReport.getPassCount());
		testResult.setDuration(parseDuration((Node) hudsonTestReport.getDuration()));
		for (HudsonTasksJunitSuiteResult hudsonSuite : hudsonTestReport.getSuite()) {
			ITestSuite testSuite = createTestSuite();
			testSuite.setLabel(hudsonSuite.getName());
			testSuite.setDuration(parseDuration((Node) hudsonSuite.getDuration()));
			testSuite.setOutput(hudsonSuite.getStdout());
			testSuite.setErrorOutput(hudsonSuite.getStderr());
			for (HudsonTasksJunitCaseResult hudsonCase : hudsonSuite.getCase()) {
				ITestCase testCase = createTestCase();
				testCase.setLabel(hudsonCase.getName());
				testCase.setClassName(hudsonCase.getClassName());
				testCase.setDuration(parseDuration((Node) hudsonCase.getDuration()));
				testCase.setSkipped(hudsonCase.isSkipped());
				// XXX seems redundant with testResult output
				//testCase.setOutput(hudsonCase.getStdout());
				//testCase.setErrorOutput(hudsonCase.getStderr());
				testCase.setMessage(hudsonCase.getErrorDetails());
				testCase.setStackTrace(hudsonCase.getErrorStackTrace());
				switch (hudsonCase.getStatus()) {
				case PASSED:
					testCase.setStatus(TestCaseResult.PASSED);
					break;
				case SKIPPED:
					testCase.setStatus(TestCaseResult.SKIPPED);
					break;
				case FAILED:
					testCase.setStatus(TestCaseResult.FAILED);
					break;
				case FIXED:
					testCase.setStatus(TestCaseResult.FIXED);
					break;
				case REGRESSION:
					testCase.setStatus(TestCaseResult.REGRESSION);
					break;

				}
				testCase.setSuite(testSuite);
			}
			testSuite.setResult(testResult);
		}
		return testResult;
	}

	private ITestResult parseTestResult(HudsonTasksTestAggregatedTestResultAction hudsonTestReport) {
		ITestResult testResult = createTestResult();
		testResult.setFailCount(hudsonTestReport.getFailCount());
		testResult.setIgnoredCount(hudsonTestReport.getSkipCount());
		testResult.setPassCount(
				hudsonTestReport.getTotalCount() - hudsonTestReport.getFailCount() - hudsonTestReport.getSkipCount());
		for (HudsonTasksTestAggregatedTestResultActionChildReport child : hudsonTestReport.getChildReport()) {
			ITestResult childResult = parseTestResult((HudsonTasksJunitTestResult) child.getResult());
			testResult.getSuites().addAll(childResult.getSuites());
		}
		return testResult;
	}

	private IUser parseUser(HudsonModelUser hudsonUser) {
		IUser user = createUser();
		user.setId(hudsonUser.getId());
		user.setName(hudsonUser.getFullName());
		user.setUrl(hudsonUser.getAbsoluteUrl());
		return user;
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

	@Override
	public void abortBuild(IBuild build, IOperationMonitor monitor) throws CoreException {
		try {
			HudsonModelJob job = createJobParameter(build.getPlan());
			HudsonModelBuild modelBuild = createBuildParameter(build);
			client.abortBuild(job, modelBuild, monitor);
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}

	protected void updateHealth(HudsonModelJob job, IBuildPlan plan) {
		String testResult = null;
		String buildResult = null;
		String result = null;
		List<HudsonModelHealthReport> husonHealthReports = job.getHealthReport();
		if (husonHealthReports.size() > 0) {
			plan.setHealth(husonHealthReports.get(0).getScore());
			for (HudsonModelHealthReport hudsonHealthReport : husonHealthReports) {
				plan.getHealthReports().add(parseHealthReport(hudsonHealthReport));

				// compute summary
				if (hudsonHealthReport.getScore() < plan.getHealth()) {
					plan.setHealth(hudsonHealthReport.getScore());
				}
				String description = hudsonHealthReport.getDescription();
				if (description != null) {
					if (hudsonHealthReport.getDescription().startsWith("Test Result: ")) { //$NON-NLS-1$
						if (testResult == null) {
							testResult = description.substring(13);
						}
					} else if (hudsonHealthReport.getDescription().startsWith("Build stability: ")) { //$NON-NLS-1$
						if (buildResult == null) {
							buildResult = description.substring(17);
						}
					} else if (result == null) {
						int i = description.indexOf(": "); //$NON-NLS-1$
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
		HudsonModelBallColor color = job.getColor();
		if (color == null) {
			plan.setStatus(null);
			plan.setState(null);
		} else {
			switch (color) {
			case BLUE:
			case GREEN:
				plan.setStatus(BuildStatus.SUCCESS);
				plan.setState(BuildState.STOPPED);
				break;
			case BLUE_ANIME:
			case GREEN_ANIME:
				plan.setStatus(BuildStatus.SUCCESS);
				plan.setState(BuildState.RUNNING);
				break;
			case RED:
				plan.setStatus(BuildStatus.FAILED);
				plan.setState(BuildState.STOPPED);
				break;
			case RED_ANIME:
				plan.setStatus(BuildStatus.FAILED);
				plan.setState(BuildState.RUNNING);
				break;
			case YELLOW:
				plan.setStatus(BuildStatus.UNSTABLE);
				plan.setState(BuildState.STOPPED);
				break;
			case YELLOW_ANIME:
				plan.setStatus(BuildStatus.UNSTABLE);
				plan.setState(BuildState.RUNNING);
				break;
			case GREY:
				plan.setStatus(BuildStatus.DISABLED);
				plan.setState(BuildState.STOPPED);
				break;
			case GREY_ANIME:
				plan.setStatus(BuildStatus.DISABLED);
				plan.setState(BuildState.RUNNING);
				break;
			case DISABLED:
				plan.setStatus(BuildStatus.DISABLED);
				plan.setState(BuildState.STOPPED);
				break;
			case DISABLED_ANIME:
				plan.setStatus(BuildStatus.DISABLED);
				plan.setState(BuildState.RUNNING);
				break;
			case ABORTED:
				plan.setStatus(BuildStatus.ABORTED);
				plan.setState(BuildState.STOPPED);
				break;
			case ABORTED_ANIME:
				plan.setStatus(BuildStatus.ABORTED);
				plan.setState(BuildState.RUNNING);
				break;
			case NOTBUILT:
				plan.setStatus(BuildStatus.NOT_BUILT);
				plan.setState(BuildState.STOPPED);
				break;
			case NOTBUILT_ANIME:
				plan.setStatus(BuildStatus.NOT_BUILT);
				plan.setState(BuildState.RUNNING);
				break;
			default:
				plan.setStatus(null);
				plan.setState(null);
			}
		}

		EnumSet<BuildState> flags = EnumSet.noneOf(BuildState.class);
		if (plan.getState() != null) {
			flags.add(plan.getState());
		}
		if (job.isInQueue()) {
			flags.add(BuildState.QUEUED);
		}
		if (job.isBuildable()) {
			flags.add(BuildState.BUILDABLE);
		}
		plan.getFlags().addAll(flags);
	}

	@Override
	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		try {
			HudsonServerInfo info = client.validate(monitor);
			HudsonStatus status = new HudsonStatus(IStatus.OK, HudsonCorePlugin.ID_PLUGIN,
					NLS.bind(Messages.HudsonServerBehaviour_Validation_succesful, info.getVersion()));
			status.setInfo(info);
			return status;
		} catch (HudsonException e) {
			throw HudsonCorePlugin.toCoreException(e);
		}
	}
}
