/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.Assert;

/**
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#abandon-input">AbandonInput</a>.
 */
public class AbandonInput {

	@SuppressWarnings("unused")
	private final String message;

	public AbandonInput(String msg) {
		Assert.isLegal(msg != null);
		message = msg;
	}
}
