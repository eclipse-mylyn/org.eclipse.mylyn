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
import org.eclipse.mylar.tasks.ui.TaskEditorInput;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.Workbench;


/**
 * @author Mik Kersten
 */
public class Task implements ITask, ITaskListElement {

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
    private String elapsedTime = "";
    private boolean completed;
    private RelatedLinks links = new RelatedLinks();
    private TaskCategory parentCategory = null;
    
    private Date activeStart = null;
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
    	if (active) {
    		activeStart = new Date();
    	} else {    		
    		calculateElapsedTime();
    		activeStart = null;
    	}    	
        this.active = active;
    }
    
    private void calculateElapsedTime() {
    	if (activeStart == null) 
    		return;
    	long elapsedMin = new Date().getTime() - activeStart.getTime();
    	long min = 1000*60;
    	this.elapsed += elapsedMin / min;
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
     */
    public void notifyTaskDataChange() {
    	final Task task = this;
        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() {
                if (TaskListView.getDefault() != null) TaskListView.getDefault().notifyTaskDataChanged(task);
            }
        });
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
		if (elapsedTime == "" && activeStart != null) {
			calculateElapsedTime();
			
		}
		return elapsedTime;
	}

	public void setElapsedTime(String elapsed) {
		this.elapsedTime = elapsed;
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
		return MylarImages.getImage(MylarImages.TASK);
	}

	public String getDescription(boolean label) {
		return getLabel();
	}

	public Image getStatusIcon() {
		if (isActive()) {
    		return MylarImages.getImage(MylarImages.TASK_ACTIVE);
    	} else {
    		return MylarImages.getImage(MylarImages.TASK_INACTIVE);
    	}        	
	}
}
