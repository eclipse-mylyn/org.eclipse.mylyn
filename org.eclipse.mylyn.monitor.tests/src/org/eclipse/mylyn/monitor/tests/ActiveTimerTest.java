/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.monitor.core.ActivityTimerThread;
import org.eclipse.mylyn.monitor.core.IActivityTimerListener;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
@SuppressWarnings("deprecation")
public class ActiveTimerTest extends TestCase {

	private boolean gotTimeOut = false;

	private ActivityTimerThread thread;

	private final IActivityTimerListener listener = new IActivityTimerListener() {

		public void fireInactive() {
			gotTimeOut = true;
			thread.kill();
		}

		public void fireActive(long start, long end) {
			// ignore
		}

		@SuppressWarnings("unused")
		public void fireActive() {
			// ignore
		}

	};

	private final IActivityTimerListener listener2 = new IActivityTimerListener() {

		public void fireInactive() {
			gotTimeOut = true;
		}

		public void fireActive(long start, long end) {
			// ignore
		}

		@SuppressWarnings("unused")
		public void fireActive() {
			// ignore

		}

	};

	public void testActiveTimer() {
		thread = new ActivityTimerThread(600, 100);
		thread.addListener(listener);
		int i = 0;
		gotTimeOut = false;
		thread.start();
		while (!gotTimeOut) {
			i++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		assertFalse("Too long of a wait (" + i + ")", i > 8);
		assertFalse("Too short of a wait (" + i + ")", i < 6);

		thread = new ActivityTimerThread(1000, 100);
		thread.addListener(listener2);
		i = 0;
		gotTimeOut = false;
		thread.start();
		for (int j = 0; j < 10; j++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			thread.resetTimer();
		}
		while (!gotTimeOut) {
			i++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		thread.kill();
		assertFalse("Too long of a wait (" + i + ")", i > 12);
		assertFalse("Too short of a wait (" + i + ")", i < 10);
	}
}
