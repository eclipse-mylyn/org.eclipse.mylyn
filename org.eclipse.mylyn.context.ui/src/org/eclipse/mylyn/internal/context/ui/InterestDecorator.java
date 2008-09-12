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

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.IFontDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.internal.context.core.InteractionContextRelation;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Not currently used.
 * 
 * @author Mik Kersten
 */
@Deprecated
public class InterestDecorator implements ILabelDecorator, IFontDecorator, IColorDecorator {

	private IInteractionElement getNode(Object element) {
		IInteractionElement node = null;
		if (element instanceof IInteractionElement) {
			node = (IInteractionElement) element;
		} else {
			AbstractContextStructureBridge adapter = ContextCore.getStructureBridge(element);
			node = ContextCore.getContextManager().getElement(adapter.getHandleIdentifier(element));
		}
		return node;
	}

	public void addListener(ILabelProviderListener listener) {
		// don't care about listeners
	}

	public void dispose() {
		// don't care when we are disposed
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// don't care about listeners
	}

	public Image decorateImage(Image image, Object element) {
		return null;
	}

	public String decorateText(String text, Object element) {
		return null;
	}

	public Font decorateFont(Object element) {
		IInteractionElement node = getNode(element);
		if (node != null) {
			if (node.getInterest().isLandmark() && !node.getInterest().isPropagated()) {
				return CommonFonts.BOLD;
			}
		}
		return null;
	}

	public Color decorateForeground(Object element) {
		IInteractionElement node = getNode(element);
		if (element instanceof InteractionContextRelation) {
			return ColorMap.RELATIONSHIP;
		} else if (node != null) {
			return ContextUi.getForeground(node);
		}
		return null;
	}

	public Color decorateBackground(Object element) {
		return null;
	}
}
