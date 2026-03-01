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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class RepositoryVersionTest {

	@Test
	public void constructWithNullVersion() throws Exception {
		assertThrows(NullPointerException.class, () -> new RepositoryVersion(null));
	}

	@Test
	public void getVersion() {
		assertEquals("1", new RepositoryVersion("1").toString());
	}

}
