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

import java.util.ConcurrentModificationException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextRelation;

/**
 * @author Mik Kersten
 */
public class InterestDecoratorLightweight implements ILightweightLabelDecorator {

	public InterestDecoratorLightweight() {
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (ContextCore.getContextManager() != null && !ContextCore.getContextManager().isContextActive()) {
			return;
		}

		AbstractContextStructureBridge bridge = null;
		try {
			if (ContextCorePlugin.getDefault() == null) {
				return;
			}
			bridge = ContextCore.getStructureBridge(element);
		} catch (ConcurrentModificationException cme) {
			// ignored, because we can add structure bridges during decoration
		}
		try {
			// NOTE: awkward coupling and special rule to deal with tasks, see bug 212639
			if (!"org.eclipse.mylyn.internal.tasks.core.TaskTask".equals(element.getClass().getName())) { //$NON-NLS-1$
				IInteractionElement node = null;
				if (element instanceof InteractionContextRelation) {
					decoration.setForegroundColor(ColorMap.RELATIONSHIP);
				} else if (element instanceof IInteractionElement) {
					node = (IInteractionElement) element;
				} else if (bridge != null && bridge.getContentType() != null) {
					node = ContextCore.getContextManager().getElement(bridge.getHandleIdentifier(element));
				}
				if (node != null) {
					decoration.setForegroundColor(ContextUi.getForeground(node));
					if (bridge != null && bridge.canBeLandmark(node.getHandleIdentifier())
							&& node.getInterest().isLandmark() && !node.getInterest().isPropagated()
							&& !node.getInterest().isPredicted()) {
						decoration.setFont(CommonFonts.BOLD);
					}
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Decoration failed", e)); //$NON-NLS-1$
		}
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

}
