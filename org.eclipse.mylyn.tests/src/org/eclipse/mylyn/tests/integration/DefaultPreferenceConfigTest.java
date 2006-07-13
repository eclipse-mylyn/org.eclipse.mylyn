/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tests.integration;

import org.eclipse.mylar.internal.monitor.MylarMonitorPreferenceConstants;
import org.eclipse.mylar.monitor.usage.MylarUsageMonitorPlugin;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class DefaultPreferenceConfigTest extends TestCase {

	public void testMonitorPreferences() {
		assertTrue(MylarUsageMonitorPlugin.getPrefs().getBoolean(MylarMonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE));
	}
}
