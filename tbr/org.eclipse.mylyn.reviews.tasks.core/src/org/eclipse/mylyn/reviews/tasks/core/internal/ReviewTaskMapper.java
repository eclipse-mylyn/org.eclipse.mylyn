/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.reviews.tasks.core.Attachment;
import org.eclipse.mylyn.reviews.tasks.core.IReviewMapper;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.PatchScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.Rating;
import org.eclipse.mylyn.reviews.tasks.core.ResourceScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.TaskComment;
import org.eclipse.mylyn.reviews.tasks.dsl.parser.antlr.ReviewDslParser;
import org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.AttachmentSource;
import org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.PatchDef;
import org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ResourceDef;
import org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ResultEnum;
import org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewDslFactory;
import org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewResult;
import org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.Source;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parsetree.reconstr.Serializer;
/**
 * @author mattk
 *
 */
public class ReviewTaskMapper implements IReviewMapper {
	private ReviewDslParser parser;
	private Serializer serializer;

	public ReviewTaskMapper(ReviewDslParser parser, Serializer serializer) {
		this.parser = parser;
		this.serializer = serializer;
	}

	private org.eclipse.mylyn.reviews.tasks.core.ReviewResult mapResult(
			org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewResult parsed,
			TaskComment comment) {
		if (parsed == null)
			return null;

		org.eclipse.mylyn.reviews.tasks.core.ReviewResult result = new org.eclipse.mylyn.reviews.tasks.core.ReviewResult();
		result.setReviewer(comment.getAuthor());
		result.setDate(comment.getDate());
		result.setRating(mapRating(parsed.getResult()));
		result.setComment(comment.getText());
		return result;
	}

	private Rating mapRating(ResultEnum result) {
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

	@Override
	public ReviewScope mapTaskToScope(ITaskProperties properties)
			throws CoreException {
		Assert.isNotNull(properties);
		IParseResult parsed = parser.doParse(properties.getDescription());

		org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewScope scope = (org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewScope) parsed
				.getRootASTElement();
		return mapReviewScope(properties, scope);
	}

	private ReviewScope mapReviewScope(ITaskProperties properties,
			org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewScope scope)
			throws CoreException {
		if (scope == null)
			return null;

		ReviewScope mappedScope = new ReviewScope();
		mappedScope.setCreator(properties.getReporter());
		for (org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewScopeItem s : scope
				.getScope()) {
			if (s instanceof PatchDef) {
				PatchScopeItem item = mapPatchDef(properties, mappedScope, (PatchDef) s);
				mappedScope.addScope(item);
			} else if (s instanceof ResourceDef) {
				ResourceDef res = (ResourceDef) s;
				ResourceScopeItem item = mapResourceDef(properties, res);
				mappedScope.addScope(item);
			}
		}
		return mappedScope;
	}

	private ResourceScopeItem mapResourceDef(ITaskProperties properties,
			ResourceDef res) throws CoreException {
		Source source = res.getSource();
		Attachment att = null;
		if (source instanceof AttachmentSource) {
			att = parseAttachmenSource(properties, source);
		}
		return new ResourceScopeItem(att);
	}

	private PatchScopeItem mapPatchDef(ITaskProperties properties,
			ReviewScope mappedScope, PatchDef patch) throws CoreException {
		Source source = patch.getSource();
		Attachment att = null;
		if (source instanceof AttachmentSource) {
			att = parseAttachmenSource(properties, source);
		}
		return new PatchScopeItem(att);
	}

	private Attachment parseAttachmenSource(ITaskProperties properties,
			Source source) throws CoreException {
		AttachmentSource attachment = (AttachmentSource) source;

		Attachment att = ReviewsUtil.findAttachment(attachment.getFilename(),
				attachment.getAuthor(), attachment.getCreatedDate(),
				properties.loadFor(attachment.getTaskId()));
		return att;
	}

	@Override
	public void mapScopeToTask(ReviewScope scope, ITaskProperties taskProperties) {
		org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewScope scope2 = mapScope(scope);

		taskProperties.setDescription(serializer.serialize(scope2));
	}

	private org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewScope mapScope(
			ReviewScope scope) {
		org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewScope scope2 = ReviewDslFactory.eINSTANCE
				.createReviewScope();
		for (ReviewScopeItem item : scope.getItems()) {
			scope2.getScope().add(mapScopeItem(item));
		}
		return scope2;
	}

	private org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewScopeItem mapScopeItem(
			ReviewScopeItem item) {
		if (item instanceof PatchScopeItem) {
			PatchScopeItem patchItem = (PatchScopeItem) item;
			PatchDef patch = ReviewDslFactory.eINSTANCE.createPatchDef();
			Attachment attachment = patchItem.getAttachment();
			AttachmentSource source = mapAttachment(attachment);
			patch.setSource(source);

			return patch;
		} else if (item instanceof ResourceScopeItem) {
			ResourceScopeItem resourceItem = (ResourceScopeItem) item;
			ResourceDef resource = ReviewDslFactory.eINSTANCE
					.createResourceDef();
			Attachment attachment = resourceItem.getAttachment();
			AttachmentSource source = mapAttachment(attachment);
			resource.setSource(source);
			return resource;
		}
		System.err.println("would return null");
		return null;
	}

	private AttachmentSource mapAttachment(Attachment attachment) {
		AttachmentSource source = ReviewDslFactory.eINSTANCE
				.createAttachmentSource();
		source.setAuthor(attachment.getAuthor());
		source.setCreatedDate(attachment.getDate());
		source.setFilename(attachment.getFileName());
		source.setTaskId(attachment.getTask().getTaskId());
		return source;
	}

	@Override
	public void mapResultToTask(
			org.eclipse.mylyn.reviews.tasks.core.ReviewResult res,
			ITaskProperties taskProperties) {
		ReviewResult result = ReviewDslFactory.eINSTANCE.createReviewResult();
		ResultEnum rating = ResultEnum.WARNING;
		switch (res.getRating()) {
		case FAIL:
			rating = ResultEnum.FAILED;
			break;
		case PASSED:
			rating = ResultEnum.PASSED;
			break;
		case TODO:
			rating = ResultEnum.TODO;
			break;
		case WARNING:
			rating = ResultEnum.WARNING;
			break;
		}
		result.setResult(rating);
		result.setComment(res.getComment());

		String resultAsText = serializer.serialize(result);
		System.err.println(resultAsText);
		taskProperties.setNewCommentText(resultAsText);
	}

	@Override
	public org.eclipse.mylyn.reviews.tasks.core.ReviewResult mapCurrentReviewResult(
			ITaskProperties taskProperties) {
		Assert.isNotNull(taskProperties);
		if (taskProperties.getNewCommentText() == null)
			return null;
		IParseResult parsed = parser
				.doParse(taskProperties.getNewCommentText());

		org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewResult res = (org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewResult) parsed
				.getRootASTElement();
		org.eclipse.mylyn.reviews.tasks.core.ReviewResult result = new org.eclipse.mylyn.reviews.tasks.core.ReviewResult();
		if (res == null)
			return null;
		result.setComment(res.getComment());
		result.setRating(mapRating(res.getResult()));
		// FIXME author is current
		// result.setReviewer()
		// result.setDate()
		return result;
	}

	@Override
	public List<org.eclipse.mylyn.reviews.tasks.core.ReviewResult> mapTaskToResults(
			ITaskProperties taskProperties) {
		List<org.eclipse.mylyn.reviews.tasks.core.ReviewResult> results = new ArrayList<org.eclipse.mylyn.reviews.tasks.core.ReviewResult>();
		for (TaskComment comment : taskProperties.getComments()) {
			IParseResult parsed = parser.doParse(comment.getText());
			if (parsed.getRootASTElement() != null) {
				results.add(mapResult(
						(org.eclipse.mylyn.reviews.tasks.dsl.reviewDsl.ReviewResult) parsed
								.getRootASTElement(), comment));
			}
		}
		return results;
	}

}
