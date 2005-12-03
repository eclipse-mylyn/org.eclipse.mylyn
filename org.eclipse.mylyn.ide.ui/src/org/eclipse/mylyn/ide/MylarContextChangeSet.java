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

package org.eclipse.mylar.ide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.synchronize.SyncInfo;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
import org.eclipse.team.internal.core.subscribers.SubscriberChangeSetCollector;
import org.osgi.service.prefs.Preferences;

/**
 * @author Mik Kersten
 */
public class MylarContextChangeSet extends ActiveChangeSet {

	private static final String PREFIX_HTTP = "http://";

	private static final String PREFIX_HTTPS = "https://";

	private static final String LABEL_PREFIX = "Mylar Task";

	private static final String LABEL_BUG = "Bug ";
	 
	private static final String CTX_TITLE = "title"; // HACK: copied from super

//	private List<IResource> resources; // TODO: get rid of this?
	
	private boolean suppressInterestContribution = false;

	private ITask task;

	// private JavaStructureBridge javaStructureBridge = new
	// JavaStructureBridge();

	public static final String SOURCE_ID = "org.eclipse.mylar.java.context.changeset.add";

	public MylarContextChangeSet(ITask task, SubscriberChangeSetCollector collector) {
		super(collector, LABEL_PREFIX);
		this.task = task;
		initTitle();
	}

	public void initTitle() {
		if (task.isLocal()) {
			super.setTitle(LABEL_PREFIX + ": " + this.task.getDescription(true));
		} else {
			super.setTitle(LABEL_PREFIX + ": " + LABEL_BUG + this.task.getDescription(true));
		}
	}

	/**
	 * Encodes the handle in the title, since init won't get called on this class.
	 */
	@Override
    public void save(Preferences prefs) {
    	super.save(prefs);
    	prefs.put(CTX_TITLE, getTitleForPersistance());
    }

	private String getTitleForPersistance() {
		return getTitle() + " (" + task.getHandleIdentifier() + ")";
	}
	
	public static String getHandleFromPersistedTitle(String title) {
		int delimStart = title.lastIndexOf('(');
		int delimEnd = title.lastIndexOf(')');
		if (delimStart != -1 && delimEnd != -1) {
			return title.substring(delimStart+1, delimEnd);
		} else {
			return null;
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
//		resources.remove(resource);
	}

	@Override
	public void remove(IResource[] newResources) {
		super.remove(newResources);
//		for (int i = 0; i < newResources.length; i++)
//			resources.remove(newResources[i]);
	}

	@Override
	public void add(SyncInfo info) {
		super.add(info);
		if (!suppressInterestContribution) {
			MylarIdePlugin.getDefault().getInterestUpdater().addResourceToContext(info.getLocal());
		}
//		resources.add(info.getLocal());
	}

	@Override
	public void add(SyncInfo[] infos) {
		super.add(infos);
	}

	@Override
	public void add(IResource[] newResources) throws TeamException {
		super.add(newResources);
//		for (int i = 0; i < newResources.length; i++) resources.add(newResources[i]);
	}

	public void restoreResources(IResource[] newResources) throws TeamException {
		suppressInterestContribution = true;
		try {
			super.add(newResources);
//			resources = new ArrayList<IResource>();
//			for (int i = 0; i < newResources.length; i++) {
//				resources.add(newResources[i]);
//			}
			setComment(getComment());
		} catch (TeamException e) {
			throw e;
		} finally {
			suppressInterestContribution = false;
		}
	}
	
	@Override
	public IResource[] getResources() {
//		return super.getResources();
		List<IResource> allResources = getAllResourcesInChangeContext();
		return allResources.toArray(new IResource[allResources.size()]);
	}
	  
	public List<IResource> getAllResourcesInChangeContext() {
		Set<IResource> allResources = new HashSet<IResource>();
		allResources.addAll(Arrays.asList(super.getResources()));
		if (MylarIdePlugin.getDefault() != null && task.isActive()) {
			// TODO: if super is always managed correctly should remove following line
			allResources.addAll(MylarIdePlugin.getDefault().getInterestingResources());
		}
		return new ArrayList<IResource>(allResources);
	}

	/**
	 * TODO: unnessary check context?
	 */
	public boolean contains(IResource local) {
//		System.err.println(">>> " + getAllResourcesInChangeContext().contains(local) + " " + local);
		return getAllResourcesInChangeContext().contains(local);
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
			comment += " \n" + url;
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
				if (id != null)
					return id.trim();
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

	@Override
	public boolean equals(Object object) {
		if (object instanceof MylarContextChangeSet && task != null) {
			MylarContextChangeSet changeSet = (MylarContextChangeSet)object;
			return task.equals(changeSet.getTask());
		} else {
			return super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		if (task != null) {
			return task.hashCode();
		} else {
			return super.hashCode();
		}
	}

	public ITask getTask() {
		return task;
	}
}
