/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.team;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.commons.repositories.RepositoryCategory;

public class RepositoryCategorySorter extends ViewerSorter {

	public RepositoryCategorySorter() {
	}

	public RepositoryCategorySorter(Collator collator) {
		super(collator);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof RepositoryCategory && e2 instanceof RepositoryCategory) {
			RepositoryCategory category1 = (RepositoryCategory) e1;
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
