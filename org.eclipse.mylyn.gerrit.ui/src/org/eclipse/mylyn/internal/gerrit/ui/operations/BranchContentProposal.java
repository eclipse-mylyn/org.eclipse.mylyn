/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
