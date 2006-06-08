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
/*
 * Created on 31-Jan-2005
 */
package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.bugzilla.ui.editor.ExistingBugEditor;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylar.internal.tasklist.ui.editors.IRepositoryTaskAttributeListener;
import org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.TaskEditorInput;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskEditor extends MylarTaskEditor {

	private static final String EDITOR_TAB_ITLE = "Bug Editor";

	/** The task that created this editor */
	protected BugzillaTask bugTask;

	/** This bug report can be modified by the user and saved offline. */
	protected RepositoryTaskData offlineBug;

	private ExistingBugEditor bugzillaEditor;

	private BugzillaTaskEditorInput bugzillaEditorInput;

//	private RepositoryTaskOutlinePage outlinePage = null;
	
//	protected RepositoryTaskOutlineNode bugzillaOutlineModel = null;

	private IRepositoryTaskAttributeListener ATTRIBUTE_LISTENER = new IRepositoryTaskAttributeListener() {
		public void attributeChanged(String attribute, String value) {
			// TODO: get rid of this?
			if (attribute.equals("Priority")) {
				bugTask.setPriority(value);
//				if (TaskListView.getDefault() != null)
//					TaskListView.getDefault().notifyTaskDataChanged(bugTask);
			}
		}
	};

	public BugzillaTaskEditor() {
		super();

		// get the workbench page and add a listener so we can detect when it
		// closes
		// IWorkbench wb = MylarTaskListPlugin.getDefault().getWorkbench();
		// IWorkbenchWindow aw = wb.getActiveWorkbenchWindow();
		// IWorkbenchPage ap = aw.getActivePage();
		// BugzillaTaskEditorListener listener = new
		// BugzillaTaskEditorListener();
		// ap.addPartListener(listener);

		bugzillaEditor = new ExistingBugEditor();
		bugzillaEditor.setParentEditor(this);
		bugzillaEditor.addAttributeListener(ATTRIBUTE_LISTENER);
		// taskSummaryEditor = new TaskInfoEditor();
		// taskSummaryEditor.setParentEditor(this);
	}

	public AbstractRepositoryTaskEditor getBugzillaEditor() {
		return bugzillaEditor;
	}

	public void gotoMarker(IMarker marker) {
		// don't do anything
	}

	/**
	 * Creates page 1 of the multi-page editor, which allows you to change the
	 * font used in page 2.
	 */
	private void createBugzillaSubmitPage() {
		bugzillaEditor.createPartControl(getContainer());
		Composite composite = bugzillaEditor.getEditorComposite();
		int index = addPage(composite);
		setPageText(index, EDITOR_TAB_ITLE);
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	@Override
	protected void createPages() {
		createBugzillaSubmitPage();
		super.createPages();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		// if(taskSummaryEditor.isDirty())
		// taskSummaryEditor.doSave(monitor);
		if (bugzillaEditor.isDirty())
			bugzillaEditor.doSave(monitor);

		// TODO save both editors if needed
	}

	public boolean isDirty() {
		return bugzillaEditor.isDirty() || super.isDirty();
	}

	public void changeDirtyStatus(boolean newDirtyStatus) {
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof BugzillaTaskEditorInput))
			throw new PartInitException("Invalid Input: Must be BugzillaTaskEditorInput");
		super.init(site, (IEditorInput) new TaskEditorInput(((BugzillaTaskEditorInput) editorInput).getBugTask(), false));
		bugzillaEditorInput = (BugzillaTaskEditorInput) editorInput;
		bugTask = bugzillaEditorInput.getBugTask();
		bugzillaEditor.setTaskOutlineModel(RepositoryTaskOutlineNode.parseBugReport(bugzillaEditorInput.getRepositoryTaskData()));
		offlineBug = bugzillaEditorInput.getRepositoryTaskData();

		super.setSite(site);
		super.setInput(editorInput);

		try {
			bugzillaEditor.init(this.getEditorSite(), this.getEditorInput());
		} catch (Exception e) {
			throw new PartInitException(e.getMessage());
		}

		// Set the title on the editor's tab
		// this.setPartName("Bug #" + bugzillaEditorInput.getBugId());
		this.setPartName(bugTask.getDescription());
		this.setTitleImage(TaskListImages.getImage(TaskListImages.TASK_REPOSITORY));
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
	}

	/**
	 * Sets the font related data to be applied to the text in page 2.
	 */
	@Override
	public void setFocus() {
		// The default focus for this editor is the submit page
		bugzillaEditor.setFocus();
	}

	/**
	 * @return Returns the bugTask.
	 */
	public BugzillaTask getBugTask() {
		return bugTask;
	}

	/**
	 * @return Returns the offlineBug.
	 */
	public RepositoryTaskData getOfflineBug() {
		return offlineBug;
	}

	/**
	 * Updates the title of the editor to reflect dirty status. If the bug
	 * report has been modified but not saved, then an indicator will appear in
	 * the title.
	 * 
	 * @param isDirty
	 *            is true when the bug report has been modified but not saved
	 */
	public void showDirtyStatus(boolean isDirty) {
		String prefix = (isDirty) ? "*" : "";
		setPartName(prefix + "Bug #" + bugzillaEditorInput.getBugId());
	}


//	@Override
//	public Object getAdapter(Class adapter) {
//		if (IContentOutlinePage.class.equals(adapter)) {
//			if (outlinePage == null && bugzillaEditorInput != null) {
//				outlinePage = new RepositoryTaskOutlinePage(bugzillaOutlineModel);
//			}
//			return outlinePage;
//		}
//		return super.getAdapter(adapter);
//	}
	
	@Override
	public Object getAdapter(Class adapter) {
		return bugzillaEditor.getAdapter(adapter);
	}
	
	// /**
	// * Class to listen for editor events
	// */
	// private class BugzillaTaskEditorListener implements IPartListener
	// {
	//
	// public void partActivated(IWorkbenchPart part) {
	// // don't care about this event
	// }
	//
	// public void partBroughtToTop(IWorkbenchPart part) {
	// // don't care about this event
	// }
	//
	// public void partClosed(IWorkbenchPart part) {
	//
	// // if we are closing a bug editor
	// if (part instanceof BugzillaTaskEditor) {
	// BugzillaTaskEditor taskEditor = (BugzillaTaskEditor)part;
	//				
	// // check if it needs to be saved
	// if (taskEditor.bugzillaEditor.isDirty) {
	// // ask the user whether they want to save it or not and perform the
	// appropriate action
	// taskEditor.bugzillaEditor.changeDirtyStatus(false);
	// boolean response = MessageDialog.openQuestion(null, "Save Changes",
	// "You have made some changes to the bug, do you want to save them?");
	// if (response) {
	// taskEditor.bugzillaEditor.saveBug();
	// } else {
	// ExistingBugEditorInput input =
	// (ExistingBugEditorInput)taskEditor.bugzillaEditor.getEditorInput();
	// bugTask.setPriority(input.getBug().getAttribute("Priority").getValue());
	// }
	// }
	// }
	// }
	//
	// public void partDeactivated(IWorkbenchPart part) {
	// // don't care about this event
	// }
	//
	// public void partOpened(IWorkbenchPart part) {
	// // don't care about this event
	// }
	// }

	public void makeNewPage(RepositoryTaskData serverBug, String newCommentText) {
		if (serverBug == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Could not open bug.", "Bug #" + offlineBug.getId()
							+ " could not be read from the server.  Try refreshing the bug task.");
			return;
		}
	}

	public void close() {
		Display display = getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				getSite().getPage().closeEditor(BugzillaTaskEditor.this, false);
			}
		});
	}

	@Override
	public void doSaveAs() {
		// do nothing here
	}
}
