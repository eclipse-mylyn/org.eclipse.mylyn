/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.internal.gerrit.core.HudsonCommentParser;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SubmitRecord.Label;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommitInfo;
import org.eclipse.mylyn.reviews.internal.core.BuildResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.common.data.ApprovalTypes;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.ChangeMessage;

/**
 * Provides additional fields used by Gerrit 2.2.
 * 
 * @author Steffen Pingel
 */
public class ChangeDetailX extends ChangeDetail {

	/**
	 * Available since Gerrit 2.2.
	 */
	private boolean canRevert;

	/**
	 * Available since Gerrit 2.2.
	 */
	private boolean canSubmit;

	/**
	 * Available since Gerrit 2.4.
	 */
	private boolean canRebase;

	/**
	 * Available since Gerrit 2.8.
	 */
	private boolean canCherryPick;

	/**
	 * Available since Gerrit 2.3.
	 */
	private boolean canDeleteDraft;

	private boolean canEdit;

	private Timestamp createdOn;

	private Timestamp lastUpdatedOn;

	protected List<SubmitRecord> submitRecords;

	private Set<ApprovalType> approvalTypes;

	private Map<Integer, CommitInfo[]> parents;

	private LinkedHashMap<String, BuildResult> patchSetBuildStatuses;

	public boolean canRevert() {
		return canRevert;
	}

	public boolean canSubmit() {
		return canSubmit;
	}

	public boolean canRebase() {
		return canRebase;
	}

	public boolean canCherryPick() {
		return canCherryPick;
	}

	public boolean canEdit() {
		return canEdit;
	}

	public boolean canDeleteDraft() {
		return canDeleteDraft;
	}

	public List<SubmitRecord> getSubmitRecords() {
		return submitRecords;
	}

	public void setSubmitRecords(List<SubmitRecord> submitRecords) {
		this.submitRecords = submitRecords;
	}

	public Set<ApprovalType> getApprovalTypes() {
		return approvalTypes;
	}

	public void setApprovalTypes(Set<ApprovalType> approvalTypes) {
		this.approvalTypes = approvalTypes;
	}

	public Iterable<BuildResult> getPatchSetBuildStatuses() {
		return ImmutableSet.copyOf(this.patchSetBuildStatuses.values());
	}

	public void convertSubmitRecordsToApprovalTypes(ApprovalTypes knownApprovalTypes) {
		if (approvalTypes != null) {
			throw new IllegalStateException();
		}
		if (submitRecords == null) {
			return;
		}
		approvalTypes = new LinkedHashSet<ApprovalType>();
		for (SubmitRecord record : submitRecords) {
			for (Label label : record.getLabels()) {
				ApprovalType approvalType = findTypeByLabel(knownApprovalTypes, label.getLabel());
				if (approvalType == null) {
					// it's a custom approval type, that's all we know about it
					ApprovalCategory approvalCategory = new ApprovalCategory(new ApprovalCategory.Id(null),
							label.getLabel());
					approvalType = new ApprovalType(approvalCategory, Collections.<ApprovalCategoryValue> emptyList());
				}
				approvalTypes.add(approvalType);
			}
		}
	}

	@Override
	public List<ChangeMessage> getMessages() {
		HudsonCommentParser commentParser = new HudsonCommentParser();
		List<ChangeMessage> allMessages = super.getMessages();
		List<ChangeMessage> filteredMessages = new ArrayList<ChangeMessage>();
		// Builds up a map of the results
		LinkedHashMap<String, BuildResult> patchSetBuildResults = new LinkedHashMap<String, BuildResult>();
		for (ChangeMessage message : allMessages) {
			ImmutableList<BuildResult> results = commentParser.getBuildResult(message.getMessage());
			if (results.size() == 0) {
				filteredMessages.add(message);
			} else {
				for (BuildResult result : results) {
					patchSetBuildResults.put(result.getBuildUrl(), result);
				}
			}
		}
		this.patchSetBuildStatuses = patchSetBuildResults;
		return filteredMessages;
	}

	private ApprovalType findTypeByLabel(ApprovalTypes knownApprovalTypes, String label) {

		if (knownApprovalTypes == null || knownApprovalTypes.getApprovalTypes() == null) {
			return null;
		}
		for (ApprovalType type : knownApprovalTypes.getApprovalTypes()) {
			if (type.getCategory().getName().equals(label.replace('-', ' '))) {
				return type;
			}
		}
		return null;
	}

	public void setDateCreated(Timestamp ts) {
		createdOn = ts;
	}

	public void setLastModified(Timestamp ts) {
		lastUpdatedOn = ts;
	}

	public Timestamp getDateCreated() {
		return createdOn;
	}

	public Timestamp getLastModified() {
		return lastUpdatedOn;
	}

	public void setCanSubmit(boolean canSubmit) {
		this.canSubmit = canSubmit;
	}

	public void setCanRebase(boolean canRebase) {
		this.canRebase = canRebase;
	}

	public void setCanCherryPick(boolean canCherryPick) {
		this.canCherryPick = canCherryPick;
	}

	public Map<Integer, CommitInfo[]> getParents() {
		return parents;
	}

	public void setParents(Map<Integer, CommitInfo[]> parents) {
		this.parents = parents;
	}

}
