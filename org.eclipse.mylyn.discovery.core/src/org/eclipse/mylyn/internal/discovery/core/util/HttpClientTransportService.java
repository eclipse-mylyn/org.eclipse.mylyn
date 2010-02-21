/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.util;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.eclipse.osgi.util.NLS;

/**
 * A utility for accessing web resources.
 * 
 * @author David Green
 * @author Steffen Pingel
 */
public class HttpClientTransportService implements ITransportService {

	public HttpClientTransportService() {
	}

	/**
	 * Download an HTTP-based resource
	 * 
	 * @param target
	 *            the target file to which the content is saved
	 * @param location
	 *            the web location of the content
	 * @param monitor
	 *            the monitor
	 * @throws IOException
	 *             if a network or IO problem occurs
	 */
	public IStatus download(java.net.URI uri, OutputStream out, IProgressMonitor monitor) {
		WebLocation location = new WebLocation(uri.toString());
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(NLS.bind(Messages.WebUtil_task_retrievingUrl, location.getUrl()), IProgressMonitor.UNKNOWN);
		try {
			HttpClient client = new HttpClient();
			org.eclipse.mylyn.commons.net.WebUtil.configureHttpClient(client, ""); //$NON-NLS-1$

			GetMethod method = new GetMethod(location.getUrl());
			try {
				HostConfiguration hostConfiguration = org.eclipse.mylyn.commons.net.WebUtil.createHostConfiguration(
						client, location, monitor);
				int result = org.eclipse.mylyn.commons.net.WebUtil.execute(client, hostConfiguration, method, monitor);
				if (result == HttpStatus.SC_OK) {
					InputStream in = org.eclipse.mylyn.commons.net.WebUtil.getResponseBodyAsStream(method, monitor);
					try {
						in = new BufferedInputStream(in);
						try {
							int i;
							while ((i = in.read()) != -1) {
								out.write(i);
							}
							return Status.OK_STATUS;
						} finally {
							out.close();
						}
					} finally {
						in.close();
					}
				} else {
					throw new IOException(NLS.bind(Messages.WebUtil_cannotDownload, location.getUrl(), result));
				}
			} finally {
				method.releaseConnection();
			}
		} catch (IOException e) {
			return new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN,
					NLS.bind("Download of {0} failed", uri.toString())); //$NON-NLS-1$
		} finally {
			monitor.done();
		}
	}

	/**
	 * Read a web-based resource at the specified location using the given processor.
	 * 
	 * @param location
	 *            the web location of the content
	 * @param processor
	 *            the processor that will handle content
	 * @param monitor
	 *            the monitor
	 * @throws IOException
	 *             if a network or IO problem occurs
	 */
	public InputStream stream(java.net.URI uri, IProgressMonitor monitor) throws IOException, CoreException {
		WebLocation location = new WebLocation(uri.toString());
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(NLS.bind(Messages.WebUtil_task_retrievingUrl, location.getUrl()), IProgressMonitor.UNKNOWN);
		try {
			HttpClient client = new HttpClient();
			org.eclipse.mylyn.commons.net.WebUtil.configureHttpClient(client, ""); //$NON-NLS-1$

			boolean success = false;
			GetMethod method = new GetMethod(location.getUrl());
			try {
				HostConfiguration hostConfiguration = org.eclipse.mylyn.commons.net.WebUtil.createHostConfiguration(
						client, location, monitor);
				int result = org.eclipse.mylyn.commons.net.WebUtil.execute(client, hostConfiguration, method, monitor);
				if (result == HttpStatus.SC_OK) {
					InputStream in = org.eclipse.mylyn.commons.net.WebUtil.getResponseBodyAsStream(method, monitor);
					success = true;
					return in;
				} else {
					throw new IOException(NLS.bind(Messages.WebUtil_cannotDownload, location.getUrl(), result));
				}
			} finally {
				if (!success) {
					method.releaseConnection();
				}
			}
		} finally {
			monitor.done();
		}
	}

	/**
	 * Verify availability of resources at the given web locations. Normally this would be done using an HTTP HEAD.
	 * 
	 * @param locations
	 *            the locations of the resource to verify
	 * @param one
	 *            indicate if only one of the resources must exist
	 * @param monitor
	 *            the monitor
	 * @return true if the resource exists
	 */
	public long getLastModified(java.net.URI uri, IProgressMonitor monitor) throws CoreException, IOException {
		WebLocation location = new WebLocation(uri.toString());
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(NLS.bind(Messages.WebUtil_task_retrievingUrl, location.getUrl()), IProgressMonitor.UNKNOWN);
		try {
			HttpClient client = new HttpClient();
			org.eclipse.mylyn.commons.net.WebUtil.configureHttpClient(client, ""); //$NON-NLS-1$

			HeadMethod method = new HeadMethod(location.getUrl());
			try {
				HostConfiguration hostConfiguration = org.eclipse.mylyn.commons.net.WebUtil.createHostConfiguration(
						client, location, monitor);
				int result = org.eclipse.mylyn.commons.net.WebUtil.execute(client, hostConfiguration, method, monitor);
				if (result == HttpStatus.SC_OK) {
					Header lastModified = method.getResponseHeader("Last-Modified"); //$NON-NLS-1$
					if (lastModified != null) {
						try {
							return DateUtil.parseDate(lastModified.getValue()).getTime();
						} catch (DateParseException e) {
							// fall through
						}
					}
					return 0;
				} else if (result == HttpStatus.SC_NOT_FOUND) {
					throw new FileNotFoundException(
							NLS.bind(Messages.WebUtil_cannotDownload, location.getUrl(), result));
				} else {
					throw new IOException(NLS.bind(Messages.WebUtil_cannotDownload, location.getUrl(), result));
				}
			} finally {
				method.releaseConnection();
			}
		} finally {
			monitor.done();
		}
	}

}
