/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AddExistingTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AbstractTaskEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachScreenshotAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewTaskFromSelectionAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ShowInTaskListAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.NewTaskEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.RepositoryTaskEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
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

	private final OpenWithBrowserAction openWithBrowserAction = new OpenWithBrowserAction();

	private final CopyTaskDetailsAction copyTaskDetailsAction = new CopyTaskDetailsAction();

	private final AbstractTaskEditorAction attachAction = new AttachAction();

	private final AbstractTaskEditorAction attachScreenshotAction = new AttachScreenshotAction();

	private final SynchronizeEditorAction synchronizeEditorAction = new SynchronizeEditorAction();

	private final ShowInTaskListAction showInTaskListAction = new ShowInTaskListAction();

	private final NewTaskFromSelectionAction newTaskFromSelectionAction = new NewTaskFromSelectionAction();

	private final GlobalAction cutAction;

	private final GlobalAction undoAction;

	private final GlobalAction redoAction;

	private final GlobalAction copyAction;

	private final GlobalAction pasteAction;

	private final GlobalAction selectAllAction;

	protected List<TextViewer> textViewers = new ArrayList<TextViewer>();

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
		manager.add(newTaskFromSelectionAction);
		manager.add(new Separator());
	}

	public void contextMenuAboutToShow(IMenuManager mng) {
		IFormPage page = this.getEditor().getActivePageInstance();
		// API 3.0 pass addClipboard as a parameter
		boolean addClipboard = (page instanceof TaskPlanningEditor || page instanceof AbstractRepositoryTaskEditor || page instanceof AbstractTaskEditorPage);
		contextMenuAboutToShow(mng, addClipboard);
	}

	public void contextMenuAboutToShow(IMenuManager manager, boolean addClipboard) {
		if (editor != null) {
			updateSelectableActions(getSelection());
		}
		if (addClipboard) {
			addClipboardActions(manager);
		}

		if (editor.getTaskEditorInput() == null && !(editor.getEditorInput() instanceof NewTaskEditorInput)) {
			final MenuManager subMenuManager = new MenuManager("Add to " + TaskListView.LABEL_VIEW);
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
			return;

		} else if (editor.getEditorInput() instanceof NewTaskEditorInput) {
			return;
		}

		final ITask task = editor.getTaskEditorInput().getTask();
		if (task == null) {
			return;
		} else {
			// TODO: refactor
			IStructuredSelection selection = new StructuredSelection(task);
			openWithBrowserAction.selectionChanged(selection);
			copyTaskDetailsAction.selectionChanged(selection);
			if (getPage() instanceof AbstractRepositoryTaskEditor) {
				attachAction.selectionChanged(selection);
				attachAction.setEditor(editor);
				attachScreenshotAction.selectionChanged(selection);
				attachScreenshotAction.setEditor(editor);
			}
			synchronizeEditorAction.selectionChanged(new StructuredSelection(this.getEditor()));
			showInTaskListAction.selectionChanged(selection);

			manager.add(new Separator());
			manager.add(synchronizeEditorAction);
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
			manager.add(new GroupMarker(TaskListView.ID_SEPARATOR_OPERATIONS));
			manager.add(new GroupMarker(TaskListView.ID_SEPARATOR_CONTEXT));

			for (String menuPath : TasksUiPlugin.getDefault().getDynamicMenuMap().keySet()) {
				for (IDynamicSubMenuContributor contributor : TasksUiPlugin.getDefault().getDynamicMenuMap().get(
						menuPath)) {
					if (TaskListView.ID_SEPARATOR_TASKS.equals(menuPath)) {
						List<ITaskElement> selectedElements = new ArrayList<ITaskElement>();
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
			manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			if (getPage() instanceof AbstractRepositoryTaskEditor) {
				manager.add(new Separator());
				manager.add(attachAction);
				manager.add(attachScreenshotAction);
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
			newTaskFromSelectionAction.selectionChanged(selection);
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
			updateSelectableActions(getSelection());
		}
	}

	@Override
	public void setActivePage(IEditorPart newEditor) {
		if (getEditor() != null) {
			updateSelectableActions(getSelection());
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		updateSelectableActions(event.getSelection());
	}

	private class GlobalAction extends Action {

		private final String actionId;

		public GlobalAction(String actionId) {
			this.actionId = actionId;
		}

		@Override
		public void run() {
			// remove check for TaskFormPage
			if (getEditor().getActivePageInstance() instanceof TaskFormPage) {
				TaskFormPage editor = (TaskFormPage) getEditor().getActivePageInstance();
				editor.doAction(actionId);
				updateSelectableActions(getSelection());
			} else {
				doAction(actionId);
				updateSelectableActions(getSelection());
			}
		}

		public void selectionChanged(ISelection selection) {
			// remove check for TaskFormPage
			if (getEditor().getActivePageInstance() instanceof TaskFormPage) {
				TaskFormPage editor = (TaskFormPage) getEditor().getActivePageInstance();
				setEnabled(editor.canPerformAction(actionId));
			} else {
				setEnabled(canPerformAction(actionId));
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

	private boolean canPerformAction(String actionId) {
		Control focusControl = getFocusControl();
		if (focusControl instanceof StyledText) {
			for (TextViewer viewer : textViewers) {
				if (viewer.getTextWidget() == focusControl) {
					return canDoGlobalAction(actionId, viewer);
				}
			}
		} else {
			if (actionId.equals(ActionFactory.UNDO.getId()) || actionId.equals(ActionFactory.REDO.getId())) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private Control getFocusControl() {
		IFormPage activePage = getEditor().getActivePageInstance();
		if (activePage == null) {
			return null;
		}

		IManagedForm form = activePage.getManagedForm();
		if (form == null) {
			return null;
		}
		Control control = form.getForm();
		if (control == null || control.isDisposed()) {
			return null;
		}
		Display display = control.getDisplay();
		Control focusControl = display.getFocusControl();
		if (focusControl == null || focusControl.isDisposed()) {
			return null;
		}
		return focusControl;
	}

	private void doAction(String actionId) {
		Control focusControl = getFocusControl();
		if (focusControl == null) {
			return;
		}
		if (canPerformDirectly(actionId, focusControl)) {
			return;
		}
		if (focusControl instanceof StyledText) {
			for (TextViewer viewer : textViewers) {
				if (viewer.getTextWidget() == focusControl) {
					doGlobalAction(actionId, viewer);
					return;
				}
			}
		}
	}

	private boolean doGlobalAction(String actionId, TextViewer textViewer) {
		if (actionId.equals(ActionFactory.CUT.getId())) {
			textViewer.doOperation(ITextOperationTarget.CUT);
			return true;
		} else if (actionId.equals(ActionFactory.COPY.getId())) {
			textViewer.doOperation(ITextOperationTarget.COPY);
			return true;
		} else if (actionId.equals(ActionFactory.PASTE.getId())) {
			textViewer.doOperation(ITextOperationTarget.PASTE);
			return true;
		} else if (actionId.equals(ActionFactory.DELETE.getId())) {
			textViewer.doOperation(ITextOperationTarget.DELETE);
			return true;
		} else if (actionId.equals(ActionFactory.UNDO.getId())) {
			textViewer.doOperation(ITextOperationTarget.UNDO);
			return true;
		} else if (actionId.equals(ActionFactory.REDO.getId())) {
			textViewer.doOperation(ITextOperationTarget.REDO);
			return true;
		} else if (actionId.equals(ActionFactory.SELECT_ALL.getId())) {
			textViewer.doOperation(ITextOperationTarget.SELECT_ALL);
			return true;
		}
		return false;
	}

	private boolean canPerformDirectly(String id, Control control) {
		if (control instanceof Text) {
			Text text = (Text) control;
			if (id.equals(ActionFactory.CUT.getId())) {
				text.cut();
				return true;
			}
			if (id.equals(ActionFactory.COPY.getId())) {
				text.copy();
				return true;
			}
			if (id.equals(ActionFactory.PASTE.getId())) {
				text.paste();
				return true;
			}
			if (id.equals(ActionFactory.SELECT_ALL.getId())) {
				text.selectAll();
				return true;
			}
			if (id.equals(ActionFactory.DELETE.getId())) {
				int count = text.getSelectionCount();
				if (count == 0) {
					int caretPos = text.getCaretPosition();
					text.setSelection(caretPos, caretPos + 1);
				}
				text.insert(""); //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	// FIXME these are never removed: this is a problem when the editor is refreshed 
	public void addTextViewer(TextViewer textViewer) {
		textViewers.add(textViewer);
	}

	public void removeTextViewer(TextViewer textViewer) {
		textViewers.remove(textViewer);
	}

	private boolean canDoGlobalAction(String actionId, TextViewer textViewer) {
		if (actionId.equals(ActionFactory.CUT.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.CUT);
		} else if (actionId.equals(ActionFactory.COPY.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.COPY);
		} else if (actionId.equals(ActionFactory.PASTE.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.PASTE);
		} else if (actionId.equals(ActionFactory.DELETE.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.DELETE);
		} else if (actionId.equals(ActionFactory.UNDO.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.UNDO);
		} else if (actionId.equals(ActionFactory.REDO.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.REDO);
		} else if (actionId.equals(ActionFactory.SELECT_ALL.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.SELECT_ALL);
		}
		return false;
	}

	public ISelection getSelection() {
		if (editor.getSite().getSelectionProvider() != null) {
			return editor.getSite().getSelectionProvider().getSelection();
		} else {
			return StructuredSelection.EMPTY;
		}
	}

}
