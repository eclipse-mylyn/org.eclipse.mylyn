/*******************************************************************************
 * Copyright (c) 2012, 2013 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.reviews.core.model.ICommentContainer;

/**
 * @author Miles Parker
 */
public class NonCommentFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ICommentContainer) {
			ICommentContainer container = (ICommentContainer) element;
			return container.getAllComments().size() > 0;
		}
		return true;
	}
}
