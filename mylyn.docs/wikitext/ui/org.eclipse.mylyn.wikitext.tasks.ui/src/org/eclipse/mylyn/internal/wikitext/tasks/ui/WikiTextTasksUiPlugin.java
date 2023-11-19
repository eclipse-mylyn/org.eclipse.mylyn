/*******************************************************************************
 * Copyright (c) 2009, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.tasks.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author David Green
 */
public class WikiTextTasksUiPlugin extends AbstractUIPlugin {

	public static final String FONT_REGISTRY_KEY_DEFAULT_FONT = "org.eclipse.mylyn.tasks.ui.fonts.task.editor.comment"; //$NON-NLS-1$

	public static final String FONT_REGISTRY_KEY_MONOSPACE_FONT = "org.eclipse.mylyn.wikitext.tasks.ui.monospaceFont"; //$NON-NLS-1$

	private static WikiTextTasksUiPlugin plugin;

	public WikiTextTasksUiPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static WikiTextTasksUiPlugin getDefault() {
		return plugin;
	}

}
