/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.core.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.bugzilla.rest.core.RepositoryKey;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;

public class RepositoryKeyTest {

	@Test
	public void testRepositoryKey() throws CoreException {
		RepositoryKey rep1 = new RepositoryKey(new TaskRepository("xx", "url"));
		RepositoryKey rep2 = new RepositoryKey(new TaskRepository("xx1", "url1"));
		RepositoryKey rep3 = new RepositoryKey(new TaskRepository("xx", "url"));
		assertTrue(rep1.equals(rep1));
		assertTrue(rep1.equals(rep3));
		assertFalse(rep1.equals(rep2));
	}

}
