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

package org.eclipse.mylyn.tasks.tests.ui;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskAttachmentPropertyTester;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

/**
 * @author Steffen Pingel
 */
public class TaskAttachmentPropertyTesterTest extends TestCase {

	public void testHasUrl() {
		TaskAttachment attachment = new StubAttachment();
		TaskAttachmentPropertyTester tester = new TaskAttachmentPropertyTester();
		attachment.setUrl("url");
		assertTrue(tester.test(attachment, "hasUrl", null, Boolean.TRUE));
		attachment.setUrl("");
		assertTrue(tester.test(attachment, "hasUrl", null, Boolean.FALSE));
		attachment.setUrl(null);
		assertFalse(tester.test(attachment, "hasUrl", null, Boolean.TRUE));
		assertFalse(tester.test(new Object(), "hasUrl", null, Boolean.TRUE));
	}

	private class StubAttachment extends TaskAttachment {

		public StubAttachment() {
			super(TaskTestUtil.createMockRepository(), TaskTestUtil.createMockTask("1"),
					TaskTestUtil.createMockTaskData("1").getRoot());
		}

	}

}
