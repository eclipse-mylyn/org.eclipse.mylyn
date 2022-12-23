/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tasks.dsl.internal;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.TreeAdaptor;
import org.eclipse.mylyn.reviews.tasks.dsl.IReviewDslMapper;
import org.eclipse.mylyn.reviews.tasks.dsl.ParseException;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslAttachmentScopeItem;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslAttachmentScopeItem.Type;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslChangesetScopeItem;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslResult;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslResult.FileComment;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslResult.LineComment;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslResult.Rating;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslScope;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslParser.attachmentSource_return;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslParser.changesetDef_return;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslParser.fileComment_return;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslParser.lineComment_return;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslParser.patchDef_return;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslParser.resourceDef_return;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslParser.reviewResult_return;
import org.eclipse.mylyn.reviews.tasks.dsl.internal.ReviewDslParser.reviewScope_return;

/**
 * 
 * @author mattk
 * 
 */
public class ReviewDslMapper implements IReviewDslMapper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.mylyn.reviews.tasks.dsl.internal.IReviewDslMapper#
	 * parseReviewResult(java.lang.String)
	 */
	public ReviewDslResult parseReviewResult(String text) throws ParseException {
		ReviewDslLexer lexer = new ReviewDslLexer(new ANTLRStringStream(text));
		TokenStream input = new CommonTokenStream(lexer);
		ReviewDslParser parser = new ReviewDslParser(input);
		try {
			return mapResult(parser.reviewResult());
		} catch (Exception e) {
			throw new ParseException(e.getMessage());
		}

	}

	private ReviewDslResult mapResult(reviewResult_return reviewResult) {
		ReviewDslResult result = null;
		try {
			result = new ReviewDslResult();
			result.setRating(Rating.valueOf(reviewResult.result));
			result.setComment(convertStr(reviewResult.comment));
			if (reviewResult.fileComments != null) {
				for (int i = 0; i < reviewResult.fileComments.size(); i++) {
					fileComment_return fc = (fileComment_return) reviewResult.fileComments
							.get(i);
					result.getFileComments().add(map(fc));
				}
			}
			if(result.getRating()==null && result.getComment()==null && result.getFileComments().size()==0) {
				return null;
			}
		} catch (Exception ex) {
		}
		return result;
	}

	private FileComment map(fileComment_return fc) {
		FileComment fileComment = new FileComment();
		fileComment.setFileName(convertStr(fc.path));
		fileComment.setComment(convertStr(fc.comment));
		if (fc.lineComments != null) {
			for (int i = 0; i < fc.lineComments.size(); i++) {
				lineComment_return lc = (lineComment_return) fc.lineComments
						.get(i);
				fileComment.getLineComments().add(map(lc));
			}
		}
		return fileComment;
	}

	private LineComment map(lineComment_return lc) {
		LineComment lineComment = new LineComment();
		lineComment.setBegin(lc.begin);
		if (lc.end != null) {
			lineComment.setEnd(Integer.parseInt(lc.end));
		} else {
			lineComment.setEnd(lineComment.getBegin());
		}
		lineComment.setComment(convertStr(lc.comment));

		return lineComment;
	}

	private String convertStr(String string) {
		if (string == null)
			return string;
		int startIdx = 0;
		int endIdx = string.length();
		if (string.startsWith("\""))
			startIdx = 1;
		if (string.endsWith("\""))
			endIdx--;
		return string.substring(startIdx, endIdx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.mylyn.reviews.tasks.dsl.internal.IReviewDslMapper#
	 * parseReviewScope(java.lang.String)
	 */
	public ReviewDslScope parseReviewScope(String text) throws ParseException {
		ReviewDslLexer lexer = new ReviewDslLexer(new ANTLRStringStream(text));
		TokenStream input = new CommonTokenStream(lexer);
		ReviewDslParser parser = new ReviewDslParser(input);
		try {
			return mapScope(parser.reviewScope(), parser.getTreeAdaptor());
		} catch (RecognitionException e) {
			throw new ParseException(e.getMessage());
		}
	}

	private ReviewDslScope mapScope(reviewScope_return reviewScope,
			TreeAdaptor treeAdaptor) {
		if (reviewScope == null)
			return null;
		ReviewDslScope scope = new ReviewDslScope();

		if (reviewScope.scopeItems != null) {
			for (int i = 0; i < reviewScope.scopeItems.size(); i++) {
				Object child = reviewScope.scopeItems.get(i);
				if (patchDef_return.class.equals(child.getClass())) {
					scope.addItem(parsePatch((patchDef_return) child,
							treeAdaptor));
				} else if (resourceDef_return.class.equals(child.getClass())) {
					scope.addItem(parseResource((resourceDef_return) child,
							treeAdaptor));
				} else if (changesetDef_return.class.equals(child.getClass())) {
					scope.addItem(parseChangeSet((changesetDef_return) child));
				}
			}
		}
		return scope;
	}

	private ReviewDslChangesetScopeItem parseChangeSet(changesetDef_return child) {
		ReviewDslChangesetScopeItem item = new ReviewDslChangesetScopeItem();
		item.setRevision(convertStr(child.revision));
		item.setRepoUrl(convertStr(child.repoUrl));
		return item;
	}

	private ReviewDslAttachmentScopeItem parseResource(
			resourceDef_return child, TreeAdaptor treeAdaptor) {
		AttachmentSource source = parseAttachmentSource((attachmentSource_return) child.source);
		return new ReviewDslAttachmentScopeItem(Type.RESOURCE, source.fileName,
				source.author, source.createdDate, source.taskId);
	}

	private ReviewDslAttachmentScopeItem parsePatch(patchDef_return child,
			TreeAdaptor treeAdaptor) {

		AttachmentSource source = parseAttachmentSource((attachmentSource_return) child.source);
		return new ReviewDslAttachmentScopeItem(Type.PATCH, source.fileName,
				source.author, source.createdDate, source.taskId);
	}

	private AttachmentSource parseAttachmentSource(attachmentSource_return child) {
		AttachmentSource source = new AttachmentSource();
		source.fileName = convertStr(child.filename);
		source.author = convertStr(child.author);
		source.createdDate = convertStr(child.createdDate);
		source.taskId = convertStr(child.taskId);
		return source;
	}

	private static class AttachmentSource {
		public String fileName;
		public String author;
		public String createdDate;
		public String taskId;
	}

	public ReviewDslResult parseChangedReviewScope(String text) {
		// TODO Auto-generated method stub
		return null;
	}
}
