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
 *     Marc-Andre Laperle (Ericsson) - Add topic
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.mylyn.internal.gerrit.core.client.compat.PermissionLabel;

import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSetApproval;

/**
 * Data model object for <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#change-info">ChangeInfo</a>.
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

	// e.g. "Topic"
	private String topic;

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

	private LinkedHashMap<String/*Label*/, LabelInfo> labels;

	private String current_revision;

	private Map<String/*commit ID*/, RevisionInfo> revisions;

	private Map<String/*Label*/, String[]> permitted_labels;

	// e.g. "0023412400000f7d"
	@SuppressWarnings("unused")
	private String _sortkey;

	// e.g. 3965
	private int _number;

	public int getNumber() {
		return _number;
	}

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

	public String getTopic() {
		return topic;
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

	public Map<String, LabelInfo> getLabels() {
		return labels;
	}

	public String getCurrentRevision() {
		return current_revision;
	}

	public Map<String, RevisionInfo> getRevisions() {
		return revisions;
	}

	public Map<String, String[]> getPermittedLabels() {
		return permitted_labels;
	}

	public PatchSet.Id getCurrentPatchSetId() {
		Change.Id changeId = new Change.Id(_number);
		int patchSetId = revisions.get(current_revision).getNumber();
		return new PatchSet.Id(changeId, patchSetId);
	}

	public Set<ApprovalDetail> convertToApprovalDetails() {
		if (labels == null) {
			return Collections.<ApprovalDetail> emptySet();
		}
		Set<ApprovalDetail> result = new LinkedHashSet<>();
		for (Entry<String, LabelInfo> entry : labels.entrySet()) {
			List<ApprovalInfo> all = entry.getValue().getAll();
			if (all != null) {
				ApprovalCategory.Id approvalCategoryId = ApprovalUtil.findCategoryIdByNameWithDash(entry.getKey());
				if (approvalCategoryId == null) {
					continue;
				}
				for (ApprovalInfo approvalInfo : all) {
					Account.Id accountId = new Account.Id(approvalInfo.getId());
					ApprovalDetail approvalDetail = new ApprovalDetail(accountId);
					approvalDetail.add(new PatchSetApproval(
							new PatchSetApproval.Key(getCurrentPatchSetId(), accountId, approvalCategoryId),
							approvalInfo.getValue()));
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
		Set<ApprovalType> result = new LinkedHashSet<>();
		for (Entry<String, LabelInfo> entry : labels.entrySet()) {
			ApprovalCategory approvalCategory = ApprovalUtil.findCategoryByNameWithDash(entry.getKey());
			if (approvalCategory == null) {
				// it's a custom approval type
				approvalCategory = new ApprovalCategory(new ApprovalCategory.Id(null), entry.getKey());
			}
			List<ApprovalCategoryValue> valueList = new ArrayList<>();
			if (entry.getValue() != null && entry.getValue().getValues() != null) {
				// custom approval types may not provide values
				for (Entry<String, String> valueEntry : entry.getValue().getValues().entrySet()) {
					valueList.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(approvalCategory.getId(),
							ApprovalUtil.parseShort(valueEntry.getKey())), valueEntry.getValue()));
				}
			}
			ApprovalType approvalType = new ApprovalType(approvalCategory, valueList);
			result.add(approvalType);
		}
		return result;
	}

	public List<PermissionLabel> convertToPermissionLabels() {
		if (permitted_labels == null) {
			return null;
		}
		List<PermissionLabel> result = new ArrayList<>(permitted_labels.size());
		for (Entry<String, String[]> entry : permitted_labels.entrySet()) {
			List<Short> values = new ArrayList<>(entry.getValue().length);
			for (String value : entry.getValue()) {
				values.add(ApprovalUtil.parseShort(value));
			}
			PermissionLabel label = new PermissionLabel();
			label.setName(PermissionLabel.toLabelName(entry.getKey()));
			label.setMin(Collections.min(values).intValue());
			label.setMax(Collections.max(values).intValue());
			result.add(label);
		}
		return result;
	}

	/**
	 * Converts labels into a map of approvals given by the provided user.
	 *
	 * @param id
	 *            id of the current patch set
	 * @param account
	 *            the user whose approvals should be converted
	 * @return map of given approvals
	 * @see #labels
	 */
	public Map<ApprovalCategory.Id, PatchSetApproval> convertToPatchSetApprovals(PatchSet.Id id, Account account) {
		if (labels == null) {
			return null;
		}
		Map<ApprovalCategory.Id, PatchSetApproval> result = new HashMap<>(
				labels.size());
		for (Entry<String, LabelInfo> entry : labels.entrySet()) {
			ApprovalCategory approvalCategory = ApprovalUtil.findCategoryByNameWithDash(entry.getKey());
			if ((approvalCategory == null) || (entry.getValue().getAll() == null)) {
				continue;
			}
			for (ApprovalInfo approvalInfo : entry.getValue().getAll()) {
				if (approvalInfo.getId() == account.getId().get()) {
					Account.Id accountId = new Account.Id(approvalInfo.getId());
					PatchSetApproval.Key key = new PatchSetApproval.Key(id, accountId, approvalCategory.getId());
					PatchSetApproval approval = new PatchSetApproval(key, approvalInfo.getValue());
					result.put(approvalCategory.getId(), approval);
				}
			}
		}
		return result;
	}

}
