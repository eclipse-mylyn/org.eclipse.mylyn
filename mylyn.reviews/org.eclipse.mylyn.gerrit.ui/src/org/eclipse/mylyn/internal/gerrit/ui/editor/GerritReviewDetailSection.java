/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Jan Lohre (SAP) - improvements
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import org.eclipse.mylyn.internal.gerrit.ui.factories.GerritReviewerUiFactoryProvider;
import org.eclipse.mylyn.internal.gerrit.ui.factories.ReviewUiFactoryProvider;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;
import org.eclipse.mylyn.reviews.ui.spi.editor.ReviewDetailSection;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactoryProvider;

/**
 * Displays basic information about a given review corresponding to top sections of Gerrit web interface.
 *
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class GerritReviewDetailSection extends ReviewDetailSection {

	@Override
	protected AbstractUiFactoryProvider<IReview> getUiFactoryProvider() {
		return new ReviewUiFactoryProvider();
	}

	@Override
	protected AbstractUiFactoryProvider<IUser> getReviewerUiFactoryProvider() {
		return new GerritReviewerUiFactoryProvider();
	}

	@Override
	protected boolean canAddReviewers() {
		return getReview().getState() == ReviewStatus.DRAFT || getReview().getState() == ReviewStatus.NEW;
	}

}
