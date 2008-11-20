/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

@HeadRequired
public abstract class AbstractTestInWorkspace extends TestCase {

	private static boolean init = false;

	private final List<IProject> temporaryProjects = new ArrayList<IProject>();

	public AbstractTestInWorkspace() {
		if (!init) {
			try {
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE,
						new NullProgressMonitor());
			} catch (CoreException e) {
				throw new IllegalStateException(e);
			}
			init = true;
		}
	}

	/**
	 * Overriding classes should call super.setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Overriding classes should call super.tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (!temporaryProjects.isEmpty()) {
			NullProgressMonitor monitor = new NullProgressMonitor();
			monitor.beginTask("Removing " + temporaryProjects.size() + " temporary projects",
					100 * temporaryProjects.size());
			try {
				for (IProject project : temporaryProjects) {
					project.delete(true, true, new SubProgressMonitor(monitor, 100));
				}
			} finally {
				monitor.done();
				temporaryProjects.clear();
			}
		}
	}

	public IProject createSimpleProject() throws CoreException {
		long seed = System.currentTimeMillis();

		String projectName = "test" + seed;

		String tmpDir = System.getProperty("java.io.tmpdir");

		URI location = (new File(tmpDir, projectName)).toURI();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocationURI(location);

		IProject project = workspace.getRoot().getProject(projectName);

		project.create(description, new NullProgressMonitor());
		if (!project.isOpen()) {
			project.open(new NullProgressMonitor());
		}

		temporaryProjects.add(project);

		return project;
	}

}
