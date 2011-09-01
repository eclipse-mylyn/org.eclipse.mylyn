/*******************************************************************************
 * Copyright (c) 2011 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnCreated;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnCreator;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnDefinition;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnDescription;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnID;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnName;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnSize;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttachmentPart;

public class BugzillaTaskEditorAttachmentPart extends TaskEditorAttachmentPart {
	@Override
	public AttachmentColumnDefinition[] getColumnDefinitions() {
		return new AttachmentColumnDefinition[] { new AttachmentColumnName(0), new AttachmentColumnDescription(1),
				new AttachmentColumnSize(2), new AttachmentColumnCreator(3), new AttachmentColumnCreated(4),
				new AttachmentColumnID(5), new AttachmentColumnFlags(6) };
	}
}
