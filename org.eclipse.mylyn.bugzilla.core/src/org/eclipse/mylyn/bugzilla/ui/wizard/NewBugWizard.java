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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.ui.OfflineView;


/**
 * The main wizard class for creating a new bug
 * 
 * @author Shawn Minto
 */
public class NewBugWizard extends AbstractBugWizard {

	/** The wizard page for where the product is selected */
	WizardProductPage productPage;
	
	/** The wizard page where the attributes are selected and the bug is submitted */
	WizardAttributesPage attributePage;

	@Override
	protected void addPagesHelper() throws Exception {
		// add only the product page for now if there are any products

		// try to get the list of products from the server
		if (!model.hasParsedProducts()) {
			try {
				WizardProductPage.products = BugzillaRepository.getInstance()
						.getProductList();
				model.setConnected(true);
				model.setParsedProductsStatus(true);
			} catch (Exception e) {
				// determine if exception was problem connecting, or if
				// something else
				// if problem connecting, then set flag and use
				// ProductConfiguration
				// otherwise throw the exception to be dealt with

				// problem connecting to Bugzilla server, so unable to get
				// Products from product page
				model.setConnected(false);

				if (e instanceof IOException) {
					// Dialog to inform user that the program could not connect
					// to the Bugzilla server
					MessageDialog
							.openError(
									null,
									"Bugzilla Connect Error",
									"Unable to connect to Bugzilla server.\n"
											+ "Product configuration will be read from the workspace.");

					// use ProductConfiguration to get products instead
					String[] products = BugzillaPlugin.getDefault()
							.getProductConfiguration().getProducts();

					// add products from ProductConfiguration to product page's
					// product list
					List<String> productList = new ArrayList<String>();
					for (int i = 0; i < products.length; i++)
						productList.add(i, products[i]);
					WizardProductPage.products = productList;
					model.setParsedProductsStatus(true);
				} else
					throw e;
			}
		}

		if (WizardProductPage.products.size() != 0
				&& BugzillaPlugin.getDefault().getProductConfiguration()
						.getProducts().length > 1) {
			productPage = new WizardProductPage(workbenchInstance, this);
			addPage(productPage);
		} else {
			// There wasn't a list of products so there must only be 1
			if (!model.hasParsedAttributes()) {
				if (model.isConnected())
					BugzillaRepository.getInstance().getnewBugAttributes(model,
							true);
				else
					BugzillaRepository.getInstance().getProdConfigAttributes(
							model);
				model.setParsedAttributesStatus(true);
			}

			// add the attributes page to the wizard
			attributePage = new WizardAttributesPage(workbenchInstance);
			addPage(attributePage);
		}
	}

	@Override
	public boolean canFinish() {
		return super.canFinishHelper(productPage);
	}

	@Override
	protected void saveBugOffline() {
		// Since the bug report is new, it just needs to be added to the
		// existing list of reports in the offline file.
		OfflineView.saveOffline(model);
	}

	@Override
	protected AbstractWizardDataPage getWizardDataPage() {
		return attributePage;
	}
}
