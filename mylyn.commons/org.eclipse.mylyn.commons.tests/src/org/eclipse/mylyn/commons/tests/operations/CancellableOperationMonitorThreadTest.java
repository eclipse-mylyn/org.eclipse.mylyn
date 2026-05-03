/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.operations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.mylyn.commons.core.operations.CancellableOperationMonitorThread;
import org.eclipse.mylyn.commons.core.operations.ICancellableOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
public class CancellableOperationMonitorThreadTest {

	private CancellableOperationMonitorThread thread;

	class MockOperation implements ICancellableOperation {

		boolean canceled;

		boolean aborted;

		@Override
		public void abort() {
			aborted = true;
		}

		@Override
		public boolean isCanceled() {
			return canceled;
		}

	}

	@BeforeEach
	void setUp() throws Exception {
		thread = new CancellableOperationMonitorThread();
	}

	@AfterEach
	void tearDown() throws Exception {
		thread.shutdown();
	}

	@Test
	public void testShutdownAddOperation() throws InterruptedException {
		thread.shutdown();
		assertThrows(IllegalStateException.class, () -> thread.addOperation(new MockOperation()));
	}

	@Test
	public void testShutdownProcessOnce() throws InterruptedException {
		thread.shutdown();
		assertThrows(IllegalStateException.class, () -> thread.processOperations());
	}

	@Test
	public void testShutdownRemoveOperation() throws InterruptedException {
		thread.shutdown();
		assertThrows(IllegalStateException.class, () -> thread.removeOperation(new MockOperation()));
	}

	@Test
	public void testShutdownStart() throws InterruptedException {
		thread.shutdown();
		assertThrows(IllegalStateException.class, () -> thread.start());
	}

	@Test
	public void testShutdownTwice() throws InterruptedException {
		thread.start();
		assertTrue(thread.isAlive());
		thread.shutdown();
		assertFalse(thread.isAlive());
		thread.shutdown();
		assertFalse(thread.isAlive());
	}

	@Test
	public void testShutdown() throws Exception {
		assertFalse(thread.isAlive());
		thread.start();
		assertTrue(thread.isAlive());
		thread.shutdown();
		assertFalse(thread.isAlive());
	}

	@Test
	public void testShutdownNotStarted() throws Exception {
		assertFalse(thread.isAlive());
		thread.shutdown();
		assertFalse(thread.isAlive());
	}

	@Test
	public void testNotCancelOperation() throws Exception {
		MockOperation operation = new MockOperation();
		thread.addOperation(operation);
		assertFalse(operation.aborted);
		thread.processOperations();
		assertFalse(operation.aborted);
	}

	@Test
	public void testCancelOperation() throws Exception {
		MockOperation operation = new MockOperation();
		thread.addOperation(operation);
		assertFalse(operation.aborted);
		operation.canceled = true;
		thread.processOperations();
		assertTrue(operation.aborted);
	}

	@Test
	public void testAddRemoveOperation() throws Exception {
		MockOperation operation = new MockOperation();
		thread.addOperation(operation);
		assertTrue(thread.isAlive());
		thread.removeOperation(operation);
		assertTrue(thread.isAlive());
		operation.canceled = true;
		try {
			thread.processOperations();
		} catch (IllegalStateException expected) {
		}
		assertFalse(operation.aborted);
	}

}
