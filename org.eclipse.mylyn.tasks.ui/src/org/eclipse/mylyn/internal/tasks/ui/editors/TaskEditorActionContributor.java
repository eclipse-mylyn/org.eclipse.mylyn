/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchActionSupport;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchActionSupport.WorkbenchActionCallback;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AddExistingTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewTaskFromSelectionAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ShowInTaskListAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskEditorActionContributor extends MultiPageEditorActionBarContributor implements
		ISelectionChangedListener {

	private class EditorPageCallback extends WorkbenchActionCallback {

		@Override
		public boolean canPerformAction(String actionId, Control control) {
			IFormPage activePage = getActivePage();
			if (activePage instanceof AbstractTaskEditorPage) {
				AbstractTaskEditorPage page = (AbstractTaskEditorPage) activePage;
				return page.canPerformAction(actionId);
			} else if (activePage != null) {
				WorkbenchActionCallback textSupport = (WorkbenchActionCallback) activePage.getAdapter(WorkbenchActionCallback.class);
				if (textSupport != null) {
					return textSupport.canPerformAction(actionId, control);
				}
			}
			return super.canPerformAction(actionId, control);
		}

		@Override
		public void doAction(String actionId, Control control) {
			IFormPage activePage = getActivePage();
			if (activePage instanceof AbstractTaskEditorPage) {
				AbstractTaskEditorPage page = (AbstractTaskEditorPage) activePage;
				page.doAction(actionId);
			} else if (activePage != null) {
				WorkbenchActionCallback textSupport = (WorkbenchActionCallback) activePage.getAdapter(WorkbenchActionCallback.class);
				if (textSupport != null) {
					textSupport.doAction(actionId, control);
				}
			}
			super.doAction(actionId, control);
		}

		@Override
		public Control getFocusControl() {
			IFormPage page = getActivePage();
			return (page != null) ? EditorUtil.getFocusControl(page) : null;
		}

		@Override
		public ISelection getSelection() {
			return TaskEditorActionContributor.this.getSelection();
		}

	}

	private TaskEditor editor;

	private final OpenWithBrowserAction openWithBrowserAction = new OpenWithBrowserAction();

	private final CopyTaskDetailsAction copyTaskDetailsAction = new CopyTaskDetailsAction();

	private final SynchronizeEditorAction synchronizeEditorAction = new SynchronizeEditorAction();

	private final ShowInTaskListAction showInTaskListAction = new ShowInTaskListAction();

	private final NewTaskFromSelectionAction newTaskFromSelectionAction = new NewTaskFromSelectionAction();

	private final WorkbenchActionSupport actionSupport;

	public TaskEditorActionContributor() {
		actionSupport = new WorkbenchActionSupport();
		actionSupport.setCallback(new EditorPageCallback());
	}

	public void addClipboardActions(IMenuManager manager) {
		manager.add(actionSupport.getUndoAction());
		manager.add(actionSupport.getRedoAction());
		manager.add(new Separator());
		manager.add(actionSupport.getCutAction());
		manager.add(actionSupport.getCopyAction());
		manager.add(copyTaskDetailsAction);
		manager.add(actionSupport.getPasteAction());
		manager.add(actionSupport.getSelectAllAction());
		manager.add(newTaskFromSelectionAction);
		manager.add(new Separator());
	}

	public void contextMenuAboutToShow(IMenuManager mng) {
		IFormPage page = getActivePage();
		boolean addClipboard = (page instanceof TaskPlanningEditor || page instanceof AbstractTaskEditorPage);
		contextMenuAboutToShow(mng, addClipboard);
	}

	public void contextMenuAboutToShow(IMenuManager manager, boolean addClipboard) {
		if (editor != null) {
			updateSelectableActions(getSelection());
		}
		if (addClipboard) {
			addClipboardActions(manager);
		}
		if (editor.getTaskEditorInput() == null) {
			final MenuManager subMenuManager = new MenuManager(MessageFormat.format(
					Messages.TaskEditorActionContributor_Add_to_X, TaskListView.LABEL_VIEW));
			List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(TasksUiInternal.getTaskList()
					.getCategories());
			Collections.sort(categories);
			for (final AbstractTaskCategory category : categories) {
				if (!(category instanceof UnmatchedTaskContainer)) {//.equals(TasksUiPlugin.getTaskList().getArchiveContainer())) {
					Action action = new Action() {
						@Override
						public void run() {
							moveToCategory(category);
						}
					};
					String text = category.getSummary();
					action.setText(text);
					action.setImageDescriptor(TasksUiImages.CATEGORY);
					subMenuManager.add(action);
				}
			}
			copyTaskDetailsAction.selectionChanged(new StructuredSelection(getSelection()));
			manager.add(subMenuManager);
		} else {
			final ITask task = editor.getTaskEditorInput().getTask();
			if (task != null) {
				// TODO: refactor
				IStructuredSelection selection = new StructuredSelection(task);
				openWithBrowserAction.selectionChanged(selection);
				copyTaskDetailsAction.selectionChanged(selection);
				synchronizeEditorAction.selectionChanged(new StructuredSelection(this.getEditor()));
				showInTaskListAction.selectionChanged(selection);

				manager.add(new Separator());
				if (!(task instanceof LocalTask)) {
					manager.add(synchronizeEditorAction);
				}
				String taskUrl = task.getUrl();
				openWithBrowserAction.setEnabled(taskUrl != null && taskUrl.length() > 0);
				manager.add(openWithBrowserAction);

				if (task.isActive()) {
					manager.add(new TaskDeactivateAction() {
						@Override
						public void run() {
							super.run(task);
						}
					});
				} else {
					manager.add(new TaskActivateAction() {
						@Override
						public void run() {
//						TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(task);
							super.run(task);
						}
					});
				}
				manager.add(showInTaskListAction);
				manager.add(new Separator());

				for (String menuPath : TasksUiPlugin.getDefault().getDynamicMenuMap().keySet()) {
					for (IDynamicSubMenuContributor contributor : TasksUiPlugin.getDefault().getDynamicMenuMap().get(
							menuPath)) {
						if (TaskListView.ID_SEPARATOR_TASKS.equals(menuPath)) {
							List<IRepositoryElement> selectedElements = new ArrayList<IRepositoryElement>();
							selectedElements.add(task);
							MenuManager subMenuManager = contributor.getSubMenuManager(selectedElements);
							if (subMenuManager != null) {
								subMenuManager.setVisible(selectedElements.size() > 0
										&& selectedElements.get(0) instanceof ITask);
								manager.add(subMenuManager);
							}
						}
					}
				}
				manager.add(new GroupMarker(TaskListView.ID_SEPARATOR_NAVIGATE));
				manager.add(new Separator());
				manager.add(new GroupMarker(TaskListView.ID_SEPARATOR_OPERATIONS));
				manager.add(new GroupMarker(TaskListView.ID_SEPARATOR_CONTEXT));
				manager.add(new Separator());
			}
		}
		manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	@Override
	public void contributeToCoolBar(ICoolBarManager cbm) {
	}

	@Override
	public void contributeToMenu(IMenuManager mm) {
	}

	@Override
	public void contributeToStatusLine(IStatusLineManager slm) {
	}

	@Override
	public void contributeToToolBar(IToolBarManager tbm) {
	}

	public void forceActionsEnabled() {
		actionSupport.forceEditActionsEnabled();
	}

	private IFormPage getActivePage() {
		return (editor != null) ? editor.getActivePageInstance() : null;
	}

	public TaskEditor getEditor() {
		return editor;
	}

	public ISelection getSelection() {
		if (editor != null && editor.getSite().getSelectionProvider() != null) {
			return editor.getSite().getSelectionProvider().getSelection();
		} else {
			return StructuredSelection.EMPTY;
		}
	}

	@Override
	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		actionSupport.install(bars);
	}

	private void moveToCategory(AbstractTaskCategory category) {
		IEditorInput input = getEditor().getEditorInput();
		if (input instanceof TaskEditorInput) {
			TaskEditorInput repositoryTaskEditorInput = (TaskEditorInput) input;
			final IProgressService svc = PlatformUI.getWorkbench().getProgressService();
			final AddExistingTaskJob job = new AddExistingTaskJob(repositoryTaskEditorInput.getTaskRepository(),
					repositoryTaskEditorInput.getTask().getTaskId(), category);
			job.schedule();
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					svc.showInDialog(getEditor().getSite().getShell(), job);
				}
			});
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		actionSupport.selectionChanged(event);
		newTaskFromSelectionAction.selectionChanged(event.getSelection());
	}

	@Override
	public void setActiveEditor(IEditorPart activeEditor) {
		if (activeEditor instanceof TaskEditor) {
			this.editor = (TaskEditor) activeEditor;
			updateSelectableActions(getSelection());
		} else {
			this.editor = null;
		}
	}

	@Override
	public void setActivePage(IEditorPart activePage) {
		updateSelectableActions(getSelection());
	}

	public void updateSelectableActions(ISelection selection) {
		if (editor != null) {
			actionSupport.updateActions(selection);
			newTaskFromSelectionAction.selectionChanged(selection);
		}
	}

}
