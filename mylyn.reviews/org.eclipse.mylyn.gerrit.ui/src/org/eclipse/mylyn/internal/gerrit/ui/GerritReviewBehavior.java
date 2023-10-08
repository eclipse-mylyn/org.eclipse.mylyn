/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.internal.gerrit.core.GerritOperationFactory;
import org.eclipse.mylyn.internal.gerrit.core.Messages;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommentInput;
import org.eclipse.mylyn.internal.gerrit.core.operations.DiscardDraftRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.SaveDraftRequest;
import org.eclipse.mylyn.internal.gerrit.ui.egit.GitFileRevisionUtils;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.history.IFileRevision;

import com.google.gerrit.reviewdb.Patch;
import com.google.gwtjsonrpc.client.VoidResult;

/**
 * @author Steffen Pingel
 * @author Guy Perron
 */
public class GerritReviewBehavior extends ReviewBehavior {

	private Repository repository = null;

	private final short LEFT_SIDE = 0;

	private final short RIGHT_SIDE = 1;

	private final String BASE = "base-"; //$NON-NLS-1$

	public GerritReviewBehavior(ITask task) {
		super(task);
	}

	public GerritReviewBehavior(ITask task, Repository repository) {
		super(task);
		this.repository = repository;
	}

	public GerritOperationFactory getOperationFactory() {
		return GerritUiPlugin.getDefault().getOperationFactory();
	}

	@Override
	public IStatus addComment(IReviewItem item, IComment comment, IProgressMonitor monitor) {
		short side = RIGHT_SIDE;
		String id = item.getId();
		if (id.startsWith(BASE)) {
			// base revision
			id = id.substring(BASE.length());
			side = LEFT_SIDE;
		}
		Patch.Key key = Patch.Key.parse(id);
		for (ILocation location : comment.getLocations()) {
			if (location instanceof ILineLocation) {
				ILineLocation lineLocation = (ILineLocation) location;
				SaveDraftRequest request = new SaveDraftRequest(key, lineLocation.getRangeMin(), side, null,
						StringUtils.defaultIfEmpty(comment.getId(), null));
				request.setMessage(comment.getDescription());

				GerritOperation<CommentInput> operation = getOperationFactory().createOperation(getTask(), request);
				IStatus status = operation.run(monitor);

				CommentInput commentInput = operation.getOperationResult();
				// save the value of uuid, and keep it with the comment
				if (commentInput != null) {
					comment.setId(commentInput.getId());
				}
				return status;
			}
		}
		//We'll only get here if there is something really broken in calling code or model. Gerrit has one and only one comment per location.
		throw new RuntimeException(NLS.bind(Messages.GerritReviewBehavior_Internal_Exception, comment.getId()));
	}

	@Override
	public IStatus discardComment(IReviewItem item, IComment comment, IProgressMonitor monitor) {
		String id = item.getId();
		if (id.startsWith(BASE)) {
			// base revision
			id = id.substring(BASE.length());
		}
		Patch.Key key = Patch.Key.parse(id);
		for (ILocation location : comment.getLocations()) {
			if (location instanceof ILineLocation) {
				DiscardDraftRequest request = new DiscardDraftRequest(key, comment.getId());
				request.setMessage(comment.getDescription());

				GerritOperation<VoidResult> operation = getOperationFactory().createOperation(getTask(), request);
				return operation.run(monitor);
			}
		}
		throw new RuntimeException(Messages.GerritReviewBehavior_Internal_Exception + comment.getId());
	}

	@Override
	public IFileRevision getFileRevision(IFileVersion reviewFileVersion) {
		if (repository != null) {
			return GitFileRevisionUtils.getFileRevision(repository, reviewFileVersion);
		}
		return null;
	}

}
