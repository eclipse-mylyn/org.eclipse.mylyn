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

package org.eclipse.mylyn.internal.commons.repositories.ui;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.commons.repositories.core.RepositoryCategory;

public class RepositoryCategorySorter extends ViewerSorter {

	public RepositoryCategorySorter() {
	}

	public RepositoryCategorySorter(Collator collator) {
		super(collator);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof RepositoryCategory category1 && e2 instanceof RepositoryCategory) {
			RepositoryCategory category2 = (RepositoryCategory) e2;
			int result = category1.getRank() - category2.getRank();
			if (result != 0) {
				return result;
			}
		}
		// fall back to comparing by label
		return super.compare(viewer, e1, e2);
	}

}
