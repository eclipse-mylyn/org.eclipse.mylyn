/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.connector;

import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;

/**
 * @author Mik Kersten
 */
public class MockRepositoryQuery extends RepositoryQuery {

	public String MOCK_QUERY_URL = MockRepositoryConnector.REPOSITORY_URL + ".query";

	public MockRepositoryQuery(String description) {
		super(MockRepositoryConnector.CONNECTOR_KIND, description);
		super.setUrl(MOCK_QUERY_URL);
		super.setRepositoryUrl(MockRepositoryConnector.REPOSITORY_URL);
	}

	public MockRepositoryQuery(String description, String url) {
		super(MockRepositoryConnector.CONNECTOR_KIND, description);
		super.setUrl(url);
	}

	@Override
	public String getConnectorKind() {
		return "mock";
	}

}
