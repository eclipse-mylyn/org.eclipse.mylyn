/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Apr 18, 2005
 */
package org.eclipse.mylyn.internal.context.ui.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.IInteractionObject;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * TODO: refactor edge stuff
 * 
 * @author Mik Kersten
 */
public class DelegatingContextLabelProvider implements ILabelProvider {

	private static boolean qualifyNamesMode = false; // TODO: make non-static

	public static boolean isQualifyNamesMode() {
		return qualifyNamesMode;
	}

	public static void setQualifyNamesMode(boolean qualify) {
		qualifyNamesMode = qualify;
	}

	public Image getImage(Object element) {
		if (element instanceof IInteractionObject) {
			ILabelProvider provider = ContextUiPlugin.getDefault().getContextLabelProvider(
					((IInteractionObject) element).getContentType());
			return provider.getImage(element);
		} else {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(element);
			ILabelProvider provider = ContextUiPlugin.getDefault().getContextLabelProvider(bridge.getContentType());
			if (provider != null) {
				return provider.getImage(element);
			}
		}
		return null;
	}

	public String getText(Object object) {
		if (object instanceof IInteractionObject) {
			IInteractionObject element = (IInteractionObject) object;
			ILabelProvider provider = ContextUiPlugin.getDefault().getContextLabelProvider(element.getContentType());

//			ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(ContextUiPrefContstants.DECORATE_INTEREST_LEVEL);
//			if (ContextUiPlugin.getDefault().isDecorateInterestMode()) { 
//				return provider.getText(element) + " [" + element.getInterest().getValue() + "]";
//			} else {
			return provider.getText(element);
//			}
		} else {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(object);
			ILabelProvider provider = ContextUiPlugin.getDefault().getContextLabelProvider(bridge.getContentType());
			if (provider != null) {
//				if (ContextUiPlugin.getDefault().isDecorateInterestMode()) {
//					IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
//							bridge.getHandleIdentifier(object));
//					return provider.getText(object) + " [" + element.getInterest().getValue() + "]";
//				} else {
				return provider.getText(object);
//				}
			}
		}
		return "? " + object;
	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO: implement?
		return false;
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO: implement?
	}

	public void dispose() {
		// TODO: implement?
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO: implement?
	}
}
