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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.dt.MylarWebRef;
import org.eclipse.mylar.tasklist.ICategory;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskFilter;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.ITaskListDynamicSubMenuContributor;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.internal.TaskCompleteFilter;
import org.eclipse.mylar.tasklist.internal.TaskListPatternFilter;
import org.eclipse.mylar.tasklist.internal.TaskPriorityFilter;
import org.eclipse.mylar.tasklist.ui.TaskEditorInput;
import org.eclipse.mylar.tasklist.ui.actions.AutoCloseAction;
import org.eclipse.mylar.tasklist.ui.actions.CollapseAllAction;
import org.eclipse.mylar.tasklist.ui.actions.CopyDescriptionAction;
import org.eclipse.mylar.tasklist.ui.actions.CreateCategoryAction;
import org.eclipse.mylar.tasklist.ui.actions.CreateTaskAction;
import org.eclipse.mylar.tasklist.ui.actions.DeleteAction;
import org.eclipse.mylar.tasklist.ui.actions.FilterCompletedTasksAction;
import org.eclipse.mylar.tasklist.ui.actions.GoIntoAction;
import org.eclipse.mylar.tasklist.ui.actions.GoUpAction;
import org.eclipse.mylar.tasklist.ui.actions.MarkTaskCompleteAction;
import org.eclipse.mylar.tasklist.ui.actions.MarkTaskIncompleteAction;
import org.eclipse.mylar.tasklist.ui.actions.NextTaskDropDownAction;
import org.eclipse.mylar.tasklist.ui.actions.OpenTaskEditorAction;
import org.eclipse.mylar.tasklist.ui.actions.PreviousTaskDropDownAction;
import org.eclipse.mylar.tasklist.ui.actions.RemoveFromCategoryAction;
import org.eclipse.mylar.tasklist.ui.actions.RenameAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskDeactivateAction;
import org.eclipse.mylar.tasklist.ui.actions.WorkOfflineAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.dialogs.FilteredTree;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class TaskListView extends ViewPart {

	public static final String ID = "org.eclipse.mylar.tasks.ui.views.TaskListView";
	private static final String SEPARATOR_ID_REPORTS = "reports";

	private static TaskListView INSTANCE;
		
	private FilteredTree tree;
    private DrillDownAdapter drillDownAdapter;
    
    private GoIntoAction goIntoAction;
    private GoUpAction goBackAction;
    
    private WorkOfflineAction workOffline;
    
    private CopyDescriptionAction copyAction;
    private OpenTaskEditorAction openAction;
    
    private CreateTaskAction createTask;
    private CreateTaskAction createTaskToolbar;
    private CreateCategoryAction createCategory;
    
    private RenameAction rename;
    
    private CollapseAllAction collapseAll;
    private DeleteAction delete;
    private AutoCloseAction autoClose;
    private OpenTaskEditorAction doubleClickAction;

    private RemoveFromCategoryAction removeAction;

    private MarkTaskCompleteAction completeTask;
    private MarkTaskIncompleteAction incompleteTask;
    private FilterCompletedTasksAction filterCompleteTask;
    private PriorityDropDownAction filterOnPriority;
    private PreviousTaskDropDownAction previousTaskAction;
    private NextTaskDropDownAction nextTaskAction;
    private static TaskPriorityFilter PRIORITY_FILTER = new TaskPriorityFilter();
    private static TaskCompleteFilter COMPLETE_FILTER = new TaskCompleteFilter();
    private List<ITaskFilter> filters = new ArrayList<ITaskFilter>();
    
    private static final String FILTER_LABEL = "<filter>";
    
    protected String[] columnNames = new String[] { "", ".", "!", "Description" };
    protected int[] columnWidths = new int[] { 70, 20, 20, 120 };
    private TreeColumn[] columns;
    private IMemento taskListMemento;
    public static final String columnWidthIdentifier = "org.eclipse.mylar.tasklist.ui.views.tasklist.columnwidth";
    public static final String tableSortIdentifier = "org.eclipse.mylar.tasklist.ui.views.tasklist.sortIndex";
    private int sortIndex = 2;
    
    public static String[] PRIORITY_LEVELS = { "P1", "P2", "P3", "P4", "P5" };
    
    private TaskActivationHistory taskHistory = new TaskActivationHistory();

	private boolean canEnableGoInto = false;
    	
    private final class PriorityDropDownAction extends Action implements IMenuCreator {
    	private Menu dropDownMenu = null;
    	
		public PriorityDropDownAction() {
			super();
			setText("Priority Filter");
			setToolTipText("Filter Priority Lower Than");
			setImageDescriptor(TaskListImages.FILTER_PRIORITY);
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
	    			MylarTasklistPlugin.setPriorityLevel(MylarTasklistPlugin.PriorityLevel.P1);
	    			PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[0]);	    			
	    			getViewer().refresh();
				}
			};  
			P1.setEnabled(true);
			P1.setToolTipText(PRIORITY_LEVELS[0]);
			ActionContributionItem item= new ActionContributionItem(P1);
			item.fill(dropDownMenu, -1);
			
			Action P2 = new Action(PRIORITY_LEVELS[1], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() {
	    			MylarTasklistPlugin.setPriorityLevel(MylarTasklistPlugin.PriorityLevel.P2);
	    			PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[1]);	    			
	    			getViewer().refresh();
				}
			};  
			P2.setEnabled(true);			
			P2.setToolTipText(PRIORITY_LEVELS[1]);
			item= new ActionContributionItem(P2);
			item.fill(dropDownMenu, -1);
			
			Action P3 = new Action(PRIORITY_LEVELS[2], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() { 
	    			MylarTasklistPlugin.setPriorityLevel(MylarTasklistPlugin.PriorityLevel.P3);
	    			PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[2]);	    			
	    			getViewer().refresh();
				}
			};
			P3.setEnabled(true);			
			P3.setToolTipText(PRIORITY_LEVELS[2]);
			item= new ActionContributionItem(P3);
			item.fill(dropDownMenu, -1);
			
			Action P4 = new Action(PRIORITY_LEVELS[3], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() {
	    			MylarTasklistPlugin.setPriorityLevel(MylarTasklistPlugin.PriorityLevel.P4);
	    			PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[3]);	    			
	    			getViewer().refresh();
				}
			};
			P4.setEnabled(true);			
			P4.setToolTipText(PRIORITY_LEVELS[3]);
			item= new ActionContributionItem(P4);
			item.fill(dropDownMenu, -1);
						
			Action P5 = new Action(PRIORITY_LEVELS[4], AS_CHECK_BOX) {	    		
	    		@Override
				public void run() { 
	    			MylarTasklistPlugin.setPriorityLevel(MylarTasklistPlugin.PriorityLevel.P5);
	    			PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[4]);	    			
	    			getViewer().refresh();
	    		}
			};  
			P5.setEnabled(true);
			P5.setToolTipText(PRIORITY_LEVELS[4]);
			item= new ActionContributionItem(P5);
			item.fill(dropDownMenu, -1);
			
			String priority = MylarTasklistPlugin.getPriorityLevel();
			if (priority.equals(PRIORITY_LEVELS[0])) {
				P1.setChecked(true);
			} else if (priority.equals(PRIORITY_LEVELS[1])) {
				P1.setChecked(true);
				P2.setChecked(true);
			} else if (priority.equals(PRIORITY_LEVELS[2])) {
				P1.setChecked(true);
				P2.setChecked(true);
				P3.setChecked(true);
			} else if (priority.equals(PRIORITY_LEVELS[3])) {
				P1.setChecked(true);
				P2.setChecked(true);
				P3.setChecked(true);
				P4.setChecked(true);
			} else if (priority.equals(PRIORITY_LEVELS[4])) {
				P1.setChecked(true);
				P2.setChecked(true);
				P3.setChecked(true);
				P4.setChecked(true);
				P5.setChecked(true);
			}
		}
		public void run() {	
			this.setChecked(isChecked());
		}
    }
    
    class TaskListContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			expandToActiveTasks();
        }
        public void dispose() {
        	// ignore
        }
        public Object[] getElements(Object parent) {
            if (parent.equals(getViewSite())) {
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
        	if (((Text)tree.getFilterControl()).getText() == "") {
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
        	if (((Text) tree.getFilterControl()).getText() == ""  
        			|| ((Text) tree.getFilterControl()).getText().startsWith(FILTER_LABEL)) {
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
        	for (ITaskFilter filter : filters) {
    			if (!filter.select(obj)) {
    				return true;
    			}
    		} 
        	return false;
        }
    }

	public static TaskListView openInActivePerspective() {
		try {
			return (TaskListView)Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().showView(ID);
		} catch(Exception e) {
			return null;
		} 
	} 
    
    public TaskListView() { 
    	INSTANCE = this;
    }

    class TaskListCellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {
        	int columnIndex = Arrays.asList(columnNames).indexOf(property);
            if(columnIndex == 0 && element instanceof ITaskListElement){
            	return ((ITaskListElement)element).isActivatable();
            } else if(columnIndex == 2 && element instanceof ITask){
            	return ((ITask)element).isDirectlyModifiable();
            }
//            int columnIndex = Arrays.asList(columnNames).indexOf(property);
//            if (element instanceof ITask) {
//            	ITask task = (ITask)element;
//                switch (columnIndex) {
//                case 0: return true;
//                case 1: return false;
//                case 2: return task.isDirectlyModifiable();
//                case 3: return task.isDirectlyModifiable();
//                }
//            } else if (element instanceof AbstractCategory) {
//                switch (columnIndex) {
//                case 0:
//                case 1: 
//                case 2:
//                	return false;
//                case 3: return ((AbstractCategory)element).isDirectlyModifiable();
//                } 
            else if(element instanceof ITaskListElement && isInRenameAction){
            	ITaskListElement taskListElement = (ITaskListElement)element;
            	switch (columnIndex) {
//            	case 0: return taskListElement.isActivatable();
//            	case 1: return false;
//            	case 2: return taskListElement.isDirectlyModifiable();
            	case 3: return taskListElement.isDirectlyModifiable();
            	}
            } 
            return false;
        }

        public Object getValue(Object element, String property) {
            try{
	        	int columnIndex = Arrays.asList(columnNames).indexOf(property);
	            if (element instanceof ITaskListElement) {
	            	final ITaskListElement taskListElement = (ITaskListElement)element;
					ITask task = null;
					if(taskListElement instanceof ITask){
						task = (ITask) taskListElement;
					} else if(taskListElement instanceof IQueryHit){
						if(((IQueryHit)taskListElement).hasCorrespondingActivatableTask()){
							task = ((IQueryHit)taskListElement).getOrCreateCorrespondingTask();
						}
					}
					switch (columnIndex) {
					case 0:
						if(task == null){
							return new Boolean(true);
						} else {
							return new Boolean(task.isCompleted());
						}
					case 1:
						return "";
					case 2:
						String priorityString = taskListElement.getPriority().substring(1);
						return new Integer(priorityString);
					case 3:
						return taskListElement.getDescription(true);
					}
				} else if (element instanceof ICategory) {
					ICategory cat = (ICategory) element;
					switch (columnIndex) {
					case 0:
						return new Boolean(false);
					case 1:
						return "";
					case 2:
						return "";
					case 3:
						return cat.getDescription(true);
					}
				}else if (element instanceof IQuery) {
					IQuery cat = (IQuery) element;
					switch (columnIndex) {
					case 0:
						return new Boolean(false);
					case 1:
						return "";
					case 2:
						return "";
					case 3:
						return cat.getDescription(true);
					}
				}
            } catch (Exception e){
            	MylarPlugin.log(e, e.getMessage());
            }
            return "";
        }

		public void modify(Object element, String property, Object value) {
			int columnIndex = -1;
			try {
				columnIndex = Arrays.asList(columnNames).indexOf(property);
				if (((TreeItem) element).getData() instanceof ICategory) {
					ICategory cat = (ICategory)((TreeItem) element).getData();
					switch (columnIndex) {
					case 0:						
//						getViewer().setSelection(null);
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						cat.setDescription(((String) value).trim());
//						getViewer().setSelection(null);
						break;
					}
				} else if (((TreeItem) element).getData() instanceof IQuery) {
					IQuery cat = (IQuery)((TreeItem) element).getData();
					switch (columnIndex) {
					case 0:						
//						getViewer().setSelection(null);
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						cat.setDescription(((String) value).trim());
//						getViewer().setSelection(null);
						break;
					}
				} else if (((TreeItem) element).getData() instanceof ITaskListElement) {

					final ITaskListElement taskListElement = (ITaskListElement) ((TreeItem) element).getData();
					ITask task = null;
					if(taskListElement instanceof ITask){
						task = (ITask) taskListElement;
					} else if(taskListElement instanceof IQueryHit){
						if(((IQueryHit)taskListElement).hasCorrespondingActivatableTask()){
							task = ((IQueryHit)taskListElement).getOrCreateCorrespondingTask();
						}
					}
					switch (columnIndex) {
					case 0:
						if(taskListElement instanceof IQueryHit){
							task = ((IQueryHit)taskListElement).getOrCreateCorrespondingTask();
						}
						if (task != null) {
							if (task.isActive()) {
								new TaskDeactivateAction(task, INSTANCE).run();
								nextTaskAction.setEnabled(taskHistory.hasNext());
					    		previousTaskAction.setEnabled(taskHistory.hasPrevious());
							} else {
								new TaskActivateAction(task).run();
								addTaskToHistory(task);
							}
//							getViewer().setSelection(null);
						}
						break;
					case 1:
						break;
					case 2:
						if (task.isDirectlyModifiable()) {
							Integer intVal = (Integer) value;
							task.setPriority("P" + (intVal + 1));
//							getViewer().setSelection(null);
						}  
						break;
					case 3: 
						if (task.isDirectlyModifiable()) {
							task.setDescription(((String) value).trim());
							MylarTasklistPlugin.getTaskListManager()
									.taskPropertyChanged(task, columnNames[3]);
//							getViewer().setSelection(null);
						}
						break;
					}
				} 
			} catch (Exception e) {
				MylarPlugin.fail(e, e.getMessage(), true);
			}
			getViewer().refresh();
		}         
    }
    
    public void addTaskToHistory(ITask task) {
    	if (!MylarTasklistPlugin.getDefault().isMultipleMode()) {
    		taskHistory.addTask(task);
    		nextTaskAction.setEnabled(taskHistory.hasNext());
    		previousTaskAction.setEnabled(taskHistory.hasPrevious());
    	}
    }
    
    public void clearTaskHistory() {
    	taskHistory.clear();
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
        	if (o1 instanceof ICategory || o1 instanceof IQuery) {
        		if (o2 instanceof ICategory|| o2 instanceof IQuery) {
        			return ((ITaskListElement)o1).getDescription(false).compareTo(
        					((ITaskListElement)o2).getDescription(false));
        		} else {
        			return -1;
        		}
        	} else if(o1 instanceof ITaskListElement){
        		if (o2 instanceof ICategory || o2 instanceof IQuery) {
        			return -1;
        		} else if(o2 instanceof ITaskListElement) {
        			ITaskListElement element1 = (ITaskListElement) o1;
        			ITaskListElement element2 = (ITaskListElement) o2;
//        			if (element1.isCompleted() && element2.isCompleted()) {
//        				return element1.getPriority().compareTo(element2.getPriority());
//        			}
//        			if (element1.isCompleted()) return 1;
//	                if (element2.isCompleted()) return -1;
//        			if (element1.hasCorrespondingActivatableTask() && element2.hasCorrespondingActivatableTask()) {
//        				ITask task1 = element1.getOrCreateCorrespondingTask();
//        				ITask task2 = element2.getOrCreateCorrespondingTask();
//        				
//    	                if (task1.isCompleted()) return 1;
//    	                if (task2.isCompleted()) return -1;
//        			}        			
	                if (column == columnNames[1]) {
	                	return 0;
	                } else if (column == columnNames[2]) {
	                    return element1.getPriority().compareTo(element2.getPriority());
	                } else if (column == columnNames[3]) {
	                	String c1 = element1.getStringForSortingDescription();
	                	String c2 = element2.getStringForSortingDescription();
	                	try{
	                		return new Integer(c1).compareTo(new Integer(c2));
	                	} catch (Exception e){}
	                	
	                	return c1.compareTo(c2);
	                		                	
	                } else {
	                	return 0;
	                }
        		}
        	} else{
        		return 0;
        	}
        	return 0;
        }
    }
    
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
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
		
		MylarTasklistPlugin.getDefault().createTaskListBackupFile();
		
		if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
	}
    
    private void restoreState() {
		if (taskListMemento != null) {
			IMemento taskListWidth = taskListMemento
					.getChild(columnWidthIdentifier);
			if (taskListWidth != null) {
				for (int i = 0; i < columnWidths.length; i++) {
					IMemento m = taskListWidth.getChild("col" + i);
					if (m != null) {
						int width = m.getInteger("width");
						columnWidths[i] = width;
						columns[i].setWidth(width);
					}
				}
			}
			IMemento sorterMemento = taskListMemento
					.getChild(tableSortIdentifier);
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
			getViewer().setSorter(new TaskListTableSorter(columnNames[sortIndex]));
		}
        addFilter(PRIORITY_FILTER);
//        if (MylarTasklistPlugin.getDefault().isFilterInCompleteMode()) 
//        	MylarTasklistPlugin.getTaskListManager().getTaskList().addFilter(inCompleteFilter);
        if (MylarTasklistPlugin.getDefault().isFilterCompleteMode()) 
        	addFilter(COMPLETE_FILTER);
        if (MylarTasklistPlugin.getDefault().isMultipleMode()) {
        	togglePreviousAction(false);
        	toggleNextAction(false);
        }
        
        getViewer().refresh();
    }
            
    /** 
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
    	tree = new FilteredTree(parent, SWT.VERTICAL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION, new TaskListPatternFilter());
//    	addToolTipHandler();
    	
//    	((Text)tree.getFilterControl()).setText(FILTER_LABEL);
    	getViewer().getTree().setHeaderVisible(true);
    	getViewer().getTree().setLinesVisible(true);
    	getViewer().setColumnProperties(columnNames);
    	getViewer().setUseHashlookup(true);  
                
        columns = new TreeColumn[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            columns[i] = new TreeColumn(getViewer().getTree(), 0); // SWT.LEFT
            columns[i].setText(columnNames[i]);
            columns[i].setWidth(columnWidths[i]);
            final int index = i;
            columns[i].addSelectionListener(new SelectionAdapter() {
            	
            	@Override
                public void widgetSelected(SelectionEvent e) {
            		sortIndex = index;
            		getViewer().setSorter(new TaskListTableSorter(columnNames[sortIndex]));
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
        TextCellEditor textEditor = new TextCellEditor(getViewer().getTree());
        ((Text) textEditor.getControl()).setOrientation(SWT.LEFT_TO_RIGHT);
        editors[0] = new CheckboxCellEditor();
        editors[1] = textEditor;
        editors[2] = new ComboBoxCellEditor(getViewer().getTree(), PRIORITY_LEVELS, SWT.READ_ONLY);
        editors[3] = textEditor;
        getViewer().setCellEditors(editors);   
        getViewer().setCellModifier(new TaskListCellModifier());
        getViewer().setSorter(new TaskListTableSorter(columnNames[sortIndex]));
      
        drillDownAdapter = new DrillDownAdapter(getViewer());
        getViewer().setContentProvider(new TaskListContentProvider());
        TaskListLabelProvider labelProvider = new TaskListLabelProvider();
        labelProvider.setBackgroundColor(parent.getBackground());
        getViewer().setLabelProvider(labelProvider);
        getViewer().setInput(getViewSite());
        
        getViewer().getTree().addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F2 && e.stateMask == 0){
					if(rename.isEnabled()){
						rename.run();
					}
				}
			}

			public void keyReleased(KeyEvent e) {}
        	
        });
        
        // HACK to support right click anywhere to select an item
        getViewer().getTree().addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {}

			public void mouseDown(MouseEvent e) {
				Tree t = getViewer().getTree();
				TreeItem item = t.getItem(new Point(e.x, e.y));
				if(e.button == 3 && item != null){
					getViewer().setSelection(new StructuredSelection(item.getData()));
				} else if(item == null){
					getViewer().setSelection(new StructuredSelection());
				}
			}

			public void mouseUp(MouseEvent e) {}
        });
        
        getViewer().addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedObject = ((IStructuredSelection)getViewer().getSelection()).getFirstElement();
				if(selectedObject instanceof ITaskListElement){
					updateActionEnablement(rename, (ITaskListElement)selectedObject);
				}
			}
        	
        });
        
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();       
        
        ToolTipHandler toolTipHandler = new ToolTipHandler(getViewer().getControl().getShell());
        toolTipHandler.activateHoverHelp(getViewer().getControl());
        
        initDragAndDrop(parent);
        expandToActiveTasks();
        restoreState();
    }

	@MylarWebRef(name="Drag and drop article", url="http://www.eclipse.org/articles/Article-Workbench-DND/drag_drop.html")
    private void initDragAndDrop(Composite parent) {
        Transfer[] types = new Transfer[] { TextTransfer.getInstance(), PluginTransfer.getInstance() };

        getViewer().addDragSupport(DND.DROP_MOVE, types, new DragSourceListener() {

            public void dragStart(DragSourceEvent event) {
                if (((StructuredSelection) getViewer().getSelection()).isEmpty()) {
                    event.doit = false;
                }
            }

            public void dragSetData(DragSourceEvent event) {
                StructuredSelection selection = (StructuredSelection) getViewer().getSelection();
                if (selection.getFirstElement() instanceof ITaskListElement) {
                	ITaskListElement element = (ITaskListElement)selection.getFirstElement();
                	
                	if (!selection.isEmpty() && element.isDragAndDropEnabled()) {
                        event.data = "" + element.getHandle();
                    } else {
                        event.data = "null";
                    }
                }
            }

            public void dragFinished(DragSourceEvent event) {
            	// don't care if the drag is done
            }
        });

        getViewer().addDropSupport(DND.DROP_MOVE, types, new ViewerDropAdapter(getViewer()) {
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
                		MylarTasklistPlugin.getTaskListManager().getTaskList().getRootTasks().remove(source);
                	}
                    
                    if (getCurrentTarget() instanceof TaskCategory) {
                    	((TaskCategory) getCurrentTarget()).addTask(source);
                    	source.setCategory((TaskCategory)getCurrentTarget());
                    } else if (getCurrentTarget() instanceof ITask) {
                    	ITask target = (ITask) getCurrentTarget();
                    	source.setCategory(null);
                    	target.addSubTask(source);                    	
                    	source.setParent(target);
                    }           
//                    getViewer().setSelection(null);
                    getViewer().refresh();
                    if (MylarTasklistPlugin.getDefault() != null) {
            			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
            		}
                    return true;
                } else if(selectedObject instanceof ITaskListElement &&
                		MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITaskListElement)selectedObject) != null &&
                		getCurrentTarget() instanceof TaskCategory){
                	
                	MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITaskListElement)selectedObject).dropItem((ITaskListElement)selectedObject, (TaskCategory)getCurrentTarget());
//					getViewer().setSelection(null);
                	getViewer().refresh();
                	if (MylarTasklistPlugin.getDefault() != null) {
            			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
            		}
                    return true;
                }
                return false;
            }

            @Override
            public boolean validateDrop(Object targetObject, int operation,
                    TransferData transferType) {
                Object selectedObject = ((IStructuredSelection) ((TreeViewer) getViewer())
                        .getSelection()).getFirstElement();
                if (selectedObject instanceof ITaskListElement && ((ITaskListElement)selectedObject).isDragAndDropEnabled()) {
                    if (getCurrentTarget() != null &&  getCurrentTarget() instanceof TaskCategory) {
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
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
		    	List<ITask> activeTasks = MylarTasklistPlugin.getTaskListManager().getTaskList().getActiveTasks();
		    	for (ITask t : activeTasks) {
		    		getViewer().expandToLevel(t, 0);
		    	}
            }
        });
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                TaskListView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(getViewer().getControl());
        getViewer().getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, getViewer());
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
    	updateDrillDownActions();
    	manager.add(collapseAll);
    	manager.add(goBackAction);
//    	manager.add(new Separator());
        manager.add(autoClose);
        autoClose.setEnabled(true);
        manager.add(workOffline);
//        workOffline.setEnabled(true);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }    
    
    void fillContextMenu(IMenuManager manager) {
    	updateDrillDownActions();
    	
    	ITaskListElement element = null;;
        final Object selectedObject = ((IStructuredSelection)getViewer().getSelection()).getFirstElement();
        if (selectedObject instanceof ITaskListElement) {
        	element = (ITaskListElement) selectedObject;
        }
        
        addAction(openAction, manager, element);
        addAction(goIntoAction, manager, element);        
        manager.add(new Separator());
        addAction(completeTask, manager, element);
        addAction(incompleteTask, manager, element);
        addAction(delete, manager, element);
        addAction(copyAction, manager, element);
        addAction(rename, manager, element);
        addAction(removeAction, manager, element);
        manager.add(new Separator());
        addAction(createTask, manager, element);
        manager.add(new Separator("mylar"));   
        	
    	for (ITaskListDynamicSubMenuContributor contributor : MylarTasklistPlugin.getDefault().getDynamicMenuContributers()) {
	        manager.add(new Separator());
	        MenuManager subMenuManager = contributor.getSubMenuManager(this, (ITaskListElement)selectedObject);
	        if (subMenuManager != null) addMenuManager(subMenuManager, manager, element);
		}
    	
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void addMenuManager(IMenuManager menuToAdd, IMenuManager manager, ITaskListElement element) {
    	if(element != null && element instanceof ITask){
        	manager.add(menuToAdd);
    	}
    }
    
    
    private void addAction(Action action, IMenuManager manager, ITaskListElement element) {
    	manager.add(action);
    	if(element != null){
    		ITaskHandler handler = MylarTasklistPlugin.getDefault().getTaskHandlerForElement(element);
    		if(handler != null){
    			action.setEnabled(handler.enableAction(action, element));
    		} else {
    			updateActionEnablement(action, element);
    		}
    	}
    }

    private void updateActionEnablement(Action action, ITaskListElement element){
    
		if(element instanceof ITask){
			if(action instanceof MarkTaskCompleteAction){
				if(element.isCompleted()){
					action.setEnabled(false);
				} else {
					action.setEnabled(true);
				}
			} else if(action instanceof MarkTaskIncompleteAction){
				if(element.isCompleted()){
					action.setEnabled(true);
				} else {
					action.setEnabled(false);
				}
			} else if(action instanceof DeleteAction){
				action.setEnabled(true);
			} else if(action instanceof CreateTaskAction){
				action.setEnabled(false);
			}else if(action instanceof OpenTaskEditorAction){
				action.setEnabled(true);
			} else if(action instanceof CopyDescriptionAction){
				action.setEnabled(true);
			} else if(action instanceof RenameAction){
				action.setEnabled(true);
			}
		} else if(element instanceof ICategory) {
			if(action instanceof MarkTaskCompleteAction){
				action.setEnabled(false);
			} else if(action instanceof MarkTaskIncompleteAction){
					action.setEnabled(false);
			} else if(action instanceof DeleteAction){
				if(((ICategory)element).isArchive())
					action.setEnabled(false);
				else
					action.setEnabled(true);
			} else if(action instanceof CreateTaskAction){
				if(((ICategory)element).isArchive())
					action.setEnabled(false);
				else
					action.setEnabled(true);
			} else if(action instanceof GoIntoAction){
				TaskCategory cat = (TaskCategory) element;
				if(cat.getChildren().size() > 0){
					action.setEnabled(true);
				} else {
					action.setEnabled(false);
				}
			}else if(action instanceof OpenTaskEditorAction){
				action.setEnabled(false);
			} else if(action instanceof CopyDescriptionAction){
				action.setEnabled(true);
			} else if(action instanceof RenameAction){
				if(((ICategory)element).isArchive())
					action.setEnabled(false);
				else
					action.setEnabled(true);
			}
		} else {
			action.setEnabled(true);
		}
		
		if(!canEnableGoInto){
			goIntoAction.setEnabled(false);
		}
    }
    
    /**
     * @see org.eclipse.pde.internal.ui.view.HistoryDropDownAction
     *
     */
    private void makeActions() {
    	
    	copyAction = new CopyDescriptionAction(this);
    	openAction = new OpenTaskEditorAction(this); 
    	
    	workOffline = new WorkOfflineAction();
    	
    	goIntoAction = new GoIntoAction(drillDownAdapter);
    	goBackAction = new GoUpAction(drillDownAdapter);
    	
    	createTask = new CreateTaskAction(this);
    	createTaskToolbar = new CreateTaskAction(this);   
        createCategory = new CreateCategoryAction(this);
        removeAction = new RemoveFromCategoryAction(this);
        rename = new RenameAction(this);
        
        delete = new DeleteAction(this);
        collapseAll = new CollapseAllAction(this);
        autoClose = new AutoCloseAction();
        completeTask = new MarkTaskCompleteAction(this);
        incompleteTask = new MarkTaskIncompleteAction(this);        
        doubleClickAction = new OpenTaskEditorAction(this);            
        filterCompleteTask = new FilterCompletedTasksAction(this);                       
        filterOnPriority = new PriorityDropDownAction();
        previousTaskAction = new PreviousTaskDropDownAction(this, taskHistory);
        nextTaskAction = new NextTaskDropDownAction(this, taskHistory);
    }

    
    public void toggleNextAction(boolean enable) {
    	nextTaskAction.setEnabled(enable);
    }
    
    public void togglePreviousAction(boolean enable) {
    	previousTaskAction.setEnabled(enable);
    }
    
    public NextTaskDropDownAction getNextTaskAction() {
    	return nextTaskAction;
    }
    
    public PreviousTaskDropDownAction getPreviousTaskAction() {
    	return previousTaskAction;
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
    	return (MylarTasklistPlugin.getTaskListManager().getTaskForHandle(taskId, true) == null);
//    	for (ITask task : MylarTasklistPlugin.getTaskListManager().getTaskList().getRootTasks()) {
//    		if (task.getHandle().equals(taskId)) {
//    			return true;
//    		}
//    	}
//    	for (TaskCategory cat : MylarTasklistPlugin.getTaskListManager().getTaskList().getTaskCategories()) {
//    		for (ITask task : cat.getChildren()) {
//        		if (task.getHandle().equals(taskId)) {
//        			return true;
//        		}
//        	}
//    	}
//		return false;
	}
	
	public void closeTaskEditors(ITask task, IWorkbenchPage page) throws LoginException, IOException{
		ITaskHandler taskHandler = MylarTasklistPlugin.getDefault().getTaskHandlerForElement(task);
	    if(taskHandler != null){
        	taskHandler.taskClosed(task, page);
        } else if (task instanceof Task) {
        	IEditorInput input = new TaskEditorInput((Task) task);

			IEditorPart editor = page.findEditor(input);
	
			if (editor != null) {
				page.closeEditor(editor, false);
			}
        }
	}
	
	private void hookDoubleClickAction() {
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }
    
	public void showMessage(String message) {
        MessageDialog.openInformation(
			getViewer().getControl().getShell(),
            "Tasklist Message",
            message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
		getViewer().getControl().setFocus();
    }

    public String getBugIdFromUser() {
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
    
//    public String[] getLabelPriorityFromUser(String kind) {
//    	String[] result = new String[2];
//    	Dialog dialog = null;
//    	boolean isTask = kind.equals("task");
//    	if (isTask) {
//    		dialog = new TaskInputDialog(
//    	            Workbench.getInstance().getActiveWorkbenchWindow().getShell());
//    	} else {
//    		dialog = new InputDialog(
//    				Workbench.getInstance().getActiveWorkbenchWindow().getShell(), 
//    	            "Enter name", 
//    	            "Enter a name for the " + kind + ": ", 
//    	            "", 
//    	            null);
//    	}
//    	
//        int dialogResult = dialog.open();
//        if (dialogResult == Window.OK) {
//        	if (isTask) {
//        		result[0] = ((TaskInputDialog)dialog).getTaskname();
//        		result[1] = ((TaskInputDialog)dialog).getSelectedPriority();
//        	} else {
//        		result[0] = ((InputDialog)dialog).getValue();
//        	}
//        	return result;
//        } else {
//            return null;
//        }
//    }
    
    public void notifyTaskDataChanged(ITask task) {
        if (getViewer().getTree() != null && !getViewer().getTree().isDisposed()) { 
        	getViewer().refresh();
        	expandToActiveTasks();
        }
    }
    
    public static TaskListView getDefault() {
    	return INSTANCE;
    }
    
    public TreeViewer getViewer() {
    	return tree.getViewer();
    }
    
    public TaskCompleteFilter getCompleteFilter() {
    	return COMPLETE_FILTER;
    }

    
    public TaskPriorityFilter getPriorityFilter() {
    	return PRIORITY_FILTER;
    }

    private void fillLocalToolBar(IToolBarManager manager) {
    	manager.removeAll();
    	manager.add(createTaskToolbar);
        manager.add(createCategory);
        manager.add(new Separator(SEPARATOR_ID_REPORTS));
        manager.add(new Separator());
	    manager.add(filterCompleteTask);
	    manager.add(filterOnPriority);
//	    manager.add(new Separator());
	    manager.add(previousTaskAction);
	    manager.add(nextTaskAction);
	    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        
    }
    
    public void addFilter(ITaskFilter filter) {
		if (!filters.contains(filter)) filters.add(filter);		
	}
	
	public void removeFilter(ITaskFilter filter) {
		filters.remove(filter);
	}	
		
	public TaskListContentProvider getContentProvider() {
		return new TaskListContentProvider();
	}
//	public  void resetToolbarsAndPopups() {
//		getViewSite().getActionBars().getMenuManager().removeAll();
//        fillLocalPullDown(getViewSite().getActionBars().getMenuManager());
//        fillContextMenu(getViewSite().getActionBars().getMenuManager());
//		fillLocalToolBar(getViewSite().getActionBars().getToolBarManager());
//		getViewSite().getActionBars().getToolBarManager().update(true);  
//	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public void updateDrillDownActions() {
		if(drillDownAdapter.canGoBack()){
			goBackAction.setEnabled(true);
		} else {
			goBackAction.setEnabled(false);
		}
		
		if(drillDownAdapter.canGoInto()){
			canEnableGoInto = true;
		} else {
			canEnableGoInto  = false;
		}
	}

	/**
	 * HACK: This is used for the copy action 
	 * @return
	 */
	public Composite getFakeComposite() {
		return tree;
	}

	private boolean isInRenameAction = false;
	
	public void setInRenameAction(boolean b) {
		isInRenameAction = b;
	}
	
	/**
	 * This method is for testing only
	 */
	public TaskActivationHistory getTaskActivationHistory(){
		return taskHistory;
	}
	
}

//TextTransfer textTransfer = TextTransfer.getInstance();
//DropTarget target = new DropTarget(viewer.getTree(), DND.DROP_MOVE);
//target.setTransfer(new Transfer[] { textTransfer });
//target.addDropListener(new TaskListDropTargetListener(parent, null, textTransfer, true));
//
//DragSource source = new DragSouarce(viewer.getTree(), DND.DROP_MOVE);
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
//
//                }
//            }
//
//        });


//private ViewerFilter completeFilter = new ViewerFilter(){
//@Override
//public boolean select(Viewer viewer, Object parentElement, Object element) {
//	if (element instanceof ITaskListElement) {
//		if(element instanceof ITask && ((ITaskListElement)element).hasCorrespondingActivatableTask()){
//			ITask task = ((ITaskListElement)element).getOrCreateCorrespondingTask();
//			if (task.isActive()) {
//				return true;
//			}
//			if(task != null){
//				return !task.isCompleted();
//			} else {
//				return true;
//			}
//		} else {
//			return true;
//		}
//	} 
//	return false;
//}    			
//};
//
//private ViewerFilter inCompleteFilter = new ViewerFilter(){
//@Override
//public boolean select(Viewer viewer, Object parentElement, Object element) {
//	if (element instanceof ITask) {
//		return ((ITask)element).isCompleted();
//	} else {
//		return true;
//	} 
//}    			
//};
//
//public class PriorityFilter extends ViewerFilter {
//private List<String> priorities = new ArrayList<String>();
//
//public PriorityFilter() {    		
//	displayPrioritiesAbove(MylarTasklistPlugin.getPriorityLevel());
//}
//
//public void displayPrioritiesAbove(String p) {
//	priorities.clear();    		
//	if (p.equals(PRIORITY_LEVELS[0])) {
//		priorities.add(PRIORITY_LEVELS[0]);
//	}
//	if (p.equals(PRIORITY_LEVELS[1])) {
//		priorities.add(PRIORITY_LEVELS[0]);
//		priorities.add(PRIORITY_LEVELS[1]);
//	} else if (p.equals(PRIORITY_LEVELS[2])) {
//		priorities.add(PRIORITY_LEVELS[0]);
//		priorities.add(PRIORITY_LEVELS[1]);
//		priorities.add(PRIORITY_LEVELS[2]);
//	} else if (p.equals(PRIORITY_LEVELS[3])) {
//		priorities.add(PRIORITY_LEVELS[0]);
//		priorities.add(PRIORITY_LEVELS[1]);
//		priorities.add(PRIORITY_LEVELS[2]);
//		priorities.add(PRIORITY_LEVELS[3]);
//	} else if (p.equals(PRIORITY_LEVELS[4])) {
//		priorities.add(PRIORITY_LEVELS[0]);
//		priorities.add(PRIORITY_LEVELS[1]);
//		priorities.add(PRIORITY_LEVELS[2]);
//		priorities.add(PRIORITY_LEVELS[3]);
//		priorities.add(PRIORITY_LEVELS[4]);
//	}
//}
//
//@Override
//public boolean select(Viewer viewer, Object parentElement, Object element) {
//	if (element instanceof ITaskListElement) {
//		ITaskListElement task = (ITaskListElement) element;
//		if (priorities.size() == PRIORITY_LEVELS.length) {
//			return true;
//		} else {
//			return checkTask(task);
//		}								
//	} else {
//		return true;
//	}
//}
//private boolean checkTask(ITaskListElement task) {
//	if (task instanceof ITask && ((ITask)task).isActive()) {
//		return true;
//	}
//	for (String filter : priorities) {				
//		if (task.getPriority().equals(filter)) {
//			return true;
//		}
//	}
//	return false;
//}
//};