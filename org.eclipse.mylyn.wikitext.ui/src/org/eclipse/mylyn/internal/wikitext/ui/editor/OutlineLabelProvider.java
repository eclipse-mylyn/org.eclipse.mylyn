/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * 
 * @author David Green
 */
class OutlineLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof OutlineItem) {
			OutlineItem outlineItem = (OutlineItem) element;
			return outlineItem.getKind() + ". " + outlineItem.getLabel(); //$NON-NLS-1$
		}
		return element.toString();
	}

}
