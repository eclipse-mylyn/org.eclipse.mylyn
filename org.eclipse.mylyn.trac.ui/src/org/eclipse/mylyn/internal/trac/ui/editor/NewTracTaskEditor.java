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

package org.eclipse.mylar.internal.trac.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.NewBugEditorInput;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

// based on NewBugzillaTaskEditor

/**
 * @author Rob Elves 
 * 
 * TODO: Get rid of this wrapper.
 * Before this can be done a factory must be added to handle new bug editor input in MylarTaskEditor.addPages()
 * then all occurrences of BugzillaUiPlugin.NEW_BUG_EDITOR_ID can be replaced with TaskListPreferenceConstants.TASK_EDITOR_ID
 * so that MylarTaskEditor is opened rather than this.
 */
public class NewTracTaskEditor extends MylarTaskEditor {

	private Menu contextMenu;
	
	private NewTracTaskEditorPage newBugEditor;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);		
	}

	@Override
	protected void addPages() {
		MenuManager manager = new MenuManager();
		IMenuListener listener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		};
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(listener);
		contextMenu = manager.createContextMenu(getContainer());
		getContainer().setMenu(contextMenu);
		try {
			newBugEditor = new NewTracTaskEditorPage(this);
			int index = addPage(newBugEditor);
			String label = "<unsubmitted> "+((NewBugEditorInput)getEditorInput()).getRepository().getUrl();
			setPageText(index, "Trac");			
			setPartName(label);
		} catch (PartInitException e) {
			MylarStatusHandler.fail(e, "Could not add new bug form", true);
		}
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		return newBugEditor.getAdapter(adapter);
	}

	public NewTracTaskEditorPage getPage() {
		return newBugEditor;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		MessageDialog.openWarning(this.newBugEditor.getSite().getShell(), "Operation not supported", "Save of un-submitted new tasks is not currently supported.\nPlease submit all new tasks.");
		monitor.setCanceled(true);
	}

	@Override
	public void doSaveAs() {
		// ignore

	}

	@Override
	public boolean isSaveAsAllowed() {
		// ignore
		return false;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

}
