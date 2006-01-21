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

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.ITaskRepositoryClient;

/**
 * @author Mik Kersten
 */
public class NewQuerySelectRepositoryPage extends SelectRepositoryPage {

	private NewQueryWizard newQueryWizard;
	
	public NewQuerySelectRepositoryPage(NewQueryWizard newQueryWizard) {
		super(newQueryWizard);
		this.newQueryWizard = newQueryWizard;
	}
	
	@Override
	public IWizardPage getNextPage() {
		if (isPageComplete()) {
			ITaskRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(wizard.repository.getKind());
			AbstractNewQueryPage nextPage = client.getQueryPage(wizard.repository);
			newQueryWizard.setQueryPage(nextPage);
			nextPage.setWizard(newQueryWizard);
			return nextPage;
		} else {
			return super.getNextPage();
		}
	}
}
