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

package org.eclipse.mylyn.context.ui;

/**
 * Plug-ins that register a context startup extension will be activated before the first context is activated.
 * 
 * @since 2.3
 * @author Steffen Pingel
 */
public interface IContextUiStartup {

	/**
	 * Invoked before the first context activation.
	 */
	void lazyStartup();

}