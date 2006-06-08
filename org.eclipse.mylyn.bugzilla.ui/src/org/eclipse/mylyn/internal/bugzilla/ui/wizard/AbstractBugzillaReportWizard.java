/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.wizard;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.Proxy;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.WebBrowserDialog;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.util.ExceptionHandler;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * Class that contains shared functions for the wizards that submit bug reports.
 * 
 * @author Eric Booth
 * @author Mik Kersten (some hardening of prototype)
 */
public abstract class AbstractBugzillaReportWizard extends Wizard implements INewWizard {

	/** The ID of the posted bug report. */
	private String id;

	protected boolean fromDialog = false;

	private BugzillaReportSubmitForm form;

	/** The model used to store all of the data for the wizard */
	protected NewBugzillaReport model;

	/**
	 * Flag to indicate if the wizard can be completed based on the attributes
	 * page
	 */
	protected boolean attributeCompleted = false;

	/** The workbench instance */
	protected IWorkbench workbenchInstance;

	private final TaskRepository repository;

	// Flag to indicate if the bug was successfully sent
	private boolean sentSuccessfully = false;

	public AbstractBugzillaReportWizard(TaskRepository repository) {
		super();
		this.repository = repository;
		model = new NewBugzillaReport(repository.getUrl(), MylarTaskListPlugin.getDefault().getOfflineReportsFile()
				.getNextOfflineBugId());		
		id = null; // Since there is no bug posted yet.
		super.setDefaultPageImageDescriptor(BugzillaUiPlugin.imageDescriptorFromPlugin(
				"org.eclipse.mylar.internal.bugzilla.ui", "icons/wizban/bug-wizard.gif"));
		// setForcePreviousAndNextButtons(true);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbenchInstance = workbench;
	}

	@Override
	public void addPages() {
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		getWizardDataPage().saveDataToModel();
		return postBug();

		// if (postBug()) {
		// // if (!fromDialog)
		// // openBugEditor();
		// return true;
		// }
		// // If the report was not sent, keep the wizard open
		// else {
		// return false;
		// }
		// }

		// if (getWizardDataPage().offlineSelected()) {
		// saveBugOffline();
		// return true;
		// }

		// If no action was selected, keep the wizard open.
		// return false;
	}

	/**
	 * Attempts to post the bug on the Bugzilla server. If it fails, an error
	 * message pops up.
	 * 
	 * @return true if the bug is posted successfully, and false otherwise
	 */
	protected boolean postBug() {

		final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
				boolean wrap = IBugzillaConstants.BugzillaServerVersion.SERVER_218.equals(repository.getVersion());
				try {
					form = BugzillaReportSubmitForm.makeNewBugPost(repository.getUrl(), repository.getUserName(),
							repository.getPassword(), proxySettings, repository.getCharacterEncoding(), model, wrap);				
					id = form.submitReportToRepository();
					if (id != null) {
						sentSuccessfully = true;
					}
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				}
			}
		};

		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			service.run(true, false, op);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof BugzillaException) {
				MessageDialog.openError(getWizardDataPage().getShell(), "I/O Error",
						"Bugzilla could not post your bug.");
			} else if (e.getCause() instanceof PossibleBugzillaFailureException) {
				WebBrowserDialog.openAcceptAgreement(getWizardDataPage().getShell(),
						"Bugzilla Submission Error Message", e.getCause().getMessage(), form.getError());
			} else if (e.getCause() instanceof LoginException) {
				MessageDialog.openError(getWizardDataPage().getShell(), "Posting Error",
						"Bugzilla could not post your bug since your login name or password is incorrect."
								+ "\nPlease check your settings in the bugzilla preferences. ");
			} else if (e.getCause() instanceof UnsupportedEncodingException) {			
				// should never get here but just in case...
				MessageDialog.openError(getWizardDataPage().getShell(), "Posting Error", "Ensure proper encoding selected in "
						+ TaskRepositoriesView.NAME + ".");			
			}
		} catch (InterruptedException e) {
			// ignore
		}
		return sentSuccessfully;

		// final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
		// protected void execute(final IProgressMonitor monitor) throws
		// CoreException {
		// PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
		// public void run() {
		// Proxy proxySettings =
		// MylarTaskListPlugin.getDefault().getProxySettings();
		// boolean wrap =
		// IBugzillaConstants.BugzillaServerVersion.SERVER_218.equals(repository.getVersion());
		// BugzillaReportSubmitForm form =
		// BugzillaReportSubmitForm.makeNewBugPost(repository.getUrl(),
		// repository.getUserName(), repository.getPassword(), proxySettings,
		// model, wrap);
		// try {
		// id = form.submitReportToRepository();
		//
		// if (id != null) {
		// sentSuccessfully = true;
		// }
		// } catch (BugzillaException e) {
		// MessageDialog.openError(null, "I/O Error", "Bugzilla could not post
		// your bug.");
		// BugzillaPlugin.log(e);
		// } catch (PossibleBugzillaFailureException e) {
		// WebBrowserDialog.openAcceptAgreement(null, "Possible Bugzilla Client
		// Failure",
		// "Bugzilla may not have posted your bug.\n" + e.getMessage(),
		// form.getError());
		// BugzillaPlugin.log(e);
		// } catch (LoginException e) {
		// MessageDialog.openError(null, "Posting Error",
		// "Bugzilla could not post your bug since your login name or password
		// is incorrect."
		// + "\nPlease check your settings in the bugzilla preferences. ");
		// sentSuccessfully = false;
		// }
		// }
		//
		// });
		// }
		// };

	}

	/**
	 * Try to open the editor with the newly created bug.
	 */
	protected void openBugEditor() {

		IEditorInput input = null;
		try {
			input = new ExistingBugEditorInput(repository, Integer.parseInt(id));
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
					BugzillaUiPlugin.EXISTING_BUG_EDITOR_ID, false);
		} catch (LoginException e) {
			// if we had an error with logging in, display an error
			MessageDialog.openError(null, "Posting Error",
					"Bugzilla could not access and display your bug in the editor because your login name or password is incorrect."
							+ "\nPlease check your settings in the bugzilla preferences. ");
		} catch (PartInitException e) {
			// if there was a problem, handle it and log it, then get out of
			// here
			ExceptionHandler.handle(e, SearchMessages.Search_Error_search_title,
					SearchMessages.Search_Error_search_message);
			BugzillaPlugin.log(e.getStatus());
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Failed to open Bugzilla report", false);
		}
	}

	/**
	 * Saves the bug report offline on the user's hard-drive. All offline bug
	 * reports are saved together in a single file in the plug-in's directory.
	 */
	abstract protected void saveBugOffline();

	public String getId() {
		return id;
	}

	/**
	 * @return the last page of this wizard
	 */
	abstract protected AbstractBugzillaWizardPage getWizardDataPage();

	public TaskRepository getRepository() {
		return repository;
	}
}
