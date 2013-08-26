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

import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public abstract class ReviewsClient {

	private final TaskRepository repository;

	AbstractRemoteEmfFactoryProvider<IRepository, IReview> factoryProvider;

	public ReviewsClient(TaskRepository repository) {
		this.repository = repository;
	}

	public AbstractRemoteEmfFactoryProvider<IRepository, IReview> getFactoryProvider() {
		if (factoryProvider == null) {
			factoryProvider = createFactoryProvider();
		}
		return factoryProvider;
	}

	public abstract AbstractRemoteEmfFactoryProvider<IRepository, IReview> createFactoryProvider();

	public TaskRepository getRepository() {
		return repository;
	}
}
