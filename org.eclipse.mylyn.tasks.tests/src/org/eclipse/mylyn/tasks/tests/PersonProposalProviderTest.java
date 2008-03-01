/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryTask;

public class PersonProposalProviderTest extends TestCase {

	public void testGetProposalsNullParameters() {
		PersonProposalProvider provider = new PersonProposalProvider(null, null);
		IContentProposal[] result = provider.getProposals("", 0);
		assertNotNull(result);
		assertEquals(0, result.length);

		result = provider.getProposals(" ", 1);
		assertNotNull(result);
		assertEquals(0, result.length);
	}

	public void testGetProposalsCurrentTask() {
		MockRepositoryTask task = new MockRepositoryTask(null, "1", null);
		task.setOwner("foo");
		PersonProposalProvider provider = new PersonProposalProvider(task, null);
		IContentProposal[] result = provider.getProposals("", 0);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo", result[0].getContent());

		result = provider.getProposals("a", 1);
		assertNotNull(result);
		assertEquals(0, result.length);

		result = provider.getProposals("fo", 2);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo", result[0].getContent());

		result = provider.getProposals("", 0);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo", result[0].getContent());
	}

}
