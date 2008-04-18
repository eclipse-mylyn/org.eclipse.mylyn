/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ccvs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.team.ui.LinkedTaskInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.team.ui.AbstractTaskReference;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.resources.RemoteResource;

/**
 * @author Eugene Kuleshov
 */
public class CvsLinkedTaskInfoAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	private static final Class[] ADAPTER_TYPES = new Class[] { AbstractTaskReference.class };

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object object, Class adapterType) {
		if (!AbstractTaskReference.class.equals(adapterType)) {
			return null;
		}

		return adaptFromComment(object);
	}

	private AbstractTaskReference adaptFromComment(Object object) {
		String comment = getCommentForElement(object);
		if (comment == null) {
			return null;
		}

		IResource resource = getResourceForElement(object);
		if (resource != null) {
			TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(resource, true);
			if (repository != null) {
				return new LinkedTaskInfo(repository.getRepositoryUrl(), null, null, comment);
			}
		}

		return new LinkedTaskInfo(null, null, null, comment);
	}

	private static String getCommentForElement(Object element) {
		if (element instanceof LogEntry) {
			return ((LogEntry) element).getComment();
		}
		return null;
	}

	private static IResource getResourceForElement(Object element) {
		if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			IResourceVariant resourceVariant = (IResourceVariant) adaptable.getAdapter(IResourceVariant.class);
			if (resourceVariant != null && resourceVariant instanceof RemoteResource) {
				RemoteResource remoteResource = (RemoteResource) resourceVariant;
				// TODO is there a better way then iterating trough all projects?
				String path = remoteResource.getRepositoryRelativePath();
				if (path != null) {
					for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
						if (project.isAccessible()) {
							ICVSResource cvsResource = CVSWorkspaceRoot.getCVSFolderFor(project);
							try {
								String repositoryRelativePath = cvsResource.getRepositoryRelativePath();
								if (repositoryRelativePath != null && path.startsWith(repositoryRelativePath)) {
									return project;
								}
							} catch (CVSException ex) {
								// ignore
							}
						}
					}
				}
			}
		}

		// TODO any other resource types?

		return null;
	}

}
