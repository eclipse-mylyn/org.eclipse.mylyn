/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.factories;

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

	public abstract AbstractUiFactory<IReviewItemSet> getOpenCommitFactory(IUiContext context, IReviewItemSet set);

	public abstract AbstractUiFactory<IReviewItemSet> getOpenParentCommitFactory(IUiContext context,
			IReviewItemSet set, String commitId);

}
