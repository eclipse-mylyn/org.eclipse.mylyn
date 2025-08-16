/*********************************************************************
 * Copyright (c) 2010, 2012 Sony Ericsson/ST Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Sascha Scholz (SAP) - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.junit.Test;

/**
 * @author Mikael Kober
 */
public class GerritAttributeTest {

	@Test
	public void testGetters() {
		final Field id = GerritTaskSchema.getDefault().KEY;
		assertTrue(id.isReadOnly());
		assertEquals(TaskAttribute.TASK_KEY, id.getKey());
		assertEquals(TaskAttribute.TYPE_SHORT_TEXT, id.getType());
	}

}
