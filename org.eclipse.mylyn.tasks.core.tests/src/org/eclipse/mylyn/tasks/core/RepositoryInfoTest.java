/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RepositoryInfoTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void constructWitNullVersion() throws Exception {
		thrown.expect(NullPointerException.class);
		new RepositoryInfo(null);
	}

	@Test
	public void getVersion() {
		RepositoryVersion version = new RepositoryVersion("1");
		assertSame(version, new RepositoryInfo(version).getVersion());
	}

}
