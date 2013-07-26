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

import java.sql.Timestamp;

/**
 * Data model object for <a
 * href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#comment-input">CommentInput</a>.
 */
public class CommentInput {

	protected final String kind = "gerritcodereview#comment"; //$NON-NLS-1$

	private String id;

	private String path;

	// REVISION (default) or PARENT
	private String side;

	// 0 for file comment (default)
	private int line;

	private String in_reply_to;

	private Timestamp updated;

	private String message;

	public CommentInput() {
	}

	public CommentInput(CommentInfo info) {
		this.id = info.getId();
		this.path = info.getPath();
		this.line = info.getLine();
		this.message = info.getMessage();
	}

	public String getKind() {
		return kind;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public int getLine() {
		return line;
	}

	public String getMessage() {
		return message;
	}
}
