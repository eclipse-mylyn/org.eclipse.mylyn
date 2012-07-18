/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.ui.BuildsUiStartup;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * @author Steffen Pingel
 */
public class HudsonStartup extends BuildsUiStartup {

	private static HudsonStartup instance;

	public static HudsonStartup getInstance() {
		return instance;
	}

	public HudsonStartup() {
		instance = this;
	}

	private HudsonDiscovery discovery;

	@Override
	public void lazyStartup() {
		if (discovery != null) {
			throw new IllegalStateException("Already started"); //$NON-NLS-1$
		}
		try {
			discovery = new HudsonDiscovery();
			discovery.start();
		} catch (LinkageError e) {
			// occurs when the optional ECF depedency is not satisfied 
			StatusHandler.log(new Status(IStatus.WARNING, HudsonUiPlugin.ID_PLUGIN,
					"ECF integration failed to load. Hudson server auto discovery is not available.", e));
			discovery = null;
		}
	}

	public void stop() {
		if (discovery != null) {
			try {
				discovery.stop();
			} catch (NullPointerException e) {
				// ignore, see bug 383316
			}
			discovery = null;
		}
	}

}
