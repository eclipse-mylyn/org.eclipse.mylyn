/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.util.List;

import org.eclipse.jgit.diff.Edit;

import com.google.gerrit.common.data.CommentDetail;
import com.google.gerrit.common.data.PatchScript;
import com.google.gerrit.prettify.common.SparseFileContent;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.Patch.ChangeType;

/**
 * Provides support for binary content.
 */
public class PatchScriptX extends PatchScript {

	private byte[] binaryA;

	private byte[] binaryB;

	public boolean isBinary() {
		for (String header : getPatchHeader()) {
			if (header.contains("Binary files differ")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	public byte[] getBinaryA() {
		return binaryA;
	}

	public void setBinaryA(byte[] binaryA) {
		this.binaryA = binaryA;
	}

	public byte[] getBinaryB() {
		return binaryB;
	}

	public void setBinaryB(byte[] binaryB) {
		this.binaryB = binaryB;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public void setA(SparseFileContent contentA) {
		a = contentA;
	}

	public void setB(SparseFileContent contentB) {
		b = contentB;
	}

	public void setEdits(List<Edit> edits) {
		this.edits = edits;
	}

	/**
	 * @param changeId
	 *            the changeId to set
	 */
	public void setChangeId(Change.Key changeId) {
		this.changeId = changeId;
	}

	/**
	 * @param changeType
	 *            the changeType to set
	 */
	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	/**
	 * @param oldName
	 *            the oldName to set
	 */
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	/**
	 * @param newName
	 *            the newName to set
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}

	/**
	 * @param diffPrefs
	 *            the diffPrefs to set
	 */
	@Override
	public void setDiffPrefs(AccountDiffPreference diffPrefs) {
		this.diffPrefs = diffPrefs;
	}

	/**
	 * @param displayMethodA
	 *            the displayMethodA to set
	 */
	public void setDisplayMethodA(DisplayMethod displayMethodA) {
		this.displayMethodA = displayMethodA;
	}

	/**
	 * @param displayMethodB
	 *            the displayMethodB to set
	 */
	public void setDisplayMethodB(DisplayMethod displayMethodB) {
		this.displayMethodB = displayMethodB;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(CommentDetail comments) {
		this.comments = comments;
	}

	/**
	 * @param history
	 *            the history to set
	 */
	public void setHistory(List<Patch> history) {
		this.history = history;
	}

	/**
	 * @param hugeFile
	 *            the hugeFile to set
	 */
	public void setHugeFile(boolean hugeFile) {
		this.hugeFile = hugeFile;
	}

	/**
	 * @param intralineDifference
	 *            the intralineDifference to set
	 */
	public void setIntralineDifference(boolean intralineDifference) {
		this.intralineDifference = intralineDifference;
	}

}
