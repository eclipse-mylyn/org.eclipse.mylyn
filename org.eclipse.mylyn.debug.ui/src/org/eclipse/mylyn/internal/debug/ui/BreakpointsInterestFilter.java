/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	private final AbstractContextStructureBridge structureBridge = ContextCore.getStructureBridge(DebugUiPlugin.CONTENT_TYPE);

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof IBreakpoint) {
			IBreakpoint breakpoint = (IBreakpoint) element;
			IInteractionElement interactionElement = ContextCore.getContextManager().getElement(
					structureBridge.getHandleIdentifier(breakpoint));
			if (interactionElement == null) {
				return true;
			}
			return !interactionElement.getInterest().isInteresting();
		}

		return true;
	}
}