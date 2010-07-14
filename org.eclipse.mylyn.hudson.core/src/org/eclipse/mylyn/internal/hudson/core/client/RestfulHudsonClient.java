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
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.commons.http.CommonHttpClient;
import org.eclipse.mylyn.commons.http.CommonHttpMethod;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelHudson;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents the Hudson repository that is accessed through REST.
 * 
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class RestfulHudsonClient {

	private static final String URL_API = "/api/xml"; //$NON-NLS-1$

	private final CommonHttpClient client;

	public RestfulHudsonClient(AbstractWebLocation location) {
		client = new CommonHttpClient(location);
		client.getHttpClient().getParams().setAuthenticationPreemptive(true);
	}

	protected void checkResponse(int statusCode) throws HudsonException {
		if (statusCode != HttpStatus.SC_OK) {
			throw new HudsonException(NLS.bind("Validation failed: {0}", HttpStatus.getStatusText(statusCode)));
		}
	}

	public List<HudsonModelJob> getJobs(final IOperationMonitor monitor) throws HudsonException {
		return new HudsonOperation<List<HudsonModelJob>>(client) {
			@Override
			public List<HudsonModelJob> execute() throws IOException, HudsonException, JAXBException {
				CommonHttpMethod method = createGetMethod(client.getLocation().getUrl() + URL_API
						+ "?depth=1&xpath=/hudson/job&wrapper=hudson&exclude=/hudson/job/build"); //$NON-NLS-1$
				try {
					int statusCode = execute(method, monitor);
					checkResponse(statusCode);

					InputStream in = method.getResponseBodyAsStream(monitor);

					HudsonModelHudson hudson = unmarshal(parse(in), HudsonModelHudson.class);
					List<HudsonModelJob> buildPlans = new ArrayList<HudsonModelJob>();
					List<Object> jobs = hudson.getJob();
					for (Object jobObj : jobs) {
						HudsonModelJob job = unmarshal((Node) jobObj, HudsonModelJob.class);
						buildPlans.add(job);
					}
					return buildPlans;
				} finally {
					method.releaseConnection(monitor);
				}
			}
		}.run();
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

	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
		throw new HudsonException(NLS.bind("Unexpected return code {0}: {1}", response,
				HttpStatus.getStatusText(response)));
	}

	public void runBuild(final HudsonModelJob job, final IOperationMonitor monitor) throws HudsonException {
		int response = new HudsonOperation<Integer>(client) {
			@Override
			public Integer execute() throws IOException {
				CommonHttpMethod method = createGetMethod(job.getUrl() + "/build");
				try {
					return execute(method, monitor);
				} finally {
					method.releaseConnection(monitor);
				}
			}
		}.run();
		if (response == HttpStatus.SC_OK) {
			return;
		}
		throw new HudsonException(NLS.bind("Unexpected return code {0}: {1}", response,
				HttpStatus.getStatusText(response)));
	}

}
