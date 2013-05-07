/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jan Lohre (SAP) - improvements
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import org.eclipse.mylyn.internal.gerrit.ui.factories.ReviewUiFactoryProvider;
import org.eclipse.mylyn.reviews.core.model.IReview;
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
	protected boolean canAddReviewers() {
		return getReview().getState() == null || getReview().getState() == ReviewStatus.NEW;
	}
}
