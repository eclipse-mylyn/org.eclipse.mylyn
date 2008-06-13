/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.deprecated;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
@Deprecated
public class BugzillaRepositoryQuery extends RepositoryQuery {

	private boolean customQuery = false;

	public BugzillaRepositoryQuery(String repositoryUrl, String queryUrl, String description) {
		super(description);
		setUrl(queryUrl);
		this.repositoryUrl = repositoryUrl;
	}

	@Override
	public String getConnectorKind() {
		return BugzillaCorePlugin.CONNECTOR_KIND;
	}

	public boolean isCustomQuery() {
		return customQuery;
	}

	public void setCustomQuery(boolean customQuery) {
		this.customQuery = customQuery;
	}
}
