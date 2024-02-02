/*******************************************************************************
 * Copyright (c) 2014, 2015 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Ericsson AB - initial API and implementation
 ******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.sql.Timestamp;

/**
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#change-message-info" >ChangeMessageInfo</a>.
 */
public class ChangeMessageInfo {

	private AccountInfo author;

	private Timestamp date;

	private String message;

	public String getMesssage() {
		return message;
	}

	public Timestamp getDate() {
		return date;
	}

	/**
	 * Author of the message as an AccountInfo entity. <b>Unset if written by the Gerrit system</b>.<br>
	 * See <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#change-message-info" >ChangeMessageInfo</a>.
	 */
	public AccountInfo getAuthor() {
		return author;
	}
}
