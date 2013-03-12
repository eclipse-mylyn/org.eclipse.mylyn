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

import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractReviewItemSetUiFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactory;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;

/**
 * @author Miles Parker
 */
public class PatchSetUiFactoryProvider extends AbstractReviewItemSetUiFactoryProvider {

	@Override
	public List<AbstractUiFactory<IReviewItemSet>> createFactories(IUiContext context, IReviewItemSet set) {
		List<AbstractUiFactory<IReviewItemSet>> factories = new ArrayList<AbstractUiFactory<IReviewItemSet>>();
		factories.add(new PublishUiFactory(context, set));
		factories.add(new FetchUiFactory(context, set));
		factories.add(new CompareWithUiFactory(context, set));
		factories.add(new RebaseUiFactory(context, set));
		factories.add(new SubmitUiFactory(context, set));
		factories.add(new AbandonUiFactory(context, set));
		factories.add(new RestoreUiFactory(context, set));
		return factories;
	}

	@Override
	public AbstractUiFactory<IReviewItemSet> getOpenCommitFactory(IUiContext context, IReviewItemSet set) {
		return new OpenCommitUiFactory(context, set);
	}

	@Override
	public AbstractUiFactory<IReviewItemSet> getOpenFileFactory(IUiContext context, IReviewItemSet set, IFileItem item) {
		return new OpenFileUiFactory(context, set, item);
	}
}
