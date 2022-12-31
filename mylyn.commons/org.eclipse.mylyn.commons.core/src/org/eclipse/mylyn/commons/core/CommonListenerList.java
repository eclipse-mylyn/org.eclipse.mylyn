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

package org.eclipse.mylyn.commons.core;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * A list like class for managing listeners. It is safe to call this class from different threads concurrently.
 * 
 * @since 3.7
 */
public class CommonListenerList<T> implements Iterable<T> {

	/**
	 * Subclasses should extend to not
	 */
	public static abstract class Notifier<T> {

		/**
		 * Fires an event to <code>listener</code>.
		 * 
		 * @param listener
		 *            the listener to be notified
		 * @throws Exception
		 *             indicates a unrecoverable problem with <code>listener</code>
		 */
		public abstract void run(T listener) throws Exception;

	}

	private final CopyOnWriteArrayList<T> listeners;

	private final String pluginId;

	/**
	 * Constructs an empty list.
	 * 
	 * @param pluginId
	 *            the ID of the bundle that is managing this instance
	 */
	public CommonListenerList(String pluginId) {
		Assert.isNotNull(pluginId);
		this.pluginId = pluginId;
		this.listeners = new CopyOnWriteArrayList<T>();
	}

	/**
	 * Adds <code>listener</code> to the list of listeners.
	 */
	public void add(T listener) {
		Assert.isNotNull(listener);
		listeners.addIfAbsent(listener);
	}

	/**
	 * Iterates over the list of listeners.
	 */
	public Iterator<T> iterator() {
		return listeners.iterator();
	}

	/**
	 * Invokes <code>runnable</code> for each listener. If {@link Notifier#run(Object)} throws an exception the
	 * corresponding listener is removed from the list and a message is logged.
	 */
	public void notify(final Notifier<T> runnable) {
		for (final T listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, pluginId,
							NLS.bind("Unexpected error notifying listener {0}", listener.getClass()), e)); //$NON-NLS-1$
					remove(listener);
				}

				public void run() throws Exception {
					runnable.run(listener);
				}
			});
		}
	}

	/**
	 * Removes <code>listener</code> to the list of listeners.
	 */
	public void remove(T listener) {
		listeners.remove(listener);
	}

}
