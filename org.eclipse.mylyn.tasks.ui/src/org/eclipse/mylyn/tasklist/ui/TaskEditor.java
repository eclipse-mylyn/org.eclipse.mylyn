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

package org.eclipse.mylar.tasklist.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.IContextEditorFactory;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
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
import org.eclipse.ui.part.MultiPageSelectionProvider;

/**
 * @author Mik Kersten
 * @author Eric Booth (initial prototype)
 */
public class TaskEditor extends MultiPageEditorPart {

	private static final String TASK_INFO_PAGE_LABEL = "Task Info";
	private static final String ISSUE_WEB_PAGE_LABEL = "Browser";
	protected ITask task;
	private TaskSummaryEditor taskSummaryEditor;
	private Browser webBrowser;
	private TaskEditorInput taskEditorInput;
	
	private static class TaskEditorSelectionProvider extends MultiPageSelectionProvider {
		private ISelection globalSelection;
		
		public TaskEditorSelectionProvider(TaskEditor taskEditor) {
			super(taskEditor);
		}
		
		public ISelection getSelection() {
			IEditorPart activeEditor = ((TaskEditor) getMultiPageEditor()).getActiveEditor();
			if (activeEditor != null && activeEditor.getSite() != null) {
				ISelectionProvider selectionProvider = activeEditor.getSite().getSelectionProvider();
				if (selectionProvider != null)
					return selectionProvider.getSelection();
			}
			return globalSelection;
		}

		public void setSelection(ISelection selection) {
			IEditorPart activeEditor = ((TaskEditor) getMultiPageEditor()).getActiveEditor();
			if (activeEditor != null && activeEditor.getSite() != null) {
				ISelectionProvider selectionProvider = activeEditor.getSite().getSelectionProvider();
				if (selectionProvider != null) selectionProvider.setSelection(selection);
			} else {
				this.globalSelection = selection;
				fireSelectionChanged(new SelectionChangedEvent(this, globalSelection));
			}
		}
	}

	public TaskEditor() {
		super();
		IWorkbench workbench = MylarTasklistPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		TaskEditorListener listener = new TaskEditorListener();
		activePage.addPartListener(listener);
		taskSummaryEditor = new TaskSummaryEditor();
	}

	@Override
	protected void createPages() {
		try { 
			int index = createTaskSummaryPage();
			if(task.getIssueReportURL().length() > 9){
				createTaskIssueWebPage();
			}
			for (IContextEditorFactory factory : MylarTasklistPlugin.getDefault().getContextEditors()) {
				taskSummaryEditor.setParentEditor(this);
				IEditorPart editor = factory.createEditor();
				index = addPage(editor, factory.createEditorInput(MylarPlugin.getContextManager().getActiveContext()));
				setPageText(index++, factory.getTitle()); 
			}
		} catch (PartInitException e) {
			MylarPlugin.fail(e, "failed to create task editor pages", false);
		}
	}
	
	public IEditorPart getActiveEditor() {
		return super.getActiveEditor();
	}

	private int createTaskSummaryPage() throws PartInitException {
		try {
			taskSummaryEditor.createPartControl(getContainer());
			taskSummaryEditor.setParentEditor(this);
			int index = addPage(taskSummaryEditor.getControl());
			setPageText(index, TASK_INFO_PAGE_LABEL);	
			return index;
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "could not add task editor", false);
		}		
		return 0;
	}

	/**
	 * Creates page 2 of the multi-page editor,
	 * which displays the task issue web page
	 */
	private void createTaskIssueWebPage() {
		try {
			webBrowser = new Browser(getContainer(), SWT.NONE);
			int index = addPage(webBrowser);
			setPageText(index, ISSUE_WEB_PAGE_LABEL);
			webBrowser.setUrl(task.getIssueReportURL());
			
			boolean openWithBrowser = MylarTasklistPlugin.getPrefs().getBoolean(
					MylarTasklistPlugin.REPORT_OPEN_INTERNAL);
			if (task.isDirectlyModifiable() || openWithBrowser) setActivePage(index);
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "could not open issue report web page", false);
		}
	}	

	@Override
	public void doSave(IProgressMonitor monitor) {
		taskSummaryEditor.doSave(monitor);
		if (webBrowser != null){
			webBrowser.setUrl(task.getIssueReportURL());
		}
		else if(task.getIssueReportURL().length() > 9){
			createTaskIssueWebPage();
		}
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
		taskEditorInput = (TaskEditorInput)input;
		super.init(site, input);

		setSite(site);
		site.setSelectionProvider(new TaskEditorSelectionProvider(this));
		
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

	public Browser getWebBrowser() {
		return webBrowser;
	}
}
