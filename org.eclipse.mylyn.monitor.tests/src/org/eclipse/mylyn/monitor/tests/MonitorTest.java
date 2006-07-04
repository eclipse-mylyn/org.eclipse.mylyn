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
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.monitor.IMylarMonitorLifecycleListener;
import org.eclipse.mylar.internal.monitor.InteractionEventLogger;
import org.eclipse.mylar.internal.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.internal.monitor.monitors.BrowserMonitor;
import org.eclipse.mylar.internal.monitor.monitors.KeybindingCommandMonitor;
import org.eclipse.mylar.internal.monitor.monitors.PerspectiveChangeMonitor;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class MonitorTest extends TestCase implements IMylarMonitorLifecycleListener {

	private InteractionEventLogger logger = MylarMonitorPlugin.getDefault().getInteractionLogger();

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
		File monitorFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
		assertTrue(monitorFile.exists());
		MylarMonitorPlugin.getDefault().stopMonitoring();
		logger.clearInteractionHistory();
		assertEquals(0, logger.getHistoryFromFile(monitorFile).size());
		generateSelection();
		assertEquals(0, logger.getHistoryFromFile(monitorFile).size());

		MylarMonitorPlugin.getDefault().startMonitoring();
		generateSelection();
		assertEquals(1, logger.getHistoryFromFile(monitorFile).size());

		MylarMonitorPlugin.getDefault().stopMonitoring();
		generateSelection();
		assertEquals(1, logger.getHistoryFromFile(monitorFile).size());

		MylarMonitorPlugin.getDefault().startMonitoring();
		generateSelection();
		assertEquals(2, logger.getHistoryFromFile(monitorFile).size());
		MylarMonitorPlugin.getDefault().stopMonitoring();
	}

	public void testUrlFilter() {
		browserMonitor.setAcceptedUrls("url1,url2,url3");
		assertEquals(3, browserMonitor.getAcceptedUrls().size());

		browserMonitor.setAcceptedUrls(null);
		assertEquals(0, browserMonitor.getAcceptedUrls().size());

		browserMonitor.setAcceptedUrls("");
		assertEquals(0, browserMonitor.getAcceptedUrls().size());
	}

	@SuppressWarnings("deprecation")
	public void testLogging() throws InterruptedException {
		MylarMonitorPlugin.getDefault().startMonitoring();
		logger.stopObserving();
		MylarMonitorPlugin.getDefault().getMonitorLogFile().delete();
		logger.startObserving();

		generateSelection();
		commandMonitor.preExecute("foo.command", new ExecutionEvent(new HashMap(), "trigger", "context"));
		File monitorFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
		assertTrue(monitorFile.exists());
		logger.stopObserving();
		List<InteractionEvent> events = logger.getHistoryFromFile(monitorFile);
		assertTrue("" + events.size(), events.size() >= 2);

		logger.stopObserving();
		events = logger.getHistoryFromFile(monitorFile);
		assertTrue(events.size() >= 0);
		MylarMonitorPlugin.getDefault().getMonitorLogFile().delete();
		logger.startObserving();

		generatePerspectiveSwitch();
		assertTrue(monitorFile.exists());
		logger.stopObserving();
		events = logger.getHistoryFromFile(monitorFile);
		assertTrue(events.size() >= 1);
	}

	private void generateSelection() {
		selectionMonitor.selectionChanged(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart(), new StructuredSelection("yo"));
	}

	private void generatePerspectiveSwitch() {
		IPerspectiveRegistry registry = MylarPlugin.getDefault().getWorkbench().getPerspectiveRegistry();
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
		MylarMonitorPlugin.getDefault().stopMonitoring();
		MylarMonitorPlugin.getDefault().addMonitoringLifecycleListener(this);
		assertFalse(monitorRunning);
		
		MylarMonitorPlugin.getDefault().startMonitoring();
		assertTrue(monitorRunning);
		MylarMonitorPlugin.getDefault().stopMonitoring();
		assertFalse(monitorRunning);
		
		MylarMonitorPlugin.getDefault().startMonitoring();
		assertTrue(monitorRunning);
		MylarMonitorPlugin.getDefault().stopMonitoring();
		assertFalse(monitorRunning);

		MylarMonitorPlugin.getDefault().removeMonitoringLifecycleListener(this);
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
// MylarMonitorPlugin.getDefault().moveMonitorLogFile(MylarPlugin.getDefault().getMylarDataDirectory()
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