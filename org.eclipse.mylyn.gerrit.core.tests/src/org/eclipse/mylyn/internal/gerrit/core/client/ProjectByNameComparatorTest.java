/*******************************************************************************
 * Copyright (c) 2014 The Eclipse Foundation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vadim Dmitriev - initial API and implementation
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
public class ProjectByNameComparatorTest {

	@Test
	public void testCompare() {
		String[] initialOrder = new String[] { "a", null, "bf", "Ba", "de", "f", "1" };
		String[] expectedOrder = new String[] { "1", "a", "Ba", "bf", "de", "f", null };

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
