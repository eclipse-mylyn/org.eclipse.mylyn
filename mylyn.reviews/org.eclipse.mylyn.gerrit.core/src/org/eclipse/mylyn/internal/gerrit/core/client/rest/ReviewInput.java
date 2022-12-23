/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.reviewdb.ApprovalCategoryValue;

/**
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#review-input">ReviewInput</a>.
 */
public class ReviewInput {

	private final String message;

	private Map<String, CommentInput[]> comments;

	private Map<String, Short> labels;

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

	public void setApprovals(Set<ApprovalCategoryValue.Id> approvals) {
		if (approvals == null || approvals.isEmpty()) {
			return;
		}
		labels = new HashMap<String, Short>(approvals.size());
		for (ApprovalCategoryValue.Id approval : approvals) {
			String labelName = ApprovalUtil.findCategoryNameById(approval.getParentKey().get());
			if (labelName == null) {
				GerritCorePlugin
						.logWarning(NLS.bind("Couldn't find label name for {0}. (Expected approval IDs are {1})", //$NON-NLS-1$
								approval.getParentKey().get(), ApprovalUtil.BY_ID.keySet()), null);
				continue;
			}
			labelName = labelName.replace(' ', '-');
			Short voteValue = Short.valueOf(approval.get());
			labels.put(labelName, voteValue);
		}
	}

}
