/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui.editor;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

import junit.framework.TestCase;

/**
 * @author Thomas Ehrnhoefer
 * @author Steffen Pingel
 */
public class AttachmentTableLabelProviderTest extends TestCase {

	public void testGetAttachmentId() {
		TaskAttachment attachment = TaskTestUtil.createMockTaskAttachment("1");
		AttachmentTableLabelProvider labelProvider = new AttachmentTableLabelProvider();

		attachment.setUrl(null);
		assertEquals("", labelProvider.buildTextFromEventIndex(5, attachment).getString());

		attachment.setUrl("");
		assertEquals("", labelProvider.buildTextFromEventIndex(5, attachment).getString());

		attachment.setUrl("http://testi.ng");
		assertEquals("", labelProvider.buildTextFromEventIndex(5, attachment).getString());

		attachment.setUrl("http://testi.ng/?some=parameter");
		assertEquals("", labelProvider.buildTextFromEventIndex(5, attachment).getString());

		attachment.setUrl("http://testi.ng/?id=myid");
		assertEquals("myid", labelProvider.buildTextFromEventIndex(5, attachment).getString());
	}

	public void testGetAttachmentDescription() throws Exception {
		TaskAttachment attachment = TaskTestUtil.createMockTaskAttachment("1");
		attachment.setDescription(null);
		AttachmentTableLabelProvider labelProvider = new AttachmentTableLabelProvider();
		StyledString styledString = labelProvider.buildTextFromEventIndex(1, attachment);
		assertEquals("", styledString.getString());

		attachment.setDescription("test");
		styledString = labelProvider.buildTextFromEventIndex(1, attachment);
		assertEquals("test", styledString.getString());
	}
}
