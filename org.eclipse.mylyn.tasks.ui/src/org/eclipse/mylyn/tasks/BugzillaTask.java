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
/*
 * Created on 14-Jan-2005
 */
package org.eclipse.mylar.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.bugzilla.offlineReports.OfflineReportsFile;
import org.eclipse.mylar.bugzilla.ui.OfflineView;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.ui.BugzillaTaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;


/**
 * @author Mik Kersten
 */
public class BugzillaTask extends Task {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257007648544469815L;
	
    public static final String FILE_EXTENSION = ".bug_reports";
    
	public enum BugTaskState {FREE, WAITING, DOWNLOADING, COMPARING, OPENING}
	private transient BugTaskState state;

	/**
	 * The bug report for this BugzillaTask. This is <code>null</code> if the
	 * bug report with the specified ID was unable to download.
	 */
	protected transient BugReport bugReport = null;
	
	/**
	 * Value is <code>true</code> if the bug report has saved changes that
	 * need synchronizing with the Bugzilla server.
	 */
	private boolean isDirty;
	
	/** The last time this task's bug report was downloaded from the server. */
	protected Date lastRefresh;
	
	public static final ISchedulingRule rule = new ISchedulingRule() {
		public boolean isConflicting(ISchedulingRule schedulingRule) {
			return schedulingRule == this;
		}
		public boolean contains(ISchedulingRule schedulingRule) {
			return schedulingRule == this;
		}
	};

	public BugzillaTask(String id, String label) {
		super(id, label);
		isDirty = false;
		GetBugReportJob job = new GetBugReportJob("Downloading from Bugzilla server...");
		job.schedule();
	}    
    
	public BugzillaTask(String id, String label, boolean noDownload) {
        super(id, label);
        isDirty = false;
        if (!noDownload) {
            GetBugReportJob job = new GetBugReportJob("Downloading from Bugzilla server...");
            job.schedule();
        }        
    }
	
    @Override
	public String getLabel() {
        return MylarTasksPlugin.getDefault().getBugzillaProvider().getBugzillaDescription(this);
    }

    /**
	 * @return Returns the bugReport.
	 */
	public BugReport getBugReport() {
		return bugReport;
	}
	
	/**
	 * @param bugReport The bugReport to set.
	 */
	public void setBugReport(BugReport bugReport) {
		this.bugReport = bugReport;
	}
	
	/**
	 * @return Returns the serialVersionUID.
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	/**
	 * @return Returns the lastRefresh.
	 */
	public Date getLastRefresh() {
		return lastRefresh;
	}
	/**
	 * @param lastRefresh The lastRefresh to set.
	 */
	public void setLastRefresh(Date lastRefresh) {
		this.lastRefresh = lastRefresh;
	}
	/**
	 * @param state The state to set.
	 */
	public void setState(BugTaskState state) {
		this.state = state;
	}
	/**
	 * @return Returns <code>true</code> if the bug report has saved changes
	 *         that need synchronizing with the Bugzilla server.
	 */
	public boolean isDirty() {
		return isDirty;
	}
	
	/**
	 * @param isDirty The isDirty to set.
	 */
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		notifyTaskDataChange();
	}
	
	/**
	 * @return Returns the state of the Bugzilla task.
	 */
	public BugTaskState getState() {
		return state;
	}
	
	/**
	 * Try to download the bug from the server.
	 * @param bugId The ID of the bug report to download.
	 * 
	 * @return The bug report, or <code>null</code> if it was unsuccessfully
	 *         downloaded.
	 */
	public BugReport downloadReport() {
//			BugzillaTaskEditorInput input = new BugzillaTaskEditorInput(this);
		try {
			// XXX make sure to send in the server name if there are multiple repositories
			return BugzillaRepository.getInstance().getBug(getBugId(getHandle()));
		} catch (LoginException e) {
			MylarPlugin.log(this.getClass().toString(), e);
		} catch (IOException e) {
			MylarPlugin.log(this.getClass().toString(), e);
		}
		return null;
	}
	
    @Override
    public void openTaskInEditor(){
        openTask(-1);
    }
    
	/**
	 * Opens this task's bug report in an editor revealing the selected comment.
     * @param commentNumber The comment number to reveal
	 */
	public void openTask(int commentNumber) {
		if (state != BugTaskState.FREE) {
			return;
		}
		
		state = BugTaskState.OPENING;
		notifyTaskDataChange();
		OpenBugTaskJob job = new OpenBugTaskJob("Opening Bugzilla task in editor...", this);
		job.schedule();
		job.addJobChangeListener(new IJobChangeListener(){

			public void aboutToRun(IJobChangeEvent event) {
				// don't care about this event
			}

			public void awake(IJobChangeEvent event) {
				// don't care about this event
			}

			public void done(IJobChangeEvent event) {
				state = BugTaskState.FREE;
				notifyTaskDataChange();	
			}

			public void running(IJobChangeEvent event) {
				// don't care about this event
			}

			public void scheduled(IJobChangeEvent event) {
				// don't care about this event
			}

			public void sleeping(IJobChangeEvent event) {
				// don't care about this event
			}
		});
   }
	
	/**
	 * @return <code>true</code> if the bug report for this BugzillaTask was
	 *         successfully downloaded.
	 */
	public boolean isBugDownloaded() {
		return bugReport != null;
	}
	
	@Override
	public String toString() {
		return "bugzilla report id: " + getHandle();
	}

	protected void openTaskEditor(final IEditorInput input) {
		if (isBugDownloaded()) {
			
			Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
				public void run() {
					// get the active workbench page
					IWorkbenchPage page = MylarTasksPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
					
					// if we couldn't get the page, get out of here
					if (page == null)
						return;
						
					try {
						// try to open an editor on the input bug
						//page.openEditor(input, IBugzillaConstants.EXISTING_BUG_EDITOR_ID);
						page.openEditor(input, "org.eclipse.mylar.tasks.ui.bugzillaTaskEditor");
					} 
					catch (PartInitException ex) {
						MylarPlugin.log(this.getClass().toString(), ex);
						return;
					}
				}
			});
		}
		else {
			Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
							"Could not open bug.", "Bug #" + getHandle()
											+ " could not be read from the server.  Try refreshing the bug task.");
				}
			});
		}
	}
	
	/**
	 * @return Returns the last time this task's bug report was downloaded from
	 *         the server.
	 */
	public Date getLastRefreshTime() {
		return lastRefresh;
	}
	
	/**
	 * @return The number of seconds ago that this task's bug report was
	 *         downloaded from the server.
	 */
	public long getTimeSinceLastRefresh() {
		Date timeNow = new Date();
		return (timeNow.getTime() - lastRefresh.getTime())/1000;
	}
	
	private class GetBugReportJob extends Job {
		public GetBugReportJob(String name) {
			super(name);
			setRule(rule);
			state = BugTaskState.WAITING;
			notifyTaskDataChange();
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			state = BugTaskState.DOWNLOADING;
			notifyTaskDataChange();
			// Update time this bugtask was last downloaded.
			lastRefresh = new Date();
			bugReport = downloadReport();			
			state = BugTaskState.FREE;			
			updateTaskDetails();
			notifyTaskDataChange();
			saveBugReport(true);
			return new Status(IStatus.OK, MylarPlugin.IDENTIFIER, IStatus.OK, "", null);
		}	
	}
	
	public void updateTaskDetails() {
		for (Iterator<Attribute> it = bugReport.getAttributes().iterator(); it.hasNext();) {
			Attribute attribute = it.next();
			String key = attribute.getParameterName();
			if (key  != null &&key.equals("priority")) {
				System.out.println(attribute.getValue());
			}
		}
	}
	
	private class OpenBugTaskJob extends Job {
		
		protected BugzillaTask bugTask;
		
		public OpenBugTaskJob(String name, BugzillaTask bugTask) {
			super(name);
			this.bugTask = bugTask;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try{
				final IEditorInput input = new BugzillaTaskEditorInput(bugTask);
				state = BugTaskState.OPENING;
				notifyTaskDataChange();
				openTaskEditor(input);
				
				state = BugTaskState.FREE;
				notifyTaskDataChange();
				return new Status(IStatus.OK, MylarPlugin.IDENTIFIER, IStatus.OK, "", null);
			}catch(Exception e){
				MylarPlugin.log(this.getClass().toString(), e);
			}
			return Status.CANCEL_STATUS;
		}
	}

	/**
	 * Refreshes the bug report with the Bugzilla server.
	 */
	public void refresh() {
		// The bug report must be untouched, and this task must not be busy.
		if (isDirty() || (state != BugTaskState.FREE)) {
			return;
		}
		GetBugReportJob job = new GetBugReportJob("Refreshing with Bugzilla server...");
		job.schedule();
	}

	@Override
	public String getToolTipText() {
		// Get the current time.
		Date timeNow = new Date();
		
		// Get the number of minutes between the current time
		// and the last time the bug report was downloaded
		long timeDifference = (timeNow.getTime() - lastRefresh.getTime())/60000;
		
		// Calculate the number of minutes and hours.
		// The amount left in "timeDifference" is the 
		// days' difference.
		long minutes = timeDifference % 60;
		timeDifference /= 60;
		long hours = timeDifference % 24;
		timeDifference /= 24;
		
		// Gradually generate the tooltip string...
		String toolTip;
		if (bugReport == null) {
			toolTip = "Last attempted download ";
		}
		else {
			toolTip = "Last downloaded ";
		}
		
		if (timeDifference > 0) {
			toolTip += timeDifference + ((timeDifference == 1) ? " day " : " days ");
		}
		if (hours > 0 || timeDifference > 0) {
			toolTip += hours + ((hours == 1) ? " hour " : " hours ");
		}
		toolTip += minutes + ((minutes == 1) ? " minute " : " minutes ") + "ago";
		
		return toolTip;
	}
	
    public boolean readBugReport() {
    	// XXX server name needs to be with the bug report
    	int location = BugzillaPlugin.getDefault().getOfflineReports().find(getBugId(getHandle()));
    	if(location == -1){
    		bugReport = null;
    		return true;
    	}
    	bugReport = (BugReport)BugzillaPlugin.getDefault().getOfflineReports().elements().get(location);
    	return true;
    }

    public void saveBugReport(boolean refresh) {
    	if(bugReport == null)
    		return;

    	// XXX use the server name for multiple repositories
    	OfflineReportsFile offlineReports = BugzillaPlugin.getDefault().getOfflineReports();
    	int location = offlineReports.find(getBugId(getHandle()));
    	if(location != -1){
	    	IBugzillaBug tmpBugReport = offlineReports.elements().get(location);
	    	List<IBugzillaBug> l = new ArrayList<IBugzillaBug>(1);
	    	l.add(tmpBugReport);
	    	offlineReports.remove(l);
    	}
	    offlineReports.add(bugReport);
	    
	    if(refresh){
		    final IWorkbench workbench = PlatformUI.getWorkbench();
	        workbench.getDisplay().asyncExec(new Runnable() {
	            public void run() {
	            	OfflineView.refresh();
	            }
	        });
	    }	    
    }

    public static String getServerName(String handle) {
		int index = handle.lastIndexOf('-'); 
		if(index != -1){
			return handle.substring(0, index);
		}
		return null;
	}
	
	public static int getBugId(String handle) {
		int index = handle.lastIndexOf('-');
		if(index != -1){
			String id = handle.substring(index+1);
			return Integer.parseInt(id);
		}
		return -1;
	}
}
