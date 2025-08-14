/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.ui.IActionFilter;

/**
 * @author Mik Kersten
 */
public class ContextActiveActionFilter implements IActionFilter {

	@Override
	public boolean testAttribute(Object target, String name, String value) {
		return ContextCore.getContextManager().isContextActive();
	}

}
