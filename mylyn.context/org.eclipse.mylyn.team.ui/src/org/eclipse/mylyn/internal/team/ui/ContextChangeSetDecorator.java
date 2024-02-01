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

package org.eclipse.mylyn.internal.team.ui;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.team.ui.IContextChangeSet;

/**
 * @author Mik Kersten
 */
public class ContextChangeSetDecorator implements ILightweightLabelDecorator {

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IContextChangeSet changeSet) {
			if (changeSet.getTask().isActive()) {
				decoration.setFont(CommonFonts.BOLD);
			}
		}
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// ignore
	}
}
