/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core;

import org.eclipse.core.runtime.PlatformObject;

/**
 * Categories to group repositories of the same kind, e.g. Tasks, Builds or Reviews.
 * 
 * @author Robert Elves
 */
public class RepositoryCategory extends PlatformObject {

	private final String id;

	private final String label;

	private final int rank;

	public static final String ID_CATEGORY_BUGS = "org.eclipse.mylyn.category.bugs"; //$NON-NLS-1$

	public static final String ID_CATEGORY_BUILDS = "org.eclipse.mylyn.category.build"; //$NON-NLS-1$

	public static final String ID_CATEGORY_OTHER = "org.eclipse.mylyn.category.other"; //$NON-NLS-1$

	public static final String ID_CATEGORY_REVIEWS = "org.eclipse.mylyn.category.review"; //$NON-NLS-1$

	public static final String ID_CATEGORY_TASKS = "org.eclipse.mylyn.category.tasks"; //$NON-NLS-1$

	public static final String ID_CATEGORY_ALL = "org.eclipse.mylyn.category.all"; //$NON-NLS-1$

	public static final String ID_CATEGORY_ROOT = "org.eclipse.mylyn.category.root"; //$NON-NLS-1$

	/**
	 * @since 3.9
	 */
	public static final String ID_CATEGORY_REQUIREMENTS = "org.eclipse.mylyn.category.requirements"; //$NON-NLS-1$

	public RepositoryCategory(String id, String label, int rank) {
		this.id = id;
		this.label = label;
		this.rank = rank;
	}

	public String getId() {
		return id;
	}

	public int compareTo(Object arg0) {
		if (arg0 instanceof RepositoryCategory) {
			return getRank() - ((RepositoryCategory) arg0).getRank();
		}
		return 0;
	}

	public int getRank() {
		return rank;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return getLabel();
	}

}
