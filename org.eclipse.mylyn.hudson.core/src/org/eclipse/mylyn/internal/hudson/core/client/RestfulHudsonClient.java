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
 *     Benjamin Muskalla - 323920: [build] config retrival fails for jobs with whitespaces
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.spi.AbstractConfigurationCache;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.http.CommonHttpClient;
import org.eclipse.mylyn.commons.http.CommonHttpMethod;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.commons.http.CommonPostMethod;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelHudson;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelRun;
import org.eclipse.osgi.util.NLS;
import org.json.JSONException;
import org.json.JSONWriter;
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
public class RestfulHudsonClient {

	public enum BuildId {
		LAST(-1, "lastBuild"), LAST_FAILED(-5, "lastFailedBuild"), LAST_STABLE(-2, "lastStableBuild"), LAST_SUCCESSFUL(
				-3, "lastSuccessfulBuild"), LAST_UNSTABLE(-4, "lastUnstableBuild");

		private HudsonModelBuild build;

		private final int id;

		private final String url;

		BuildId(int id, String url) {
			this.id = id;
			this.url = url;
			this.build = new HudsonModelBuild();
			this.build.setNumber(id);
		}

		public HudsonModelBuild getBuild() {
			return build;
		}

	};

	private static final String URL_API = "/api/xml"; //$NON-NLS-1$

	private AbstractConfigurationCache<HudsonConfiguration> cache;

	private final CommonHttpClient client;

	public RestfulHudsonClient(AbstractWebLocation location, HudsonConfigurationCache cache) {
		client = new CommonHttpClient(location);
		client.getHttpClient().getParams().setAuthenticationPreemptive(true);
		setCache(cache);
	}

	protected void checkResponse(CommonHttpMethod method) throws HudsonException {
		checkResponse(method, HttpStatus.SC_OK);
	}

	protected void checkResponse(CommonHttpMethod method, int expected) throws HudsonException {
		int statusCode = method.getStatusCode();
		if (statusCode != expected) {
			throw new HudsonException(NLS.bind("Unexpected response from Hudson server for ''{0}'': {1}", method
					.getPath(), HttpStatus.getStatusText(statusCode)));
		}
	}

	public HudsonModelBuild getBuild(final HudsonModelJob job, final HudsonModelRun build,
			final IOperationMonitor monitor) throws HudsonException {
		return new HudsonOperation<HudsonModelBuild>(client) {
			@Override
			public HudsonModelBuild execute() throws IOException, HudsonException, JAXBException {
				CommonHttpMethod method = createGetMethod(getBuildUrl(job, build) + URL_API);
				try {
					execute(method, monitor);
					if (method.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
						// indicates that job was never built or invalid build was requested
						return null;
					}
					checkResponse(method);
					InputStream in = method.getResponseBodyAsStream(monitor);
					return unmarshal(parse(in), HudsonModelBuild.class);
				} finally {
					method.releaseConnection(monitor);
				}
			}
		}.run();
	}

	protected String getBuildUrl(final HudsonModelJob job, final HudsonModelRun build) throws HudsonException {
		if (build.getNumber() == -1) {
			return getJobUrl(job) + "/lastBuild";
		} else {
			return getJobUrl(job) + "/" + build.getNumber();
		}
	}

	public AbstractConfigurationCache<HudsonConfiguration> getCache() {
		return cache;
	}

	public HudsonConfiguration getConfiguration() {
		return getCache().getConfiguration(client.getLocation().getUrl());
	}

	public Reader getConsole(final HudsonModelJob job, final HudsonModelBuild hudsonBuild,
			final IOperationMonitor monitor) throws HudsonException {
		return new HudsonOperation<Reader>(client) {
			@Override
			public Reader execute() throws IOException, HudsonException {
				CommonHttpMethod method = createGetMethod(getBuildUrl(job, hudsonBuild) + "/consoleText");
				execute(method, monitor);
				checkResponse(method);
				String charSet = method.getResponseCharSet();
				if (charSet == null) {
					charSet = "UTF-8";
				}
				return new InputStreamReader(method.getResponseBodyAsStream(monitor), charSet);
			}
		}.run();
	}

	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	public List<HudsonModelJob> getJobs(final List<String> ids, final IOperationMonitor monitor) throws HudsonException {
		return new HudsonOperation<List<HudsonModelJob>>(client) {
			@Override
			public List<HudsonModelJob> execute() throws IOException, HudsonException, JAXBException {
				String url = HudsonUrl.create(client.getLocation().getUrl() + URL_API).depth(1).include("/hudson/job")
						.match("name", ids).exclude("/hudson/job/build").toUrl();
				CommonHttpMethod method = createGetMethod(url);
				try {
					execute(method, monitor);
					checkResponse(method);
					InputStream in = method.getResponseBodyAsStream(monitor);

					Map<String, String> jobNameById = new HashMap<String, String>();

					HudsonModelHudson hudson = unmarshal(parse(in), HudsonModelHudson.class);

					List<HudsonModelJob> buildPlans = new ArrayList<HudsonModelJob>();
					List<Object> jobsNodes = hudson.getJob();
					for (Object jobNode : jobsNodes) {
						Node node = (Node) jobNode;
						HudsonModelJob job = unmarshal(node, HudsonModelJob.class);
						if (job.getDisplayName() != null && job.getDisplayName().length() > 0) {
							jobNameById.put(job.getName(), job.getDisplayName());
						} else {
							jobNameById.put(job.getName(), job.getName());
						}
						buildPlans.add(job);
					}

					HudsonConfiguration configuration = new HudsonConfiguration();
					configuration.jobNameById = jobNameById;
					setConfiguration(configuration);

					return buildPlans;
				} finally {
					method.releaseConnection(monitor);
				}
			}
		}.run();
	}

	private String getJobUrl(HudsonModelJob job) throws HudsonException {
		String encodedJobname = "";
		try {
			encodedJobname = new URI(null, job.getName(), null).toASCIIString();
		} catch (URISyntaxException e) {
			throw new HudsonException(e);
		}
		return client.getLocation().getUrl() + "/job/" + encodedJobname;
	}

	Element parse(InputStream in) throws HudsonException {
		try {
			return getDocumentBuilder().parse(in).getDocumentElement();
		} catch (SAXException e) {
			throw new HudsonException(e);
		} catch (Exception e) {
			throw new HudsonException(e);
		}
	}

	public Document getJobConfig(final HudsonModelJob job, final IOperationMonitor monitor) throws HudsonException {
		return new HudsonOperation<Document>(client) {
			@Override
			public Document execute() throws IOException, HudsonException, JAXBException {
				CommonHttpMethod method = createGetMethod(getJobUrl(job) + "/config.xml");
				try {
					execute(method, monitor);
					checkResponse(method);

					InputStream in = method.getResponseBodyAsStream(monitor);
					DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					return builder.parse(in); // TODO Enhance progress monitoring
				} catch (ParserConfigurationException e) {
					throw new HudsonException(e);
				} catch (SAXException e) {
					throw new HudsonException(e);
				} finally {
					method.releaseConnection(monitor);
				}
			}
		}.run();
	}

	public void runBuild(final HudsonModelJob job, final Map<String, String> parameters, final IOperationMonitor monitor)
			throws HudsonException {
		new HudsonOperation<Object>(client) {
			@Override
			public Object execute() throws IOException, HudsonException {
				CommonPostMethod method = (CommonPostMethod) createPostMethod(getJobUrl(job) + "/build");
				method.setFollowRedirects(false);
				method.setDoAuthentication(true);
				if (parameters != null) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					Writer writer = new OutputStreamWriter(out);
					try {
						JSONWriter json = new JSONWriter(writer);
						json.object().key("parameter").array();
						for (Entry<String, String> entry : parameters.entrySet()) {
							method.addParameter(new NameValuePair("name", entry.getKey()));
							json.object().key("name").value(entry.getKey());
							String value = entry.getValue();
							if (value != null) {
								method.addParameter(new NameValuePair("value", value));
								json.key("value");
								if (entry.getKey().equals("Boolean")) {
									json.value(Boolean.parseBoolean(entry.getValue()));
								} else {
									json.value(entry.getValue());
								}
							}
							json.endObject();
						}
						json.endArray().endObject();
						writer.flush();
						method.addParameter(new NameValuePair("json", out.toString()));
						method.addParameter(new NameValuePair("Submit", "Build"));
					} catch (JSONException e) {
						throw new IOException("Error constructing request: " + e);
					}
				}

				try {
					execute(method, monitor);
					checkResponse(method, 302);
					return null;
				} finally {
					method.releaseConnection(monitor);
				}
			}
		}.run();
	}

	public void setCache(AbstractConfigurationCache<HudsonConfiguration> cache) {
		Assert.isNotNull(cache);
		this.cache = cache;
	}

	protected void setConfiguration(HudsonConfiguration configuration) {
		getCache().setConfiguration(client.getLocation().getUrl(), configuration);
	}

	private <T> T unmarshal(Node node, Class<T> clazz) throws JAXBException {
		JAXBContext ctx = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = ctx.createUnmarshaller();

		JAXBElement<T> hudsonElement = unmarshaller.unmarshal(node, clazz);
		return hudsonElement.getValue();
	}

	public IStatus validate(final IOperationMonitor monitor) throws HudsonException {
		int response = new HudsonOperation<Integer>(client) {
			@Override
			public Integer execute() throws IOException {
				CommonHttpMethod method = createHeadMethod(client.getLocation().getUrl() + URL_API);
				try {
					return execute(method, monitor);
				} finally {
					method.releaseConnection(monitor);
				}
			}
		}.run();
		if (response == HttpStatus.SC_OK) {
			return Status.OK_STATUS;
		}
		throw new HudsonException(NLS.bind("Unexpected return code {0}: {1}", response, HttpStatus
				.getStatusText(response)));
	}

}
