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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.Project.NameKey;

/**
 * @author Vadim Dmitriev
 */
@SuppressWarnings("nls")
public class ProjectByNameComparatorTest {

	@Test
	public void testCompare() {
		String[] initialOrder = { "a", null, "bf", "Ba", "de", "f", "1" };
		String[] expectedOrder = { "1", "a", "Ba", "bf", "de", "f", null };

		Project[] projects = fromNames(initialOrder);
		Arrays.sort(projects, new ProjectByNameComparator());
		Assert.assertArrayEquals(expectedOrder, fromProjects(projects));
	}

	private static Project[] fromNames(String... names) {
		Project[] projects = new Project[names.length];
		for (int i = 0; i < names.length; i++) {
			projects[i] = new Project(new NameKey(names[i]));
		}
		return projects;
	}

	private static String[] fromProjects(Project... projects) {
		String[] names = new String[projects.length];
		for (int i = 0; i < projects.length; i++) {
			names[i] = projects[i].getName();
		}
		return names;
	}
}
