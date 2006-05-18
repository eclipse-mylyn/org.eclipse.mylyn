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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 * 
 * The first page of the new bug wizard where the user chooses the bug's product
 */
public class BugzillaProductPage extends AbstractWizardListPage {
  
	private static final String NEW_BUGZILLA_TASK_ERROR_TITLE = "New Bugzilla Task Error";

	private static final String DESCRIPTION = "Pick a product on which to enter a bug.\n"
			+ "Press the Update button if you do not see the desired product.";

	private static final String LABEL_UPDATE = "Update Products from Repository";

	/** The list of products to submit a bug report for */
	static List<String> products = null;

	/**
	 * Reference to the bug wizard which created this page so we can create the
	 * second page
	 */
	NewBugzillaReportWizard bugWizard;

	/**
	 * String to hold previous product; determines if attribute option values
	 * need to be updated
	 */
	private String prevProduct;

	private final TaskRepository repository;

	/**
	 * Constructor for BugzillaProductPage
	 * 
	 * @param workbench
	 *            The instance of the workbench
	 * @param bugWiz
	 *            The bug wizard which created this page
	 * @param repository
	 *            The repository the data is coming from
	 */
	public BugzillaProductPage(IWorkbench workbench, NewBugzillaReportWizard bugWiz, TaskRepository repository) {
		super("Page1", IBugzillaConstants.TITLE_NEW_BUG, DESCRIPTION, workbench);
		this.bugWizard = bugWiz;
		this.repository = repository;
		setImageDescriptor(BugzillaUiPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.bugzilla.ui",
				"icons/wizban/bug-wizard.gif"));
	}

	protected ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

	protected IPreferenceStore prefs = BugzillaUiPlugin.getDefault().getPreferenceStore();

	@Override
	public void createAdditionalControls(Composite parent) {
		Button updateButton = new Button(parent, SWT.LEFT | SWT.PUSH);
		updateButton.setText(LABEL_UPDATE);

		updateButton.setLayoutData(new GridData());

		updateButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				monitorDialog.open();
				IProgressMonitor monitor = monitorDialog.getProgressMonitor();
				monitor.beginTask("Updating search options...", 55);

				try {
					BugzillaUiPlugin.updateQueryOptions(repository, monitor);

					products = new ArrayList<String>();
					for (String product : BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_PRODUCT,
							null, repository.getUrl())) {
						products.add(product);
					}
					monitor.worked(1);
					populateList(false);
				} catch (LoginException exception) {
					// we had a problem that seems to have been caused from bad
					// login info
					MessageDialog
							.openError(
									null,
									"Login Error",
									"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
					BugzillaPlugin.log(exception);
				} catch (IOException exception) {
					MessageDialog.openError(null, "Connection Error",
							"\nPlease check your settings in the bugzilla preferences. ");
				} finally {
					monitor.done();
					monitorDialog.close();
				}
			}
		});
	}

	private void initProducts() {
		// try to get the list of products from the server
		if (!bugWizard.model.hasParsedProducts()) {
			String repositoryUrl = repository.getUrl();
			try {
				String[] storedProducts = BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_PRODUCT,
						null, repositoryUrl);
				if (storedProducts.length > 0) {
					products = Arrays.asList(storedProducts);
				} else {
					products = BugzillaRepositoryUtil.getProductList(repository.getUrl(), repository.getUserName(), repository.getPassword(), repository.getCharacterEncoding());
				}
				bugWizard.model.setConnected(true);
				bugWizard.model.setParsedProductsStatus(true);

			} catch (Exception e) {
				bugWizard.model.setConnected(false);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(Display.getDefault().getActiveShell(), NEW_BUGZILLA_TASK_ERROR_TITLE,
								"Unable to get products. Ensure proper repository configuration in "
										+ TaskRepositoriesView.NAME + ".");
					}
				});
			}
		}
	}

	/**
	 * Populates the listBox with all available products.
	 * 
	 * @param read
	 */
	@Override
	protected void populateList(boolean init) {
		if (init) {
			initProducts();
		}

		if (products != null) {
			listBox.removeAll();
			Iterator<String> itr = products.iterator();

			while (itr.hasNext()) {
				String prod = itr.next();
				listBox.add(prod);
			}
		}
		listBox.setFocus();
	}

	@Override
	public void handleEvent(Event event) {
		handleEventHelper(event, "You must select a product");
	}

	@Override
	public IWizardPage getNextPage() {
		// save the product information to the model
		saveDataToModel();
		NewBugzillaReportWizard wizard = (NewBugzillaReportWizard) getWizard();
		NewBugzillaReport model = wizard.model;

		// try to get the attributes from the bugzilla server
		try {
			if (!model.hasParsedAttributes() || !model.getProduct().equals(prevProduct)) {
				BugzillaRepositoryUtil.setupNewBugAttributes(repository.getUrl(), repository.getUserName(), repository.getPassword(), model, null);
				model.setParsedAttributesStatus(true);
			}

			if (prevProduct == null) {
				bugWizard.setAttributePage(new WizardAttributesPage(workbench));
				bugWizard.addPage(bugWizard.getAttributePage());
			} else {
				// selected product has changed
				// will createControl again with new attributes in model
				bugWizard.getAttributePage().setControl(null);
			}
			// }
		} catch (final Exception e) {
			e.printStackTrace();
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), NEW_BUGZILLA_TASK_ERROR_TITLE, e
							.getLocalizedMessage()
							+ " Ensure proper repository configuration in " + TaskRepositoriesView.NAME + ".");
				}
			});
			// MylarStatusHandler.fail(e, e.getLocalizedMessage()+" Ensure
			// proper repository configuration in
			// "+TaskRepositoriesView.NAME+".", true);
			// BugzillaPlugin.getDefault().logAndShowExceptionDetailsDialog(e,
			// "occurred.", "Bugzilla Error");
		}
		return super.getNextPage();
	}

	/**
	 * Save the currently selected product to the model when next is clicked
	 */
	private void saveDataToModel() {
		// Gets the model
		NewBugzillaReport model = bugWizard.model;

		prevProduct = model.getProduct();
		model.setProduct((listBox.getSelection())[0]);
	}
}
