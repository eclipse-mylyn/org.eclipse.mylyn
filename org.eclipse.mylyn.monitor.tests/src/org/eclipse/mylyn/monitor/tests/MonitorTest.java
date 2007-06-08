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

package org.eclipse.mylyn.monitor.tests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.monitor.usage.InteractionEventLogger;
import org.eclipse.mylyn.internal.monitor.usage.MylarUsageMonitorPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.IMylarMonitorLifecycleListener;
import org.eclipse.mylyn.monitor.ui.workbench.BrowserMonitor;
import org.eclipse.mylyn.monitor.ui.workbench.KeybindingCommandMonitor;
import org.eclipse.mylyn.monitor.ui.workbench.PerspectiveChangeMonitor;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class MonitorTest extends TestCase implements IMylarMonitorLifecycleListener {

	private InteractionEventLogger logger = MylarUsageMonitorPlugin.getDefault().getInteractionLogger();

	private MockSelectionMonitor selectionMonitor = new MockSelectionMonitor();

	private KeybindingCommandMonitor commandMonitor = new KeybindingCommandMonitor();

	private BrowserMonitor browserMonitor = new BrowserMonitor();

	private PerspectiveChangeMonitor perspectiveMonitor = new PerspectiveChangeMonitor();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEnablement() throws IOException {
		File monitorFile = MylarUsageMonitorPlugin.getDefault().getMonitorLogFile();
		assertTrue(monitorFile.exists());
		MylarUsageMonitorPlugin.getDefault().stopMonitoring();
		logger.clearInteractionHistory();
		assertEquals(0, logger.getHistoryFromFile(monitorFile).size());
		generateSelection();
		assertEquals(0, logger.getHistoryFromFile(monitorFile).size());

		MylarUsageMonitorPlugin.getDefault().startMonitoring();
		generateSelection();
		assertEquals(1, logger.getHistoryFromFile(monitorFile).size());

		MylarUsageMonitorPlugin.getDefault().stopMonitoring();
		generateSelection();
		assertEquals(1, logger.getHistoryFromFile(monitorFile).size());

		MylarUsageMonitorPlugin.getDefault().startMonitoring();
		generateSelection();
		assertEquals(2, logger.getHistoryFromFile(monitorFile).size());
		MylarUsageMonitorPlugin.getDefault().stopMonitoring();
	}

	public void testUrlFilter() {
		browserMonitor.setAcceptedUrls("url1,url2,url3");
		assertEquals(3, browserMonitor.getAcceptedUrls().size());

		browserMonitor.setAcceptedUrls(null);
		assertEquals(0, browserMonitor.getAcceptedUrls().size());

		browserMonitor.setAcceptedUrls("");
		assertEquals(0, browserMonitor.getAcceptedUrls().size());
	}

	@SuppressWarnings( { "deprecation", "unchecked" })
	public void testLogging() throws InterruptedException {
		MylarUsageMonitorPlugin.getDefault().startMonitoring();
		logger.stopMonitoring();
		MylarUsageMonitorPlugin.getDefault().getMonitorLogFile().delete();
		logger.startMonitoring();

		generateSelection();
		commandMonitor.preExecute("foo.command", new ExecutionEvent(new HashMap(), "trigger", "context"));
		File monitorFile = MylarUsageMonitorPlugin.getDefault().getMonitorLogFile();
		assertTrue(monitorFile.exists());
		logger.stopMonitoring();
		List<InteractionEvent> events = logger.getHistoryFromFile(monitorFile);
		assertTrue("" + events.size(), events.size() >= 2);

		logger.stopMonitoring();
		events = logger.getHistoryFromFile(monitorFile);
		assertTrue(events.size() >= 0);
		MylarUsageMonitorPlugin.getDefault().getMonitorLogFile().delete();
		logger.startMonitoring();

		generatePerspectiveSwitch();
		assertTrue(monitorFile.exists());
		logger.stopMonitoring();
		events = logger.getHistoryFromFile(monitorFile);
		assertTrue(events.size() >= 1);
	}

	private void generateSelection() {
		selectionMonitor.selectionChanged(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart(), new StructuredSelection("yo"));
	}

	private void generatePerspectiveSwitch() {
		IPerspectiveRegistry registry = PlatformUI.getWorkbench().getPerspectiveRegistry();
		IPerspectiveDescriptor perspective = registry.clonePerspective("newId", "newLabel",
				registry.getPerspectives()[0]);

		perspectiveMonitor.perspectiveActivated(null, perspective);
	}

	boolean monitorRunning = false;

	public void startMonitoring() {
		monitorRunning = true;
	}

	public void stopMonitoring() {
		monitorRunning = false;
	}

	public void testLifecycleCallbacks() {
		assertFalse(monitorRunning);
		MylarUsageMonitorPlugin.getDefault().stopMonitoring();
		MylarUsageMonitorPlugin.getDefault().addMonitoringLifecycleListener(this);
		assertTrue(monitorRunning);

		MylarUsageMonitorPlugin.getDefault().startMonitoring();
		assertTrue(monitorRunning);
		MylarUsageMonitorPlugin.getDefault().stopMonitoring();
		assertFalse(monitorRunning);

		MylarUsageMonitorPlugin.getDefault().startMonitoring();
		assertTrue(monitorRunning);
		MylarUsageMonitorPlugin.getDefault().stopMonitoring();
		assertFalse(monitorRunning);

		MylarUsageMonitorPlugin.getDefault().removeMonitoringLifecycleListener(this);
	}
}

// public void testLogFileMove() throws IOException {
// File defaultFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
// MylarMonitorPlugin.getDefault().stopMonitoring();
// assertTrue(logger.clearInteractionHistory());
//    	
// MylarMonitorPlugin.getDefault().startMonitoring();
// generateSelection();
// generateSelection();
// assertEquals(2, logger.getHistoryFromFile(defaultFile).size());
//        
// File newFile =
// MylarMonitorPlugin.getDefault().moveMonitorLogFile(ContextCorePlugin.getDefault().getMylarDataDirectory()
// + "/monitor-test-new.xml");
// assertNotNull(newFile);
// File movedFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
// assertTrue(!newFile.equals(defaultFile));
// assertEquals(newFile, movedFile);
// assertEquals(newFile, logger.getOutputFile());
// assertEquals(2, logger.getHistoryFromFile(newFile).size());
// assertEquals(0, logger.getHistoryFromFile(defaultFile).size());
//    	
// generateSelection();
// assertEquals(3, logger.getHistoryFromFile(newFile).size());
// File restoredFile =
// MylarMonitorPlugin.getDefault().moveMonitorLogFile(defaultFile.getAbsolutePath());
// assertNotNull(restoredFile);
// }
