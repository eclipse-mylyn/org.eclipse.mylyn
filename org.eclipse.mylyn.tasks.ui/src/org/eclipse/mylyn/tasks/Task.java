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
import java.util.List;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.ui.TaskEditorInput;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
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
    private String elapsedTime = "";
    private boolean completed;
    private transient List<Category> categories = new ArrayList<Category>();
    private RelatedLinks links = new RelatedLinks();
    
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
    
//    public String getRelativePath() {
//    	//returns relative path from Mylar Directory
//    
//    	if (path.startsWith("..")) {
//    		return "../" + path;
//    	} else {
//    		return path.substring(path.indexOf('/')+1, path.length());
//    	}    	
//    }
    
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

    public List<ITask> getChildren() {
        return children;
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

    public void removeSubtask(ITask task) {
        children.remove(task);
        task.setParent(null); // HACK
    }
    
    public void addSubtask(ITask task) {
        children.add(task);
        task.setParent(this);
    }

    /**
     * Package visible in order to prevent sets that don't update the index.
     */
    public void setActive(boolean active) {
        this.active = active;
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
			MylarPlugin.log(this.getClass().toString(), ex);
		}
	}

    /**
     * Refreshes the tasklist viewer.
     */
    public void notifyTaskDataChange() {
        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() {
                if (TaskListView.getDefault() != null) TaskListView.getDefault().notifyTaskDataChanged();
            }
        });
    }
    
	public String getToolTipText() {
		// No tool-tip used for a general task as of yet.
		return null;
	}
    public List<Category> getCategories() {
        return categories;
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
		// TODO: removed check for null once xml updated.
		if (elapsedTime == null) {
			elapsedTime = "";
		}
		return elapsedTime;
	}

	public void setElapsedTime(String elapsed) {
		this.elapsedTime = elapsed;
	}

	public String getEstimatedTime() {
		// TODO: removed check for null once xml updated.
		if (estimatedTime == null) {
			estimatedTime = "";
		}
		return estimatedTime;
	}

	public void setEstimatedTime(String estimated) {
		this.estimatedTime = estimated;
	}

	public List<ITask> getSubTasksInProgress() {
		List<ITask> inprogress = new ArrayList<ITask>();
		for (ITask task : children) {
            if (!task.isCompleted()) {
            	inprogress.add(task);
            }
        }
		return inprogress;
	}
	
	public List<ITask> getCompletedSubTasks() {
		List<ITask> complete = new ArrayList<ITask>();
		for (ITask task : children) {
            if (task.isCompleted()) {
            	complete.add(task);
            }
        }
		return complete;
	}
}
