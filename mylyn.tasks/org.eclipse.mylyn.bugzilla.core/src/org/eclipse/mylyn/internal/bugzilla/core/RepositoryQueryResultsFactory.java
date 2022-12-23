/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
	public int performQuery(String repositoryUrl, TaskDataCollector collector, TaskAttributeMapper mapper)
			throws IOException {
		SaxBugzillaQueryContentHandler contentHandler = new SaxBugzillaQueryContentHandler(repositoryUrl, collector,
				mapper);
		collectResults(contentHandler, false);
		return contentHandler.getResultCount();
	}
}
