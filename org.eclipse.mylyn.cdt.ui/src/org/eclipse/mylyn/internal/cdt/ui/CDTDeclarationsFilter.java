/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui;

import org.eclipse.cdt.core.model.IFunction;
import org.eclipse.cdt.core.model.IMethod;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class CDTDeclarationsFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		return !((element instanceof IMethod || element instanceof IFunction));
	}

}
