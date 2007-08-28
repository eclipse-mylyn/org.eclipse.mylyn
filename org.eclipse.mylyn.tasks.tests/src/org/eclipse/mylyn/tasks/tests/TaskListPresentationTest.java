/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.List;

import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class TaskListPresentationTest extends TestCase {

	public void testDefaultPresentations() {
		
		List<AbstractTaskListPresentation> presentations = TaskListView.getPresentations();
		assertEquals(2, presentations.size());
	}
	
}
