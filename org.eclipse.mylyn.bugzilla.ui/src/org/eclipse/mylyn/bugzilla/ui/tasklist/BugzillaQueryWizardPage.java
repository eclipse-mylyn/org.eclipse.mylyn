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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPrefConstants;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractNewQueryPage;
import org.eclipse.mylar.tasklist.TaskRepository;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 */
public class BugzillaQueryWizardPage extends AbstractNewQueryPage {

	private static final String TITLE = "New Bugzilla Query";

	private static final String DESCRIPTION = "Enter the parameters for this query.";
	
	private BugzillaQueryDialog queryDialog;

	public BugzillaQueryWizardPage(TaskRepository repository) {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		queryDialog = new BugzillaQueryDialog(repository);
	}

	public void createControl(Composite parent) {
		queryDialog.createContents(parent);
		setControl(parent);
	}

	@Override
	public void addQuery() {
		queryDialog.okPressed();
		
		TaskRepository repository = queryDialog.getRepository();
		if (repository == null) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Mylar Bugzilla Client",
					TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
			return;
		}    		
		
    	final BugzillaQueryCategory queryCategory;
    	if(!queryDialog.isCustom()){
    		queryCategory = new BugzillaQueryCategory(repository.getUrl().toExternalForm(), queryDialog.getUrl(), queryDialog.getName(), queryDialog.getMaxHits());
    	} else {
    		queryCategory = new BugzillaCustomQueryCategory(repository.getUrl().toExternalForm(), queryDialog.getName(), queryDialog.getUrl(), queryDialog.getMaxHits());
    	}
		MylarTaskListPlugin.getTaskListManager().addQuery(queryCategory);
    	boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.WORK_OFFLINE);
		if(!offline){
            WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            	protected void execute(IProgressMonitor monitor) throws CoreException {
	            	queryCategory.refreshBugs();
            	}
            };
            
            IProgressService service = PlatformUI.getWorkbench().getProgressService();
            try {
            	service.run(true, true, op);
            } catch (Exception e) {
            	MylarStatusHandler.log(e, "There was a problem executing the query refresh");
            }  
		}
        if(TaskListView.getDefault() != null) {
        	// TODO: remove
        	TaskListView.getDefault().getViewer().refresh();
        }
	}
}
