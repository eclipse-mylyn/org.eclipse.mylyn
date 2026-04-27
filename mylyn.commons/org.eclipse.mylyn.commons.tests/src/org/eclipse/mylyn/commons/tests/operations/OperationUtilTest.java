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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.operations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.Policy;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
public class OperationUtilTest {

	@Test
	public void testIsBackgroundMonitorNull() {
		assertFalse(OperationUtil.isBackgroundMonitor(null));
	}

	@Test
	public void testIsBackgroundMonitorProgressMonitor() {
		assertFalse(OperationUtil.isBackgroundMonitor(new NullProgressMonitor()));
	}

	@Test
	public void testIsBackgroundMonitorBackgroundMonitorForNullProgressMonitor() {
		assertTrue(OperationUtil.isBackgroundMonitor(Policy.backgroundMonitorFor(new NullProgressMonitor())));
	}

	@Test
	public void testIsBackgroundMonitorBackgroundMonitorForNull() {
		assertTrue(OperationUtil.isBackgroundMonitor(Policy.backgroundMonitorFor(null)));
	}

	@Test
	public void testIsBackgroundMonitorOperationMonitorNull() {
		assertFalse(OperationUtil.isBackgroundMonitor(OperationUtil.convert(null)));
	}

	@Test
	public void testIsBackgroundMonitorOperationMonitorProgressMonitor() {
		assertFalse(OperationUtil.isBackgroundMonitor(OperationUtil.convert(new NullProgressMonitor())));
	}

	@Test
	public void testIsBackgroundMonitorOperationMonitorBackground() {
		IOperationMonitor monitor = OperationUtil.convert(new NullProgressMonitor());
		monitor.addFlag(OperationFlag.BACKGROUND);
		assertTrue(OperationUtil.isBackgroundMonitor(monitor));
		monitor.removeFlag(OperationFlag.BACKGROUND);
		assertFalse(OperationUtil.isBackgroundMonitor(monitor));
	}

	@Test
	public void testIsBackgroundMonitorOperationMonitorChild() {
		IOperationMonitor monitor = OperationUtil.convert(new NullProgressMonitor());
		monitor.addFlag(OperationFlag.BACKGROUND);
		assertTrue(OperationUtil.isBackgroundMonitor(monitor));

		IOperationMonitor child = monitor.newChild(1);
		assertTrue(OperationUtil.isBackgroundMonitor(child));
		monitor.removeFlag(OperationFlag.BACKGROUND);
		assertFalse(OperationUtil.isBackgroundMonitor(child));
	}

	@Test
	public void testConvert() {
		assertNotNull(OperationUtil.convert(null));
	}

}
