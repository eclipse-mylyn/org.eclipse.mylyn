/*******************************************************************************
 * Copyright (c) 2009, 2014 Atlassian and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.util.Objects;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.spi.ModelUtil;
import org.eclipse.osgi.util.NLS;

/**
 * Class to represent a comment in a review
 *
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class CommentAnnotation extends Annotation {

	public static final String COMMENT_ANNOTATION_ID = "org.eclipse.mylyn.reviews.ui.comment.Annotation"; //$NON-NLS-1$

	public static final String COMMENT_ANNOTATION_ME_ID = "org.eclipse.mylyn.reviews.ui.comment.AnnotationMe"; //$NON-NLS-1$

	private final Position position;

	private final IComment comment;

	public CommentAnnotation(int offset, int length, IComment comment) {
		super(comment.isMine() ? COMMENT_ANNOTATION_ME_ID : COMMENT_ANNOTATION_ID, false, null);
		position = new Position(offset, length);
		this.comment = comment;
	}

	public Position getPosition() {
		return position;
	}

	@Override
	public String getText() {
		return NLS.bind(Messages.CommentAnnotation_X_dash_Y, comment.getAuthor().getDisplayName(), comment.getTitle());
	}

	public IComment getComment() {
		return comment;
	}

	@Override
	public int hashCode() {
		int result = position == null ? 0 : position.hashCode();
		return ModelUtil.ecoreHash(result, comment);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		CommentAnnotation other = (CommentAnnotation) obj;
		if (!Objects.equals(position, other.position)) {
			return false;
		}
		if (comment != null && other.comment != null && comment.getId() != null && other.comment.getId() != null) {
			return comment.getId().equals(other.comment.getId());
		}
		return EcoreUtil.equals(comment, other.comment);
	}
}
