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

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
import org.eclipse.team.internal.core.subscribers.SubscriberChangeSetCollector;

/**
 * @author Mik Kersten
 */
public class MylarContextChangeSet extends ActiveChangeSet {

	private static final String PREFIX_URL = "Report ";
	private static final String LABEL_URL = "URL: ";
	private static final String LABEL_PREFIX = "Mylar Task";
	private static final String LABEL_BUG = "Bug ";
	
	private List<IResource> resources;
	private ITask task;
	
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
			comment += "\n" + PREFIX_URL + LABEL_URL + url;
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
    	int urlIndex = comment.indexOf(LABEL_URL);
    	if (urlIndex != -1) {
    		int idStart = urlIndex + LABEL_URL.length();
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
