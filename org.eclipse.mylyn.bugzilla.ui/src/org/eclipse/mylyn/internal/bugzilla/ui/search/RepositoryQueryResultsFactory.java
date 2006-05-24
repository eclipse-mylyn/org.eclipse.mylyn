/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.search;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.internal.bugzilla.core.AbstractReportFactory;


/**
 * @author Rob Elves
 */
public class RepositoryQueryResultsFactory extends AbstractReportFactory {

	private static RepositoryQueryResultsFactory instance;

	private RepositoryQueryResultsFactory() {
		// no initial setup needed
	}

	public static RepositoryQueryResultsFactory getInstance() {
		if (instance == null) {
			instance = new RepositoryQueryResultsFactory();
		}
		return instance;
	}

	public void performQuery(String repositoryUrl, IBugzillaSearchResultCollector collector, String queryUrlString,
			Proxy proxySettings, int maxHits, String characterEncoding) throws LoginException, KeyManagementException,
			NoSuchAlgorithmException, IOException {

		
		SaxBugzillaQueryContentHandler contentHandler = new SaxBugzillaQueryContentHandler(repositoryUrl,
				collector, maxHits);
		
		URL url = new URL(queryUrlString);
		
		collectResults(url, proxySettings, characterEncoding, contentHandler);
	}
}
