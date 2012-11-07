/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.operations.AbandonRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.AddReviewersRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.PublishRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.RebaseRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.RefreshConfigRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.RestoreRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.SaveDraftRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.SubmitRequest;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ReviewerResult;
import com.google.gerrit.reviewdb.PatchLineComment;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class GerritOperationFactory {

	private final IRepositoryManager repositoryManager;

	public GerritOperationFactory(IRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	public GerritOperation<PatchLineComment> createSaveDraftOperation(ITask review, SaveDraftRequest request) {
		return new GerritOperation<PatchLineComment>("Saving Draft", getClient(review), request);
	}

	public GerritOperation<ChangeDetail> createAbandonOperation(ITask review, AbandonRequest request) {
		return new GerritOperation<ChangeDetail>("Abandoning Change", getClient(review), request);
	}

	public GerritOperation<ReviewerResult> createAddReviewersOperation(ITask review, AddReviewersRequest request) {
		return new GerritOperation<ReviewerResult>("Adding Reviewers", getClient(review), request);
	}

	public GerritOperation<Object> createPublishOperation(ITask review, PublishRequest request) {
		return new GerritOperation<Object>("Publishing Change", getClient(review), request);
	}

	public GerritOperation<GerritConfiguration> createRefreshConfigOperation(ITask review, RefreshConfigRequest request) {
		return new GerritOperation<GerritConfiguration>("Refreshing Configuration", getClient(review), request);
	}

	public GerritOperation<ChangeDetail> createRebaseOperation(ITask review, RebaseRequest request) {
		return new GerritOperation<ChangeDetail>("Rebasing Change", getClient(review), request);
	}

	public GerritOperation<ChangeDetail> createRestoreOperation(ITask review, RestoreRequest request) {
		return new GerritOperation<ChangeDetail>("Restoring Change", getClient(review), request);
	}

	public GerritOperation<ChangeDetail> createSubmitOperation(ITask review, SubmitRequest request) {
		return new GerritOperation<ChangeDetail>("Submitting Change", getClient(review), request);
	}

	public GerritClient getClient(ITask review) {
		TaskRepository repository = repositoryManager.getRepository(review.getConnectorKind(),
				review.getRepositoryUrl());
		GerritConnector connector = (GerritConnector) repositoryManager.getRepositoryConnector(repository.getConnectorKind());
		GerritClient client = connector.getClient(repository);
		return client;
	}

}
