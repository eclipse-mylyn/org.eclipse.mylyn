/*******************************************************************************
 * Copyright (c) 2019 Tasktop Technologies and others.
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

public class CommentInfo {

	private String id;

	private String path;

	// REVISION (default) or PARENT
//	@SuppressWarnings("unused")
	private String side;

	// 0 for file comment (default)
	private int line;

	@SuppressWarnings("unused")
	private String in_reply_to;

	@SuppressWarnings("unused")
	private Timestamp updated;

	private String message;

	private AccountInfo author;

	public AccountInfo getAuthor() {
		return author;
	}

	public void setAuthor(AccountInfo author) {
		this.author = author;
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
		line = number;
	}

	public String getSide() {
		return side;
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
