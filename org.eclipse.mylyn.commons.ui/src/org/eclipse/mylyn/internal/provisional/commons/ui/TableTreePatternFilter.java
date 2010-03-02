/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.provisional.commons.ui;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

public class TableTreePatternFilter extends PatternFilter {

	@Override
	protected boolean isParentMatch(Viewer viewer, Object element) {
		if (viewer instanceof AbstractTreeViewer) {
			return super.isParentMatch(viewer, element);
		}
		return false;
	}

}
