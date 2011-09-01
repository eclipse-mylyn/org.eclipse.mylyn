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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.SWT;

public class AttachmentColumnCreated extends AttachmentColumnDefinition {

	public AttachmentColumnCreated(int index) {
		super(index, 100, "Created", SWT.LEFT, true, SWT.DOWN);
	}

	@Override
	public String getColumnText(ITaskAttachment attachment, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		return (attachment.getCreationDate() != null) ? EditorUtil.formatDateTime(attachment.getCreationDate()) : ""; //$NON-NLS-1$
	}

	@Override
	public int compare(TableViewer viewer, ITaskAttachment attachment1, ITaskAttachment attachment2, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		return compare(attachment1.getCreationDate(), attachment2.getCreationDate());
	}

}
