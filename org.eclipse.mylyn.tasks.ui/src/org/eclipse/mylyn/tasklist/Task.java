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

package org.eclipse.mylar.tasklist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.TaskEditorInput;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class Task implements ITask {

	public static final long INACTIVITY_TIME_MILLIS = MylarPlugin.getContextManager().getActivityTimeoutSeconds() * 1000;
	public static final int MAX_LABEL_LENGTH = 50;
	
    private static final long serialVersionUID = 3545518391537382197L;
    private boolean active = false;
    protected String handle = "-1";
    private boolean category = false;
    private boolean hasReminded = false;
    
    /**
     * Either to local resource or URL.
     * TODO: consider changing to java.net.URL
     */
    private String contextPath;
    private String label;
    private String priority = "P3";
    private String notes = "";
    private int estimatedTime = 0;
    private boolean completed;
    private List<String> links = new ArrayList<String>();
    private List<String> plans = new ArrayList<String>();
    private String issueReportURL = "";
    private ITaskListCategory parentCategory = null;
    
    private Date timeActivated = null;
    private Date endDate = null;
    private Date creationDate = null;
    private Date reminderDate=null;
    private long elapsed;

    /**
     * null if root
     */
    private transient ITask parent;
    
    private List<ITask> children = new ArrayList<ITask>();
    
    @Override
    public String toString() {
        return label;
    }
    
    public String getPath() {
    	// returns relative contextPath Mylar Directory
        return contextPath;
    }
    
    public void setPath(String path) {
    	if (path.startsWith(".mylar")) {
    		this.contextPath = path.substring(path.lastIndexOf('/')+1, path.length());
    	} else if (!path.equals("")) {
    		this.contextPath = path;
    	}
    }
        
    public Task(String handle, String label, boolean newTask) {
        this.handle = handle;
        this.label = label;     
        this.contextPath = handle;
        if(newTask){
        	creationDate = new Date();
        }
    } 
    
    public String getHandleIdentifier() {
        return handle;
    }
    
    public void setHandle(String id) {
        this.handle = id;
    }
    
    public ITask getParent() {
        return parent;
    }

    public void setParent(ITask parent) {
        this.parent = parent;
    }
    public Object getAdapter(Class adapter) {
        return null;
    }    
    
    /**
     * Package visible in order to prevent sets that don't update the index.
     */
    public void setActive(boolean active, boolean isStalled) {
    	this.active = active;
    	if (active && !isStalled) {
    		timeActivated = new Date();
    	} else {    		
    		calculateElapsedTime(isStalled);
    		timeActivated = null;
    	}    	        
    }
    
    private void calculateElapsedTime(boolean isStalled) {
    	if (timeActivated == null) 
    		return;
    	elapsed += new Date().getTime() - timeActivated.getTime();
    	if(isStalled){
    		elapsed-= INACTIVITY_TIME_MILLIS;
    	}
    	if (isActive()) {
    		timeActivated = new Date();
    	} else {
    		timeActivated = null;
    	}
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void openTaskInEditor(boolean offline) {
    	Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
			public void run() {
				openTaskEditor();
			}
		});
    }

	/**
	 * Opens the task in an editor.
	 * @return Resulting <code>IStatus</code> of the operation
	 */
	protected void openTaskEditor() {
		
		// get the active page so that we can reuse it
		IWorkbenchPage page = MylarTasklistPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// if we couldn't get a page, get out
		if (page == null) {
			return;
		}

		IEditorInput input = new TaskEditorInput(this);
		try 
		{
			// try to open an editor on the input task
			page.openEditor(input, MylarTasklistPlugin.TASK_EDITOR_ID);
			
		} 
		catch (PartInitException ex) 
		{
			MylarPlugin.log(ex, "open failed");
		}
	}

    /**
     * Refreshes the tasklist viewer.
     * 
     * TODO: shouldn't be coupled to the TaskListView
     */
    public void notifyTaskDataChange() {
    	final Task task = this;
    	if (Workbench.getInstance() != null && !Workbench.getInstance().getDisplay().isDisposed()) {
	        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
	            public void run() {
	                if (TaskListView.getDefault() != null) TaskListView.getDefault().notifyTaskDataChanged(task);
	            }
	        });
    	}
    }
    
	public String getToolTipText() {
		return getDescription(true);
	}

    @Override
    public boolean equals(Object obj) {
       if (obj instanceof Task && obj != null) {
           return this.getHandleIdentifier().compareTo(((Task)obj).getHandleIdentifier()) == 0;
       } else {
           return false;
       }
    }
    
    @Override
    public int hashCode() {
        return this.getHandleIdentifier().hashCode(); 
    }
    public boolean isCompleted() {
    	return completed;
    }
    public void setCompleted(boolean completed) {
    	if (completed) {  
    		endDate = new Date();
    	}
        this.completed = completed;
    }

    public boolean isCategory() {
        return category;
    }

    public void setIsCategory(boolean category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

	public List<String> getRelatedLinks() {
		// TODO: removed check for null once xml updated.
		if (links == null) {
			links = new ArrayList<String>();
		}
		return links;
	}

	public void setRelatedLinks(List<String> relatedLinks) {
		this.links = relatedLinks;
	}

	public void addLink(String url) {
		links.add(url);
	}

	public void removeLink(String url) {
		links.remove(url);
	}
	
	public void setIssueReportURL(String url){
		issueReportURL = url;
	}
	
	public String getIssueReportURL(){
		return issueReportURL;
	}

	public String getNotes() {
		// TODO: removed check for null once xml updated.
		if (notes == null) {
			notes = "";
		}
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getElapsedTime() {
		if (isActive()) {
			calculateElapsedTime(false);			
		}
		return "" + elapsed;
	}

	public long getElapsedTimeLong() {
		if (isActive()) {
			calculateElapsedTime(false);			
		}
		return elapsed; 
	}
	public void setElapsedTime(String elapsedString) {
		if (elapsedString.equals("")) {
			elapsed = 0;
		} else {
			elapsed = Long.parseLong(elapsedString);
		}
	}

//	public String getEstimatedTimeForDisplay() {
//		if (estimatedTime == 0) {
//			return "";
//		} else {
//			return "" + estimatedTime * 10;
//		}		
//	}

	public int getEstimateTime() {
		return estimatedTime;
	}
	
	public void setEstimatedTime(int estimated) {
		this.estimatedTime = estimated;
	}	
	
	public List<ITask> getChildren() {
		return children;
	}
	
	public void addSubTask(ITask t) {
		children.add(t);
		if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
	}
	
	public void removeSubTask(ITask t) {
		children.remove(t);
		if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
	}
	
	public void setCategory(ITaskListCategory cat) {
		this.parentCategory = cat;
		if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
	}
	
	public void internalSetCategory(TaskCategory cat) {
		this.parentCategory = cat;
	}
    
    public ITaskListCategory getCategory() {
    	return parentCategory;
    }

	public Image getIcon() {
		if (issueReportURL != null && !issueReportURL.trim().equals("") && !issueReportURL.equals("http://")) {
			return TaskListImages.getImage(TaskListImages.TASK_WEB);
		} else {
			return TaskListImages.getImage(TaskListImages.TASK);
		}
	}

	public String getDescription(boolean truncate) {
		if (!truncate) {
			return label;
		} else {
			if (label == null || label.length() <= MAX_LABEL_LENGTH) {
				return label;
			} else {
				return label.substring(0, MAX_LABEL_LENGTH) + "...";
			}
		}
	}

	/**
	 * TODO: tasks hsouldn't know about images, or the context manager
	 */
	public Image getStatusIcon() {
		if (isActive()) {
    		return TaskListImages.getImage(TaskListImages.TASK_ACTIVE);
    	} else {
    		if (MylarPlugin.getContextManager().hasContext(getPath())) {
    			return TaskListImages.getImage(TaskListImages.TASK_INACTIVE_CONTEXT);
    		} else {
    			return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
    		}
    	}        	
	}
	
	public String getElapsedTimeForDisplay() {
		calculateElapsedTime(false);
		return DateUtil.getFormattedDuration(elapsed);		
	}
	
    public boolean canEditDescription() {
    	return true;
    }
    
    public String getDeleteConfirmationMessage() {
    	return "Delete the selected task and discard task context?";
    }
    
	public boolean isDirectlyModifiable() {
		return true;
	}
	
	public boolean isActivatable() {
		return true;
	}
	
	public boolean isDragAndDropEnabled() {
		return true;
	}

	public boolean participatesInTaskHandles() {
		return true;
	}
	
	public Color getForeground() {
        if (isCompleted()){
        	return GRAY_LIGHT;
        } else if (isActive()) {
        	return MylarTasklistPlugin.ACTIVE_TASK;
        } else {
        	return null;
        }
	}
	
	public Font getFont(){
		if (isActive()) return BOLD;            
        for (ITask child : getChildren()) {
			if (child.isActive())
				return BOLD;
		}
        return null;
	}
	
	public Date getEndDate() {
		return endDate;
	}	
	
	public String getEndDateString() {
		if (endDate != null) {
			String f = "yyyy-MM-dd HH:mm:ss.S z";
	    	SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
	    	return format.format(endDate);
		} else {
			return "";
		}		
	}
	
	public void setEndDate(String date) {
		if (!date.equals("")) {
			String formatString = "yyyy-MM-dd HH:mm:ss.S z";
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
			try {
				endDate = format.parse(date);
			} catch (ParseException e) {
				MylarPlugin.log(e, "Could not parse end date");
				endDate = null;
			}
		} else {
			if (isCompleted()) {
				endDate = new Date(0);
			}
		}
	}

	public void setReminderDate(Date date) {		
		reminderDate = date;		
	}

	public void setReminderDate(String date) {
		if (!date.equals("")) {
			String formatString = "yyyy-MM-dd HH:mm:ss.S z";
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
			try {
				reminderDate = format.parse(date);
			} catch (ParseException e) {
				MylarPlugin.log(e, "Could not parse end date");
				reminderDate = null;
			}
		} else {
			reminderDate = null;
		}
		
	}

	public Date getReminderDate() {
		return reminderDate;
	}
	
	public String getReminderDateString(boolean forDisplay) {
		if (reminderDate != null) {
			String f = "";
			if (forDisplay) {
				f = "EEE, yyyy-MM-dd";
			} else {
				f = "yyyy-MM-dd HH:mm:ss.S z";
			}			
	    	SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
	    	return format.format(reminderDate);
		} else {
			return "";
		}	
	}

	public boolean hasBeenReminded() {
		return hasReminded;
	}

	public void setReminded(boolean reminded) {
		this.hasReminded = reminded;	
	}

	public String getReminderDateForDisplay() {
		if (reminderDate != null) {
			
			String f = "EEE, yyyy-MM-dd";
	    	SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
	    	return format.format(reminderDate);
		} else {
			return "";
		}	
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(String date) {
		if (!date.equals("")) {
			String formatString = "yyyy-MM-dd HH:mm:ss.S z";
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
			try {
				creationDate = format.parse(date);
			} catch (ParseException e) {
				MylarPlugin.log(e, "Could not parse end date");
				creationDate = null;
			}
		} else {
			
		}
	}
	
	public String getCreationDateString() {
		if (creationDate != null) {
			String f = "yyyy-MM-dd HH:mm:ss.S z";
	    	SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
	    	return format.format(creationDate);
		} else {
			return "";
		}
	}

	public void setDescription(String description) {
		this.label = description;
	}

	public String getStringForSortingDescription() {
		return getDescription(true);
	}

	public String getEstimateTimeForDisplay() {		
		return estimatedTime / 10 + " Hours";
	}

	public void addPlan(String plan) {
		if (plan != null && !plans.contains(plan)) plans.add(plan);
	}

	public List<String> getPlans() {
		return plans;
	}
}
