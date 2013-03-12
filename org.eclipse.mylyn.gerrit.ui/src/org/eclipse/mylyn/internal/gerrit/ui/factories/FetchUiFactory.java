/*******************************************************************************
 * Copyright (c) 2013 Ericsson, Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
		super("Fetch...", context, set);
	}

	@Override
	public void execute() {
		GerritToGitMapping mapping = getGitRepository();
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
