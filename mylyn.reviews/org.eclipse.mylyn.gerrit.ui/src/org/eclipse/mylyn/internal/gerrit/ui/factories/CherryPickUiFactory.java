/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
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

import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.ui.operations.CherryPickDialog;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

public class CherryPickUiFactory extends AbstractPatchSetUiFactory {

	public CherryPickUiFactory(IUiContext context, IReviewItemSet set) {
		super(Messages.CherryPickUiFactory_Cherry_Pick, context, set);
	}

	@Override
	public boolean isExecutable() {
		if (isAnonymous()) {
			return false;
		}
		GerritChange change = getChange();
		return change != null && change.getChangeDetail() != null && change.getChangeDetail().canCherryPick();
	}

	@Override
	public void execute() {
		new CherryPickDialog(getShell(), getTask(), getPatchSetDetail().getPatchSet(), getChange()).open(getEditor());
	}

}
