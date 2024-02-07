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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client.rest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ProjectInfo;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class ProjectInfoTest extends TestCase {
	@Test
	public void testFromEmptyJson() throws Exception {
		ProjectInfo projectInfo = parseFile("testdata/EmptyWithMagic.json");

		assertNotNull(projectInfo);
		assertNull(projectInfo.getKind());
		assertNull(projectInfo.getId());
		assertNull(projectInfo.getName());
		assertNull(projectInfo.getParent());
		assertNull(projectInfo.getDescription());
	}

	@Test
	public void testFromInvalid() throws Exception {
		ProjectInfo projectInfo = parseFile("testdata/InvalidWithMagic.json");

		assertNotNull(projectInfo);
		assertNull(projectInfo.getKind());
		assertNull(projectInfo.getId());
		assertNull(projectInfo.getName());
		assertNull(projectInfo.getParent());
		assertNull(projectInfo.getDescription());
	}

	@Test
	public void testFromSingleProject() throws Exception {
		ProjectInfo projectInfo = parseFile("testdata/ProjectInfo_project.json");

		assertNotNull(projectInfo);
		assertEquals("gerritcodereview#project", projectInfo.getKind());
		assertEquals("plugins%2Freplication", projectInfo.getId());
		assertEquals("plugins/replication", projectInfo.getName());
		assertEquals("Public-Plugins", projectInfo.getParent());
		assertEquals("Copies to other servers using the Git protocol", projectInfo.getDescription());
	}

	@Test
	public void testFromProjects() throws Exception {
		File file = CommonTestUtil.getFile(this, "testdata/ProjectInfo_projects.json");
		TypeToken<Map<String, ProjectInfo>> resultType = new TypeToken<>() {
		};
		String content = CommonTestUtil.read(file);
		Map<String, ProjectInfo> projects = new JSonSupport().parseResponse(content, resultType.getType());

		assertNotNull(projects);
		assertEquals(4, projects.size());
		assertProjectInfo(projects.get("external/bison"), "external%2Fbison", "GNU parser generator");
		assertProjectInfo(projects.get("external/gcc"), "external%2Fgcc", null /* no description */);
		assertProjectInfo(projects.get("external/openssl"), "external%2Fopenssl", "encryption\ncrypto routines");
		assertProjectInfo(projects.get("test"), "test", "\u003chtml\u003e is escaped");
	}

	private ProjectInfo parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, ProjectInfo.class);
	}

	private static void assertProjectInfo(ProjectInfo project, String id, String desc) {
		assertNotNull(project);
		assertEquals("gerritcodereview#project", project.getKind());
		assertEquals(id, project.getId());
		assertNull(project.getName());
		assertNull(project.getParent());
		assertEquals(desc, project.getDescription());
	}
}