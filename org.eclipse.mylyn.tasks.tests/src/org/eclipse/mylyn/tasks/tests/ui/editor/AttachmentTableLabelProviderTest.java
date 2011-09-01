/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui.editor;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnCreated;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnCreator;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnDefinition;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnDescription;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnID;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnName;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnSize;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

/**
 * @author Thomas Ehrnhoefer
 * @author Steffen Pingel
 */
public class AttachmentTableLabelProviderTest extends TestCase {
	private static AttachmentColumnDefinition[] columnDefinitions = { new AttachmentColumnName(0),
			new AttachmentColumnDescription(1), new AttachmentColumnSize(2), new AttachmentColumnCreator(3),
			new AttachmentColumnCreated(4), new AttachmentColumnID(5) };

	public void testGetAttachmentId() {
		TaskAttachment attachment = TaskTestUtil.createMockTaskAttachment("1");
		AttachmentTableLabelProvider labelProvider = new AttachmentTableLabelProvider(null, null, columnDefinitions);

		attachment.setUrl(null);
		assertEquals("", labelProvider.getColumnText(attachment, 5));

		attachment.setUrl("");
		assertEquals("", labelProvider.getColumnText(attachment, 5));

		attachment.setUrl("http://testi.ng");
		assertEquals("", labelProvider.getColumnText(attachment, 5));

		attachment.setUrl("http://testi.ng/?some=parameter");
		assertEquals("", labelProvider.getColumnText(attachment, 5));

		attachment.setUrl("http://testi.ng/?id=myid");
		assertEquals("myid", labelProvider.getColumnText(attachment, 5));
	}

}
