/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.RemoteFactoryProviderConfigurer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.factories.ReviewsUiDataLocator;
import org.eclipse.mylyn.reviews.ui.spi.remote.RemoteUiService;

public class RemoteUiFactoryProviderConfigurer extends RemoteFactoryProviderConfigurer {
	@Override
	public synchronized void configure(AbstractRemoteEmfFactoryProvider<IRepository, IReview> provider) {
		if (!(provider.getService() instanceof RemoteUiService)) {
			provider.setService(new RemoteUiService());
		}
		if (!(provider.getDataLocator() instanceof ReviewsUiDataLocator)) {
			provider.setDataLocator(new ReviewsUiDataLocator());
		}
		provider.open();
	}
}
