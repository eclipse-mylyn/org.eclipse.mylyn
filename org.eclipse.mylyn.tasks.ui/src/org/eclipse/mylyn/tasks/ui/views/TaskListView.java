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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.action.Action;
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
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.dt.MylarWebRef;
import org.eclipse.mylar.tasks.BugzillaTask;
import org.eclipse.mylar.tasks.Category;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.ui.BugzillaTaskEditorInput;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.actions.ToggleGlobalInterestFilteringAction;
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
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 */
public class TaskListView extends ViewPart {

	private static TaskListView INSTANCE;
	
	//private CheckboxTreeViewer viewer;
	private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    
    private Action refresh;
    private Action createTask;
    private Action createCategory;
    private Action addBugzillaReport; 
    private Action rename;
    private Action delete;
    private Action doubleClickAction;
    private Action clearSelectedTaskscapeAction;

    //private Action toggleIntersectionModeAction = new ToggleIntersectionModeAction();
    private Action toggleFilteringAction = new ToggleGlobalInterestFilteringAction();

    private Action completeTask;
    private Action incompleteTask;
    
    private Action filterCompleteTask;
    private Action filterInCompleteTask;
    
    protected String[] columnNames = new String[] { "", ".", "!", "Description", "handle" };
    protected int[] columnWidths = new int[] { 70, 20, 20, 120, 70 };
    private TreeColumn[] columns;
    private IMemento taskListMemento;
    public static final String columnWidthIdentifier = "org.eclipse.mylar.tasks.ui.views.tasklist.columnwidth";
    public static final String tableSortIdentifier = "org.eclipse.mylar.tasks.ui.views.tasklist.sortIndex";
    private int sortIndex = 2;
    

    private String[] PRIORITY_LEVELS = { "P1", "P2", "P3", "P4", "P5" };
    
    private final class CreateTaskAction extends Action {
        private boolean isCategory = false;
        
        public CreateTaskAction(boolean isCategory) {
            this.isCategory = isCategory;
        }
        
        @Override
        public void run() {
            String label = getLabelNameFromUser("task");
            if(label == null) return;
            Task newTask = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), label);
            
            Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
            if (selectedObject instanceof Task && !isCategory){
                ((Task)selectedObject).addSubtask(newTask);
            } else {
            	if (isCategory) {
            		newTask.setIsCategory(true);
            	} 
                MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(newTask);
            }  
            MylarUiPlugin.getDefault().setHighlighterMapping(
                    newTask.getHandle(), 
                    MylarUiPlugin.getDefault().getDefaultHighlighter().getName());
            TaskListView.this.viewer.refresh();
            
            viewer.refresh();
        }
    }
    
    private final ITaskscapeListener FILTER_LISTENER = new ITaskscapeListener() { 
        public void interestChanged(ITaskscapeNode info) {}        
        public void interestChanged(List<ITaskscapeNode> nodes) {}  
        public void taskscapeActivated(ITaskscape taskscape) {}
        public void taskscapeDeactivated(ITaskscape taskscape) {}
        public void nodeDeleted(ITaskscapeNode node) {}
        public void landmarkAdded(ITaskscapeNode element) {}
        public void landmarkRemoved(ITaskscapeNode element) {}
        public void relationshipsChanged() {}
        public void presentationSettingsChanged(UpdateKind kind) {
        	refresh();
        }         
        public void presentationSettingsChanging(UpdateKind kind) {
        	refresh();
        }
                         
        private void refresh() {
        	if (viewer != null && !viewer.getTree().isDisposed()) {        		
        		viewer.refresh();
                setCheckedState(viewer.getTree().getItems());       
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
            	if (MylarUiPlugin.getDefault().isGlobalFilteringEnabled()) {
            		return MylarTasksPlugin.getTaskListManager().getTaskList().getTasksInProgress().toArray();
            	} else {
            		return MylarTasksPlugin.getTaskListManager().getTaskList().getRootTasks().toArray();
            	}                
            }
            return getChildren(parent);
        }
        public Object getParent(Object child) {
            if (child instanceof Task) {
                return ((Task)child).getParent();
            }
            return null;
        }
        public Object [] getChildren(Object parent) {
            if (parent instanceof ITask) {
            	if (MylarUiPlugin.getDefault().isGlobalFilteringEnabled()) {
            		return ((ITask)parent).getSubTasksInProgress().toArray();
            	} else {
            		return ((ITask)parent).getChildren().toArray();
            	}                
            } 
            return new Object[0];
        }
        public boolean hasChildren(Object parent) {  
            if (parent instanceof ITask) {
                ITask task = (ITask)parent;
                return task.getChildren() != null && task.getChildren().size() > 0;
            } 
            return false;
        }
    }

    public TaskListView() { 
    	INSTANCE = this;
    	MylarPlugin.getTaskscapeManager().addListener(FILTER_LISTENER);
    }

    class TaskListCellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {
            int columnIndex = Arrays.asList(columnNames).indexOf(property);
            ITask task = (ITask) element;
            switch (columnIndex) {
            case 0:
                return true;
            case 1:
            	return false;
            case 2:
                return !(task instanceof BugzillaTask);
            case 3:
                return !(task instanceof BugzillaTask);
            case 4:
            	return false;
            }
            return false;
        }

        public Object getValue(Object element, String property) {
            int columnIndex = Arrays.asList(columnNames).indexOf(property);
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
            return "";
        }

		public void modify(Object element, String property, Object value) {
			int columnIndex = -1;
			try {
				columnIndex = Arrays.asList(columnNames).indexOf(property);
				ITask task = (ITask) ((TreeItem) element).getData();
				switch (columnIndex) {
				case 0:
					if (!task.isCategory()) {
						if (task.isActive()) {
							MylarTasksPlugin.getTaskListManager().deactivateTask(
									task);
						} else {
							MylarTasksPlugin.getTaskListManager().activateTask(
									task);
						}
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
					viewer.setSelection(null);
					break;
				case 4:
					break;
				}
				viewer.refresh();
			} catch (Exception e) {
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
                    ITask target = (ITask) getCurrentTarget();
                    source.getParent().removeSubtask(source);
                    target.addSubtask(source);
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
                    ITask source = (ITask) selectedObject;
                    ITask target = (ITask) getCurrentTarget();
                    if (target != null && !target.isCategory())
                        return false;
                    if (source.isCategory())
                        return false;
                }
                return TextTransfer.getInstance().isSupportedType(transferType);
            }

        });
    }
    
    private void setCheckedState(TreeItem[] items) {
        for (int i = 0; i < items.length; i++) {
            TreeItem item = items[i];
            if (item.getData() instanceof Task) {
                item.setChecked(((Task)item.getData()).isCompleted());            	
            } else if (item.getData() instanceof Category) {
                item.setGrayed(true);
            }
            setCheckedState(item.getItems());
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
//        manager.add(createCategory);
//        manager.add(new Separator());
//        manager.add(createTask);
    }

    void fillContextMenu(IMenuManager manager) {
        manager.add(completeTask);
        manager.add(incompleteTask);
//        manager.add(new Separator());
        manager.add(createTask);
        manager.add(addBugzillaReport);
        manager.add(rename);
        manager.add(delete);
        manager.add(clearSelectedTaskscapeAction);
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
        manager.add(addBugzillaReport);
        manager.add(refresh);
        manager.add(new Separator());
        manager.add(toggleFilteringAction);
        //manager.add(toggleIntersectionModeAction);
        manager.add(new Separator());
        manager.add(filterCompleteTask);
        manager.add(filterInCompleteTask);
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {
    	refresh = new Action() {
    		
    		@Override
			public void run() { 
//    			Object[] expanded = viewer.getExpandedElements();
//    			for (int i = 0; i < expanded.length; i++) {
//					Object element = expanded[i];
//					if (element instanceof BugzillaTask) {
//						((BugzillaTask)element).refresh();
//					}
//				}
				
				List<ITask> tasks = MylarTasksPlugin.getTaskListManager().getTaskList().getRootTasks();
				
                for (ITask task : tasks) {
					if (task instanceof BugzillaTask) {
						((BugzillaTask)task).refresh();
					}
					refreshChildren(task.getChildren());
				}
				
				viewer.refresh();
			}
		};  
    	refresh.setText("Refresh all Bugzilla reports");
    	refresh.setToolTipText("Refresh all Bugzilla reports"); 
    	refresh.setImageDescriptor(MylarImages.REFRESH);
        
//        createCategory = new Action() {
//            public void run() {
//                try {
//                    String label = getLabelNameFromUser("category");
//                    MylarTasksPlugin.getTaskListManager().getTaskList().createCategory(label);
//                    viewer.refresh();
//                } catch (Exception e) {
//                    MylarPlugin.fail(e, "Couldn't create category", true); 
//                }
//            }
//        };
//        createCategory.setText("Create category");
//        createCategory.setToolTipText("Create category");
//        createCategory.setImageDescriptor(MylarImages.TASK_CATEGORY_NEW);
        
        createTask = new CreateTaskAction(false);
        createTask.setText("Create task");
        createTask.setToolTipText("Create task");
        createTask.setImageDescriptor(MylarImages.TASK_NEW);

        createCategory = new CreateTaskAction(true);
        createCategory.setText("Create category");
        createCategory.setToolTipText("Create category");
        createCategory.setImageDescriptor(MylarImages.CATEGORY_NEW);
        
        addBugzillaReport = new Action() {
        	
        	@Override
            public void run() {
                String bugIdString = getBugIdFromUser();
                int bugId = -1;
                try {
                    bugId = Integer.parseInt(bugIdString);
                } catch (NumberFormatException nfe) {
                    showMessage("Please enter a valid report number");
                    return;
                }
				
				// Check the existing tasks to see if the id is used already.
				// This is to prevent the creation of mutliple Bugzilla tasks
				//   for the same Bugzilla report.
				boolean doesIdExistAlready = false;
				List<ITask> tasks = MylarTasksPlugin.getTaskListManager().getTaskList().getRootTasks();
				for (Iterator<ITask> iter = tasks.iterator(); iter.hasNext() && !doesIdExistAlready;) {
					ITask task = iter.next();
					doesIdExistAlready = lookForId(task, "" + bugId); // HACK:
				}
				if (doesIdExistAlready) {
                    showMessage("A Bugzilla task with ID " + bugId + " already exists.");
                    return;
				}
				
                
				//HACK need the server name and handle properly
                ITask newTask = new BugzillaTask("Bugzilla-"+bugId, "<bugzilla info>");
				
                Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
                if (selectedObject instanceof Task){
                    ((Task)selectedObject).addSubtask(newTask);
                } else { 
                    MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(newTask);
                }
//                viewer.expandAll();
                viewer.refresh();
            }
        };
        addBugzillaReport.setText("Add bugzilla report");
        addBugzillaReport.setToolTipText("Add bugzilla report");
        addBugzillaReport.setImageDescriptor(MylarImages.TASK_BUGZILLA_NEW);
        
        delete = new Action() {
        	
        	@Override
            public void run() {              
                boolean deleteConfirmed = MessageDialog.openQuestion(
                        Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
                        "Confirm delete", 
                        "Delete selected task and all subtasks?");
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
                            closeBugTaskEditors((ITask)selectedObject, page);
                        }catch(Exception e){
                        	MylarPlugin.log(this.getClass().toString(), e);
                        }
                    } 
                }
                viewer.refresh();
            }
        };
        delete.setText("Delete");
        delete.setToolTipText("Delete");
        delete.setImageDescriptor(MylarImages.REMOVE);
        
        completeTask = new Action() {
        	
        	@Override
            public void run() {              
                Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
                if (selectedObject instanceof Task){ 
                	((Task)selectedObject).setCompleted(true);                	
                	viewer.refresh();
                }
            }
        };
        completeTask.setText("Mark Complete");
        completeTask.setToolTipText("Mark Complete");
//        activateTask.setImageDescriptor(MylarImages.REMOVE);
        
        incompleteTask = new Action() {
        	
        	@Override
            public void run() {              
                Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
                if (selectedObject instanceof Task){ 
                	((Task)selectedObject).setCompleted(false);                	
                	viewer.refresh();
                }
            }
        };
        incompleteTask.setText("Mark Incomplete");
        incompleteTask.setToolTipText("Mark Incomplete");
//        deactivateTask.setImageDescriptor(MylarImages.REMOVE);

        rename = new Action() {
        	
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
                viewer.refresh();
            }
        };
        rename.setText("Rename");
        rename.setToolTipText("Rename");

        clearSelectedTaskscapeAction = new Action() {
        	
        	@Override
            public void run() {
                Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
                if (selectedObject != null) {
                	MylarPlugin.getTaskscapeManager().taskDeleted(((Task)selectedObject).getHandle(), ((Task)selectedObject).getPath());
                	viewer.refresh();
                }
            }
        };
        clearSelectedTaskscapeAction.setText("Erase Taskscape");
        clearSelectedTaskscapeAction.setToolTipText("Erase Taskscape");
        clearSelectedTaskscapeAction.setImageDescriptor(MylarImages.ERASE_TASKSCAPE);
        
        doubleClickAction = new Action() {        	
        	@Override
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection)selection).getFirstElement();
                if (obj instanceof ITask) {
                	((ITask)obj).openTaskInEditor();
                }
                viewer.refresh();
            }
        };            

        filterCompleteTask = new Action() {
        	@Override
            public void run() {
        		MylarUiPlugin.getDefault().setFilterCompleteMode(isChecked());
                viewer.refresh();
            }
        };
        filterCompleteTask.setText("Filter Complete tasks");
        filterCompleteTask.setToolTipText("Filter Completed tasks");
        filterCompleteTask.setImageDescriptor(MylarImages.TASK_ACTIVE);
        
        filterInCompleteTask = new Action() {
        	@Override
            public void run() {
        		MylarUiPlugin.getDefault().setFilterInCompleteMode(isChecked());
                viewer.refresh();
            }
        };
        filterInCompleteTask.setText("Filter Incomplete tasks");
        filterInCompleteTask.setToolTipText("Filter Incomplete tasks");
        filterInCompleteTask.setImageDescriptor(MylarImages.TASK_INACTIVE);        
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
    protected boolean lookForId(ITask task, String taskId) {
		if (task.getHandle() == taskId) {
			return true;
		}
		
		List<ITask> children = task.getChildren();
		if (children == null) {
			return false;
		}
		
        for (ITask childTask : children) {
			if (lookForId(childTask, taskId)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected void closeBugTaskEditors(ITask task, IWorkbenchPage page) throws LoginException, IOException{
		if (task instanceof BugzillaTask) {
			IEditorInput input = new BugzillaTaskEditorInput((BugzillaTask)task);
			IEditorPart bugEditor = page.findEditor(input);
			
			if (bugEditor != null) {
				page.closeEditor(bugEditor, false);	
			}
		}
		
		List<ITask> children = task.getChildren();
		if (children == null) return;
        for (ITask child : children) closeBugTaskEditors(child, page);
	}
	
	protected void refreshChildren(List<ITask> children) {
		if (children != null) {
            for (ITask child : children) {
				if (child instanceof BugzillaTask) {
					((BugzillaTask)child).refresh();
				}
				refreshChildren(child.getChildren());
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
    
    public void notifyTaskDataChanged() {
        if (viewer.getTree() != null && !viewer.getTree().isDisposed()) viewer.refresh();
    }
    
    public static TaskListView getDefault() {
    	return INSTANCE;
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
//					System.out.println(counter++);
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
