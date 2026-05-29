/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

/**
 * Data model object for <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-projects.html#project-info">ProjectInfo</a>.
 */
public class ProjectInfo {
	// should be "gerritcodereview#project"
	private String kind;

	private String id;

	private String name;

	private String parent;

	private String description;

	public String getKind() {
		return kind;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getParent() {
		return parent;
	}

	public String getDescription() {
		return description;
	}
}
