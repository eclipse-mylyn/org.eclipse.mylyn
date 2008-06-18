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
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class AttachmentsTableContentProvider implements IStructuredContentProvider {

	private final List<RepositoryAttachment> attachments;

	public AttachmentsTableContentProvider(List<RepositoryAttachment> attachments) {
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
