/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.Project.NameKey;

public class GerritUtilTest {

	@Test
	public void toChangeId() {
		assertEquals("123", GerritUtil.toChangeId("123"));
		assertEquals("I95aa5d1d28009ecc6a59b1bf33a2866d186e5c62",
				GerritUtil.toChangeId("I95aa5d1d28009ecc6a59b1bf33a2866d186e5c62"));
		assertEquals("I95aa5d1d28009ecc6a59b1bf33a2866d186e5c62",
				GerritUtil.toChangeId("123~234~I95aa5d1d28009ecc6a59b1bf33a2866d186e5c62"));
		assertEquals("abc~I95aa5d1d28009ecc6a59b1bf33a2866d186e5c62",
				GerritUtil.toChangeId("abc~I95aa5d1d28009ecc6a59b1bf33a2866d186e5c62"));
	}

	@Test
	public void forProject() {
		Project project = new Project(new NameKey("some_name"));
		assertEquals("http://jdoe@gerrithost:8080/" + project.getName(),
				GerritUtil.forProject("http://jdoe@gerrithost:8080/${project}", project));
	}
}
