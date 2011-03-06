/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;

/**
 * @author Mikael Kober
 */
public class GerritAttributeTest extends TestCase {

	public void testGetters() {
		final Field id = GerritTaskSchema.getDefault().KEY;
		assertTrue("should be read only", id.isReadOnly());
		assertEquals("wrong task key", TaskAttribute.TASK_KEY, id.getKey());
		assertEquals("wrong type", TaskAttribute.TYPE_SHORT_TEXT, id.getType());
	}

}
