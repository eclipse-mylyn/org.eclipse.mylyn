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

import org.eclipse.mylyn.internal.gerrit.ui.operations.AddReviewersDialog;
import org.eclipse.mylyn.internal.reviews.ui.ReviewUiUtil;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactory;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class AddReviewersUiFactory extends AbstractUiFactory<IReview> {

	public AddReviewersUiFactory(IUiContext context, IReview review) {
		super(Messages.AddReviewersUiFactory_Add_Reviewers, context, review);
	}

	@Override
	public void execute() {
		new AddReviewersDialog(getShell(), getTask()).open(getEditor());
	}

	@Override
	protected boolean isExecutableStateKnown() {
		return true;
	}

	public boolean isAnonymous() {
		return getModelObject() != null && getModelObject().getRepository() != null
				&& getModelObject().getRepository().getAccount() == null;
	}

	@Override
	public boolean isExecutable() {
		return !ReviewUiUtil.isAnonymous(getModelObject());
	}
}
