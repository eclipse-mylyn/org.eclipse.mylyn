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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylar.bugzilla.ui.OfflineView;
import org.eclipse.mylar.tasklist.ui.wizards.SelectRepositoryPage;

/**
 * @author Mik Kersten
 */
public class NewBugzillaReportWizard extends AbstractBugWizard {

	/** The wizard page for where the product is selected */
//	private BugzillaProductPage productPage;

	/**
	 * The wizard page where the attributes are selected and the bug is
	 * submitted
	 */
	private WizardAttributesPage attributePage;

	public NewBugzillaReportWizard() {
		this(false);
	}

	public NewBugzillaReportWizard(boolean fromDialog) {
		super();
		this.fromDialog = fromDialog;
	}

	@Override
	public void addPages() {
		super.addPages();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof SelectRepositoryPage) {
			addPage(new BugzillaProductPage(workbenchInstance, this));
		}
		return super.getNextPage(page);
	}

	@Override
	public boolean canFinish() {
		return attributeCompleted;
	}

	@Override
	protected void saveBugOffline() {
		OfflineView.saveOffline(model, true);
	}

	@Override
	protected AbstractWizardDataPage getWizardDataPage() {
		return attributePage;
	}

	public WizardAttributesPage getAttributePage() {
		return attributePage;
	}

	public void setAttributePage(WizardAttributesPage attributePage) {
		this.attributePage = attributePage;
	}
}

// @Override
// protected void addPagesHelper() throws Exception {
// // try to get the list of products from the server
// if (!model.hasParsedProducts()) {
// try {
// BugzillaProductPage.products =
// BugzillaRepositoryUtil.getProductList(repository.getServerUrl()
// .toExternalForm());
// model.setConnected(true);
// model.setParsedProductsStatus(true);
// } catch (Exception e) {
// model.setConnected(false);
//
// if (e instanceof IOException) {
// MessageDialog.openError(null, "Bugzilla Connect Error", "Unable to connect to
// Bugzilla server.\n"
// + "Product configuration will be read from the workspace.");
//
// // use ProductConfiguration to get products instead
// String[] products = BugzillaPlugin.getDefault().getProductConfiguration(
// repository.getServerUrl().toExternalForm()).getProducts();
//
// // add products from ProductConfiguration to product page's
// // product list
// List<String> productList = new ArrayList<String>();
// for (int i = 0; i < products.length; i++)
// productList.add(i, products[i]);
// BugzillaProductPage.products = productList;
// model.setParsedProductsStatus(true);
// } else {
// throw e;
// }
// }
// }
//
// try {
// if (BugzillaProductPage.products != null && BugzillaProductPage.products.size() >
// 0) {
// productPage = new BugzillaProductPage(workbenchInstance, this);
// addPage(productPage);
// }
// } catch (NullPointerException e) {
// throw new CoreException(new Status(IStatus.ERROR,
// "org.eclipse.mylar.bugzilla.core", IStatus.OK,
// "Unable to get products, possibly due to Bugzilla version incompatability",
// e));
// }
// }
