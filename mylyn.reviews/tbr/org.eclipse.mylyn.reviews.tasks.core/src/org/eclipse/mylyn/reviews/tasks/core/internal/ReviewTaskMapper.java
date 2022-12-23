/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *     Jacques Bouthillier (Ericsson) - Bug 422509 Prevent Null pointer exception
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.reviews.tasks.core.Attachment;
import org.eclipse.mylyn.reviews.tasks.core.ChangesetScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.IReviewMapper;
import org.eclipse.mylyn.reviews.tasks.core.IReviewScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.PatchScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.Rating;
import org.eclipse.mylyn.reviews.tasks.core.ResourceScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.ReviewResult;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
import org.eclipse.mylyn.reviews.tasks.core.TaskComment;
import org.eclipse.mylyn.reviews.tasks.dsl.IReviewDslMapper;
import org.eclipse.mylyn.reviews.tasks.dsl.IReviewDslSerializer;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslAttachmentScopeItem;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslAttachmentScopeItem.Type;
import org.eclipse.mylyn.reviews.tasks.dsl.ParseException;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslChangesetScopeItem;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslResult;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslScope;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslScopeItem;

/**
 * @author mattk
 * 
 */
public class ReviewTaskMapper implements IReviewMapper {
	private IReviewDslMapper parser;
	private IReviewDslSerializer serializer;

	public ReviewTaskMapper(IReviewDslMapper parser,
			IReviewDslSerializer serializer) {
		this.parser = parser;
		this.serializer = serializer;
	}

	public ReviewScope mapTaskToScope(ITaskProperties properties)
			throws CoreException {
		Assert.isNotNull(properties);
		try {
			ReviewDslScope parsedReviewScope = parser
					.parseReviewScope(properties.getDescription());
			ReviewScope originalScope = mapReviewScope(properties,
					parsedReviewScope);
			// FIXME changed review scope
			// for (TaskComment comment : properties.getComments()) {
			// if (properties.getReporter().equals(comment.getAuthor())) {
			// ChangedReviewScope changedScope =
			// parser.parseChangedReviewScope(comment.getText());
			// applyChangedScope(properties, originalScope, changedScope);
			// }
			// }
			// }
			return originalScope;
		} catch (ParseException ex) {
			// ignore
		}
		return null;

	}

	public void mapScopeToTask(ReviewScope scope, ITaskProperties taskProperties) {
		ReviewDslScope scope2 = mapScope(scope);

		taskProperties.setDescription(serializer.serialize(scope2));
	}

	public void mapResultToTask(
			org.eclipse.mylyn.reviews.tasks.core.ReviewResult res,
			ITaskProperties taskProperties) {
		ReviewDslResult result = new ReviewDslResult();
		ReviewDslResult.Rating rating = ReviewDslResult.Rating.WARNING;
		// Need to set the rating, otherwise get a null pointer exception
		if (res.getRating() != null) {
			switch (res.getRating()) {
			case FAIL:
				rating = ReviewDslResult.Rating.FAILED;
				break;
			case PASSED:
				rating = ReviewDslResult.Rating.PASSED;
				break;
			case TODO:
				rating = ReviewDslResult.Rating.TODO;
				break;
			case WARNING:
				rating = ReviewDslResult.Rating.WARNING;
				break;
			}
			result.setRating(rating);

		}
		result.setComment(res.getComment());

		String resultAsText = serializer.serialize(result);
		taskProperties.setNewCommentText(resultAsText);
	}

	public org.eclipse.mylyn.reviews.tasks.core.ReviewResult mapCurrentReviewResult(
			ITaskProperties taskProperties) {
		Assert.isNotNull(taskProperties);
		if (taskProperties.getNewCommentText() == null || "".equals(taskProperties.getNewCommentText()))
			return null;
		ReviewResult result = null;
		try {
			ReviewDslResult res = parser.parseReviewResult(taskProperties
					.getNewCommentText());
			if (res == null)
				return null;
			result = new ReviewResult();
			result.setComment(res.getComment());
			result.setRating(mapRating(res.getRating()));
			// FIXME filecomment, linecomment
			// FIXME author is current
			// result.setReviewer()
			// result.setDate()
		} catch (ParseException ex) {
			/* ignore */
		}
		return result;
	}

	public List<ReviewResult> mapTaskToResults(ITaskProperties taskProperties) {
		List<ReviewResult> results = new ArrayList<ReviewResult>();
		for (TaskComment comment : taskProperties.getComments()) {
			try {
				ReviewDslResult parsed = parser.parseReviewResult(comment
						.getText());
				if (parsed != null) {
					results.add(mapResult(parsed, comment));
				}
			} catch (ParseException ex) {
				// ignore
			}
		}
		return results;
	}

	// FIXME Changed Review scope
	// private void applyChangedScope(ITaskProperties properties,
	// ReviewScope originalScope, ChangedReviewScope changedScope)
	// throws CoreException {
	// for (ReviewScopeItem scope : changedScope.getScope()) {
	// IReviewScopeItem item = mapReviewScopeItem(properties, scope);
	// originalScope.addScope(item);
	// }
	// }

	private ReviewScope mapReviewScope(ITaskProperties properties,
			ReviewDslScope scope) throws CoreException {
		if (scope == null)
			return null;

		ReviewScope mappedScope = new ReviewScope();
		mappedScope.setCreator(properties.getReporter());
		for (ReviewDslScopeItem s : scope.getItems()) {
			IReviewScopeItem item = mapReviewScopeItem(properties, s);
			if (item != null) {
				mappedScope.addScope(item);
			}
		}
		return mappedScope;
	}

	private IReviewScopeItem mapReviewScopeItem(ITaskProperties properties,
			ReviewDslScopeItem s) throws CoreException {
		IReviewScopeItem item = null;
		if (s instanceof ReviewDslAttachmentScopeItem) {
			item = mapPatchDef(properties, (ReviewDslAttachmentScopeItem) s);
		} else if (s instanceof ReviewDslChangesetScopeItem) {
			item = mapChangesetDef(properties, (ReviewDslChangesetScopeItem) s);
		}
		return item;
	}

	private ChangesetScopeItem mapChangesetDef(ITaskProperties properties,
			ReviewDslChangesetScopeItem cs) throws CoreException {
		return new ChangesetScopeItem(cs.getRevision(), cs.getRepoUrl());
	}

	private IReviewScopeItem mapPatchDef(ITaskProperties properties,
			ReviewDslAttachmentScopeItem scopeItem) throws CoreException {

		Attachment att = ReviewsUtil.findAttachment(scopeItem.getFileName(),
				scopeItem.getAuthor(), scopeItem.getCreatedDate(),
				properties.loadFor(scopeItem.getTaskId()));
		if (scopeItem.getType() == Type.PATCH) {
			return new PatchScopeItem(att);
		} else {
			return new ResourceScopeItem(att);
		}
	}

	private ReviewResult mapResult(ReviewDslResult parsed, TaskComment comment) {
		if (parsed == null)
			return null;

		ReviewResult result = new ReviewResult();
		result.setReviewer(comment.getAuthor());
		result.setDate(comment.getDate());
		result.setRating(mapRating(parsed.getRating()));
		result.setComment(parsed.getComment());
		return result;
	}

	private Rating mapRating(ReviewDslResult.Rating result) {
		switch (result) {
		case PASSED:
			return Rating.PASSED;
		case FAILED:
			return Rating.FAIL;
		case WARNING:
			return Rating.WARNING;
		case TODO:
			return Rating.TODO;
		}
		throw new IllegalArgumentException();
	}

	private ReviewDslScope mapScope(ReviewScope scope) {
		ReviewDslScope scope2 = new ReviewDslScope();

		for (IReviewScopeItem item : scope.getItems()) {
			scope2.addItem(mapScopeItem(item));
		}
		return scope2;
	}

	private ReviewDslScopeItem mapScopeItem(IReviewScopeItem item) {
		if (item instanceof PatchScopeItem) {
			PatchScopeItem patchItem = (PatchScopeItem) item;
			return createAttachmentScopeItem(Type.PATCH,
					patchItem.getAttachment());
		} else if (item instanceof ResourceScopeItem) {
			ResourceScopeItem resourceItem = (ResourceScopeItem) item;
			return createAttachmentScopeItem(Type.RESOURCE,
					resourceItem.getAttachment());
		} else if (item instanceof ChangesetScopeItem) {
			ChangesetScopeItem changesetItem = (ChangesetScopeItem) item;
			ReviewDslChangesetScopeItem changeset = new ReviewDslChangesetScopeItem();
			changeset.setRevision(changesetItem.getRevisionId());
			changeset.setRepoUrl(changesetItem.getRepositoryUrl());
			return changeset;
		}
		return null;
	}

	private ReviewDslAttachmentScopeItem createAttachmentScopeItem(Type type,
			Attachment attachment) {
		return new ReviewDslAttachmentScopeItem(type, attachment.getFileName(),
				attachment.getAuthor(), attachment.getDate(), attachment
						.getTask().getTaskId());
	}

}
