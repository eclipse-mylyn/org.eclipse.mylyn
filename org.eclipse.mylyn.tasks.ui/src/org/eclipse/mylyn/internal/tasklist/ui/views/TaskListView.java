/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.internal.core.dt.MylarWebRef;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.AbstractTaskFilter;
import org.eclipse.mylar.internal.tasklist.ui.IDynamicSubMenuContributor;
import org.eclipse.mylar.internal.tasklist.ui.TaskCompleteFilter;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.TaskListPatternFilter;
import org.eclipse.mylar.internal.tasklist.ui.TaskPriorityFilter;
import org.eclipse.mylar.internal.tasklist.ui.actions.CollapseAllAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.CopyDescriptionAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.DeleteAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.FilterCompletedTasksAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.GoIntoAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.GoUpAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.MarkTaskCompleteAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.MarkTaskIncompleteAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.NewCategoryAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.NewLocalTaskAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.NextTaskDropDownAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.OpenTaskInExternalBrowserAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.PreviousTaskDropDownAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.RemoveFromCategoryAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.RenameAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.TaskDeactivateAction;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskActivityListener;
import org.eclipse.mylar.provisional.tasklist.ITaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class TaskListView extends ViewPart {

	private static final String SEPARATOR_LOCAL = "local";

	private static final String SEPARATOR_CONTEXT = "context";

	private static final String SEPARATOR_REPORTS = "reports";

	private static final String LABEL_NO_TASKS = "no task active";

	public static final String ID = "org.eclipse.mylar.tasks.ui.views.TaskListView";

	public static final String[] PRIORITY_LEVELS = { Task.PriorityLevel.P1.toString(),
			Task.PriorityLevel.P2.toString(), Task.PriorityLevel.P3.toString(), Task.PriorityLevel.P4.toString(),
			Task.PriorityLevel.P5.toString() };
	
	private static final String SEPARATOR_ID_REPORTS = SEPARATOR_REPORTS;

	private static final String PART_NAME = "Mylar Tasks";

	private static TaskListView INSTANCE;

	private TaskListFilteredTree filteredTree;

	private DrillDownAdapter drillDownAdapter;

	private ITaskContainer drilledIntoCategory = null;

	private GoIntoAction goIntoAction;

	private GoUpAction goUpAction;

	private CopyDescriptionAction copyDescriptionAction;

	private OpenTaskListElementAction openTaskEditor;

	private OpenTaskInExternalBrowserAction openUrlInExternal;

	private NewLocalTaskAction newLocalTaskAction;

	private NewCategoryAction newCategoryAction;

	private RenameAction renameAction;

	private CollapseAllAction collapseAll;

	private DeleteAction deleteAction;

	private RemoveFromCategoryAction removeFromCategoryAction;

	private TaskActivateAction activateAction = new TaskActivateAction();

	private TaskDeactivateAction deactivateAction = new TaskDeactivateAction();

	private MarkTaskCompleteAction markIncompleteAction;

	private MarkTaskIncompleteAction markCompleteAction;

	private FilterCompletedTasksAction filterCompleteTask;

	private PriorityDropDownAction filterOnPriority;

	private PreviousTaskDropDownAction previousTaskAction;

	private NextTaskDropDownAction nextTaskAction;

	// private WorkOfflineAction workOffline;

	// private ManageEditorsAction autoClose;

	private static TaskPriorityFilter PRIORITY_FILTER = new TaskPriorityFilter();

	private static TaskCompleteFilter COMPLETE_FILTER = new TaskCompleteFilter();

	List<AbstractTaskFilter> filters = new ArrayList<AbstractTaskFilter>();

	static final String FILTER_LABEL = "<filter>";

	protected String[] columnNames = new String[] { "", ".", "!", "Description" };

	protected int[] columnWidths = new int[] { 60, 20, 20, 150 };

	private TreeColumn[] columns;

	private IMemento taskListMemento;

	public static final String columnWidthIdentifier = "org.eclipse.mylar.tasklist.ui.views.tasklist.columnwidth";

	public static final String tableSortIdentifier = "org.eclipse.mylar.tasklist.ui.views.tasklist.sortIndex";

	private static final int DEFAULT_SORT_DIRECTION = -1;

	private int sortIndex = 2;

	private int sortDirection = DEFAULT_SORT_DIRECTION;

	private TaskActivationHistory taskHistory = new TaskActivationHistory();

	/**
	 * True if the view should indicate that interaction monitoring is paused
	 */
	protected boolean isPaused = false;

	private final ITaskActivityListener TASK_REFERESH_LISTENER = new ITaskActivityListener() {

		public void taskActivated(ITask task) {
			if (task != null) {
				updateDescription(task);
				selectedAndFocusTask(task);
				filteredTree.indicateActiveTask(task);
			}
		}

		public void tasksActivated(List<ITask> tasks) {
			if (tasks.size() == 1) {
				taskActivated(tasks.get(0));
			}
		}

		public void taskDeactivated(ITask task) {
			updateDescription(null);
			filteredTree.indicateNoActiveTask();
		}

		public void localInfoChanged(ITask task) {
			refreshTask(task);
		}

		public void repositoryInfoChanged(ITask task) {
			refreshTask(task);
		}

		public void tasklistRead() {
			refresh(null);
		}

		public void taskListModified() {
			refresh(null);
		}
	};

	private TaskListTableLabelProvider taskListTableLabelProvider;

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
					MylarTaskListPlugin.setCurrentPriorityLevel(Task.PriorityLevel.P1);
					PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[0]);
					getViewer().refresh();
				}
			};
			P1.setEnabled(true);
			P1.setToolTipText(PRIORITY_LEVELS[0]);
			ActionContributionItem item = new ActionContributionItem(P1);
			item.fill(dropDownMenu, -1);

			Action P2 = new Action(PRIORITY_LEVELS[1], AS_CHECK_BOX) {
				@Override
				public void run() {
					MylarTaskListPlugin.setCurrentPriorityLevel(Task.PriorityLevel.P2);
					PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[1]);
					getViewer().refresh();
				}
			};
			P2.setEnabled(true);
			P2.setToolTipText(PRIORITY_LEVELS[1]);
			item = new ActionContributionItem(P2);
			item.fill(dropDownMenu, -1);

			Action P3 = new Action(PRIORITY_LEVELS[2], AS_CHECK_BOX) {
				@Override
				public void run() {
					MylarTaskListPlugin.setCurrentPriorityLevel(Task.PriorityLevel.P3);
					PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[2]);
					getViewer().refresh();
				}
			};
			P3.setEnabled(true);
			P3.setToolTipText(PRIORITY_LEVELS[2]);
			item = new ActionContributionItem(P3);
			item.fill(dropDownMenu, -1);

			Action P4 = new Action(PRIORITY_LEVELS[3], AS_CHECK_BOX) {
				@Override
				public void run() {
					MylarTaskListPlugin.setCurrentPriorityLevel(Task.PriorityLevel.P4);
					PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[3]);
					getViewer().refresh();
				}
			};
			P4.setEnabled(true);
			P4.setToolTipText(PRIORITY_LEVELS[3]);
			item = new ActionContributionItem(P4);
			item.fill(dropDownMenu, -1);

			Action P5 = new Action(PRIORITY_LEVELS[4], AS_CHECK_BOX) {
				@Override
				public void run() {
					MylarTaskListPlugin.setCurrentPriorityLevel(Task.PriorityLevel.P5);
					PRIORITY_FILTER.displayPrioritiesAbove(PRIORITY_LEVELS[4]);
					getViewer().refresh();
				}
			};
			P5.setEnabled(true);
			P5.setToolTipText(PRIORITY_LEVELS[4]);
			item = new ActionContributionItem(P5);
			item.fill(dropDownMenu, -1);

			String priority = MylarTaskListPlugin.getCurrentPriorityLevel();
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

	public static TaskListView openInActivePerspective() {
		try {
			return (TaskListView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
		} catch (Exception e) {
			return null;
		}
	}


	public TaskListView() {
		INSTANCE = this;
		MylarTaskListPlugin.getTaskListManager().addListener(TASK_REFERESH_LISTENER);
	}

	@Override
	public void dispose() {
		super.dispose();
		MylarTaskListPlugin.getTaskListManager().removeListener(TASK_REFERESH_LISTENER);
	}

	/**
	 * TODO: should be updated when view mode switches to fast and vice-versa
	 */
	private void updateDescription(ITask task) {
		if (getSite() == null || getSite().getPage() == null)
			return;

		IViewReference reference = getSite().getPage().findViewReference(ID);
		boolean shouldSetDescription = false;
		if (reference != null && reference.isFastView()) {
			shouldSetDescription = true;
		}

		if (task != null) {
			setTitleToolTip(PART_NAME + " (" + task.getDescription() + ")");
			if (shouldSetDescription) {
				setContentDescription(task.getDescription());
			} else {
				setContentDescription("");
			}
		} else {
			setTitleToolTip(PART_NAME);
			if (shouldSetDescription) {
				setContentDescription(LABEL_NO_TASKS);
			} else {
				setContentDescription("");
			}
		}
	}

	class TaskListCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			int columnIndex = Arrays.asList(columnNames).indexOf(property);
			if (columnIndex == 0 && element instanceof ITaskListElement) {
				return element instanceof ITask || element instanceof AbstractQueryHit;
				// return ((ITaskListElement) element).isActivatable();
			} else if (columnIndex == 2 && element instanceof ITask) {
				return ((ITask) element).isLocal();
			} else if (element instanceof ITaskListElement && isInRenameAction) {
				ITaskListElement taskListElement = (ITaskListElement) element;
				switch (columnIndex) {
				case 3:
					return taskListElement.isLocal();
				}
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			try {
				int columnIndex = Arrays.asList(columnNames).indexOf(property);
				if (element instanceof ITaskListElement) {
					final ITaskListElement taskListElement = (ITaskListElement) element;
					ITask task = null;
					if (taskListElement instanceof ITask) {
						task = (ITask) taskListElement;
					} else if (taskListElement instanceof AbstractQueryHit) {
						if (((AbstractQueryHit) taskListElement).getCorrespondingTask() != null) {
							task = ((AbstractQueryHit) taskListElement).getCorrespondingTask();
						}
					}
					switch (columnIndex) {
					case 0:
						if (task == null) {
							return Boolean.TRUE;
						} else {
							return new Boolean(task.isCompleted());
						}
					case 1:
						return "";
					case 2:
						String priorityString = taskListElement.getPriority().substring(1);
						int priorityInt = new Integer(priorityString);
						return priorityInt - 1;
					case 3:
						return taskListElement.getDescription();
					}
				} else if (element instanceof ITaskContainer) {
					ITaskContainer cat = (ITaskContainer) element;
					switch (columnIndex) {
					case 0:
						return new Boolean(false);
					case 1:
						return "";
					case 2:
						return "";
					case 3:
						return cat.getDescription();
					}
				} else if (element instanceof AbstractRepositoryQuery) {
					AbstractRepositoryQuery cat = (AbstractRepositoryQuery) element;
					switch (columnIndex) {
					case 0:
						return new Boolean(false);
					case 1:
						return "";
					case 2:
						return "";
					case 3:
						return cat.getDescription();
					}
				}
			} catch (Exception e) {
				MylarStatusHandler.log(e, e.getMessage());
			}
			return "";
		}

		public void modify(Object element, String property, Object value) {
			int columnIndex = -1;
			try {
				columnIndex = Arrays.asList(columnNames).indexOf(property);
				if (((TreeItem) element).getData() instanceof ITaskContainer) {
					ITaskContainer cat = (ITaskContainer) ((TreeItem) element).getData();
					switch (columnIndex) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						cat.setDescription(((String) value).trim());
						break;
					}
				} else if (((TreeItem) element).getData() instanceof AbstractRepositoryQuery) {
					AbstractRepositoryQuery cat = (AbstractRepositoryQuery) ((TreeItem) element).getData();
					switch (columnIndex) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						cat.setDescription(((String) value).trim());
						break;
					}
				} else if (((TreeItem) element).getData() instanceof ITaskListElement) {

					final ITaskListElement taskListElement = (ITaskListElement) ((TreeItem) element).getData();
					ITask task = null;
					if (taskListElement instanceof ITask) {
						task = (ITask) taskListElement;
					} else if (taskListElement instanceof AbstractQueryHit) {
						if (((AbstractQueryHit) taskListElement).getCorrespondingTask() != null) {
							task = ((AbstractQueryHit) taskListElement).getCorrespondingTask();
						}
					}
					switch (columnIndex) {
					case 0:
						if (taskListElement instanceof AbstractQueryHit) {
							task = ((AbstractQueryHit) taskListElement).getOrCreateCorrespondingTask();
						}
						if (task != null) {
							if (task.isActive()) {
								new TaskDeactivateAction().run();
								nextTaskAction.setEnabled(taskHistory.hasNext());
								previousTaskAction.setEnabled(taskHistory.hasPrevious());
							} else {
								new TaskActivateAction().run();
								addTaskToHistory(task);
							}
						}
						break;
					case 1:
						break;
					case 2:
						if (task.isLocal()) {
							Integer intVal = (Integer) value;
							task.setPriority("P" + (intVal + 1));
							MylarTaskListPlugin.getTaskListManager().notifyLocalInfoChanged(task);
						}
						break;
					case 3:
						if (task.isLocal()) {
							task.setDescription(((String) value).trim());
							// MylarTaskListPlugin.getTaskListManager().notifyTaskPropertyChanged(task,
							// columnNames[3]);
							MylarTaskListPlugin.getTaskListManager().notifyLocalInfoChanged(task);
						}
						break;
					}
				}
			} catch (Exception e) {
				MylarStatusHandler.fail(e, e.getMessage(), true);
			}
			getViewer().refresh();
		}
	}

	public void addTaskToHistory(ITask task) {
		if (!MylarTaskListPlugin.getDefault().isMultipleActiveTasksMode()) {
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
			if (o1 instanceof ITaskContainer && o2 instanceof ITaskContainer && ((ITaskContainer)o2).isArchive()) {
				return -1;
			} 
			
			if (o1 instanceof ITaskContainer && o2 instanceof ITask) {
				return 1;
			} 
			if (o1 instanceof ITaskContainer || o1 instanceof AbstractRepositoryQuery) {
				if (o2 instanceof ITaskContainer || o2 instanceof AbstractRepositoryQuery) {
					return sortDirection
							* ((ITaskListElement) o1).getDescription().compareTo(
									((ITaskListElement) o2).getDescription());
				} else {
					return -1;
				}
			} else if (o1 instanceof ITaskListElement) {
				if (o2 instanceof ITaskContainer || o2 instanceof AbstractRepositoryQuery) {
					return -1;
				} else if (o2 instanceof ITaskListElement) {
					ITaskListElement element1 = (ITaskListElement) o1;
					ITaskListElement element2 = (ITaskListElement) o2;

					if (column != null && column.equals(columnNames[1])) {
						return 0;
					} else if (column == columnNames[2]) {
						return sortDirection * element1.getPriority().compareTo(element2.getPriority());
					} else if (column == columnNames[3]) {
						String c1 = element1.getDescription();
						String c2 = element2.getDescription();
						try {
							return new Integer(c1).compareTo(new Integer(c2));
						} catch (Exception e) {
						}

						return sortDirection * c1.compareTo(c2);

					} else {
						return 0;
					}
				}
			} else {
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
			IMemento m = colMemento.createChild("col" + i);
			m.putInteger("width", columnWidths[i]);
		}

		IMemento sorter = memento.createChild(tableSortIdentifier);
		IMemento m = sorter.createChild("sorter");
		m.putInteger("sortIndex", sortIndex);
		m.putInteger("sortDirection", sortDirection);
		MylarTaskListPlugin.getDefault().getTaskListSaveManager().createTaskListBackupFile();

		if (MylarTaskListPlugin.getDefault() != null) {
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().saveTaskListAndContexts();
		}
	}

	private void restoreState() {
		if (taskListMemento != null) {
			IMemento taskListWidth = taskListMemento.getChild(columnWidthIdentifier);
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
			IMemento sorterMemento = taskListMemento.getChild(tableSortIdentifier);
			if (sorterMemento != null) {
				IMemento m = sorterMemento.getChild("sorter");
				if (m != null) {
					sortIndex = m.getInteger("sortIndex");
					Integer sortDirInt = m.getInteger("sortDirection");
					if (sortDirInt != null) {
						sortDirection = sortDirInt.intValue();
					}
				} else {
					sortIndex = 2;
					sortDirection = DEFAULT_SORT_DIRECTION;
				}

			} else {
				sortIndex = 2; // default priority
				sortDirection = DEFAULT_SORT_DIRECTION;
			}
			getViewer().setSorter(new TaskListTableSorter(columnNames[sortIndex]));
		}
		addFilter(PRIORITY_FILTER);
		// if (MylarTaskListPlugin.getDefault().isFilterInCompleteMode())
		// MylarTaskListPlugin.getTaskListManager().getTaskList().addFilter(inCompleteFilter);
		if (MylarTaskListPlugin.getDefault().isFilterCompleteMode())
			addFilter(COMPLETE_FILTER);
		if (MylarTaskListPlugin.getDefault().isMultipleActiveTasksMode()) {
			togglePreviousAction(false);
			toggleNextAction(false);
		}

		getViewer().refresh();
	}

	@Override
	public void createPartControl(Composite parent) {
		filteredTree = new TaskListFilteredTree(parent, SWT.MULTI | SWT.VERTICAL | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION, new TaskListPatternFilter());
		filteredTree.setInitialText("");

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
					sortDirection *= DEFAULT_SORT_DIRECTION;
					getViewer().setSorter(new TaskListTableSorter(columnNames[sortIndex]));
				}
			});
			columns[i].addControlListener(new ControlListener() {
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
		
		taskListTableLabelProvider = new TaskListTableLabelProvider(new TaskElementLabelProvider(), PlatformUI.getWorkbench()
				.getDecoratorManager().getLabelDecorator(), parent.getBackground());
		
		CellEditor[] editors = new CellEditor[columnNames.length];
		TextCellEditor textEditor = new TextCellEditor(getViewer().getTree());
		((Text) textEditor.getControl()).setOrientation(SWT.LEFT_TO_RIGHT);
		editors[0] = new CheckboxCellEditor();
		editors[1] = textEditor;
		editors[2] = new ComboBoxCellEditor(getViewer().getTree(), PRIORITY_LEVELS, SWT.READ_ONLY);		
//		editors[2] = new ImageTableCellEditor(getViewer().getTree(), getPirorityImages());
		editors[3] = textEditor;
		getViewer().setCellEditors(editors);
		getViewer().setCellModifier(new TaskListCellModifier());
		getViewer().setSorter(new TaskListTableSorter(columnNames[sortIndex]));

		drillDownAdapter = new DrillDownAdapter(getViewer());
		getViewer().setContentProvider(new TaskListContentProvider(this));
		getViewer().setLabelProvider(taskListTableLabelProvider);
		getViewer().setInput(getViewSite());
		getViewer().getTree().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F2 && e.stateMask == 0) {
					if (renameAction.isEnabled()) {
						renameAction.run();
					}
				} else if (e.keyCode == 'c' && e.stateMask == SWT.MOD1) {
					copyDescriptionAction.run();
				} else if (e.keyCode == SWT.DEL) {
					deleteAction.run();
				} else if (e.keyCode == SWT.INSERT) {
					newLocalTaskAction.run();
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		// HACK: shouldn't need to update explicitly
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedObject = ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
				if (selectedObject instanceof ITaskListElement) {
					updateActionEnablement(renameAction, (ITaskListElement) selectedObject);
				}
			}
		});

		makeActions();
		hookContextMenu();
		hookOpenAction();
		contributeToActionBars();

		TaskListToolTipHandler taskListToolTipHandler = new TaskListToolTipHandler(getViewer().getControl().getShell());
		taskListToolTipHandler.activateHoverHelp(getViewer().getControl());

		initDragAndDrop(parent);
		expandToActiveTasks();
		restoreState();

		List<ITask> activeTasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks();
		if (activeTasks.size() > 0) {
			updateDescription(activeTasks.get(0));
		}
	}

	@MylarWebRef(name = "Drag and drop article", url = "http://www.eclipse.org/articles/Article-Workbench-DND/drag_drop.html")
	private void initDragAndDrop(Composite parent) {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance(), PluginTransfer.getInstance() };

		getViewer().addDragSupport(DND.DROP_MOVE, types, new TaskListDragSourceListener(this));

		getViewer().addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, types, new TaskListDropAdapter(getViewer()));
	}

	void expandToActiveTasks() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				List<ITask> activeTasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks();
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
		manager.add(goUpAction);
		manager.add(collapseAll);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator(SEPARATOR_ID_REPORTS));
		manager.add(newLocalTaskAction);
		// manager.add(newCategoryAction);
		manager.add(new Separator());
		manager.add(filterCompleteTask);
		manager.add(filterOnPriority);
		manager.add(new Separator("navigation"));
		manager.add(previousTaskAction);
		manager.add(nextTaskAction);
		manager.add(new Separator(SEPARATOR_CONTEXT));
		// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillContextMenu(IMenuManager manager) {
		updateDrillDownActions();

		ITaskListElement element = null;

		final Object selectedObject = ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
		if (selectedObject instanceof ITaskListElement) {
			element = (ITaskListElement) selectedObject;
		}

		addAction(openTaskEditor, manager, element);
		if ((element instanceof ITask) || (element instanceof AbstractQueryHit)) {
			ITask task = null;
			if (element instanceof AbstractQueryHit) {
				task = ((AbstractQueryHit) element).getCorrespondingTask();
			} else {
				task = (ITask) element;
			}

			addAction(openUrlInExternal, manager, element);

			if (task != null) {
				if (task.isLocal()) {
					if (task.isCompleted()) {
						addAction(markCompleteAction, manager, element);
					} else {
						addAction(markIncompleteAction, manager, element);
					}
				}

				if (task.isActive()) {
					manager.add(deactivateAction);
				} else {
					manager.add(activateAction);
				}

//				if (!task.isLocal()) {
				addAction(removeFromCategoryAction, manager, element);
//				}
				addAction(deleteAction, manager, element);
			} else {
				manager.add(activateAction);
			}
		} else if (element instanceof ITaskContainer || element instanceof AbstractRepositoryQuery) {
			addAction(deleteAction, manager, element);
		}

		if ((element instanceof ITask && ((ITask) element).isLocal()) || element instanceof ITaskContainer
				|| element instanceof AbstractRepositoryQuery) {
			addAction(renameAction, manager, element);
		}
		if (element instanceof ITaskContainer) {
			manager.add(goIntoAction);
		}
		if (drilledIntoCategory != null) {
			manager.add(goUpAction);
		}

		manager.add(new Separator(SEPARATOR_LOCAL));
		manager.add(newCategoryAction);
		manager.add(newLocalTaskAction);
		manager.add(new Separator(SEPARATOR_REPORTS));

		manager.add(new Separator(SEPARATOR_CONTEXT));
		for (IDynamicSubMenuContributor contributor : MylarTaskListPlugin.getDefault().getDynamicMenuContributers()) {
			MenuManager subMenuManager = contributor.getSubMenuManager(this, (ITaskListElement) selectedObject);
			if (subMenuManager != null)
				addMenuManager(subMenuManager, manager, element);
		}

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void addMenuManager(IMenuManager menuToAdd, IMenuManager manager, ITaskListElement element) {
		if (element != null && element instanceof ITask) {
			manager.add(menuToAdd);
		}
	}

	private void addAction(Action action, IMenuManager manager, ITaskListElement element) {
		manager.add(action);
		if (element != null) {
			// ITaskHandler handler =
			// MylarTaskListPlugin.getDefault().getHandlerForElement(element);
			// if (handler != null) {
			// action.setEnabled(handler.enableAction(action, element));
			// } else {
			updateActionEnablement(action, element);
			// }
		}
	}

	/**
	 * Refactor out element
	 */
	private void updateActionEnablement(Action action, ITaskListElement element) {
		if (element instanceof ITask) {
			// if (action instanceof MarkTaskCompleteAction) {
			// if (element.isCompleted()) {
			// action.setEnabled(false);
			// } else {
			// action.setEnabled(true);
			// }
			// } else
			if (action instanceof OpenTaskInExternalBrowserAction) {
				if (((ITask) element).hasValidUrl()) {
					action.setEnabled(true);
				} else {
					action.setEnabled(false);
				}
				// } else if (action instanceof MarkTaskIncompleteAction) {
				// if (element.isCompleted()) {
				// action.setEnabled(true);
				// } else {
				// action.setEnabled(false);
				// }
			} else if (action instanceof DeleteAction) {
				action.setEnabled(true);
			} else if (action instanceof NewLocalTaskAction) {
				action.setEnabled(false);
			} else if (action instanceof OpenTaskListElementAction) {
				action.setEnabled(true);
			} else if (action instanceof CopyDescriptionAction) {
				action.setEnabled(true);
			} else if (action instanceof RenameAction) {
				action.setEnabled(true);
			}
		} else if (element instanceof ITaskContainer) {
			if (action instanceof MarkTaskCompleteAction) {
				action.setEnabled(false);
			} else if (action instanceof MarkTaskIncompleteAction) {
				action.setEnabled(false);
			} else if (action instanceof DeleteAction) {
				if (((ITaskContainer) element).isArchive())
					action.setEnabled(false);
				else
					action.setEnabled(true);
			} else if (action instanceof NewLocalTaskAction) {
				if (((ITaskContainer) element).isArchive())
					action.setEnabled(false);
				else
					action.setEnabled(true);
			} else if (action instanceof GoIntoAction) {
				TaskCategory cat = (TaskCategory) element;
				if (cat.getChildren().size() > 0) {
					action.setEnabled(true);
				} else {
					action.setEnabled(false);
				}
			} else if (action instanceof OpenTaskListElementAction) {
				action.setEnabled(true);
			} else if (action instanceof CopyDescriptionAction) {
				action.setEnabled(true);
			} else if (action instanceof RenameAction) {
				if (((ITaskContainer) element).isArchive())
					action.setEnabled(false);
				else
					action.setEnabled(true);
			}
		} else {
			action.setEnabled(true);
		}
		// if(!canEnableGoInto){
		// goIntoAction.setEnabled(false);
		// }
	}

	private void makeActions() {

		copyDescriptionAction = new CopyDescriptionAction(this);
		// workOffline = new WorkOfflineAction();

		goIntoAction = new GoIntoAction();
		goUpAction = new GoUpAction(drillDownAdapter);

		newLocalTaskAction = new NewLocalTaskAction(this);
		newCategoryAction = new NewCategoryAction(this);
		removeFromCategoryAction = new RemoveFromCategoryAction(this);
		renameAction = new RenameAction(this);

		deleteAction = new DeleteAction();
		collapseAll = new CollapseAllAction(this);
		// autoClose = new ManageEditorsAction();
		markIncompleteAction = new MarkTaskCompleteAction(this);
		markCompleteAction = new MarkTaskIncompleteAction(this);
		openTaskEditor = new OpenTaskListElementAction(this.getViewer());
		openUrlInExternal = new OpenTaskInExternalBrowserAction();
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
		return (MylarTaskListPlugin.getTaskListManager().getTaskForHandle(taskId, true) == null);
		// for (ITask task :
		// MylarTaskListPlugin.getTaskListManager().getTaskList().getRootTasks())
		// {
		// if (task.getHandle().equals(taskId)) {
		// return true;
		// }
		// }
		// for (TaskCategory cat :
		// MylarTaskListPlugin.getTaskListManager().getTaskList().getTaskCategories())
		// {
		// for (ITask task : cat.getChildren()) {
		// if (task.getHandle().equals(taskId)) {
		// return true;
		// }
		// }
		// }
		// return false;
	}

	private void hookOpenAction() {
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openTaskEditor.run();
			}
		});
	}

	// public void showMessage(String message) {
	// MessageDialog.openInformation(getViewer().getControl().getShell(),
	// "TaskList Message", message);
	// }

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		filteredTree.getViewer().getControl().setFocus();
	}

	public String getBugIdFromUser() {
		InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Enter Bugzilla ID", "Enter the Bugzilla ID: ", "", null);
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			return dialog.getValue();
		} else {
			return null;
		}
	}

	// public void notifyTaskDataChanged(ITask task) {
	// if (getViewer().getTree() != null && !getViewer().getTree().isDisposed())
	// {
	// getViewer().refresh();
	// expandToActiveTasks();
	// }
	// }

	public static TaskListView getDefault() {
		return INSTANCE;
	}

	public void refreshAndFocus() {
		getViewer().refresh();
		selectedAndFocusTask(MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTask());
	}

	public TreeViewer getViewer() {
		return filteredTree.getViewer();
	}

	public TaskCompleteFilter getCompleteFilter() {
		return COMPLETE_FILTER;
	}

	public TaskPriorityFilter getPriorityFilter() {
		return PRIORITY_FILTER;
	}

	public void addFilter(AbstractTaskFilter filter) {
		if (!filters.contains(filter))
			filters.add(filter);
	}

	public void removeFilter(AbstractTaskFilter filter) {
		filters.remove(filter);
	}

	public void updateDrillDownActions() {
		if (drillDownAdapter.canGoBack()) {
			goUpAction.setEnabled(true);
		} else {
			goUpAction.setEnabled(false);
		}
	}

	/**
	 * HACK: This is used for the copy action
	 */
	public Composite getDummyComposite() {
		return filteredTree;
	}

	private boolean isInRenameAction = false;

	public void setInRenameAction(boolean b) {
		isInRenameAction = b;
	}

	/**
	 * This method is for testing only
	 */
	public TaskActivationHistory getTaskActivationHistory() {
		return taskHistory;
	}

	public void goIntoCategory() {
		ISelection selection = getViewer().getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof ITaskContainer) {
				drilledIntoCategory = (ITaskContainer) element;
				drillDownAdapter.goInto();
				updateDrillDownActions();
			}
		}
	}

	public void goUpToRoot() {
		drilledIntoCategory = null;
		drillDownAdapter.goBack();
		updateDrillDownActions();
	}

	public ITask getSelectedTask() {
		ISelection selection = getViewer().getSelection();
		if (selection.isEmpty())
			return null;
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof ITask) {
				return (ITask) structuredSelection.getFirstElement();
			} else if (element instanceof AbstractQueryHit) {
				return ((AbstractQueryHit) element).getOrCreateCorrespondingTask();
			}
		}
		return null;
	}

	public void indicatePaused(boolean paused) {
		isPaused = paused;
		IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
		if (isPaused) {
			statusLineManager.setMessage(TaskListImages.getImage(TaskListImages.TASKLIST),
					"Mylar context capture paused");
		} else {
			statusLineManager.setMessage("");
		}
	}

	/**
	 * Show the shared data folder currently in use. Call with "" to turn off
	 * the indication. TODO: Need a better way to indicate paused and/or the
	 * shared folder
	 */
	public void indicateSharedFolder(String folderName) {
		if (folderName.equals("")) {
			if (isPaused) {
				setPartName("(paused) " + PART_NAME);
			} else {
				setPartName(PART_NAME);
			}
		} else {
			if (isPaused) {
				setPartName("(paused) " + folderName + " " + PART_NAME);
			} else {
				setPartName(folderName + " " + PART_NAME);
			}
		}

	}

	public ITaskContainer getDrilledIntoCategory() {
		return drilledIntoCategory;
	}

	
	public TaskListFilteredTree getFilteredTree() {
		return filteredTree;
	}
	

	public void selectedAndFocusTask(ITask task) {
		if (task == null)
			return;
		getViewer().setSelection(new StructuredSelection(task));
		// if no task exists, select the query hit if exists
		AbstractQueryHit hit = null;
		if (getViewer().getSelection().isEmpty()
				&& (hit = MylarTaskListPlugin.getTaskListManager().getTaskList().getQueryHitForHandle(
						task.getHandleIdentifier())) != null) {
			AbstractRepositoryQuery query = MylarTaskListPlugin.getTaskListManager().getTaskList().getQueryForHandle(
					task.getHandleIdentifier());
			getViewer().expandToLevel(query, 1);
			getViewer().setSelection(new StructuredSelection(hit), true);
		} else {
			if (task.getCategory() != null) {
				getViewer().expandToLevel(task.getCategory(), 1);
			}
		}
	}
	
	protected void refreshTask(ITask task) {
		refresh(task);
		if (task.getCategory() == null || task.getCategory().getHandleIdentifier().equals(TaskList.LABEL_ARCHIVE)) {
			refresh(null);
		} else {
			refresh(task.getCategory());
		}
		
		Set<AbstractQueryHit> hits = MylarTaskListPlugin.getTaskListManager().getTaskList().getQueryHitsForHandle(task.getHandleIdentifier());
		for (AbstractQueryHit hit : hits) {
			refresh(hit);			
		}
		
	}
	
	private void refresh(final ITaskListElement element) {
		if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (getViewer().getControl() != null && !getViewer().getControl().isDisposed()) {
						if (element == null) {
							// getViewer().getControl().setRedraw(false);
							// getViewer().refresh();
							filteredTree.textChanged(0);
							// getViewer().getControl().setRedraw(true);
						} else {
							// getViewer().getControl().setRedraw(false);
							getViewer().refresh(element, true);
							// getViewer().getControl().setRedraw(true);
						}
					}
				}
			});
		}
	}
	
	
	public Image[] getPirorityImages() {
		Image[] images = new Image[Task.PriorityLevel.values().length];
		for (int i = 0; i < Task.PriorityLevel.values().length; i++) {
			images[i] = taskListTableLabelProvider.getImageForPriority(Task.PriorityLevel.values()[i]);
		}
		return images;
	}
}
