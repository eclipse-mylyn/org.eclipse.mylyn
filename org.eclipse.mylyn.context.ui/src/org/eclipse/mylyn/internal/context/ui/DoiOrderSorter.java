/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.context.core.InterestComparator;

/**
 * @author Mik Kersten
 */
public class DoiOrderSorter extends ViewerSorter {
	protected InterestComparator<Object> comparator = new InterestComparator<Object>();

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return comparator.compare(e1, e2);
	}
}
