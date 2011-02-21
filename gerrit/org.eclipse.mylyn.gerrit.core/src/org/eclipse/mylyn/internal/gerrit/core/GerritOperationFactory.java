/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.operations.AbandonRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.AddReviewersRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.PublishRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.RefreshConfigRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.RestoreRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.SubmitRequest;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class GerritOperationFactory {

	private final IRepositoryManager repositoryManager;

	public GerritOperationFactory(IRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	public GerritOperation createAbandonOperation(ITask review, AbandonRequest request) {
		return new GerritOperation("Abandoning Change", getClient(review), request);
	}

	public GerritOperation createAddReviewersOperation(ITask review, AddReviewersRequest request) {
		return new GerritOperation("Adding Reviewers", getClient(review), request);
	}

	public GerritOperation createPublishOperation(ITask review, PublishRequest request) {
		return new GerritOperation("Publishing Change", getClient(review), request);
	}

	public GerritOperation createRefreshConfigOperation(ITask review, RefreshConfigRequest request) {
		return new GerritOperation("Refreshing Configuration", getClient(review), request);
	}

	public GerritOperation createRestoreOperation(ITask review, RestoreRequest request) {
		return new GerritOperation("Restoring Change", getClient(review), request);
	}

	public GerritOperation createSubmitOperation(ITask review, SubmitRequest request) {
		return new GerritOperation("Submitting Change", getClient(review), request);
	}

	public GerritClient getClient(ITask review) {
		TaskRepository repository = repositoryManager.getRepository(review.getConnectorKind(),
				review.getRepositoryUrl());
		GerritConnector connector = (GerritConnector) repositoryManager.getRepositoryConnector(repository.getConnectorKind());
		GerritClient client = connector.getClient(repository);
		return client;
	}

}
