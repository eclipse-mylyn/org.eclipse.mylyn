/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.mylyn.team.ui.IContextChangeSet;

/**
 * @author Mik Kersten
 */
public class ContextChangeSetDecorator implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IContextChangeSet) {
//			decoration.addOverlay(ContextUiImages.MYLYN_OVERLAY, IDecoration.BOTTOM_RIGHT);
			IContextChangeSet changeSet = (IContextChangeSet) element;
			if (changeSet.getTask().isActive()) {
				decoration.setFont(CommonFonts.BOLD);
			}
		}
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore
	}

}
