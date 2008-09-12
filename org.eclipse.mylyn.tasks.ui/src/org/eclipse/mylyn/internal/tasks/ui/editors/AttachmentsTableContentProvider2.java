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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;

/**
 * @author Mik Kersten
 */
public class AttachmentsTableContentProvider2 implements IStructuredContentProvider {

	private final List<ITaskAttachment> attachments;

	public AttachmentsTableContentProvider2(List<ITaskAttachment> attachments) {
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
