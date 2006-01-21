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

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class NewQueryWizard extends AbstractRepositoryWizard {

	private AbstractNewQueryPage queryPage;
	
	public NewQueryWizard() {
		super();
		super.setSelectRepositoryPage(new NewQuerySelectRepositoryPage(this));
		init();
	}

	@Override
	public boolean performFinish() {
		if (canFinish()) {
			queryPage.addQuery();
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private void init() {
		super.setForcePreviousAndNextButtons(true);
	}

	@Override
	public void addPages() {
		super.addPages();
	}
	
	public void setQueryPage(AbstractNewQueryPage queryPage) {
		this.queryPage = queryPage;
	}
	
	@Override
	public boolean canFinish() {
		return super.canFinish() && queryPage != null && queryPage.isPageComplete();
	}
}
