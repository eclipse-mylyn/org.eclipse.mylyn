/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class IdeUiBridgePlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.ide"; //$NON-NLS-1$

	private static IdeUiBridgePlugin INSTANCE;

	public static final ImageDescriptor EDGE_REF_XML = getImageDescriptor("icons/elcl16/edge-ref-xml.gif"); //$NON-NLS-1$

	public static class IdeUiBridgeStartup implements IContextUiStartup {

		@Override
		public void lazyStartup() {
			// ignore, it is sufficient that the bundle is activated on context ui startup
		}

	}

	public IdeUiBridgePlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	public static IdeUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.ide", path); //$NON-NLS-1$
	}

}
