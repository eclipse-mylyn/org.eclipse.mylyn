/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on 19-Jan-2005
 */
package org.eclipse.mylar.tasklist.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Eric Booth
 * @author Mik Kersten
 */
public class TaskEditor extends MultiPageEditorPart {

	private static final String EDITOR_PAGE_LABEL = "Task Info";
	protected ITask task;
	private TaskSummaryEditor taskSummaryEditor;
	private TaskEditorInput taskEditorInput;
	
	public TaskEditor() {
		super();

		// get the workbench page and add a listener so we can detect when it closes
		IWorkbench wb = MylarTasklistPlugin.getDefault().getWorkbench();
		IWorkbenchWindow aw = wb.getActiveWorkbenchWindow();
		IWorkbenchPage ap = aw.getActivePage();
		TaskEditorListener listener = new TaskEditorListener();
		ap.addPartListener(listener);
		
		taskSummaryEditor = new TaskSummaryEditor();
	}

	/**
	 * Creates page 1 of the multi-page editor,
	 * which displays the task for viewing.
	 */
	private void createTaskSummaryPage() {
		taskSummaryEditor.createPartControl(getContainer());
		taskSummaryEditor.setParentEditor(this);
		int index = addPage(taskSummaryEditor.getControl());
		setPageText(index, EDITOR_PAGE_LABEL);
		
		for (IEditorPart editor : MylarTasklistPlugin.getDefault().getTaskEditors()) {
			try {
				taskSummaryEditor.setParentEditor(this);
				index = addPage(editor, null);
				setPageText(index, "xxx");
			} catch (PartInitException e) {
				MylarPlugin.fail(e, "could not add task editor", false);
			}
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	@Override
	protected void createPages() {
		createTaskSummaryPage();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		taskSummaryEditor.doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

//		if (!(input instanceof TaskEditorInput))
//			throw new PartInitException("Invalid Input: Must be TaskEditorInput");
			taskEditorInput = (TaskEditorInput)input;
		super.init(site, input);

		/*
		 * The task data is saved only once, at the initialization of the editor.  This is
		 * then passed to each of the child editors.  This way, only one instance of 
		 * the task data is stored for each editor opened.
		*/
		task = taskEditorInput.getTask();		
		try {
			taskSummaryEditor.init(this.getEditorSite(), this.getEditorInput());
			taskSummaryEditor.setTask(task);
			// Set the title on the editor's tab
			this.setPartName(taskEditorInput.getLabel());
		} catch (Exception e) {
			throw new PartInitException(e.getMessage());
		}
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return taskSummaryEditor.isDirty();
	}
	/**
	 * Class to listen for editor events
	 */
	private class TaskEditorListener implements IPartListener
	{

		/**
		 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partActivated(IWorkbenchPart part) {
			// don't care about this event
		}

		/**
		 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partBroughtToTop(IWorkbenchPart part) {
			// don't care about this event
		}

		/**
		 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partClosed(IWorkbenchPart part) {
			// don't care about this event
		}

		/**
		 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partDeactivated(IWorkbenchPart part) {
			// don't care about this event
		}

		/**
		 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
		 */
		public void partOpened(IWorkbenchPart part) {
			// don't care about this event
		}
	}
	
	public void updatePartName() {
		firePropertyChange(PROP_DIRTY);
		return;
	}
	@Override
	public void setFocus() {
//		taskSummaryEditor.setFocus();
	}
}
