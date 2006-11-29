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

package org.eclipse.mylar.internal.team;

import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.internal.team.template.CommitTemplateManager;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.team.MylarTeamPlugin;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
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

	private static final String PREFIX_HTTP = "http://";

    private static final String PREFIX_HTTPS = "https://";

//    private static boolean haveSubclipse;
    
	public static void registerAdapters() {
		IAdapterManager adapterManager = Platform.getAdapterManager();

		// Mylar
		adapterManager.registerAdapters(FACTORY, ContextChangeSet.class);

		// Team public
		adapterManager.registerAdapters(FACTORY, IFileRevision.class);
		adapterManager.registerAdapters(FACTORY, DiffNode.class);

		// Team internal
		adapterManager.registerAdapters(FACTORY, DiffChangeSet.class); // CVSCheckedInChangeSet ???
		adapterManager.registerAdapters(FACTORY, ChangeSetDiffNode.class);
		adapterManager.registerAdapters(FACTORY, SynchronizeModelElement.class);
		
		// Team CVS internal; is it used? Maybe CVS History view in Eclipse 3.1?
		adapterManager.registerAdapters(FACTORY, org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry.class);
		
		// Subclipse		
//		try {
//			SubclipseWrapper.init(adapterManager, FACTORY);
//			haveSubclipse = true;
//		} catch (Throwable ex) {
//			// ignore
//		}  
	}
	
	public static void unregisterAdapters() {
		Platform.getAdapterManager().unregisterAdapters(FACTORY); 
	}
    
	
	private LinkedTaskInfoAdapterFactory() {
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Object object, Class adapterType) {
		if(!ILinkedTaskInfo.class.equals(adapterType)) {
			return null;
		}
			
		if(object instanceof ChangeSetDiffNode) {
			return adaptChangeSetDiffNode(object);
		}
		
		if(object instanceof DiffNode) {
			return getAdapter(((DiffNode) object).getParent(), adapterType);
		}
		
//		if(haveSubclipse &&
//				"org.tigris.subversion.subclipse.core.history.LogEntry".equals(object.getClass().getName())) {
//			ILinkedTaskInfo info = SubclipseWrapper.adaptSubclipseLogEntry(object);
//			if(info!=null) {
//				return info;
//			}
//		}
		
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
        if (set instanceof ContextChangeSet) {
            return new LinkedTaskInfo(((ContextChangeSet) set).getTask());
        } 
//        else if(haveSubclipse && set.getClass().getName().startsWith("org.tigris")) {
//            ILinkedTaskInfo info = SubclipseWrapper.adaptSubclipseChangeset(diffNode, set);
//			if(info!=null) {
//				return info;
//			}
//        }

        return adaptFromComment(object);
	}

	private ILinkedTaskInfo adaptFromComment(Object object) {
		String comment = getCommentFromElement(object);
		
		CommitTemplateManager commitTemplateManager = MylarTeamPlugin.getDefault().getCommitTemplateManager();
		String taskId = commitTemplateManager.getTaskIdFromCommentOrLabel(comment);
		if (taskId == null) {
			taskId = getTaskIdFromLegacy07Label(comment);
		}

		IProject project = findCorrespondingProject(object);
		if (project != null) {
			TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(project, false);
			if (repository != null && taskId!=null) {
				return new LinkedTaskInfo(repository.getUrl(), taskId, null);
			}
		}
		
		String fullTaskUrl = getUrlFromComment(comment);
        if(fullTaskUrl!=null) {
        	return new LinkedTaskInfo(fullTaskUrl);
        }
        
        return null;
	}
	
    private String getCommentFromElement(Object element) {
        if (element instanceof DiffChangeSet) {
            return ((DiffChangeSet) element).getComment();
        } else if (element instanceof ChangeSetDiffNode) {
            return ((ChangeSetDiffNode) element).getName();
        } else if (element instanceof IFileRevision) {
        	return ((IFileRevision) element).getComment();
        } else if (element instanceof org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry) {
            return ((org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry) element).getComment();
        } else if (element instanceof org.tigris.subversion.subclipse.core.history.LogEntry) {
            return ((org.tigris.subversion.subclipse.core.history.LogEntry) element).getComment();
        }
        return null;
    }
	
	
	//

    public static String getUrlFromComment(String comment) {
        int httpIndex = comment.indexOf(PREFIX_HTTP);
        int httpsIndex = comment.indexOf(PREFIX_HTTPS);
        int idStart = -1;
        if (httpIndex != -1) {
            idStart = httpIndex;
        } else if (httpsIndex != -1) {
            idStart = httpsIndex;
        }
        if (idStart != -1) {
            int idEnd = comment.indexOf(' ', idStart);
            if (idEnd == -1) {
                return comment.substring(idStart);
            } else if (idEnd != -1 && idStart < idEnd) {
                return comment.substring(idStart, idEnd);
            }
        }
        return null;
    }

    public static String getTaskIdFromLegacy07Label(String comment) {
        String PREFIX_DELIM = ":";
        String PREFIX_START_1 = "Progress on:";
        String PREFIX_START_2 = "Completed:";
        String usedPrefix = PREFIX_START_1;
        int firstDelimIndex = comment.indexOf(PREFIX_START_1);
        if (firstDelimIndex == -1) {
            firstDelimIndex = comment.indexOf(PREFIX_START_2);
            usedPrefix = PREFIX_START_2;
        }
        if (firstDelimIndex != -1) {
            int idStart = firstDelimIndex + usedPrefix.length();
            int idEnd = comment.indexOf(PREFIX_DELIM, firstDelimIndex + usedPrefix.length());// comment.indexOf(PREFIX_DELIM);
            if (idEnd != -1 && idStart < idEnd) {
                String id = comment.substring(idStart, idEnd);
                if (id != null) {
                    return id.trim();
                }
            } else {
                return comment.substring(0, firstDelimIndex);
            }
        }
        return null;
    }
	
    private static IProject findCorrespondingProject(Object element) {
        if (element instanceof DiffChangeSet) {
            IResource[] resources = ((DiffChangeSet) element).getResources();
            if (resources.length > 0) {
                // TODO: only checks first resource
                return resources[0].getProject();
            }
        } else if (element instanceof SynchronizeModelElement) {
            SynchronizeModelElement modelElement = (SynchronizeModelElement)element;
            IResource resource = modelElement.getResource();
            if (resource != null) {
                return resource.getProject();
            } else {
                IDiffElement[] elements = modelElement.getChildren();
                if (elements.length > 0) {
                    // TODO: only checks first diff
                    if (elements[0] instanceof SynchronizeModelElement) {
                        return ((SynchronizeModelElement)elements[0]).getResource().getProject();
                    }
                }
            }
        } else if (element instanceof IAdaptable) {
        	IAdaptable adaptable = (IAdaptable) element;
			IResourceVariant resourceVariant = (IResourceVariant) adaptable.getAdapter(IResourceVariant.class);
            if (resourceVariant != null && resourceVariant instanceof RemoteResource) {
            	RemoteResource remoteResource = (RemoteResource) resourceVariant;
            	// TODO is there a better way then iterating trough all projects?
                String path = remoteResource.getRepositoryRelativePath();
                for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
                	if(project.isAccessible()) {
                		ICVSResource cvsResource = CVSWorkspaceRoot.getCVSFolderFor(project);
                		try {
							if(cvsResource!=null && path.startsWith(cvsResource.getRepositoryRelativePath())) {
								return project;
							}
						} catch (CVSException ex) {
							// ignore
						}
                	}
                }
                
                return null;
            }
        } else {
        	// TODO
        	
        }
        return null;
    }  
}

