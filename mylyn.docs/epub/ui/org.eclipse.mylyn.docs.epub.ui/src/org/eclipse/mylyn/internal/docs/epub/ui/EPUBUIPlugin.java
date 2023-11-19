/*******************************************************************************
 * Copyright (c) 2011, 2012 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class EPUBUIPlugin extends AbstractUIPlugin {

	private static EPUBUIPlugin plugin;

	public static final String PLUGIN_ID = "org.eclipse.mylyn.docs.epub.ui"; //$NON-NLS-1$

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (plugin == this) {
			plugin = null;
		}
		super.stop(context);
	}

	public static EPUBUIPlugin getDefault() {
		return plugin;
	}

}
