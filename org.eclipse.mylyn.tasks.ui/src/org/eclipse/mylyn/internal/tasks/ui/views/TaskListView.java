/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - improvements
 *     Eugene Kuleshov - improvements
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.provisional.commons.ui.DelayedRefreshJob;
import org.eclipse.mylyn.internal.provisional.commons.ui.SubstringPatternFilter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.CategorizedPresentation;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.ScheduledPresentation;
import org.eclipse.mylyn.internal.tasks.ui.TaskArchiveFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskCompletionFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskPriorityFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryUtil;
import org.eclipse.mylyn.internal.tasks.ui.TaskWorkingSetFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CollapseAllAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ExpandAllAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.FilterCompletedTasksAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.GoUpAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.GroupSubTasksAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.LinkWithEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTasksUiPreferencesAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.PresentationDropDownSelectionAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeAutomaticallyAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskListSortAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskListViewActionGroup;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskListChangeAdapter;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListServiceMessageControl;
import org.eclipse.mylyn.internal.tasks.ui.util.PlatformUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.SortCriterion;
import org.eclipse.mylyn.internal.tasks.ui.util.SortCriterion.SortKey;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDragSourceListener;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.TreeVisitor;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 * @author Eugene Kuleshov
 */
public class TaskListView extends ViewPart implements IPropertyChangeListener, IShowInTarget {

	private final class TaskListRefreshJob extends DelayedRefreshJob {

		private TaskListRefreshJob(TreeViewer treeViewer, String name) {
			super(treeViewer, name);
		}

		@Override
		protected void doRefresh(Object[] items) {
			TreePath selection = preserveSelection();

			if (items == null) {
				viewer.refresh(true);
			} else if (items.length > 0) {
				try {
					if (TaskListView.this.isFocusedMode()) {
						Set<Object> children = new HashSet<Object>(Arrays.asList(items));
						Set<AbstractTaskContainer> parents = new HashSet<AbstractTaskContainer>();
						for (Object item : items) {
							if (item instanceof AbstractTask) {
								parents.addAll(((AbstractTask) item).getParentContainers());
							}
						}
						// 1. refresh parents
						children.removeAll(parents);
						for (AbstractTaskContainer parent : parents) {
							viewer.refresh(parent, false);
							// only refresh label of parent
							viewer.update(parent, null);
						}
						// 2. refresh children
						for (Object item : children) {
							viewer.refresh(item, true);
						}
						// 3. update states of all changed items
						for (Object item : items) {
							updateExpansionState(item);
						}
					} else {
						Set<AbstractTaskContainer> parents = new HashSet<AbstractTaskContainer>();
						for (Object item : items) {
							if (item instanceof AbstractTask) {
								parents.addAll(((AbstractTask) item).getParentContainers());
							}
							viewer.refresh(item, true);
							updateExpansionState(item);
						}
						// refresh labels of parents for task activation or incoming indicators
						for (AbstractTaskContainer parent : parents) {
							// only refresh label
							viewer.update(parent, null);
						}
					}
				} catch (SWTException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to refresh viewer: " //$NON-NLS-1$
							+ viewer, e));
				}
			}

			updateToolTip(false);
			restoreSelection(selection);
		}

		private TreePath preserveSelection() {
			if (viewer instanceof TreeViewer) {
				TreeViewer treeViewer = (TreeViewer) viewer;
				// in case the refresh removes the currently selected item, 
				// remember the next item in the tree to restore the selection
				// TODO: consider making this optional
				TreeItem[] selection = treeViewer.getTree().getSelection();
				if (selection.length > 0) {
					TreeWalker treeWalker = new TreeWalker(treeViewer);
					return treeWalker.walk(new TreeVisitor() {
						@Override
						public boolean visit(Object object) {
							return true;
						}
					}, selection[selection.length - 1]);
				}
			}
			return null;
		}

		private void restoreSelection(TreePath treePath) {
			if (treePath != null) {
				ISelection newSelection = viewer.getSelection();
				if (newSelection == null || newSelection.isEmpty()) {
					viewer.setSelection(new TreeSelection(treePath), true);
				}
			}
		}

		protected void updateExpansionState(Object item) {
			if (TaskListView.this.isFocusedMode() && isAutoExpandMode()) {
				TaskListView.this.getViewer().expandToLevel(item, 3);
			}
		}
	}

	private static final String ID_SEPARATOR_FILTERS = "filters"; //$NON-NLS-1$

	private static final String ID_SEPARATOR_SEARCH = "search"; //$NON-NLS-1$

	private static final String ID_SEPARATOR_TASKS = "tasks"; //$NON-NLS-1$

	private static final String ID_SEPARATOR_CONTEXT = "context"; //$NON-NLS-1$

	/**
	 * @deprecated Use {@link ITasksUiConstants#ID_VIEW_TASKS} instead
	 */
	@Deprecated
	public static final String ID = ITasksUiConstants.ID_VIEW_TASKS;

	public static final String LABEL_VIEW = Messages.TaskListView_Task_List;

	@Deprecated
	private static final String MEMENTO_KEY_SORT_DIRECTION = "sortDirection"; //$NON-NLS-1$

	@Deprecated
	private static final String MEMENTO_KEY_SORTER = "sorter"; //$NON-NLS-1$

	@Deprecated
	private static final String MEMENTO_KEY_SORTER2 = "sorter2"; //$NON-NLS-1$

	@Deprecated
	private static final String MEMENTO_KEY_SORT_INDEX = "sortIndex"; //$NON-NLS-1$

	@Deprecated
	private static final String MEMENTO_SORT_INDEX = "org.eclipse.mylyn.tasklist.ui.views.tasklist.sortIndex"; //$NON-NLS-1$

	private static final String MEMENTO_SORTER = "sorter"; //$NON-NLS-1$

	private static final String MEMENTO_LINK_WITH_EDITOR = "linkWithEditor"; //$NON-NLS-1$

	private static final String MEMENTO_PRESENTATION = "presentation"; //$NON-NLS-1$

	private static final String LABEL_NO_TASKS = "no task active"; //$NON-NLS-1$

	private final static int SIZE_MAX_SELECTION_HISTORY = 10;

	static final String[] PRIORITY_LEVELS = { PriorityLevel.P1.toString(), PriorityLevel.P2.toString(),
			PriorityLevel.P3.toString(), PriorityLevel.P4.toString(), PriorityLevel.P5.toString() };

	public static final String[] PRIORITY_LEVEL_DESCRIPTIONS = { PriorityLevel.P1.getDescription(),
			PriorityLevel.P2.getDescription(), PriorityLevel.P3.getDescription(), PriorityLevel.P4.getDescription(),
			PriorityLevel.P5.getDescription() };

	private static List<AbstractTaskListPresentation> presentationsPrimary = new ArrayList<AbstractTaskListPresentation>();

	private static List<AbstractTaskListPresentation> presentationsSecondary = new ArrayList<AbstractTaskListPresentation>();

	private boolean focusedMode;

	private boolean linkWithEditor;

	private final TaskListCellModifier taskListCellModifier = new TaskListCellModifier(this);

	private IThemeManager themeManager;

	private TaskListFilteredTree filteredTree;

	private org.eclipse.mylyn.internal.provisional.commons.ui.SelectionProviderAdapter selectionProvider;

	private DrillDownAdapter drillDownAdapter;

	private AbstractTaskContainer drilledIntoCategory;

	private CollapseAllAction collapseAll;

	private ExpandAllAction expandAll;

	private FilterCompletedTasksAction filterCompleteTask;

	private GroupSubTasksAction filterSubTasksAction;

	private SynchronizeAutomaticallyAction synchronizeAutomatically;

	private OpenTasksUiPreferencesAction openPreferencesAction;

	private PriorityDropDownAction filterOnPriorityAction;

	private TaskListSortAction sortDialogAction;

	private NewTaskAction newTaskAction;

	private LinkWithEditorAction linkWithEditorAction;

	private final PresentationDropDownSelectionAction presentationDropDownSelectionAction = new PresentationDropDownSelectionAction(
			this);

	private final TaskPriorityFilter filterPriority = new TaskPriorityFilter();

	private final TaskCompletionFilter filterComplete = new TaskCompletionFilter();

	private final TaskArchiveFilter filterArchive = new TaskArchiveFilter();

	private TaskWorkingSetFilter filterWorkingSet;

	private final Set<AbstractTaskListFilter> filters = new HashSet<AbstractTaskListFilter>();

	protected String[] columnNames = new String[] { Messages.TaskListView_Summary };

	protected int[] columnWidths = new int[] { 200 };

	private TreeColumn[] columns;

	private IMemento taskListMemento;

	private AbstractTaskListPresentation currentPresentation;

	private TaskTableLabelProvider taskListTableLabelProvider;

	private TaskListSorter tableSorter;

	private TaskListViewActionGroup actionGroup;

	private CustomTaskListDecorationDrawer customDrawer;

	private TaskListServiceMessageControl serviceMessageControl;

	private final IPageListener PAGE_LISTENER = new IPageListener() {
		public void pageActivated(IWorkbenchPage page) {
			filteredTree.indicateActiveTaskWorkingSet();
		}

		public void pageClosed(IWorkbenchPage page) {
			// ignore

		}

		public void pageOpened(IWorkbenchPage page) {
			// ignore
		}
	};

	private final LinkedHashMap<String, IStructuredSelection> lastSelectionByTaskHandle = new LinkedHashMap<String, IStructuredSelection>(
			SIZE_MAX_SELECTION_HISTORY);

	/**
	 * True if the view should indicate that interaction monitoring is paused
	 */
	protected boolean isPaused = false;

	private final ITaskActivityListener TASK_ACTIVITY_LISTENER = new TaskActivityAdapter() {
		@Override
		public void activityReset() {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refreshJob.refresh();
				}
			});
		}
	};

	private final ITaskActivationListener TASK_ACTIVATION_LISTENER = new TaskActivationAdapter() {

		@Override
		public void taskActivated(final ITask task) {
			if (task != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						updateDescription();
						refresh(task);
						selectedAndFocusTask(task);
						filteredTree.indicateActiveTask(task);
					}
				});
			}
		}

		@Override
		public void taskDeactivated(final ITask task) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(task);
					updateDescription();
					filteredTree.indicateNoActiveTask();
				}
			});
		}

		private void refresh(final ITask task) {
			if (TaskListView.this.isScheduledPresentation()) {
				refreshJob.refresh();
			} else {
				refreshJob.refreshElement(task);
			}
		}

	};

	private final ITaskListChangeListener TASKLIST_CHANGE_LISTENER = new TaskListChangeAdapter() {

		@Override
		public void containersChanged(final Set<TaskContainerDelta> deltas) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					for (TaskContainerDelta taskContainerDelta : deltas) {
						if (isScheduledPresentation()) {
							// TODO: implement refresh policy for scheduled presentation
							refreshJob.refresh();
						} else {
							switch (taskContainerDelta.getKind()) {
							case ROOT:
								refreshJob.refresh();
								break;
							case ADDED:
							case REMOVED:
								if (taskContainerDelta.getElement() != null) {
									refreshJob.refreshElement(taskContainerDelta.getElement());
								}
								if (taskContainerDelta.getParent() != null) {
									refreshJob.refreshElement(taskContainerDelta.getParent());
								} else {
									// element was added/removed from the root
									refreshJob.refresh();
								}
								break;
							case CONTENT:
								refreshJob.refreshElement(taskContainerDelta.getElement());
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
					|| CommonThemes.isCommonTheme(event.getProperty())) {
				taskListTableLabelProvider.setCategoryBackgroundColor(themeManager.getCurrentTheme()
						.getColorRegistry()
						.get(CommonThemes.COLOR_CATEGORY));
				getViewer().refresh();
			}
		}
	};

	private final IPropertyChangeListener tasksUiPreferenceListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (ITasksUiPreferenceConstants.TASK_LIST_TOOL_TIPS_ENABLED.equals(event.getProperty())) {
				updateTooltipEnablement();
			}
			if (event.getProperty().equals(ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED)
					|| event.getProperty().equals(ITasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT)) {
				refreshJob.refresh();
			}
		}
	};

	private TaskListToolTip taskListToolTip;

	public static TaskListView getFromActivePerspective() {
		if (PlatformUI.isWorkbenchRunning()) {
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null) {
				IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
				if (activePage != null) {
					IViewPart view = activePage.findView(ITasksUiConstants.ID_VIEW_TASKS);
					if (view instanceof TaskListView) {
						return (TaskListView) view;
					}
				}
			}
		}
		return null;
	}

	private static boolean initializedSynchronization;

	public TaskListView() {
		if (!initializedSynchronization) {
			initializedSynchronization = true;
			// trigger additional initialization when task list is first made visible.
			TasksUiPlugin.getDefault().initializeNotificationsAndSynchronization();
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		if (actionGroup != null) {
			actionGroup.dispose();
		}

		TasksUiPlugin.getDefault().getServiceMessageManager().removeServiceMessageListener(serviceMessageControl);
		TasksUiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(tasksUiPreferenceListener);
		TasksUiInternal.getTaskList().removeChangeListener(TASKLIST_CHANGE_LISTENER);
		TasksUiPlugin.getTaskActivityManager().removeActivityListener(TASK_ACTIVITY_LISTENER);
		TasksUiPlugin.getTaskActivityManager().removeActivationListener(TASK_ACTIVATION_LISTENER);

		PlatformUI.getWorkbench().getWorkingSetManager().removePropertyChangeListener(this);
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().removePageListener(PAGE_LISTENER);
		}

		final IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		if (themeManager != null) {
			themeManager.removePropertyChangeListener(THEME_CHANGE_LISTENER);
		}

		if (editorListener != null) {
			getSite().getPage().removePartListener(editorListener);
		}
	}

	private void updateDescription() {
		ITask task = TasksUi.getTaskActivityManager().getActiveTask();
		if (getSite() == null || getSite().getPage() == null) {
			return;
		}

		IViewReference reference = getSite().getPage().findViewReference(ITasksUiConstants.ID_VIEW_TASKS);
		boolean shouldSetDescription = false;
		if (reference != null && reference.isFastView() && !getSite().getPage().isPartVisible(this)) {
			shouldSetDescription = true;
		}

		if (task != null) {
			setTitleToolTip(LABEL_VIEW + " (" + task.getSummary() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			if (shouldSetDescription) {
				setContentDescription(task.getSummary());
			} else {
				setContentDescription(""); //$NON-NLS-1$
			}
		} else {
			setTitleToolTip(LABEL_VIEW);
			if (shouldSetDescription) {
				setContentDescription(LABEL_NO_TASKS);
			} else {
				setContentDescription(""); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.taskListMemento = memento;
	}

	@Override
	public void saveState(IMemento memento) {
		if (tableSorter != null) {
			IMemento child = memento.createChild(MEMENTO_SORTER);
			tableSorter.saveState(child);
		}

		memento.putString(MEMENTO_LINK_WITH_EDITOR, Boolean.toString(linkWithEditor));
		memento.putString(MEMENTO_PRESENTATION, currentPresentation.getId());

		if (filteredTree.getTextSearchControl() != null) {
			filteredTree.getTextSearchControl().saveState(memento);
		}
	}

	private void restoreState() {
		if (taskListMemento != null) {
			if (tableSorter != null) {
				IMemento sorterMemento = taskListMemento.getChild(MEMENTO_SORTER);
				if (sorterMemento != null) {
					tableSorter.restoreState(sorterMemento);
				} else {
					sorterMemento = taskListMemento.getChild(MEMENTO_SORT_INDEX);
					if (sorterMemento != null) {
						migrateSorterState(tableSorter, sorterMemento);
					}
				}
			}
			applyPresentation(taskListMemento.getString(MEMENTO_PRESENTATION));
		}

		filterWorkingSet = new TaskWorkingSetFilter();
		filterWorkingSet.updateWorkingSet(getSite().getPage().getAggregateWorkingSet());
		filteredTree.setWorkingSetFilter(filterWorkingSet);
		addFilter(filterWorkingSet);
		addFilter(filterPriority);
		if (TasksUiPlugin.getDefault().getPreferenceStore().contains(ITasksUiPreferenceConstants.FILTER_COMPLETE_MODE)) {
			addFilter(filterComplete);
		}

		//if (TasksUiPlugin.getDefault().getPreferenceStore().contains(TasksUiPreferenceConstants.FILTER_ARCHIVE_MODE)) {
		addFilter(filterArchive);
		//}

		// Restore "link with editor" value; by default true
		boolean linkValue = true;
		if (taskListMemento != null && taskListMemento.getString(MEMENTO_LINK_WITH_EDITOR) != null) {
			linkValue = Boolean.parseBoolean(taskListMemento.getString(MEMENTO_LINK_WITH_EDITOR));
		}
		setLinkWithEditor(linkValue);

		if (taskListMemento != null && filteredTree.getTextSearchControl() != null) {
			filteredTree.getTextSearchControl().restoreState(taskListMemento);
		}

		getViewer().refresh();
	}

	/**
	 * Public for testing only.
	 */
	public void migrateSorterState(TaskListSorter tableSorter, IMemento sorterMemento) {
		int restoredSortIndex = 0;
		if (sorterMemento != null) {
			int sortDirection = -1;
			IMemento m = sorterMemento.getChild(MEMENTO_KEY_SORTER);
			if (m != null) {
				Integer sortIndexInt = m.getInteger(MEMENTO_KEY_SORT_INDEX);
				if (sortIndexInt != null) {
					restoredSortIndex = sortIndexInt.intValue();
				}
				Integer sortDirInt = m.getInteger(MEMENTO_KEY_SORT_DIRECTION);
				if (sortDirInt != null) {
					sortDirection = sortDirInt.intValue();
					tableSorter.getComparator().getSortCriterion(0).setDirection(sortDirection);
					switch (restoredSortIndex) {
					case 1:
						tableSorter.getComparator().getSortCriterion(0).setKey(SortKey.SUMMARY);
						break;
					case 2:
						tableSorter.getComparator().getSortCriterion(0).setKey(SortKey.DATE_CREATED);
						break;
					case 3:
						tableSorter.getComparator().getSortCriterion(0).setKey(SortKey.TASK_ID);
						break;
					default:
						tableSorter.getComparator().getSortCriterion(0).setKey(SortKey.PRIORITY);
					}
				}
			}

			IMemento m2 = sorterMemento.getChild(MEMENTO_KEY_SORTER2);
			if (m2 != null) {
				Integer sortIndexInt = m2.getInteger(MEMENTO_KEY_SORT_INDEX);
				if (sortIndexInt != null) {
					restoredSortIndex = sortIndexInt.intValue();
				}
				Integer sortDirInt = m2.getInteger(MEMENTO_KEY_SORT_DIRECTION);
				if (sortDirInt != null) {
					sortDirection = sortDirInt.intValue();
					tableSorter.getComparator().getSortCriterion(1).setDirection(sortDirection);
					switch (restoredSortIndex) {
					case 1:
						tableSorter.getComparator().getSortCriterion(1).setKey(SortKey.SUMMARY);
						break;
					case 2:
						tableSorter.getComparator().getSortCriterion(1).setKey(SortKey.DATE_CREATED);
						break;
					case 3:
						tableSorter.getComparator().getSortCriterion(1).setKey(SortKey.TASK_ID);
						break;
					default:
						tableSorter.getComparator().getSortCriterion(1).setKey(SortKey.PRIORITY);
					}
				}
			}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite body = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 1;
		body.setLayout(layout);

		IWorkbenchSiteProgressService progress = (IWorkbenchSiteProgressService) getSite().getAdapter(
				IWorkbenchSiteProgressService.class);
		if (progress != null) {
			// show indicator for all running query synchronizations
			progress.showBusyForFamily(ITasksCoreConstants.JOB_FAMILY_SYNCHRONIZATION);
		}

		this.selectionProvider = new org.eclipse.mylyn.internal.provisional.commons.ui.SelectionProviderAdapter();
		getSite().setSelectionProvider(selectionProvider);

		themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		themeManager.addPropertyChangeListener(THEME_CHANGE_LISTENER);

		filteredTree = new TaskListFilteredTree(body, SWT.MULTI | SWT.VERTICAL | /* SWT.H_SCROLL | */SWT.V_SCROLL
				| SWT.NO_SCROLL | SWT.FULL_SELECTION, new SubstringPatternFilter(), getViewSite().getWorkbenchWindow());

		// need to do initialize tooltip early for native tooltip disablement to take effect
		taskListToolTip = new TaskListToolTip(getViewer().getControl());
		updateTooltipEnablement();

		getSite().registerContextMenu(TasksUiInternal.ID_MENU_ACTIVE_TASK, filteredTree.getActiveTaskMenuManager(),
				selectionProvider);
		filteredTree.setActiveTaskSelectionProvider(selectionProvider);

		getViewer().addSelectionChangedListener(this.selectionProvider);
		getViewer().getControl().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				selectionProvider.setSelection(getViewer().getSelection());
			}
		});

		filteredTree.getFilterControl().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateFilterEnablement();
			}
		});

		getViewer().getTree().setHeaderVisible(false);
		getViewer().setUseHashlookup(true);
		refreshJob = new TaskListRefreshJob(getViewer(), "Task List Refresh"); //$NON-NLS-1$

		configureColumns(columnNames, columnWidths);

		final IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		Color categoryBackground = themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_CATEGORY);
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

		tableSorter = new TaskListSorter();
		getViewer().setSorter(tableSorter);

		applyPresentation(CategorizedPresentation.ID);

		drillDownAdapter = new DrillDownAdapter(getViewer());
		getViewer().setInput(getViewSite());

		final int activationImageOffset = PlatformUtil.getTreeImageOffset();
		customDrawer = new CustomTaskListDecorationDrawer(activationImageOffset, false);
		getViewer().getTree().addListener(SWT.EraseItem, customDrawer);
		getViewer().getTree().addListener(SWT.PaintItem, customDrawer);

		getViewer().getTree().addMouseListener(new MouseListener() {

			public void mouseDown(MouseEvent e) {
				// NOTE: need e.x offset for Linux/GTK, which does not see
				// left-aligned items in tree
				Object selectedNode = ((Tree) e.widget).getItem(new Point(e.x + 70, e.y));
				if (selectedNode instanceof TreeItem) {
					Object selectedObject = ((TreeItem) selectedNode).getData();
					if (selectedObject instanceof ITask) {
						if (e.x > activationImageOffset && e.x < activationImageOffset + 13) {
							taskListCellModifier.toggleTaskActivation((TreeItem) selectedNode);
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

		// TODO make these proper commands and move code into TaskListViewCommands
		getViewer().getTree().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F2 && e.stateMask == 0) {
					if (actionGroup.getRenameAction().isEnabled()) {
						actionGroup.getRenameAction().run();
					}
				} else if ((e.keyCode & SWT.KEYCODE_BIT) != 0) {
					// Do nothing here since it is key code
				} else if (e.keyCode == SWT.ESC) {
					taskListToolTip.hide();
				} else if (e.keyCode == 'f' && e.stateMask == SWT.MOD1) {
					filteredTree.getFilterControl().setFocus();
				} else if (e.stateMask == 0) {
					if (Character.isLetter((char) e.keyCode) || Character.isDigit((char) e.keyCode)) {
						String string = new Character((char) e.keyCode).toString();
						filteredTree.getFilterControl().setFocus();
						filteredTree.getFilterControl().setText(string);
						filteredTree.getFilterControl().setSelection(1, 1);
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

		// update tooltip contents
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateToolTip(true);
			}
		});

		getViewer().getTree().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				taskListToolTip.hide();
			}
		});

		makeActions();
		hookGlobalActions();
		hookContextMenu();
		hookOpenAction();
		contributeToActionBars();
		initHandlers();

		new GradientDrawer(themeManager, getViewer());

		initDragAndDrop(parent);
		expandToActiveTasks();
		restoreState();

		updateDescription();

		IContextService contextSupport = (IContextService) getSite().getService(IContextService.class);
		if (contextSupport != null) {
			contextSupport.activateContext(ITasksUiConstants.ID_VIEW_TASKS);
		}

		getSite().getPage().addPartListener(editorListener);

		// we need to update the icon here as the action was not created when the presentation was applied
		updatePresentationSelectorImage();

		PlatformUI.getWorkbench().getWorkingSetManager().addPropertyChangeListener(this);
		TasksUiPlugin.getTaskActivityManager().addActivityListener(TASK_ACTIVITY_LISTENER);
		TasksUiPlugin.getTaskActivityManager().addActivationListener(TASK_ACTIVATION_LISTENER);
		TasksUiInternal.getTaskList().addChangeListener(TASKLIST_CHANGE_LISTENER);

		TasksUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(tasksUiPreferenceListener);

		serviceMessageControl = new TaskListServiceMessageControl(body);

		List<TaskRepository> repos = TasksUi.getRepositoryManager().getAllRepositories();
		boolean showMessage = true;
		for (TaskRepository repository : repos) {
			if (!repository.getConnectorKind().equals("local") //$NON-NLS-1$
					&& !TaskRepositoryUtil.isAddAutomatically(repository.getRepositoryUrl())) {
				showMessage = false;
				break;
			}
		}

		String lastClosedId = TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getString(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ID);

		if (showMessage && lastClosedId.equals("")) { //$NON-NLS-1$
			ServiceMessage message = new ServiceMessage();
			message.setDescription("<a href=\"connect\">Connect</a> to your task and ALM tools."); //$NON-NLS-1$
			message.setTitle("Connect Mylyn"); //$NON-NLS-1$
			message.setImage(Dialog.DLG_IMG_MESSAGE_INFO);
			message.setId("0"); //$NON-NLS-1$
			serviceMessageControl.setMessage(message);
		}

		TasksUiPlugin.getDefault().getServiceMessageManager().addServiceMessageListener(serviceMessageControl);

		// Need to do this because the page, which holds the active working set is not around on creation, see bug 203179
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPageListener(PAGE_LISTENER);
	}

	private void initHandlers() {
		IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
		handlerService.activateHandler(CollapseAllHandler.COMMAND_ID, new CollapseAllHandler(getViewer()));
	}

	private void hookGlobalActions() {
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), actionGroup.getDeleteAction());
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), actionGroup.getCopyDetailsAction());
	}

	private void applyPresentation(String id) {
		if (id != null) {
			for (AbstractTaskListPresentation presentation : presentationsPrimary) {
				if (id.equals(presentation.getId())) {
					applyPresentation(presentation);
					return;
				}
			}
			for (AbstractTaskListPresentation presentation : presentationsSecondary) {
				if (id.equals(presentation.getId())) {
					applyPresentation(presentation);
					return;
				}
			}
		}
	}

	public void applyPresentation(AbstractTaskListPresentation presentation) {
		try {
			getViewer().getControl().setRedraw(false);
			if (!filteredTree.getFilterString().equals("")) { //$NON-NLS-1$
				filteredTree.getFilterControl().setText(""); //$NON-NLS-1$
			}
			AbstractTaskListContentProvider contentProvider = presentation.getContentProvider(this);
			getViewer().setContentProvider(contentProvider);
			refresh(true);

			currentPresentation = presentation;
			updatePresentationSelectorImage();
		} finally {
			getViewer().getControl().setRedraw(true);
		}
	}

	private void updatePresentationSelectorImage() {
		if (presentationDropDownSelectionAction != null && currentPresentation != null) {
			presentationDropDownSelectionAction.setImageDescriptor(currentPresentation.getImageDescriptor());
		}
		for (IContributionItem item : getViewSite().getActionBars().getToolBarManager().getItems()) {
			if (item instanceof ActionContributionItem) {
				IAction action = ((ActionContributionItem) item).getAction();
				if (action instanceof PresentationDropDownSelectionAction.PresentationSelectionAction) {
					((PresentationDropDownSelectionAction.PresentationSelectionAction) action).update();
				}
			}
		}
	}

	public AbstractTaskListPresentation getCurrentPresentation() {
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

			columns[i].addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					SortCriterion criterion = tableSorter.getComparator().getSortCriterion(0);
					criterion.setDirection(criterion.getDirection() * -1);
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
	private final IPartListener editorListener = new IPartListener() {

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
				IViewReference reference = getSite().getPage().findViewReference(ITasksUiConstants.ID_VIEW_TASKS);
				if (reference != null && reference.isFastView()) {
					updateDescription();
				}
				taskListToolTip.hide();
			}
		}

		public void partOpened(IWorkbenchPart part) {
		}
	};

	private void initDragAndDrop(Composite parent) {
		Transfer[] dragTypes = new Transfer[] { LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() };
		Transfer[] dropTypes = new Transfer[] { LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance(),
				TextTransfer.getInstance(), RTFTransfer.getInstance(), URLTransfer.getInstance() };

		getViewer().addDragSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK, dragTypes,
				new TaskDragSourceListener(getViewer()));
		getViewer().addDropSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_DEFAULT, dropTypes,
				new TaskListDropAdapter(getViewer()));
	}

	void expandToActiveTasks() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				ITask task = TasksUi.getTaskActivityManager().getActiveTask();
				if (task != null) {
					getViewer().expandToLevel(task, 0);
				}
			}
		});
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				actionGroup.fillContextMenu(manager);
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
		manager.add(actionGroup.getGoUpAction());
		manager.add(collapseAll);
		manager.add(expandAll);
		manager.add(new Separator(ID_SEPARATOR_FILTERS));
		manager.add(sortDialogAction);
		manager.add(filterOnPriorityAction);
		manager.add(filterCompleteTask);
		manager.add(filterSubTasksAction);

		manager.add(new Separator(ID_SEPARATOR_SEARCH));
		manager.add(new GroupMarker(ID_SEPARATOR_TASKS));
		manager.add(synchronizeAutomatically);

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				filterOnPriorityAction.updateCheckedState();
			}
		});

		manager.add(linkWithEditorAction);
		manager.add(new Separator());
		manager.add(openPreferencesAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(newTaskAction);
		manager.add(new Separator());
		addPresentations(manager);
		manager.add(new Separator());
		manager.add(new GroupMarker(ID_SEPARATOR_CONTEXT));
		manager.add(new Separator());
		manager.add(filterCompleteTask);
		manager.add(collapseAll);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void addPresentations(IToolBarManager manager) {
		for (AbstractTaskListPresentation presentation : TaskListView.getPresentations()) {
			if (!presentation.isPrimary()) {
				// at least one non primary presentation present
				manager.add(presentationDropDownSelectionAction);
				return;
			}
		}

		// add toggle buttons for primary presentations
		for (AbstractTaskListPresentation presentation : TaskListView.getPresentations()) {
			manager.add(new PresentationDropDownSelectionAction.PresentationSelectionAction(this, presentation));
		}
	}

	public List<IRepositoryElement> getSelectedTaskContainers() {
		List<IRepositoryElement> selectedElements = new ArrayList<IRepositoryElement>();
		for (Iterator<?> i = ((IStructuredSelection) getViewer().getSelection()).iterator(); i.hasNext();) {
			Object object = i.next();
			if (object instanceof ITaskContainer) {
				selectedElements.add((IRepositoryElement) object);
			}
		}
		return selectedElements;
	}

	private void makeActions() {
		actionGroup = new TaskListViewActionGroup(this, drillDownAdapter);
		actionGroup.getOpenAction().setViewer(getViewer());

		collapseAll = new CollapseAllAction(this);
		expandAll = new ExpandAllAction(this);
		filterCompleteTask = new FilterCompletedTasksAction(this);
		filterSubTasksAction = new GroupSubTasksAction(this);
		synchronizeAutomatically = new SynchronizeAutomaticallyAction();
		openPreferencesAction = new OpenTasksUiPreferencesAction();
		//filterArchiveCategory = new FilterArchiveContainerAction(this);
		sortDialogAction = new TaskListSortAction(getSite(), this);
		filterOnPriorityAction = new PriorityDropDownAction(this);
		linkWithEditorAction = new LinkWithEditorAction(this);
		newTaskAction = new NewTaskAction();
		filteredTree.getViewer().addSelectionChangedListener(newTaskAction);
	}

	private void hookOpenAction() {
		getViewer().addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				actionGroup.getOpenAction().run();
			}
		});

		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.getBoolean(ITasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED)) {
					AbstractTask selectedTask = getSelectedTask();
					if (selectedTask != null && !selectedTask.isActive()) {
						TasksUiInternal.activateTaskThroughCommand(selectedTask);
					}
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

	public void refresh(boolean expandIfFocused) {
		if (expandIfFocused && isFocusedMode() && isAutoExpandMode()) {
			try {
				getViewer().getControl().setRedraw(false);
				refreshJob.refreshNow();
				getViewer().expandAll();
			} finally {
				getViewer().getControl().setRedraw(true);
			}
		} else {
			refreshJob.refreshNow();
		}
	}

	public void refresh() {
		refreshJob.refreshNow();
	}

	public TaskListToolTip getToolTip() {
		return taskListToolTip;
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

	public void clearFilters() {
		filters.clear();
		filters.add(filterArchive);
		filters.add(filterWorkingSet);
	}

	public void removeFilter(AbstractTaskListFilter filter) {
		filters.remove(filter);
	}

	public void updateDrillDownActions() {
		actionGroup.updateDrillDownActions();
	}

	boolean isInRenameAction = false;

	private DelayedRefreshJob refreshJob;

	private boolean itemNotFoundExceptionLogged;

	public void setInRenameAction(boolean b) {
		isInRenameAction = b;
	}

	public void goIntoCategory() {
		ISelection selection = getViewer().getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof ITaskContainer) {
				drilledIntoCategory = (AbstractTaskContainer) element;
				drillDownAdapter.goInto();
				IActionBars bars = getViewSite().getActionBars();
				bars.getToolBarManager().remove(actionGroup.getGoUpAction().getId());
				bars.getToolBarManager().add(actionGroup.getGoUpAction());
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
		if (selection.isEmpty()) {
			return null;
		}
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof ITask) {
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
			if (element instanceof ITask) {
				return (AbstractTask) structuredSelection.getFirstElement();
			}
		}
		return null;
	}

	public void indicatePaused(boolean paused) {
		isPaused = paused;
		IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
		if (isPaused) {
			statusLineManager.setMessage(CommonImages.getImage(TasksUiImages.TASKS_VIEW),
					Messages.TaskListView_Mylyn_context_capture_paused);
			setPartName(Messages.TaskListView__paused_ + LABEL_VIEW);
		} else {
			statusLineManager.setMessage(""); //$NON-NLS-1$
			setPartName(LABEL_VIEW);
		}
	}

	public AbstractTaskContainer getDrilledIntoCategory() {
		return drilledIntoCategory;
	}

	public TaskListFilteredTree getFilteredTree() {
		return filteredTree;
	}

	public void selectedAndFocusTask(ITask task) {
		if (task == null || getViewer().getControl().isDisposed()) {
			return;
		}
		saveSelection();

		IStructuredSelection selection = restoreSelection(task);
		try {
			getViewer().setSelection(selection, true);
		} catch (SWTError e) {
			if (!itemNotFoundExceptionLogged) {
				itemNotFoundExceptionLogged = true;
				// It's probably not worth displaying this to the user since the item
				// is not there in this case, so consider removing.
				StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
						"Could not link Task List with editor", //$NON-NLS-1$
						e));
			}
		}
	}

	private void saveSelection() {
		IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		if (!selection.isEmpty()) {
			if (selection.getFirstElement() instanceof ITaskContainer) {
				// make sure the new selection is inserted at the end of the
				// list
				String handle = ((IRepositoryElement) selection.getFirstElement()).getHandleIdentifier();
				lastSelectionByTaskHandle.remove(handle);
				lastSelectionByTaskHandle.put(handle, selection);

				if (lastSelectionByTaskHandle.size() > SIZE_MAX_SELECTION_HISTORY) {
					Iterator<String> it = lastSelectionByTaskHandle.keySet().iterator();
					it.next();
					it.remove();
				}
			}
		}
	}

	private IStructuredSelection restoreSelection(IRepositoryElement task) {
		IStructuredSelection selection = lastSelectionByTaskHandle.get(task.getHandleIdentifier());
		if (selection != null) {
			return selection;
		} else {
			return new StructuredSelection(task);
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
		if (TasksUiPlugin.getDefault().getPreferenceStore().contains(ITasksUiPreferenceConstants.FILTER_PRIORITY)) {
			return TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.getString(ITasksUiPreferenceConstants.FILTER_PRIORITY);
		} else {
			return PriorityLevel.P5.toString();
		}
	}

	public TaskArchiveFilter getArchiveFilter() {
		return filterArchive;
	}

	private void updateFilterEnablement() {
		boolean enabled = !isFocusedMode();
		if (enabled) {
			String filterText = filteredTree.getFilterString();
			if (filterText != null && filterText.length() > 0) {
				enabled = false;
			}
		}
		sortDialogAction.setEnabled(enabled);
		filterOnPriorityAction.setEnabled(enabled);
		filterCompleteTask.setEnabled(enabled);
		//filterArchiveCategory.setEnabled(enabled);
	}

	public boolean isScheduledPresentation() {
		return currentPresentation != null && ScheduledPresentation.ID.equals(currentPresentation.getId());
	}

	public boolean isFocusedMode() {
		return focusedMode;
	}

	private boolean isAutoExpandMode() {
		return TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.AUTO_EXPAND_TASK_LIST);
	}

	public void setFocusedMode(boolean focusedMode) {
		if (this.focusedMode == focusedMode) {
			return;
		}
		this.focusedMode = focusedMode;
		customDrawer.setFocusedMode(focusedMode);
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		ToolBarManager toolBarManager = getToolBarManager(manager);
		try {
			if (toolBarManager != null) {
				toolBarManager.getControl().setRedraw(false);
			}
			if (focusedMode && isAutoExpandMode()) {
				manager.remove(FilterCompletedTasksAction.ID);
				manager.remove(CollapseAllAction.ID);
			} else if (manager.find(CollapseAllAction.ID) == null) {
				manager.prependToGroup(ID_SEPARATOR_CONTEXT, collapseAll);
				manager.prependToGroup(ID_SEPARATOR_CONTEXT, filterCompleteTask);
			}
			updateFilterEnablement();
			manager.update(false);
		} finally {
			if (toolBarManager != null) {
				toolBarManager.getControl().setRedraw(true);
			}
		}
	}

	private ToolBarManager getToolBarManager(IToolBarManager manager) {
		if (manager instanceof ToolBarManager && ((ToolBarManager) manager).getControl() != null
				&& !((ToolBarManager) manager).getControl().isDisposed()) {
			return (ToolBarManager) manager;
		}
		return null;
	}

	public void displayPrioritiesAbove(String priority) {
		filterPriority.displayPrioritiesAbove(priority);
		getViewer().refresh();
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE.equals(property)
				|| IWorkingSetManager.CHANGE_WORKING_SET_REMOVE.equals(property)) {
			if (getSite() != null && getSite().getPage() != null) {
				if (filterWorkingSet.updateWorkingSet(getSite().getPage().getAggregateWorkingSet())) {
					try {
						getViewer().getControl().setRedraw(false);

						if (drilledIntoCategory != null) {
							goUpToRoot();
						}

						getViewer().refresh();
						if (isFocusedMode() && isAutoExpandMode()) {
							getViewer().expandAll();
						}
					} finally {
						getViewer().getControl().setRedraw(true);
					}
				}
			}

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					filteredTree.indicateActiveTaskWorkingSet();
				}
			});
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
			ITask task = ((TaskEditorInput) input).getTask();
			ITask selected = getSelectedTask();
			if (selected == null || !selected.equals(task)) {
				selectedAndFocusTask(task);
			}
		}
	}

	private void updateToolTip(boolean force) {
		if (taskListToolTip != null && taskListToolTip.isVisible()) {
			if (!force && taskListToolTip.isTriggeredByMouse()) {
				return;
			}

			TreeItem[] selection = getViewer().getTree().getSelection();
			if (selection != null && selection.length > 0) {
				Rectangle bounds = selection[0].getBounds();
				taskListToolTip.show(new Point(bounds.x + 1, bounds.y + 1));
			}
		}
	}

	/**
	 * This can be used for experimentally adding additional presentations, but note that this convention is extremely
	 * likely to change in the Mylyn 3.0 cycle.
	 */
	public static List<AbstractTaskListPresentation> getPresentations() {
		List<AbstractTaskListPresentation> presentations = new ArrayList<AbstractTaskListPresentation>();
		presentations.addAll(presentationsPrimary);
		presentations.addAll(presentationsSecondary);
		return presentations;
	}

	public static void addPresentation(AbstractTaskListPresentation presentation) {
		if (presentation.isPrimary()) {
			presentationsPrimary.add(presentation);
		} else {
			presentationsSecondary.add(presentation);
		}
	}

	public TaskListSorter getSorter() {
		return tableSorter;
	}

	public boolean show(ShowInContext context) {
		ISelection selection = context.getSelection();
		if (selection instanceof IStructuredSelection) {
			getViewer().setSelection(selection, true);
			return true;
		}
		return false;
	}

	private void updateTooltipEnablement() {
		// Set to empty string to disable native tooltips (windows only?)
		// bug#160897
		// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg29614.html
		if (taskListToolTip != null) {
			boolean enabled = TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.getBoolean(ITasksUiPreferenceConstants.TASK_LIST_TOOL_TIPS_ENABLED);
			taskListToolTip.setEnabled(enabled);
			if (getViewer().getTree() != null && !getViewer().getTree().isDisposed()) {
				getViewer().getTree().setToolTipText((enabled) ? "" : null); //$NON-NLS-1$
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == ISizeProvider.class) {
			return new ISizeProvider() {
				public int getSizeFlags(boolean width) {
					if (width) {
						return SWT.MIN;
					}
					return 0;
				}

				public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular,
						int preferredResult) {
					if (width) {
						if (getViewSite().getActionBars().getToolBarManager() instanceof ToolBarManager) {
							Point size = ((ToolBarManager) getViewSite().getActionBars().getToolBarManager()).getControl()
									.computeSize(SWT.DEFAULT, SWT.DEFAULT);
							// leave some room for the view menu drop-down
							return size.x + PlatformUtil.getViewMenuWidth();
						}
					}
					return preferredResult;
				}
			};
		}
		return super.getAdapter(adapter);
	}
}
