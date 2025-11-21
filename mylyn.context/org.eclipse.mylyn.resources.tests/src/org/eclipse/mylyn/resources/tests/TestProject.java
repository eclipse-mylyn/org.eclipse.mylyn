/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @deprecated use {@link org.eclipse.mylyn.commons.sdk.util.TestProxy} instead
 * @author Mik Kersten
 */
@Deprecated
public class TestProject {

	@Deprecated
	public IProject project;

	@Deprecated
	public TestProject(final String name) throws CoreException, InvocationTargetException, InterruptedException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(name);
		project.create(null);
		project.open(null);
	}

	@Deprecated
	public IProject getProject() {
		return project;
	}

	@Deprecated
	public IFolder createFolder(String name) throws CoreException {
		IFolder folder = project.getFolder(name);
		folder.create(false, true, null);
		return folder;
	}
}
