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

package org.eclipse.mylar.internal.tasks.ui.views;

import java.util.ArrayList;
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
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylar.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylar.internal.tasks.ui.TaskArchiveFilter;
import org.eclipse.mylar.internal.tasks.ui.TaskCompletionFilter;
import org.eclipse.mylar.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylar.internal.tasks.ui.TaskListPatternFilter;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.TaskPriorityFilter;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.internal.tasks.ui.actions.CollapseAllAction;
import org.eclipse.mylar.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylar.internal.tasks.ui.actions.DeleteAction;
import org.eclipse.mylar.internal.tasks.ui.actions.ExpandAllAction;
import org.eclipse.mylar.internal.tasks.ui.actions.FilterArchiveContainerAction;
import org.eclipse.mylar.internal.tasks.ui.actions.FilterCompletedTasksAction;
import org.eclipse.mylar.internal.tasks.ui.actions.FilterSubTasksAction;
import org.eclipse.mylar.internal.tasks.ui.actions.GoIntoAction;
import org.eclipse.mylar.internal.tasks.ui.actions.GoUpAction;
import org.eclipse.mylar.internal.tasks.ui.actions.MarkTaskCompleteAction;
import org.eclipse.mylar.internal.tasks.ui.actions.MarkTaskIncompleteAction;
import org.eclipse.mylar.internal.tasks.ui.actions.NewLocalTaskAction;
import org.eclipse.mylar.internal.tasks.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylar.internal.tasks.ui.actions.OpenTasksUiPreferencesAction;
import org.eclipse.mylar.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylar.internal.tasks.ui.actions.PresentationDropDownSelectionAction;
import org.eclipse.mylar.internal.tasks.ui.actions.PreviousTaskDropDownAction;
import org.eclipse.mylar.internal.tasks.ui.actions.RemoveFromCategoryAction;
import org.eclipse.mylar.internal.tasks.ui.actions.RenameAction;
import org.eclipse.mylar.internal.tasks.ui.actions.SynchronizeAutomaticallyAction;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskListElementPropertiesAction;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskActivityListener;
import org.eclipse.mylar.tasks.core.ITaskListChangeListener;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskArchive;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.ui.TaskTransfer;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
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
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class TaskListView extends ViewPart {

	private final class CUSTOM_DECORATION_DRAWER implements Listener {

		private final int activationImageOffset;

		private Image taskActive = TasksUiImages.getImage(TasksUiImages.TASK_ACTIVE);

		private Image taskInactive = TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE);

		private Image taskInactiveContext = TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE_CONTEXT);

		boolean overlaySynchronization = false;

		private CUSTOM_DECORATION_DRAWER(int activationImageOffset) {
			this.activationImageOffset = activationImageOffset;
		}

		private int currWidth = 0;

		/*
		 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
		 * Therefore, it is critical for performance that these methods be as
		 * efficient as possible.
		 */
		public void handleEvent(Event event) {
			Object data = event.item.getData();
			ITask task = null;
			Image image = null;
			if (data instanceof ITask) {
				task = (ITask) data;
			} else if (data instanceof AbstractQueryHit) {
				task = ((AbstractQueryHit) data).getCorrespondingTask();
			}
			if (task != null) {
				if (task.isActive()) {
					image = taskActive;
				} else if (ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
					image = taskInactiveContext;
				} else {
					image = taskInactive;
				}
			} else if (data instanceof AbstractQueryHit) {
				image = taskInactive;
			}
			if (image != null) {
				switch (event.type) {
				case SWT.MeasureItem: {
					break;
				}
				case SWT.EraseItem: {
					drawActivationImage(activationImageOffset, event, image);
					currWidth = event.width;
					break;
				}
				case SWT.PaintItem: {
					drawActivationImage(activationImageOffset, event, image);
					if (data instanceof ITaskListElement) {
						drawSyncronizationImage((ITaskListElement)data, event);
					}
//					drawPriorityImage(task, event);
					break;
				}
				}
			}
		}

		private void drawSyncronizationImage(ITaskListElement task, Event event) {
			if (overlaySynchronization) {
				Image image = TasksUiImages.getImage(TaskElementLabelProvider
						.getSynchronizationImageDescriptor(task));
				if (image != null) {
					event.gc.drawImage(image, event.x + 17, event.y + 3);
				}
			} else {
				Image image = TasksUiImages.getCompositeSynchImage(TaskElementLabelProvider
						.getSynchronizationImageDescriptor(task), true);
				if (image != null) {
					event.gc.drawImage(image, currWidth - 16, event.y);
				}
			}
		}

//		private void drawPriorityImage(ITask task, Event event) {
//			ImageDescriptor descriptor = TaskElementLabelProvider.getPriorityImageDescriptor(task);
//			if (descriptor != null) {
//				Image image = TasksUiImages.getImage(descriptor);
//				if (image != null) {
//					event.gc.drawImage(image, event.x + CompositeTaskImageDescriptor.WIDTH_DECORATION-4, event.y);
//				}
//			}
//		}

		private void drawActivationImage(final int activationImageOffset, Event event, Image image) {
			Rectangle rect = image.getBounds();
			int offset = Math.max(0, (event.height - rect.height) / 2);
			event.gc.drawImage(image, activationImageOffset, event.y + offset);
		}
	}

	private static final String PRESENTATION_SCHEDULED = "Scheduled";

	public static final String ID = "org.eclipse.mylar.tasks.ui.views.TaskListView";

	public static final String LABEL_VIEW = "Task List";

	private static final String MEMENTO_KEY_SORT_DIRECTION = "sortDirection";

	private static final String MEMENTO_KEY_SORTER = "sorter";

	private static final String MEMENTO_KEY_SORT_INDEX = "sortIndex";

	private static final String ID_SEPARATOR_NEW = "new";

	private static final String ID_SEPARATOR_CONTEXT = "context";

	private static final String ID_SEPARATOR_TASKS = "tasks";

	private static final String ID_SEPARATOR_NAVIGATION = "navigation";

	private static final String ID_SEPARATOR_FILTERS = "filters";

	private static final String ID_SEPARATOR_REPOSITORY = "repository";

	private static final String LABEL_NO_TASKS = "no task active";

	static final String[] PRIORITY_LEVELS = { Task.PriorityLevel.P1.toString(), Task.PriorityLevel.P2.toString(),
			Task.PriorityLevel.P3.toString(), Task.PriorityLevel.P4.toString(), Task.PriorityLevel.P5.toString() };

	public static final String[] PRIORITY_LEVEL_DESCRIPTIONS = { Task.PriorityLevel.P1.getDescription(),
			Task.PriorityLevel.P2.getDescription(), Task.PriorityLevel.P3.getDescription(),
			Task.PriorityLevel.P4.getDescription(), Task.PriorityLevel.P5.getDescription() };

	private static final String PART_NAME = "Task List";

	private boolean focusedMode = false;

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

	private NewLocalTaskAction newLocalTaskAction;

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

	PreviousTaskDropDownAction previousTaskAction;

	private PresentationDropDownSelectionAction presentationDropDownSelectionAction;

	static TaskPriorityFilter FILTER_PRIORITY = new TaskPriorityFilter();

	private static TaskCompletionFilter FILTER_COMPLETE = new TaskCompletionFilter();

	private static TaskArchiveFilter FILTER_ARCHIVE = new TaskArchiveFilter();

	private Set<AbstractTaskListFilter> filters = new HashSet<AbstractTaskListFilter>();

	protected String[] columnNames = new String[] { "Summary" };

	protected int[] columnWidths = new int[] { 200 };

	private TreeColumn[] columns;

	private IMemento taskListMemento;

	public static final String columnWidthIdentifier = "org.eclipse.mylar.tasklist.ui.views.tasklist.columnwidth";

	public static final String tableSortIdentifier = "org.eclipse.mylar.tasklist.ui.views.tasklist.sortIndex";

	private static final int DEFAULT_SORT_DIRECTION = 1;

	private int sortIndex = 0;

	private ITaskListPresentation currentPresentation;

	private TaskListTableLabelProvider taskListTableLabelProvider;

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

	private final Listener CATEGORY_GRADIENT_DRAWER = new Listener() {
		public void handleEvent(Event event) {
			if (event.item.getData() instanceof AbstractTaskContainer) {
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
		public void taskActivated(final ITask task) {
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

		public void tasksActivated(List<ITask> tasks) {
			if (tasks.size() == 1) {
				taskActivated(tasks.get(0));
			}
		}

		public void taskDeactivated(final ITask task) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(task);
					updateDescription(null);
					filteredTree.indicateNoActiveTask();
				}
			});
		}

		public void activityChanged(DateRangeContainer week) {
			// ignore
		}

		public void taskListRead() {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(null);
				}
			});
		}

		public void calendarChanged() {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(null);
				}
			});
		}
	};

	private final ITaskListChangeListener TASK_REFERESH_LISTENER = new ITaskListChangeListener() {

		public void localInfoChanged(final ITask task) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (getCurrentPresentation().getPresentationName().equals(
							scheduledPresentation.getPresentationName())) {
						refresh(null);
					} else {
						refresh(task);
					}
				}
			});
			if (task.isActive()) {
				String activeTaskLabel = filteredTree.getActiveTaskLabelText();
				if (activeTaskLabel != null && !activeTaskLabel.equals(task.getSummary())) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							filteredTree.indicateActiveTask(task);
						}
					});
				}
			}
		}

		public void repositoryInfoChanged(ITask task) {
			localInfoChanged(task);
		}

		public void taskMoved(final ITask task, final AbstractTaskContainer fromContainer,
				final AbstractTaskContainer toContainer) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					// category might appear or disappear
					refresh(null);
					AbstractTaskContainer rootCategory = TasksUiPlugin.getTaskListManager().getTaskList()
							.getRootCategory();
					if (rootCategory.equals(fromContainer) || rootCategory.equals(toContainer)) {
						refresh(null);
					} else {
						refresh(toContainer);
						refresh(task);
						refresh(fromContainer);
					}
				}
			});
		}

		public void taskDeleted(ITask task) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(null);
				}
			});
		}

		public void containerAdded(AbstractTaskContainer container) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(null);
				}
			});
		}

		public void containerDeleted(AbstractTaskContainer container) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(null);
				}
			});
		}

		public void taskAdded(ITask task) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(null);
				}
			});
		}

		public void containerInfoChanged(final AbstractTaskContainer container) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (container == null) {
						// HACK: should be part of policy
						getViewer().refresh(false);
					} else if (container.equals(TasksUiPlugin.getTaskListManager().getTaskList().getRootCategory())) {
						refresh(null);
					} else {
						refresh(container);
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
				taskListTableLabelProvider.setCategoryBackgroundColor(themeManager.getCurrentTheme().getColorRegistry()
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

		if (gradientListenerAdded == false && categoryGradientStart != null
				&& !categoryGradientStart.equals(categoryGradientEnd)) {
			getViewer().getTree().addListener(SWT.EraseItem, CATEGORY_GRADIENT_DRAWER);

			// TODO: weird override of custom gradients
			Color parentBackground = getViewer().getTree().getParent().getBackground();
			double GRADIENT_TOP = 1.02;
			double GRADIENT_BOTTOM = 1.035;

			int red = Math.min(255, (int) (parentBackground.getRed() * GRADIENT_TOP));
			int green = Math.min(255, (int) (parentBackground.getGreen() * GRADIENT_TOP));
			int blue = Math.min(255, (int) (parentBackground.getBlue() * GRADIENT_TOP));

			categoryGradientStart = new Color(Display.getDefault(), red, green, blue);

			red = Math.max(0, (int) (parentBackground.getRed() / GRADIENT_BOTTOM));
			green = Math.max(0, (int) (parentBackground.getGreen() / GRADIENT_BOTTOM));
			blue = Math.max(0, (int) (parentBackground.getBlue() / GRADIENT_BOTTOM));
			categoryGradientEnd = new Color(Display.getDefault(), red, green, blue);

			gradientListenerAdded = true;
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
			return null;
		}
	}

	public TaskListView() {
		TasksUiPlugin.getTaskListManager().addActivityListener(TASK_ACTIVITY_LISTENER);
		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(TASK_REFERESH_LISTENER);
	}

	@Override
	public void dispose() {
		super.dispose();
		TasksUiPlugin.getTaskListManager().getTaskList().removeChangeListener(TASK_REFERESH_LISTENER);
		TasksUiPlugin.getTaskListManager().removeActivityListener(TASK_ACTIVITY_LISTENER);

		final IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		if (themeManager != null) {
			themeManager.removePropertyChangeListener(THEME_CHANGE_LISTENER);
		}

		categoryGradientStart.dispose();
		categoryGradientEnd.dispose();
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

	public void addTaskToHistory(ITask task) {
		if (!TasksUiPlugin.getDefault().isMultipleActiveTasksMode()) {
			TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(task);
			// nextTaskAction.setEnabled(taskHistory.hasNext());
			// previousTaskAction.setEnabled(TasksUiPlugin.getTaskListManager().getTaskActivationHistory().hasPrevious());
		}
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
		m.putInteger(MEMENTO_KEY_SORT_INDEX, sortIndex);
		m.putInteger(MEMENTO_KEY_SORT_DIRECTION, sortDirection);
	}

	private void restoreState() {
		if (taskListMemento != null) {
			IMemento sorterMemento = taskListMemento.getChild(tableSortIdentifier);
			if (sorterMemento != null) {
				IMemento m = sorterMemento.getChild(MEMENTO_KEY_SORTER);
				if (m != null) {
					Integer sortIndexInt = m.getInteger(MEMENTO_KEY_SORT_INDEX);
					if (sortIndexInt != null) {
						this.sortIndex = sortIndexInt.intValue();
					}
					Integer sortDirInt = m.getInteger(MEMENTO_KEY_SORT_DIRECTION);
					if (sortDirInt != null) {
						sortDirection = sortDirInt.intValue();
					}
				} else {
					sortIndex = 0;
					sortDirection = DEFAULT_SORT_DIRECTION;
				}
			} else {
				sortIndex = 0; // default priority
				sortDirection = DEFAULT_SORT_DIRECTION;
			}
			if (sortIndex == 1) {
				setSortByPriority(false);
			} else {
				setSortByPriority(true);
			}
		}
		addFilter(FILTER_PRIORITY);
		if (TasksUiPlugin.getDefault().getPreferenceStore().contains(TaskListPreferenceConstants.FILTER_COMPLETE_MODE))
			addFilter(FILTER_COMPLETE);

		if (TasksUiPlugin.getDefault().getPreferenceStore().contains(TaskListPreferenceConstants.FILTER_ARCHIVE_MODE))
			addFilter(FILTER_ARCHIVE);

		if (TasksUiPlugin.getDefault().isMultipleActiveTasksMode()) {
			togglePreviousAction(false);
		}

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
		taskListTableLabelProvider = new TaskListTableLabelProvider(new TaskElementLabelProvider(true, getViewer()),
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

		tableSorter = new TaskListTableSorter(this, true);
		getViewer().setSorter(tableSorter);

		applyPresentation(catagorizedPresentation);

		drillDownAdapter = new DrillDownAdapter(getViewer());
		getViewer().setInput(getViewSite());

		final int activationImageOffset = 23;
		CUSTOM_DECORATION_DRAWER customDrawer = new CUSTOM_DECORATION_DRAWER(activationImageOffset);
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
					if (selectedObject instanceof ITask || selectedObject instanceof AbstractQueryHit) {
						if (e.x > activationImageOffset - 2 && e.x < activationImageOffset + 16) {
							taskListCellModifier.toggleTaskActivation((ITaskListElement) selectedObject);
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
					newLocalTaskAction.run();
				} else if ((e.keyCode & SWT.KEYCODE_BIT) != 0) {
					// Do nothing here since it is key code
				} else if (e.keyCode == SWT.F2 && e.stateMask == 0) {
					if (renameAction.isEnabled()) {
						renameAction.run();
					}
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

		// Set to empty string to disable native tooltips (windows only?)
		// bug#160897
		// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg29614.html
		getViewer().getTree().setToolTipText("");

		configureGradientColors();

		initDragAndDrop(parent);
		expandToActiveTasks();
		restoreState();

		List<ITask> activeTasks = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTasks();
		if (activeTasks.size() > 0) {
			updateDescription(activeTasks.get(0));
		}
		getSite().setSelectionProvider(getViewer());
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

			final int index = i;
			columns[i].addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					sortIndex = index;
					sortDirection *= DEFAULT_SORT_DIRECTION;
					tableSorter.setColumn(columnNames[sortIndex]);
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
				List<ITask> activeTasks = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTasks();
				for (ITask t : activeTasks) {
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
		manager.add(showSubTasksAction);
		manager.add(filterArchiveCategory);

		manager.add(new Separator(ID_SEPARATOR_TASKS));

		manager.add(synchronizeAutomatically);

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				filterOnPriorityAction.updateCheckedState();
			}
		});

		manager.add(new Separator());
		manager.add(openPreferencesAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator(ID_SEPARATOR_NEW));
		manager.add(new Separator(ID_SEPARATOR_NAVIGATION));
		manager.add(presentationDropDownSelectionAction);
		manager.add(previousTaskAction);
		manager.add(new Separator(ID_SEPARATOR_CONTEXT));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/*
	 * TODO: clean up, consider relying on extension points for groups
	 */
	private void fillContextMenu(IMenuManager manager) {
		updateDrillDownActions();
		ITaskListElement element = null;

		final Object firstSelectedObject = ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
		if (firstSelectedObject instanceof ITaskListElement) {
			element = (ITaskListElement) firstSelectedObject;
		}
		List<ITaskListElement> selectedElements = new ArrayList<ITaskListElement>();
		for (Iterator<?> i = ((IStructuredSelection) getViewer().getSelection()).iterator(); i.hasNext();) {
			Object object = i.next();
			if (object instanceof ITaskListElement) {
				selectedElements.add((ITaskListElement) object);
			}
		}
		ITask task = null;
		if ((element instanceof ITask) || (element instanceof AbstractQueryHit)) {
			if (element instanceof AbstractQueryHit) {
				task = ((AbstractQueryHit) element).getCorrespondingTask();
			} else {
				task = (ITask) element;
			}
		}

		manager.add(new Separator(ID_SEPARATOR_NEW));
		manager.add(new Separator());

		Map<String, List<IDynamicSubMenuContributor>> dynamicMenuMap = TasksUiPlugin.getDefault().getDynamicMenuMap();

		if (!(element instanceof AbstractTaskContainer)) {
			addAction(openAction, manager, element);
		}
		addAction(openWithBrowser, manager, element);
		if (task != null) {
			if (task.isActive()) {
				manager.add(deactivateAction);
			} else {
				manager.add(activateAction);
			}
		} else if (element instanceof AbstractQueryHit) {
			manager.add(activateAction);
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
		if (task != null && !(element instanceof AbstractQueryHit)) {
			addAction(removeFromCategoryAction, manager, element);
		}
		addAction(deleteAction, manager, element);
		if (!(element instanceof AbstractRepositoryTask) || element instanceof AbstractTaskContainer) {
			addAction(renameAction, manager, element);
		}

		if (element instanceof AbstractTaskContainer) {
			manager.add(goIntoAction);
		}
		if (drilledIntoCategory != null) {
			manager.add(goUpAction);
		}
		manager.add(new Separator(ID_SEPARATOR_REPOSITORY));
		manager.add(new Separator(ID_SEPARATOR_CONTEXT));

		if (element instanceof ITask || element instanceof AbstractQueryHit) {
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

		if (element instanceof AbstractRepositoryQuery || element instanceof TaskCategory) {
			manager.add(new Separator());
			addAction(propertiesAction, manager, element);
		}

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void addMenuManager(IMenuManager menuToAdd, IMenuManager manager, ITaskListElement element) {
		if ((element instanceof ITask || element instanceof AbstractQueryHit)
				|| (element instanceof AbstractTaskContainer || element instanceof AbstractRepositoryQuery)) {
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
			if (action instanceof OpenWithBrowserAction) {
				if (((ITask) element).hasValidUrl()) {
					action.setEnabled(true);
				} else {
					action.setEnabled(false);
				}
			} else if (action instanceof DeleteAction) {
				action.setEnabled(true);
			} else if (action instanceof NewLocalTaskAction) {
				action.setEnabled(false);
			} else if (action instanceof OpenTaskListElementAction) {
				action.setEnabled(true);
			} else if (action instanceof CopyTaskDetailsAction) {
				action.setEnabled(true);
			} else if (action instanceof RenameAction) {
				action.setEnabled(true);
			}
		} else if (element instanceof AbstractTaskContainer) {
			if (action instanceof MarkTaskCompleteAction) {
				action.setEnabled(false);
			} else if (action instanceof MarkTaskIncompleteAction) {
				action.setEnabled(false);
			} else if (action instanceof DeleteAction) {
				if (element instanceof TaskArchive)
					action.setEnabled(false);
				else
					action.setEnabled(true);
			} else if (action instanceof NewLocalTaskAction) {
				if (element instanceof TaskArchive)
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
				if (element instanceof AbstractTaskContainer) {
					AbstractTaskContainer container = (AbstractTaskContainer) element;
					action.setEnabled(container.canRename());
				}
				// if (element instanceof TaskArchive)
				// action.setEnabled(false);
				// else
				// action.setEnabled(true);
			}
		} else {
			action.setEnabled(true);
		}
		// if(!canEnableGoInto){
		// goIntoAction.setEnabled(false);
		// }
	}

	private void makeActions() {

		copyDetailsAction = new CopyTaskDetailsAction(true);

		goIntoAction = new GoIntoAction();
		goUpAction = new GoUpAction(drillDownAdapter);

		newLocalTaskAction = new NewLocalTaskAction(this);
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
		previousTaskAction = new PreviousTaskDropDownAction(this, TasksUiPlugin.getTaskListManager()
				.getTaskActivationHistory());
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

	public PreviousTaskDropDownAction getPreviousTaskAction() {
		return previousTaskAction;
	}

	/**
	 * Recursive function that checks for the occurrence of a certain task
	 * taskId. All children of the supplied node will be checked.
	 * 
	 * @param task
	 *            The <code>ITask</code> object that is to be searched.
	 * @param taskId
	 *            The taskId that is being searched for.
	 * @return <code>true</code> if the taskId was found in the node or any of
	 *         its children
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
						TaskListPreferenceConstants.ACTIVATE_ON_OPEN)) {
					ITask selectedTask = TaskListView.getFromActivePerspective().getSelectedTask();
					if (selectedTask != null) {
						// TODO: move history stuff
						activateAction.run(selectedTask);
						addTaskToHistory(selectedTask);
						previousTaskAction.setButtonStatus();
					}
				}
				if (object instanceof TaskCategory || object instanceof AbstractRepositoryQuery) {
					TasksUiUtil.refreshAndOpenTaskListElement((ITaskListElement) object);
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
		return FILTER_COMPLETE;
	}

	public TaskPriorityFilter getPriorityFilter() {
		return FILTER_PRIORITY;
	}

	public void addFilter(AbstractTaskListFilter filter) {
		if (!filters.contains(filter)) {
			filters.add(filter);
		}
	}

	public void clearFilters(boolean preserveArchiveFilter) {
		filters.clear();
		if (preserveArchiveFilter) {
			filters.add(FILTER_ARCHIVE);
		}
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

	public static ITask getSelectedTask(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			if (structuredSelection.size() != 1) {
				return null;
			}
			Object element = structuredSelection.getFirstElement();
			if (element instanceof ITask) {
				return (ITask) structuredSelection.getFirstElement();
			} else if (element instanceof AbstractQueryHit) {
				return ((AbstractQueryHit) element).getCorrespondingTask();
			}
		}
		return null;
	}

	public void indicatePaused(boolean paused) {
		isPaused = paused;
		IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
		if (isPaused) {
			statusLineManager
					.setMessage(TasksUiImages.getImage(TasksUiImages.TASKLIST), "Mylar context capture paused");
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

	public void selectedAndFocusTask(ITask task) {
		if (task == null || getViewer().getControl().isDisposed()) {
			return;
		}

		saveSelection();

		IStructuredSelection selection = restoreSelection(task);
		getViewer().setSelection(selection, true);

		// if no task exists, select the query hit if exists
		AbstractQueryHit hit = null;
		if (getViewer().getSelection().isEmpty()
				&& (hit = TasksUiPlugin.getTaskListManager().getTaskList().getQueryHit(task.getHandleIdentifier())) != null) {
			try {
				AbstractRepositoryQuery query = TasksUiPlugin.getTaskListManager().getTaskList().getQueryForHandle(
						task.getHandleIdentifier());
				getViewer().expandToLevel(query, 1);
				getViewer().setSelection(new StructuredSelection(hit), true);
			} catch (SWTException e) {
				MylarStatusHandler.log(e, "Failed to expand Task List");
			}
		}
	}

	private void saveSelection() {
		IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		if (!selection.isEmpty()) {
			if (selection.getFirstElement() instanceof ITaskListElement) {
				// make sure the new selection is inserted at the end of the
				// list
				String handle = ((ITaskListElement) selection.getFirstElement()).getHandleIdentifier();
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

	private IStructuredSelection restoreSelection(ITaskListElement task) {
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
	private void refresh(final ITaskListElement element) {
		if (getViewer().getControl() != null && !getViewer().getControl().isDisposed()) {
			if (element == null) {
				try {
					// getViewer().getControl().setRedraw(false);
					getViewer().refresh(true);
				} finally {
					// getViewer().getControl().setRedraw(true);
				}
			} else {
				try {
					if (element instanceof ITask) {
						ITask task = (ITask) element;
						AbstractTaskContainer rootCategory = TasksUiPlugin.getTaskListManager().getTaskList()
								.getRootCategory();
						Set<AbstractRepositoryQuery> queries = TasksUiPlugin.getTaskListManager().getTaskList()
								.getQueriesForHandle(task.getHandleIdentifier());
						if (task.getContainer() == null || task.getContainer().equals(rootCategory)
								|| (task instanceof AbstractRepositoryTask && queries.isEmpty())) {
							// || task.getContainer() instanceof TaskArchive) {
							refresh(null);
						} else {
							getViewer().refresh(task.getContainer(), true);
							// refresh(task.getContainer());
						}

						AbstractQueryHit hit = TasksUiPlugin.getTaskListManager().getTaskList().getQueryHit(
								task.getHandleIdentifier());
						if (hit != null) {
							refresh(hit);
						}
					} else if (element instanceof AbstractQueryHit) {
						AbstractQueryHit hit = (AbstractQueryHit) element;
						Set<AbstractRepositoryQuery> queries = TasksUiPlugin.getTaskListManager().getTaskList()
								.getQueriesForHandle(hit.getHandleIdentifier());
						for (AbstractRepositoryQuery query : queries) {
							refresh(query);
						}
					} else if (element instanceof AbstractTaskContainer) {
						getViewer().refresh(element, true);
					} else {
						getViewer().refresh(element, true);
					}
				} catch (SWTException e) {
					MylarStatusHandler.log(e, "Failed to refresh Task List");
				}
			}
		}
	}

	public Image[] getPirorityImages() {
		Image[] images = new Image[Task.PriorityLevel.values().length];
		for (int i = 0; i < Task.PriorityLevel.values().length; i++) {
			images[i] = TasksUiImages.getImageForPriority(Task.PriorityLevel.values()[i]);
		}
		return images;
	}

	public Set<AbstractTaskListFilter> getFilters() {
		return filters;
	}

	public static String getCurrentPriorityLevel() {
		if (TasksUiPlugin.getDefault().getPreferenceStore().contains(TaskListPreferenceConstants.SELECTED_PRIORITY)) {
			return TasksUiPlugin.getDefault().getPreferenceStore().getString(
					TaskListPreferenceConstants.SELECTED_PRIORITY);
		} else {
			return Task.PriorityLevel.P5.toString();
		}
	}

	public TaskArchiveFilter getArchiveFilter() {
		return FILTER_ARCHIVE;
	}

	public void setManualFiltersEnabled(boolean enabled) {
		sortByAction.setEnabled(enabled);
		filterOnPriorityAction.setEnabled(enabled);
		filterCompleteTask.setEnabled(enabled);
		showSubTasksAction.setEnabled(enabled);
		filterArchiveCategory.setEnabled(enabled);
	}

	public boolean isFocusedMode() {
		return focusedMode;
	}

	public void setFocusedMode(boolean focusedMode) {
		this.focusedMode = focusedMode;
	}

	public void setSortByPriority(boolean byPriority) {
		if (byPriority) {
			sortIndex = 0;
		} else {
			sortIndex = 1;
		}
		getViewer().setSorter(new TaskListTableSorter(this, byPriority));
	}

	public boolean isSortByPriority() {
		return sortIndex == 0;
	}
}
