/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.context.tests.support;

import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.tests.support.CommonTestUtil;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.natures.PluginProject;

public class ContextTestUtil {

	private static boolean contextUiLazyStarted;

	private static final int MAX_RETRY = 10;

	private final static IProgressMonitor NULL_MONITOR = new NullProgressMonitor();

	public static IJavaProject createJavaPluginProjectFromZip(String projectName, String zipFileName)
			throws CoreException, ZipException, IOException {
		IProject project = ContextTestUtil.createProject(projectName);
		ZipFile zip = new ZipFile(CommonTestUtil.getFile(ContextTestUtil.class, "testdata/projects/" + zipFileName));

		CommonTestUtil.unzip(zip, project.getLocation().toFile());

		project.refreshLocal(IResource.DEPTH_INFINITE, null);

		IJavaProject javaProject = ContextTestUtil.createPluginProject(project);
		return javaProject;
	}

	private static IJavaProject createPluginProject(IProject project) throws CoreException, JavaModelException {

		if (project == null) {
			return null;
		}

		IJavaProject javaProject = JavaCore.create(project);

		// create bin folder
		IFolder binFolder = project.getFolder("bin");
		if (!binFolder.exists()) {
			binFolder.create(false, true, null);
		}

		// set java nature
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { PDE.PLUGIN_NATURE, JavaCore.NATURE_ID });
		project.setDescription(description, null);

		// create output folder
		IPath outputLocation = binFolder.getFullPath();
		javaProject.setOutputLocation(outputLocation, null);

		PluginProject pluginProject = new PluginProject();
		pluginProject.setProject(project);
		pluginProject.configure();

		return javaProject;
	}

	private static IProject createProject(String projectName) throws CoreException {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (!project.exists()) {
			project.create(NULL_MONITOR);
		} else {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}

		if (!project.isOpen()) {
			project.open(NULL_MONITOR);
		}

		return project;
	}

	public static void delete(final IResource resource) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (int i = 0; i < MAX_RETRY; i++) {
					try {
						resource.delete(true, null);
						i = MAX_RETRY;
					} catch (CoreException e) {
						if (i == MAX_RETRY - 1) {
							StatusHandler.log(e.getStatus());
							throw e;
						}
						System.gc(); // help windows to really close file
						// locks
						try {
							Thread.sleep(1000); // sleep a second
						} catch (InterruptedException e1) {
						}
					}
				}
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, null);

	}

	public static void deleteProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (project.exists()) {
			delete(project);
		}
	}

	/**
	 * Test cases that rely on lazy startup of Context Ui (e.g. context bridges) need to invoke this method prior to
	 * running the test.
	 */
	public static void triggerContextUiLazyStart() {
		if (contextUiLazyStarted) {
			return;
		}

		contextUiLazyStarted = true;

		// make sure monitor UI is started and logs the start interaction event 
		MonitorUiPlugin.getDefault();

		ContextCore.getContextManager().activateContext("startup");
		ContextCore.getContextManager().deactivateContext("startup");
	}
}
