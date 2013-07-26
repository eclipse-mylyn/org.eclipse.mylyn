/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.Map;

import org.eclipse.core.runtime.Assert;

/**
 * Data model object for <a
 * href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#review-input">ReviewInput</a>.
 */
public class ReviewInput {

	private final String message;

	private Map<String, CommentInput[]> comments;

	public ReviewInput(String msg) {
		Assert.isLegal(msg != null);
		this.message = msg;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, CommentInput[]> getComments() {
		return comments;
	}

	public void setComments(Map<String, CommentInput[]> comments) {
		this.comments = comments;
	}
}
