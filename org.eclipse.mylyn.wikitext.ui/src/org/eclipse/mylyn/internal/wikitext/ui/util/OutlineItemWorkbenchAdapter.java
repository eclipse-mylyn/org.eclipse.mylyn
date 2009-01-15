/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * A workbench adapter for {@link OutlineItem}
 * 
 * @author David Green
 * 
 * @see OutlineItemAdapterFactory
 */
public class OutlineItemWorkbenchAdapter implements IWorkbenchAdapter {

	private static final Object[] NO_CHILDREN = new Object[0];

	private static OutlineItemWorkbenchAdapter instance = new OutlineItemWorkbenchAdapter();

	public Object[] getChildren(Object o) {
		if (o instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) o;
			if (!item.getChildren().isEmpty()) {
				return item.getChildren().toArray();
			}
		}
		return NO_CHILDREN;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		// no images yet, see bug 260447
		return null;
	}

	public String getLabel(Object o) {
		if (o instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) o;
			// TODO: bug 260447 remove text prefix when icons become available
			if (item.getKind() == null) {
				return item.getLabel();
			} else {
				return item.getKind() + ". " + item.getLabel(); //$NON-NLS-1$
			}
		}
		return null;
	}

	public Object getParent(Object o) {
		if (o instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) o;
			return item.getParent();
		}
		return null;
	}

	public static OutlineItemWorkbenchAdapter instance() {
		return instance;
	}

}
