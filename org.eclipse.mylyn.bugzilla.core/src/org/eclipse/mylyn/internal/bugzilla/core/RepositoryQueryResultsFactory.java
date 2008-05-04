/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import org.eclipse.mylyn.internal.tasks.core.deprecated.LegacyTaskDataCollector;

/**
 * @author Rob Elves
 */
public class RepositoryQueryResultsFactory extends AbstractReportFactory {

	public RepositoryQueryResultsFactory(InputStream inStream, String encoding) {
		super(inStream, encoding);
	}

	/**
	 * expects rdf returned from repository (ctype=rdf in url)
	 * 
	 * @throws GeneralSecurityException
	 */
	public int performQuery(String repositoryUrl, LegacyTaskDataCollector collector, int maxHits) throws IOException {
		SaxBugzillaQueryContentHandler contentHandler = new SaxBugzillaQueryContentHandler(repositoryUrl, collector);
		collectResults(contentHandler, false);
		return contentHandler.getResultCount();
	}
}
