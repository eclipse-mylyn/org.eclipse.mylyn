/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sebastian Schmidt - bug 155333
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.InterestFilter;

/**
 * @author Mik Kersten
 * @author Sebastian Schmidt
 */
public class BreakpointsInterestFilter extends InterestFilter {

	private final AbstractContextStructureBridge structureBridge = ContextCore
			.getStructureBridge(DebugUiPlugin.CONTENT_TYPE);

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof IBreakpoint) {
			IBreakpoint breakpoint = (IBreakpoint) element;
			IInteractionElement interactionElement = ContextCore.getContextManager()
					.getElement(structureBridge.getHandleIdentifier(breakpoint));
			if (interactionElement == null) {
				return true;
			}
			return !interactionElement.getInterest().isInteresting();
		}

		return true;
	}
}