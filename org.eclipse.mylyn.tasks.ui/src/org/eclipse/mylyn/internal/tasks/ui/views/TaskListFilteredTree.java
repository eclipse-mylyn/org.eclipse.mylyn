/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TaskListHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TaskSearchPage;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskDialogAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskHistoryDropDownAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskWorkingSetAction;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.search.internal.ui.SearchDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.internal.ObjectActionContributorManager;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos - Task Working Set UI
 */
public class TaskListFilteredTree extends AbstractFilteredTree {

	private static final String LABEL_ACTIVE_NONE = "Activate...  ";

	private static final String LABEL_SETS_EDIT = "Edit Task Working Sets...";

	private static final String LABEL_SETS_MULTIPLE = "<multiple>";

	public static final String LABEL_SEARCH = "Search repository for key or summary...";
	
	private TaskListHyperlink workingSetLink;

	private TaskListHyperlink activeTaskLink;

	private WorkweekProgressBar taskProgressBar;

	private int totalTasks;

	private int completeTime;

	private int completeTasks;

	private int incompleteTime;

	private IWorkingSet currentWorkingSet;

	private MenuManager activeTaskMenuManager = null;

	private Menu activeTaskMenu = null;

	private CopyTaskDetailsAction copyTaskDetailsAction = new CopyTaskDetailsAction(false);

	public TaskListFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
		hookContextMenu();
	}

	private void hookContextMenu() {
		activeTaskMenuManager = new MenuManager("#PopupMenu");
		activeTaskMenuManager.setRemoveAllWhenShown(true);
		activeTaskMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager, TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask());
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

		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(new ITaskListChangeListener() {

			public void containersChanged(Set<TaskContainerDelta> containers) {
				for (TaskContainerDelta taskContainerDelta : containers) {
					if (taskContainerDelta.getContainer() instanceof AbstractTask) {
						updateTaskProgressBar();
						break;
					}
				}
			}
		});

		TasksUiPlugin.getTaskListManager().addActivityListener(new ITaskActivityListener() {

			public void activityChanged(ScheduledTaskContainer week) {
				updateTaskProgressBar();
			}

			public void taskActivated(AbstractTask task) {
			}

			public void taskDeactivated(AbstractTask task) {
			}

			public void taskListRead() {
			}
		});
		return progressComposite;
	}

	@Override
	protected Composite createSearchComposite(Composite container) {
		Composite searchComposite = new Composite(container, SWT.NONE);
		GridLayout searchLayout = new GridLayout(1, false);
		searchLayout.marginWidth = 8;
		searchLayout.marginHeight = 0;
		searchLayout.marginBottom = 0;
		searchLayout.horizontalSpacing = 0;
		searchLayout.verticalSpacing = 0;
		searchComposite.setLayout(searchLayout);
		searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 4, 1));

		final TaskListHyperlink searchLink = new TaskListHyperlink(searchComposite, SWT.LEFT);
		searchLink.setText(LABEL_SEARCH);
		searchLink.setForeground(TaskListColorsAndFonts.COLOR_HYPERLINK_WIDGET);
		
		searchLink.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				new SearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), TaskSearchPage.ID).open();
			}

			public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				searchLink.setUnderlined(true);
			}

			public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				searchLink.setUnderlined(false);
			}
		});
		
		return searchComposite;
	}
	
	@SuppressWarnings("deprecation")
	private void updateTaskProgressBar() {
		if (taskProgressBar.isDisposed()) {
			return;
		}

		Set<AbstractTask> tasksThisWeek = TasksUiPlugin.getTaskListManager().getScheduledForThisWeek();
		
		totalTasks = tasksThisWeek.size();
		completeTime = 0;
		completeTasks = 0;
		incompleteTime = 0;
		for (AbstractTask task : tasksThisWeek) {
			if (task.isCompleted()) {
				completeTasks++;
				if (task.getEstimateTimeHours() > 0) {
					completeTime += task.getEstimateTimeHours();
				} else {
					completeTime++;
				}
			} else {
				if (task.getEstimateTimeHours() > 0) {
					incompleteTime += task.getEstimateTimeHours();
				} else {
					incompleteTime++;
				}
			}
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (PlatformUI.isWorkbenchRunning() && !taskProgressBar.isDisposed()) {
					taskProgressBar.reset(completeTime, (completeTime + incompleteTime));
					
//					String workingSetName = "All";
//					if (currentWorkingSet != null) {
//						workingSetName = currentWorkingSet.getLabel();
//					}

					taskProgressBar.setToolTipText(
							"Workweek Progress"
							+ "\n     Estimated hours: " + completeTime + " of "
							+ (completeTime + incompleteTime) + " estimated" + "\n     Scheduled tasks: " + completeTasks
							+ " of " + totalTasks + " scheduled");
				}
			}
		});
	}

	@Override
	protected Composite createActiveWorkingSetComposite(Composite container) {
		final ImageHyperlink workingSetButton = new ImageHyperlink(container, SWT.FLAT);
		workingSetButton.setImage(TasksUiImages.getImage(TasksUiImages.TOOLBAR_ARROW_RIGHT));
		workingSetButton.setToolTipText("Select Working Set");

		workingSetLink = new TaskListHyperlink(container, SWT.LEFT);
		workingSetLink.setText(TaskWorkingSetAction.LABEL_SETS_NONE);
		workingSetLink.setUnderlined(false);
		workingSetLink.setForeground(TaskListColorsAndFonts.COLOR_HYPERLINK_WIDGET);

		final TaskWorkingSetAction workingSetAction = new TaskWorkingSetAction();
		workingSetButton.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				workingSetAction.getMenu(workingSetButton).setVisible(true);
			}

			public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				workingSetButton.setImage(TasksUiImages.getImage(TasksUiImages.TOOLBAR_ARROW_DOWN));
			}

			public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				workingSetButton.setImage(TasksUiImages.getImage(TasksUiImages.TOOLBAR_ARROW_RIGHT));
			}
		});

		workingSetLink.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent e) {
				workingSetLink.setUnderlined(true);
			}

			public void mouseExit(MouseEvent e) {
				workingSetLink.setUnderlined(false);
			}

			public void mouseHover(MouseEvent e) {
			}
		});

		indicateActiveTaskWorkingSet();

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
		activeTaskButton.setImage(TasksUiImages.getImage(TasksUiImages.TOOLBAR_ARROW_RIGHT));
		activeTaskButton.setToolTipText("Select Active Task");

		activeTaskLink = new TaskListHyperlink(container, SWT.LEFT);
		activeTaskLink.setText(LABEL_ACTIVE_NONE);
		activeTaskLink.setForeground(TaskListColorsAndFonts.COLOR_HYPERLINK_WIDGET);

		AbstractTask activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (activeTask != null) {
			indicateActiveTask(activeTask);
		}

		final ActivateTaskHistoryDropDownAction action = new ActivateTaskHistoryDropDownAction(
				TasksUiPlugin.getTaskListManager().getTaskActivationHistory(), true);

		activeTaskButton.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				action.getMenu(activeTaskButton).setVisible(true);
			}

			public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				activeTaskButton.setImage(TasksUiImages.getImage(TasksUiImages.TOOLBAR_ARROW_DOWN));
			}

			public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				activeTaskButton.setImage(TasksUiImages.getImage(TasksUiImages.TOOLBAR_ARROW_RIGHT));
			}
		});

		activeTaskLink.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				if (activeTaskMenu != null) {
					activeTaskMenu.dispose();
				}
				activeTaskMenu = activeTaskMenuManager.createContextMenu(container);
				activeTaskMenu.setVisible(true);
			}
		});

		activeTaskLink.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					AbstractTask activeTask = (TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask());
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
						TasksUiUtil.refreshAndOpenTaskListElement(activeTask);
					}
				}
			}
		});

		return activeTaskLink;
	}

	@Override
	protected void textChanged() {
		super.textChanged();
		if (getFilterString() != null && !getFilterString().trim().equals("")) {
			setShowSearch(true);
		} else {
			setShowSearch(false);
		}
	} 
	
	public void indicateActiveTaskWorkingSet() {
		Set<IWorkingSet> activeSets = TaskListView.getActiveWorkingSets(); 
		
		if (filterComposite.isDisposed() || activeSets == null) {
			return;
		}

		if (activeSets.size() == 0) {
			workingSetLink.setText(TaskWorkingSetAction.LABEL_SETS_NONE);
			workingSetLink.setToolTipText(LABEL_SETS_EDIT);
			currentWorkingSet = null;
		} else if (activeSets.size() > 1) {
			workingSetLink.setText(LABEL_SETS_MULTIPLE);
			workingSetLink.setToolTipText(LABEL_SETS_EDIT);
			currentWorkingSet = null;
		} else {
			Object[] array = activeSets.toArray();
			IWorkingSet workingSet = (IWorkingSet) array[0];
			workingSetLink.setText(workingSet.getLabel());
			workingSetLink.setToolTipText(LABEL_SETS_EDIT);
			currentWorkingSet = workingSet;
		}
		updateTaskProgressBar();
		filterComposite.layout();
	}

	public void indicateActiveTask(AbstractTask task) {
		if (filterComposite.isDisposed()) {
			return;
		}

		String text = task.getSummary();
		activeTaskLink.setText(text);
		activeTaskLink.setUnderlined(false);
		activeTaskLink.setToolTipText("Open: " + task.getSummary());
		activeTaskLink.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent e) {
				activeTaskLink.setUnderlined(true);
			}

			public void mouseExit(MouseEvent e) {
				activeTaskLink.setUnderlined(false);
			}

			public void mouseHover(MouseEvent e) {
			}
		});

		filterComposite.layout();
	}

	public String getActiveTaskLabelText() {
		return activeTaskLink.getText();
	}

	public void indicateNoActiveTask() {
		if (filterComposite.isDisposed()) {
			return;
		}

		activeTaskLink.setText(LABEL_ACTIVE_NONE);
		activeTaskLink.setToolTipText("");
		filterComposite.layout();
	}

	@Override
	public void setFilterText(String string) {
		if (filterText != null) {
			filterText.setText(string);
			selectAll();
		}
	}

	private void fillContextMenu(IMenuManager manager, final AbstractTask activeTask) {
		if (activeTask != null) {
			IStructuredSelection selection = new StructuredSelection(activeTask);
			copyTaskDetailsAction.selectionChanged(selection);

			manager.add(new OpenTaskListElementAction(null) {
				@Override
				public void run() {
					TasksUiUtil.refreshAndOpenTaskListElement(activeTask);
				}
			});

			if (activeTask.isActive()) {
				manager.add(new TaskDeactivateAction() {
					@Override
					public void run() {
						super.run(activeTask);
					}
				});
			} else {
				manager.add(new TaskActivateAction() {
					@Override
					public void run() {
//						TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(activeTask);
						super.run(activeTask);
					}
				});
			}

			manager.add(new Separator());

			for (String menuPath : TasksUiPlugin.getDefault().getDynamicMenuMap().keySet()) {
				for (IDynamicSubMenuContributor contributor : TasksUiPlugin.getDefault().getDynamicMenuMap().get(
						menuPath)) {
					if (TaskListView.ID_SEPARATOR_TASKS.equals(menuPath)) {
						List<AbstractTaskContainer> selectedElements = new ArrayList<AbstractTaskContainer>();
						selectedElements.add(activeTask);
						MenuManager subMenuManager = contributor.getSubMenuManager(selectedElements);
						if (subMenuManager != null) {
							manager.add(subMenuManager);
						}
					}
				}
			}

			manager.add(new Separator());
			manager.add(copyTaskDetailsAction);
			manager.add(new Separator());

			ObjectActionContributorManager.getManager().contributeObjectActions(null, manager,
					new ISelectionProvider() {

						public void addSelectionChangedListener(ISelectionChangedListener listener) {
							// ignore
						}

						public ISelection getSelection() {
							return new StructuredSelection(activeTask);
						}

						public void removeSelectionChangedListener(ISelectionChangedListener listener) {
							// ignore
						}

						public void setSelection(ISelection selection) {
							// ignore
						}
					});
		}
	}
}
