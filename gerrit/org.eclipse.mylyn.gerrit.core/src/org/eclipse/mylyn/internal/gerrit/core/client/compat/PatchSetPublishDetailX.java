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

import java.util.List;

import com.google.gerrit.reviewdb.PatchSetApproval;

/**
 * Provides additional fields used by Gerrit 2.2.
 * 
 * @author Steffen Pingel
 */
public class PatchSetPublishDetailX extends com.google.gerrit.common.data.PatchSetPublishDetail {

	protected boolean canSubmit;

	protected List<PatchSetApproval> given;

	// Gerrit 2.2: mirrors the content of given to avoid a conflict with PatchSetPublishDetail.given
	protected List<PatchSetApproval> given2;

	protected List<PermissionLabel> labels;

	public boolean canSubmit() {
		return canSubmit;
	}

	public List<PatchSetApproval> getGiven2() {
		return given2;
	}

	public void fixFields() {
		this.given2 = this.given;
	}

	public List<PermissionLabel> getLabels() {
		return labels;
	}

}
