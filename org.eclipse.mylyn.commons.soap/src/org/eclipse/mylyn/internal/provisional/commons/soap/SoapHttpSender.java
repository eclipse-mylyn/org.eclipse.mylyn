/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.soap;

import java.net.URL;

import org.apache.axis.MessageContext;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.commons.soap.SoapRequest;

/**
 * A client for SOAP calls that uses {@link AbstractWebLocation} and is based on {@link CommonsHttpSender}. Specify the
 * client in your soap service configuration:
 * 
 * <pre>
 * &lt;deployment name=&quot;defaultClientConfig&quot;
 *             xmlns=&quot;http://xml.apache.org/axis/wsdd/&quot;
 *             xmlns:java=&quot;http://xml.apache.org/axis/wsdd/providers/java&quot;&gt;
 *   &lt;transport name=&quot;http&quot; pivot=&quot;java:org.eclipse.mylyn.internal.provisional.commons.soap.SoapHttpSender&quot; /&gt;
 *   &lt;transport name=&quot;https&quot; pivot=&quot;java:org.eclipse.mylyn.internal.provisional.commons.soap.SoapHttpSender&quot;/&gt;
 *   ...
 * &lt;/deployment&gt;
 * </pre>
 * 
 * @author Steffen Pingel
 */
public class SoapHttpSender extends CommonsHttpSender {

	private static final long serialVersionUID = -5876804777334482128L;

	/**
	 * The key for specifying the server location of type {@link AbstractWebLocation}.
	 */
	public static final String LOCATION = "org.eclipse.mylyn.commons.soap.location"; //$NON-NLS-1$

	/**
	 * The key for specifying a user agent of type {@link String}.
	 */
	public static final String USER_AGENT = "org.eclipse.mylyn.commons.soap.userAgent"; //$NON-NLS-1$

	@Override
	protected HostConfiguration getHostConfiguration(HttpClient client, MessageContext context, URL url) {
		AbstractWebLocation location = (AbstractWebLocation) context.getProperty(LOCATION);
		if (location == null) {
			throw new RuntimeException("Required property SoapHttpSender.LOCATION not set"); //$NON-NLS-1$
		}
		SoapRequest request = SoapRequest.getCurrentRequest();
		WebUtil.configureHttpClient(client, (String) context.getProperty(USER_AGENT));
		return WebUtil.createHostConfiguration(client, location, (request != null) ? request.getMonitor() : null);
	}

	@Override
	protected void addContextInfo(HttpMethodBase method, HttpClient httpClient, MessageContext msgContext, URL tmpURL)
			throws Exception {
		super.addContextInfo(method, httpClient, msgContext, tmpURL);

		SoapRequest request = SoapRequest.getCurrentRequest();
		if (request != null) {
			request.setMethod(method);
		}
	}

}
