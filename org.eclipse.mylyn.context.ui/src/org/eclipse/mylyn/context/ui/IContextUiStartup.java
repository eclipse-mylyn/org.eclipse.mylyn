/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	public abstract void lazyStartup();

}