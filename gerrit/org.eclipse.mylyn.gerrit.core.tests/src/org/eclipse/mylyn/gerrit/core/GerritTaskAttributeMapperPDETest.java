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

import org.eclipse.mylyn.internal.gerrit.core.GerritAttribute;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskAttributeMapper;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskDataHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import junit.framework.TestCase;

/**
 * @author Mikael Kober, Sony Ericsson
 *
 */
public class GerritTaskAttributeMapperPDETest extends TestCase {

	public void testMapToRepositoryKeyTaskAttributeString() {
		GerritTaskAttributeMapper mapper = new GerritTaskAttributeMapper(new TaskRepository("gerrit", "http://some.url"));
		TaskAttribute parent = GerritTaskDataHandler.createAttribute(new TaskData(mapper, "gerrit", "http://some.url", "12345"), GerritAttribute.ID);
		assertEquals("wrong mapping", GerritAttribute.SUMMARY.getGerritKey(), mapper.mapToRepositoryKey(parent, TaskAttribute.SUMMARY));
	}

}
