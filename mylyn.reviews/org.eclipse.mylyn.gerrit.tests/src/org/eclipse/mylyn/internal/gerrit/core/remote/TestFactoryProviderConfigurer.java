/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
