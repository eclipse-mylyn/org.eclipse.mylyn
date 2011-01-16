/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.mylyn.reviews.core.model.ITopic;

/**
 * Class to represent a comment in a Crucible review
 * 
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class CommentAnnotation extends Annotation {

	public static final String COMMENT_ANNOTATION_ID = "org.eclipse.mylyn.reviews.ui.comment.Annotation";

	private final Position position;

	private final ITopic comment;

	public CommentAnnotation(int offset, int length, ITopic comment) {
		super(COMMENT_ANNOTATION_ID, false, null);
		position = new Position(offset, length);
		this.comment = comment;
	}

	public Position getPosition() {
		return position;
	}

	@Override
	public String getText() {
		return comment.getAuthor().getDisplayName() + " - " + comment.getTitle();
	}

	public ITopic getTopic() {
		return comment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CommentAnnotation)) {
			return false;
		}
		final CommentAnnotation other = (CommentAnnotation) obj;
		if (comment == null) {
			if (other.comment != null) {
				return false;
			}
		} else if (!comment.equals(other.comment)) {
			return false;
		}
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		return true;
	}

}
