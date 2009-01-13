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

package org.eclipse.mylyn.internal.provisional.commons.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

/**
 * Manages commons workbench actions for editing text.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class WorkbenchActionSupport implements ISelectionChangedListener {

	private class GlobalAction extends Action {

		private final String actionId;

		public GlobalAction(String actionId) {
			this.actionId = actionId;
		}

		@Override
		public void run() {
			if (callback != null) {
				callback.doAction(actionId, callback.getFocusControl());
				updateActions(callback.getSelection());
			}
		}

		public void selectionChanged(ISelection selection) {
			if (callback != null) {
				setEnabled(callback.canPerformAction(actionId, callback.getFocusControl()));
			} else {
				setEnabled(false);
			}
		}
	}

	public static class WorkbenchActionCallback {

		public boolean canPerformAction(String actionId, Control control) {
			return CommonTextSupport.canPerformAction(actionId, control);
		}

		public void doAction(String actionId, Control control) {
			CommonTextSupport.doAction(actionId, control);
		}

		public Control getFocusControl() {
			return Display.getDefault().getFocusControl();
		}

		public ISelection getSelection() {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null && window.getSelectionService() != null) {
				return window.getSelectionService().getSelection();
			}
			return null;
		}

	}

	private WorkbenchActionCallback callback;

	private final GlobalAction copyAction;

	private final GlobalAction cutAction;

	private final GlobalAction findAction;

	private final GlobalAction pasteAction;

	private final GlobalAction redoAction;

	private final GlobalAction selectAllAction;

	private final GlobalAction undoAction;

	public WorkbenchActionSupport() {
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

		findAction = new GlobalAction(ActionFactory.FIND.getId());
		findAction.setText(WorkbenchMessages.Workbench_findReplace);
		findAction.setImageDescriptor(CommonImages.FIND);
		findAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.FIND_REPLACE);
	}

	public void contributeActions(IMenuManager manager) {
		manager.add(undoAction);
		manager.add(redoAction);
		manager.add(new Separator());
		manager.add(cutAction);
		manager.add(copyAction);
		manager.add(pasteAction);
		manager.add(selectAllAction);
		manager.add(new Separator());
	}

	public void forceEditActionsEnabled() {
		cutAction.setEnabled(true);
		copyAction.setEnabled(true);
		pasteAction.setEnabled(true);
		selectAllAction.setEnabled(true);
		undoAction.setEnabled(false);
		redoAction.setEnabled(false);
	}

	public WorkbenchActionCallback getCallback() {
		return callback;
	}

	public Action getCopyAction() {
		return copyAction;
	}

	public Action getCutAction() {
		return cutAction;
	}

	public Action getFindAction() {
		return findAction;
	}

	public Action getPasteAction() {
		return pasteAction;
	}

	public Action getRedoAction() {
		return redoAction;
	}

	public Action getSelectAllAction() {
		return selectAllAction;
	}

	public Action getUndoAction() {
		return undoAction;
	}

	public void install(IActionBars bars) {
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), cutAction);
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAllAction);
		bars.setGlobalActionHandler(ActionFactory.FIND.getId(), findAction);
		bars.updateActionBars();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof TextSelection) {
			// only update global actions
			updateActions(event.getSelection());
		} else if (selection.isEmpty()) {
			// XXX a styled text widget has lost focus, re-enable all edit actions
			forceEditActionsEnabled();
		}
	}

	public void setCallback(WorkbenchActionCallback callback) {
		this.callback = callback;
	}

	public void uninstall(IActionBars bars) {
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), null);
		bars.setGlobalActionHandler(ActionFactory.FIND.getId(), null);
		bars.updateActionBars();
	}

	public void updateActions(ISelection selection) {
		cutAction.selectionChanged(selection);
		copyAction.selectionChanged(selection);
		pasteAction.selectionChanged(selection);
		undoAction.selectionChanged(selection);
		redoAction.selectionChanged(selection);
		selectAllAction.selectionChanged(selection);
		findAction.selectionChanged(selection);
	}

}
