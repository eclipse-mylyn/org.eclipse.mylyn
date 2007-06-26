/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.ui.IActionFilter;

/**
 * @author Mik Kersten
 */
public class ContextActiveActionFilter implements IActionFilter {

	public boolean testAttribute(Object target, String name, String value) {
		return ContextCorePlugin.getContextManager().isContextActive();
	}

}
