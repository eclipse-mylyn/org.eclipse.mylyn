/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui;

import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.tasks.core.ILinkedTaskInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.resources.RemoteResource;
import org.eclipse.team.internal.core.subscribers.ChangeSet;
import org.eclipse.team.internal.core.subscribers.DiffChangeSet;
import org.eclipse.team.internal.ui.synchronize.ChangeSetDiffNode;
import org.eclipse.team.internal.ui.synchronize.SynchronizeModelElement;

/**
 * Adapter factory used to create adapters for <code>LinkedTaskInfo</code>
 * 
 * @author Eugene Kuleshov
 */
public class LinkedTaskInfoAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	private static final Class[] ADAPTER_TYPES = new Class[] { ILinkedTaskInfo.class };

	private static IAdapterFactory FACTORY = new LinkedTaskInfoAdapterFactory();


	public static void registerAdapters() {
		IAdapterManager adapterManager = Platform.getAdapterManager();

		// Mylar
		adapterManager.registerAdapters(FACTORY, ContextChangeSet.class);

		// Team public
		adapterManager.registerAdapters(FACTORY, IFileRevision.class);

		// Team internal
		adapterManager.registerAdapters(FACTORY, DiffChangeSet.class); // CVSCheckedInChangeSet
		adapterManager.registerAdapters(FACTORY, ChangeSetDiffNode.class);
		adapterManager.registerAdapters(FACTORY, SynchronizeModelElement.class);

		// Team CVS internal; is it used? Maybe CVS History view in Eclipse 3.1?
		adapterManager.registerAdapters(FACTORY, LogEntry.class);
	}

	public static void unregisterAdapters() {
		Platform.getAdapterManager().unregisterAdapters(FACTORY);
	}

	private LinkedTaskInfoAdapterFactory() {
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object object, Class adapterType) {
		if (!ILinkedTaskInfo.class.equals(adapterType)) {
			return null;
		}

		if (object instanceof ChangeSetDiffNode) {
			return adaptChangeSetDiffNode(object);
		}

		// TODO add other adapted types

		return adaptFromComment(object);
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	private ILinkedTaskInfo adaptChangeSetDiffNode(Object object) {
		ChangeSetDiffNode diffNode = (ChangeSetDiffNode) object;
		ChangeSet set = diffNode.getSet();

		Object adapter = null;
		if (set instanceof IAdaptable) {
			adapter = ((IAdaptable) set).getAdapter(ILinkedTaskInfo.class);
		}
		if (adapter == null) {
			adapter = Platform.getAdapterManager().getAdapter(set, ILinkedTaskInfo.class);
		}
		if (adapter != null) {
			return (ILinkedTaskInfo) adapter;
		}

		return adaptFromComment(object);
	}

	private ILinkedTaskInfo adaptFromComment(Object object) {
		String comment = getCommentForElement(object);
		if (comment == null) {
			return null;
		}
		
		IResource resource = getResourceForElement(object);
		if (resource != null) {
			TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(resource, true);
			if (repository != null) {
				return new LinkedTaskInfo(repository.getUrl(), null, null, comment);
			}
		}

		return new LinkedTaskInfo(null, null, null, comment);
	}

	private static String getCommentForElement(Object element) {
		if (element instanceof DiffChangeSet) {
			return ((DiffChangeSet) element).getComment();
		} else if (element instanceof ChangeSetDiffNode) {
			return ((ChangeSetDiffNode) element).getName();
		} else if (element instanceof IFileRevision) {
			return ((IFileRevision) element).getComment();
		} else if (element instanceof LogEntry) {
			return ((LogEntry) element).getComment();
		}
		return null;
	}

	private static IResource getResourceForElement(Object element) {
		if (element instanceof DiffChangeSet) {
			IResource[] resources = ((DiffChangeSet) element).getResources();
			if (resources.length > 0) {
				// TODO: only checks first resource
				return resources[0];
			}
		} 
		if (element instanceof SynchronizeModelElement) {
			SynchronizeModelElement modelElement = (SynchronizeModelElement) element;
			IResource resource = modelElement.getResource();
			if (resource != null) {
				return resource;
			} else {
				IDiffElement[] elements = modelElement.getChildren();
				if (elements.length > 0) {
					// TODO: only checks first diff
					if (elements[0] instanceof SynchronizeModelElement) {
						return ((SynchronizeModelElement) elements[0]).getResource();
					}
				}
			}
		} 
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
								if (cvsResource != null && repositoryRelativePath != null
										&& path.startsWith(repositoryRelativePath)) {
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

