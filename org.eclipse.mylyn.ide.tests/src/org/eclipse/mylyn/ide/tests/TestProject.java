/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jul 20, 2004
 */
package org.eclipse.mylar.ide.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * From Erich Gamma's "Contributing to Eclipse" book.
 */
public class TestProject {
	
	public IProject project;

	public TestProject(final String name) throws CoreException, InvocationTargetException, InterruptedException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(name);
		project.create(null);
		project.open(null);
	}

	public IProject getProject() {
		return project;
	}

	public IFolder createFolder(String name) throws CoreException {
		IFolder binFolder = project.getFolder(name);
		binFolder.create(false, true, null);
		return binFolder;
	}
}