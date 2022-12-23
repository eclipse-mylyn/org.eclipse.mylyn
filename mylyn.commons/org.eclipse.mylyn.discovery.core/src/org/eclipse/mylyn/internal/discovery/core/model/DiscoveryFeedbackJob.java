/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.core.model;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.discovery.core.util.WebUtil;
import org.osgi.framework.Bundle;

/**
 * @author Steffen Pingel
 */
public class DiscoveryFeedbackJob extends Job {

	private static String toUrl(Map<String, String> p) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : p.entrySet()) {
			if (entry.getValue() != null) {
				if (sb.length() > 0) {
					sb.append("&"); //$NON-NLS-1$
				}
				sb.append(entry.getKey());
				sb.append("="); //$NON-NLS-1$
				sb.append(URLEncoder.encode(entry.getValue(), "UTF-8")); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	private static String getBundleVersion(Bundle bundle) {
		if (bundle == null) {
			return null;
		}
		Object bundleVersion = bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
		if (bundleVersion == null) {
			return null;
		}
		return stripQualifier((String) bundleVersion);
	}

	private static Map<String, String> getProperties(ConnectorDescriptor descriptor) {
		Map<String, String> p = new LinkedHashMap<String, String>();
		p.put("id", descriptor.getId()); //$NON-NLS-1$
		p.put("discovery", getBundleVersion(Platform.getBundle("org.eclipse.mylyn"))); //$NON-NLS-1$ //$NON-NLS-2$
		p.put("product", System.getProperty("eclipse.product")); //$NON-NLS-1$//$NON-NLS-2$
		p.put("buildId", System.getProperty("eclipse.buildId")); //$NON-NLS-1$//$NON-NLS-2$
		p.put("os", System.getProperty("osgi.os")); //$NON-NLS-1$ //$NON-NLS-2$
		p.put("arch", System.getProperty("osgi.arch")); //$NON-NLS-1$ //$NON-NLS-2$
		p.put("ws", System.getProperty("osgi.ws")); //$NON-NLS-1$ //$NON-NLS-2$
		p.put("nl", System.getProperty("osgi.nl")); //$NON-NLS-1$ //$NON-NLS-2$
		return p;
	}

	private static String stripQualifier(String longVersion) {
		if (longVersion == null) {
			return null;
		}

		String parts[] = longVersion.split("\\."); //$NON-NLS-1$
		StringBuilder version = new StringBuilder();
		if (parts.length > 0) {
			version.append(parts[0]);
			if (parts.length > 1) {
				version.append("."); //$NON-NLS-1$
				version.append(parts[1]);
				if (parts.length > 2) {
					version.append("."); //$NON-NLS-1$
					version.append(parts[2]);
				}
			}
		}
		return version.toString();
	}

	private final List<ConnectorDescriptor> descriptors;

	public DiscoveryFeedbackJob(List<ConnectorDescriptor> descriptors) {
		super(Messages.DiscoveryFeedbackJob_Job_Label);
		Assert.isNotNull(descriptors);
		this.descriptors = descriptors;
		setSystem(true);
	}

	private List<URI> getStatUrls() {
		List<URI> uris = new ArrayList<URI>(descriptors.size());
		for (ConnectorDescriptor descriptor : descriptors) {
			try {
				StringBuilder sb = new StringBuilder(descriptor.getStatsUrl());
				try {
					Map<String, String> p = getProperties(descriptor);
					String parameters = toUrl(p);
					sb.append("?"); //$NON-NLS-1$
					sb.append(parameters);
				} catch (UnsupportedEncodingException e) {
					// ignore, ping the plain url instead
				}
				URI uri = new URI(sb.toString());
				uris.add(uri);
			} catch (URISyntaxException e) {
				// ignore
			}
		}
		return uris;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			WebUtil.verifyAvailability(getStatUrls(), false, monitor);
		} catch (Exception e) {
			// ignore
		}
		return Status.OK_STATUS;
	}

}
