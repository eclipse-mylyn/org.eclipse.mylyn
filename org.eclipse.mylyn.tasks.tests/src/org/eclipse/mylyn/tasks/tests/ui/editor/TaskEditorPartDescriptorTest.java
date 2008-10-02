/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui.editor;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * @author Steffen Pingel
 */
public class TaskEditorPartDescriptorTest extends TestCase {

	private TaskEditorPartDescriptor descriptor1;

	private TaskEditorPartDescriptor descriptor2;

	@Override
	protected void setUp() throws Exception {
		descriptor1 = new TaskEditorPartDescriptor("id") {
			@Override
			public AbstractTaskEditorPart createPart() {
				return null;
			}
		};
		descriptor2 = new TaskEditorPartDescriptor("id") {
			@Override
			public AbstractTaskEditorPart createPart() {
				// ignore
				return null;
			}
		}.setPath(AbstractTaskEditorPage.PATH_ACTIONS);
	}

	public void testEquals() {
		assertEquals(descriptor1, descriptor2);
		assertEquals(descriptor1.hashCode(), descriptor2.hashCode());
	}

	public void testInsertIntoSet() {
		Set<TaskEditorPartDescriptor> set = new LinkedHashSet<TaskEditorPartDescriptor>();
		set.add(descriptor1);
		set.add(descriptor2);
		assertEquals(1, set.size());
		assertSame(descriptor1, set.iterator().next());
	}

}
