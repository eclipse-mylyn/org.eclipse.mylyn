/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.internal.wikitext.ui.ScreenshotOnTimeoutRule;
import org.eclipse.mylyn.wikitext.toolkit.StackDumpOnTimeoutRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

@HeadRequired
public abstract class AbstractTestInWorkspace {

	@Rule
	public final StackDumpOnTimeoutRule stackDumpOnTimeoutRule = new StackDumpOnTimeoutRule();

	@Rule
	public final ScreenshotOnTimeoutRule screenshotRule = new ScreenshotOnTimeoutRule();

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private final List<IProject> temporaryProjects = new ArrayList<>();

	@Before
	public void before() {
		refreshWorkspace();
	}

	/**
	 * Overriding classes should call super.tearDown()
	 */
	@After
	public void tearDown() throws Exception {
		if (!temporaryProjects.isEmpty()) {
			NullProgressMonitor monitor = new NullProgressMonitor();
			monitor.beginTask("Removing " + temporaryProjects.size() + " temporary projects",
					100 * temporaryProjects.size());
			try {
				for (IProject project : temporaryProjects) {
					project.delete(true, true, SubMonitor.convert(monitor, 100));
				}
			} finally {
				monitor.done();
				temporaryProjects.clear();
			}
		}
	}

	public IProject createSimpleProject() {
		long seed = System.currentTimeMillis();

		String projectName = "test" + Long.toHexString(seed) + Long.toHexString(new Random().nextLong());

		File folderParent = temporaryFolder.getRoot();

		URI location = (new File(folderParent, projectName)).toURI();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocationURI(location);

		IProject project = workspace.getRoot().getProject(projectName);

		try {
			project.create(description, new NullProgressMonitor());
			if (!project.isOpen()) {
				project.open(new NullProgressMonitor());
			}
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

		temporaryProjects.add(project);

		return project;
	}

	private void refreshWorkspace() {
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			throw new IllegalStateException(e);
		}
	}
}
