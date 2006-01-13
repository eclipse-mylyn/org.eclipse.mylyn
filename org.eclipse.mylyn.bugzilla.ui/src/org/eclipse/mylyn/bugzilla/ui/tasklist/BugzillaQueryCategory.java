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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.search.BugzillaSearchHit;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.search.BugzillaResultCollector;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaCategorySearchOperation.ICategorySearchListener;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITaskQuery;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.repositories.TaskRepository;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class BugzillaQueryCategory implements ITaskQuery {

	private String repositoryUrl;
	
	private String queryUrl;

	private int maxHits;

	private List<IQueryHit> hits = new ArrayList<IQueryHit>();

	private boolean hasBeenRefreshed = false;

	protected Date lastRefresh;

	protected String description = "";

	private String handle = "";

	private ICategorySearchListener listener = new BugzillaQueryCategorySearchListener();

	private boolean isMaxReached = false;

	public class BugzillaQueryCategorySearchListener implements ICategorySearchListener {

		Map<Integer, BugzillaSearchHit> hits = new HashMap<Integer, BugzillaSearchHit>();

		public void searchCompleted(BugzillaResultCollector collector) {
			for (BugzillaSearchHit hit : collector.getResults()) {

				addHit(new BugzillaHit(
						hit.getId() + ": " + hit.getDescription(), 
						hit.getPriority(), 
						repositoryUrl,
						hit.getId(), 
						null,
						hit.getState()));
			}
		}

	}

	public BugzillaQueryCategory(String repositoryUrl, String queryUrl, String label, String maxHits) {
		this.description = label;
		this.queryUrl = queryUrl;
		this.repositoryUrl = repositoryUrl;
		try {
			this.maxHits = Integer.parseInt(maxHits);
		} catch (Exception e) {
			this.maxHits = -1;
		}
	}

	public String getDescription(boolean label) {
		if (hits.size() > 0 || !label) {
			if (!hasBeenRefreshed && label) {
				return description + " <needs refresh>";
			} else if (isMaxReached && label) {
				return description + " <first " + maxHits + " hits>";
			} else {
				return description;
			}
		} else if (!hasBeenRefreshed) {
			return description + " <needs refresh>";
		} else {
			return description + " <no hits>";
		}
	}

	public Image getIcon() {
		return TaskListImages.getImage(BugzillaImages.CATEGORY_QUERY);
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public List<IQueryHit> getHits() {
		return hits;
	}

	public void addHit(IQueryHit hit) {
		BugzillaTask task = BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().getFromBugzillaTaskRegistry(
				hit.getHandleIdentifier());
		hit.setCorrespondingTask(task);
		hits.add(hit);
	}

	public void removeHit(BugzillaHit hit) {
		hits.remove(hit);
	}

	public void refreshBugs() {
		hits.clear();
		// refresh the view to show that the results are gone
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (TaskListView.getDefault() != null)
					TaskListView.getDefault().getViewer().refresh();
			}
		});

		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(BugzillaPlugin.REPOSITORY_KIND, repositoryUrl);
		if (repository == null) {
            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                public void run() {
                	MessageDialog.openInformation(
        					Display.getDefault().getActiveShell(), IBugzillaConstants.TITLE_MESSAGE_DIALOG,
        					"No task repository associated with this query. Open the query to associate it with a repository.");  
                }
            });
//			MylarStatusHandler.fail(null, "could not find repository for url: " + repositoryUrl, true);
		} else {
			final BugzillaCategorySearchOperation catSearch = new BugzillaCategorySearchOperation(
					repository, getQueryUrl(), maxHits);
			catSearch.addResultsListener(listener);
			final IStatus[] status = new IStatus[1];
	
			try {
				// execute the search operation
				catSearch.execute(new NullProgressMonitor());
				isMaxReached = catSearch.isMaxReached();
				hasBeenRefreshed = true;
				lastRefresh = new Date();
	
				// get the status of the search operation
				status[0] = catSearch.getStatus();
	
				// determine if there was an error, if it was cancelled, or if it is
				// ok
				if (status[0].getCode() == IStatus.CANCEL) {
					// it was cancelled, so just return
					status[0] = Status.OK_STATUS;
				} else if (!status[0].isOK()) {
					// there was an error, so display an error message
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							ErrorDialog.openError(null, "Bugzilla Search Error", null, status[0]);
						}
					});
					status[0] = Status.OK_STATUS;
				}
			} catch (LoginException e) {
				// we had a problem while searching that seems like a login info
				// problem
				// thrown in BugzillaSearchOperation
				MessageDialog
						.openError(
								Display.getDefault().getActiveShell(),
								"Login Error",
								"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.OK, "", e));
			}
		}
	}

	public void setQueryUrl(String url) {
		this.queryUrl = url;
	}

	public String getPriority() {
		String highestPriority = MylarTaskListPlugin.PriorityLevel.P5.toString();
		if (hits.isEmpty()) {
			return MylarTaskListPlugin.PriorityLevel.P1.toString();
		}
		for (IQueryHit hit : hits) {
			if (highestPriority.compareTo(hit.getPriority()) > 0) {
				highestPriority = hit.getPriority();
			}
		}
		return highestPriority;
	}

	public boolean isLocal() {
		return true;
	}

	public boolean isActivatable() {
		return false;
	}

	public boolean isDragAndDropEnabled() {
		return false;
	}

	public Font getFont() {
		for (ITaskListElement child : getHits()) {
			if (child instanceof BugzillaHit) {
				BugzillaHit hit = (BugzillaHit) child;
				BugzillaTask task = hit.getCorrespondingTask();
				if (task != null && task.isActive()) {
					return TaskListImages.BOLD;
				}
			}
		}
		return null;
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
		tooltip += BugzillaTask.getLastRefreshTime(lastRefresh);
		return tooltip;
	}

	public int getMaxHits() {
		return maxHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}

	public Image getStatusIcon() {
		return null;
	}

	public String getHandleIdentifier() {
		return handle;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStringForSortingDescription() {
		return getDescription(true);
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
}
