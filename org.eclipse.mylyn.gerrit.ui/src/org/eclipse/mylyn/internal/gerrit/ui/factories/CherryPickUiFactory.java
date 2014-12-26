/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
