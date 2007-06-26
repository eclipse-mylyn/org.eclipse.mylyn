/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.monitor.usage.InteractionEventLogger;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * @author Brian de Alwis
 * @author Mik Kersten
 */
public class MultiWindowMonitorTest extends TestCase {

	private InteractionEventLogger logger = UiUsageMonitorPlugin.getDefault().getInteractionLogger();

	private MockSelectionMonitor selectionMonitor = new MockSelectionMonitor();

	private IWorkbenchWindow window1;

	private IWorkbenchWindow window2;

	private boolean monitoringWasEnabled;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		monitoringWasEnabled = UiUsageMonitorPlugin.getDefault().isMonitoringEnabled();
		UiUsageMonitorPlugin.getDefault().stopMonitoring();
		window1 = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		window2 = duplicateWindow(window1);
		assertNotNull(window2);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		window2.close();
		if (monitoringWasEnabled) {
			UiUsageMonitorPlugin.getDefault().startMonitoring();
		}
	}

	protected void generateSelection(IWorkbenchWindow w) {
		selectionMonitor.selectionChanged(w.getActivePage().getActivePart(), new StructuredSelection("yo"));
	}

	public void testMultipleWindows() throws IOException {
		File monitorFile = UiUsageMonitorPlugin.getDefault().getMonitorLogFile();
		logger.clearInteractionHistory();
		assertEquals(0, logger.getHistoryFromFile(monitorFile).size());

		generateSelection(window1);
		assertEquals(0, logger.getHistoryFromFile(monitorFile).size());

		UiUsageMonitorPlugin.getDefault().startMonitoring();
		generateSelection(window1);
		generateSelection(window2);
		assertEquals(2, logger.getHistoryFromFile(monitorFile).size());
	}

	protected IWorkbenchWindow duplicateWindow(IWorkbenchWindow window) {
		WorkbenchWindow w = (WorkbenchWindow) window;
		XMLMemento memento = XMLMemento.createWriteRoot(IWorkbenchConstants.TAG_WINDOW);
		IStatus status = w.saveState(memento);
		if (!status.isOK()) {
			fail("failed to duplicate window: " + status);
		}
		return restoreWorkbenchWindow((Workbench) w.getWorkbench(), memento);
	}

	protected IWorkbenchWindow restoreWorkbenchWindow(Workbench workbench, IMemento memento) {
		return (IWorkbenchWindow) invokeMethod(workbench, "restoreWorkbenchWindow", new Class[] { IMemento.class },
				new Object[] { memento });
	}

	protected Object invokeMethod(Object instance, String methodName, Class<?> argTypes[], Object arguments[]) {
		Class<?> clas = instance.getClass();
		try {
			Method method = clas.getDeclaredMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(instance, arguments);
		} catch (Exception ex) {
			fail("exception during reflective invocation of " + clas.getName() + "." + methodName + ": " + ex);
			return null;
		}
	}

}
