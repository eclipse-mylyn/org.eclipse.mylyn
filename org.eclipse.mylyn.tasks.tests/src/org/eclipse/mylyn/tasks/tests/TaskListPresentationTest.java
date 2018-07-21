/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten
 */
public class TaskListPresentationTest extends TestCase {

	public void testDefaultPresentations() {

		List<AbstractTaskListPresentation> presentations = TaskListView.getPresentations();
		// depends on whether Sandbox is running
		assertTrue(presentations.size() == 2 || presentations.size() == 7);
	}

}
