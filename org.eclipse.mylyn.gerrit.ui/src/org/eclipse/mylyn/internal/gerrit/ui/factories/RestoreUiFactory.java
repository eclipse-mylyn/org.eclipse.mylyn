/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson, Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
