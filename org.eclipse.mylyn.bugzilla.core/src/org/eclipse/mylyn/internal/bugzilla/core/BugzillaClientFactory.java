/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.core;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Steffen Pingel
 * @author Robert Elves (adaption for Bugzilla)
 */
public class BugzillaClientFactory {

	public static BugzillaClient createClient(String hostUrl, String username, String password, String htAuthUser, String htAuthPass, String encoding)
			throws MalformedURLException {
		URL url = new URL(hostUrl);

		BugzillaClient client =  new BugzillaClient(url, username, password, htAuthUser, htAuthPass, encoding);// pass authenticator?
//		client.setProxy(WebClientUtil.getProxySettings());
		return client;
	}

//	/**
//	 * Tries all supported access types for <code>location</code> and returns
//	 * the corresponding version if successful; throws an exception otherwise.
//	 * 
//	 * <p>
//	 * Order of the tried access types: XML-RPC, Trac 0.9
//	 */
//	public static Version probeClient(String location, String username, String password) throws MalformedURLException,
//			TracException {
//		URL url = new URL(location);
//		try {
//			ITracClient repository = new TracXmlRpcClient(url, Version.XML_RPC, username, password);
//			repository.validate();
//			return Version.XML_RPC;
//		} catch (TracException e) {
//			try {
//				ITracClient repository = new Trac09Client(url, Version.TRAC_0_9, username, password);
//				repository.validate();
//				return Version.TRAC_0_9;
//			} catch (TracLoginException e2) {
//				throw e;
//			} catch (TracException e2) {
//			}
//		}
//
//		throw new TracException();
//	}

}
