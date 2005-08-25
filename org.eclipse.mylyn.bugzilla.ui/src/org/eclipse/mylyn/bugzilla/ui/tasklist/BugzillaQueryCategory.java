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
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 */
public class BugzillaQueryCategory implements IQuery {
	
	private static final long serialVersionUID = 5517146402031743253L;	
	private String queryString;
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
			for(BugzillaSearchHit hit: collector.getResults()){
		
				// HACK need the server name and handle properly
				addHit(new BugzillaHit(hit.getId() + ": " + hit.getDescription(), hit.getPriority(), hit.getId(), null, hit.getState()));
			}
		}

	}	
		
	public BugzillaQueryCategory(String label, String url, String maxHits) {
		this.description = label;
		this.queryString = url;
		try{
			this.maxHits = Integer.parseInt(maxHits);
		} catch (Exception e){
			this.maxHits = -1;
		}
	}

	public String getDescription(boolean label) {
		if (hits.size() > 0 || !label) {
			if(!hasBeenRefreshed){
				return description + " <needs refresh>";	
			}else if(isMaxReached && label){
				return description + " <first "+ maxHits +" hits>";
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
	
	public String getQueryString() {
		return queryString;
	}
	
	
	
	public List<IQueryHit> getChildren() {
		return hits;
	}
	
	public void addHit(IQueryHit hit) {
		BugzillaTask task = BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().getFromBugzillaTaskRegistry(hit.getHandle());
		hit.setAssociatedTask(task);
		hits.add(hit);
	}

	public void removeHit(BugzillaHit hit) {
		hits.remove(hit);
	}
	
	public void refreshBugs() {
		hits.clear();
		final BugzillaCategorySearchOperation catSearch = new BugzillaCategorySearchOperation(
				getQueryString(), maxHits);
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
				//			                return status[0];
				return;
			} else if (!status[0].isOK()) {
				// there was an error, so display an error message
				PlatformUI.getWorkbench().getDisplay().asyncExec(
						new Runnable() {
							public void run() {
								ErrorDialog.openError(null,
										"Bugzilla Search Error", null,
										status[0]);
							}
						});
				status[0] = Status.OK_STATUS;
				return;
				//			                return status[0];
			}
		} catch (LoginException e) {
			// we had a problem while searching that seems like a login info
			// problem
			// thrown in BugzillaSearchOperation
			MessageDialog
					.openError(
							null,
							"Login Error",
							"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
			BugzillaPlugin.log(new Status(IStatus.ERROR,
					IBugzillaConstants.PLUGIN_ID, IStatus.OK, "", e));
		}
		return;
	}

	public void setQueryString(String url) {
		this.queryString = url;
	}
	
	public String getPriority() {
		String highestPriority = "P5";
		if (hits.isEmpty()) {
			return "P1";
		}
		for (IQueryHit hit : hits) {
			if (highestPriority.compareTo(hit.getPriority()) > 0) {
				highestPriority = hit.getPriority();
			}
		}
		return highestPriority;
	}

	public boolean isDirectlyModifiable() {
		return true;
	}
	
	public boolean isActivatable() {
		return false;
	}

	public boolean isDragAndDropEnabled() {
		return false;
	}
	
	public Color getForeground() {
       	return null;
	}

	public Font getFont() {
        for (ITaskListElement child : getChildren()) {
			if (child instanceof BugzillaHit){
				BugzillaHit hit = (BugzillaHit) child;
				BugzillaTask task = hit.getAssociatedTask();
				if(task != null && task.isActive()){
					return BOLD;
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

	public String getHandle() {
		return handle;
	}

	public void setDescription(String description) {
		this.description = description;		
	}

	public String getStringForSortingDescription() {
		return getDescription(true);
	}

	public void setHandle(String id) {
		this.handle = id;
	}
}
