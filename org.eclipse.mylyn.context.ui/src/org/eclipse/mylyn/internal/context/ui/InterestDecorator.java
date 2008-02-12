/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.IFontDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextRelation;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Not currently used.
 * 
 * @author Mik Kersten
 */
public class InterestDecorator implements ILabelDecorator, IFontDecorator, IColorDecorator {

	public InterestDecorator() {
		super();
	}

	private IInteractionElement getNode(Object element) {
		IInteractionElement node = null;
		if (element instanceof IInteractionElement) {
			node = (IInteractionElement) element;
		} else {
			AbstractContextStructureBridge adapter = ContextCorePlugin.getDefault().getStructureBridge(element);
			node = ContextCorePlugin.getContextManager().getElement(adapter.getHandleIdentifier(element));
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
				return ContextUiPrefContstants.BOLD;
			}
		}
		return null;
	}

	public Color decorateForeground(Object element) {
		IInteractionElement node = getNode(element);
		if (element instanceof InteractionContextRelation) {
			return ColorMap.RELATIONSHIP;
		} else if (node != null) {
			getForegroundForElement(node);
		}
		return null;
	}

	public Color decorateBackground(Object element) {
		return null;
//		IInteractionElement node = getNode(element);
//		if (node != null) {
//			return UiUtil.getBackgroundForElement(node);
//		} else {
//			return null;
//		}
	}
	
	public static Color getForegroundForElement(IInteractionElement node) {
		if (node == null)
			return null;
		if (node.getInterest().isPredicted() || node.getInterest().isPropagated()) {
//			if (node.getInterest().getValue() >= InteractionContextManager.getScalingFactors().getLandmark() / 3) {
//				return ColorMap.GRAY_DARK;
//			} else if (node.getInterest().getValue() >= 10) {
//				return ColorMap.GRAY_MEDIUM;
//			} else {
			return ColorMap.GRAY_MEDIUM;
//			}
		} else if (node.getInterest().isLandmark()) {
			return ColorMap.LANDMARK;
		} else if (node.getInterest().isInteresting()) {
			return null;
		}
		return ColorMap.GRAY_MEDIUM;
	}
}
