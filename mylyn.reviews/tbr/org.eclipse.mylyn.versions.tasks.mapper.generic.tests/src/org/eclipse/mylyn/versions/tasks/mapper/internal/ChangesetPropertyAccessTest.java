package org.eclipse.mylyn.versions.tasks.mapper.internal;
/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Kilian Matt
 *
 */
public class ChangesetPropertyAccessTest {

	private ChangeSet cs;
	final String expectedId = "123";
	final String expectedMessage = "Sample Message";
	final String expectedRepository = "git://git.eclipse.org/c/mylyn/org.eclipse.versions.git";

	@Before
	public void prepare() {
		cs = new ChangeSet(null, new Date(), expectedId, expectedMessage, new ScmRepository(null, "test", expectedRepository),
				new ArrayList<Change>());
	}

	@Test
	public void testRevisionAccess() {
		assertEquals(expectedId, ChangesetPropertyAccess.REVISION.getValue(cs));
	}

	@Test
	public void testCommitMessageAccess() {
		assertEquals(expectedMessage,
				ChangesetPropertyAccess.COMMIT_MESSAGE.getValue(cs));
	}

	@Test
	public void testRepositoryAccess() {
		assertEquals(expectedRepository,
				ChangesetPropertyAccess.REPOSITORY.getValue(cs));
	}

}
