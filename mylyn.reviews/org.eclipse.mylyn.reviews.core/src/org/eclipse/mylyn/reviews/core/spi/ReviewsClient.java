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
