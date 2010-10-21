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

import org.eclipse.mylyn.gerrit.core.GerritAttribute;
import org.eclipse.mylyn.gerrit.core.GerritConstants;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

import junit.framework.TestCase;

/**
 * Testclass for GerritAttribute.
 * @author Mikael Kober, Sony Ericsson
 *
 */
public class GerritAttributeTest extends TestCase {

	/**
	 * tests some getters.
	 */
	public void testGetters() {
		final GerritAttribute id = GerritAttribute.ID;
		assertTrue("should be read only",id.isReadOnly());
		assertEquals("wrong gerrit key", GerritConstants.ATTRIBUTE_ID, id.getGerritKey());
		assertEquals("wrong kind", TaskAttribute.KIND_DEFAULT, id.getKind());
		assertEquals("wrong task key", TaskAttribute.TASK_KEY, id.getTaskKey());
		assertEquals("wrong type", TaskAttribute.TYPE_SHORT_TEXT, id.getType());
	}

}
