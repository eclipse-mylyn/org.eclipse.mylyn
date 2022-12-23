/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.sql.Timestamp;

/**
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#comment-input">CommentInput</a>
 * and
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#comment-info">CommentInfo</a>.
 */
public class CommentInput {

	protected final String kind = "gerritcodereview#comment"; //$NON-NLS-1$

	private String id;

	private String path;

	// REVISION (default) or PARENT
	@SuppressWarnings("unused")
	private String side;

	// 0 for file comment (default)
	private int line;

	@SuppressWarnings("unused")
	private String in_reply_to;

	@SuppressWarnings("unused")
	private Timestamp updated;

	private String message;

	public String getKind() {
		return kind;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int number) {
		this.line = number;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
