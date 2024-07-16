/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     IBM Corporation - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.context.sdk.java;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.pde.internal.core.natures.PluginProject;

@SuppressWarnings("nls")
public class WorkspaceSetupHelper {

	private static final int MAX_RETRY = 10;

	private static final String HELPER_CONTEXT_ID = "helper-context";

	private static boolean isSetup = false;

	private static InteractionContext taskscape;

	private static IJavaProject project1;

	private static IJavaProject project2;

	private static TestJavaProject jdtCoreDomProject;

	private static IWorkspaceRoot workspaceRoot;

	private final static IProgressMonitor NULL_MONITOR = new NullProgressMonitor();

	public static IJavaProject createJavaPluginProjectFromZip(Object source, String projectName, String zipFileName)
			throws CoreException, ZipException, IOException {
		IProject project = createProject(projectName);
		ZipFile zip = new ZipFile(CommonTestUtil.getFile(source, "testdata/projects/" + zipFileName));

		CommonTestUtil.unzip(zip, project.getLocation().toFile());

		project.refreshLocal(IResource.DEPTH_INFINITE, null);

		IJavaProject javaProject = createPluginProject(project);
		return javaProject;
	}

	public static IJavaProject createJavaPluginProjectFromDirectory(File sourceDirectory, String projectName)
			throws CoreException, ZipException, IOException {
		IProject project = createProject(projectName);

		CommonTestUtil.copyFolderRecursively(sourceDirectory, project.getLocation().toFile());

		project.refreshLocal(IResource.DEPTH_INFINITE, null);

		IJavaProject javaProject = createPluginProject(project);
		return javaProject;
	}

	public static IJavaProject createPluginProject(IProject project) throws CoreException, JavaModelException {
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
		description.setNatureIds(new String[] { PluginProject.NATURE, JavaCore.NATURE_ID });
		project.setDescription(description, null);

		// create output folder
		IPath outputLocation = binFolder.getFullPath();
		javaProject.setOutputLocation(outputLocation, null);

		PluginProject pluginProject = new PluginProject();
		pluginProject.setProject(project);
		pluginProject.configure();

		return javaProject;
	}

	public static IProject createProject(String projectName) throws CoreException {
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

	public static void clearWorkspace() throws CoreException, IOException {
		isSetup = false;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		root.delete(true, true, new NullProgressMonitor());
		File workspace = root.getLocation().toFile();
		File mylynMetadata = new File(workspace, ".metadata/.mylyn");
		if (mylynMetadata.isDirectory()) {
			CommonTestUtil.deleteFolderRecursively(mylynMetadata);
			assertFalse("Failed to delete test workspace metadata at " + mylynMetadata.getAbsolutePath(),
					mylynMetadata.exists());
		}
		clearDoiModel();
	}

	public static IWorkspaceRoot setupWorkspace()
			throws CoreException, IOException, InvocationTargetException, InterruptedException {
		if (isSetup) {
			clearDoiModel();
			return workspaceRoot;
		}
		taskscape = new InteractionContext(HELPER_CONTEXT_ID, new InteractionContextScaling());

		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		project1 = createJavaPluginProjectFromZip(WorkspaceSetupHelper.class, "project1", "project1.zip");
		project2 = createJavaPluginProjectFromZip(WorkspaceSetupHelper.class, "project2", "project2.zip");

		jdtCoreDomProject = new TestJavaProject("workspace-helper-project");
		IPackageFragment jdtCoreDomPkg = jdtCoreDomProject.createPackage("org.eclipse.jdt.core.dom");
		IType astNodeType = jdtCoreDomProject.createType(jdtCoreDomPkg, "ASTNode.java", "public class ASTNode { }");
		astNodeType.createMethod("public final void setSourceRange(int startPosition, int length) { }", null, false,
				null);
		isSetup = true;

		project1.open(new NullProgressMonitor());
		project2.open(new NullProgressMonitor());
		jdtCoreDomProject.getJavaProject().open(new NullProgressMonitor());

		return workspaceRoot;
	}

	public static void delete(final IResource resource) throws CoreException {
		IWorkspaceRunnable runnable = monitor -> {
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

	public static void clearDoiModel() throws CoreException {
		ContextCore.getContextManager().deleteContext(HELPER_CONTEXT_ID);
		taskscape = new InteractionContext(HELPER_CONTEXT_ID, new InteractionContextScaling());
	}

	public static InteractionContext getContext()
			throws CoreException, IOException, InvocationTargetException, InterruptedException {
		if (!isSetup) {
			setupWorkspace();
		}
		return taskscape;
	}

	public static IJavaProject getJdtCoreDomProject()
			throws CoreException, IOException, InvocationTargetException, InterruptedException {
		if (!isSetup) {
			setupWorkspace();
		}
		return jdtCoreDomProject.getJavaProject();
	}

	public static IJavaProject getProject1()
			throws CoreException, IOException, InvocationTargetException, InterruptedException {
		if (!isSetup) {
			setupWorkspace();
		}
		return project1;
	}

	public static IJavaProject getProject2()
			throws CoreException, IOException, InvocationTargetException, InterruptedException {
		if (!isSetup) {
			setupWorkspace();
		}
		return project2;
	}

	public static IWorkspaceRoot getWorkspaceRoot()
			throws CoreException, IOException, InvocationTargetException, InterruptedException {
		if (!isSetup) {
			setupWorkspace();
		}
		return workspaceRoot;
	}

	public static IFile getFile(IJavaProject jp, String name) throws JavaModelException {
		if (jp == null || name == null) {
			return null;
		}
		Object[] files = jp.getNonJavaResources();
		for (Object o : files) {
			if (o instanceof IFile && ((IFile) o).getName().equals(name)) {
				return (IFile) o;
			}
		}
		return null;
	}

	public static IType getType(IJavaProject jp, String fullyQualifiedName) throws JavaModelException {
		if (jp == null || fullyQualifiedName == null) {
			return null;
		}
		IType t = jp.findType(fullyQualifiedName);
		return t;
	}

	public static IMethod getMethod(IType t, String methodName, String[] params) {
		if (t == null || methodName == null || params == null) {
			return null;
		}
		return t.getMethod(methodName, params);
	}
}
