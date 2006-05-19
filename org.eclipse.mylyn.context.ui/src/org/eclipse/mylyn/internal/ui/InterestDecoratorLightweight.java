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

package org.eclipse.mylar.internal.ui;

import java.util.ConcurrentModificationException;

import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.internal.core.MylarContextRelation;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;

/**
 * @author Mik Kersten
 */
public class InterestDecoratorLightweight implements ILightweightLabelDecorator {

	private boolean doiTextDecorationEnabled = true;
	
	public InterestDecoratorLightweight() {
		super();
	}

	public void decorate(Object element, IDecoration decoration) {
		IMylarStructureBridge bridge = null;
		try {
			if (MylarPlugin.getDefault() == null)
				return;
			bridge = MylarPlugin.getDefault().getStructureBridge(element);
		} catch (ConcurrentModificationException cme) {
			// ignored, because we can add structure bridges during decoration
		}
		try {
			IMylarElement node = null;
			if (element instanceof MylarContextRelation) {
				decoration.setForegroundColor(MylarUiPlugin.getDefault().getColorMap().RELATIONSHIP);
			} else if (element instanceof IMylarElement) {
				node = (IMylarElement) element;
			} else {
				if (bridge != null && bridge.getContentType() != null) {
					node = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(element));
				}
			}
			if (node != null) {
				decoration.setBackgroundColor(UiUtil.getBackgroundForElement(node));
				decoration.setForegroundColor(UiUtil.getForegroundForElement(node));
				if (bridge != null && bridge.canBeLandmark(node.getHandleIdentifier())
						&& node.getInterest().isLandmark() && !node.getInterest().isPropagated()
						&& !node.getInterest().isPredicted()) {
					decoration.setFont(MylarUiPrefContstants.BOLD);
				}
				if (doiTextDecorationEnabled) {
					decoration.addSuffix(" {" + node.getInterest().getValue() + "}");
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
