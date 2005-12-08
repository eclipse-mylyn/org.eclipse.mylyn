/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.ui.wizard;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.BugPost;
import org.eclipse.mylar.bugzilla.core.BugzillaException;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaPreferencePage;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.NewBugModel;
import org.eclipse.mylar.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.WebBrowserDialog;
import org.eclipse.mylar.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylar.bugzilla.ui.editor.ExistingBugEditorInput;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.util.ExceptionHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressService;


/**
 * Class that contains shared functions for the wizards that submit bug reports.
 *
 * @author Eric Booth
 * @author Mik Kersten (some hardening of prototype)
 */
public abstract class AbstractBugWizard extends Wizard implements INewWizard {

	protected boolean fromDialog = false;
	
	/** The model used to store all of the data for the wizard */
	protected NewBugModel model;

	/**
	 * Flag to indicate if the wizard can be completed based on the attributes
	 * page
	 */
	protected boolean attributeCompleted = false;

	/** The workbench instance */
	protected IWorkbench workbenchInstance;

	/** The ID of the posted bug report. */
	private String id;

	/**
	 * Constructor for AbstractBugWizard
	 */
	public AbstractBugWizard() {
		super();
		model = new NewBugModel();
		id = null; // Since there is no bug posted yet.
		super.setDefaultPageImageDescriptor(BugzillaUiPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.bugzilla.ui", "icons/wizban/bug-wizard.gif"));
		setForcePreviousAndNextButtons(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbenchInstance = workbench;
	}

	@Override
	public void addPages() {
		try {
			// check Bugzilla preferences to see if user has supplied a username
			if (BugzillaPreferencePage.getUserName().equals(""))
				throw new LoginException(
						"A Bugzilla User Name has not been provided."
								+ "  Please check your Bugzilla Preferences information.");
			// Each wizard has its own way of creating and adding the page
			addPagesHelper();
		} catch (LoginException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Posting Error",
			        "Cannot proceed because your login name or password is incorrect."
			        + "\nPlease check your settings in the Bugzilla preferences. ");
	} catch (Exception e) {
		    BugzillaPlugin.getDefault().logAndShowExceptionDetailsDialog(e, "occurred.", "Bugzilla Error");
		}
	}

	/**
	 * A helper function for "addPages" that creates and adds the first page to
	 * the wizard
	 * 
	 * @throws Exception
	 */
	abstract protected void addPagesHelper() throws Exception;

	/**
	 * A helper function for "canFinish" (implemented in the individual wizard
	 * classes)
	 * 
	 * @param dataPage
	 *            The first page in the wizard, casted as an
	 *            AbstractWizardListPage.
	 * @return true if the wizard could be finished, and false otherwise
	 */
	public boolean canFinishHelper(AbstractWizardListPage dataPage) {
		// cannot complete the wizard from the first page
		if (this.getContainer().getCurrentPage() == dataPage)
			return false;
		else
			return attributeCompleted;
	}

	@Override
	public boolean performFinish() {
		if (getWizardDataPage().serverSelected()) {
			getWizardDataPage().saveDataToModel();
			// If the bug report is sent successfully,
			// then close the wizard and open the bug in an editor
			if (postBug()) {
				if(!fromDialog) openBugEditor();
				return true;
			}
			// If the report was not sent, keep the wizard open
			else {
				return false;
			}
		}
		
		if (getWizardDataPage().offlineSelected()) {
			saveBugOffline();
			return true;
		}
		
		// If no action was selected, keep the wizard open.
		return false;
	}

	// Flag to indicate if the bug was successfully sent
	private boolean sentSuccessfully = false;
	
	/**
	 * Attempts to post the bug on the Bugzilla server. If it fails, an error
	 * message pops up.
	 * 
	 * @return true if the bug is posted successfully, and false otherwise
	 */
	protected boolean postBug() {
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(final IProgressMonitor monitor) throws CoreException {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable(){
					public void run() {
						BugPost form = new BugPost();
						form.setPrefix("Bug ");
						form.setPostfix1(" posted");
						form.setPostfix2(" Submitted");

						try {
							setURL(form, "post_bug.cgi");
							// go through all of the attributes and add them to the bug post
							Iterator<Attribute> itr = model.getAttributes().iterator();
							while (itr.hasNext()) {
								Attribute a = itr.next();
								if (a != null && a.getParameterName() != null
										&& a.getParameterName().compareTo("") != 0
										&& !a.isHidden()) {
									String key = a.getName();
									String value = null;

									// get the values from the attribute
									if (key.equalsIgnoreCase("OS")) {
										value = a.getValue();
									} else if (key.equalsIgnoreCase("Version")) {
										value = a.getValue();
									} else if (key.equalsIgnoreCase("Severity")) {
										value = a.getValue();
									} else if (key.equalsIgnoreCase("Platform")) {
										value = a.getValue();
									} else if (key.equalsIgnoreCase("Component")) {
										value = a.getValue();
									} else if (key.equalsIgnoreCase("Priority")) {
										value = a.getValue();
									} else if (key.equalsIgnoreCase("URL")) {
										value = a.getValue();
									} else if (key.equalsIgnoreCase("Assign To") || key.equalsIgnoreCase("Assigned To")) {
										value = a.getValue();
									}

									// add the attribute to the bug post
									if (value == null)
										value = "";

									form.add(a.getParameterName(), value);
								} else if (a != null && a.getParameterName() != null
										&& a.getParameterName().compareTo("") != 0
										&& a.isHidden()) {
									// we have a hidden attribute, add it to the posting
									form.add(a.getParameterName(), a.getValue());

								}

							}

							// set the summary, and description

							// add the summary to the bug post
							form.add("short_desc", model.getSummary());
							
							// dummy target milestone
							form.add("target_milestone", "---");

							// format the description of the bug so that it is roughly in 80
							// character lines
							formatDescription();

							if (model.getDescription().length() != 0) {
								// add the new comment to the bug post if there is some text in
								// it
								form.add("comment", model.getDescription());
							}

							// update the bug on the server
							try {
								id = form.post();

								if (id != null) {
									sentSuccessfully = true;
								}

							} catch (BugzillaException e) {
								MessageDialog
										.openError(
												null,
												"I/O Error",
												"Bugzilla could not post your bug.");
								BugzillaPlugin.log(e);
							} catch (PossibleBugzillaFailureException e) {
								WebBrowserDialog
								.openAcceptAgreement(
										null,
										"Possible Bugzilla Client Failure",
										"Bugzilla may not have posted your bug.\n" + e.getMessage(), form.getError());
								BugzillaPlugin.log(e);
							} catch (LoginException e) {
								// if we had an error with logging in, display an error
								MessageDialog
										.openError(
												null,
												"Posting Error",
												"Bugzilla could not post your bug since your login name or password is incorrect."
														+ "\nPlease check your settings in the bugzilla preferences. ");
								sentSuccessfully = false;
							}
						} catch (MalformedURLException e) {
							MessageDialog
									.openError(
											null,
											"Unsupported Protocol",
											"The server that was specified for Bugzilla is not supported by your JVM."
													+ "\nPlease make sure that you are using a JDK that supports SSL.");
							BugzillaPlugin.log(e);
							sentSuccessfully = false;
						}
					}
					
				});
			}
		};

		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			service.run(false, false, op);
		} catch (Exception e) {
			ErrorLogger.log(e, "Unable to submit bug");
		} 
		return sentSuccessfully;
	}

	/**
	 * Try to open the editor with the newly created bug.
	 */
	protected void openBugEditor() {

		IEditorInput input = null;
		try {
			input = new ExistingBugEditorInput(Integer.parseInt(id));
			BugzillaPlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().openEditor(
							input, IBugzillaConstants.EXISTING_BUG_EDITOR_ID, false);
		} catch (LoginException e) {
			// if we had an error with logging in, display an error
			MessageDialog.openError(null, "Posting Error",
			        "Bugzilla could not access and display your bug in the editor because your login name or password is incorrect."
			        + "\nPlease check your settings in the bugzilla preferences. ");
		} catch (PartInitException e) {
			// if there was a problem, handle it and log it, then get out of
			// here
			ExceptionHandler.handle(e, SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message);
			BugzillaPlugin.log(e.getStatus());
		} catch (IOException e) {
		    BugzillaPlugin.getDefault().logAndShowExceptionDetailsDialog(e, "occurred while opening the bug report.", "Bugzilla Error");
		}
	}

	/**
	 * Saves the bug report offline on the user's hard-drive. All offline bug
	 * reports are saved together in a single file in the plug-in's directory.
	 */
	abstract protected void saveBugOffline();

	/**
	 * Function to set the url to post the bug to.
	 * 
	 * @param form
	 *            A reference to a BugPost that the bug is going to be posted to
	 * @param formName
	 *            The form that we wish to use to submit the bug
	 */
	protected void setURL(BugPost form, String formName)
			throws MalformedURLException {

		String baseURL = BugzillaPlugin.getDefault().getServerName();
		if (!baseURL.endsWith("/"))
			baseURL += "/";
		form.setURL(baseURL + formName);

		// add the login information to the bug post
		form.add("Bugzilla_login", BugzillaPreferencePage.getUserName());
		form.add("Bugzilla_password", BugzillaPreferencePage.getPassword());
	}

	/**
	 * Format the description into lines of about 80 characters so that it is
	 * displayed properly in bugzilla, done automatically by Bugzilla 2.20
	 */
	protected void formatDescription() {
		String origDesc = model.getDescription();
		if (BugzillaPlugin.getDefault().isServerCompatability220()) {
			model.setDescription(origDesc);
		} else {
			String[] descArray = new String[(origDesc.length() / AbstractBugEditor.WRAP_LENGTH + 1) * 2];
			for (int i = 0; i < descArray.length; i++)
				descArray[i] = null;
			int j = 0;
			while (true) {
				int spaceIndex = origDesc.indexOf(" ", AbstractBugEditor.WRAP_LENGTH - 5);
				if (spaceIndex == origDesc.length() || spaceIndex == -1) {
					descArray[j] = origDesc;
					break;
				}
				descArray[j] = origDesc.substring(0, spaceIndex);
				origDesc = origDesc.substring(spaceIndex + 1, origDesc.length());
				j++;
			}

			String newDesc = "";

			for (int i = 0; i < descArray.length; i++) {
				if (descArray[i] == null)
					break;
				newDesc += descArray[i] + "\n";
			}
			model.setDescription(newDesc);
		}
	}
	
	public String getId() {
		return id;		
	}
	
	/**
	 * @return the last page of this wizard
	 */
	abstract protected AbstractWizardDataPage getWizardDataPage();
}
