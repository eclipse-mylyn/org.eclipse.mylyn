/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.IFontDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.internal.context.core.InteractionContextRelation;
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

	@Override
	public void addListener(ILabelProviderListener listener) {
		// don't care about listeners
	}

	@Override
	public void dispose() {
		// don't care when we are disposed
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// don't care about listeners
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		return null;
	}

	@Override
	public String decorateText(String text, Object element) {
		return null;
	}

	@Override
	public Font decorateFont(Object element) {
		IInteractionElement node = getNode(element);
		if (node != null) {
			if (node.getInterest().isLandmark() && !node.getInterest().isPropagated()) {
				return CommonFonts.BOLD;
			}
		}
		return null;
	}

	@Override
	public Color decorateForeground(Object element) {
		IInteractionElement node = getNode(element);
		if (element instanceof InteractionContextRelation) {
			return ColorMap.RELATIONSHIP;
		} else if (node != null) {
			return ContextUi.getForeground(node);
		}
		return null;
	}

	@Override
	public Color decorateBackground(Object element) {
		return null;
	}
}
