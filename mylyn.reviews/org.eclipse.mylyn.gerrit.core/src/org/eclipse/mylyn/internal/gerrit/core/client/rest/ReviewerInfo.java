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

import java.util.Map;
import java.util.Map.Entry;

import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSetApproval;

/**
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#reviewer-info">ReviewerInfo</a>.
 */
public class ReviewerInfo extends AccountInfo {

	private final String kind = "gerritcodereview#reviewer"; //$NON-NLS-1$

	private Map<String/*label name*/, String/*approval value*/> approvals;

	public String getKind() {
		return kind;
	}

	public Map<String, String> getApprovals() {
		return approvals;
	}

	public ApprovalDetail toApprovalDetail(PatchSet currentPatchSet) {
		Account.Id accountId = new Account.Id(getId());
		ApprovalDetail approvalDetail = new ApprovalDetail(accountId);
		Map<String, String> map = getApprovals();
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				ApprovalCategory.Id categoryId = ApprovalUtil.findCategoryIdByName(entry.getKey().replace('-', ' '));
				if (categoryId != null) {
					PatchSetApproval patchSetApproval = new PatchSetApproval(
							new PatchSetApproval.Key(currentPatchSet.getId(), accountId, categoryId),
							ApprovalUtil.parseShort(entry.getValue()));
					approvalDetail.add(patchSetApproval);
				}
			}
		}
		return approvalDetail;
	}

	public com.google.gerrit.common.data.AccountInfo toAccountInfo() {
		Account.Id accountId = new Account.Id(getId());
		Account account = new Account(accountId);
		account.setFullName(getName());
		account.setPreferredEmail(getEmail());
		com.google.gerrit.common.data.AccountInfo accountInfo = new com.google.gerrit.common.data.AccountInfo(account);
		return accountInfo;
	}
}
