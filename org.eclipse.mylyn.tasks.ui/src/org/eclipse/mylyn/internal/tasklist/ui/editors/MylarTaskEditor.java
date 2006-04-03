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

package org.eclipse.mylar.internal.tasklist.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
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
public class MylarTaskEditor extends MultiPageEditorPart {

	private static final String TASK_INFO_PAGE_LABEL = "Planning";

	private static final String ISSUE_WEB_PAGE_LABEL = "Browser";

	protected ITask task;

	private TaskInfoEditor taskInfoEditor;

	private Browser webBrowser;

	private TaskEditorInput taskEditorInput;

	private TaskEditorListener partListener;

	private List<IEditorPart> editors = new ArrayList<IEditorPart>();

	private IEditorPart contentOutlineProvider = null;
		
	private static class TaskEditorSelectionProvider extends MultiPageSelectionProvider {
		private ISelection globalSelection;

		public TaskEditorSelectionProvider(MylarTaskEditor taskEditor) {
			super(taskEditor);
		}

		public ISelection getSelection() {
			IEditorPart activeEditor = ((MylarTaskEditor) getMultiPageEditor()).getActiveEditor();
			if (activeEditor != null && activeEditor.getSite() != null) {
				ISelectionProvider selectionProvider = activeEditor.getSite().getSelectionProvider();
				if (selectionProvider != null)
					return selectionProvider.getSelection();
			}
			return globalSelection;
		}

		public void setSelection(ISelection selection) {
			IEditorPart activeEditor = ((MylarTaskEditor) getMultiPageEditor()).getActiveEditor();
			if (activeEditor != null && activeEditor.getSite() != null) {
				ISelectionProvider selectionProvider = activeEditor.getSite().getSelectionProvider();
				if (selectionProvider != null)
					selectionProvider.setSelection(selection);
			} else {
				this.globalSelection = selection;
				fireSelectionChanged(new SelectionChangedEvent(this, globalSelection));
			}
		}
	}

	public MylarTaskEditor() {
		super();
		IWorkbench workbench = MylarTaskListPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		partListener = new TaskEditorListener();
		activePage.addPartListener(partListener);
		taskInfoEditor = new TaskInfoEditor();
		taskInfoEditor.setParentEditor(this);
	}

	@Override
	protected void createPages() {
		try {
			int index = 0;
			index = createTaskSummaryPage();
			int selectedIndex = index;
			for (ITaskEditorFactory factory : MylarTaskListPlugin.getDefault().getTaskEditorFactories()) {
				if (factory.canCreateEditorFor(task)) {
					try {
						IEditorPart editor = factory.createEditor(this);
						IEditorInput input = factory.createEditorInput(task);
						if (editor != null && input != null) {
							editors.add(editor);
							index = addPage(editor, input);
							selectedIndex = index;
							setPageText(index++, factory.getTitle());
						}
						// HACK: overwrites if multiple present
						if (factory.providesOutline()) {
							contentOutlineProvider = editor;
						}
					} catch (Exception e) {
						MylarStatusHandler.fail(e, "Could not create editor via factory: " + factory, true);
					}
				}
			}
			if (hasValidUrl()) {
				int browserIndex = createBrowserPage();
				if (selectedIndex == 0) {
					selectedIndex = browserIndex;
				}
			}
			setActivePage(selectedIndex);
			
			if (task instanceof AbstractRepositoryTask) {
				 setTitleImage(TaskListImages.getImage(TaskListImages.TASK_REPOSITORY));
			} else if (hasValidUrl()){
				 setTitleImage(TaskListImages.getImage(TaskListImages.TASK_WEB));
			}
		} catch (PartInitException e) {
			MylarStatusHandler.fail(e, "failed to create task editor pages", false);
		}
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO: consider adding: IContentOutlinePage.class.equals(adapter) && 
		if (contentOutlineProvider != null) {
			return contentOutlineProvider.getAdapter(adapter);
		} else {
			return super.getAdapter(adapter);
		}
	}
	
	public IEditorPart getActiveEditor() {
		return super.getActiveEditor();
	}

	private int createTaskSummaryPage() throws PartInitException {
		try {
			taskInfoEditor.createPartControl(getContainer());
			taskInfoEditor.setParentEditor(this);
			editors.add(taskInfoEditor);

			int index = addPage(taskInfoEditor.getControl());
			setPageText(index, TASK_INFO_PAGE_LABEL);
			return index;
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "could not add task editor", false);
		}
		return 0;
	}

	private int createBrowserPage() {
		try {
			webBrowser = new Browser(getContainer(), SWT.NONE);
			int index = addPage(webBrowser);
			setPageText(index, ISSUE_WEB_PAGE_LABEL);
			webBrowser.setUrl(task.getUrl());

			boolean openWithBrowser = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(
					TaskListPreferenceConstants.REPORT_OPEN_INTERNAL);
			if (!(task instanceof AbstractRepositoryTask) || openWithBrowser) {
				setActivePage(index);
			}
			return index;
		} catch (SWTError e) {
			MylarStatusHandler.fail(e, "Could not create Browser page: " + e.getMessage(), true);
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "could not create issue report page", false);
		}
		return 0;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		for (IEditorPart editor : editors) {
			editor.doSave(monitor);
		}		

		if (webBrowser != null) {
			webBrowser.setUrl(task.getUrl());
		} else if (hasValidUrl()) {
			createBrowserPage();
		}
	}

	/**
	 * HACK: perform real check
	 */
	private boolean hasValidUrl() {
		return task.getUrl().length() > 9;
	}
	
	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
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
		taskEditorInput = (TaskEditorInput) input;
		super.init(site, input);

		setSite(site);
		site.setSelectionProvider(new TaskEditorSelectionProvider(this));

		/*
		 * The task data is saved only once, at the initialization of the
		 * editor. This is then passed to each of the child editors. This way,
		 * only one instance of the task data is stored for each editor opened.
		 */
		task = taskEditorInput.getTask();
		try {
			taskInfoEditor.init(this.getEditorSite(), this.getEditorInput());
			taskInfoEditor.setTask(task);
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
		for (IEditorPart editor : editors) {
			if (editor.isDirty()) {
				return true;
			}
		}
		return false;
		// return taskInfoEditor.isDirty();
	}

	private class TaskEditorListener implements IPartListener {

		public void partActivated(IWorkbenchPart part) {
			if (part.equals(MylarTaskEditor.this)) {
				ITask task = taskEditorInput.getTask();
				if (TaskListView.getDefault() != null) {
					TaskListView.getDefault().selectedAndFocusTask(task);
				}  
			}
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

	/**
	 * Updates the tab titile
	 */
	public void changeTitle() {
		this.setPartName(taskEditorInput.getLabel());
	}

	public void markDirty() {
		firePropertyChange(PROP_DIRTY);
		return;
	}

	@Override
	public void setFocus() {
		// taskInfoEditor.setFocus();
	}

	public Browser getWebBrowser() {
		return webBrowser;
	}

	@Override
	protected void pageChange(int newPageIndex) {
		// super.pageChange(newPageIndex);
		for (ITaskEditorFactory factory : MylarTaskListPlugin.getDefault().getTaskEditorFactories()) {
			for (IEditorPart editor : editors) {
				factory.notifyEditorActivationChange(editor);
			}
		}
	}

	public void dispose() {
		for (IEditorPart part : editors) {
			part.dispose();
		}
		if (taskInfoEditor != null)
			taskInfoEditor.dispose();
		if (webBrowser != null)
			webBrowser.dispose();

		IWorkbench workbench = MylarTaskListPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = null;
		IWorkbenchPage activePage = null;
		if (workbench != null) {
			window = workbench.getActiveWorkbenchWindow();
		}
		if (window != null) {
			activePage = window.getActivePage();
		}
		if (activePage != null) {
			activePage.removePartListener(partListener);
		}
	}
}
