/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesSorter;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class TaskRepositorySorterTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCategorySorting() {
		TaskRepositoriesSorter sorter = new TaskRepositoriesSorter();
		TaskRepository t1 = new TaskRepository("t1", "http://a");
		TaskRepository t2 = new TaskRepository("t2", "http://a");
		TaskRepository t3 = new TaskRepository("t3", "http://a");
		assertTrue(sorter.compare(null, t1, t2) < 0);
		assertTrue(sorter.compare(null, t3, t1) > 0);
	}

	public void testUrlSorting() {
		TaskRepositoriesSorter sorter = new TaskRepositoriesSorter();
		TaskRepository t1 = new TaskRepository("t", "http://a");
		TaskRepository t2 = new TaskRepository("t", "http://b");
		TaskRepository t3 = new TaskRepository("t", "http://c");
		assertTrue(sorter.compare(null, t1, t2) < 0);
		assertTrue(sorter.compare(null, t3, t1) > 0);
	}
	
	public void testLabelSorting() {
		TaskRepositoriesSorter sorter = new TaskRepositoriesSorter();
		TaskRepository t1 = new TaskRepository("kind", "http://a");
		t1.setProperty(IRepositoryConstants.PROPERTY_LABEL, "a");
		TaskRepository t2 = new TaskRepository("kind", "http://a");
		t2.setProperty(IRepositoryConstants.PROPERTY_LABEL, "b");
		TaskRepository t3 = new TaskRepository("kind", "http://a");
		t3.setProperty(IRepositoryConstants.PROPERTY_LABEL, "c");
		
		assertTrue(sorter.compare(null, t1, t2) < 0);
		assertTrue(sorter.compare(null, t3, t1) > 0);
	}

	public void testLabelVsNoLabel() {
		TaskRepositoriesSorter sorter = new TaskRepositoriesSorter();
		TaskRepository t1 = new TaskRepository("kind", "http://a");
		t1.setProperty(IRepositoryConstants.PROPERTY_LABEL, "a");
		TaskRepository t2 = new TaskRepository("kind", "http://a");
		
		assertTrue(sorter.compare(null, t1, t2) < 0);
	}

}
