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
 * Created on Dec 22, 2004
  */
package org.eclipse.mylar.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.ui.TaskEditorInput;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
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

    private static final long serialVersionUID = 3545518391537382197L;
    private boolean active = false;
    protected String handle = "-1";
    private boolean category = false;
    
    
    /**
     * Either to local resource or URL.
     * TODO: consider changing to java.net.URL
     */
    private String path;
    private String label;
    private String priority = "P3";
    private String notes = "";
    private String estimatedTime = "";
//    private String elapsedTime = "";
    private boolean completed;
    private RelatedLinks links = new RelatedLinks();
    private TaskCategory parentCategory = null;
    
    private Date timeActivated = null;
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
    	// returns relative path Mylar Directory
        return path;
    }
    
    public void setPath(String path) {
    	if (path.startsWith(".mylar")) {
    		this.path = path.substring(path.lastIndexOf('/')+1, path.length());
    	} else if (!path.equals("")) {
    		this.path = path;
    	}
    }
    
    public Task(String handle, String label) {
        this.handle = handle;
        this.label = label;     
        this.path = handle;
    } 
    
    public String getHandle() {
        return handle;
    }
    public void setHandle(String id) {
        this.handle = id;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
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
    public void setActive(boolean active) {
    	this.active = active;
    	if (active) {
    		timeActivated = new Date();
    	} else {    		
    		calculateElapsedTime();
    		timeActivated = null;
    	}    	        
    }
    
    private void calculateElapsedTime() {
    	if (timeActivated == null) 
    		return;
    	elapsed += new Date().getTime() - timeActivated.getTime();
    	if (isActive()) {
    		timeActivated = new Date();
    	} else {
    		timeActivated = null;
    	}
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void openTaskInEditor() {
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
		IWorkbenchPage page = MylarTasksPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// if we couldn't get a page, get out
		if (page == null) {
			return;
		}

		IEditorInput input = new TaskEditorInput(this);
		try 
		{
			// try to open an editor on the input task
			page.openEditor(input, MylarTasksPlugin.TASK_EDITOR_ID);
			
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
		// No tool-tip used for a general task as of yet.
		return null;
	}

    @Override
    public boolean equals(Object obj) {
       if (obj instanceof Task && obj != null) {
           return this.getHandle() == ((Task)obj).getHandle();
       } else {
           return false;
       }
    }
    
    @Override
    public int hashCode() {
        return this.getHandle().hashCode(); 
    }
    public boolean isCompleted() {
    	return completed;
    }
    public void setCompleted(boolean completed) {
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

	public RelatedLinks getRelatedLinks() {
		// TODO: removed check for null once xml updated.
		if (links == null) {
			links = new RelatedLinks();
		}
		return links;
	}

	public void setRelatedLinks(RelatedLinks relatedLinks) {
		this.links = relatedLinks;
	}

	public void addLink(String url) {
		links.add(url);
	}

	public void removeLink(String url) {
		links.remove(url);
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
			calculateElapsedTime();			
		}
		return "" + elapsed;
	}

	public void setElapsedTime(String elapsedString) {
		if (elapsedString.equals("")) {
			elapsed = 0;
		} else {
			elapsed = Long.parseLong(elapsedString);
		}
	}

	public String getEstimatedTime() {
		if (estimatedTime == null) {
			estimatedTime = "";
		}
		return estimatedTime;
	}

	public void setEstimatedTime(String estimated) {
		this.estimatedTime = estimated;
	}	
	
	public List<ITask> getChildren() {
		return children;
	}
	
	public void addSubTask(ITask t) {
		children.add(t);
	}
	
	public void removeSubTask(ITask t) {
		children.remove(t);
	}
	
	public void setCategory(TaskCategory cat) {
		this.parentCategory = cat;
	}
    
    public TaskCategory getCategory() {
    	return parentCategory;
    }

	public Image getIcon() {
		return TaskListImages.getImage(TaskListImages.TASK);
	}

	public String getDescription(boolean label) {
		return getLabel();
	}

	public Image getStatusIcon() {
		if (isActive()) {
    		return TaskListImages.getImage(TaskListImages.TASK_ACTIVE);
    	} else {
    		return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
    	}        	
	}
	
	public String getElapsedTimeForDisplay() {
		long seconds = elapsed / 1000;
		long minutes = 0;
		long hours = 0;
//		final long SECOND = 1000;
		final long MIN = 60;
		final long HOUR = MIN * 60;
		
		String hour = "";
		String min = "";
		String sec = "";
		if (seconds >= HOUR) {
			hours = seconds / HOUR;
			if (hours == 1) {
				hour = hours + " hour ";
			} else if (hours > 1) {
				hour = hours + " hours ";
			}
			seconds -= hours * HOUR;
			
			minutes = seconds / MIN;
			if (minutes == 1) {
				min = minutes + " minute ";
			} else if (minutes > 1) {
				min = minutes + " minutes ";
			}
//			seconds -= minutes * MIN;
//			if (seconds == 1) {
//				sec = seconds + " second";
//			} else if (seconds > 1) {
//				sec = seconds + " seconds";
//			}
			return hour + min + sec;
		} else if (seconds >= MIN) {
			minutes = seconds / MIN;
			if (minutes == 1) {
				min = minutes + " minute ";
			} else if (minutes > 1) {
				min = minutes + " minutes ";
			}
//			seconds -= minutes * MIN;
//			if (seconds == 1) {
//				sec = seconds + " second";
//			} else if (seconds > 1) {
//				sec = seconds + " seconds";
//			}
			return min + sec;
		} else {
//			if (seconds == 1) {
//				sec = seconds + " second";
//			} else if (seconds > 1) {
//				sec = seconds + " seconds";
//			}
			return sec;
		}
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

	public ITask getCorrespondingActivatableTask() {
		return this;
	}
	
	public boolean hasCorrespondingActivatableTask() {
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
        	return GRAY_VERY_LIGHT;
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
}
