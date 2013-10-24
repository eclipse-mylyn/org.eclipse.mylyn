/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.internal.gerrit.core.client.compat.SubmitRecord.Label;

import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.common.data.ApprovalTypes;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;

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
	 * Available since Gerrit 2.3.
	 */
	private boolean canDeleteDraft;

	private boolean canEdit;

	protected List<SubmitRecord> submitRecords;

	private Set<ApprovalType> approvalTypes;

	public boolean canRevert() {
		return canRevert;
	}

	public boolean canSubmit() {
		return canSubmit;
	}

	public boolean canRebase() {
		return canRebase;
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

	public void convertSubmitRecordsToApprovalTypes(ApprovalTypes knownApprovalTypes) {
		if (approvalTypes != null) {
			throw new IllegalStateException();
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
}
