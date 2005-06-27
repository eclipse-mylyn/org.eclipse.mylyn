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
package org.eclipse.mylar.tasks.ui.views;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.dt.MylarWebRef;
import org.eclipse.mylar.tasks.BugzillaTask;
import org.eclipse.mylar.tasks.Category;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.bugzilla.BugzillaStructureBridge;
import org.eclipse.mylar.tasks.ui.BugzillaTaskEditorInput;
import org.eclipse.mylar.tasks.ui.TaskEditorInput;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.internal.views.Highlighter;
import org.eclipse.mylar.ui.internal.views.HighlighterImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 */
public class TaskListView extends ViewPart {

	private static TaskListView INSTANCE;
	
	//private CheckboxTreeViewer viewer;
	private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    
    private RefreshAction refresh;
    private CreateTaskAction createTask;
    private CreateCategoryAction createCategory;
    private CreateBugzillaTaskAction createBugzillaTask; 
    private RenameAction rename;
    private DeleteAction delete;
    private OpenTaskEditorAction doubleClickAction;
    private ClearTaskscapeAction clearSelectedTaskscapeAction;

    //private Action toggleIntersectionModeAction = new ToggleIntersectionModeAction();
//    private Action toggleFilteringAction = new ToggleGlobalInterestFilteringAction();

    private MarkTaskCompleteAction completeTask;
    private MarkTaskIncompleteAction incompleteTask;
    private FilterCompletedTasksAction filterCompleteTask;
    private FilterIncompleteTasksAction filterInCompleteTask;
    private PriorityDropDownAction filterOnPriority;
    private Action moveTaskToRoot; 
    private PriorityFilter priorityFilter = new PriorityFilter();
    
    protected String[] columnNames = new String[] { "", ".", "!", "Description", "handle" };
    protected int[] columnWidths = new int[] { 70, 20, 20, 120, 70 };
    private TreeColumn[] columns;
    private IMemento taskListMemento;
    public static final String columnWidthIdentifier = "org.eclipse.mylar.tasks.ui.views.tasklist.columnwidth";
    public static final String tableSortIdentifier = "org.eclipse.mylar.tasks.ui.views.tasklist.sortIndex";
    private int sortIndex = 2;
    
    private String[] PRIORITY_LEVELS = { "P1", "P2", "P3", "P4", "P5" };
    
    private final class MoveTaskToRootAction extends Action {
		public MoveTaskToRootAction() {
			setText("Move Task to Root");
	        setToolTipText("Move Task to Root");
		}
		@Override
		public void run() {
			ISelection selection = viewer.getSelection();
		    Object obj = ((IStructuredSelection)selection).getFirstElement();
		    if (obj instanceof ITask) {
		    	ITask t = (ITask) obj;
		    	Category cat = t.getCategory();
		    	if (cat != null) {
		    		cat.removeTask(t);
		    		t.setCategory(null);
		    		t.setParent(null);
		    		MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(t);
		    		viewer.refresh();
		    	} else if (t.getParent() != null) {
		    		t.getParent().removeSubTask(t);
		    		t.setParent(null);
		    		MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(t);
		    		viewer.refresh();
		    	}
		    }		    
		}
	}
    
    private final class FilterIncompleteTasksAction extends Action {
    	public FilterIncompleteTasksAction() {
    		setText("Filter Incomplete tasks");
            setToolTipText("Filter Incomplete tasks");
            setImageDescriptor(MylarImages.TASK_INACTIVE);
            setChecked(MylarUiPlugin.getDefault().isFilterInCompleteMode());
    	}
    	
		@Override
		public void run() {
			MylarUiPlugin.getDefault().setFilterInCompleteMode(isChecked());
			if (isChecked()) {
				viewer.addFilter(inCompleteFilter);
				filterCompleteTask.setChecked(false);
				viewer.removeFilter(completeFilter);
			} else {
				viewer.removeFilter(inCompleteFilter);
			}
		    viewer.refresh();
		}
	}

	private final class FilterCompletedTasksAction extends Action {
		public FilterCompletedTasksAction() {
			setText("Filter Complete tasks");
	        setToolTipText("Filter Completed tasks");
	        setImageDescriptor(MylarImages.TASK_ACTIVE);
	        setChecked(MylarUiPlugin.getDefault().isFilterCompleteMode());
		}
		@Override
		public void run() {
			MylarUiPlugin.getDefault().setFilterCompleteMode(isChecked());
			if (isChecked()) {
				viewer.addFilter(completeFilter);
				filterInCompleteTask.setChecked(false);
				viewer.removeFilter(inCompleteFilter);
			} else {
				viewer.removeFilter(completeFilter);        			
			}
		    viewer.refresh();
		}
	}

	private final class OpenTaskEditorAction extends Action {
		@Override
		public void run() {
		    ISelection selection = viewer.getSelection();
		    Object obj = ((IStructuredSelection)selection).getFirstElement();
		    if (obj instanceof ITask) {
		    	((ITask)obj).openTaskInEditor();
		    }
		    viewer.refresh(obj);
		}
	}

	private final class ClearTaskscapeAction extends Action {
		public ClearTaskscapeAction() {
			setText("Erase Taskscape");
	        setToolTipText("Erase Taskscape");
	        setImageDescriptor(MylarImages.ERASE_TASKSCAPE);
		}
		@Override
		public void run() {
		    Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
		    if (selectedObject != null) {
		    	MylarPlugin.getTaskscapeManager().taskDeleted(((Task)selectedObject).getHandle(), ((Task)selectedObject).getPath());
		    	viewer.refresh();
		    }
		}
	}

	private final class RenameAction extends Action {
		public RenameAction() {
			setText("Rename");
	        setToolTipText("Rename");
		}
		@Override
		public void run() {
		    String label = "category";
		    Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
		    if (selectedObject instanceof Task)  label = "task";
		    
		    String newName = getLabelNameFromUser(label);
		    if (selectedObject instanceof Task) {
		        ((Task)selectedObject).setLabel(newName);
		    } else if (selectedObject instanceof Category) {
		        ((Category)selectedObject).setName(newName);
		    }
		    viewer.refresh(selectedObject);
		}
	}

	private final class MarkTaskIncompleteAction extends Action {
		public MarkTaskIncompleteAction() {
			setText("Mark Incomplete");
	        setToolTipText("Mark Incomplete");
		}
		@Override
		public void run() {              
		    Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
		    if (selectedObject instanceof Task){ 
		    	((Task)selectedObject).setCompleted(false);                	
		    }
		    viewer.refresh();
		}
	}

	private final class MarkTaskCompleteAction extends Action {
		public MarkTaskCompleteAction() {
			setText("Mark Complete");
	        setToolTipText("Mark Complete");
		}
		@Override
		public void run() {              
		    Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
		    if (selectedObject instanceof Task){ 
		    	((Task)selectedObject).setCompleted(true);
		    	viewer.refresh(selectedObject);
		    }
		}
	}

	private final class DeleteAction extends Action {
		public DeleteAction() {
			setText("Delete");
	        setToolTipText("Delete");
	        setImageDescriptor(MylarImages.REMOVE);
		}
		@Override
		public void run() {              
		    boolean deleteConfirmed = MessageDialog.openQuestion(
		            Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
		            "Confirm delete", 
		            "Delete selected item?");
		    if (!deleteConfirmed) { 
		        return;
		    } else {
		        Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
		        if (selectedObject instanceof Task) {
					MylarTasksPlugin.getTaskListManager().deleteTask((Task)selectedObject);
					MylarPlugin.getTaskscapeManager().taskDeleted(((Task)selectedObject).getHandle(), ((Task)selectedObject).getPath());
					IWorkbenchPage page = MylarTasksPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
					
					// if we couldn't get the page, get out of here
					if (page == null)
						return;
					try{
		                closeTaskEditors((ITask)selectedObject, page);
		            }catch(Exception e){
		            	MylarPlugin.log(e, " deletion failed");
		            }
		        } else if (selectedObject instanceof Category) {
		        	Category cat = (Category) selectedObject;
		        	for (ITask task : cat.getTasks()) {
		        		MylarPlugin.getTaskscapeManager().taskDeleted(task.getHandle(), task.getPath());
		        		IWorkbenchPage page = MylarTasksPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();    					
						if (page == null)
							return;
						try{
		                    closeTaskEditors(task, page);
		                }catch(Exception e){
		                	MylarPlugin.log(e, " deletion failed");
		                }
		        	}
		        	MylarTasksPlugin.getTaskListManager().deleteCategory((Category)selectedObject);
		        }
		    }
		    viewer.refresh();
		}
	}

	private final class CreateBugzillaTaskAction extends Action {
		public CreateBugzillaTaskAction() {
			setText("Add bugzilla report");
	        setToolTipText("Add bugzilla report");
	        setImageDescriptor(MylarImages.TASK_BUGZILLA_NEW);
		}
		@Override
		public void run() {
		    String bugIdString = getBugIdFromUser();
		    int bugId = -1;
		    try {
		    	if (bugIdString != null) {
		    		bugId = Integer.parseInt(bugIdString);
		    	} else {
		    		return;
		    	}
		    } catch (NumberFormatException nfe) {
		        showMessage("Please enter a valid report number");
		        return;
		    }
			
			// Check the existing tasks to see if the id is used already.
			// This is to prevent the creation of mutliple Bugzilla tasks
			//   for the same Bugzilla report.
			boolean doesIdExistAlready = false;
			doesIdExistAlready = lookForId("Bugzilla-" + bugId);				
			if (doesIdExistAlready) {
		        showMessage("A Bugzilla task with ID Bugzilla-" + bugId + " already exists.");
		        return;
			}
		
		    ITask newTask = new BugzillaTask("Bugzilla-"+bugId, "<bugzilla info>");				
		    Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
		    if (selectedObject instanceof Category){
		        ((Category)selectedObject).addTask(newTask);
		    } else { 
		        MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(newTask);
		    }
		    viewer.refresh();
		}
	}

	private final class RefreshAction extends Action {
		public RefreshAction() {
			setText("Refresh all Bugzilla reports");
	    	setToolTipText("Refresh all Bugzilla reports"); 
	    	setImageDescriptor(MylarImages.REFRESH);
		}
		
		@Override			
		public void run() {
			// TODO background?
			// perform the update in an operation so that we get a progress monitor 
		    // update the structure bridge cache with the reference provider cached bugs
		    WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
		    	protected void execute(IProgressMonitor monitor) throws CoreException {
									
					List<ITask> tasks = MylarTasksPlugin.getTaskListManager().getTaskList().getRootTasks();
					
		            for (ITask task : tasks) {
						if (task instanceof BugzillaTask) {
							((BugzillaTask)task).refresh();
						}							
					}
		            for (Category cat : MylarTasksPlugin.getTaskListManager().getTaskList().getCategories()) {
		            	for (ITask task : cat.getTasks()) {
		            		if (task instanceof BugzillaTask) {
								((BugzillaTask)task).refresh();
							}	
		            	}
		            	refreshChildren(cat.getTasks());
					}		                
					
		            // clear the caches
		    		Set<String> cachedHandles = new HashSet<String>();
		    		cachedHandles.addAll(MylarTasksPlugin.getDefault().getStructureBridge().getCachedHandles());
		            cachedHandles.addAll(MylarTasksPlugin.getReferenceProvider().getCachedHandles());
		            MylarTasksPlugin.getDefault().getStructureBridge().clearCache();
		        	MylarTasksPlugin.getReferenceProvider().clearCachedReports();
		
		        	BugzillaStructureBridge bridge = MylarTasksPlugin.getDefault().getStructureBridge();
		    		monitor.beginTask("Downloading Bugs" , cachedHandles.size());
		        	for(String key: cachedHandles){
		                try {
		                	String [] parts = key.split(";");
		                    final int id = Integer.parseInt(parts[1]);
		                	BugReport bug = BugzillaRepository.getInstance().getCurrentBug(id);
		                	if(bug != null)
		                		bridge.cache(key, bug);
		                }catch(Exception e){}
		                
		                monitor.worked(1);
		        	}
		        	monitor.done();
		        	viewer.refresh();
		    	}
		    };
		    	
		 	// Use the progess service to execute the runnable
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			try {
				service.run(true, false, op);
			} catch (InvocationTargetException e) {
				// Operation was canceled
			} catch (InterruptedException e) {
				// Handle the wrapped exception
			}
		}
	}

	private final class CreateTaskAction extends Action {
		public CreateTaskAction() {
			setText("Create task");
	        setToolTipText("Create task");
	        setImageDescriptor(MylarImages.TASK_NEW);
		}
		
        @Override
        public void run() {
            String label = getLabelNameFromUser("task");
            if(label == null) return;
            Task newTask = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), label);
            
            Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
            if (selectedObject instanceof Category){
            	newTask.setCategory((Category)selectedObject);
                ((Category)selectedObject).addTask(newTask);
            } 
//            else if (selectedObject instanceof Task) {
//            	ITask t = (ITask) selectedObject;
//            	newTask.setParent(t);
//            	t.addSubTask(newTask);
//            }
            else {            	
                MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(newTask);                
            }  
            MylarUiPlugin.getDefault().setHighlighterMapping(
                    newTask.getHandle(), 
                    MylarUiPlugin.getDefault().getDefaultHighlighter().getName());
            viewer.refresh();
        }
    }
    
    private final class CreateCategoryAction extends Action {        
        public CreateCategoryAction() {
        	setText("Create category");
            setToolTipText("Create category");
            setImageDescriptor(MylarImages.CATEGORY_NEW);
        }
        
        @Override
        public void run() {
            String label = getLabelNameFromUser("Category");
            if(label == null) return;
            Category cat = new Category(label);
            MylarTasksPlugin.getTaskListManager().getTaskList().addCategory(cat);
            viewer.refresh();
        }
    }
    
    private final class PriorityDropDownAction extends Action implements IMenuCreator {
    	private Menu dropDownMenu = null;
    	
		public PriorityDropDownAction() {
			setText("Display Priorities");
			setToolTipText("Show Tasks with Priority Levels");
			setImageDescriptor(MylarImages.FILTER_DECLARATIONS);
			setMenuCreator(this);			
		}
    	
		public void dispose() {			
			if (dropDownMenu != null) {
				dropDownMenu.dispose();
				dropDownMenu = null;
			}
		}

		public Menu getMenu(Control parent) {			
			if (dropDownMenu != null) {
				dropDownMenu.dispose();
			}
			dropDownMenu = new Menu(parent);
			addActionsToMenu();
			return dropDownMenu;
		}

		public Menu getMenu(Menu parent) {
			if (dropDownMenu != null) {
				dropDownMenu.dispose();
			}
			dropDownMenu = new Menu(parent);
			addActionsToMenu();
			return dropDownMenu;
		}     
		
		public void addActionsToMenu() {
			Action P1 = new Action(PRIORITY_LEVELS[0], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() {
	    			MylarUiPlugin.getDefault().setP1FilterMode(!isChecked());
	    			if (isChecked()) {
	    				priorityFilter.displayPriority(PRIORITY_LEVELS[0]);
	    			} else {
	    				priorityFilter.hidePriority(PRIORITY_LEVELS[0]);
	    			}
	    			viewer.refresh();
				}
			};  
			P1.setEnabled(true);
			P1.setChecked(!MylarUiPlugin.getDefault().isP1FilterMode());
			P1.setToolTipText(PRIORITY_LEVELS[0]);
			ActionContributionItem item= new ActionContributionItem(P1);
			item.fill(dropDownMenu, -1);
			
			Action P2 = new Action(PRIORITY_LEVELS[1], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() {
	    			MylarUiPlugin.getDefault().setP2FilterMode(!isChecked());
	    			if (isChecked()) {
	    				priorityFilter.displayPriority(PRIORITY_LEVELS[1]);
	    			} else {
	    				priorityFilter.hidePriority(PRIORITY_LEVELS[1]);
	    			}
	    			viewer.refresh();
				}
			};  
			P2.setEnabled(true);
			P2.setChecked(!MylarUiPlugin.getDefault().isP2FilterMode());
			P2.setToolTipText(PRIORITY_LEVELS[1]);
			item= new ActionContributionItem(P2);
			item.fill(dropDownMenu, -1);
			
			Action P3 = new Action(PRIORITY_LEVELS[2], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() { 
	    			MylarUiPlugin.getDefault().setP3FilterMode(!isChecked());
	    			if (isChecked()) {
	    				priorityFilter.displayPriority(PRIORITY_LEVELS[2]);
	    			} else {
	    				priorityFilter.hidePriority(PRIORITY_LEVELS[2]);
	    			}
	    			viewer.refresh();
				}
			};
			P3.setEnabled(true);
			P3.setChecked(!MylarUiPlugin.getDefault().isP3FilterMode());
			P3.setToolTipText(PRIORITY_LEVELS[2]);
			item= new ActionContributionItem(P3);
			item.fill(dropDownMenu, -1);
			
			Action P4 = new Action(PRIORITY_LEVELS[3], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() {
	    			MylarUiPlugin.getDefault().setP4FilterMode(!isChecked());
	    			if (isChecked()) {
	    				priorityFilter.displayPriority(PRIORITY_LEVELS[3]);
	    			} else {
	    				priorityFilter.hidePriority(PRIORITY_LEVELS[3]);
	    			}
	    			viewer.refresh();
				}
			};
			P4.setEnabled(true);
			P4.setChecked(!MylarUiPlugin.getDefault().isP4FilterMode());
			P4.setToolTipText(PRIORITY_LEVELS[3]);
			item= new ActionContributionItem(P4);
			item.fill(dropDownMenu, -1);
						
			Action P5 = new Action(PRIORITY_LEVELS[4], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() { 
	    			MylarUiPlugin.getDefault().setP5FilterMode(!isChecked());
	    			if (isChecked()) {
	    				priorityFilter.displayPriority(PRIORITY_LEVELS[4]);
	    			} else {
	    				priorityFilter.hidePriority(PRIORITY_LEVELS[4]);
	    			}	
	    			viewer.refresh();
	    		}
			};  
			P5.setEnabled(true);
			P5.setChecked(!MylarUiPlugin.getDefault().isP5FilterMode());
			P5.setToolTipText(PRIORITY_LEVELS[4]);
			item= new ActionContributionItem(P5);
			item.fill(dropDownMenu, -1);			
		}
		public void run() {			
		}
    }
    
    private ViewerFilter completeFilter = new ViewerFilter(){
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof ITask) {
				return !((ITask)element).isCompleted();
			} else if (element instanceof Category){ 
				return true;
			} else {
				return false;
			}
		}    	
    };
    
    private ViewerFilter inCompleteFilter = new ViewerFilter(){
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof ITask) {
				return ((ITask)element).isCompleted();
			} else if (element instanceof Category){
				return true;
			} else {
				return false;
			}
		}    			
    };
    
    public class PriorityFilter extends ViewerFilter {
    	// list of priorities that will be shown in the tasklistview
    	private List<String> priorities = new ArrayList<String>();
    	
    	public PriorityFilter() {
    		// if filter is off, then add to list
    		if (!MylarUiPlugin.getDefault().isP1FilterMode()) {
    			displayPriority("P1");
    		}
    		if (!MylarUiPlugin.getDefault().isP2FilterMode()) {
    			displayPriority("P2");
    		}
    		if (!MylarUiPlugin.getDefault().isP3FilterMode()) {
    			displayPriority("P3");
    		}
    		if (!MylarUiPlugin.getDefault().isP4FilterMode()) {
    			displayPriority("P4");
    		}
    		if (!MylarUiPlugin.getDefault().isP5FilterMode()) {
    			displayPriority("P5");
    		}
    	}
    	
    	public void displayPriority(String p) {
    		if (!priorities.contains(p)) {
    			priorities.add(p);
    		}    		
    	}
    	
    	public void hidePriority(String p) {
    		priorities.remove(p);
    	}
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof ITask) {
				ITask task = (ITask) element;
				if (priorities.size() == PRIORITY_LEVELS.length) {
					return true;
				} else {
					for (String filter : priorities) {
						if (task.getPriority().equals(filter)) {
							return true;
						}
					}
					return false;
				}								
			} else if (element instanceof Category) {
				return true;
			} else {
				return false;
			}			
		}
    	
    };
    
    class TaskListContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        	// don't care if the input changes
        }
        public void dispose() {
        	// don't care if we are disposed
        }
        public Object[] getElements(Object parent) {
            if (parent.equals(getViewSite())) {
            	return MylarTasksPlugin.getTaskListManager().getTaskList().getRoots().toArray();            	          
            }
            return getChildren(parent);
        }
        public Object getParent(Object child) {
            if (child instanceof Task) {
            	if (((Task)child).getParent() != null) {
            		return ((Task)child).getParent();
            	} else {
            		return ((Task)child).getCategory();
            	}
                
            }
            return null;
        }
        public Object [] getChildren(Object parent) {
        	if (parent instanceof Category) {
        		return ((Category)parent).getTasks().toArray();
        	} else if (parent instanceof Task) {
        		return ((Task)parent).getChildren().toArray();
        	}
        	return new Object[0];
        }
        public boolean hasChildren(Object parent) {  
            if (parent instanceof Category) {
            	Category cat = (Category)parent;
                return cat.getTasks() != null && cat.getTasks().size() > 0;
            }  else if (parent instanceof Task) {
            	Task t = (Task) parent;
            	return t.getChildren() != null && t.getChildren().size() > 0;
            }
            return false;
        }
    }

    public TaskListView() { 
    	INSTANCE = this;
    }

    class TaskListCellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {
            int columnIndex = Arrays.asList(columnNames).indexOf(property);
            if (element instanceof ITask) {
            	ITask task = (ITask) element;
                switch (columnIndex) {
                case 0: return true;
                case 1: return false;
                case 2: return !(task instanceof BugzillaTask);
                case 3: return !(task instanceof BugzillaTask);
                case 4: return false;
                }
            } else if (element instanceof Category) {
                switch (columnIndex) {
                case 0:
                case 1: 
                case 2:
                	return false;
                case 3: return true;
                case 4: return false;
                } 
            }            
            return false;
        }

        public Object getValue(Object element, String property) {
            int columnIndex = Arrays.asList(columnNames).indexOf(property);
            if (element instanceof ITask) {
				ITask task = (ITask) element;
				switch (columnIndex) {
				case 0:
					return new Boolean(task.isCompleted());
				case 1:
					return "";
				case 2:
					String priorityString = task.getPriority().substring(1);
					return new Integer(priorityString);
				case 3:
					return task.getLabel();
				case 4:
					return task.getHandle();
				}
			} else if (element instanceof Category) {
				Category cat = (Category) element;
				switch (columnIndex) {
				case 0:
					return new Boolean(false);
				case 1:
					return "";
				case 2:
					return "";
				case 3:
					return cat.getName();
				case 4:
					return "";
				}
			}
            return "";
        }

		public void modify(Object element, String property, Object value) {
			int columnIndex = -1;
			try {
				columnIndex = Arrays.asList(columnNames).indexOf(property);
				if (((TreeItem) element).getData() instanceof ITask) {

					ITask task = (ITask) ((TreeItem) element).getData();
					switch (columnIndex) {
					case 0:
						if (task.isActive()) {
							MylarTasksPlugin.getTaskListManager()
									.deactivateTask(task);
						} else {
							MylarTasksPlugin.getTaskListManager().activateTask(
									task);
						}
						viewer.setSelection(null);
						break;
					case 1:
						break;
					case 2:
						Integer intVal = (Integer) value;
						task.setPriority("P" + (intVal + 1));
						viewer.setSelection(null);
						break;
					case 3:
						task.setLabel(((String) value).trim());
						MylarTasksPlugin.getTaskListManager()
								.taskPropertyChanged(task, columnNames[3]);
						viewer.setSelection(null);
						break;
					case 4:
						break;
					}
				} else if (((TreeItem) element).getData() instanceof Category) {
					Category cat = (Category)((TreeItem) element).getData();
					switch (columnIndex) {
					case 0:						
						viewer.setSelection(null);
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						cat.setName(((String) value).trim());
						viewer.setSelection(null);
						break;
					case 4:
						break;
					}
				}
				viewer.refresh();
			} catch (Exception e) {
				MylarPlugin.log(e, e.getMessage());
			}
		}                
    }
    
    private class TaskListTableSorter extends ViewerSorter {

        private String column;

        public TaskListTableSorter(String column) {
            super();
            this.column = column;
        }

        /**
		 * compare - invoked when column is selected calls the actual comparison
		 * method for particular criteria
		 */
        @Override
        public int compare(Viewer compareViewer, Object o1, Object o2) {
        	if (o1 instanceof Category) {
        		if (o2 instanceof Category) {
        			return 0;
        		} else {
        			return -1;
        		}
        	} else {
        		if (o2 instanceof Category) {
        			return -1;
        		} else {
        			ITask task1 = (ITask) o1;
                    ITask task2 = (ITask) o2;
                    
                    if (task1.isCompleted()) return 1;
                    if (task2.isCompleted()) return -1;
                    if (column == columnNames[1]) {
                        if (task1 instanceof BugzillaTask && !(task2 instanceof BugzillaTask)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if (column == columnNames[2]) {
                        return task1.getPriority().compareTo(task2.getPriority());
                    } else if (column == columnNames[3]) {
                        return task1.getLabel().compareTo(task2.getLabel());
                    } else if (column == columnNames[4]){
                    	return task1.getPath().compareTo(task2.getPath());
                    } else {
                    	return 0;
                    }
        		}
        	}            
        }
    }
    
    @Override
    public void init(IViewSite site,IMemento memento) throws PartInitException {
    	init(site);
    	this.taskListMemento = memento;
    }
    
    @Override
    public void saveState(IMemento memento) {
		IMemento colMemento = memento.createChild(columnWidthIdentifier);

		for (int i = 0; i < columnWidths.length; i++) {
			IMemento m = colMemento.createChild("col"+i);
			m.putInteger("width", columnWidths[i]);
		}
		
		IMemento sorter = memento.createChild(tableSortIdentifier);
		IMemento m = sorter.createChild("sorter");
		m.putInteger("sortIndex", sortIndex);
	}
    
    private void restoreState() {
        if (taskListMemento == null)
                return;
        IMemento taskListWidth = taskListMemento.getChild(columnWidthIdentifier);
        if (taskListWidth != null) {
        	for (int i = 0; i < columnWidths.length; i++) {
        		IMemento m = taskListWidth.getChild("col"+i);
        		if (m != null) {
        			int width = m.getInteger("width");
        			columnWidths[i] = width;
        			columns[i].setWidth(width);
        		}
        	}        	
        }
        IMemento sorterMemento = taskListMemento.getChild(tableSortIdentifier);
        if (sorterMemento != null) {
        	IMemento m = sorterMemento.getChild("sorter");
        	if (m != null) {
        		sortIndex = m.getInteger("sortIndex");
        	} else {
        		sortIndex = 2;
        	}
        } else {
        	sortIndex = 2; // default priority
        }
        viewer.setSorter(new TaskListTableSorter(columnNames[sortIndex]));
        viewer.addFilter(priorityFilter);
        if(MylarUiPlugin.getDefault().isFilterInCompleteMode()) viewer.addFilter(inCompleteFilter);
        if(MylarUiPlugin.getDefault().isFilterCompleteMode()) viewer.addFilter(completeFilter);
        viewer.refresh();
    }
        
    /** 
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {    	    	
        viewer = new TreeViewer(parent, SWT.VERTICAL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        viewer.getTree().setHeaderVisible(true);
        viewer.getTree().setLinesVisible(true);
        viewer.setColumnProperties(columnNames);
        viewer.setUseHashlookup(true);    
                
        columns = new TreeColumn[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            columns[i] = new TreeColumn(viewer.getTree(), 0); // SWT.LEFT
            columns[i].setText(columnNames[i]);
            columns[i].setWidth(columnWidths[i]);
            final int index = i;
            columns[i].addSelectionListener(new SelectionAdapter() {
            	
            	@Override
                public void widgetSelected(SelectionEvent e) {
            		sortIndex = index;
                    viewer.setSorter(new TaskListTableSorter(columnNames[sortIndex]));
                }
            });
            columns[i].addControlListener(new ControlListener () {
            	public void controlResized(ControlEvent e) {
            		for (int j = 0; j < columnWidths.length; j++) {
            			if (columns[j].equals(e.getSource())) {
            				columnWidths[j] = columns[j].getWidth();
            			}
            		}
            	}
				public void controlMoved(ControlEvent e) {	
					// don't care if the control is moved
				}
            });

        }
                
        CellEditor[] editors = new CellEditor[columnNames.length];
        TextCellEditor textEditor = new TextCellEditor(viewer.getTree());
        ((Text) textEditor.getControl()).setOrientation(SWT.LEFT_TO_RIGHT);
        editors[0] = new CheckboxCellEditor();
        editors[1] = textEditor;
        editors[2] = new ComboBoxCellEditor(viewer.getTree(), PRIORITY_LEVELS, SWT.READ_ONLY);
        editors[3] = textEditor;
        viewer.setCellEditors(editors);   
        viewer.setCellModifier(new TaskListCellModifier());
        viewer.setSorter(new TaskListTableSorter(columnNames[sortIndex]));
        
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new TaskListContentProvider());
        TaskListLabelProvider lp = new TaskListLabelProvider();
        lp.setBackgroundColor(parent.getBackground());
        viewer.setLabelProvider(lp);
        viewer.setInput(getViewSite());
        
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();       
        ToolTipHandler toolTipHandler = new ToolTipHandler(viewer.getControl().getShell());
        toolTipHandler.activateHoverHelp(viewer.getControl());
        
        initDragAndDrop(parent);
        expandToActiveTasks();
        restoreState();
   }

    @MylarWebRef(name="Drag and drop article", url="http://www.eclipse.org/articles/Article-Workbench-DND/drag_drop.html")
    private void initDragAndDrop(Composite parent) {
        Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

        viewer.addDragSupport(DND.DROP_MOVE, types, new DragSourceListener() {

            public void dragStart(DragSourceEvent event) {
                if (((StructuredSelection) viewer.getSelection()).isEmpty()) {
                    event.doit = false;
                }
            }

            public void dragSetData(DragSourceEvent event) {
                StructuredSelection selection = (StructuredSelection) viewer.getSelection();
                if (!selection.isEmpty()) {
                    event.data = "" + ((ITask) selection.getFirstElement()).getHandle();
                } else {
                    event.data = "null";
                }
            }

            public void dragFinished(DragSourceEvent event) {
            	// don't care if the drag is done
            }
        });

        viewer.addDropSupport(DND.DROP_MOVE, types, new ViewerDropAdapter(viewer) {
            {
                setFeedbackEnabled(false);
            }

            @Override
            public boolean performDrop(Object data) {
                Object selectedObject = ((IStructuredSelection) ((TreeViewer) getViewer())
                        .getSelection()).getFirstElement();
                if (selectedObject instanceof ITask) {
                    ITask source = (ITask) selectedObject;
                    if (source.getCategory() != null) {
                		source.getCategory().removeTask(source);
                	} else if (source.getParent() != null) {
                		source.getParent().removeSubTask(source);
                	} else {
                		MylarTasksPlugin.getTaskListManager().getTaskList().getRootTasks().remove(source);
                	}
                    
                    if (getCurrentTarget() instanceof Category) {
                    	((Category) getCurrentTarget()).addTask(source);
                    	source.setCategory((Category)getCurrentTarget());
                    } else if (getCurrentTarget() instanceof ITask) {
                    	ITask target = (ITask) getCurrentTarget();
                    	source.setCategory(null);
                    	target.addSubTask(source);                    	
                    	source.setParent(target);
                    }           
                    viewer.setSelection(null);
                    viewer.refresh();
                    return true;
                }
                return false;
            }

            @Override
            public boolean validateDrop(Object targetObject, int operation,
                    TransferData transferType) {
                Object selectedObject = ((IStructuredSelection) ((TreeViewer) getViewer())
                        .getSelection()).getFirstElement();
                if (selectedObject instanceof ITask) {
                    if (getCurrentTarget() != null && 
                    		(getCurrentTarget() instanceof ITask || getCurrentTarget() instanceof Category)) {
                    	return true;
                    } else {
                    	return false;
                    }
                }
                return TextTransfer.getInstance().isSupportedType(transferType);
            }

        });
    }
    
    private void expandToActiveTasks() {
    	List<ITask> activeTasks = MylarTasksPlugin.getTaskListManager().getTaskList().getActiveTasks();
    	for (ITask t : activeTasks) {
    		viewer.expandToLevel(t, 0);
    	}
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                TaskListView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
    	drillDownAdapter.addNavigationActions(manager);
//        manager.add(createCategory);
//        manager.add(new Separator());
//        manager.add(createTask);
    }

    void fillContextMenu(IMenuManager manager) {
        manager.add(completeTask);
        manager.add(incompleteTask);
//        manager.add(new Separator());
        manager.add(createTask);
        manager.add(createBugzillaTask);
        manager.add(rename);
        manager.add(delete);
        manager.add(clearSelectedTaskscapeAction);
        manager.add(moveTaskToRoot);
        manager.add(new Separator());
        MenuManager subMenuManager = new MenuManager("choose highlighter");
        for (Iterator<Highlighter> it = MylarUiPlugin.getDefault().getHighlighters().iterator(); it.hasNext();) {
            final Highlighter highlighter = it.next();
            final Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
            if (selectedObject instanceof Task){
                Action action = new Action() {
                	
                	@Override
                    public void run() { 
                        Task task = (Task)selectedObject;
                        MylarUiPlugin.getDefault().setHighlighterMapping(task.getHandle(), highlighter.getName());
                        TaskListView.this.viewer.refresh();
                        MylarPlugin.getTaskscapeManager().notifyPostPresentationSettingsChange(ITaskscapeListener.UpdateKind.HIGHLIGHTER);
//                        taskscapeComponent.getTableViewer().refresh();
                    }
                };
                if (highlighter.isGradient()) {
                    action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getBase(), highlighter.getLandmarkColor()));
                } else {
                    action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getLandmarkColor(), highlighter.getLandmarkColor()));
                }
                action.setText(highlighter.toString());
                subMenuManager.add(action);
            } else {
//                showMessage("Select task before choosing highlighter");
            }
        }
        manager.add(subMenuManager);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(createCategory);
        manager.add(createTask);
//        manager.add(new Separator());
        manager.add(createBugzillaTask);
        manager.add(refresh);
//        manager.add(new Separator());
//        manager.add(toggleFilteringAction);
        //manager.add(toggleIntersectionModeAction);
        manager.add(new Separator());
        manager.add(filterCompleteTask);
        manager.add(filterInCompleteTask);
        manager.add(filterOnPriority);
        
    }

    /**
     * @see org.eclipse.pde.internal.ui.view.HistoryDropDownAction
     *
     */
    private void makeActions() {
    	refresh = new RefreshAction();      	               
        createTask = new CreateTaskAction();        
        createCategory = new CreateCategoryAction();                
        createBugzillaTask = new CreateBugzillaTaskAction();                
        delete = new DeleteAction();
        completeTask = new MarkTaskCompleteAction();
        incompleteTask = new MarkTaskIncompleteAction();        
        rename = new RenameAction();        
        clearSelectedTaskscapeAction = new ClearTaskscapeAction();
        moveTaskToRoot = new MoveTaskToRootAction();
        doubleClickAction = new OpenTaskEditorAction();            
        filterCompleteTask = new FilterCompletedTasksAction();        
        filterInCompleteTask = new FilterIncompleteTasksAction();                        
        filterOnPriority = new PriorityDropDownAction();             
    }

    /**
	 * Recursive function that checks for the occurrence of a certain task id.
	 * All children of the supplied node will be checked.
	 * 
	 * @param task
	 *            The <code>ITask</code> object that is to be searched.
	 * @param taskId
	 *            The id that is being searched for.
	 * @return <code>true</code> if the id was found in the node or any of its
	 *         children
	 */
    protected boolean lookForId(String taskId) {
    	for (ITask task : MylarTasksPlugin.getTaskListManager().getTaskList().getRootTasks()) {
    		if (task.getHandle().equals(taskId)) {
    			return true;
    		}
    	}
    	for (Category cat : MylarTasksPlugin.getTaskListManager().getTaskList().getCategories()) {
    		for (ITask task : cat.getTasks()) {
        		if (task.getHandle().equals(taskId)) {
        			return true;
        		}
        	}
    	}
		return false;
	}
	
	protected void closeTaskEditors(ITask task, IWorkbenchPage page) throws LoginException, IOException{
		IEditorInput input = null;		
		if (task instanceof BugzillaTask) {
			input = new BugzillaTaskEditorInput((BugzillaTask)task);
		} else if (task instanceof Task) {
			input = new TaskEditorInput((Task) task);
		}
		IEditorPart editor = page.findEditor(input);

		if (editor != null) {
			page.closeEditor(editor, false);
		}		
	}
	
	protected void refreshChildren(List<ITask> children) {
		if (children != null) {
            for (ITask child : children) {
				if (child instanceof BugzillaTask) {
					((BugzillaTask)child).refresh();
				}
			}
		}
	}

	private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }
    private void showMessage(String message) {
        MessageDialog.openInformation(
            viewer.getControl().getShell(),
            "Tasklist Message",
            message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
        //TODO: foo
    }

    private String getBugIdFromUser() {
        InputDialog dialog = new InputDialog(
            Workbench.getInstance().getActiveWorkbenchWindow().getShell(), 
            "Enter Bugzilla ID", 
            "Enter the Bugzilla ID: ", 
            "", 
            null);
        int dialogResult = dialog.open();
        if (dialogResult == Window.OK) { 
            return dialog.getValue();
        } else {
            return null;
        }
    }
    
    private String getLabelNameFromUser(String kind) {
        
        InputDialog dialog = new InputDialog(
            Workbench.getInstance().getActiveWorkbenchWindow().getShell(), 
            "Enter name", 
            "Enter a name for the " + kind + ": ", 
            "", 
            null);
        int dialogResult = dialog.open();
        if (dialogResult == Window.OK) { 
            return dialog.getValue();
        } else {
            return null;
        }
    }
    
    public void notifyTaskDataChanged(ITask task) {
        if (viewer.getTree() != null && !viewer.getTree().isDisposed()) { 
        	viewer.refresh(task);
        }
    }
    
    public static TaskListView getDefault() {
    	return INSTANCE;
    }
    
    public TreeViewer getViewer() {
    	return viewer;
    }
    
    public ViewerFilter getCompleteFilter() {
    	return completeFilter;
    }
    
    public ViewerFilter getInCompleteFilter() {
    	return inCompleteFilter;
    }
    
    public PriorityFilter getPriorityFilter() {
    	return priorityFilter;
    }
}

//TextTransfer textTransfer = TextTransfer.getInstance();
//DropTarget target = new DropTarget(viewer.getTree(), DND.DROP_MOVE);
//target.setTransfer(new Transfer[] { textTransfer });
//target.addDropListener(new TaskListDropTargetListener(parent, null, textTransfer, true));
//
//DragSource source = new DragSource(viewer.getTree(), DND.DROP_MOVE);
//source.setTransfer(types); 

//source.addDragListener(new DragSourceListener() {
//public void dragStart(DragSourceEvent event) {
//  if (((StructuredSelection)viewer.getSelection()).isEmpty()) { 
//      event.doit = false; 
//  }
//}
//public void dragSetData(DragSourceEvent event) {
//  StructuredSelection selection = (StructuredSelection) viewer.getSelection();
//  if (!selection.isEmpty()) { 
//      event.data = "" + ((ITask)selection.getFirstElement()).getId();
//  } else {
//      event.data = "null";
//  }
//}
//
//public void dragFinished(DragSourceEvent event) { }
//});


//	public boolean getServerStatus() {
//		return serverStatus;
//	}
//	
//	/**
//	 * Sets whether or not we could connect to the Bugzilla server. If
//	 * necessary, the corresponding label in the view is updated.
//	 * 
//	 * @param canRead
//	 *            <code>true</code> if the Bugzilla server could be connected
//	 *            to
//	 */
//	public void setServerStatus(boolean canRead) {
//		if (serverStatus != canRead) {
//			serverStatus = canRead;
//			updateServerStatusLabel();
//		}
//	}
//	
//	private void updateServerStatusLabel() {
//		if (serverStatusLabel.isDisposed()) {
//			return;
//		}
//		if (serverStatus) {
//			serverStatusLabel.setText(CAN_READ_LABEL);
//		}
//		else {
//			serverStatusLabel.setText(CANNOT_READ_LABEL);
//		}
//	}
//	
//	private class ServerPingJob extends Job {
//		private boolean shouldCheckAgain = true;
//		private int counter = 0;
//		
//		public ServerPingJob(String name) {
//			super(name);
//		}
//		
//		public void stopPinging() {
//			shouldCheckAgain = false;
//		}
//
//		protected IStatus run(IProgressMonitor monitor) {
//			while (shouldCheckAgain) {
//				try {
//					final boolean canReadFromServer = TaskListView.checkServer();
//					Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
//						public void run() {
//							setServerStatus(canReadFromServer);
//						}
//					});
//					Thread.sleep(10000/*MylarPreferencePage.getServerPing()*5000*/);
//				} catch (InterruptedException e) {
//					break;
//				}
//			}
//			return new Status(IStatus.OK, MylarPlugin.IDENTIFIER, IStatus.OK, "", null);
//		}
//	}
//	
//	/**
//	 * @return <code>true</code> if we could connect to the Bugzilla server
//	 */
//	public static boolean checkServer() {
//		boolean canRead = true;
//		BufferedReader in = null;
//		
//		// Call this function to intialize the Bugzilla url that the repository
//		// is using.
//		BugzillaRepository.getInstance();
//
//		try {
//			// connect to the bugzilla server
//			SSLContext ctx = SSLContext.getInstance("TLS");
//			javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[]{new TrustAll()};
//			ctx.init(null, tm, null);
//			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
//			String urlText = "";
//			
//			// use the usename and password to get into bugzilla if we have it
//			if(BugzillaPreferences.getUserName() != null && !BugzillaPreferences.getUserName().equals("") && BugzillaPreferences.getPassword() != null && !BugzillaPreferences.getPassword().equals(""))
//			{
//				/*
//				 * The UnsupportedEncodingException exception for
//				 * URLEncoder.encode() should not be thrown, since every
//				 * implementation of the Java platform is required to support
//				 * the standard charset "UTF-8"
//				 */
//				try {
//					urlText += "?GoAheadAndLogIn=1&Bugzilla_login=" + URLEncoder.encode(BugzillaPreferences.getUserName(), "UTF-8") + "&Bugzilla_password=" + URLEncoder.encode(BugzillaPreferences.getPassword(), "UTF-8");
//				} catch (UnsupportedEncodingException e) { }
//			}
//			
//			URL url = new URL(BugzillaRepository.getURL() + "/enter_bug.cgi" + urlText);
//			
//			// create a new input stream for getting the bug
//			in = new BufferedReader(new InputStreamReader(url.openStream()));
//		}
//		catch (Exception e) {
//			// If there was an IOException, then there was a problem connecting.
//			// If there was some other exception, then it was a problem not
//			// related to the server.
//			if (e instanceof IOException) {
//				canRead = false;
//			}
//		}
//
//		// Close the BufferedReader if we opened one.
//		try {
//			if (in != null)
//				in.close();
//		} catch(IOException e) {}
//		
//		return canRead;
//	}
//
//	public void dispose() {
//		if (serverPingJob != null) {
//			serverPingJob.stopPinging();
//		}
//		super.dispose();
//	}

//      source.addDragListener(new DragSourceListener() {
//
//            public void dragStart(DragSourceEvent event) {
//                if (((StructuredSelection) viewer.getSelection()).getFirstElement() == null) {
//                    event.doit = false;
//                }
//            }
//
//            public void dragSetData(DragSourceEvent event) {
//                StructuredSelection selection = (StructuredSelection)viewer.getSelection();
//                ITask task = (ITask) selection.getFirstElement();
//                if (task != null) {
//                    event.data = "" + task.getId();
//                } else {
//                    event.data = " ";
//                }
//            }
//
//            public void dragFinished(DragSourceEvent event) {
//                StructuredSelection selection = (StructuredSelection)viewer.getSelection();
//                if (selection.isEmpty()) {
//                    return;
//                } else {
//                    ITask task = (ITask) selection.getFirstElement();
//                    
//                    System.err.println(">>> got task: " + task + ">> " + );
//
//                }
//            }
//
//        });
