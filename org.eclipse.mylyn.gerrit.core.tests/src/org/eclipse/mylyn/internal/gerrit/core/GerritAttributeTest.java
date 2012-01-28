/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Sascha Scholz (SAP) - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
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
