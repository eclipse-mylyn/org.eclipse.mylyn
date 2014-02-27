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

import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.ui.operations.RebaseDialog;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class RebaseUiFactory extends AbstractPatchSetUiFactory {

	public RebaseUiFactory(IUiContext context, IReviewItemSet set) {
		super(Messages.RebaseUiFactory_Rebase, context, set);
	}

	@Override
	public void execute() {
		new RebaseDialog(getShell(), getTask(), getPatchSetDetail().getPatchSet()).open(getEditor());
	}

	@Override
	public boolean isExecutable() {
		if (isAnonymous()) {
			return false;
		}
		GerritChange change = getChange();
		return change != null && change.getChangeDetail() != null && change.getChangeDetail().canRebase();
	}
}
