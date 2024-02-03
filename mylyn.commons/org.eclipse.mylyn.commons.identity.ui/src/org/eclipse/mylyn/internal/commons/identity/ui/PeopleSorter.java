/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.ui;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.mylyn.commons.identity.core.IIdentity;

/**
 * @author Steffen Pingel
 */
public class PeopleSorter extends ViewerComparator {

	public PeopleSorter() {
	}

	public PeopleSorter(Collator collator) {
		super(collator);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof IIdentity id1 && e2 instanceof IIdentity id2) {
			int result = id1.getAccounts()[0].getId().compareTo(id2.getAccounts()[0].getId());
			if (result != 0) {
				return result;
			}
		}
		// fall back to comparing by label
		return super.compare(viewer, e1, e2);
	}

}
