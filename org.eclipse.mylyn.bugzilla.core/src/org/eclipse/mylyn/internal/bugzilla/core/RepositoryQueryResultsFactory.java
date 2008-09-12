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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

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
	public int performQuery(String repositoryUrl, TaskDataCollector collector, TaskAttributeMapper mapper, int maxHits)
			throws IOException {
		SaxBugzillaQueryContentHandler contentHandler = new SaxBugzillaQueryContentHandler(repositoryUrl, collector,
				mapper);
		collectResults(contentHandler, false);
		return contentHandler.getResultCount();
	}
}
