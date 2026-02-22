/*******************************************************************************
 * Copyright (c) 2014 The Eclipse Foundation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Vadim Dmitriev - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.Comparator;

import com.google.gerrit.reviewdb.Project;

/**
 * Compares {@link com.google.gerrit.reviewdb.Project} by name, case insensitive, ascending, nulls last.
 *
 * @throws NullPointerException
 *             one or both parameters are null
 * @see com.google.gerrit.reviewdb.Project
 * @author Vadim Dmitriev
 */
public class ProjectByNameComparator implements Comparator<Project> {

	@Override
	public int compare(Project o1, Project o2) {
		if (o1 == null || o2 == null) {
			throw new NullPointerException("Project can not be null"); //$NON-NLS-1$
		}

		String o1Name = o1.getName();
		String o2Name = o2.getName();

		if (o1Name == null && o2Name == null) {
			return 0;
		} else if (o1Name == null) {
			return 1;
		} else if (o2Name == null) {
			return -1;
		} else {
			return o1Name.compareToIgnoreCase(o2Name);
		}
	}

}
