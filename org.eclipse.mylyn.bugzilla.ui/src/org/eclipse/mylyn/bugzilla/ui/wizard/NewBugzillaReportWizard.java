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
import org.eclipse.mylar.internal.tasklist.ui.wizards.SelectRepositoryPage;

/**
 * @author Mik Kersten
 */
public class NewBugzillaReportWizard extends AbstractBugWizard {

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
