/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.ui.SelectionProviderAdapter;
import org.eclipse.mylyn.commons.workbench.WorkbenchActionSupport;
import org.eclipse.mylyn.commons.workbench.WorkbenchActionSupport.WorkbenchActionCallback;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskEditorActionGroup;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskEditorActionContributor extends MultiPageEditorActionBarContributor implements
		ISelectionChangedListener {

	private final class SelectionProviderAdapterExtension extends SelectionProviderAdapter implements
			ISelectionChangedListener {
		@Override
		public ISelection getSelection() {
			if (editor != null && editor.getSite().getSelectionProvider() != null) {
				return editor.getSite().getSelectionProvider().getSelection();
			} else {
				return StructuredSelection.EMPTY;
			}
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			super.selectionChanged(event);
		}

	}

	private class EditorPageCallback extends WorkbenchActionCallback {

		@Override
		public boolean canPerformAction(String actionId, Control control) {
			IFormPage activePage = getActivePage();
			if (activePage instanceof AbstractTaskEditorPage) {
				AbstractTaskEditorPage page = (AbstractTaskEditorPage) activePage;
				return page.canPerformAction(actionId);
			} else if (activePage != null) {
				WorkbenchActionCallback callback = (WorkbenchActionCallback) activePage.getAdapter(WorkbenchActionCallback.class);
				if (callback != null) {
					return callback.canPerformAction(actionId, control);
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
				return;
			} else if (activePage != null) {
				WorkbenchActionCallback callback = (WorkbenchActionCallback) activePage.getAdapter(WorkbenchActionCallback.class);
				if (callback != null) {
					callback.doAction(actionId, control);
					return;
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
			return selectionProvider.getSelection();
		}

	}

	private TaskEditor editor;

	private final WorkbenchActionSupport actionSupport;

	private final SelectionProviderAdapterExtension selectionProvider;

	private final TaskEditorActionGroup actionGroup;

	public TaskEditorActionContributor() {
		this.actionSupport = new WorkbenchActionSupport();
		this.actionSupport.setCallback(new EditorPageCallback());
		this.selectionProvider = new SelectionProviderAdapterExtension();
		this.actionGroup = new TaskEditorActionGroup(actionSupport);
	}

	public void contextMenuAboutToShow(IMenuManager mng) {
		IFormPage page = getActivePage();
		boolean addClipboard = (page instanceof TaskPlanningEditor || page instanceof AbstractTaskEditorPage);
		contextMenuAboutToShow(mng, addClipboard);
	}

	public void contextMenuAboutToShow(IMenuManager manager, boolean addClipboard) {
		actionGroup.fillContextMenu(manager, editor, addClipboard);
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

	@Override
	public void dispose() {
		actionGroup.setSelectionProvider(null);
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

	@Override
	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		actionSupport.install(bars);
		bars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), actionGroup.getSynchronizeEditorAction());
	}

	public void selectionChanged(SelectionChangedEvent event) {
		actionSupport.selectionChanged(event);
		actionGroup.getNewTaskFromSelectionAction().selectionChanged(event.getSelection());
	}

	@Override
	public void setActiveEditor(IEditorPart activeEditor) {
		if (this.editor != null) {
			this.editor.getSite().getSelectionProvider().removeSelectionChangedListener(selectionProvider);
		}

		if (activeEditor instanceof TaskEditor) {
			this.editor = (TaskEditor) activeEditor;
			this.editor.getSite().getSelectionProvider().addSelectionChangedListener(selectionProvider);
			actionGroup.getSynchronizeEditorAction().selectionChanged(new StructuredSelection(this.editor));
			updateSelectableActions(selectionProvider.getSelection());
		} else {
			actionGroup.getSynchronizeEditorAction().selectionChanged(StructuredSelection.EMPTY);
			this.editor = null;
		}
	}

	@Override
	public void setActivePage(IEditorPart activePage) {
		updateSelectableActions(selectionProvider.getSelection());
	}

	public void updateSelectableActions(ISelection selection) {
		if (editor != null) {
			actionSupport.updateActions(selection);
			actionGroup.getNewTaskFromSelectionAction().selectionChanged(selection);
		}
	}

}
