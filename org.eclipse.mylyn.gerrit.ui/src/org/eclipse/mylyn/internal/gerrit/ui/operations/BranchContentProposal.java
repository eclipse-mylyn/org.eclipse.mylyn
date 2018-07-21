/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.operations;

import org.eclipse.jface.fieldassist.IContentProposal;

public class BranchContentProposal implements IContentProposal, Comparable<BranchContentProposal> {

	private final int cursorPosition;

	private final String branch;

	public BranchContentProposal(String branch) {
		this.branch = branch;
		this.cursorPosition = branch.length();
	}

	@Override
	public int compareTo(BranchContentProposal otherBranchProposal) {
		return this.branch.compareTo(otherBranchProposal.getContent());
	}

	@Override
	public String getContent() {
		return branch;
	}

	@Override
	public int getCursorPosition() {
		return cursorPosition;
	}

	@Override
	public String getLabel() {
		return branch;
	}

	@Override
	public String getDescription() {
		return null;
	}

}
