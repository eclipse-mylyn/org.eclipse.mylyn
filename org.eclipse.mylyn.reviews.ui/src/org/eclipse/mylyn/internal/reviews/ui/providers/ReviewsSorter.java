/*******************************************************************************
 * Copyright (c) 2012 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import java.util.Date;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IIndexed;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;

/**
 * Default ordering for review items. Order is in general:
 * <ol>
 * <li>Global Comments</i>
 * <li>Patch Sets, ReviewItemSets, other containers</li>
 * <li>Dated Items by Change Date (Includes e.g. patch sets, artifacts, etc..)</li>
 * <li>Orderable Items (Locations, Comments without dates)</li>
 * <li>Other Review Items (Files, by full path name)</li>
 * </ol>
 * 
 * @author Miles Parker
 */
public class ReviewsSorter extends ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof GlobalCommentsNode) {
			return -1;
		}
		if (e2 instanceof GlobalCommentsNode) {
			return 1;
		}
		Date d1 = null;
		if (e1 instanceof IDated) {
			IDated ed1 = (IDated) e1;
			d1 = ed1.getModificationDate();
			if (d1 == null) {
				d1 = ed1.getCreationDate();
			}
		}
		Date d2 = null;
		if (e2 instanceof IDated) {
			IDated ed2 = (IDated) e2;
			d2 = ed2.getModificationDate();
			if (d2 == null) {
				d2 = ed2.getCreationDate();
			}
		}

		if (d1 != null && d2 != null) {
			return d1.compareTo(d2);
		}

		if (e1 instanceof IIndexed && e2 instanceof IIndexed) {
			return IIndexed.COMPARATOR.compare((IIndexed) e1, (IIndexed) e2);
		}

		if (d1 != null) {
			return -1;
		}
		if (d2 != null) {
			return 1;
		}
		if (e1 instanceof IIndexed) {
			return -1;
		}
		if (e2 instanceof IIndexed) {
			return 1;
		}

		//We want to use full path, not the shortened name in UI.
		if (e1 instanceof IReviewItem && e2 instanceof IReviewItem) {
			return super.compare(viewer, ((IReviewItem) e1).getName(), ((IReviewItem) e2).getName());
		}
		return super.compare(viewer, e1, e2);
	}

	@Override
	public int category(Object element) {
		if (element instanceof IIndexed) {
			return 1;
		}
		if (element instanceof IDated) {
			return 2;
		}
		return super.category(element);
	}
}
