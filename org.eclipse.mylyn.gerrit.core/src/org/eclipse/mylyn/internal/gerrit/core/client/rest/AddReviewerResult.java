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

import java.util.List;

/**
 * Data model object for <a
 * href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#add-reviewer-result"
 * >AddReviewerResult</a>.
 */
public class AddReviewerResult {

	private List<ReviewerInfo> reviewers;

	private String error;

	public List<ReviewerInfo> getReviewers() {
		return reviewers;
	}

	public String getError() {
		return error;
	}

}
