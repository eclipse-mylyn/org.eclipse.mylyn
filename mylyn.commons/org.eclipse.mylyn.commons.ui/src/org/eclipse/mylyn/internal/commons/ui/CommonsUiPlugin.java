/*******************************************************************************
 * Copyright (c) 2008, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @deprecated use {@link CommonsUiConstants} instead
 */
@Deprecated
public class CommonsUiPlugin extends AbstractUIPlugin {

	@Deprecated
	public static final String ID_PLUGIN = "org.eclipse.mylyn.commons.ui"; //$NON-NLS-1$

	private static CommonsUiPlugin plugin;

	// shared colors for all forms
	private FormColors formColors;

	@Deprecated
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Deprecated
	@Override
	public void stop(BundleContext context) throws Exception {
		if (formColors != null) {
			formColors.dispose();
			formColors = null;
		}
		plugin = null;
		super.stop(context);
	}

	@Deprecated
	public static CommonsUiPlugin getDefault() {
		return plugin;
	}

	@Deprecated
	public FormColors getFormColors(Display display) {
		if (formColors == null) {
			formColors = new FormColors(display);
			formColors.markShared();
		}
		return formColors;
	}

}
