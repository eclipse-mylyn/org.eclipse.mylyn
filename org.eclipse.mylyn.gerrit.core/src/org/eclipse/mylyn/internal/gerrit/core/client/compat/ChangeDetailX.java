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

import com.google.gerrit.common.data.ChangeDetail;

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
}
