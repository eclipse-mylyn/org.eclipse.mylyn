/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;

/**
 * @author Steffen Pingel
 */
public class TracClientFactory {

	public static ITracClient createClient(String location, Version version, String username, String password,
			Proxy proxy) throws MalformedURLException {
		URL url = new URL(location);

		if (version == Version.TRAC_0_9) {
			return new TracWebClient(url, version, username, password, proxy);
		} else if (version == Version.XML_RPC) {
			return new TracXmlRpcClient(url, version, username, password, proxy);
		}

		throw new RuntimeException("Invalid repository version: " + version);
	}

	/**
	 * Tries all supported access types for <code>location</code> and returns the corresponding version if successful;
	 * throws an exception otherwise.
	 * 
	 * <p>
	 * Order of the tried access types: XML-RPC, Trac 0.9
	 */
	public static Version probeClient(String location, String username, String password, Proxy proxy)
			throws MalformedURLException, TracException {
		URL url = new URL(location);
		try {
			ITracClient repository = new TracXmlRpcClient(url, Version.XML_RPC, username, password, proxy);
			repository.validate();
			return Version.XML_RPC;
		} catch (TracException e) {
			try {
				ITracClient repository = new TracWebClient(url, Version.TRAC_0_9, username, password, proxy);
				repository.validate();
				return Version.TRAC_0_9;
			} catch (TracLoginException e2) {
				throw e;
			} catch (TracException e2) {
			}
		}

		throw new TracException();
	}

}
