/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core;

import java.util.Iterator;

import org.eclipse.mylyn.commons.core.CommonListenerList;
import org.eclipse.mylyn.commons.core.CommonListenerList.Notifier;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class CommonListenerListTest extends TestCase {

	private class Listener {

		private boolean notified;

	}

	public void testAddRemove() {
		final Listener addedListener = new Listener();
		CommonListenerList<Listener> list = new CommonListenerList<>("a");

		list.add(addedListener);
		assertTrue(list.iterator().hasNext());
		assertSame(addedListener, list.iterator().next());

		list.remove(addedListener);
		assertFalse(list.iterator().hasNext());
	}

	public void testAddTwice() {
		final Listener addedListener = new Listener();
		CommonListenerList<Listener> list = new CommonListenerList<>("a");
		list.add(addedListener);
		list.add(addedListener);

		Iterator<Listener> iterator = list.iterator();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertFalse(iterator.hasNext());
	}

	public void testIterator() {
		final Listener listener1 = new Listener();
		final Listener listener2 = new Listener();
		final Listener listener3 = new Listener();
		CommonListenerList<Listener> list = new CommonListenerList<>("a");
		list.add(listener1);
		list.add(listener2);
		list.add(listener3);
		list.add(listener1);

		Iterator<Listener> iterator = list.iterator();
		assertSame(listener1, iterator.next());
		assertSame(listener2, iterator.next());
		assertSame(listener3, iterator.next());
	}

	public void testNotify() {
		final Listener addedListener = new Listener();
		CommonListenerList<Listener> list = new CommonListenerList<>("a");
		list.add(addedListener);

		list.notify(new Notifier<Listener>() {
			@Override
			public void run(Listener listener) throws Exception {
				assertSame(listener, addedListener);
				addedListener.notified = true;
			}
		});
		assertTrue(addedListener.notified);
	}

	public void testNotifyException() {
		final Listener addedListener = new Listener();
		CommonListenerList<Listener> list = new CommonListenerList<>("a");
		list.add(addedListener);

		list.notify(new Notifier<Listener>() {
			@Override
			public void run(Listener listener) throws Exception {
				// should cause listener to get removed
				throw new LinkageError("Deliberately thrown by testNotifyException to check handling of LinkageError.");
			}
		});
		assertFalse(list.iterator().hasNext());

		list.notify(new Notifier<Listener>() {
			@Override
			public void run(Listener listener) throws Exception {
				addedListener.notified = true;
			}
		});
		assertFalse(addedListener.notified);
	}

}
