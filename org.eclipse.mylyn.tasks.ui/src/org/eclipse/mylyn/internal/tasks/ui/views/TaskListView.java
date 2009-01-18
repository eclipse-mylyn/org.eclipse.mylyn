/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
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
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.CategorizedPresentation;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.ScheduledPresentation;
import org.eclipse.mylyn.internal.tasks.ui.TaskArchiveFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskCompletionFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskPriorityFilter;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.TaskWorkingSetFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CollapseAllAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.EditRepositoryPropertiesAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ExpandAllAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.FilterCompletedTasksAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.GoIntoAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.GoUpAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.GroupSubTasksAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.LinkWithEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTasksUiPreferencesAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.PresentationDropDownSelectionAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.RemoveFromCategoryAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.RenameAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeAutomaticallyAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskListSortAction;
import org.eclipse.mylyn.internal.tasks.ui.commands.CollapseAllHandler;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskListChangeAdapter;
import org.eclipse.mylyn.internal.tasks.ui.util.PlatformUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDragSourceListener;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.TreeVisitor;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
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
import org.eclipse.ui.IPageListener;
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
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
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
			if (TaskListView.this.isFocusedMode()) {
				TaskListView.this.getViewer().expandToLevel(item, 3);
			}
		}
	}

	// TODO e3.4 replace with SWT.NO_SCROLL constant
	public static final int SWT_NO_SCROLL = 1 << 4;

	public static final String ID = "org.eclipse.mylyn.tasks.ui.views.tasks"; //$NON-NLS-1$

	public static final String LABEL_VIEW = Messages.TaskListView_Task_List;

	private static final String MEMENTO_KEY_SORT_DIRECTION = "sortDirection"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_ROOT_SORT_DIRECTION = "rootSortDirection"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_SORTER = "sorter"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_SORTER2 = "sorter2"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_SORT_INDEX = "sortIndex"; //$NON-NLS-1$

	private static final String MEMENTO_SORT_INDEX = "org.eclipse.mylyn.tasklist.ui.views.tasklist.sortIndex"; //$NON-NLS-1$

	private static final String MEMENTO_LINK_WITH_EDITOR = "linkWithEditor"; //$NON-NLS-1$

	private static final String MEMENTO_PRESENTATION = "presentation"; //$NON-NLS-1$

	private static final String ID_SEPARATOR_NEW = "new"; //$NON-NLS-1$

	public static final String ID_SEPARATOR_OPERATIONS = "operations"; //$NON-NLS-1$

	public static final String ID_SEPARATOR_CONTEXT = "context"; //$NON-NLS-1$

	public static final String ID_SEPARATOR_TASKS = "tasks"; //$NON-NLS-1$

	private static final String ID_SEPARATOR_FILTERS = "filters"; //$NON-NLS-1$

	private static final String ID_SEPARATOR_REPOSITORY = "repository"; //$NON-NLS-1$

	private static final String ID_SEPARATOR_PROPERTIES = "properties"; //$NON-NLS-1$

	public static final String ID_SEPARATOR_NAVIGATE = "navigate"; //$NON-NLS-1$

	private static final String LABEL_NO_TASKS = "no task active"; //$NON-NLS-1$

	private final static int SIZE_MAX_SELECTION_HISTORY = 10;

	static final String[] PRIORITY_LEVELS = { PriorityLevel.P1.toString(), PriorityLevel.P2.toString(),
			PriorityLevel.P3.toString(), PriorityLevel.P4.toString(), PriorityLevel.P5.toString() };

	public static final String[] PRIORITY_LEVEL_DESCRIPTIONS = { PriorityLevel.P1.getDescription(),
			PriorityLevel.P2.getDescription(), PriorityLevel.P3.getDescription(), PriorityLevel.P4.getDescription(),
			PriorityLevel.P5.getDescription() };

	private static List<AbstractTaskListPresentation> presentationsPrimary = new ArrayList<AbstractTaskListPresentation>();

	private static List<AbstractTaskListPresentation> presentationsSecondary = new ArrayList<AbstractTaskListPresentation>();

	private boolean focusedMode = false;

	private boolean linkWithEditor;

	private final TaskListCellModifier taskListCellModifier = new TaskListCellModifier(this);

	private IThemeManager themeManager;

	private TaskListFilteredTree filteredTree;

	private DrillDownAdapter drillDownAdapter;

	private AbstractTaskContainer drilledIntoCategory = null;

	private GoIntoAction goIntoAction;

	private GoUpAction goUpAction;

	private CopyTaskDetailsAction copyDetailsAction;

	private OpenTaskListElementAction openAction;

	private OpenWithBrowserAction openWithBrowser;

	private RenameAction renameAction;

	private CollapseAllAction collapseAll;

	private ExpandAllAction expandAll;

	private DeleteAction deleteAction;

	private RemoveFromCategoryAction removeFromCategoryAction;

	private final TaskActivateAction activateAction = new TaskActivateAction();

	private final TaskDeactivateAction deactivateAction = new TaskDeactivateAction();

	private FilterCompletedTasksAction filterCompleteTask;

	private GroupSubTasksAction filterSubTasksAction;

	private SynchronizeAutomaticallyAction synchronizeAutomatically;

	private OpenTasksUiPreferencesAction openPreferencesAction;

	//private FilterArchiveContainerAction filterArchiveCategory;

	private PriorityDropDownAction filterOnPriorityAction;

	private TaskListSortAction sortDialogAction;

	private PresentationDropDownSelectionAction presentationDropDownSelectionAction;

	private LinkWithEditorAction linkWithEditorAction;

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

	private Color categoryGradientStart;

	private Color categoryGradientEnd;

	private CustomTaskListDecorationDrawer customDrawer;

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

	boolean synchronizationOverlaid = false;

	private final Listener CATEGORY_GRADIENT_DRAWER = new Listener() {
		public void handleEvent(Event event) {
			if (event.item.getData() instanceof ITaskContainer && !(event.item.getData() instanceof ITask)) {
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
				// gc.setForeground(new Clr(Display.getCurrent(), 255, 0, 0));

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
			if (scrollable instanceof Table) {
				columnCount = ((Table) scrollable).getColumnCount();
			} else {
				columnCount = ((Tree) scrollable).getColumnCount();
			}

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
						if (ScheduledPresentation.ID.equals(getCurrentPresentation().getId())) {
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
				configureGradientColors();
				taskListTableLabelProvider.setCategoryBackgroundColor(themeManager.getCurrentTheme()
						.getColorRegistry()
						.get(CommonThemes.COLOR_CATEGORY));
				getViewer().refresh();
			}
		}
	};

	private TaskListToolTip taskListToolTip;

	private void configureGradientColors() {
		categoryGradientStart = themeManager.getCurrentTheme().getColorRegistry().get(
				CommonThemes.COLOR_CATEGORY_GRADIENT_START);
		categoryGradientEnd = themeManager.getCurrentTheme().getColorRegistry().get(
				CommonThemes.COLOR_CATEGORY_GRADIENT_END);

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
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not set color: " + red //$NON-NLS-1$
							+ ", " + green + ", " + blue, e)); //$NON-NLS-1$ //$NON-NLS-2$
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
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not set color: " + red //$NON-NLS-1$
							+ ", " + green + ", " + blue, e)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		} else if (categoryGradientStart != null && categoryGradientStart.equals(categoryGradientEnd)) {
			getViewer().getTree().removeListener(SWT.EraseItem, CATEGORY_GRADIENT_DRAWER);
			gradientListenerAdded = false;
		}
	}

	public static TaskListView getFromActivePerspective() {
		if (PlatformUI.isWorkbenchRunning()) {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage != null) {
				IViewPart view = activePage.findView(ID);
				if (view instanceof TaskListView) {
					return (TaskListView) view;
				}
			}
		}
		return null;
	}

	public TaskListView() {
		PlatformUI.getWorkbench().getWorkingSetManager().addPropertyChangeListener(this);
		TasksUiPlugin.getTaskActivityManager().addActivityListener(TASK_ACTIVITY_LISTENER);
		TasksUiPlugin.getTaskActivityManager().addActivationListener(TASK_ACTIVATION_LISTENER);
		TasksUiInternal.getTaskList().addChangeListener(TASKLIST_CHANGE_LISTENER);
	}

	@Override
	public void dispose() {
		super.dispose();
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

		customDrawer.dispose();
		categoryGradientStart.dispose();
		categoryGradientEnd.dispose();
	}

	private void updateDescription() {
		ITask task = TasksUi.getTaskActivityManager().getActiveTask();
		if (getSite() == null || getSite().getPage() == null) {
			return;
		}

		IViewReference reference = getSite().getPage().findViewReference(ID);
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
		IMemento sorter = memento.createChild(MEMENTO_SORT_INDEX);
		IMemento m = sorter.createChild(MEMENTO_KEY_SORTER);
		switch (tableSorter.getComparator().getSortByIndex()) {
		case SUMMARY:
			m.putInteger(MEMENTO_KEY_SORT_INDEX, 1);
			break;
		case DATE_CREATED:
			m.putInteger(MEMENTO_KEY_SORT_INDEX, 2);
			break;
		case TASK_ID:
			m.putInteger(MEMENTO_KEY_SORT_INDEX, 3);
			break;
		default:
			m.putInteger(MEMENTO_KEY_SORT_INDEX, 0);
		}

		m.putInteger(MEMENTO_KEY_SORT_DIRECTION, tableSorter.getComparator().getSortDirection());
		IMemento m2 = sorter.createChild(MEMENTO_KEY_SORTER2);
		switch (tableSorter.getComparator().getSortByIndex2()) {
		case SUMMARY:
			m2.putInteger(MEMENTO_KEY_SORT_INDEX, 1);
			break;
		case DATE_CREATED:
			m2.putInteger(MEMENTO_KEY_SORT_INDEX, 2);
			break;
		case TASK_ID:
			m2.putInteger(MEMENTO_KEY_SORT_INDEX, 3);
			break;
		default:
			m2.putInteger(MEMENTO_KEY_SORT_INDEX, 0);
		}

		m2.putInteger(MEMENTO_KEY_SORT_DIRECTION, tableSorter.getComparator().getSortDirection2());
		memento.putString(MEMENTO_LINK_WITH_EDITOR, Boolean.toString(linkWithEditor));
		memento.putString(MEMENTO_PRESENTATION, currentPresentation.getId());
		memento.putInteger(MEMENTO_KEY_ROOT_SORT_DIRECTION, tableSorter.getSortDirectionRootElement());
	}

	private void restoreState() {
		if (taskListMemento != null) {
			IMemento sorterMemento = taskListMemento.getChild(MEMENTO_SORT_INDEX);
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
						tableSorter.getComparator().setSortDirection(sortDirection);
						switch (restoredSortIndex) {
						case 1:
							tableSorter.getComparator().setSortByIndex(TaskComparator.SortByIndex.SUMMARY);
							break;
						case 2:
							tableSorter.getComparator().setSortByIndex(TaskComparator.SortByIndex.DATE_CREATED);
							break;
						case 3:
							tableSorter.getComparator().setSortByIndex(TaskComparator.SortByIndex.TASK_ID);
							break;
						default:
							tableSorter.getComparator().setSortByIndex(TaskComparator.SortByIndex.PRIORITY);
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
						tableSorter.getComparator().setSortDirection2(sortDirection);
						switch (restoredSortIndex) {
						case 1:
							tableSorter.getComparator().setSortByIndex2(TaskComparator.SortByIndex.SUMMARY);
							break;
						case 2:
							tableSorter.getComparator().setSortByIndex2(TaskComparator.SortByIndex.DATE_CREATED);
							break;
						case 3:
							tableSorter.getComparator().setSortByIndex2(TaskComparator.SortByIndex.TASK_ID);
							break;
						default:
							tableSorter.getComparator().setSortByIndex2(TaskComparator.SortByIndex.PRIORITY);
						}
					}
				}
			}
			applyPresentation(taskListMemento.getString(MEMENTO_PRESENTATION));
			Integer sortOrder = taskListMemento.getInteger(MEMENTO_KEY_ROOT_SORT_DIRECTION);
			if (sortOrder != null) {
				tableSorter.setSortDirectionRootElement(sortOrder);
			} else {
				tableSorter.setSortDirectionRootElement(TaskListSorter.DEFAULT_SORT_DIRECTION);
			}
		}

		filterWorkingSet = new TaskWorkingSetFilter();
		filterWorkingSet.updateWorkingSet(getSite().getPage().getAggregateWorkingSet());
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

		getViewer().refresh();
	}

	@Override
	public void createPartControl(Composite parent) {
		themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		themeManager.addPropertyChangeListener(THEME_CHANGE_LISTENER);

		filteredTree = new TaskListFilteredTree(parent, SWT.MULTI | SWT.VERTICAL | /* SWT.H_SCROLL | */SWT.V_SCROLL
				| SWT_NO_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION, new SubstringPatternFilter());
		// Set to empty string to disable native tooltips (windows only?)
		// bug#160897
		// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg29614.html
		getViewer().getTree().setToolTipText(""); //$NON-NLS-1$

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
		customDrawer = new CustomTaskListDecorationDrawer(this, activationImageOffset);
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
					if (renameAction.isEnabled()) {
						renameAction.run();
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
				if (selectedObject instanceof ITaskContainer) {
					updateActionEnablement(renameAction, (ITaskContainer) selectedObject);
					updateActionEnablement(deleteAction, (ITaskContainer) selectedObject);
				}
			}
		});

		taskListToolTip = new TaskListToolTip(getViewer().getControl());

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

		configureGradientColors();

		initDragAndDrop(parent);
		expandToActiveTasks();
		restoreState();

		updateDescription();

		IContextService contextSupport = (IContextService) getSite().getService(IContextService.class);
		if (contextSupport != null) {
			contextSupport.activateContext(TaskListView.ID);
		}

		getSite().setSelectionProvider(getViewer());
		getSite().getPage().addPartListener(editorListener);

		// Need to do this because the page, which holds the active working set is not around on creation, see bug 203179
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPageListener(PAGE_LISTENER);
	}

	private void initHandlers() {
		IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
		handlerService.activateHandler(CollapseAllHandler.ID_COMMAND, new CollapseAllHandler(getViewer()));
	}

	private void hookGlobalActions() {
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyDetailsAction);
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
			if (!filteredTree.getFilterControl().getText().equals("")) { //$NON-NLS-1$
				filteredTree.getFilterControl().setText(""); //$NON-NLS-1$
			}
			AbstractTaskListContentProvider contentProvider = presentation.getContentProvider(this);
			getViewer().setContentProvider(contentProvider);
			refresh(true);

			currentPresentation = presentation;
		} finally {
			getViewer().getControl().setRedraw(true);
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
					tableSorter.getComparator().setSortDirection(tableSorter.getComparator().getSortDirection() * -1);
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
				IViewReference reference = getSite().getPage().findViewReference(ID);
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
		Transfer[] dragTypes = new Transfer[] { TaskTransfer.getInstance(), FileTransfer.getInstance() };

		Transfer[] dropTypes = new Transfer[] { TaskTransfer.getInstance(), FileTransfer.getInstance(),
				TextTransfer.getInstance(), RTFTransfer.getInstance() };

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
		manager.add(sortDialogAction);
		manager.add(filterOnPriorityAction);
		manager.add(filterCompleteTask);
		//manager.add(filterArchiveCategory);
		manager.add(filterSubTasksAction);

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
		manager.add(new Separator());
		manager.add(collapseAll);
		manager.add(new GroupMarker(ID_SEPARATOR_CONTEXT));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/*
	 * TODO: clean up, consider relying on extension points for groups
	 */
	private void fillContextMenu(final IMenuManager manager) {
		updateDrillDownActions();
		final ITaskContainer element;

		final Object firstSelectedObject = ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
		if (firstSelectedObject instanceof ITaskContainer) {
			element = (ITaskContainer) firstSelectedObject;
		} else {
			element = null;
		}
		final List<IRepositoryElement> selectedElements = getSelectedTaskContainers();
		AbstractTask task = null;
		if (element instanceof ITask) {
			task = (AbstractTask) element;
		}

		manager.add(new Separator(ID_SEPARATOR_NEW));
		manager.add(new Separator());

		if (element instanceof ITask) {
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

		Map<String, List<IDynamicSubMenuContributor>> dynamicMenuMap = TasksUiPlugin.getDefault().getDynamicMenuMap();
		for (String menuPath : dynamicMenuMap.keySet()) {
			if (!ID_SEPARATOR_CONTEXT.equals(menuPath)) {
				for (final IDynamicSubMenuContributor contributor : dynamicMenuMap.get(menuPath)) {
					SafeRunnable.run(new ISafeRunnable() {
						public void handleException(Throwable e) {
							StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
									"Menu contributor failed")); //$NON-NLS-1$
						}

						public void run() throws Exception {
							MenuManager subMenuManager = contributor.getSubMenuManager(selectedElements);
							if (subMenuManager != null) {
								addMenuManager(subMenuManager, manager, element);
							}
						}

					});
				}
			}
		}
		manager.add(new Separator(ID_SEPARATOR_NAVIGATE));
//		manager.add(new Separator(ID_SEPARATOR_OPERATIONS));
		manager.add(new Separator());

		addAction(copyDetailsAction, manager, element);

		boolean enableRemove = true;
		for (IRepositoryElement repositoryElement : selectedElements) {
			if (repositoryElement instanceof ITask) {
				AbstractTaskCategory tempCategory = TaskCategory.getParentTaskCategory((AbstractTask) repositoryElement);
				if (tempCategory == null) {
					enableRemove = false;
					break;
				}
			}
		}
		if (enableRemove) {
			addAction(removeFromCategoryAction, manager, element);
		}

		// This should also test for null, or else nothing to delete!
		addAction(deleteAction, manager, element);
		if (!(element instanceof ITask)) {
			addAction(renameAction, manager, element);
		}

		if (element != null && !(element instanceof ITask)) {
			manager.add(goIntoAction);
		}
		if (drilledIntoCategory != null) {
			manager.add(goUpAction);
		}
		manager.add(new Separator(ID_SEPARATOR_CONTEXT));
		manager.add(new Separator(ID_SEPARATOR_OPERATIONS));

		if (element instanceof ITask) {
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
		if (element instanceof IRepositoryQuery) {
			EditRepositoryPropertiesAction repositoryPropertiesAction = new EditRepositoryPropertiesAction();
			repositoryPropertiesAction.selectionChanged(new StructuredSelection(element));
			if (repositoryPropertiesAction.isEnabled()) {
				MenuManager subMenu = new MenuManager(Messages.TaskListView_Repository);
				manager.add(subMenu);

				UpdateRepositoryConfigurationAction resetRepositoryConfigurationAction = new UpdateRepositoryConfigurationAction();
				resetRepositoryConfigurationAction.selectionChanged(new StructuredSelection(element));
				subMenu.add(resetRepositoryConfigurationAction);
				subMenu.add(new Separator());
				subMenu.add(repositoryPropertiesAction);
			}
		}
		manager.add(new Separator(ID_SEPARATOR_REPOSITORY));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		manager.add(new Separator());
		manager.add(new Separator(ID_SEPARATOR_PROPERTIES));
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

	private void addMenuManager(IMenuManager menuToAdd, IMenuManager manager, ITaskContainer element) {
		if ((element instanceof ITask) || element instanceof IRepositoryQuery) {
			manager.add(menuToAdd);
		}
	}

	private void addAction(Action action, IMenuManager manager, ITaskContainer element) {
		manager.add(action);
		if (element != null) {
			updateActionEnablement(action, element);
		}
	}

	// FIXME move the enablement to the action classes
	private void updateActionEnablement(Action action, ITaskContainer element) {
		if (element instanceof ITask) {
			if (action instanceof OpenWithBrowserAction) {
				if (TasksUiInternal.isValidUrl(((ITask) element).getUrl())) {
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
			if (action instanceof DeleteAction) {
				if (element instanceof UncategorizedTaskContainer) {
					action.setEnabled(false);
				} else {
					action.setEnabled(true);
				}
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
					action.setEnabled(container.isUserManaged());
				} else if (element instanceof IRepositoryQuery) {
					action.setEnabled(true);
				}
			}
		} else {
			action.setEnabled(true);
		}
	}

	private void makeActions() {
		copyDetailsAction = new CopyTaskDetailsAction();
		copyDetailsAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.COPY);

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
		openWithBrowser = new OpenWithBrowserAction();
		filterCompleteTask = new FilterCompletedTasksAction(this);
		filterSubTasksAction = new GroupSubTasksAction(this);
		synchronizeAutomatically = new SynchronizeAutomaticallyAction();
		openPreferencesAction = new OpenTasksUiPreferencesAction();
		//filterArchiveCategory = new FilterArchiveContainerAction(this);
		sortDialogAction = new TaskListSortAction(getSite(), this);
		filterOnPriorityAction = new PriorityDropDownAction(this);
		linkWithEditorAction = new LinkWithEditorAction(this);
		presentationDropDownSelectionAction = new PresentationDropDownSelectionAction(this);

		filteredTree.getViewer().addSelectionChangedListener(openWithBrowser);
		filteredTree.getViewer().addSelectionChangedListener(copyDetailsAction);
	}

	private void hookOpenAction() {
		getViewer().addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				openAction.run();
			}
		});

		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
						ITasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED)) {
					AbstractTask selectedTask = TaskListView.getFromActivePerspective().getSelectedTask();
					if (selectedTask != null) {
						activateAction.run(selectedTask);
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
		if (expandIfFocused && isFocusedMode()) {
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
//		if (expand) {
//			getViewer().expandAll();
//		}
//		getViewer().refresh();
//		refresh(null);
//		selectedAndFocusTask(TasksUiPlugin.getTaskList().getActiveTask());
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
		if (drillDownAdapter.canGoBack()) {
			goUpAction.setEnabled(true);
		} else {
			goUpAction.setEnabled(false);
		}
	}

	boolean isInRenameAction = false;

	private DelayedRefreshJob refreshJob;

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
		getViewer().setSelection(selection, true);
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
			return TasksUiPlugin.getDefault().getPreferenceStore().getString(
					ITasksUiPreferenceConstants.FILTER_PRIORITY);
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
			Text textControl = filteredTree.getFilterControl();
			if (textControl != null && textControl.getText().length() > 0) {
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

	public void setFocusedMode(boolean focusedMode) {
		if (this.focusedMode == focusedMode) {
			return;
		}
		this.focusedMode = focusedMode;
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		if (focusedMode) {
			manager.remove(CollapseAllAction.ID);
		} else {
			manager.prependToGroup(ID_SEPARATOR_CONTEXT, collapseAll);
		}
		manager.update(false);
		updateFilterEnablement();
	}

	public void setSynchronizationOverlaid(boolean synchronizationOverlaid) {
		this.synchronizationOverlaid = synchronizationOverlaid;
		getViewer().refresh();
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
						// XXX why is this needed?
						//getViewer().collapseAll();
						getViewer().refresh();
						if (isFocusedMode()) {
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
				taskListToolTip.show(new Point(bounds.x, bounds.y));
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
				if (workingSet.getId() == null
						|| !workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
					tasksSets.remove(workingSet);
				}
			}
			return tasksSets;
		} else {
			return Collections.emptySet();
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

}
