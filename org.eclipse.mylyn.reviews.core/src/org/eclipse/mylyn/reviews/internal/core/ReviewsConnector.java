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

package org.eclipse.mylyn.reviews.internal.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteFactoryProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public abstract class ReviewsConnector extends AbstractRepositoryConnector {

	Map<TaskRepository, AbstractRemoteFactoryProvider> factoryForRepository = new HashMap<TaskRepository, AbstractRemoteFactoryProvider>();

	public abstract AbstractRemoteFactoryProvider createFactoryProvider(TaskRepository repository);

	public AbstractRemoteFactoryProvider getFactoryProvider(TaskRepository repository) {
		AbstractRemoteFactoryProvider factoryProvider = factoryForRepository.get(repository);
		if (factoryProvider == null) {
			factoryProvider = createFactoryProvider(repository);
			factoryForRepository.put(repository, factoryProvider);
		}
		return factoryProvider;
	}

}
