/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.ITaskAttachment2;

/**
 * @author Mik Kersten
 */
public class AttachmentsTableContentProvider2 implements IStructuredContentProvider {

	private final List<ITaskAttachment2> attachments;

	public AttachmentsTableContentProvider2(List<ITaskAttachment2> attachments) {
		this.attachments = attachments;
	}

	public Object[] getElements(Object inputElement) {
		return attachments.toArray();
	}

	public void dispose() {
		// ignore
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (!viewer.getControl().isDisposed()) {
			viewer.refresh();
		}
	}
}
