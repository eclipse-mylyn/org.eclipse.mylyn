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
 *     Benjamin Muskalla - 323920: [build] config retrival fails for jobs with whitespaces
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.builds.core.spi.AbstractConfigurationCache;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.internal.hudson.model.HudsonMavenReportersSurefireAggregatedReport;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelHudson;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelProject;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelRun;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelRunArtifact;
import org.eclipse.mylyn.internal.hudson.model.HudsonTasksJunitTestResult;
import org.eclipse.mylyn.internal.hudson.model.HudsonTasksTestAggregatedTestResultActionChildReport;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsServerInfo.Type;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents the Hudson repository that is accessed through REST.
 *
 * @author Markus Knittig
 * @author Steffen Pingel
 * @author Eike Stepper
 */
public class RestfulJenkinsClient {

	public enum BuildId {
		LAST(-1), LAST_FAILED(-5), LAST_STABLE(-2), LAST_SUCCESSFUL(-3), LAST_UNSTABLE(-4);

		private HudsonModelBuild build;

		BuildId(int id) {
			build = new HudsonModelBuild();
			build.setNumber(id);
		}

		public HudsonModelBuild getBuild() {
			return build;
		}

	}

	private static final String URL_API = "/api/xml"; //$NON-NLS-1$

	private static final int JOB_RETRIEVE_BATCH_SIZE = 50;

	private AbstractConfigurationCache<JenkinsConfiguration> cache;

	private final CommonHttpClient client;

	private final JenkinsUrlUtil jenkinsUrlUtil;

	private volatile JenkinsServerInfo info;

	public RestfulJenkinsClient(RepositoryLocation location, JenkinsConfigurationCache cache) {
		// FIXME register listener to location to handle credential changes
		client = new CommonHttpClient(location);
		setCache(cache);
		jenkinsUrlUtil = new JenkinsUrlUtil(location);
	}

	public List<HudsonModelRun> getBuilds(final HudsonModelJob job, final IOperationMonitor monitor)
			throws JenkinsException {

		return new JenkinsOperation<List<HudsonModelRun>>(client) {
			@Override
			public List<HudsonModelRun> execute() throws IOException, JenkinsException, JAXBException {
				String url = JenkinsUrl.create(getJobUrl(job))
						.depth(1)
						.tree("builds[number,url,building,result,duration,timestamp,actions[causes[shortDescription],failCount,totalCount,skipCount]]") //$NON-NLS-1$
						.toUrl();
				HttpRequestBase request = createGetRequest(url);
				CommonHttpResponse response = execute(request, monitor);
				return processAndRelease(response, monitor);
			}

			@Override
			protected List<HudsonModelRun> doProcess(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException, JAXBException {
				InputStream in = response.getResponseEntityAsStream();
				HudsonModelProject project = unmarshal(parse(in, response.getRequestPath()), HudsonModelProject.class);
				return project.getBuild();
			}
		}.run();
	}

	public HudsonModelBuild getBuild(final HudsonModelJob job, final HudsonModelRun build,
			final IOperationMonitor monitor) throws JenkinsException {
		return new JenkinsOperation<HudsonModelBuild>(client) {
			@Override
			public HudsonModelBuild execute() throws IOException, JenkinsException, JAXBException {
				String url = getBuildUrl(job, build) + URL_API;
				HttpRequestBase request = createGetRequest(url);
				CommonHttpResponse response = execute(request, monitor);
				return processAndRelease(response, monitor);
			}

			@Override
			protected HudsonModelBuild doProcess(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException, JAXBException {
				InputStream in = response.getResponseEntityAsStream();
				HudsonModelBuild hudsonBuild = unmarshal(parse(in, response.getRequestPath()), HudsonModelBuild.class);
				return hudsonBuild;
			}
		}.run();
	}

	public JenkinsTestReport getTestReport(final HudsonModelJob job, final HudsonModelRun build,
			final IOperationMonitor monitor) throws JenkinsException {

		return new JenkinsOperation<JenkinsTestReport>(client) {
			@Override
			public JenkinsTestReport execute() throws IOException, JenkinsException, JAXBException {
				//				String url = JenkinsUrl.create(getBuildUrl(job, build) + "/testReport" + URL_API).exclude(
				//						"/testResult/suite/case/stdout").exclude("/testResult/suite/case/stderr").toUrl();
				// need to scope retrieved data due to http://issues.hudson-ci.org/browse/HUDSON-7399
				String resultTree = "duration,failCount,passCount,skipCount,suites[cases[className,duration,errorDetails,errorStackTrace,failedSince,name,skipped,status],duration,name,stderr,stdout]"; //$NON-NLS-1$
				String aggregatedTree = "failCount,skipCount,totalCount,childReports[child[number,url],result[" //$NON-NLS-1$
						+ resultTree + "]]"; //$NON-NLS-1$
				String url = JenkinsUrl.create(getBuildUrl(job, build) + "/testReport") //$NON-NLS-1$
						.tree(resultTree + "," + aggregatedTree) //$NON-NLS-1$
						.toUrl();
				HttpRequestBase request = createGetRequest(url);
				CommonHttpResponse response = execute(request, monitor);
				return processAndRelease(response, monitor);
			}

			@Override
			protected JenkinsTestReport doProcess(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException, JAXBException {
				InputStream in = response.getResponseEntityAsStream();
				Element element = parse(in, response.getRequestPath());
				if ("surefireAggregatedReport".equals(element.getNodeName())) { //$NON-NLS-1$
					HudsonMavenReportersSurefireAggregatedReport report = unmarshal(element,
							HudsonMavenReportersSurefireAggregatedReport.class);
					// unmarshal nested test results
					for (HudsonTasksTestAggregatedTestResultActionChildReport child : report.getChildReport()) {
						child.setResult(RestfulJenkinsClient.unmarshal((Node) child.getResult(),
								HudsonTasksJunitTestResult.class));
					}
					return new JenkinsTestReport(report);
				}
				return new JenkinsTestReport(unmarshal(element, HudsonTasksJunitTestResult.class));
			}
		}.run();
	}

	protected String getBuildUrl(final HudsonModelJob job, final HudsonModelRun build) throws JenkinsException {
		if (build.getNumber() == -1) {
			return getJobUrl(job) + "/lastBuild"; //$NON-NLS-1$
		} else {
			return getJobUrl(job) + "/" + build.getNumber(); //$NON-NLS-1$
		}
	}

	public String getArtifactUrl(final HudsonModelJob job, final HudsonModelRun build, HudsonModelRunArtifact artifact)
			throws JenkinsException {
		return getBuildUrl(job, build) + "/artifact/" + artifact.getRelativePath(); //$NON-NLS-1$
	}

	public AbstractConfigurationCache<JenkinsConfiguration> getCache() {
		return cache;
	}

	public JenkinsConfiguration getConfiguration() {
		return getCache().getConfiguration(client.getLocation().getUrl());
	}

	public Reader getConsole(final HudsonModelJob job, final HudsonModelBuild hudsonBuild,
			final IOperationMonitor monitor) throws JenkinsException {

		return new JenkinsOperation<Reader>(client) {
			@Override
			public Reader execute() throws IOException, JenkinsException, JAXBException {
				HttpRequestBase request = createGetRequest(getBuildUrl(job, hudsonBuild) + "/consoleText"); //$NON-NLS-1$
				CommonHttpResponse response = execute(request, monitor);
				return process(response, monitor);
			}

			@Override
			protected Reader doProcess(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException {
				InputStream in = response.getResponseEntityAsStream();
				String charSet = response.getResponseCharSet();
				if (charSet == null) {
					charSet = "UTF-8"; //$NON-NLS-1$
				}
				return new InputStreamReader(in, charSet);
			}
		}.run();
	}

	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	public List<HudsonModelJob> getJobs(final List<String> ids, final IOperationMonitor monitor)
			throws JenkinsException {
		if (ids != null && ids.isEmpty()) {
			return Collections.emptyList();
		}

		List<HudsonModelJob> jobs = new ArrayList<>();

		if (ids != null) {
			Map<String, List<String>> jobNamesByFolderUrl = jenkinsUrlUtil.groupJobNamesByFolderUrl(ids);

			for (String folderUrl : jobNamesByFolderUrl.keySet()) {
				List<String> jobNames = jobNamesByFolderUrl.get(folderUrl);
				for (int batchStartIndex = 0; batchStartIndex < jobNames
						.size(); batchStartIndex += JOB_RETRIEVE_BATCH_SIZE) {
					List<String> jobNamesBatch = jobNames.subList(batchStartIndex,
							Math.min(batchStartIndex + JOB_RETRIEVE_BATCH_SIZE, jobNames.size()));
					jobs.addAll(getJobsFromFolder(folderUrl, jobNamesBatch, monitor));
				}
			}
		} else {
			jobs = getJobsFromFolder(jenkinsUrlUtil.baseUrl(), ids, monitor);
			updateConfiguration(jobs);
		}

		return jobs;
	}

	private void updateConfiguration(List<HudsonModelJob> jobs) throws JenkinsException {

		Map<String, String> jobNameById = new HashMap<>();

		for (HudsonModelJob job : jobs) {
			String jobUrl = job.getUrl();
			String displayName = jenkinsUrlUtil.getDisplayName(jobUrl);
			if (jobUrl != null && jenkinsUrlUtil.isNestedJob(jobUrl)) {
				jobNameById.put(jobUrl, displayName);
			} else {
				jobNameById.put(job.getName(), displayName);
			}
		}
		JenkinsConfiguration configuration = new JenkinsConfiguration();
		configuration.jobNameById = jobNameById;
		setConfiguration(configuration);
	}

	private List<HudsonModelJob> getJobsFromFolder(final String folderUrl, final List<String> ids,
			final IOperationMonitor monitor) throws JenkinsException {

		return new JenkinsOperation<List<HudsonModelJob>>(client) {

			@Override
			protected List<HudsonModelJob> execute() throws IOException, JenkinsException, JAXBException {

				String url = JenkinsUrl.create(folderUrl)
						.depth(1)
						.include("/*/job") //$NON-NLS-1$
						.match("name", ids) //$NON-NLS-1$
						.exclude("/*/job/build") //$NON-NLS-1$
						.toUrl();

				HttpRequestBase request = createGetRequest(url);
				CommonHttpResponse response = execute(request, monitor);
				return processAndRelease(response, monitor);
			}

			@Override
			protected List<HudsonModelJob> doProcess(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException, JAXBException {

				InputStream in = response.getResponseEntityAsStream();

				HudsonModelHudson hudson = unmarshal(parse(in, response.getRequestPath()), HudsonModelHudson.class);

				List<HudsonModelJob> buildPlans = new ArrayList<>();
				List<Object> jobsNodes = hudson.getJob();
				Set<String> urls = new HashSet<>();
				for (Object jobNode : jobsNodes) {
					Node node = (Node) jobNode;
					HudsonModelJob job = unmarshal(node, HudsonModelJob.class);
					if (job.getColor() != null) { // job folders don't have a color
						String jobUrl = jenkinsUrlUtil.assembleJobUrl(job.getName(), folderUrl);
						if (!urls.contains(jobUrl)) {
							job.setUrl(jobUrl);
							buildPlans.add(job);
							urls.add(jobUrl);
						}
					} else if (ids == null) { // retrieve jobs from sub-folder only if we need to fetch all jobs
						buildPlans.addAll(getJobsFromFolder(job.getUrl(), ids, monitor));
					}
				}
				return buildPlans;
			}

		}.run();
	}

	String getJobUrl(HudsonModelJob job) throws JenkinsException {

		String url = job.getUrl();
		if (url != null) {
			return url;
		}

		return jenkinsUrlUtil.getJobUrlFromJobId(job.getName());
	}

	Element parse(InputStream in, String url) throws JenkinsException {
		try {
			return getDocumentBuilder().parse(in).getDocumentElement();
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			throw new JenkinsException(NLS.bind("Failed to parse response from {0}", url), e); //$NON-NLS-1$
		}
	}

	public Document getJobConfig(final HudsonModelJob job, final IOperationMonitor monitor) throws JenkinsException {
		return new JenkinsOperation<Document>(client) {
			@Override
			public Document execute() throws IOException, JenkinsException, JAXBException {
				HttpRequestBase request = createGetRequest(getJobUrl(job) + "/config.xml"); //$NON-NLS-1$
				CommonHttpResponse response = execute(request, monitor);
				return processAndRelease(response, monitor);
			}

			@Override
			protected Document doProcess(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException, JAXBException {
				InputStream in = response.getResponseEntityAsStream();
				try {
					DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					return builder.parse(in); // TODO Enhance progress monitoring
				} catch (ParserConfigurationException | SAXException e) {
					throw new JenkinsException(e);
				}
			}
		}.run();
	}

	public void runBuild(final HudsonModelJob job, final Map<String, String> parameters,
			final IOperationMonitor monitor) throws JenkinsException {
		new JenkinsOperation<>(client) {
			@Override
			public Object execute() throws IOException, JenkinsException, JAXBException {
				HttpPost request = createPostRequest(getJobUrl(job) + "/build"); //$NON-NLS-1$
				if (parameters != null) {
					JenkinsRunBuildForm form = new JenkinsRunBuildForm();
					for (Entry<String, String> entry : parameters.entrySet()) {
						form.add(entry.getKey(), entry.getValue());
					}
					request.setEntity(form.createEntity());
				}

				CommonHttpResponse response = execute(request, monitor);
				return processAndRelease(response, monitor);
			}

			@Override
			protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException {

				int statusCode = response.getStatusCode();
				if (statusCode != HttpStatus.SC_CREATED && statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
					throw new JenkinsException(NLS.bind("Unexpected response from Hudson server for ''{0}'': {1}", //$NON-NLS-1$
							response.getRequestPath(), HttpUtil.getStatusText(statusCode)));
				}
			}
		}.run();
	}

	public void abortBuild(final HudsonModelJob job, final HudsonModelBuild build, final IOperationMonitor monitor)
			throws JenkinsException {
		new JenkinsOperation<>(client) {
			@Override
			public Object execute() throws IOException, JenkinsException, JAXBException {
				HttpPost request = createPostRequest(getJobUrl(job) + build.getNumber() + "/stop"); //$NON-NLS-1$

				CommonHttpResponse response = execute(request, monitor);
				return processAndRelease(response, monitor);
			}

			@Override
			protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException {

				int statusCode = response.getStatusCode();
				if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
					throw new JenkinsException(NLS.bind("Unexpected response from Hudson server for ''{0}'': {1}", //$NON-NLS-1$
							response.getRequestPath(), HttpUtil.getStatusText(statusCode)));
				}
			}
		}.run();
	}

	public void setCache(AbstractConfigurationCache<JenkinsConfiguration> cache) {
		Assert.isNotNull(cache);
		this.cache = cache;
	}

	protected void setConfiguration(JenkinsConfiguration configuration) {
		getCache().setConfiguration(client.getLocation().getUrl(), configuration);
	}

	public static <T> T unmarshal(Node node, Class<T> clazz) throws JAXBException {
		JAXBContext ctx = JAXBContext.newInstance(clazz);

		Unmarshaller unmarshaller = ctx.createUnmarshaller();

		JAXBElement<T> hudsonElement = unmarshaller.unmarshal(node, clazz);
		return hudsonElement.getValue();
	}

	public JenkinsServerInfo validate(final IOperationMonitor monitor) throws JenkinsException {
		info = new JenkinsOperation<JenkinsServerInfo>(client) {
			@Override
			public JenkinsServerInfo execute() throws IOException, JenkinsException, JAXBException {
				// XXX should use createHeadRequest() which is broken on Jenkins 1.459, see bug 376468
				HttpRequestBase request = createGetRequest(baseUrl());
				CommonHttpResponse response = execute(request, monitor);
				return processAndRelease(response, monitor);
			}

			@Override
			protected JenkinsServerInfo doProcess(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, JenkinsException, JAXBException {
				Header header = response.getResponse().getFirstHeader("X-Jenkins"); //$NON-NLS-1$
				Type type;
				if (header == null) {
					type = Type.HUDSON;
					header = response.getResponse().getFirstHeader("X-Hudson"); //$NON-NLS-1$
					if (header == null) {
						throw new JenkinsException(NLS.bind("{0} does not appear to be a Hudson or Jenkins instance", //$NON-NLS-1$
								baseUrl()));
					}
				} else {
					type = Type.JENKINS;
				}
				JenkinsServerInfo info = new JenkinsServerInfo(type, header.getValue());
				return info;
			}
		}.run();
		return info;
	}

	public RepositoryLocation getLocation() {
		return client.getLocation();
	}

	public JenkinsServerInfo getInfo() {
		return info;
	}

	public JenkinsServerInfo getInfo(final IOperationMonitor monitor) throws JenkinsException {
		JenkinsServerInfo info = this.info;
		if (info != null) {
			return info;
		}
		return validate(monitor);
	}

	public void reset() {
		client.getHttpClient().getCookieStore().clear();
	}

}