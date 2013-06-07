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

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.RemoteFactoryProviderConfigurer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.reviews.spi.edit.remote.AbstractRemoteEditFactoryProvider;

public class TestFactoryProviderConfigurer extends RemoteFactoryProviderConfigurer {
	@Override
	public synchronized void configure(AbstractRemoteEmfFactoryProvider<IRepository, IReview> provider) {
		provider.setDataLocator(new TestDataLocator());
		provider.setService(new TestRemoteService());
		((AbstractRemoteEditFactoryProvider) provider).deleteCache();
		provider.open();
	}
}
