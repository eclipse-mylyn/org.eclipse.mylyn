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

import static org.junit.Assert.assertEquals;

import java.util.SortedSet;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class BranchProposalProviderTest {

	private SortedSet<String> proposals;

	private BranchProposalProvider provider;

	@Before
	public void setUp() {
		setUpProvider("a", "b", "c");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProposalsNullContents() {
		provider.getProposals(null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProposalsNegativeCursorPosition() {
		provider.getProposals("", -1);
	}

	@Test
	public void testGetAllProposalsWhenContentsEmpty() {
		IContentProposal[] branches = provider.getProposals("", 0);
		assertEquals(3, branches.length);
		assertEquals("a", branches[0].getContent());
		assertEquals("b", branches[1].getContent());
		assertEquals("c", branches[2].getContent());
	}

	@Test
	public void testGetAllProposalsInNaturalOrdering() {
		setUpProvider("c", "b", "a");
		IContentProposal[] branches = provider.getProposals("", 0);
		assertEquals(3, branches.length);
		assertEquals("a", branches[0].getContent());
		assertEquals("b", branches[1].getContent());
		assertEquals("c", branches[2].getContent());
	}

	@Test
	public void testGetCorrectProposal() {
		IContentProposal[] branches = provider.getProposals("a", 0);
		assertEquals(1, branches.length);
		assertEquals("a", branches[0].getContent());
	}

	@Test
	public void testGetMatchingProposals() {
		setUpProvider("ac", "ba", "cb");
		IContentProposal[] branches = provider.getProposals("a", 0);
		assertEquals(2, branches.length);
		assertEquals("ac", branches[0].getContent());
		assertEquals("ba", branches[1].getContent());
	}

	@Test
	public void testCursorInCorrectPosition() {
		setUpProvider("a", "ab", "abc");
		IContentProposal[] branches = provider.getProposals("a", 0);
		assertEquals(3, branches.length);
		assertEquals("a", branches[0].getContent());
		assertEquals(1, branches[0].getCursorPosition());
		assertEquals("ab", branches[1].getContent());
		assertEquals(2, branches[1].getCursorPosition());
		assertEquals("abc", branches[2].getContent());
		assertEquals(3, branches[2].getCursorPosition());
	}

	private void setUpProvider(String... branchNames) {
		proposals = Sets.newTreeSet();
		for (String branch : branchNames) {
			proposals.add(branch);
		}
		provider = new BranchProposalProvider(proposals);
	}
}
