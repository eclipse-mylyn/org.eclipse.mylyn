/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.context.ui;

import java.util.ConcurrentModificationException;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IInteractionElement;
import org.eclipse.mylar.context.core.AbstractContextStructureBridge;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.context.core.InteractionContextRelation;

/**
 * @author Mik Kersten
 */
public class InterestDecoratorLightweight implements ILightweightLabelDecorator {

	public InterestDecoratorLightweight() {
		super();
	}

	public void decorate(Object element, IDecoration decoration) {
		AbstractContextStructureBridge bridge = null;
		try {
			if (ContextCorePlugin.getDefault() == null)
				return;
			bridge = ContextCorePlugin.getDefault().getStructureBridge(element);
		} catch (ConcurrentModificationException cme) {
			// ignored, because we can add structure bridges during decoration
		}
		try {
			IInteractionElement node = null;
			if (element instanceof InteractionContextRelation) {
				decoration.setForegroundColor(ColorMap.RELATIONSHIP);
			} else if (element instanceof IInteractionElement) {
				node = (IInteractionElement) element;
			} else {
				if (bridge != null && bridge.getContentType() != null) {
					node = ContextCorePlugin.getContextManager().getElement(bridge.getHandleIdentifier(element));
				}
			}
			if (node != null) {
				decoration.setBackgroundColor(UiUtil.getBackgroundForElement(node));
				decoration.setForegroundColor(UiUtil.getForegroundForElement(node));
				if (bridge != null && bridge.canBeLandmark(node.getHandleIdentifier())
						&& node.getInterest().isLandmark() && !node.getInterest().isPropagated()
						&& !node.getInterest().isPredicted()) {
					decoration.setFont(ContextUiPrefContstants.BOLD);
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.log(e, "decoration failed");
		}
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

}
