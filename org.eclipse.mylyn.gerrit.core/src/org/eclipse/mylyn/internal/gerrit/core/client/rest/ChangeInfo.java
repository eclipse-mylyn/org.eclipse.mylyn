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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSetApproval;

/**
 * Data model object for <a
 * href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#change-info">ChangeInfo</a>.
 */
public class ChangeInfo {
	// e.g. "gerritcodereview#change"
	private String kind;

	// e.g. "myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940"
	private String id;

	// e.g. "myProject"
	private String project;

	// e.g. "master"
	private String branch;

	// e.g. "I8473b95934b5732ac55d26311a706c9c2bde9940"
	private String change_id;

	// e.g. "Implementing Feature X"
	private String subject;

	// e.g. "ABANDONED"
	private Change.Status status;

	// e.g. "2013-02-01 09:59:32.126000000"
	private Timestamp created;

	// e.g. "2013-02-21 11:16:36.775000000",
	private Timestamp updated;

	private boolean reviewed;

	private boolean mergeable;

	private AccountInfo owner;

	// e.g. "0023412400000f7d"
	@SuppressWarnings("unused")
	private String _sortkey;

	// e.g. 3965
	@SuppressWarnings("unused")
	private int _number;

	public String getKind() {
		return kind;
	}

	public String getId() {
		return id;
	}

	public String getProject() {
		return project;
	}

	public String getBranch() {
		return branch;
	}

	public String getChangeId() {
		return change_id;
	}

	public String getSubject() {
		return subject;
	}

	public Change.Status getStatus() {
		return status;
	}

	public Timestamp getCreated() {
		return created;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public boolean isMergeable() {
		return mergeable;
	}

	public AccountInfo getOwner() {
		return owner;
	}

	private Map<String/*Label*/, LabelInfo> labels;

	private String current_revision;

	private Map<String/*commit ID*/, RevisionInfo> revisions;

	public Map<String, LabelInfo> getLabels() {
		return labels;
	}

	public String getCurrentRevision() {
		return current_revision;
	}

	public Map<String, RevisionInfo> getRevisions() {
		return revisions;
	}

	private PatchSet.Id getCurrentPatchSetId() {
		Change.Id changeId = new Change.Id(_number);
		int patchSetId = revisions.get(current_revision).getNumber();
		return new PatchSet.Id(changeId, patchSetId);
	}

	public Set<ApprovalDetail> convertToApprovalDetails() {
		if (labels == null) {
			return Collections.<ApprovalDetail> emptySet();
		}
		Set<ApprovalDetail> result = new HashSet<ApprovalDetail>();
		for (Entry<String, LabelInfo> entry : labels.entrySet()) {
			List<ApprovalInfo> all = entry.getValue().getAll();
			if (all != null) {
				String name = entry.getKey().replace('-', ' ');
				ApprovalCategory.Id approvalCategoryId = ApprovalUtil.findCategoryIdByName(name);
				for (ApprovalInfo approvalInfo : all) {
					Account.Id accountId = new Account.Id(approvalInfo.getId());
					ApprovalDetail approvalDetail = new ApprovalDetail(accountId);
					approvalDetail.add(new PatchSetApproval(new PatchSetApproval.Key(getCurrentPatchSetId(), accountId,
							approvalCategoryId), approvalInfo.getValue()));
					result.add(approvalDetail);
				}
			}
		}
		return result;
	}

	public Set<ApprovalType> convertToApprovalTypes() {
		if (labels == null) {
			return null;
		}
		Set<ApprovalType> result = new HashSet<ApprovalType>();
		for (Entry<String, LabelInfo> entry : labels.entrySet()) {
			String name = entry.getKey().replace('-', ' ');
			ApprovalCategory.Id approvalCategoryId = ApprovalUtil.findCategoryIdByName(name);
			ApprovalCategory approvalCategory = new ApprovalCategory(approvalCategoryId, name);
			List<ApprovalCategoryValue> valueList = new ArrayList<ApprovalCategoryValue>();
			for (Entry<String, String> valueEntry : entry.getValue().getValues().entrySet()) {
				valueList.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(approvalCategoryId,
						parseShort(valueEntry.getKey())), valueEntry.getValue()));
			}
			ApprovalType approvalType = new ApprovalType(approvalCategory, valueList);
			result.add(approvalType);
		}
		return result;
	}

	private static short parseShort(String s) {
		s = s.trim();
		// only Java7 handles a plus sign as indication of a positive value
		if (s.startsWith("+")) { //$NON-NLS-1$
			s = s.substring(1);
		}
		return Short.parseShort(s);
	}

}
