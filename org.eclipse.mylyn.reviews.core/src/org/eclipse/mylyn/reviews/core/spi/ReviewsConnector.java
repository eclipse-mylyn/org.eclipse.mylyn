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

package org.eclipse.mylyn.reviews.core.spi;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public abstract class ReviewsConnector extends AbstractRepositoryConnector {

	Map<TaskRepository, AbstractRemoteEmfFactoryProvider<IRepository, IReview>> factoryForRepository = new HashMap<TaskRepository, AbstractRemoteEmfFactoryProvider<IRepository, IReview>>();

	public abstract AbstractRemoteEmfFactoryProvider<IRepository, IReview> createFactoryProvider(
			TaskRepository repository);

	public AbstractRemoteEmfFactoryProvider<IRepository, IReview> getFactoryProvider(TaskRepository repository) {
		AbstractRemoteEmfFactoryProvider<IRepository, IReview> factoryProvider = factoryForRepository.get(repository);
		if (factoryProvider == null) {
			factoryProvider = createFactoryProvider(repository);
			factoryForRepository.put(repository, factoryProvider);
		}
		return factoryProvider;
	}

	public void close() {
		for (AbstractRemoteEmfFactoryProvider<IRepository, IReview> provider : factoryForRepository.values()) {
			provider.close();
		}
	}
}
