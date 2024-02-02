/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 */
public class GerritChange {

	private ChangeDetailX changeDetail;

	private List<PatchSetDetail> patchSetDetails;

	private Map<PatchSet.Id, PatchSetPublishDetailX> publishDetailByPatchSetId;

	public ChangeDetailX getChangeDetail() {
		return changeDetail;
	}

	public List<PatchSetDetail> getPatchSetDetails() {
		return patchSetDetails;
	}

	public Map<PatchSet.Id, PatchSetPublishDetailX> getPublishDetailByPatchSetId() {
		return publishDetailByPatchSetId;
	}

	void setChangeDetail(ChangeDetailX changeDetail) {
		this.changeDetail = changeDetail;
	}

	void setPatchSets(List<PatchSetDetail> patchSets) {
		patchSetDetails = patchSets;
	}

	void setPatchSetPublishDetailByPatchSetId(
			Map<PatchSet.Id, PatchSetPublishDetailX> patchSetPublishDetailByPatchSetId) {
		publishDetailByPatchSetId = patchSetPublishDetailByPatchSetId;
	}

}
