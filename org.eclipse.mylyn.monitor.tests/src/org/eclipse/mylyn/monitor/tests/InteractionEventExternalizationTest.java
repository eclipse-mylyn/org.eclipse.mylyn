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
package org.eclipse.mylar.monitor.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.internal.monitor.InteractionEventLogger;
import org.eclipse.mylar.internal.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.internal.monitor.MylarMonitorPreferenceConstants;
import org.eclipse.mylar.provisional.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class InteractionEventExternalizationTest extends AbstractContextTest {

	private static final String PATH = "test-log.xml";

	public void testManualExternalization() throws IOException {
		MylarMonitorPlugin.getPrefs().setValue(MylarMonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE, false);

		List<InteractionEvent> events = new ArrayList<InteractionEvent>();
		File f = new File(PATH);
		if (f.exists()) {
			f.delete();
		}
		InteractionEventLogger logger = new InteractionEventLogger(f);
		logger.clearInteractionHistory();
		logger.startObserving();
		String handle = "";
		for (int i = 0; i < 100; i++) {
			handle += "1";
			InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.SELECTION, "structureKind", handle,
					"originId", "navigatedRelation", "delta", 2f, new Date(0), new Date());
			events.add(event);
			logger.interactionObserved(event);
		}
		logger.stopObserving();

		File infile = new File(PATH);
		List<InteractionEvent> readEvents = logger.getHistoryFromFile(infile);
		for (int i = 0; i < events.size(); i++) {
			assertEquals(events.get(i), readEvents.get(i));
		}

		MylarMonitorPlugin.getPrefs().setValue(MylarMonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE, true);
	}
}
