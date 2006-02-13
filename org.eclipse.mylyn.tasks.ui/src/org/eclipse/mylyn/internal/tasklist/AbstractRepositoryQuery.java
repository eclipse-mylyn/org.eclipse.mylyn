/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryQuery implements ITaskContainer {

	protected String repositoryUrl;

	protected String queryUrl;

	protected int maxHits;

	private List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();

	protected Date lastRefresh;

	protected String description = "";

	private String handle = "";

	public abstract String getRepositoryKind();

	public String getDescription() {
		return description;
	}

//	public Image getIcon() {
//		return TaskListImages.getImage(TaskListImages.QUERY);
//	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public List<ITask> getChildren() {
		List<ITask> tasks = new ArrayList<ITask>();
		for (AbstractQueryHit hit : getHits()) {
			ITask task = hit.getCorrespondingTask();
			if (task != null) {
				tasks.add(task);
			}
		}
		return tasks;
	}

	public boolean isArchive() {
		return false;
	}

	public void setIsArchive(boolean isArchive) {
		// ignore
	}

	public List<AbstractQueryHit> getHits() {
		return Collections.unmodifiableList(hits);
	}

	public void clearHits() {
		hits.clear();
	}
	
	public void addHit(AbstractQueryHit hit) {
		AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(
				getRepositoryKind());
		ITask correspondingTask = client.getTaskFromArchive(hit.getHandleIdentifier());
		if (correspondingTask instanceof AbstractRepositoryTask) {
			hit.setCorrespondingTask((AbstractRepositoryTask) correspondingTask);
		}
		hits.add(hit);
	}

	public void removeHit(AbstractQueryHit hit) {
		hits.remove(hit);
	}

	public void setQueryUrl(String url) {
		this.queryUrl = url;
	}

	public String getPriority() {
		String highestPriority = Task.PriorityLevel.P5.toString();
		if (hits.isEmpty()) {
			return Task.PriorityLevel.P1.toString();
		}
		for (AbstractQueryHit hit : hits) {
			if (highestPriority.compareTo(hit.getPriority()) > 0) {
				highestPriority = hit.getPriority();
			}
		}
		return highestPriority;
	}

	public boolean isLocal() {
		return true;
	}

	public boolean isCompleted() {
		return false;
	}

	public String getToolTipText() {
		String tooltip = "";
		if (hits.size() == 1) {
			tooltip += "1 hit";
		} else {
			tooltip += hits.size() + " hits";
		}
		// if (maxHitsReached) {
		// tooltip += " MAX REACHED";
		// }
		if (maxHits != -1) {
			tooltip += " (max set to: " + maxHits + ")";
		}
		tooltip += AbstractRepositoryTask.getLastRefreshTime(lastRefresh);
		return tooltip;
		// if (hits.size() > 0 || !label) {
		// if (!hasBeenRefreshed && label) {
		// return description + " <needs refresh>";
		// } else if (maxHitsReached && label) {
		// return description + " <first " + maxHits + " hits>";
		// } else {
		// return description;
		// }
		// } else if (!hasBeenRefreshed) {
		// return description + " <needs refresh>";
		// } else {
		// return description + " <no hits>";
		// }
	}

	public int getMaxHits() {
		return maxHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}

	public String getHandleIdentifier() {
		return handle;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHandleIdentifier(String id) {
		this.handle = id;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public Date getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(Date lastRefresh) {
		this.lastRefresh = lastRefresh;
	}
}
