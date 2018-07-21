/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - fix for bug 280172
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.SelectionProviderAdapter;
import org.eclipse.mylyn.commons.workbench.AbstractFilteredTree;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.commons.workbench.search.SearchHistoryPopupDialog;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.TaskHistoryDropDown;
import org.eclipse.mylyn.internal.tasks.ui.TaskScalingHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TaskWorkingSetFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskDialogAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.RepositoryElementActionGroup;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskWorkingSetAction;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.UiLegendDialog;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskListChangeAdapter;
import org.eclipse.mylyn.internal.tasks.ui.search.AbstractSearchHandler;
import org.eclipse.mylyn.internal.tasks.ui.search.AbstractSearchHandler.IFilterChangeListener;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos - Task Working Set UI
 * @author David Green
 */
public class TaskListFilteredTree extends AbstractFilteredTree {

	public static final String LABEL_SEARCH = Messages.TaskListFilteredTree_Search_repository_for_key_or_summary_;

	private TaskScalingHyperlink workingSetLink;

	private TaskScalingHyperlink activeTaskLink;

	private WorkweekProgressBar taskProgressBar;

	private int totalTasks;

	private int completeTime;

	private int completeTasks;

	private int incompleteTime;

	private IWorkingSet currentWorkingSet;

	private MenuManager activeTaskMenuManager;

	private TaskListToolTip taskListToolTip;

	private ITaskListChangeListener changeListener;

	private TaskListChangeAdapter taskProgressBarChangeListener;

	private TaskActivityAdapter taskProgressBarActivityListener;

	private IPropertyChangeListener taskProgressBarWorkingSetListener;

	private TaskWorkingSetFilter workingSetFilter;

	private final IWorkbenchWindow window;

	private SelectionProviderAdapter activeTaskSelectionProvider;

	private RepositoryElementActionGroup actionGroup;

	private StructuredSelection activeSelection;

	private final AbstractSearchHandler searchHandler;

	private Composite searchComposite;

	/**
	 * @param window
	 *            can be null. Needed for the working sets to be displayed properly
	 */
	public TaskListFilteredTree(Composite parent, int treeStyle, AbstractSearchHandler searchHandler,
			IWorkbenchWindow window) {
		super(parent, treeStyle, searchHandler.createFilter());
		this.searchHandler = searchHandler;
		initSearchComposite();
		hookContextMenu();
		this.window = window;
		indicateActiveTaskWorkingSet();
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (changeListener != null) {
					TasksUiInternal.getTaskList().removeChangeListener(changeListener);
				}
				if (taskProgressBarChangeListener != null) {
					TasksUiInternal.getTaskList().removeChangeListener(taskProgressBarChangeListener);
				}
				if (taskProgressBarActivityListener != null) {
					TasksUi.getTaskActivityManager().removeActivityListener(taskProgressBarActivityListener);
				}
				if (taskProgressBarWorkingSetListener != null) {
					PlatformUI.getWorkbench()
							.getWorkingSetManager()
							.removePropertyChangeListener(taskProgressBarWorkingSetListener);
				}
				actionGroup.setSelectionProvider(null);
				activeTaskMenuManager.dispose();
				if (taskListToolTip != null) {
					taskListToolTip.dispose();
				}
			}
		});
	}

	private void hookContextMenu() {
		actionGroup = new RepositoryElementActionGroup();

		activeTaskMenuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		activeTaskMenuManager.setRemoveAllWhenShown(true);
		activeTaskMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				actionGroup.fillContextMenu(manager);
			}
		});
	}

	@Override
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		// Use a single Composite for the Tree to being able to use the
		// TreeColumnLayout. See Bug 177891 for more details.
		Composite container = new Composite(parent, SWT.None);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.verticalIndent = 0;
		gridData.horizontalIndent = 0;
		container.setLayoutData(gridData);
		container.setLayout(new TreeColumnLayout());
		return super.doCreateTreeViewer(container, style);
	}

	@Override
	protected Composite createProgressComposite(Composite container) {
		Composite progressComposite = new Composite(container, SWT.NONE);
		GridLayout progressLayout = new GridLayout(1, false);
		progressLayout.marginWidth = 4;
		progressLayout.marginHeight = 0;
		progressLayout.marginBottom = 0;
		progressLayout.horizontalSpacing = 0;
		progressLayout.verticalSpacing = 0;
		progressComposite.setLayout(progressLayout);
		progressComposite.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 4, 1));

		taskProgressBar = new WorkweekProgressBar(progressComposite);
		taskProgressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		updateTaskProgressBar();

		taskProgressBarChangeListener = new TaskListChangeAdapter() {

			@Override
			public void containersChanged(Set<TaskContainerDelta> containers) {
				for (TaskContainerDelta taskContainerDelta : containers) {
					if (taskContainerDelta.getElement() instanceof ITask) {
						updateTaskProgressBar();
						break;
					}
				}
			}
		};
		TasksUiInternal.getTaskList().addChangeListener(taskProgressBarChangeListener);

		taskProgressBarActivityListener = new TaskActivityAdapter() {

			@Override
			public void activityReset() {
				updateTaskProgressBar();
			}

		};
		TasksUiPlugin.getTaskActivityManager().addActivityListener(taskProgressBarActivityListener);

		taskProgressBarWorkingSetListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				String property = event.getProperty();
				if (IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE.equals(property)
						|| IWorkingSetManager.CHANGE_WORKING_SET_REMOVE.equals(property)) {
					updateTaskProgressBar();
				}
			}
		};
		PlatformUI.getWorkbench().getWorkingSetManager().addPropertyChangeListener(taskProgressBarWorkingSetListener);

		return progressComposite;
	}

	@Override
	protected Composite createSearchComposite(Composite container) {
		searchComposite = new Composite(container, SWT.NONE);
		GridLayout searchLayout = new GridLayout(2, false);
		searchLayout.marginHeight = 0;
		searchLayout.marginWidth = 0;
		searchComposite.setLayout(searchLayout);
		searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 4, 1));

		return searchComposite;
	}

	private void initSearchComposite() {
		searchHandler.createSearchComposite(searchComposite);
		searchHandler.adaptTextSearchControl(getTextSearchControl().getTextControl());
		searchHandler.addFilterChangeListener(new IFilterChangeListener() {
			public void filterChanged() {
				getRefreshPolicy().filterChanged();
			}
		});

		if (SearchUtil.supportsTaskSearch()) {

			final TaskScalingHyperlink searchLink = new TaskScalingHyperlink(searchComposite, SWT.LEFT);
			searchLink.setText(LABEL_SEARCH);

			searchLink.addHyperlinkListener(new IHyperlinkListener() {

				public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
					SearchUtil.openSearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				}

				public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
					searchLink.setUnderlined(true);
				}

				public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
					searchLink.setUnderlined(false);
				}
			});
		}
	}

	private void updateTaskProgressBar() {
		if (taskProgressBar.isDisposed()) {
			return;
		}

		Set<ITask> tasksThisWeek = TasksUiPlugin.getTaskActivityManager().getScheduledForADayThisWeek();
		if (workingSetFilter != null) {
			for (Iterator<ITask> it = tasksThisWeek.iterator(); it.hasNext();) {
				ITask task = it.next();
				if (!workingSetFilter.select(task)) {
					it.remove();
				}
			}
		}

		totalTasks = tasksThisWeek.size();
		completeTime = 0;
		completeTasks = 0;
		incompleteTime = 0;
		for (ITask task : tasksThisWeek) {
			if (task instanceof AbstractTask) {
				AbstractTask abstractTask = (AbstractTask) task;
				if (task.isCompleted()) {
					completeTasks++;
					if (abstractTask.getEstimatedTimeHours() > 0) {
						completeTime += abstractTask.getEstimatedTimeHours();
					} else {
						completeTime++;
					}
				} else {
					if (abstractTask.getEstimatedTimeHours() > 0) {
						incompleteTime += abstractTask.getEstimatedTimeHours();
					} else {
						incompleteTime++;
					}
				}
			}
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (PlatformUI.isWorkbenchRunning() && !taskProgressBar.isDisposed()) {
					taskProgressBar.reset(completeTime, (completeTime + incompleteTime));

					taskProgressBar.setToolTipText(Messages.TaskListFilteredTree_Workweek_Progress
							+ "\n" //$NON-NLS-1$
							+ MessageFormat.format(Messages.TaskListFilteredTree_Estimated_hours, completeTime,
									completeTime + incompleteTime)
							+ "\n" //$NON-NLS-1$
							+ MessageFormat.format(Messages.TaskListFilteredTree_Scheduled_tasks, completeTasks,
									totalTasks));
				}
			}
		});
	}

	@Override
	protected Composite createActiveWorkingSetComposite(Composite container) {
		final ImageHyperlink workingSetButton = new ImageHyperlink(container, SWT.FLAT);
		workingSetButton.setImage(CommonImages.getImage(CommonImages.TOOLBAR_ARROW_RIGHT));
		workingSetButton.setToolTipText(Messages.TaskListFilteredTree_Select_Working_Set);

		workingSetLink = new TaskScalingHyperlink(container, SWT.LEFT);
		workingSetLink.setText(TaskWorkingSetAction.LABEL_SETS_NONE);
		workingSetLink.setUnderlined(false);

		final TaskWorkingSetAction workingSetAction = new TaskWorkingSetAction();
		workingSetButton.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				workingSetAction.getMenu(workingSetButton).setVisible(true);
			}

			public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				workingSetButton.setImage(CommonImages.getImage(CommonImages.TOOLBAR_ARROW_DOWN));
			}

			public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				workingSetButton.setImage(CommonImages.getImage(CommonImages.TOOLBAR_ARROW_RIGHT));
			}
		});

		workingSetLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (currentWorkingSet != null) {
					workingSetAction.run(currentWorkingSet);
				} else {
					workingSetAction.run();
				}
			}
		});

		return workingSetLink;
	}

	@Override
	protected Composite createActiveTaskComposite(final Composite container) {
		final ImageHyperlink activeTaskButton = new ImageHyperlink(container, SWT.LEFT);// SWT.ARROW | SWT.RIGHT);
		activeTaskButton.setImage(CommonImages.getImage(CommonImages.TOOLBAR_ARROW_RIGHT));
		activeTaskButton.setToolTipText(Messages.TaskListFilteredTree_Select_Active_Task);

		activeTaskLink = new TaskScalingHyperlink(container, SWT.LEFT);

		changeListener = new TaskListChangeAdapter() {
			@Override
			public void containersChanged(Set<TaskContainerDelta> containers) {
				for (TaskContainerDelta taskContainerDelta : containers) {
					if (taskContainerDelta.getElement() instanceof ITask) {
						final AbstractTask changedTask = (AbstractTask) (taskContainerDelta.getElement());
						if (changedTask.isActive()) {
							if (Platform.isRunning() && PlatformUI.getWorkbench() != null) {
								if (Display.getCurrent() == null) {
									if (PlatformUI.getWorkbench().getDisplay() != null
											&& !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
										PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
											public void run() {
												indicateActiveTask(changedTask);
											}
										});
									}
								} else {
									indicateActiveTask(changedTask);
								}
							}
						}
					}
				}
			}
		};
		TasksUiInternal.getTaskList().addChangeListener(changeListener);

		activeTaskLink.setText(Messages.TaskListFilteredTree_Activate);
		// avoid having the Hyperlink class show a native tooltip when it shortens the text which would overlap with the task list tooltip
		activeTaskLink.setToolTipText(""); //$NON-NLS-1$

		taskListToolTip = new TaskListToolTip(activeTaskLink);

		ITask activeTask = TasksUi.getTaskActivityManager().getActiveTask();
		if (activeTask != null) {
			indicateActiveTask(activeTask);
		}

		activeTaskButton.addHyperlinkListener(new IHyperlinkListener() {

			private Menu dropDownMenu;

			public void linkActivated(HyperlinkEvent event) {
				if (dropDownMenu != null) {
					dropDownMenu.dispose();
				}
				TaskHistoryDropDown taskHistory = new TaskHistoryDropDown();
				taskHistory.setScopedToWorkingSet(true);
				dropDownMenu = new Menu(activeTaskButton);
				taskHistory.fill(dropDownMenu, 0);
				dropDownMenu.setVisible(true);
			}

			public void linkEntered(HyperlinkEvent event) {
				activeTaskButton.setImage(CommonImages.getImage(CommonImages.TOOLBAR_ARROW_DOWN));
			}

			public void linkExited(HyperlinkEvent event) {
				activeTaskButton.setImage(CommonImages.getImage(CommonImages.TOOLBAR_ARROW_RIGHT));
			}
		});

		activeTaskLink.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				// do not show menu for inactive task link
				if (activeTaskSelectionProvider != null && activeSelection != null && !activeSelection.isEmpty()) {
					// grab focus since the active task will become the active selection: this causes the focus listener on the task list viewer to reset the active selection when focus is set back to the task list
					activeTaskLink.setFocus();

					// set site selection to active task
					activeTaskSelectionProvider.setSelection(activeSelection);

					// show menu that has been registered with view
					Menu activeTaskMenu = activeTaskMenuManager.createContextMenu(container);
					activeTaskMenu.setVisible(true);
				}
			}
		});

		activeTaskLink.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				// only handle left clicks, context menu is handled by platform
				if (e.button == 1) {
					ITask activeTask = (TasksUi.getTaskActivityManager().getActiveTask());
					if (activeTask == null) {
						ActivateTaskDialogAction activateAction = new ActivateTaskDialogAction();
						activateAction.init(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
						activateAction.run(null);
					} else {
//						if (TaskListFilteredTree.super.filterText.getText().length() > 0) {
//							TaskListFilteredTree.super.filterText.setText("");
//							TaskListFilteredTree.this.textChanged();
//						}
//						if (TaskListView.getFromActivePerspective().getDrilledIntoCategory() != null) {
//							TaskListView.getFromActivePerspective().goUpToRoot();
//						}
						TasksUiInternal.refreshAndOpenTaskListElement(activeTask);
					}
				}
			}
		});

		return activeTaskLink;
	}

	@Override
	protected void textChanged() {
		super.textChanged();
		if (getFilterString() != null && !getFilterString().trim().equals("")) { //$NON-NLS-1$
			setShowSearch(true);
		} else {
			setShowSearch(false);
		}
	}

	public void indicateActiveTaskWorkingSet() {
		if (window == null || workingSetLink == null || filterComposite == null || filterComposite.isDisposed()) {
			return;
		}

		Set<IWorkingSet> activeSets = TaskWorkingSetUpdater.getActiveWorkingSets(window);
		if (activeSets == null) {
			return;
		}

		if (activeSets.size() == 0) {
			workingSetLink.setText(TaskWorkingSetAction.LABEL_SETS_NONE);
			workingSetLink.setToolTipText(Messages.TaskListFilteredTree_Edit_Task_Working_Sets_);
			currentWorkingSet = null;
		} else if (activeSets.size() > 1) {
			workingSetLink.setText(Messages.TaskListFilteredTree__multiple_);
			workingSetLink.setToolTipText(Messages.TaskListFilteredTree_Edit_Task_Working_Sets_);
			currentWorkingSet = null;
		} else {
			Object[] array = activeSets.toArray();
			IWorkingSet workingSet = (IWorkingSet) array[0];
			workingSetLink.setText(workingSet.getLabel());
			workingSetLink.setToolTipText(Messages.TaskListFilteredTree_Edit_Task_Working_Sets_);
			currentWorkingSet = workingSet;
		}
		relayoutFilterControls();
	}

	public void indicateActiveTask(ITask task) {
		if (Display.getCurrent() != null) {

			if (filterComposite.isDisposed()) {
				return;
			}

			activeTaskLink.setTask(task);
			activeSelection = new StructuredSelection(task);

			relayoutFilterControls();
		}
	}

	private void relayoutFilterControls() {
		filterComposite.layout();
	}

	public String getActiveTaskLabelText() {
		return activeTaskLink.getText();
	}

	public void indicateNoActiveTask() {
		if (filterComposite.isDisposed()) {
			return;
		}

		activeTaskLink.setTask(null);
		activeTaskLink.setText(Messages.TaskListFilteredTree_Activate);
		activeTaskLink.setToolTipText(""); //$NON-NLS-1$
		activeSelection = StructuredSelection.EMPTY;

		relayoutFilterControls();
	}

	@Override
	public void setFilterText(String string) {
		if (filterText != null) {
			filterText.setText(string);
			selectAll();
		}
	}

	@Override
	protected String getFilterString() {
		String text = super.getFilterString();
		return (text != null) ? text.trim() : null;
	}

	public TaskWorkingSetFilter getWorkingSetFilter() {
		return workingSetFilter;
	}

	public void setWorkingSetFilter(TaskWorkingSetFilter workingSetFilter) {
		this.workingSetFilter = workingSetFilter;
	}

	public MenuManager getActiveTaskMenuManager() {
		return activeTaskMenuManager;
	}

	public SelectionProviderAdapter getActiveTaskSelectionProvider() {
		return activeTaskSelectionProvider;
	}

	public void setActiveTaskSelectionProvider(SelectionProviderAdapter activeTaskSelectionProvider) {
		this.activeTaskSelectionProvider = activeTaskSelectionProvider;
		this.actionGroup.setSelectionProvider(activeTaskSelectionProvider);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected SearchHistoryPopupDialog getHistoryPopupDialog() {
		return null;
	}

	@Override
	protected Composite createAdditionalControls(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayoutFactory.fillDefaults().applyTo(container);

		ImageHyperlink showUILegend = new ImageHyperlink(container, SWT.NONE);
		showUILegend.setImage(CommonImages.QUESTION.createImage());
		showUILegend.setToolTipText(Messages.TaskListFilteredTree_Show_Tasks_UI_Legend);
		showUILegend.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				UiLegendDialog uiLegendDialog = new UiLegendDialog(WorkbenchUtil.getShell());
				uiLegendDialog.open();

			}
		});

		return container;
	}

}
