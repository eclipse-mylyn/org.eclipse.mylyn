/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.commons.workbench;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Shawn Minto
 * @since 3.7
 */
public class TableTreePatternFilter extends PatternFilter {

	@Override
	protected boolean isParentMatch(Viewer viewer, Object element) {
		if (viewer instanceof AbstractTreeViewer) {
			return super.isParentMatch(viewer, element);
		}
		return false;
	}

}
