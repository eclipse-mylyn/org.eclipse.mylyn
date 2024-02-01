/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.context.core.InterestComparator;

/**
 * @author Mik Kersten
 */
public class DoiOrderSorter extends ViewerSorter {
	protected InterestComparator<Object> comparator = new InterestComparator<>();

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return comparator.compare(e1, e2);
	}
}
