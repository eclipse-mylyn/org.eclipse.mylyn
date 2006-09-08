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

package org.eclipse.mylar.internal.bugzilla.core;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.eclipse.mylar.tasks.core.IQueryHitCollector;
import org.eclipse.mylar.tasks.core.TaskList;


/**
 * @author Rob Elves
 */
public class RepositoryQueryResultsFactory extends AbstractReportFactory {

	/** expects rdf returned from repository (ctype=rdf in url) 
	 * @throws GeneralSecurityException */
	public void performQuery(TaskList taskList, String repositoryUrl, IQueryHitCollector collector, String queryUrlString,
			Proxy proxySettings, int maxHits, String characterEncoding) throws IOException, BugzillaException, GeneralSecurityException {
		
		SaxBugzillaQueryContentHandler contentHandler = new SaxBugzillaQueryContentHandler(taskList, repositoryUrl,
				collector, maxHits);
		URL url = new URL(queryUrlString);
		collectResults(url, proxySettings, characterEncoding, contentHandler, false);
	}
}
