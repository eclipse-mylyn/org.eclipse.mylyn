/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steffen Pingel
 * @author Robert Elves (adaption for Bugzilla)
 */
public class BugzillaClientFactory {

	public static BugzillaClient createClient(String hostUrl, String username, String password, String htAuthUser,
			String htAuthPass, Proxy proxy, String encoding) throws MalformedURLException {
		BugzillaLanguageSettings languageSettings = BugzillaCorePlugin.getDefault().getLanguageSetting(
				IBugzillaConstants.DEFAULT_LANG);
		return createClient(hostUrl, username, password, htAuthUser, htAuthPass, proxy, encoding,
				new HashMap<String, String>(), languageSettings);
	}

	public static BugzillaClient createClient(String hostUrl, String username, String password, String htAuthUser,
			String htAuthPass, Proxy proxy, String encoding, Map<String, String> configParameters,
			BugzillaLanguageSettings bugzillaLanguageSettings) throws MalformedURLException {
		URL url = new URL(hostUrl);

		BugzillaClient client = new BugzillaClient(url, username, password, htAuthUser, htAuthPass, encoding,
				configParameters, bugzillaLanguageSettings);
		client.setProxy(proxy);
		return client;
	}
}
