/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
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

package org.eclipse.mylyn.versions.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tests.support.MockRepositoryProvider;
import org.eclipse.mylyn.versions.tests.support.MockScmConnector;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class ScmCoreTest {

	@Test
	public void testMockConnectorPresent() {
		List<ScmConnector> connectors = ScmCore.getAllRegisteredConnectors();
		for (ScmConnector connector : connectors) {
			if (MockRepositoryProvider.ID.equals(connector.getProviderId())) {
				assertEquals(MockScmConnector.class, connector.getClass());
				return;
			}
		}
		Assert.fail("Expected MockScmConnector in " + connectors.toString());
	}

	@Test
	public void testGetResource() throws Exception {
		setupTestWorkspace();
		IResource expectedResource = ResourcesPlugin.getWorkspace()
				.getRoot()
				.findMember("/project1/src/org/eclipse/mylar/tests/project1/Project1Plugin.java");

		assertEquals(expectedResource,
				ScmCore.findResource("/project1/src/org/eclipse/mylar/tests/project1/Project1Plugin.java"));
		assertEquals(expectedResource,
				ScmCore.findResource("project1/src/org/eclipse/mylar/tests/project1/Project1Plugin.java"));
	}

	@Test
	public void testGetResourcePathChild() throws Exception {
		setupTestWorkspace();
		IResource expectedResource = ResourcesPlugin.getWorkspace()
				.getRoot()
				.findMember("/project1/src/org/eclipse/mylar/tests/project1/Project1Plugin.java");

		assertEquals(expectedResource, ScmCore
				.findResource("/root/level1/level2/project1/src/org/eclipse/mylar/tests/project1/Project1Plugin.java"));
		assertEquals(expectedResource, ScmCore
				.findResource("root/level1/level2/project1/src/org/eclipse/mylar/tests/project1/Project1Plugin.java"));

	}

	@Test
	public void testGetResourceFromProject() throws Exception {
		setupTestWorkspace();
		IResource expectedResource = ResourcesPlugin.getWorkspace()
				.getRoot()
				.findMember("/project2/src/org/eclipse/mylar/tests/project2/Project2Plugin.java");

		assertEquals(expectedResource,
				ScmCore.findResource("/src/org/eclipse/mylar/tests/project2/Project2Plugin.java"));
		assertEquals(expectedResource,
				ScmCore.findResource("src/org/eclipse/mylar/tests/project2/Project2Plugin.java"));
	}

	@Test
	public void testGetResourceCannotFind() throws Exception {
		setupTestWorkspace();
		assertNull(ScmCore.findResource("/project1/randompath"));
		assertNull(ScmCore.findResource("/2/src/org/eclipse/mylar/tests/project2/Project2Plugin.java"));
		assertNull(ScmCore.findResource("/org/eclipse/mylar/tests/project2/Project2Plugin.java"));
	}

	private void setupTestWorkspace() throws CoreException, ZipException, IOException {
		setupProjectFromZipFile(ScmCoreTest.class, "project1", "project1.zip");
		setupProjectFromZipFile(ScmCoreTest.class, "project2", "project2.zip");
	}

	private IProject setupProjectFromZipFile(Object source, String projectName, String zipFileName)
			throws ZipException, IOException, CoreException {
		IProject project = createTestProject(projectName);
		ZipFile zip = new ZipFile(CommonTestUtil.getFile(source, "testdata/projects/" + zipFileName));

		CommonTestUtil.unzip(zip, project.getLocation().toFile());
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		return project;
	}

	private IProject createTestProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (!project.exists()) {
			project.create(new NullProgressMonitor());
		} else {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}

		if (!project.isOpen()) {
			project.open(new NullProgressMonitor());
		}

		return project;
	}

}
