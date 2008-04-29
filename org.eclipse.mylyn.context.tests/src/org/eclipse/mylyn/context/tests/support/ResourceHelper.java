/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.tests.ContextTestsPlugin;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.natures.PluginProject;

/**
 * @since 3.0
 */
public class ResourceHelper {

	private final static IProgressMonitor NULL_MONITOR = new NullProgressMonitor();

	private static final int MAX_RETRY = 10;

	public static void deleteProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (project.exists()) {
			delete(project);
		}
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

	public static IJavaProject createJavaPluginProjectFromZip(String projectName, String zipFileName)
			throws CoreException, ZipException, IOException {
		IProject project = ResourceHelper.createProject(projectName);
		ZipFile zip = new ZipFile(FileTool.getFileInPlugin(ContextTestsPlugin.getDefault(), new Path(
				"testdata/projects/" + zipFileName)));

		FileTool.unzip(zip, project.getLocation().toFile());

		project.refreshLocal(IResource.DEPTH_INFINITE, null);

		IJavaProject javaProject = ResourceHelper.createPluginProject(project);
		return javaProject;
	}
}
