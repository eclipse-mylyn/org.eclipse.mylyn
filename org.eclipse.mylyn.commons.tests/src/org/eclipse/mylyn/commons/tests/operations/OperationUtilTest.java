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

	public void testIsBackgroundMonitorBackgroundMonitorForNullProgressMonitor() {
		assertTrue(OperationUtil.isBackgroundMonitor(Policy.backgroundMonitorFor(new NullProgressMonitor())));
	}

	public void testIsBackgroundMonitorBackgroundMonitorForNull() {
		assertTrue(OperationUtil.isBackgroundMonitor(Policy.backgroundMonitorFor(null)));
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
