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

import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class BugzillaRepositoryQuery extends AbstractRepositoryQuery {

	private boolean customQuery = false;

	public BugzillaRepositoryQuery(String repositoryUrl, String queryUrl, String description) {
		super(description);
		this.url = queryUrl;
		this.repositoryUrl = repositoryUrl;
//		try {
//			this.maxHits = Integer.parseInt(maxHits);
//		} catch (Exception e) {
//			this.maxHits = -1;
//		}
	}

	@Override
	public String getRepositoryKind() {
		return BugzillaCorePlugin.REPOSITORY_KIND;
	}

	public boolean isCustomQuery() {
		return customQuery;
	}

	public void setCustomQuery(boolean customQuery) {
		this.customQuery = customQuery;
	}
}
