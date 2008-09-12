/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.Comparator;

/**
 * Ranks elements by their degree-of-interest.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public class InterestComparator<T> implements Comparator<T> {

	public int compare(T e1, T e2) {
		if (e1 instanceof IInteractionElement && e2 instanceof IInteractionElement) {
			IInteractionElement info1 = (IInteractionElement) e1;
			IInteractionElement info2 = (IInteractionElement) e2;
			float v1 = info1.getInterest().getValue();
			float v2 = info2.getInterest().getValue();
			if (v1 >= v2) {
				return -1;
			}
			if (v1 < v2) {
				return 1;
			}
		}
		return 0;
	}
}
