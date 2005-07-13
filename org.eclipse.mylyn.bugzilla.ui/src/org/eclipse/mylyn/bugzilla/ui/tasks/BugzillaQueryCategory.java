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

package org.eclipse.mylar.bugzilla.ui.tasks;
import java.util.ArrayList;
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
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaCategorySearchOperation.ICategorySearchListener;
import org.eclipse.mylar.tasks.AbstractCategory;
import org.eclipse.mylar.tasks.TaskListImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 */
public class BugzillaQueryCategory extends AbstractCategory {
	
	private static final long serialVersionUID = 5517146402031743253L;	
	private String url;
	private List<BugzillaHit> hits = new ArrayList<BugzillaHit>();
	private boolean hasBeenRefreshed = false;
	
	public class BugzillaQueryCategorySearchListener implements ICategorySearchListener { 

		Map<Integer, BugzillaSearchHit> hits = new HashMap<Integer, BugzillaSearchHit>();
		
		public void searchCompleted(BugzillaResultCollector collector) {
			for(BugzillaSearchHit hit: collector.getResults()){
		
				// HACK need the server name and handle properly
				addHit(new BugzillaHit(hit.getId() + ": " + hit.getDescription(), hit.getPriority(), hit.getId(), null));
			}
		}

	}	
	
	private ICategorySearchListener listener = new BugzillaQueryCategorySearchListener();
	
	public BugzillaQueryCategory(String label, String url) {
		super(label);
		this.url = url;
	}

	public String getDescription(boolean label) {
		if (hits.size() > 0 || !label) {
			return super.getDescription(label);
		} else if (!hasBeenRefreshed) {
			return super.getDescription(label) + " <needs refresh>";
		} else {
			return super.getDescription(label) + " <no hits>";
		}
	}
	
	
	public Image getIcon() {
		return TaskListImages.getImage(BugzillaImages.CATEGORY_QUERY);
	}
	
	public String getUrl() {
		return url;
	}
	
	public List<BugzillaHit> getHits() {
		return hits;
	}
	
	public void addHit(BugzillaHit hit) {
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
				getUrl());
		catSearch.addResultsListener(listener);
		final IStatus[] status = new IStatus[1];

		try {
			// execute the search operation
			catSearch.execute(new NullProgressMonitor());
			hasBeenRefreshed = true;

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

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPriority() {
		String highestPriority = "P5";
		for (BugzillaHit hit : hits) {
			if (highestPriority.compareTo(hit.getPriority()) > 0) {
				highestPriority = hit.getPriority();
			}
		}
		return highestPriority;
	}
}
