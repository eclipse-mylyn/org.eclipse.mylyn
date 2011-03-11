/*******************************************************************************
 * Copyright (c) 2010 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * Contributors:
 *   Alvaro Sanchez-Leon - Intial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core.spi;

import java.net.URI;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author lmcalvs
 */
public class ScmResourceUtils {
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Return the projects currently opened in the work space
	 * 
	 * @return
	 */
	public static IProject[] getProjects() {
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] projects = root.getRoot().getProjects();
		return projects;
	}

	/**
	 * Return the workspace project associated with the given name
	 * 
	 * @param name
	 * @return - IProject if found, null if not found
	 */
	public static IProject getProject(String name) {
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] projects = root.getRoot().getProjects();
		IProject project = null;
		if (projects != null) {
			for (IProject project2 : projects) {
				project = project2;
				if (project.getName().equals(name)) {
					return project;
				}
			}
		}
		return null;
	}

	/**
	 * Find the workspace file identified by the absolute URI which is associated to the given project
	 * 
	 * @param aFilePathURI
	 *            - URI in absolute format
	 * @param aProject
	 *            - workspace project where is expected
	 * @return - null if no file found
	 */
	public static IFile getWorkSpaceFile(URI aFilePathURI, IProject aProject) {
		IFile[] files = getWorkSpaceFiles(aFilePathURI);
		for (IFile iFile : files) {
			if (iFile.getProject().equals(aProject)) {
				// found
				return iFile;
			}
		}
		// not found
		return null;

	}

	/**
	 * Return all handles to Resource files for the given URI (in absolute form)
	 * 
	 * @param filePathURI
	 *            - Absolute URI to the file
	 * @return - File handles to resource files within the workspace
	 */
	public static IFile[] getWorkSpaceFiles(URI filePathURI) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		IFile[] files = null;
		if (filePathURI != null) {
			files = filterNonExistentFiles(root.findFilesForLocationURI(filePathURI));
		}

		return files;
	}

	/**
	 * @param files
	 * @return
	 */
	private static IFile[] filterNonExistentFiles(IFile[] files) {
		if (files == null) {
			return null;
		}
		int length = files.length;
		ArrayList<IFile> existentFiles = new ArrayList<IFile>(length);
		for (int i = 0; i < length; i++) {
			if (files[i].exists()) {
				existentFiles.add(files[i]);
			} else if (files[i].getType() == IResource.FILE) {
				existentFiles.add(files[i]);
			}
		}
		return existentFiles.toArray(new IFile[existentFiles.size()]);
	}
}
