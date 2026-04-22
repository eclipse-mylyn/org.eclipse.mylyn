/*******************************************************************************
 * Copyright (c) 2012, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.tests.core;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.mylyn.internal.commons.repositories.ui.UiLocationService;
import org.eclipse.mylyn.internal.commons.repositories.ui.UiSecureCredentialsStore;
import org.eclipse.swt.widgets.Display;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class UiSecureCredentialsStoreTest {
	private static final String DEADLOCK_ERROR_MESSAGE = "Aborting request to prevent deadlock accessing secure storage";

	private static class TestSecureCredentialsStore extends UiSecureCredentialsStore {
		public TestSecureCredentialsStore() {
			super(UiSecureCredentialsStoreTest.class.getName());
		}

		public ILock getLock() {
			return UiSecureCredentialsStore.getSecurePreferencesRootLock();
		}
	}

	protected TestSecureCredentialsStore createCredentialsStore() {
		TestSecureCredentialsStore store = new TestSecureCredentialsStore();
		store.clear();
		return store;
	}

	@Test
	public void testUiLocationService() throws Exception {
		assertTrue(new UiLocationService().getCredentialsStore("test") instanceof UiSecureCredentialsStore);
	}

	@Test
	public void testDetectDeadlockOnUiThread() throws Exception {
		assumeFalse(System.getProperty("eclipse.launcher") != null || System.getProperty("eclipse.application") != null,
				"Secure Storage not set up by the PDE"); // FIXME secure storage not set up

		assertNotNull(Display.getCurrent());
		TestSecureCredentialsStore store = createCredentialsStore();
		store.put("key", "value", true);
		final ILock lock = store.getLock();
		assertNotNull(lock);
		final AtomicBoolean lockAcquired = new AtomicBoolean(false);
		final AtomicBoolean lockReleased = new AtomicBoolean(false);
		final AtomicBoolean done = new AtomicBoolean(false);
		try {

			runOnBackgroundThread(false, () -> {
				try {
					assertTrue(lock.acquire(3000));
					lockAcquired.set(true);
					while (!done.get()) {
						// hold the lock
					}
				} catch (InterruptedException e) {
					fail("Interrupted trying to acquire SecurePreferencesRoot lock");
				} finally {
					lock.release();
					lockReleased.set(true);
				}
			});
			while (!lockAcquired.get()) {
				// wait for background thread to acquire lock
			}

			Throwable e = assertThrows(RuntimeException.class, () -> store.get("key", null));
			assertEquals(DEADLOCK_ERROR_MESSAGE, e.getMessage());

			e = assertThrows(RuntimeException.class, () -> store.put("key", "newValue", true));
			assertEquals(DEADLOCK_ERROR_MESSAGE, e.getMessage());

		} finally {
			done.set(true);
		}
		while (!lockReleased.get()) {
			// wait for background thread to release lock
		}
		assertEquals("value", store.get("key", null));

		store.put("key", "newValue", true);
		assertEquals("newValue", store.get("key", null));
	}

	@Test
	public void testDoNotDetectDeadlockWhenUiThreadAlreadyHoldsLock() throws Exception {
		assertNotNull(Display.getCurrent());
		TestSecureCredentialsStore store = createCredentialsStore();
		store.put("key", "value", true);
		ILock lock = store.getLock();
		assertNotNull(lock);

		try {
			assertTrue(lock.acquire(3000));
			assertEquals("value", store.get("key", null));

			store.put("key", "newValue", true);
			assertEquals("newValue", store.get("key", null));
		} catch (InterruptedException e) {
			fail("Interrupted trying to acquire SecurePreferencesRoot lock");
		} finally {
			lock.release();
		}
	}

	@Test
	public void testDetectDeadlockOnBackgroundThread() throws Exception {
		assumeFalse(System.getProperty("eclipse.launcher") != null || System.getProperty("eclipse.application") != null,
				"Secure Storage not set up by the PDE"); // FIXME secure storage not set up

		assertNotNull(Display.getCurrent());
		final TestSecureCredentialsStore store = createCredentialsStore();
		store.put("key", "value", true);
		ILock lock = store.getLock();
		assertNotNull(lock);

		try {
			assertTrue(lock.acquire(3000));
			runOnBackgroundThread(true, () -> {
				Throwable e = assertThrows(RuntimeException.class, () -> store.get("key", null));
				assertEquals(DEADLOCK_ERROR_MESSAGE, e.getMessage());

				e = assertThrows(RuntimeException.class, () -> store.put("key", "newValue", true));
				assertEquals(DEADLOCK_ERROR_MESSAGE, e.getMessage());
			});
		} catch (InterruptedException e) {
			fail("Interrupted trying to acquire SecurePreferencesRoot lock");
		} finally {
			lock.release();
		}
		runOnBackgroundThread(true, () -> {
			assertEquals("value", store.get("key", null));

			store.put("key", "newValue", true);
			assertEquals("newValue", store.get("key", null));
		});
	}

	protected void runOnBackgroundThread(boolean join, final Runnable runnable) throws AssertionError {
		final AtomicReference<AssertionError> assertionError = new AtomicReference<>();
		Thread thread = new Thread(() -> {
			try {
				runnable.run();
			} catch (AssertionError e) {
				assertionError.set(e);
			}

		});
		thread.start();
		if (join) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				fail("Interrupted while joining thread");
			}
		}
		if (assertionError.get() != null) {
			throw assertionError.get();
		}
	}

	@Test
	public void testRunOnBackgroundThread() {
		assertThrows(AssertionError.class, () -> runOnBackgroundThread(true, Assertions::fail));
	}

}
