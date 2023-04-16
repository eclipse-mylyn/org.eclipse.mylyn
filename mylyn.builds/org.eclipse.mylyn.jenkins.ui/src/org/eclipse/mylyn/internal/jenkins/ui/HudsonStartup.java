/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.ui;

import org.eclipse.mylyn.builds.ui.BuildsUiStartup;

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
			// occurs when the optional ECF dependency is not satisfied 
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
