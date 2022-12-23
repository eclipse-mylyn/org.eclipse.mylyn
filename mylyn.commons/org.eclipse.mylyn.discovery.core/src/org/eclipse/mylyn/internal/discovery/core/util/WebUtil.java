/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;

/**
 * A utility for accessing web resources
 *
 * @author David Green
 */
public class WebUtil {
	/**
	 * implementors are capable of processing character content
	 *
	 * @see WebUtil#readResource(AbstractWebLocation, TextContentProcessor, IProgressMonitor)
	 */
	public interface TextContentProcessor {
		public void process(Reader reader) throws IOException;
	}

	private static ITransportService transport;

	/**
	 * Download an HTTP-based resource
	 *
	 * @param target
	 *            the target file to which the content is saved
	 * @param location
	 *            the web location of the content
	 * @param monitor
	 *            the monitor
	 * @return
	 * @throws IOException
	 *             if a network or IO problem occurs
	 */
	public static IStatus download(URI uri, File target, IProgressMonitor monitor) throws IOException {
		IStatus result;
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
			result = download(uri, out, monitor);
		}
		if (!result.isOK()) {
			target.delete();
			if (result.getException() instanceof IOException) {
				throw (IOException) result.getException();
			}
			throw new IOWithCauseException(result.getException());
		}
		return result;
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
	 * @throws CoreException
	 */
	public static void readResource(URI uri, TextContentProcessor processor, IProgressMonitor monitor)
			throws IOException, CoreException {
		InputStream in = stream(uri, monitor);
		try {
			// FIXME how can the charset be determined?
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8")); //$NON-NLS-1$
			processor.process(reader);
		} finally {
			in.close();
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
	 * @throws CoreException
	 */
	public static boolean verifyAvailability(List<? extends URI> locations, boolean one, IProgressMonitor monitor) {
		if (locations.isEmpty() || locations.size() > 5) {
			throw new IllegalArgumentException();
		}
		int countFound = 0;
		MultiStatus status = new MultiStatus(DiscoveryCore.ID_PLUGIN, 0, "Verifying resource availability failed", //$NON-NLS-1$
				new Exception());
		try {
			for (URI location : locations) {
				try {
					getLastModified(location, monitor);
					if (one) {
						return true;
					}
					++countFound;
				} catch (IOException | CoreException e) {
					status.add(getStatus(e));
					if (!one) {
						return false;
					}
					continue;
				}
			}
		} finally {
			if (!status.isOK()) {
				StatusHandler.log(status);
			}

		}
		return countFound == locations.size();
	}

	private static IStatus getStatus(Exception e) {
		if (e instanceof CoreException) {
			return ((CoreException) e).getStatus();
		}
		return new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN, e.getMessage(), e);
	}

	public static synchronized ITransportService getTransport() {
		if (transport == null) {
			if (Platform.isRunning()) {
				try {
					transport = new P2TransportService();
				} catch (ClassNotFoundException e) {
					// fall back to HttpClientTransport
				}
			}
			if (transport == null) {
				transport = new HttpClientTransportService();
			}
		}
		return transport;
	}

	public static IStatus download(URI uri, OutputStream out, IProgressMonitor monitor) {
		return getTransport().download(uri, out, monitor);
	}

	public static InputStream stream(URI uri, IProgressMonitor monitor) throws IOException, CoreException {
		return getTransport().stream(uri, monitor);
	}

	private static long getLastModified(URI location, IProgressMonitor monitor) throws CoreException, IOException {
		return getTransport().getLastModified(location, monitor);
	}

	public static String getFileNameFor(String bundleUrl) throws IOException {
		if (bundleUrl.charAt(bundleUrl.length() - 1) == '/') {
			bundleUrl = bundleUrl.substring(0, bundleUrl.length() - 1);
		}
		if (bundleUrl.lastIndexOf('/') != -1) {
			bundleUrl = bundleUrl.substring(bundleUrl.lastIndexOf('/') + 1);
		}
		return bundleUrl.replaceAll("[^a-zA-Z0-9_\\.]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
