/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.net;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.protocol.Protocol;

/**
 * Based on org.apache.commons.httpclient.contrib.ssl.HostConfigurationWithStickyProtocol.
 * 
 * @author John Kristian
 * @author Steffen Pingel
 */
public class CloneableHostConfiguration extends HostConfiguration {

	public CloneableHostConfiguration() {
	}

	public CloneableHostConfiguration(CloneableHostConfiguration hostConfiguration) {
		super(hostConfiguration);
	}

	@Override
	public Object clone() {
		return new CloneableHostConfiguration(this);
	}

	@Override
	public synchronized void setHost(String host, int port, String scheme) {
		setHost(new HttpHost(host, port, getProtocol(host, port, scheme)));
	}

	/**
	 * Keeps the previous {@link Protocol} if the <code>scheme</code> matches the previous protocol scheme.
	 */
	private Protocol getProtocol(String host, int port, String scheme) {
		final Protocol oldProtocol = getProtocol();
		if (oldProtocol != null) {
			final String oldScheme = oldProtocol.getScheme();
			if (oldScheme == scheme || (oldScheme != null && oldScheme.equalsIgnoreCase(scheme))) {
				return oldProtocol;
			}
		}
		return Protocol.getProtocol(scheme);
	}

}
