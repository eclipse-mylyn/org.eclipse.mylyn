/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.index.tests.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.mylyn.commons.core.DelegatingProgressMonitor;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.index.tests.AbstractTaskListIndexTest;
import org.eclipse.mylyn.internal.tasks.index.ui.AbstractIndexReference;
import org.eclipse.mylyn.internal.tasks.index.ui.ContentProposalProvider;
import org.eclipse.mylyn.internal.tasks.index.ui.ContentProposalProvider.ProposalContentState;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link ContentProposalProvider}
 * 
 * @author David Green
 */
public class ContentProposalProviderTest extends AbstractTaskListIndexTest {

	private ContentProposalProvider proposalProvider;

	@Override
	@Before
	public void setup() throws IOException {
		super.setup();

		setupIndex();

		proposalProvider = new ContentProposalProvider(context.getTaskList(), new AbstractIndexReference() {

			@Override
			public TaskListIndex index() {
				return index;
			}
		});
	}

	@Test
	public void testComputeProposalContentState_SimpleProposal() {
		ProposalContentState state = proposalProvider.computeProposalContentState("one tw", 6);
		assertNotNull(state);
		assertEquals("one ", state.beforePrefixContent);
		assertEquals("", state.fieldPrefix);
		assertEquals("tw", state.prefix);
		assertEquals("", state.suffix);
	}

	@Test
	public void testComputeProposalContentState_FieldValueProposal() {
		String content = "one person:";
		ProposalContentState state = proposalProvider.computeProposalContentState(content, content.length());
		assertNotNull(state);
		assertEquals("one person:", state.beforePrefixContent);
		assertEquals("person", state.fieldPrefix);
		assertEquals("", state.prefix);
		assertEquals("", state.suffix);
	}

	@Test
	public void testComputeProposalContentState_FieldValueProposalMidRange() {
		String content = "one person:";
		String content2 = " foo";
		ProposalContentState state = proposalProvider.computeProposalContentState(content + content2, content.length());
		assertNotNull(state);
		assertEquals("one person:", state.beforePrefixContent);
		assertEquals("person", state.fieldPrefix);
		assertEquals("", state.prefix);
		assertEquals(" foo", state.suffix);
	}

	@Test
	public void testComputeProposalContentState_SimpleProposalMidRange() {
		String content = "one tw";
		String content2 = " foo";
		ProposalContentState state = proposalProvider.computeProposalContentState(content + content2, content.length());
		assertNotNull(state);
		assertEquals("one ", state.beforePrefixContent);
		assertEquals("", state.fieldPrefix);
		assertEquals("tw", state.prefix);
		assertEquals(" foo", state.suffix);
	}

	@Test
	public void testGetProposals_FieldName() {
		IContentProposal[] proposals = proposalProvider.getProposals("per", 3);
		assertNotNull(proposals);
		assertEquals(1, proposals.length);
		assertEquals("person:", proposals[0].getContent());
		assertEquals(7, proposals[0].getCursorPosition());
	}

	@Test
	public void testGetProposals_PersonSuggest() throws CoreException {
		ITask task = context.createRepositoryTask();
		String owner = task.getOwner();
		assertNotNull(owner);
		assertTrue(owner.trim().length() > 0);
		assertEquals(owner.trim(), owner);

		IContentProposal[] proposals = proposalProvider.getProposals("person:", 7);
		assertNotNull(proposals);
		assertEquals(1, proposals.length);
		final String expectedValue = "person:" + owner;
		assertEquals(expectedValue, proposals[0].getContent());
		assertEquals(expectedValue.length(), proposals[0].getCursorPosition());
		assertEquals(owner, proposals[0].getLabel());
	}

	@Test
	public void testGetProposals_PersonSuggestWithSpace() throws CoreException, InterruptedException {
		ITask task = context.createRepositoryTask();

		TaskData taskData = context.getDataManager().getTaskData(task);

		// sanity
		assertNotNull(taskData);
		assertNotNull(taskData.getRepositoryUrl());

		// setup owner with space
		final String ownerName = "Jane Doe";
		taskData.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED).setValue(ownerName);

		context.getDataManager().putSubmittedTaskData(task, taskData, new DelegatingProgressMonitor());

		context.getTaskList().notifyElementsChanged(Collections.singleton(task));

		index.waitUntilIdle();

		String owner = task.getOwner();
		assertNotNull(owner);
		assertTrue(owner.trim().length() > 0);
		assertEquals(owner.trim(), owner);
		assertEquals(ownerName, owner);

		IContentProposal[] proposals = proposalProvider.getProposals("person:", 7);
		assertNotNull(proposals);
		assertEquals(1, proposals.length);
		final String expectedValue = "person:\"" + owner + "\"";
		assertEquals(expectedValue, proposals[0].getContent());
		assertEquals(expectedValue.length(), proposals[0].getCursorPosition());
		assertEquals(owner, proposals[0].getLabel());
	}

	@Test
	public void testGetProposals_PersonSuggest_MidStream() throws CoreException {
		ITask task = context.createRepositoryTask();
		String owner = task.getOwner();
		assertNotNull(owner);
		assertTrue(owner.trim().length() > 0);
		assertEquals(owner.trim(), owner);

		final String content = "person:";
		String content2 = " after";
		IContentProposal[] proposals = proposalProvider.getProposals(content + content2, content.length());
		assertNotNull(proposals);
		assertEquals(1, proposals.length);
		final String expectedValue = content + owner + content2;
		assertEquals(expectedValue, proposals[0].getContent());
		assertEquals((content + owner).length(), proposals[0].getCursorPosition());
		assertEquals(owner, proposals[0].getLabel());
	}
}
