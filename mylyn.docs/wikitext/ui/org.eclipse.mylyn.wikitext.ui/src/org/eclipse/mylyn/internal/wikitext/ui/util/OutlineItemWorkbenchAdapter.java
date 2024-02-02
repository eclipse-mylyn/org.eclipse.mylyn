/*******************************************************************************
 * Copyright (c) 2009, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * A workbench adapter for {@link OutlineItem}
 *
 * @author David Green
 * @see OutlineItemAdapterFactory
 */
public class OutlineItemWorkbenchAdapter implements IWorkbenchAdapter {

	private static final Object[] NO_CHILDREN = {};

	private static OutlineItemWorkbenchAdapter instance = new OutlineItemWorkbenchAdapter();

	@Override
	public Object[] getChildren(Object o) {
		if (o instanceof OutlineItem item) {
			if (!item.getChildren().isEmpty()) {
				return item.getChildren().toArray();
			}
		}
		return NO_CHILDREN;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// no images yet, see bug 260447
		return null;
	}

	@Override
	public String getLabel(Object o) {
		if (o instanceof OutlineItem item) {
			// TODO: bug 260447 remove text prefix when icons become available
			if (item.getKind() == null) {
				return item.getLabel();
			} else {
				return item.getKind() + ". " + item.getLabel(); //$NON-NLS-1$
			}
		}
		return null;
	}

	@Override
	public Object getParent(Object o) {
		if (o instanceof OutlineItem item) {
			return item.getParent();
		}
		return null;
	}

	public static OutlineItemWorkbenchAdapter instance() {
		return instance;
	}

}
