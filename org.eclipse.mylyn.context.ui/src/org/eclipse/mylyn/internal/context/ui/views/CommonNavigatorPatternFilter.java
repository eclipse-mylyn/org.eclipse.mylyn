/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.ui.TaskListPatternFilter;

/**
 * @author Mik Kersten
 */
public class CommonNavigatorPatternFilter extends TaskListPatternFilter {
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		return super.isLeafMatch(viewer, element);
	}
}
