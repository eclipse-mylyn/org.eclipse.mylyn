/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.ui.IActionFilter;

/**
 * @author Mik Kersten
 */
public class ContextActiveActionFilter implements IActionFilter {

	public boolean testAttribute(Object target, String name, String value) {
		return ContextCore.getContextManager().isContextActive();
	}

}
