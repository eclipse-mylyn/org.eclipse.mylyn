/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.provisional.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.InterestComparator;
import org.eclipse.mylar.provisional.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class InterestSorter extends ViewerSorter {

	protected InterestComparator<Object> comparator = new InterestComparator<Object>();

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		IMylarElement element1 = getCorresponding(e1);
		IMylarElement element2 = getCorresponding(e2);
		if (element1 instanceof IMylarElement && element2 instanceof IMylarElement) {
			return comparator.compare(element1, element2);
		} else {
			return 0;
		}
	} 
 
	private IMylarElement getCorresponding(Object object) {
		if (object instanceof IMylarElement) {
			return(IMylarElement) object;
		} else {
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(object);
			String handle = bridge.getHandleIdentifier(object);
			return MylarPlugin.getContextManager().getElement(handle);
		}
	}
	
}
