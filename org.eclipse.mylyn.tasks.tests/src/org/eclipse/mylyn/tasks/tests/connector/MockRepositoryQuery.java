/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import org.eclipse.mylyn.internal.tasks.core.AbstractRepositoryQuery;

/**
 * @author Mik Kersten
 */
public class MockRepositoryQuery extends AbstractRepositoryQuery {

	public String MOCK_QUERY_URL = MockRepositoryConnector.REPOSITORY_URL + ".query";

	public MockRepositoryQuery(String description) {
		super(description);
		super.setUrl(MOCK_QUERY_URL);
		super.setRepositoryUrl(MockRepositoryConnector.REPOSITORY_URL);
	}

	public MockRepositoryQuery(String description, String url) {
		super(description);
		super.setUrl(url);
	}

	@Override
	public String getConnectorKind() {
		return "mock";
	}

}
