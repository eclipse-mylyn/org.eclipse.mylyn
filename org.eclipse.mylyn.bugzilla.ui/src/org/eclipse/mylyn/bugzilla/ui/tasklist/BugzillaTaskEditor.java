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
 * Created on 31-Jan-2005
 */
package org.eclipse.mylar.bugzilla.ui.tasklist;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.IBugzillaAttributeListener;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylar.bugzilla.ui.editor.ExistingBugEditor;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.ui.TaskEditor;
import org.eclipse.mylar.tasklist.ui.TaskEditorInput;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Eric Booth
 */
public class BugzillaTaskEditor extends TaskEditor {

	/** The task that created this editor */
	protected BugzillaTask bugTask;
	
	/** This bug report can be modified by the user and saved offline. */
	protected BugReport offlineBug;
	
	private ExistingBugEditor bugzillaEditor;

	private BugzillaTaskEditorInput bugzillaEditorInput;
	
//    private TaskSummaryEditor taskSummaryEditor = new TaskSummaryEditor();
    
	protected IContentOutlinePage outlinePage = null;
	
	private IBugzillaAttributeListener ATTRIBUTE_LISTENER = new IBugzillaAttributeListener() {
		public void attributeChanged(String attribute, String value) {
			if (attribute.equals("Priority")) {
				bugTask.setPriority(value);
				if (TaskListView.getDefault() != null) TaskListView.getDefault().notifyTaskDataChanged(bugTask);
			}
		}
    };    

    
	public BugzillaTaskEditor() {
		super();

		// get the workbench page and add a listener so we can detect when it closes
//		IWorkbench wb = MylarTasklistPlugin.getDefault().getWorkbench();
//		IWorkbenchWindow aw = wb.getActiveWorkbenchWindow();
//		IWorkbenchPage ap = aw.getActivePage();
//		BugzillaTaskEditorListener listener = new BugzillaTaskEditorListener();
//		ap.addPartListener(listener);
		
		bugzillaEditor = new ExistingBugEditor();
		bugzillaEditor.setParentEditor(this);
		bugzillaEditor.addAttributeListener(ATTRIBUTE_LISTENER);
//        taskSummaryEditor = new TaskSummaryEditor();
//        taskSummaryEditor.setParentEditor(this);
	}

    public AbstractBugEditor getBugzillaEditor(){
        return bugzillaEditor;
    }
    
//    public TaskSummaryEditor getTaskEditor(){
//        return taskSummaryEditor;
//    }
    
    
	public void gotoMarker(IMarker marker) {
		// don't do anything
	}
	
	/**
	 * Creates page 1 of the multi-page editor,
	 * which allows you to change the font used in page 2.
	 */
	private void createBugzillaSubmitPage() {
		bugzillaEditor.createPartControl(getContainer());
		Composite composite = bugzillaEditor.getEditorComposite();
		int index = addPage(composite);
		setPageText(index, "Bugzilla");
	}
    
    
//    private void createSummaryPage() {
//        try{
//            int index = addPage(taskSummaryEditor, new TaskEditorInput(bugTask));
//            setPageText(index, "Summary");         
//        }catch(Exception e){
//        	MylarPlugin.log(e, "summary failed");
//        }
//    }
	
	/**
	 * Creates the pages of the multi-page editor.
	 */
    @Override
	protected void createPages() {	
		createBugzillaSubmitPage();
		super.createPages();
//        createSummaryPage();
	}
	
	/**
	 * Saves the multi-page editor's document.
	 */
    @Override
	public void doSave(IProgressMonitor monitor) {
    	super.doSave(monitor);
//    	if(taskSummaryEditor.isDirty())
//    		taskSummaryEditor.doSave(monitor);
    	if(bugzillaEditor.isDirty())
    		bugzillaEditor.doSave(monitor);
    	
		// TODO save both editors if needed
	}
	
    public boolean isDirty(){
    	return bugzillaEditor.isDirty() || super.isDirty();
    }
	
	public void changeDirtyStatus(boolean newDirtyStatus) {
		firePropertyChange(PROP_DIRTY);
	}
    
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof BugzillaTaskEditorInput))
			throw new PartInitException("Invalid Input: Must be BugzillaTaskEditorInput");		
		super.init(site, (IEditorInput)new TaskEditorInput(((BugzillaTaskEditorInput)editorInput).getBugTask()));
		bugzillaEditorInput = (BugzillaTaskEditorInput) editorInput;
		bugTask = bugzillaEditorInput.getBugTask();

		offlineBug = bugzillaEditorInput.getOfflineBug();
		
		super.setSite(site);
		super.setInputWithNotify(editorInput);
		
		try {
			bugzillaEditor.init(this.getEditorSite(), this.getEditorInput());
		}
		catch (Exception e) {
			throw new PartInitException(e.getMessage());
		}
		
		// Set the title on the editor's tab
		this.setPartName("Bug #" + bugzillaEditorInput.getBugId());
		this.setTitleImage(TaskListImages.getImage(BugzillaImages.TASK_BUGZILLA));
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
	public BugReport getOfflineBug() {
		return offlineBug;
	}
	
	/**
	 * Updates the title of the editor to reflect dirty status.
	 * If the bug report has been modified but not saved, then
	 * an indicator will appear in the title.
	 * @param isDirty
	 *            is true when the bug report has been modified but not saved
	 */
	public void showDirtyStatus(boolean isDirty) {
		String prefix = (isDirty) ? "*" : "" ;
		setPartName(prefix + "Bug #" + bugzillaEditorInput.getBugId());
	}
	
//	/**
//	 * Class to listen for editor events
//	 */
//	private class BugzillaTaskEditorListener implements IPartListener
//	{
//
//		public void partActivated(IWorkbenchPart part) {
//			// don't care about this event
//		}
//
//		public void partBroughtToTop(IWorkbenchPart part) {
//			// don't care about this event
//		}
//
//		public void partClosed(IWorkbenchPart part) {
//
//			// if we are closing a bug editor
//			if (part instanceof BugzillaTaskEditor) {
//				BugzillaTaskEditor taskEditor = (BugzillaTaskEditor)part;
//				
//				// check if it needs to be saved
//				if (taskEditor.bugzillaEditor.isDirty) {
//					// ask the user whether they want to save it or not and perform the appropriate action
//					taskEditor.bugzillaEditor.changeDirtyStatus(false);
//					boolean response = MessageDialog.openQuestion(null, "Save Changes", 
//							"You have made some changes to the bug, do you want to save them?");
//					if (response) {
//						taskEditor.bugzillaEditor.saveBug();
//					} else {
//						ExistingBugEditorInput input = (ExistingBugEditorInput)taskEditor.bugzillaEditor.getEditorInput();
//						bugTask.setPriority(input.getBug().getAttribute("Priority").getValue());
//					}
//				}
//			}
//		}
//
//		public void partDeactivated(IWorkbenchPart part) {
//			// don't care about this event
//		}
//
//		public void partOpened(IWorkbenchPart part) {
//			// don't care about this event
//		}
//	}

	public void makeNewPage(BugReport serverBug, String newCommentText) {
		if (serverBug == null) {
			MessageDialog.openInformation(Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
					"Could not open bug.", "Bug #" + offlineBug.getId()
									+ " could not be read from the server.  Try refreshing the bug task.");
			return;
		}
	}

	@Override
	public Object getAdapter(Class adapter) {
		return bugzillaEditor.getAdapter(adapter);
	}
    
    public void close() {
        Display display= getSite().getShell().getDisplay();
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
