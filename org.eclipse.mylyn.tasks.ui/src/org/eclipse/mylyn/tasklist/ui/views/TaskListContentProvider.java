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

package org.eclipse.mylar.tasklist.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.tasklist.ICategory;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskFilter;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mik Kersten
 */
public class TaskListContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		private final TaskListView view;

		public TaskListContentProvider(TaskListView view) {
			this.view = view;
		}
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			this.view.expandToActiveTasks();
        }
        
		public void dispose() {
        	// ignore
        }
        
        public Object[] getElements(Object parent) {
            if (parent.equals(this.view.getViewSite())) {
//            	if (MylarTasklistPlugin.getTaskListManager() != null) {
            		return applyFilter(MylarTasklistPlugin.getTaskListManager().getTaskList().getRoots()).toArray();
//            	} else {
//            		return new Object[0];
//            	}
//            	return MylarTasklistPlugin.getTaskListManager().getTaskList().getRoots().toArray();            	          
            }
            return getChildren(parent);
        }
        
        public Object getParent(Object child) {
            if (child instanceof ITask) {
            	if (((ITask)child).getParent() != null) {
            		return ((ITask)child).getParent();
            	} else {
            		return ((ITask)child).getCategory();
            	}
                
            }
            return null;
        }
        
        public Object [] getChildren(Object parent) {
        	return getFilteredChildrenFor(parent).toArray();
        }
        
        public boolean hasChildren(Object parent) {  
            if (parent instanceof ICategory) {
            	ICategory cat = (ICategory)parent;
                return cat.getChildren() != null && cat.getChildren().size() > 0;
            }  else if (parent instanceof Task) {
            	Task t = (Task) parent;
            	return t.getChildren() != null && t.getChildren().size() > 0;
            } else if (parent instanceof IQuery) {
            	IQuery t = (IQuery) parent;
            	return t.getChildren() != null && t.getChildren().size() > 0;
            } 
            return false;
        }
        private List<Object> applyFilter(List<Object> list) {
        	String filterText = ((Text)this.view.tree.getFilterControl()).getText();
        	if (containsNoFilterText(filterText)) {
        		List<Object> filteredRoots = new ArrayList<Object>();
        		for (int i = 0; i < list.size(); i++) {
        			if (list.get(i) instanceof ITask) {
        				if (!filter(list.get(i))) {
        					filteredRoots.add(list.get(i));
        				}
        			} else if (list.get(i) instanceof ICategory) {
//        				if(((ICategory)list.get(i)).isArchive())
//        					continue;
        				if (selectCategory((ICategory)list.get(i))) {
        					filteredRoots.add(list.get(i));
        				}
        			} else if (list.get(i) instanceof IQuery) {
        				if (selectQuery((IQuery)list.get(i))) {
        					filteredRoots.add(list.get(i));
        				}
        			}
	        	}
        		return filteredRoots;
        	} else {
        		return list;
        	}
        }
        
        /**
         * See bug 109693
         */
        private boolean containsNoFilterText(String filterText) {
        	return filterText == null || filterText.length() == 0;
		}
		
        private boolean selectQuery(IQuery cat) {
        	List<? extends ITaskListElement> list = cat.getChildren();
        	if (list.size() == 0) {
        		return true;
        	}
        	for (int i = 0; i < list.size(); i++) {
        		if (!filter(list.get(i))) {
        			return true;
        		}    		
        	}
        	return false;
        }
        
        private boolean selectCategory(ICategory cat) {
        	if(cat.isArchive()){
        		for (ITask task: cat.getChildren()) {
					if(task.isActive()){
						ITask t = MylarTasklistPlugin.getTaskListManager().getTaskForHandle(task.getHandle(), false);
						if(t == null)
							return true;
					}
				}
        		return false;
        	}
        	List<? extends ITaskListElement> list = cat.getChildren();
        	if (list.size() == 0) {
        		return true;
        	}
        	for (int i = 0; i < list.size(); i++) {
        		if (!filter(list.get(i))) {
        			return true;
        		}    		
        	}
        	return false;
        }
        
        private List<Object> getFilteredChildrenFor(Object parent) {
        	if (containsNoFilterText(((Text) this.view.tree.getFilterControl()).getText())
        		|| ((Text) this.view.tree.getFilterControl()).getText().startsWith(TaskListView.FILTER_LABEL)) {
        		List<Object> children = new ArrayList<Object>();
	        	if (parent instanceof ICategory) {
	        		if(((ICategory)parent).isArchive()){
	        			for (ITask task: ((ICategory)parent).getChildren()) {
	    					if(task.isActive()){
	    						ITask t = MylarTasklistPlugin.getTaskListManager().getTaskForHandle(task.getHandle(), false);
	    						if(t == null)
	    							children.add(task);
	    					}
	    				} 
	        			return children;
	        		}
	        		List<? extends ITaskListElement> list = ((ICategory) parent)
							.getChildren();
        			for (int i = 0; i < list.size(); i++) {
            			if (!filter(list.get(i))) {
            				children.add(list.get(i));
	            		}    		
    	        	} 
        			return children;
        		} else if (parent instanceof IQuery) {
					List<? extends ITaskListElement> list = ((IQuery) parent)
							.getChildren();
					for (int i = 0; i < list.size(); i++) {
						if (!filter(list.get(i))) {
							children.add(list.get(i));
						}
					}
					return children;
				}else if (parent instanceof Task) {
        			List<ITask> subTasks = ((Task)parent).getChildren();
	        		for (ITask t : subTasks) {
    	    			if (!filter(t)) {
        					children.add(t);
        				}
        			}
	        		return children;
    	    	}
			} else {
				List<Object> children = new ArrayList<Object>();
				if (parent instanceof ICategory) {
					children.addAll(((ICategory) parent).getChildren());
					return children;					
				} else if (parent instanceof IQuery) {
					children.addAll(((IQuery) parent).getChildren());
					return children;					
				}else if (parent instanceof Task) {
					children.addAll(((Task) parent).getChildren());
					return children;	
				}
        	}
        	return new ArrayList<Object>();
        }
        
        private boolean filter(Object obj){
        	for (ITaskFilter filter : this.view.filters) {
    			if (!filter.select(obj)) {
    				return true;
    			}
    		} 
        	return false;
        }
    }