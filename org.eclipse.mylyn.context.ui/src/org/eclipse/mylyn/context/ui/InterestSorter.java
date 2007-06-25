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

package org.eclipse.mylyn.context.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.InterestComparator;
import org.eclipse.mylyn.context.core.ContextCorePlugin;

/**
 * Sorts view elements by degree-of-interest.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public class InterestSorter extends ViewerSorter {

	protected InterestComparator<Object> comparator = new InterestComparator<Object>();

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		IInteractionElement element1 = getCorresponding(e1);
		IInteractionElement element2 = getCorresponding(e2);
		if (element1 != null && element2 != null) {
			return comparator.compare(element1, element2);
		} else {
			return 0;
		}
	}

	private IInteractionElement getCorresponding(Object object) {
		if (object instanceof IInteractionElement) {
			return (IInteractionElement) object;
		} else {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(object);
			String handle = bridge.getHandleIdentifier(object);
			return ContextCorePlugin.getContextManager().getElement(handle);
		}
	}

}
