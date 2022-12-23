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

import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.ui.operations.RestoreDialog;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class RestoreUiFactory extends AbstractPatchSetUiFactory {

	public RestoreUiFactory(IUiContext context, IReviewItemSet set) {
		super(Messages.RestoreUiFactory_Restore, context, set);
	}

	@Override
	public void execute() {
		new RestoreDialog(getShell(), getTask(), getPatchSetDetail().getPatchSet()).open(getEditor());
	}

	@Override
	public boolean isExecutable() {
		if (isAnonymous()) {
			return false;
		}
		ChangeDetailX changeDetail = getChange().getChangeDetail();
		return changeDetail != null && changeDetail.isCurrentPatchSet(getPatchSetDetail()) && changeDetail.canRestore();
	}
}
