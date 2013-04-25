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

import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritRemoteFactoryProvider;
import org.eclipse.mylyn.internal.gerrit.ui.operations.AddReviewersDialog;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactory;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.Change.Status;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class AddReviewersUiFactory extends AbstractUiFactory<IReview> {

	public AddReviewersUiFactory(IUiContext context, IReview review) {
		super("Add Reviewers...", context, review);
	}

	@Override
	public void execute() {
		new AddReviewersDialog(getShell(), getTask()).open(getEditor());
	}

	protected GerritChange getChange() {
		return ((GerritRemoteFactoryProvider) getFactoryProvider()).getReviewFactory()
				.getConsumerForModel(getModelObject().getRepository(), getModelObject())
				.getRemoteObject();
	}

	@Override
	public boolean isExecutable() {
		Status status = getChange().getChangeDetail().getChange().getStatus();
		return status == Change.Status.NEW || GerritUtil.isDraft(status);
	}
}
