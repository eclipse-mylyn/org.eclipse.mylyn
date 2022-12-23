/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson, Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import org.eclipse.egit.ui.internal.fetch.FetchGerritChangeWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.gerrit.core.egit.GerritToGitMapping;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class FetchUiFactory extends AbstractPatchSetUiFactory {

	public FetchUiFactory(IUiContext context, IReviewItemSet set) {
		super(Messages.FetchUiFactory_Fetch, context, set);
	}

	@Override
	public void execute() {
		GerritToGitMapping mapping = getGitRepository(true);
		if (mapping != null) {
			String refName = getPatchSetDetail().getPatchSet().getRefName();
			FetchGerritChangeWizard wizard = new FetchGerritChangeWizard(mapping.getRepository(), refName);
			WizardDialog wizardDialog = new WizardDialog(getShell(), wizard);
			wizardDialog.setHelpAvailable(false);
			wizardDialog.open();
		}
	}

	@Override
	public boolean isExecutable() {
		return true;
	}
}
