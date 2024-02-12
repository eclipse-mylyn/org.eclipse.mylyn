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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.InterestComparator;

/**
 * Sorts view elements by degree-of-interest.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public class InterestSorter extends ViewerComparator {

	protected InterestComparator<Object> comparator = new InterestComparator<>();

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
			AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(object);
			String handle = bridge.getHandleIdentifier(object);
			return ContextCore.getContextManager().getElement(handle);
		}
	}

}
