/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.mylyn.internal.tasks.ui.OptionsProposalProvider;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class OptionsProposalProviderTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
	}

	@Test
	public void testEmptyProposals() {
		OptionsProposalProvider provider = new OptionsProposalProvider(new HashMap<String, String>(), true);
		assertEquals(0, provider.getProposals("", 0).length);
		assertEquals(0, provider.getProposals("", 10).length);
		assertEquals(0, provider.getProposals("test", 0).length);
	}

	@Test
	public void testSingleProposal() {
		final Map<String, String> proposalMap = ImmutableMap.of("aTest", "1");
		OptionsProposalProvider provider = new OptionsProposalProvider(proposalMap, true);

		List<IContentProposal> proposals = Arrays.asList(provider.getProposals("", 0));
		assertEquals(1, proposals.size());
		assertProposal("aTest", "aTest", proposals.get(0));
	}

	@Test
	public void testMultipleProposals() {
		final Map<String, String> proposalMap = ImmutableMap.of("aTest", "1", "bTest", "2", "cTest", "3");
		OptionsProposalProvider provider = new OptionsProposalProvider(proposalMap, true);

		List<IContentProposal> proposals = Arrays.asList(provider.getProposals("", 0));
		assertEquals(3, proposals.size());
		assertProposal("aTest", "aTest", proposals.get(0));
		assertProposal("bTest", "bTest", proposals.get(1));
		assertProposal("cTest", "cTest", proposals.get(2));
	}

	@Test
	public void testMultipleProposalsSorted() {
		final Map<String, String> proposalMap = ImmutableMap.of("oneTest", "1", "twoTest", "2", "threeTest", "3");
		OptionsProposalProvider provider = new OptionsProposalProvider(proposalMap, true);

		List<IContentProposal> proposals = Arrays.asList(provider.getProposals("", 0));
		assertEquals(3, proposals.size());
		// NOTE: Sorted alphabetically by contents
		assertProposal("oneTest", "oneTest", proposals.get(0));
		assertProposal("threeTest", "threeTest", proposals.get(1));
		assertProposal("twoTest", "twoTest", proposals.get(2));
	}

	@Test
	public void testProposalFilterMultiSelect() {
		final Map<String, String> proposalMap = ImmutableMap.of("OneTest", "1", "TwoTest", "2", "ThreeTest", "3");
		OptionsProposalProvider provider = new OptionsProposalProvider(proposalMap, true);

		assertEquals(0, provider.getProposals("ThreeTest", 0).length);

		List<IContentProposal> proposals = Arrays.asList(provider.getProposals("ThreeTest,", 0));
		assertEquals(2, proposals.size());
		assertProposal("ThreeTest,OneTest", "OneTest", proposals.get(0));
		assertProposal("ThreeTest,TwoTest", "TwoTest", proposals.get(1));

		proposals = Arrays.asList(provider.getProposals("ThreeTest, ", 0));
		assertEquals(2, proposals.size());
		assertProposal("ThreeTest, OneTest", "OneTest", proposals.get(0));
		assertProposal("ThreeTest, TwoTest", "TwoTest", proposals.get(1));

		proposals = Arrays.asList(provider.getProposals("o", 0));
		assertEquals(2, proposals.size());
		assertProposal("OneTest", "OneTest", proposals.get(0));
		assertProposal("TwoTest", "TwoTest", proposals.get(1));

		proposals = Arrays.asList(provider.getProposals("O", 0));
		assertEquals(2, proposals.size());
		assertProposal("OneTest", "OneTest", proposals.get(0));
		assertProposal("TwoTest", "TwoTest", proposals.get(1));

		proposals = Arrays.asList(provider.getProposals("one", 0));
		assertEquals(1, proposals.size());
		assertProposal("OneTest", "OneTest", proposals.get(0));

		assertEquals(0, provider.getProposals("four", 0).length);

		proposals = Arrays.asList(provider.getProposals("four,", 0));
		assertEquals(3, proposals.size());
		assertProposal("four,OneTest", "OneTest", proposals.get(0));
		assertProposal("four,ThreeTest", "ThreeTest", proposals.get(1));
		assertProposal("four,TwoTest", "TwoTest", proposals.get(2));

		proposals = Arrays.asList(provider.getProposals("four,   ", 0));
		assertEquals(3, proposals.size());
		assertProposal("four,   OneTest", "OneTest", proposals.get(0));
		assertProposal("four,   ThreeTest", "ThreeTest", proposals.get(1));
		assertProposal("four,   TwoTest", "TwoTest", proposals.get(2));

		proposals = Arrays.asList(provider.getProposals(",,           ,four,        five     ,   ", 0));
		assertEquals(3, proposals.size());
		assertProposal(",,           ,four,        five     ,   OneTest", "OneTest", proposals.get(0));
		assertProposal(",,           ,four,        five     ,   ThreeTest", "ThreeTest", proposals.get(1));
		assertProposal(",,           ,four,        five     ,   TwoTest", "TwoTest", proposals.get(2));

		proposals = Arrays.asList(provider.getProposals(",,           ,four,        five     ,   one", 0));
		assertEquals(1, proposals.size());
		assertProposal(",,           ,four,        five     ,   OneTest", "OneTest", proposals.get(0));
	}

	@Test
	public void testProposalFilterSingleSelect() {
		final Map<String, String> proposalMap = ImmutableMap.of("OneTest", "1", "TwoTest", "2", "ThreeTest", "3");
		OptionsProposalProvider provider = new OptionsProposalProvider(proposalMap, false);

		List<IContentProposal> proposals = Arrays.asList(provider.getProposals("ThreeTest", 0));
		assertEquals(1, proposals.size());
		assertProposal("ThreeTest", "ThreeTest", proposals.get(0));

		assertEquals(0, provider.getProposals("ThreeTest,", 0).length);
		assertEquals(0, provider.getProposals("ThreeTest, ", 0).length);

		proposals = Arrays.asList(provider.getProposals("o", 0));
		assertEquals(2, proposals.size());
		assertProposal("OneTest", "OneTest", proposals.get(0));
		assertProposal("TwoTest", "TwoTest", proposals.get(1));

		proposals = Arrays.asList(provider.getProposals("O", 0));
		assertEquals(2, proposals.size());
		assertProposal("OneTest", "OneTest", proposals.get(0));
		assertProposal("TwoTest", "TwoTest", proposals.get(1));

		proposals = Arrays.asList(provider.getProposals("one", 0));
		assertEquals(1, proposals.size());
		assertProposal("OneTest", "OneTest", proposals.get(0));

		assertEquals(0, provider.getProposals("four", 0).length);
		assertEquals(0, provider.getProposals("four,", 0).length);
		assertEquals(0, provider.getProposals("four,   ", 0).length);
		assertEquals(0, provider.getProposals(",,           ,four,        five     ,   ", 0).length);
		assertEquals(0, provider.getProposals(",,           ,four,        five     ,   one", 0).length);
	}

	private void assertProposal(String content, String label, IContentProposal proposal) {
		assertEquals(content, proposal.getContent());
		assertEquals(label, proposal.getLabel());
		assertNull(proposal.getDescription());
	}
}
