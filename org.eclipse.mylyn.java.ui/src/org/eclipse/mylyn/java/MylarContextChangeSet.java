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

package org.eclipse.mylar.java;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.synchronize.SyncInfo;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
import org.eclipse.team.internal.core.subscribers.SubscriberChangeSetCollector;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class MylarContextChangeSet extends ActiveChangeSet {

//	private static final String PREFIX_URL = "Report ";
//	private static final String LABEL_URL = "URL: ";
	private static final String PREFIX_HTTP = "http://";
	private static final String PREFIX_HTTPS = "https://";
	private static final String LABEL_PREFIX = "Mylar Task";
	private static final String LABEL_BUG = "Bug ";
	
	private List<IResource> resources;
	private ITask task;
	private JavaStructureBridge javaStructureBridge = new JavaStructureBridge();
	public static final String SOURCE_ID = "org.eclipse.mylar.java.context.changeset.add";
	
	public MylarContextChangeSet(ITask task, SubscriberChangeSetCollector collector) {
		super(collector, LABEL_PREFIX);
		this.task = task;
		if (task.isLocal()) {
			super.setTitle(LABEL_PREFIX + ": " + this.task.getDescription(true));
		} else {
			super.setTitle(LABEL_PREFIX + ": " + LABEL_BUG + " " + this.task.getDescription(true));
		}
	}
	
	@Override
	public String getComment() { 
		String completedPrefix = MylarTasklistPlugin.getPrefs().getString(MylarTasklistPlugin.COMMIT_PREFIX_COMPLETED);
		String progressPrefix = MylarTasklistPlugin.getPrefs().getString(MylarTasklistPlugin.COMMIT_PREFIX_PROGRESS);
		String comment = "";
		comment = generateComment(task, completedPrefix, progressPrefix);
		return comment;
	}

	@Override
	public void remove(IResource resource) {
		super.remove(resource);
		resources.remove(resource);
	}

	@Override
	public void remove(IResource[] newResources) {
		super.remove(newResources);
		for (int i = 0; i < newResources.length; i++) resources.remove(newResources[i]);
	} 

	@Override
	public void add(SyncInfo info) {
		super.add(info);
		addResourceToContext(info.getLocal());
	}

	@Override
	public void add(SyncInfo[] infos) {
		super.add(infos); 
	} 

	@Override
	public void add(IResource[] newResources) throws TeamException {
		super.add(newResources);
		for (int i = 0; i < newResources.length; i++) resources.add(newResources[i]);
	}

	@Override
	public IResource[] getResources() {
		if (MylarIdePlugin.getDefault() != null) {
			resources = MylarIdePlugin.getDefault().getInterestingResources();
		}
		return resources.toArray(new IResource[resources.size()]);
	}
	
	private void addResourceToContext(final IResource resource) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (resource instanceof IFile) {
					IMylarStructureBridge bridge = null;
					Object adapter = resource.getAdapter(IJavaElement.class);
					String handle = null;
					if (adapter instanceof IJavaElement) {
						bridge = javaStructureBridge;
						handle = bridge.getHandleIdentifier((IJavaElement)adapter);
					} else {
						bridge = MylarPlugin.getDefault().getStructureBridge(resource);
						handle = bridge.getHandleIdentifier(resource);
					}
					if (handle != null) {
						InteractionEvent manipulationEvent = new InteractionEvent(
				                InteractionEvent.Kind.SELECTION,
				                bridge.getContentType(),
				                handle,
				                SOURCE_ID);
						MylarPlugin.getContextManager().handleInteractionEvent(manipulationEvent, true);
					}
				}
			}
		});
	}
	
    public boolean contains(IResource local) {
    	return resources.contains(local);
    }

	public static String generateComment(ITask task, String completedPrefix, String progressPrefix) {
		String comment;
		if (task.isCompleted()) {
			comment = completedPrefix + " "; 
		} else {
			comment = progressPrefix + " ";
		}
		if (task.isLocal()) {
			comment += task.getDescription(false);
		} else { // bug report
			comment += LABEL_BUG + task.getDescription(false);
		}
		String url = task.getIssueReportURL();
		if (url != null && !url.equals("") && !url.endsWith("//")) {
			comment += "\n" + url;
		}
		return comment;
	}
    
    public static String getIssueIdFromComment(String comment) {
    	int bugIndex = comment.indexOf(LABEL_BUG);
    	if (bugIndex != -1) {
    		int idEnd = comment.indexOf(':', bugIndex);
    		int idStart = bugIndex + LABEL_BUG.length();
    		if (idEnd != -1 && idStart < idEnd) {
    			String id = comment.substring(idStart, idEnd);
    			if (id != null) return id.trim();
    		}
    	}
    	return null;
    }

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
}
