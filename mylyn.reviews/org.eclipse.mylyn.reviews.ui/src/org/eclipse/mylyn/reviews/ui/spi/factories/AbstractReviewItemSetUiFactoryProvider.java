/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.factories;

import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

/**
 * Provides additional specific factories for supporting opening of review set items.
 *
 * @author Miles Parker
 */
public abstract class AbstractReviewItemSetUiFactoryProvider extends AbstractUiFactoryProvider<IReviewItemSet> {

	public abstract AbstractUiFactory<IReviewItemSet> getOpenFileFactory(IUiContext context, IReviewItemSet set,
			IFileItem item);

	public abstract AbstractUiFactory<IReviewItemSet> getOpenFileToCommentFactory(IUiContext context,
			IReviewItemSet set, IFileItem item, IComment comment);

	public abstract AbstractUiFactory<IReviewItemSet> getOpenCommitFactory(IUiContext context, IReviewItemSet set);

	public abstract AbstractUiFactory<IReviewItemSet> getOpenParentCommitFactory(IUiContext context, IReviewItemSet set,
			String commitId);

}
