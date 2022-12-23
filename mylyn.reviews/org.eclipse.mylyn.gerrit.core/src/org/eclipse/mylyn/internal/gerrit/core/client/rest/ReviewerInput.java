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
 * Data model object for <a href=
 * "https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#reviewer-input">ReviewerInput</a>.
 */
public class ReviewerInput {

	private final String reviewer;

	public ReviewerInput(String reviewer) {
		Assert.isLegal(reviewer != null);
		Assert.isLegal(!reviewer.isEmpty());
		this.reviewer = reviewer;
	}

	public String getReviewer() {
		return reviewer;
	}
}
