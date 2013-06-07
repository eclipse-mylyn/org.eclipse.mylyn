/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.spi.edit.remote.review;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.edit.ReviewsEditPluginActivator;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;
import org.eclipse.mylyn.reviews.spi.edit.remote.AbstractRemoteEditFactoryProvider;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Supports decoupling of Reviews from remote API as well as job management.
 * 
 * @author Miles Parker
 */
public abstract class ReviewsRemoteEditFactoryProvider extends AbstractRemoteEditFactoryProvider<IRepository, IReview>
		implements IReviewRemoteFactoryProvider {

	private final TaskRepository taskRepository;

	public ReviewsRemoteEditFactoryProvider(TaskRepository repository) {
		super((EFactory) IReviewsFactory.INSTANCE, ReviewsPackage.Literals.REPOSITORY__REVIEWS,
				ReviewsPackage.Literals.CHANGE__ID, ReviewsPackage.Literals.REVIEW);
		this.taskRepository = repository;
	}

	@Override
	public IRepository open() {
		IRepository modelRepository = super.open();
		modelRepository.setTaskRepository(taskRepository);
		modelRepository.setTaskRepositoryUrl(taskRepository.getUrl());
		modelRepository.setTaskConnectorKind(taskRepository.getConnectorKind());
		return modelRepository;
	}

	@Override
	public String getContainerSegment() {
		try {
			return taskRepository.getConnectorKind() + "-" + asFileName(taskRepository.getUrl());
		} catch (MalformedURLException e) {
			RepositoryStatus.createStatus(taskRepository, IStatus.ERROR, ReviewsEditPluginActivator.PLUGIN_ID,
					"Bad repository url: " + taskRepository.getUrl());
			return "BadRepository";
		}
	}

	public static String asFileName(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		return url.getProtocol() + "-" + url.getHost() + "-" + url.getPath().replaceAll("/", "-");
	}

	@Override
	public String getFileExtension(EClass eClass) {
		return "reviews";
	}
}
