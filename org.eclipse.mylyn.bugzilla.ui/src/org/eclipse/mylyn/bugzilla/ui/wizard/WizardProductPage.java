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
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.NewBugModel;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbench;

/**
 * @author Shawn Minto
 * 
 * The first page of the new bug wizard where the user chooses the bug's product
 */
public class WizardProductPage extends AbstractWizardListPage {

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

	/**
	 * Constructor for WizardProductPage
	 * 
	 * @param workbench
	 *            The instance of the workbench
	 * @param bugWiz
	 *            The bug wizard which created this page
	 */
	public WizardProductPage(IWorkbench workbench, NewBugzillaReportWizard bugWiz) {
		super("Page1", "New Bug Report", DESCRIPTION, workbench);
		this.bugWizard = bugWiz;
	}

	protected ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(BugzillaPlugin.getDefault()
			.getWorkbench().getActiveWorkbenchWindow().getShell());

	protected IPreferenceStore prefs = BugzillaPlugin.getDefault().getPreferenceStore();

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
					BugzillaRepositoryUtil.updateQueryOptions(bugWizard.getRepository(), monitor);

					products = new ArrayList<String>();
					for (String product : BugzillaRepositoryUtil.getQueryOptions(IBugzillaConstants.VALUES_PRODUCT,
							bugWizard.getRepository().getUrl().toExternalForm())) {
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
			String repositoryUrl = bugWizard.getRepository().getUrl().toExternalForm();
			try {
//				ProductConfiguration productConfiguration = BugzillaPlugin.getDefault().getProductConfiguration(repositoryUrl);
//				System.err.println(">>>>> stored config: " + productConfiguration);
				String[] storedProducts = BugzillaRepositoryUtil.getQueryOptions(IBugzillaConstants.VALUES_PRODUCT,
						repositoryUrl);
				if (storedProducts.length > 0) {
//					String[] storedProducts = BugzillaPlugin.getDefault().getProductConfiguration(repositoryUrl).getProducts();
					products = Arrays.asList(storedProducts);
				} else {
					products = BugzillaRepositoryUtil.getProductList(repositoryUrl);
				}
//				bugWizard.model.setConnected(true);
				bugWizard.model.setParsedProductsStatus(true);
//			} catch (IOException e) {
//				bugWizard.model.setConnected(false);
//
//				if (e instanceof IOException) {
//					MessageDialog.openError(null, "Bugzilla Connect Error", "Unable to connect to Bugzilla server.\n"
//							+ "Product configuration will be read from the workspace.");
//
//					products = Arrays.asList(BugzillaPlugin.getDefault().getProductConfiguration(repositoryUrl).getProducts());
//					bugWizard.model.setParsedProductsStatus(true);
//				} 
			} catch (Exception e) {
				bugWizard.model.setConnected(false);
				MylarStatusHandler.fail(e, "Unable to get products, possibly due to Bugzilla version incompatability", true);
			}
		}
	}

	/**
	 * Populates the listBox with all available products.
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
		NewBugModel model = wizard.model;

		// try to get the attributes from the bugzilla server
		try {
			if (!model.hasParsedAttributes() || !prevProduct.equals(model.getProduct())) {
				String serverUrl = bugWizard.getRepository().getUrl().toExternalForm();
				if (model.isConnected()) {
					BugzillaRepositoryUtil.setupNewBugAttributes(serverUrl, model, false);
				} else {
					BugzillaRepositoryUtil.setupProdConfigAttributes(serverUrl, model);
				}
				model.setParsedAttributesStatus(true);
				if (prevProduct == null) {
					bugWizard.attributePage = new WizardAttributesPage(workbench);
					bugWizard.addPage(bugWizard.attributePage);
				} else {
					// selected product has changed
					// will createControl again with new attributes in model
					bugWizard.attributePage.setControl(null);
				}
			}
		} catch (Exception e) {
			BugzillaPlugin.getDefault().logAndShowExceptionDetailsDialog(e, "occurred.", "Bugzilla Error");
		}
		return super.getNextPage();
	}

	/**
	 * Save the currently selected product to the model when next is clicked
	 */
	private void saveDataToModel() {
		// Gets the model
		NewBugModel model = bugWizard.model;

		prevProduct = model.getProduct();
		model.setProduct((listBox.getSelection())[0]);
	}

	@Override
	public String getTableName() {
		return "Product:";
	}
}
