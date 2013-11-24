/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.core.osgi;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.internal.wikitext.core.osgi.util.EclipseServiceLocator;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.osgi.framework.BundleContext;

/**
 * The WikiText plug-in class. Use only in an Eclipse runtime environment. Programs should use the
 * {@link ServiceLocator} instead of this class if possible. Stand-alone programs (that is, those programs that do not
 * run in an Eclipse runtime) must not use this class. Should not be instantiated directly, instead use
 * {@link #getDefault()}.
 * 
 * @author David Green
 * @see #getDefault()
 * @see ServiceLocator
 */
public class WikiTextPlugin extends Plugin {

	private static WikiTextPlugin plugin;

	public WikiTextPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ServiceLocator.setImplementation(EclipseServiceLocator.class);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (plugin == this) {
			plugin = null;
		}
		super.stop(context);
	}

	public static WikiTextPlugin getDefault() {
		return plugin;
	}

	public String getPluginId() {
		return getBundle().getSymbolicName();
	}
}
