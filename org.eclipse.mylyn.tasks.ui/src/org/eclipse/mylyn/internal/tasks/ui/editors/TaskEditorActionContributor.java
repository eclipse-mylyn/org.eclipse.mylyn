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

package org.eclipse.mylar.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylar.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/**
 * @author Mik Kersten
 */
public class TaskEditorActionContributor extends MultiPageEditorActionBarContributor {

	private SubActionBars sourceActionBars;

	private MylarTaskEditor editor;

	private OpenWithBrowserAction openWithBrowserAction = new OpenWithBrowserAction();
	
	public void contextMenuAboutToShow(IMenuManager mng) {
		contextMenuAboutToShow(mng, true);
	}

	public void contextMenuAboutToShow(IMenuManager manager, boolean addClipboard) {
		// if (editor != null)
		// updateSelectableActions(editor.getSelection());
		final ITask task = editor.getTaskEditorInput().getTask();
		if (task == null) {
			return;
		} else {
			openWithBrowserAction.selectionChanged(new StructuredSelection(task));
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
						super.run(task);
					}					
				});
			}

			manager.add(new Separator());
			
			for (IDynamicSubMenuContributor contributor : TasksUiPlugin.getDefault().getDynamicMenuContributers()) {
				List<ITaskListElement> selectedElements = new ArrayList<ITaskListElement>();
				selectedElements.add(task);
				MenuManager subMenuManager = contributor.getSubMenuManager(selectedElements);
				if (subMenuManager != null) {
					manager.add(subMenuManager);
				}
			}
			
			manager.add(new Separator());
			// HACK: there should be a saner way of doing this	
			ObjectActionContributorManager.getManager().contributeObjectActions(editor, manager, new ISelectionProvider() {

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
			manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		}
	}

	public void contributeToMenu(IMenuManager mm) {
	}

	public void contributeToStatusLine(IStatusLineManager slm) {
	}

	public void contributeToToolBar(IToolBarManager tbm) {
	}

	public void contributeToCoolBar(ICoolBarManager cbm) {
	}

	public void dispose() {
		sourceActionBars.dispose();
		super.dispose();
	}

	public void init(IActionBars bars) {
		super.init(bars);
		sourceActionBars = new SubActionBars(bars);
	}

	public MylarTaskEditor getEditor() {
		return editor;
	}

	public IStatusLineManager getStatusLineManager() {
		return getActionBars().getStatusLineManager();
	}

	public void setActiveEditor(IEditorPart targetEditor) {
		if (targetEditor instanceof MylarTaskEditor) {
			editor = (MylarTaskEditor) targetEditor;
		}
		// if (targetEditor instanceof PDESourcePage) {
		// PDESourcePage page = (PDESourcePage) targetEditor;
		// PDEPlugin.getActivePage().activate(page.getEditor());
		// return;
		// }
		// if (targetEditor instanceof PDEFormEditor)
		// this.editor = (PDEFormEditor) targetEditor;
		// else
		// return;
		// editor.updateUndo(getGlobalAction(ActionFactory.UNDO.getId()),
		// getGlobalAction(ActionFactory.REDO.getId()));
		// IEditorPart page = editor.getActiveEditor();
		// setActivePage(page);
		// updateSelectableActions(editor.getSelection());
	}

	public void setActivePage(IEditorPart newEditor) {
		// if (editor == null)
		// return;
		// IFormPage oldPage = page;
		// IFormPage newPage = editor.getActivePageInstance();
		// this.page = newPage;
		// if (newPage == null)
		// return;
		// updateActions();
		// if (oldPage != null && oldPage.isEditor() == false
		// && newPage.isEditor() == false) {
		// getActionBars().updateActionBars();
		// return;
		// }
		// PDESourcePage sourcePage = null;
		// if (newPage instanceof PDESourcePage)
		// sourcePage = (PDESourcePage) newPage;
		// if (sourcePage != null && sourcePage.equals(oldPage))
		// return;
		// sourceContributor.setActiveEditor(sourcePage);
		// setSourceActionBarsActive(sourcePage != null);
	}
}
