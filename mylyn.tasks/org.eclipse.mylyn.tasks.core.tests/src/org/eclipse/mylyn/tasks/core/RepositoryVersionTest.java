/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RepositoryVersionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void constructWithNullVersion() throws Exception {
		thrown.expect(NullPointerException.class);
		new RepositoryVersion(null);
	}

	@Test
	public void getVersion() {
		assertEquals("1", new RepositoryVersion("1").toString());
	}

}
