/*******************************************************************************
 * Copyright (c) 2010, 2011 Itema AS and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Itema AS - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui;

/**
 * Plug-ins that register a builds startup extension will be activated before the builds user interface starts.
 * 
 * @since 3.5
 * @author Torkild U. Resheim
 */
public abstract class BuildsUiStartup {

	/**
	 * Invoked before the builds user interface starts.
	 */
	public abstract void lazyStartup();

}
