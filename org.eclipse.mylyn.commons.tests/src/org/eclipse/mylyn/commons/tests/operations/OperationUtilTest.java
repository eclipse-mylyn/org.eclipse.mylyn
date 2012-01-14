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

package org.eclipse.mylyn.commons.tests.operations;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.Policy;

/**
 * @author Steffen Pingel
 */
public class OperationUtilTest extends TestCase {

	public void testIsBackgroundMonitorNull() {
		assertFalse(OperationUtil.isBackgroundMonitor(null));
	}

	public void testIsBackgroundMonitorProgressMonitor() {
		assertFalse(OperationUtil.isBackgroundMonitor(new NullProgressMonitor()));
	}

	public void testIsBackgroundMonitorBackgroundMonitor() {
		assertTrue(OperationUtil.isBackgroundMonitor(Policy.backgroundMonitorFor(new NullProgressMonitor())));
	}

	public void testIsBackgroundMonitorOperationMonitorNull() {
		assertFalse(OperationUtil.isBackgroundMonitor(OperationUtil.convert(null)));
	}

	public void testIsBackgroundMonitorOperationMonitorProgressMonitor() {
		assertFalse(OperationUtil.isBackgroundMonitor(OperationUtil.convert(new NullProgressMonitor())));
	}

	public void testIsBackgroundMonitorOperationMonitorBackground() {
		IOperationMonitor monitor = OperationUtil.convert(new NullProgressMonitor());
		monitor.addFlag(OperationFlag.BACKGROUND);
		assertTrue(OperationUtil.isBackgroundMonitor(monitor));
		monitor.removeFlag(OperationFlag.BACKGROUND);
		assertFalse(OperationUtil.isBackgroundMonitor(monitor));
	}

	public void testIsBackgroundMonitorOperationMonitorChild() {
		IOperationMonitor monitor = OperationUtil.convert(new NullProgressMonitor());
		monitor.addFlag(OperationFlag.BACKGROUND);
		assertTrue(OperationUtil.isBackgroundMonitor(monitor));

		IOperationMonitor child = monitor.newChild(1);
		assertTrue(OperationUtil.isBackgroundMonitor(child));
		monitor.removeFlag(OperationFlag.BACKGROUND);
		assertFalse(OperationUtil.isBackgroundMonitor(child));
	}

	public void testConvert() {
		assertNotNull(OperationUtil.convert(null));
	}

}
