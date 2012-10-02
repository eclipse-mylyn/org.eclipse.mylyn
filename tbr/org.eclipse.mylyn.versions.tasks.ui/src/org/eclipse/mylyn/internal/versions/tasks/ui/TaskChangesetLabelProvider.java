/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.versions.tasks.ui;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.versions.tasks.core.TaskChangeSet;
import org.eclipse.swt.graphics.Image;

/**
 * @author Kilian Matt
 */
public class TaskChangesetLabelProvider implements ITableLabelProvider {
	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		TaskChangeSet cs = ((TaskChangeSet) element);
		switch (columnIndex) {
		case 0:
			return cs.getChangeset().getId();
		case 1:
			return cs.getChangeset().getMessage();
		case 2:
			return cs.getChangeset().getAuthor().getEmail();
		case 3:
			return cs.getChangeset().getDate().toString();
		}
		return element.toString() + " " + columnIndex;
	}
}
