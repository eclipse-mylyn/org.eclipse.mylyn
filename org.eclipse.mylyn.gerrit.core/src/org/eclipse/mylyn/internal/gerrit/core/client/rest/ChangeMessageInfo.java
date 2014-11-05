/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ericsson AB - initial API and implementation
 ******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.sql.Timestamp;

/**
 * Data model object for <a
 * href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#change-message-info"
 * >ChangeMessageInfo</a>.
 */
public class ChangeMessageInfo {

	private String id;

	private AccountInfo author;

	private Timestamp date;

	private String message;

	private Integer _revisionNumber;

	public String getMesssage() {
		return message;
	}

	public Timestamp getDate() {
		return date;
	}

}
