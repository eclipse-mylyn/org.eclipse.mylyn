/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.reviews.core.spi.remote.RemoteFactoryProviderConfigurer;
import org.eclipse.mylyn.reviews.internal.core.TaskReviewsMappingsStore;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public abstract class ReviewsConnector extends AbstractRepositoryConnector {

	Map<TaskRepository, ReviewsClient> clientForRepository = new HashMap<TaskRepository, ReviewsClient>();

	private RemoteFactoryProviderConfigurer factoryProviderConfigurer = new RemoteFactoryProviderConfigurer();

	public final ReviewsClient getReviewClient(TaskRepository repository) {
		ReviewsClient client = clientForRepository.get(repository);
		if (client == null) {
			client = createReviewClient(repository, true);
			factoryProviderConfigurer.configure(client.getFactoryProvider());
			clientForRepository.put(repository, client);
		}
		return client;
	}

	/**
	 * Returns the behavior for {@code repository}.
	 * 
	 * @param repository
	 *            the repository
	 * @param b
	 *            always true
	 * @return the {@link ReviewsClient} instance
	 */
	protected abstract ReviewsClient createReviewClient(TaskRepository repository, boolean b);

	public void setFactoryProviderConfigurer(RemoteFactoryProviderConfigurer factoryProviderConfigurer) {
		this.factoryProviderConfigurer = factoryProviderConfigurer;
	}

	public RemoteFactoryProviderConfigurer getFactoryProviderConfigurer() {
		return factoryProviderConfigurer;
	}

	/**
	 * Subclasses should call super to update the task review mapping.
	 */
	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		if (TaskReviewsMappingsStore.getInstance() != null) {
			TaskReviewsMappingsStore.getInstance().addTaskAssocation(task, taskData);
		}
	}
}
