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
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaPreferences;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.NewBugModel;
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

	/** The list of products to submit a bug report for */
	static List<String> products = null;

	/** Reference to the bug wizard which created this page so we can create the second page */ 
	NewBugWizard bugWizard;

	/** String to hold previous product; determines if attribute option values need to be updated */
	private String prevProduct;

	/**
	 * Constructor for WizardProductPage
	 * 
	 * @param workbench
	 *            The instance of the workbench
	 * @param bugWiz
	 *            The bug wizard which created this page
	 */
	public WizardProductPage(IWorkbench workbench, NewBugWizard bugWiz) {
		super("Page1", "New Bug Report",
				"Pick a product on which to enter a bug.", workbench);
		this.bugWizard = bugWiz;
	}

	protected ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(BugzillaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
	protected IPreferenceStore prefs = BugzillaPlugin.getDefault().getPreferenceStore();
	
	@Override
	public void createAdditionalControls(Composite parent){
		Button updateButton = new Button(parent, SWT.LEFT | SWT.PUSH);
		updateButton.setText("Update");
			
		updateButton.setLayoutData(new GridData());
		
		updateButton.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				
				monitorDialog.open();
				IProgressMonitor monitor = monitorDialog.getProgressMonitor();
				monitor.beginTask("Updating search options...", 55);

				try {
					BugzillaPreferences.updateQueryOptions(monitor);
					
					products = new ArrayList<String>();
					for(String product : BugzillaPreferences.queryOptionsToArray(prefs.getString(IBugzillaConstants.PRODUCT_VALUES))){
						products.add(product);
					}
					monitor.worked(1);
				}
				catch (LoginException exception) {
					// we had a problem that seems to have been caused from bad login info
					MessageDialog.openError(null, "Login Error", "Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
					BugzillaPlugin.log(exception);
				}
				finally {
					monitor.done();
					monitorDialog.close();
				}
			}
		});
	}
	
	/**
	 * Populates the listBox with all available products.
	 */
	@Override
	protected void populateList() {
		listBox.removeAll();
		Iterator<String> itr = products.iterator();

		while (itr.hasNext()) {
			String prod = itr.next();
			listBox.add(prod);
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
		NewBugWizard wizard = (NewBugWizard) getWizard();
		NewBugModel model = wizard.model;

		// try to get the attributes from the bugzilla server
		try	{
			if (!model.hasParsedAttributes() || !prevProduct.equals(model.getProduct())) {
				if (model.isConnected()) { 
					BugzillaRepository.getInstance().getnewBugAttributes(model, false);
				}
	 			else {
	 				BugzillaRepository.getInstance().getProdConfigAttributes(model);
	 			}
				model.setParsedAttributesStatus(true);
				if (prevProduct == null) {
					bugWizard.attributePage = new WizardAttributesPage(workbench);
					bugWizard.addPage(bugWizard.attributePage);
				}
				else {
					// selected product has changed
					bugWizard.attributePage.setControl(null);   // will createControl again with new attributes in model
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

