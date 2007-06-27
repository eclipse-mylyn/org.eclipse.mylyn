/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UnfiledCategory;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TaskArchiveFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskCompletionFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TaskListPatternFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskPriorityFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.TaskWorkingSetFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskHistoryDropDownAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CollapseAllAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ExpandAllAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.FilterArchiveContainerAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.FilterCompletedTasksAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.FilterSubTasksAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.GoIntoAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.GoUpAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.LinkWithEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskCompleteAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskIncompleteAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTasksUiPreferencesAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.PresentationDropDownSelectionAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.RemoveFromCategoryAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.RenameAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeAutomaticallyAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskListElementPropertiesAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListTableSorter.SortByIndex;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewLocalTaskWizard;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 * @author Eugene Kuleshov
 */
public class TaskListView extends ViewPart implements IPropertyChangeListener {

	private static final String PRESENTATION_SCHEDULED = "Scheduled";

	public static final String ID = "org.eclipse.mylyn.tasks.ui.views.tasks";

	public static final String LABEL_VIEW = "Task List";

	private static final String MEMENTO_KEY_SORT_DIRECTION = "sortDirection";

	private static final String MEMENTO_KEY_SORTER = "sorter";

	private static final String MEMENTO_KEY_SORT_INDEX = "sortIndex";

	private static final String MEMENTO_LINK_WITH_EDITOR = "linkWithEditor";

	private static final String ID_SEPARATOR_NEW = "new";

	private static final String ID_SEPARATOR_CONTEXT = "context";

	public static final String ID_SEPARATOR_TASKS = "tasks";

	private static final String ID_SEPARATOR_FILTERS = "filters";

	private static final String ID_SEPARATOR_REPOSITORY = "repository";

	private static final String LABEL_NO_TASKS = "no task active";

	static final String[] PRIORITY_LEVELS = { PriorityLevel.P1.toString(), PriorityLevel.P2.toString(),
			PriorityLevel.P3.toString(), PriorityLevel.P4.toString(), PriorityLevel.P5.toString() };

	public static final String[] PRIORITY_LEVEL_DESCRIPTIONS = { PriorityLevel.P1.getDescription(),
			PriorityLevel.P2.getDescription(), PriorityLevel.P3.getDescription(), PriorityLevel.P4.getDescription(),
			PriorityLevel.P5.getDescription() };

	private static final String PART_NAME = "Task List";

	private boolean focusedMode = false;

	private boolean linkWithEditor;

	private TaskListCellModifier taskListCellModifier = new TaskListCellModifier(this);

	private IThemeManager themeManager;

	private TaskListFilteredTree filteredTree;

	private DrillDownAdapter drillDownAdapter;

	private AbstractTaskContainer drilledIntoCategory = null;

	private GoIntoAction goIntoAction;

	private GoUpAction goUpAction;

	private CopyTaskDetailsAction copyDetailsAction;

	private OpenTaskListElementAction openAction;

	private TaskListElementPropertiesAction propertiesAction;

	private OpenWithBrowserAction openWithBrowser;

	//private NewLocalTaskAction newLocalTaskAction;

	private RenameAction renameAction;

	private CollapseAllAction collapseAll;

	private ExpandAllAction expandAll;

	private DeleteAction deleteAction;

	private RemoveFromCategoryAction removeFromCategoryAction;

	private TaskActivateAction activateAction = new TaskActivateAction();

	private TaskDeactivateAction deactivateAction = new TaskDeactivateAction();

	private FilterCompletedTasksAction filterCompleteTask;

	private FilterSubTasksAction showSubTasksAction;

	private SynchronizeAutomaticallyAction synchronizeAutomatically;

	private OpenTasksUiPreferencesAction openPreferencesAction;

	private FilterArchiveContainerAction filterArchiveCategory;

	private PriorityDropDownAction filterOnPriorityAction;

	private SortyByDropDownAction sortByAction;

	ActivateTaskHistoryDropDownAction previousTaskAction;

	private PresentationDropDownSelectionAction presentationDropDownSelectionAction;

	private LinkWithEditorAction linkWithEditorAction;

	private TaskPriorityFilter filterPriority = new TaskPriorityFilter();

	private TaskCompletionFilter filterComplete = new TaskCompletionFilter();

	private TaskArchiveFilter filterArchive = new TaskArchiveFilter();

	private TaskWorkingSetFilter filterWorkingSet = new TaskWorkingSetFilter();

	private Set<AbstractTaskListFilter> filters = new HashSet<AbstractTaskListFilter>();

	protected String[] columnNames = new String[] { "Summary" };

	protected int[] columnWidths = new int[] { 200 };

	private TreeColumn[] columns;

	private IMemento taskListMemento;

	public static final String columnWidthIdentifier = "org.eclipse.mylyn.tasklist.ui.views.tasklist.columnwidth";

	public static final String tableSortIdentifier = "org.eclipse.mylyn.tasklist.ui.views.tasklist.sortIndex";

	private static final int DEFAULT_SORT_DIRECTION = 1;

	private SortByIndex sortByIndex = SortByIndex.PRIORITY;

	private ITaskListPresentation currentPresentation;

	private TaskTableLabelProvider taskListTableLabelProvider;

	private TaskListTableSorter tableSorter;

	int sortDirection = DEFAULT_SORT_DIRECTION;

	private Color categoryGradientStart;

	private Color categoryGradientEnd;

	private final static int MAX_SELECTION_HISTORY_SIZE = 10;

	private LinkedHashMap<String, IStructuredSelection> lastSelectionByTaskHandle = new LinkedHashMap<String, IStructuredSelection>(
			MAX_SELECTION_HISTORY_SIZE);

	private ITaskListPresentation catagorizedPresentation = new ITaskListPresentation() {

		public IStructuredContentProvider getContentProvider() {
			return new TaskListContentProvider(TaskListView.this);
		}

		public String getPresentationName() {
			return "Categorized";
		}

		public ImageDescriptor getImageDescriptor() {
			return TasksUiImages.CATEGORY;
		}
	};

	// TODO: Use extension point
	private ITaskListPresentation scheduledPresentation = new ITaskListPresentation() {

		public IStructuredContentProvider getContentProvider() {
			return new TaskScheduleContentProvider(TaskListView.this, TasksUiPlugin.getTaskListManager());
		}

		public String getPresentationName() {
			return PRESENTATION_SCHEDULED;
		}

		public ImageDescriptor getImageDescriptor() {
			return TasksUiImages.CALENDAR;
		}
	};

	/**
	 * True if the view should indicate that interaction monitoring is paused
	 */
	protected boolean isPaused = false;

	boolean synchronizationOverlaid = false;

	private final Listener CATEGORY_GRADIENT_DRAWER = new Listener() {
		public void handleEvent(Event event) {
			if (event.item.getData() instanceof AbstractTaskContainer
					&& !(event.item.getData() instanceof AbstractTask)) {
				Scrollable scrollable = (Scrollable) event.widget;
				GC gc = event.gc;

				Rectangle area = scrollable.getClientArea();
				Rectangle rect = event.getBounds();

				/* Paint the selection beyond the end of last column */
				expandRegion(event, scrollable, gc, area);

				/* Draw Gradient Rectangle */
				Color oldForeground = gc.getForeground();
				Color oldBackground = gc.getBackground();

				gc.setForeground(categoryGradientEnd);
				gc.drawLine(0, rect.y, area.width, rect.y);

				gc.setForeground(categoryGradientStart);
				gc.setBackground(categoryGradientEnd);

				// gc.setForeground(categoryGradientStart);
				// gc.setBackground(categoryGradientEnd);
				// gc.setForeground(new Color(Display.getCurrent(), 255, 0, 0));

				gc.fillGradientRectangle(0, rect.y + 1, area.width, rect.height, true);

				/* Bottom Line */
				// gc.setForeground();
				gc.setForeground(categoryGradientEnd);
				gc.drawLine(0, rect.y + rect.height - 1, area.width, rect.y + rect.height - 1);

				gc.setForeground(oldForeground);
				gc.setBackground(oldBackground);
				/* Mark as Background being handled */
				event.detail &= ~SWT.BACKGROUND;
			}
		}

		private void expandRegion(Event event, Scrollable scrollable, GC gc, Rectangle area) {
			int columnCount;
			if (scrollable instanceof Table)
				columnCount = ((Table) scrollable).getColumnCount();
			else
				columnCount = ((Tree) scrollable).getColumnCount();

			if (event.index == columnCount - 1 || columnCount == 0) {
				int width = area.x + area.width - event.x;
				if (width > 0) {
					Region region = new Region();
					gc.getClipping(region);
					region.add(event.x, event.y, width, event.height);
					gc.setClipping(region);
					region.dispose();
				}
			}
		}
	};

	private boolean gradientListenerAdded = false;

	private final ITaskActivityListener TASK_ACTIVITY_LISTENER = new ITaskActivityListener() {
		public void taskActivated(final AbstractTask task) {
			if (task != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						updateDescription(task);
						selectedAndFocusTask(task);
						filteredTree.indicateActiveTask(task);
					}
				});
			}
		}

		public void taskDeactivated(final AbstractTask task) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(task);
					updateDescription(null);
					filteredTree.indicateNoActiveTask();
				}
			});
		}

		public void activityChanged(final ScheduledTaskContainer week) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (getCurrentPresentation().getPresentationName().equals(
							scheduledPresentation.getPresentationName())) {
						refresh(week);
					}
				}
			});
		}

		public void taskListRead() {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(null);
				}
			});
		}
	};

	private final ITaskListChangeListener TASK_REFERESH_LISTENER = new ITaskListChangeListener() {

		public void containersChanged(final Set<TaskContainerDelta> containers) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					for (TaskContainerDelta taskContainerDelta : containers) {
						if (getCurrentPresentation().getPresentationName().equals(
								scheduledPresentation.getPresentationName())) {
							// TODO: implement refresh policy for scheduled presentation
							refresh(null);
						} else {
							if (taskContainerDelta.getContainer() instanceof AbstractTask) {
								AbstractTask task = (AbstractTask) taskContainerDelta.getContainer();
								switch (taskContainerDelta.getKind()) {
								case ROOT:
									refresh(null);
									break;
								case ADDED:
									refresh(null);
									break;
								case REMOVED:
									refresh(null);
									break;
								default:
									// TODO: move logic into deltas
									refresh(task);
									Set<AbstractTaskContainer> containers = new HashSet<AbstractTaskContainer>(
											TasksUiPlugin.getTaskListManager().getTaskList().getQueriesForHandle(
													task.getHandleIdentifier()));
									containers.addAll(task.getParentContainers());
									containers.add(TasksUiPlugin.getTaskListManager()
											.getTaskList()
											.getArchiveContainer());
									containers.add(TasksUiPlugin.getTaskListManager()
											.getTaskList()
											.getDefaultCategory());
									for (AbstractTaskContainer container : containers) {
										refresh(container);
									}
									break;
								}
							} else { // category or query
								switch (taskContainerDelta.getKind()) {
								case ROOT:
									refresh(null);
									break;
								case ADDED:
									refresh(null);
									break;
								case REMOVED:
									refresh(null);
									break;
								default:
									if (taskContainerDelta.getContainer().equals(
											TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory())) {
										refresh(null);
									} else {
										refresh(taskContainerDelta.getContainer());
									}
								}
							}
						}
					}
				}
			});
		}
	};

	private final IPropertyChangeListener THEME_CHANGE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(IThemeManager.CHANGE_CURRENT_THEME)
					|| TaskListColorsAndFonts.isTaskListTheme(event.getProperty())) {
				configureGradientColors();
				taskListTableLabelProvider.setCategoryBackgroundColor(themeManager.getCurrentTheme()
						.getColorRegistry()
						.get(TaskListColorsAndFonts.THEME_COLOR_TASKLIST_CATEGORY));
				getViewer().refresh();
			}
		}
	};

	private void configureGradientColors() {
		categoryGradientStart = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_CATEGORY_GRADIENT_START);
		categoryGradientEnd = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_CATEGORY_GRADIENT_END);

		boolean customized = true;
		if (categoryGradientStart != null && categoryGradientStart.getRed() == 240
				&& categoryGradientStart.getGreen() == 240 && categoryGradientStart.getBlue() == 240
				&& categoryGradientEnd != null && categoryGradientEnd.getRed() == 220
				&& categoryGradientEnd.getGreen() == 220 && categoryGradientEnd.getBlue() == 220) {
			customized = false;
		}

		if (gradientListenerAdded == false && categoryGradientStart != null
				&& !categoryGradientStart.equals(categoryGradientEnd)) {
			getViewer().getTree().addListener(SWT.EraseItem, CATEGORY_GRADIENT_DRAWER);
			gradientListenerAdded = true;
			if (!customized) {
				// Set parent-based colors
				Color parentBackground = getViewer().getTree().getParent().getBackground();
				double GRADIENT_TOP = 1.05;// 1.02;
				double GRADIENT_BOTTOM = .995;// 1.035;

				int red = Math.min(255, (int) (parentBackground.getRed() * GRADIENT_TOP));
				int green = Math.min(255, (int) (parentBackground.getGreen() * GRADIENT_TOP));
				int blue = Math.min(255, (int) (parentBackground.getBlue() * GRADIENT_TOP));

				try {
					categoryGradientStart = new Color(Display.getDefault(), red, green, blue);
				} catch (Exception e) {
					categoryGradientStart = getViewer().getTree().getParent().getBackground();
					StatusHandler.fail(e, "Could not set color: " + red + ", " + green + ", " + blue, false);
				}
				red = Math.max(0, (int) (parentBackground.getRed() / GRADIENT_BOTTOM));
				green = Math.max(0, (int) (parentBackground.getGreen() / GRADIENT_BOTTOM));
				blue = Math.max(0, (int) (parentBackground.getBlue() / GRADIENT_BOTTOM));
				if (red > 255) {
					red = 255;
				}
				try {
					categoryGradientEnd = new Color(Display.getDefault(), red, green, blue);
				} catch (Exception e) {
					categoryGradientStart = getViewer().getTree().getParent().getBackground();
					StatusHandler.fail(e, "Could not set color: " + red + ", " + green + ", " + blue, false);
				}
			}
		} else if (categoryGradientStart != null && categoryGradientStart.equals(categoryGradientEnd)) {
			getViewer().getTree().removeListener(SWT.EraseItem, CATEGORY_GRADIENT_DRAWER);
			gradientListenerAdded = false;
		}
	}

	public static TaskListView getFromActivePerspective() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage != null) {
			IViewPart view = activePage.findView(ID);
			if (view instanceof TaskListView) {
				return (TaskListView) view;
			}
		}
		return null;
	}

	public static TaskListView openInActivePerspective() {
		try {
			return (TaskListView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
		} catch (Exception e) {
			StatusHandler.fail(e, "Could not show Task List view", false);
			e.printStackTrace();
			return null;
		}
	}

	public TaskListView() {
		TasksUiPlugin.getTaskListManager().addActivityListener(TASK_ACTIVITY_LISTENER);
		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(TASK_REFERESH_LISTENER);

		PlatformUI.getWorkbench().getWorkingSetManager().addPropertyChangeListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		TasksUiPlugin.getTaskListManager().getTaskList().removeChangeListener(TASK_REFERESH_LISTENER);
		TasksUiPlugin.getTaskListManager().removeActivityListener(TASK_ACTIVITY_LISTENER);

		PlatformUI.getWorkbench().getWorkingSetManager().removePropertyChangeListener(this);

		final IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		if (themeManager != null) {
			themeManager.removePropertyChangeListener(THEME_CHANGE_LISTENER);
		}

		categoryGradientStart.dispose();
		categoryGradientEnd.dispose();
	}

	private void updateDescription() {
		List<AbstractTask> activeTasks = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTasks();
		if (activeTasks.size() > 0) {
			updateDescription(activeTasks.get(0));
		} else {
			updateDescription(null);
		}
	}

	private void updateDescription(AbstractTask task) {
		if (getSite() == null || getSite().getPage() == null)
			return;

		IViewReference reference = getSite().getPage().findViewReference(ID);
		boolean shouldSetDescription = false;
		if (reference != null && reference.isFastView() && !getSite().getPage().isPartVisible(this)) {
			shouldSetDescription = true;
		}

		if (task != null) {
			setTitleToolTip(PART_NAME + " (" + task.getSummary() + ")");
			if (shouldSetDescription) {
				setContentDescription(task.getSummary());
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

	public void addTaskToHistory(AbstractTask task) {
		TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(task);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.taskListMemento = memento;
	}

	@Override
	public void saveState(IMemento memento) {
		IMemento sorter = memento.createChild(tableSortIdentifier);
		IMemento m = sorter.createChild(MEMENTO_KEY_SORTER);
		switch (sortByIndex) {
		case SUMMARY:
			m.putInteger(MEMENTO_KEY_SORT_INDEX, 1);
			break;
		case DATE_CREATED:
			m.putInteger(MEMENTO_KEY_SORT_INDEX, 2);
			break;
		default:
			m.putInteger(MEMENTO_KEY_SORT_INDEX, 0);
		}
		m.putInteger(MEMENTO_KEY_SORT_DIRECTION, sortDirection);

		memento.putString(MEMENTO_LINK_WITH_EDITOR, Boolean.toString(linkWithEditor));
	}

	private void restoreState() {
		if (taskListMemento != null) {
			IMemento sorterMemento = taskListMemento.getChild(tableSortIdentifier);
			int restoredSortIndex = 0;
			if (sorterMemento != null) {
				IMemento m = sorterMemento.getChild(MEMENTO_KEY_SORTER);
				if (m != null) {
					Integer sortIndexInt = m.getInteger(MEMENTO_KEY_SORT_INDEX);
					if (sortIndexInt != null) {
						restoredSortIndex = sortIndexInt.intValue();
					}
					Integer sortDirInt = m.getInteger(MEMENTO_KEY_SORT_DIRECTION);
					if (sortDirInt != null) {
						sortDirection = sortDirInt.intValue();
					}
				} else {
					sortDirection = DEFAULT_SORT_DIRECTION;
				}
			} else {
				sortDirection = DEFAULT_SORT_DIRECTION;
			}
			switch (restoredSortIndex) {
			case 1:
				this.sortByIndex = SortByIndex.SUMMARY;
				break;
			case 2:
				this.sortByIndex = SortByIndex.DATE_CREATED;
				break;
			default:
				this.sortByIndex = SortByIndex.PRIORITY;
			}
		}

		addFilter(filterWorkingSet);
		addFilter(filterPriority);
		if (TasksUiPlugin.getDefault().getPreferenceStore().contains(TasksUiPreferenceConstants.FILTER_COMPLETE_MODE)) {
			addFilter(filterComplete);
		}

		if (TasksUiPlugin.getDefault().getPreferenceStore().contains(TasksUiPreferenceConstants.FILTER_ARCHIVE_MODE)) {
			addFilter(filterArchive);
		}

		// Restore "link with editor" value; by default true
		boolean linkValue = true;
		if (taskListMemento != null && taskListMemento.getString(MEMENTO_LINK_WITH_EDITOR) != null) {
			linkValue = Boolean.parseBoolean(taskListMemento.getString(MEMENTO_LINK_WITH_EDITOR));
		}
		setLinkWithEditor(linkValue);

		getViewer().setSorter(new TaskListTableSorter(this, sortByIndex));
		getViewer().refresh();
	}

	@Override
	public void createPartControl(Composite parent) {
		themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		themeManager.addPropertyChangeListener(THEME_CHANGE_LISTENER);

		filteredTree = new TaskListFilteredTree(parent, SWT.MULTI | SWT.VERTICAL | /* SWT.H_SCROLL | */SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION, new TaskListPatternFilter());

		getViewer().getTree().setHeaderVisible(false);
		getViewer().setUseHashlookup(true);

		configureColumns(columnNames, columnWidths);

		final IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		Color categoryBackground = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASKLIST_CATEGORY);
		taskListTableLabelProvider = new TaskTableLabelProvider(new TaskElementLabelProvider(true),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), categoryBackground);
		getViewer().setLabelProvider(taskListTableLabelProvider);

		CellEditor[] editors = new CellEditor[columnNames.length];
		TextCellEditor textEditor = new TextCellEditor(getViewer().getTree());
		((Text) textEditor.getControl()).setOrientation(SWT.LEFT_TO_RIGHT);
		editors[0] = textEditor;
		// editors[1] = new ComboBoxCellEditor(getViewer().getTree(),
		// editors[2] = new CheckboxCellEditor();

		getViewer().setCellEditors(editors);
		getViewer().setCellModifier(taskListCellModifier);

		tableSorter = new TaskListTableSorter(this, TaskListTableSorter.SortByIndex.PRIORITY);
		getViewer().setSorter(tableSorter);

		applyPresentation(catagorizedPresentation);

		drillDownAdapter = new DrillDownAdapter(getViewer());
		getViewer().setInput(getViewSite());

		final int activationImageOffset = 20;
		CustomTaskListDecorationDrawer customDrawer = new CustomTaskListDecorationDrawer(this, activationImageOffset);
		getViewer().getTree().addListener(SWT.MeasureItem, customDrawer);
		getViewer().getTree().addListener(SWT.EraseItem, customDrawer);
		getViewer().getTree().addListener(SWT.PaintItem, customDrawer);

		getViewer().getTree().addMouseListener(new MouseListener() {

			public void mouseDown(MouseEvent e) {
				// NOTE: need e.x offset for Linux/GTK, which does not see
				// left-aligned items in tree
				Object selectedNode = ((Tree) e.widget).getItem(new Point(e.x + 70, e.y));
				if (selectedNode instanceof TreeItem) {
					Object selectedObject = ((TreeItem) selectedNode).getData();
					if (selectedObject instanceof AbstractTask) {
						if (e.x > activationImageOffset && e.x < activationImageOffset + 13) {
							taskListCellModifier.toggleTaskActivation((AbstractTaskContainer) selectedObject);
						}
					}
				}
			}

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}

			public void mouseUp(MouseEvent e) {
				// ignore
			}

		});

		getViewer().getTree().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.INSERT) {
					new NewLocalTaskWizard().performFinish();
				} else if (e.keyCode == SWT.F2 && e.stateMask == 0) {
					if (renameAction.isEnabled()) {
						renameAction.run();
					}
				} else if ((e.keyCode & SWT.KEYCODE_BIT) != 0) {
					// Do nothing here since it is key code
				} else if (e.keyCode == 'c' && e.stateMask == SWT.MOD1) {
					copyDetailsAction.run();
				} else if (e.keyCode == SWT.DEL) {
					deleteAction.run();
				} else if (e.keyCode == 'f' && e.stateMask == SWT.MOD1) {
					filteredTree.getFilterControl().setFocus();
				} else if (e.stateMask == 0) {
					if (Character.isLetter((char) e.keyCode) || Character.isDigit((char) e.keyCode)) {
						String string = new Character((char) e.keyCode).toString();
						filteredTree.getFilterControl().setText(string);
						filteredTree.getFilterControl().setSelection(1, 1);
						filteredTree.getFilterControl().setFocus();
					}
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		getViewer().addTreeListener(new ITreeViewerListener() {

			public void treeCollapsed(final TreeExpansionEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						getViewer().refresh(event.getElement());
					}
				});
			}

			public void treeExpanded(final TreeExpansionEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						getViewer().refresh(event.getElement());
					}
				});
			}
		});

		// HACK: shouldn't need to update explicitly
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedObject = ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
				if (selectedObject instanceof AbstractTaskContainer) {
					updateActionEnablement(renameAction, (AbstractTaskContainer) selectedObject);
				}
			}
		});

		makeActions();
		hookContextMenu();
		hookOpenAction();
		contributeToActionBars();

		TaskListToolTipHandler taskListToolTipHandler = new TaskListToolTipHandler();
		taskListToolTipHandler.activateHoverHelp(getViewer().getControl());

		// Set to empty string to disable native tooltips (windows only?)
		// bug#160897
		// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg29614.html
		getViewer().getTree().setToolTipText("");

		configureGradientColors();

		initDragAndDrop(parent);
		expandToActiveTasks();
		restoreState();

		updateDescription();

		getSite().setSelectionProvider(getViewer());
		getSite().getPage().addPartListener(editorListener);
	}

	public void applyPresentation(ITaskListPresentation presentation) {
		try {
			getViewer().getControl().setRedraw(false);
			if (!filteredTree.getFilterControl().getText().equals("")) {
				filteredTree.getFilterControl().setText("");
			}
			if (presentation.getPresentationName().equals(PRESENTATION_SCHEDULED)) {
				TasksUiPlugin.getTaskListManager().parseFutureReminders();
			}
			getViewer().setContentProvider(presentation.getContentProvider());
			refreshAndFocus(isFocusedMode());

			currentPresentation = presentation;
		} finally {
			getViewer().getControl().setRedraw(true);
		}
	}

	public ITaskListPresentation getCurrentPresentation() {
		return currentPresentation;
	}

	private void configureColumns(final String[] columnNames, final int[] columnWidths) {
		TreeColumnLayout layout = (TreeColumnLayout) getViewer().getTree().getParent().getLayout();
		getViewer().setColumnProperties(columnNames);
		columns = new TreeColumn[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = new TreeColumn(getViewer().getTree(), 0);
			columns[i].setText(columnNames[i]);

			if (i == 0) {
				layout.setColumnData(columns[i], new ColumnWeightData(100));
			} else {
				layout.setColumnData(columns[i], new ColumnPixelData(columnWidths[i]));
			}

// final int index = i;
			columns[i].addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
// sortByIndex = index;
					sortDirection *= DEFAULT_SORT_DIRECTION;
// tableSorter.setColumn(columnNames[sortByIndex]);
					getViewer().refresh(false);
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
	}

	/**
	 * Tracks editor activation and jump to corresponding task, if applicable
	 */
	private IPartListener editorListener = new IPartListener() {

		private void jumpToEditor(IWorkbenchPart part) {
			if (!linkWithEditor || !(part instanceof IEditorPart)) {
				return;
			}
			jumpToEditorTask((IEditorPart) part);
		}

		public void partActivated(IWorkbenchPart part) {
			if (part == TaskListView.this) {
				updateDescription();
			} else {
				jumpToEditor(part);
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
			if (part == TaskListView.this) {
				IViewReference reference = getSite().getPage().findViewReference(ID);
				if (reference != null && reference.isFastView()) {
					updateDescription();
				}
			}
		}

		public void partOpened(IWorkbenchPart part) {
		}
	};

	private void initDragAndDrop(Composite parent) {
		Transfer[] dragTypes = new Transfer[] { TaskTransfer.getInstance(), TextTransfer.getInstance(),
				FileTransfer.getInstance() };

		Transfer[] dropTypes = new Transfer[] { TaskTransfer.getInstance(), TextTransfer.getInstance(),
				FileTransfer.getInstance(), // PluginTransfer.getInstance(),
				RTFTransfer.getInstance() };

		getViewer().addDragSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK, dragTypes,
				new TaskListDragSourceListener(this));
		getViewer().addDropSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_DEFAULT, dropTypes,
				new TaskListDropAdapter(getViewer()));
	}

	void expandToActiveTasks() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				List<AbstractTask> activeTasks = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTasks();
				for (AbstractTask t : activeTasks) {
					getViewer().expandToLevel(t, 0);
				}
			}
		});
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TaskListView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuManager.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, getViewer());
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
		manager.add(expandAll);
		manager.add(new Separator(ID_SEPARATOR_FILTERS));
		manager.add(sortByAction);
		manager.add(filterOnPriorityAction);
		manager.add(filterCompleteTask);
		manager.add(filterArchiveCategory);
		manager.add(showSubTasksAction);

		manager.add(new Separator(ID_SEPARATOR_TASKS));
		manager.add(synchronizeAutomatically);

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				filterOnPriorityAction.updateCheckedState();
			}
		});

//		manager.add(new Separator());
		manager.add(linkWithEditorAction);
		manager.add(new Separator());
		manager.add(openPreferencesAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator(ID_SEPARATOR_NEW));
//		manager.add(new Separator(ID_SEPARATOR_NAVIGATION));
		manager.add(presentationDropDownSelectionAction);
//		manager.add(previousTaskAction);
		manager.add(new Separator(ID_SEPARATOR_CONTEXT));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/*
	 * TODO: clean up, consider relying on extension points for groups
	 */
	private void fillContextMenu(IMenuManager manager) {
		updateDrillDownActions();
		AbstractTaskContainer element = null;

		final Object firstSelectedObject = ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
		if (firstSelectedObject instanceof AbstractTaskContainer) {
			element = (AbstractTaskContainer) firstSelectedObject;
		}
		List<AbstractTaskContainer> selectedElements = new ArrayList<AbstractTaskContainer>();
		for (Iterator<?> i = ((IStructuredSelection) getViewer().getSelection()).iterator(); i.hasNext();) {
			Object object = i.next();
			if (object instanceof AbstractTaskContainer) {
				selectedElements.add((AbstractTaskContainer) object);
			}
		}
		AbstractTask task = null;
		if ((element instanceof AbstractTask)) {
			task = (AbstractTask) element;
		}

		manager.add(new Separator(ID_SEPARATOR_NEW));
		manager.add(new Separator());

		Map<String, List<IDynamicSubMenuContributor>> dynamicMenuMap = TasksUiPlugin.getDefault().getDynamicMenuMap();

		if (element instanceof AbstractTask) {
			addAction(openAction, manager, element);
		}
		addAction(openWithBrowser, manager, element);
		if (task != null) {
			if (task.isActive()) {
				manager.add(deactivateAction);
			} else {
				manager.add(activateAction);
			}
		}

		manager.add(new Separator());

		for (String menuPath : dynamicMenuMap.keySet()) {
			if (!ID_SEPARATOR_CONTEXT.equals(menuPath)) {
				for (IDynamicSubMenuContributor contributor : dynamicMenuMap.get(menuPath)) {
					MenuManager subMenuManager = contributor.getSubMenuManager(selectedElements);
					if (subMenuManager != null) {
						addMenuManager(subMenuManager, manager, element);
					}
				}
			}
		}
		manager.add(new Separator());

		addAction(copyDetailsAction, manager, element);
		if (task != null) {
			addAction(removeFromCategoryAction, manager, element);
		}
		addAction(deleteAction, manager, element);
		if (!(element instanceof AbstractTask)) {
			addAction(renameAction, manager, element);
		}

		if (element != null && !(element instanceof AbstractTask)) {
			manager.add(goIntoAction);
		}
		if (drilledIntoCategory != null) {
			manager.add(goUpAction);
		}
		manager.add(new Separator(ID_SEPARATOR_CONTEXT));

		if (element instanceof AbstractTask) {
			for (String menuPath : dynamicMenuMap.keySet()) {
				if (ID_SEPARATOR_CONTEXT.equals(menuPath)) {
					for (IDynamicSubMenuContributor contributor : dynamicMenuMap.get(menuPath)) {
						MenuManager subMenuManager = contributor.getSubMenuManager(selectedElements);
						if (subMenuManager != null) {
							addMenuManager(subMenuManager, manager, element);
						}
					}
				}
			}
		}

		manager.add(new Separator(ID_SEPARATOR_REPOSITORY));

		if (element instanceof AbstractRepositoryQuery || element instanceof TaskCategory) {
			manager.add(new Separator());
			addAction(propertiesAction, manager, element);
		}

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void addMenuManager(IMenuManager menuToAdd, IMenuManager manager, AbstractTaskContainer element) {
		if ((element instanceof AbstractTask) || element instanceof AbstractRepositoryQuery) {
			manager.add(menuToAdd);
		}
	}

	private void addAction(Action action, IMenuManager manager, AbstractTaskContainer element) {
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
	private void updateActionEnablement(Action action, AbstractTaskContainer element) {
		if (element instanceof AbstractTask) {
			if (action instanceof OpenWithBrowserAction) {
				if (((AbstractTask) element).hasValidUrl()) {
					action.setEnabled(true);
				} else {
					action.setEnabled(false);
				}
			} else if (action instanceof DeleteAction) {
				action.setEnabled(true);
			} else if (action instanceof OpenTaskListElementAction) {
				action.setEnabled(true);
			} else if (action instanceof CopyTaskDetailsAction) {
				action.setEnabled(true);
			} else if (action instanceof RenameAction) {
				action.setEnabled(true);
			}
		} else if (element != null) {
			if (action instanceof MarkTaskCompleteAction) {
				action.setEnabled(false);
			} else if (action instanceof MarkTaskIncompleteAction) {
				action.setEnabled(false);
			} else if (action instanceof DeleteAction) {
				if (element instanceof TaskArchive || element instanceof UnfiledCategory)
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
			} else if (action instanceof CopyTaskDetailsAction) {
				action.setEnabled(true);
			} else if (action instanceof RenameAction) {
				if (element instanceof AbstractTaskCategory) {
					AbstractTaskCategory container = (AbstractTaskCategory) element;
					action.setEnabled(container.isUserDefined());
				} else if (element instanceof AbstractRepositoryQuery) {
					action.setEnabled(true);
				}
			}
		} else {
			action.setEnabled(true);
		}
	}

	private void makeActions() {

		copyDetailsAction = new CopyTaskDetailsAction(true);

		goIntoAction = new GoIntoAction();
		goUpAction = new GoUpAction(drillDownAdapter);

		//newLocalTaskAction = new NewLocalTaskAction(this);
		removeFromCategoryAction = new RemoveFromCategoryAction(this);
		renameAction = new RenameAction(this);
		filteredTree.getViewer().addSelectionChangedListener(renameAction);

		deleteAction = new DeleteAction();
		collapseAll = new CollapseAllAction(this);
		expandAll = new ExpandAllAction(this);
		openAction = new OpenTaskListElementAction(this.getViewer());
		propertiesAction = new TaskListElementPropertiesAction(this.getViewer());
		openWithBrowser = new OpenWithBrowserAction();
		filterCompleteTask = new FilterCompletedTasksAction(this);
		showSubTasksAction = new FilterSubTasksAction(this);
		synchronizeAutomatically = new SynchronizeAutomaticallyAction();
		openPreferencesAction = new OpenTasksUiPreferencesAction();
		filterArchiveCategory = new FilterArchiveContainerAction(this);
		sortByAction = new SortyByDropDownAction(this);
		filterOnPriorityAction = new PriorityDropDownAction(this);
		previousTaskAction = new ActivateTaskHistoryDropDownAction(TasksUiPlugin.getTaskListManager()
				.getTaskActivationHistory(), false);
		linkWithEditorAction = new LinkWithEditorAction(this);
		ITaskListPresentation[] presentations = { catagorizedPresentation, scheduledPresentation };
		presentationDropDownSelectionAction = new PresentationDropDownSelectionAction(this, presentations);

		filteredTree.getViewer().addSelectionChangedListener(openWithBrowser);
		filteredTree.getViewer().addSelectionChangedListener(copyDetailsAction);
		// openWithBrowser.selectionChanged((StructuredSelection)
		// getViewer().getSelection());
		// copyDetailsAction.selectionChanged((StructuredSelection)
		// getViewer().getSelection());
		//
	}

	// public void toggleNextAction(boolean enable) {
	// nextTaskAction.setEnabled(enable);
	// }
	// public NextTaskDropDownAction getNextTaskAction() {
	// return nextTaskAction;
	// }

	public void togglePreviousAction(boolean enable) {
		previousTaskAction.setEnabled(enable);
	}

	public ActivateTaskHistoryDropDownAction getPreviousTaskAction() {
		return previousTaskAction;
	}

	/**
	 * Recursive function that checks for the occurrence of a certain task taskId. All children of the supplied node
	 * will be checked.
	 * 
	 * @param task
	 *            The <code>ITask</code> object that is to be searched.
	 * @param taskId
	 *            The taskId that is being searched for.
	 * @return <code>true</code> if the taskId was found in the node or any of its children
	 */
	protected boolean lookForId(String taskId) {
		return (TasksUiPlugin.getTaskListManager().getTaskList().getTask(taskId) == null);
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

		getViewer().addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				openAction.run();
			}
		});

		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selection = (StructuredSelection) getViewer().getSelection();
				Object object = selection.getFirstElement();
				if (TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
						TasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED)) {
					AbstractTask selectedTask = TaskListView.getFromActivePerspective().getSelectedTask();
					if (selectedTask != null) {
						// TODO: move history stuff
						activateAction.run(selectedTask);
						addTaskToHistory(selectedTask);
						previousTaskAction.setButtonStatus();
					}
				}
				if (object instanceof TaskCategory || object instanceof AbstractRepositoryQuery) {
					TasksUiUtil.refreshAndOpenTaskListElement((AbstractTaskContainer) object);
					// if(getViewer().getExpandedState(object)){
					// getViewer().collapseToLevel(object,
					// TreeViewer.ALL_LEVELS);
					// } else {
					// getViewer().expandToLevel(object, TreeViewer.ALL_LEVELS);
					// }
				}
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		filteredTree.getViewer().getControl().setFocus();
	}

	public String getBugIdFromUser() {
		InputDialog dialog = new InputDialog(getSite().getWorkbenchWindow().getShell(), "Enter Bugzilla ID",
				"Enter the Bugzilla ID: ", "", null);
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			return dialog.getValue();
		} else {
			return null;
		}
	}

	public void refreshAndFocus(boolean expand) {
		if (expand) {
			getViewer().expandAll();
		}
		refresh(null);
		selectedAndFocusTask(TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask());
	}

	public TreeViewer getViewer() {
		return filteredTree.getViewer();
	}

	public TaskCompletionFilter getCompleteFilter() {
		return filterComplete;
	}

	public TaskPriorityFilter getPriorityFilter() {
		return filterPriority;
	}

	public void addFilter(AbstractTaskListFilter filter) {
		if (!filters.contains(filter)) {
			filters.add(filter);
		}
	}

	public void clearFilters(boolean preserveArchiveFilter) {
		filters.clear();
		if (preserveArchiveFilter) {
			filters.add(filterArchive);
		}
		filters.add(filterWorkingSet);
	}

	public void removeFilter(AbstractTaskListFilter filter) {
		filters.remove(filter);
	}

	public void updateDrillDownActions() {
		if (drillDownAdapter.canGoBack()) {
			goUpAction.setEnabled(true);
		} else {
			goUpAction.setEnabled(false);
		}
	}

	boolean isInRenameAction = false;

	public void setInRenameAction(boolean b) {
		isInRenameAction = b;
	}

	public void goIntoCategory() {
		ISelection selection = getViewer().getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof AbstractTaskContainer) {
				drilledIntoCategory = (AbstractTaskContainer) element;
				drillDownAdapter.goInto();
				IActionBars bars = getViewSite().getActionBars();
				bars.getToolBarManager().remove(goUpAction.getId());
				bars.getToolBarManager().add(goUpAction);
				bars.updateActionBars();
				updateDrillDownActions();
			}
		}
	}

	public void goUpToRoot() {
		drilledIntoCategory = null;
		drillDownAdapter.goBack();
		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().remove(GoUpAction.ID);
		bars.updateActionBars();
		updateDrillDownActions();
	}

	public AbstractTask getSelectedTask() {
		ISelection selection = getViewer().getSelection();
		if (selection.isEmpty())
			return null;
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof AbstractTask) {
				return (AbstractTask) structuredSelection.getFirstElement();
			}
		}
		return null;
	}

	public static AbstractTask getSelectedTask(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			if (structuredSelection.size() != 1) {
				return null;
			}
			Object element = structuredSelection.getFirstElement();
			if (element instanceof AbstractTask) {
				return (AbstractTask) structuredSelection.getFirstElement();
			}
		}
		return null;
	}

	public void indicatePaused(boolean paused) {
		isPaused = paused;
		IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
		if (isPaused) {
			statusLineManager.setMessage(TasksUiImages.getImage(TasksUiImages.TASKLIST), "Mylar context capture paused");
			setPartName("(paused) " + PART_NAME);
		} else {
			statusLineManager.setMessage("");
			setPartName(PART_NAME);
		}
	}

	public AbstractTaskContainer getDrilledIntoCategory() {
		return drilledIntoCategory;
	}

	public TaskListFilteredTree getFilteredTree() {
		return filteredTree;
	}

	public void selectedAndFocusTask(AbstractTask task) {
		if (task == null || getViewer().getControl().isDisposed()) {
			return;
		}

		saveSelection();

		IStructuredSelection selection = restoreSelection(task);
		getViewer().setSelection(selection, true);

// // if no task exists, select the query hit if exists
// if (getViewer().getSelection().isEmpty()
// && (hit =
// TasksUiPlugin.getTaskListManager().getTaskList().getQueryHit(task.getHandleIdentifier()))
// != null) {
// try {
// AbstractRepositoryQuery query =
// TasksUiPlugin.getTaskListManager().getTaskList().getQueryForHandle(
// task.getHandleIdentifier());
// getViewer().expandToLevel(query, 1);
// getViewer().setSelection(new StructuredSelection(hit), true);
// } catch (SWTException e) {
// MylarStatusHandler.log(e, "Failed to expand Task List");
// }
// }
	}

	private void saveSelection() {
		IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		if (!selection.isEmpty()) {
			if (selection.getFirstElement() instanceof AbstractTaskContainer) {
				// make sure the new selection is inserted at the end of the
				// list
				String handle = ((AbstractTaskContainer) selection.getFirstElement()).getHandleIdentifier();
				lastSelectionByTaskHandle.remove(handle);
				lastSelectionByTaskHandle.put(handle, selection);

				if (lastSelectionByTaskHandle.size() > MAX_SELECTION_HISTORY_SIZE) {
					Iterator<String> it = lastSelectionByTaskHandle.keySet().iterator();
					it.next();
					it.remove();
				}
			}
		}
	}

	private IStructuredSelection restoreSelection(AbstractTaskContainer task) {
		IStructuredSelection selection = lastSelectionByTaskHandle.get(task.getHandleIdentifier());
		if (selection != null) {
			return selection;
		} else {
			return new StructuredSelection(task);
		}
	}

	/**
	 * Encapsulates refresh policy.
	 */
	private void refresh(final AbstractTaskContainer element) {
		if (getViewer().getControl() != null && !getViewer().getControl().isDisposed()) {
			if (element == null) {
				getViewer().refresh(true);
			} else {
				try {
					if (element instanceof AbstractTask) {
						AbstractTask task = (AbstractTask) element;
						getViewer().refresh(task, true);
					} else {
						getViewer().refresh(element, true);
					}
				} catch (SWTException e) {
					StatusHandler.log(e, "Failed to refresh Task List");
				}
			}
		}
	}

	public Image[] getPirorityImages() {
		Image[] images = new Image[PriorityLevel.values().length];
		for (int i = 0; i < PriorityLevel.values().length; i++) {
			images[i] = TasksUiImages.getImageForPriority(PriorityLevel.values()[i]);
		}
		return images;
	}

	public Set<AbstractTaskListFilter> getFilters() {
		return filters;
	}

	public static String getCurrentPriorityLevel() {
		if (TasksUiPlugin.getDefault().getPreferenceStore().contains(TasksUiPreferenceConstants.FILTER_PRIORITY)) {
			return TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.getString(TasksUiPreferenceConstants.FILTER_PRIORITY);
		} else {
			return PriorityLevel.P5.toString();
		}
	}

	public TaskArchiveFilter getArchiveFilter() {
		return filterArchive;
	}

	public void setManualFiltersEnabled(boolean enabled) {
		sortByAction.setEnabled(enabled);
		filterOnPriorityAction.setEnabled(enabled);
		filterCompleteTask.setEnabled(enabled);
		filterArchiveCategory.setEnabled(enabled);
	}

	public boolean isFocusedMode() {
		return focusedMode;
	}

	public void setFocusedMode(boolean focusedMode) {
		this.linkWithEditor = focusedMode;
		this.focusedMode = focusedMode;
	}

	public void setSortBy(SortByIndex sortByIndex) {
		this.sortByIndex = sortByIndex;
		getViewer().setSorter(new TaskListTableSorter(this, sortByIndex));
	}

	public void setSortDirection(int sortDirection) {
		this.sortDirection = sortDirection;
		getViewer().setSorter(new TaskListTableSorter(this, sortByIndex));
	}

	public SortByIndex getSortByIndex() {
		return sortByIndex;
	}

	public void setSynchronizationOverlaid(boolean synchronizationOverlaid) {
		this.synchronizationOverlaid = synchronizationOverlaid;
		getViewer().refresh();
	}

	public void displayPrioritiesAbove(String priority) {
		filterPriority.displayPrioritiesAbove(priority);
		getViewer().refresh();
	}

	// IPropertyChangeListener

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE.equals(property)
				|| IWorkingSetManager.CHANGE_WORKING_SET_REMOVE.equals(property)) {
			if (getSite() != null && getSite().getPage() != null) {
				filterWorkingSet.setCurrentWorkingSet(getSite().getPage().getAggregateWorkingSet());
			}

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					filteredTree.indicateActiveTaskWorkingSet();
				}
			});
		}

		try {
			getViewer().getControl().setRedraw(false);
			getViewer().collapseAll();
			getViewer().refresh();
			if (isFocusedMode()) {
				getViewer().expandAll();
			}
		} finally {
			getViewer().getControl().setRedraw(true);
		}
	}

	public void setLinkWithEditor(boolean linkWithEditor) {
		this.linkWithEditor = linkWithEditor;
		linkWithEditorAction.setChecked(linkWithEditor);
		if (linkWithEditor) {
			IEditorPart activeEditor = getSite().getPage().getActiveEditor();
			if (activeEditor != null) {
				jumpToEditorTask(activeEditor);
			}
		}
	}

	private void jumpToEditorTask(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof TaskEditorInput) {
			AbstractTask task = ((TaskEditorInput) input).getTask();
			AbstractTask selected = getSelectedTask();
			if (selected == null || !selected.equals(task)) {
				selectedAndFocusTask(task);
			}
		}
	}

	public static Set<IWorkingSet> getActiveWorkingSets() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null) {
			Set<IWorkingSet> allSets = new HashSet<IWorkingSet>(Arrays.asList(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage()
					.getWorkingSets()));
			Set<IWorkingSet> tasksSets = new HashSet<IWorkingSet>(allSets);
			for (IWorkingSet workingSet : allSets) {
				if (!workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
					tasksSets.remove(workingSet);
				}
			}
			return tasksSets;
		} else {
			return Collections.emptySet();
		}
	}

}
