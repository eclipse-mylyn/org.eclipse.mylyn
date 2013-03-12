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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactory;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

/**
 * @author Miles Parker
 */
public class ReviewUiFactoryProvider extends AbstractUiFactoryProvider<IReview> {

	@Override
	public List<AbstractUiFactory<IReview>> createFactories(IUiContext context, IReview set) {
		List<AbstractUiFactory<IReview>> factories = new ArrayList<AbstractUiFactory<IReview>>();
		factories.add(new AddReviewersUiFactory(context, set));
		return factories;
	}
}
