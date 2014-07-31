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

package org.eclipse.mylyn.commons.repositories.tests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.mylyn.internal.commons.repositories.ui.UiLocationService;
import org.eclipse.mylyn.internal.commons.repositories.ui.UiSecureCredentialsStore;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

public class UiSecureCredentialsStoreTest {
	private static final String DEADLOCK_ERROR_MESSAGE = "Aborting request to prevent deadlock accessing secure storage";

	private class TestSecureCredentialsStore extends UiSecureCredentialsStore {
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
		assertNotNull(Display.getCurrent());
		TestSecureCredentialsStore store = createCredentialsStore();
		store.put("key", "value", true);
		ILock lock = store.getLock();
		assertNotNull(lock);

		try {
			assertTrue(lock.acquire(3000));
			try {
				store.get("key", null);
				fail("Expected exception");
			} catch (RuntimeException e) {// expected
				assertEquals(DEADLOCK_ERROR_MESSAGE, e.getMessage());
			}
			try {
				store.put("key", "newValue", true);
				fail("Expected exception");
			} catch (RuntimeException e) {// expected
				assertEquals(DEADLOCK_ERROR_MESSAGE, e.getMessage());
			}
		} catch (InterruptedException e) {
			fail("Interrupted trying to acquire SecurePreferencesRoot lock");
		} finally {
			lock.release();
		}
		assertEquals("value", store.get("key", null));

		store.put("key", "newValue", true);
		assertEquals("newValue", store.get("key", null));
	}

	@Test
	public void testDetectDeadlockOnBackgroundThread() throws Exception {
		assertNotNull(Display.getCurrent());
		final TestSecureCredentialsStore store = createCredentialsStore();
		store.put("key", "value", true);
		ILock lock = store.getLock();
		assertNotNull(lock);

		try {
			assertTrue(lock.acquire(3000));
			runOnBackgroundThread(new Runnable() {
				@Override
				public void run() {
					try {
						store.get("key", null);
						fail("Expected exception");
					} catch (RuntimeException e) {// expected
						assertEquals(DEADLOCK_ERROR_MESSAGE, e.getMessage());
					}
					try {
						store.put("key", "newValue", true);
						fail("Expected exception");
					} catch (RuntimeException e) {// expected
						assertEquals(DEADLOCK_ERROR_MESSAGE, e.getMessage());
					}
				}
			});
		} catch (InterruptedException e) {
			fail("Interrupted trying to acquire SecurePreferencesRoot lock");
		} finally {
			lock.release();
		}
		runOnBackgroundThread(new Runnable() {
			@Override
			public void run() {
				assertEquals("value", store.get("key", null));

				store.put("key", "newValue", true);
				assertEquals("newValue", store.get("key", null));
			}
		});
	}

	protected void runOnBackgroundThread(final Runnable runnable) throws AssertionError {
		final AtomicReference<AssertionError> assertionError = new AtomicReference<AssertionError>();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch (AssertionError e) {
					assertionError.set(e);
				}

			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			fail("Interrupted while joining thread");
		}
		if (assertionError.get() != null) {
			throw assertionError.get();
		}
	}

	@Test
	public void testRunOnBackgroundThread() throws Exception {
		try {
			runOnBackgroundThread(new Runnable() {
				public void run() {
					fail();
				}
			});
		} catch (AssertionError e) {// expected
			return;
		}
		fail("Expected AssertionError");
	}

}
