/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.net;

import javax.net.ssl.X509TrustManager;

/**
 * TrustAll class implements X509TrustManager to access all https servers with signed and unsigned certificates.
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @since 2.0
 */
public class TrustAllTrustManager implements X509TrustManager {

	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
		// don't need to do any checks
	}

	public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
		// don't need to do any checks
	}
}
