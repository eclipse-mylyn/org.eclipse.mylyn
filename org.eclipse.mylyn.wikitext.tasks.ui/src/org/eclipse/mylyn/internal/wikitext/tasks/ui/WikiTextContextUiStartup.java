/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.tasks.ui;

import org.eclipse.mylyn.context.ui.IContextUiStartup;

/**
 * @author David Green
 */
public class WikiTextContextUiStartup implements IContextUiStartup {

	public void lazyStartup() {
		WikiTextTasksUiPlugin.getDefault().contextUiStartup();
	}

}
