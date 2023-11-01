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

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class BranchProposalProvider implements IContentProposalProvider {

	private final SortedSet<String> proposals;

	public BranchProposalProvider(SortedSet<String> proposals) {
		this.proposals = new TreeSet<>(proposals);
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		Assert.isLegal(contents != null);
		Assert.isLegal(position >= 0);

		SortedSet<BranchContentProposal> branches = new TreeSet<>();
		String searchText = contents.toLowerCase();
		addMatchingProposals(branches, searchText);

		return branches.toArray(new BranchContentProposal[0]);
	}

	private void addMatchingProposals(SortedSet<BranchContentProposal> branches, String searchText) {
		for (String branchProposal : proposals) {
			if (branchProposal.toLowerCase().contains(searchText)) {
				branches.add(new BranchContentProposal(branchProposal));
			}
		}
	}

}
