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

package org.eclipse.mylyn.internal.tasks.ui.editors;

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
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.AddExistingTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachFileAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ShowInTaskListAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskEditorActionContributor extends MultiPageEditorActionBarContributor implements
		ISelectionChangedListener {

	private SubActionBars sourceActionBars;

	private TaskEditor editor;

	private OpenWithBrowserAction openWithBrowserAction = new OpenWithBrowserAction();

	private CopyTaskDetailsAction copyTaskDetailsAction = new CopyTaskDetailsAction(false);

	private AttachFileAction attachFileAction = new AttachFileAction();

	private SynchronizeEditorAction synchronizeEditorAction = new SynchronizeEditorAction();

	private ShowInTaskListAction showInTaskListAction = new ShowInTaskListAction();

	private GlobalAction cutAction;

	private GlobalAction undoAction;

	private GlobalAction redoAction;

	private GlobalAction copyAction;

	private GlobalAction pasteAction;

	private GlobalAction selectAllAction;

	public TaskEditorActionContributor() {

		cutAction = new GlobalAction(ActionFactory.CUT.getId());
		cutAction.setText(WorkbenchMessages.Workbench_cut);
		cutAction.setToolTipText(WorkbenchMessages.Workbench_cutToolTip);
		cutAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		cutAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		cutAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
		cutAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.CUT);

		pasteAction = new GlobalAction(ActionFactory.PASTE.getId());
		pasteAction.setText(WorkbenchMessages.Workbench_paste);
		pasteAction.setToolTipText(WorkbenchMessages.Workbench_pasteToolTip);
		pasteAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		pasteAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		pasteAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		pasteAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);

		copyAction = new GlobalAction(ActionFactory.COPY.getId());
		copyAction.setText(WorkbenchMessages.Workbench_copy);
		copyAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		copyAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.COPY);

		undoAction = new GlobalAction(ActionFactory.UNDO.getId());
		undoAction.setText(WorkbenchMessages.Workbench_undo);
		undoAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		undoAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		undoAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
		undoAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.UNDO);

		redoAction = new GlobalAction(ActionFactory.REDO.getId());
		redoAction.setText(WorkbenchMessages.Workbench_redo);
		redoAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		redoAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		redoAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO_DISABLED));
		redoAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.REDO);

		selectAllAction = new GlobalAction(ActionFactory.SELECT_ALL.getId());
		selectAllAction.setText(WorkbenchMessages.Workbench_selectAll);
		selectAllAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.SELECT_ALL);
		selectAllAction.setEnabled(true);

	}

	public void addClipboardActions(IMenuManager manager) {
		manager.add(undoAction);
		manager.add(redoAction);
		manager.add(new Separator());
		manager.add(cutAction);
		manager.add(copyAction);
		manager.add(copyTaskDetailsAction);
		manager.add(pasteAction);
		manager.add(selectAllAction);
		manager.add(new Separator());
	}

	public void contextMenuAboutToShow(IMenuManager mng) {
		boolean addClipboard = this.getEditor().getActivePageInstance() != null
				&& (this.getEditor().getActivePageInstance() instanceof TaskPlanningEditor || this.getEditor()
						.getActivePageInstance() instanceof AbstractRepositoryTaskEditor);
		contextMenuAboutToShow(mng, addClipboard);
	}

	public void contextMenuAboutToShow(IMenuManager manager, boolean addClipboard) {
		if (editor != null)
			updateSelectableActions(editor.getSelection());
		if (addClipboard) {
			addClipboardActions(manager);
		}

		if (editor.getTaskEditorInput() == null && !(editor.getEditorInput() instanceof NewTaskEditorInput)) {
			final MenuManager subMenuManager = new MenuManager("Add to " + TaskListView.LABEL_VIEW);
			List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(
					TasksUiPlugin.getTaskListManager().getTaskList().getCategories());

			Collections.sort(categories);
			for (final AbstractTaskCategory category : categories) {
				if (!category.equals(TasksUiPlugin.getTaskListManager().getTaskList().getArchiveContainer())) {
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

			copyTaskDetailsAction.selectionChanged(new StructuredSelection(editor.getSelection()));
			manager.add(subMenuManager);
			return;

		} else if (editor.getEditorInput() instanceof NewTaskEditorInput) {
			return;
		}

		final AbstractTask task = editor.getTaskEditorInput().getTask();
		if (task == null) {
			return;
		} else {
			// TODO: refactor
			IStructuredSelection selection = new StructuredSelection(task);
			openWithBrowserAction.selectionChanged(selection);
			copyTaskDetailsAction.selectionChanged(selection);
			attachFileAction.selectionChanged(selection);
			attachFileAction.setEditor(editor);
			synchronizeEditorAction.selectionChanged(new StructuredSelection(this.getEditor()));
			showInTaskListAction.selectionChanged(selection);

			manager.add(openWithBrowserAction);
			manager.add(attachFileAction);
			manager.add(synchronizeEditorAction);

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
						TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(task);
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
						List<AbstractTaskContainer> selectedElements = new ArrayList<AbstractTaskContainer>();
						selectedElements.add(task);
						MenuManager subMenuManager = contributor.getSubMenuManager(selectedElements);
						if (subMenuManager != null) {
							manager.add(subMenuManager);
						}
					}
				}
			}

			manager.add(new Separator());
			// HACK: there should be a saner way of doing this
			ObjectActionContributorManager.getManager().contributeObjectActions(editor, manager,
					new ISelectionProvider() {

						public void addSelectionChangedListener(ISelectionChangedListener listener) {
							// ignore
						}

						public ISelection getSelection() {
							return new StructuredSelection(task);
						}

						public void removeSelectionChangedListener(ISelectionChangedListener listener) {
							// ignore

						}

						public void setSelection(ISelection selection) {
							// ignore

						}
					});
		}
		manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void moveToCategory(AbstractTaskCategory category) {
		IEditorInput input = getEditor().getEditorInput();
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput repositoryTaskEditorInput = (RepositoryTaskEditorInput) input;
			final IProgressService svc = PlatformUI.getWorkbench().getProgressService();
			final AddExistingTaskJob job = new AddExistingTaskJob(repositoryTaskEditorInput.getRepository(),
					repositoryTaskEditorInput.getId(), category);
			job.schedule();
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					svc.showInDialog(getEditor().getSite().getShell(), job);
				}
			});
		}
	}

	public void updateSelectableActions(ISelection selection) {
		if (editor != null) {
			cutAction.selectionChanged(selection);
			copyAction.selectionChanged(selection);
			pasteAction.selectionChanged(selection);
			undoAction.selectionChanged(selection);
			redoAction.selectionChanged(selection);
			selectAllAction.selectionChanged(selection);
		}
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

	@Override
	public void contributeToCoolBar(ICoolBarManager cbm) {
	}

	@Override
	public void dispose() {
		sourceActionBars.dispose();
		super.dispose();
	}

	@Override
	public void init(IActionBars bars) {
		super.init(bars);
		sourceActionBars = new SubActionBars(bars);
	}

	@Override
	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		registerGlobalHandlers(bars);

	}

	public TaskEditor getEditor() {
		return editor;
	}

	public IStatusLineManager getStatusLineManager() {
		return getActionBars().getStatusLineManager();
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		if (targetEditor instanceof TaskEditor) {
			editor = (TaskEditor) targetEditor;
			updateSelectableActions(editor.getSelection());
		}
	}

	@Override
	public void setActivePage(IEditorPart newEditor) {
		if (getEditor() != null) {
			updateSelectableActions(getEditor().getSelection());
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		updateSelectableActions(event.getSelection());
	}

	private class GlobalAction extends Action {

		private String actionId;

		public GlobalAction(String actionId) {
			this.actionId = actionId;
		}

		@Override
		public void run() {
			if (getEditor().getActivePageInstance() instanceof TaskFormPage) {
				TaskFormPage editor = (TaskFormPage) getEditor().getActivePageInstance();
				editor.doAction(actionId);
				updateSelectableActions(getEditor().getSelection());
			}
		}

		public void selectionChanged(ISelection selection) {
			if (getEditor().getActivePageInstance() instanceof TaskFormPage) {
				TaskFormPage editor = (TaskFormPage) getEditor().getActivePageInstance();
				setEnabled(editor.canPerformAction(actionId));
			}
		}
	}

	public void registerGlobalHandlers(IActionBars bars) {
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), cutAction);
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAllAction);
		bars.updateActionBars();
	}

	public void unregisterGlobalHandlers(IActionBars bars) {
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), null);
		bars.updateActionBars();
	}

	public void forceActionsEnabled() {
		cutAction.setEnabled(true);
		copyAction.setEnabled(true);
		pasteAction.setEnabled(true);
		selectAllAction.setEnabled(true);
		undoAction.setEnabled(false);
		redoAction.setEnabled(false);
	}

}
