/*******************************************************************************
 * Copyright (c) 2010, 2024 Tasktop Technologies and others.
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

package org.eclipse.mylyn.commons.sdk.util.junit4;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.DumpThreadTask;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Prints the name of each test to System.err when it started and dumps a stack trace of all thread to System.err if a test takes longer
 * than 10 minutes.
 *
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class ManagedTestSuite extends TestSuite {

	private static class Listener implements TestListener {

		/**
		 * Tests may execute in parallel and hence multiple dump threads maybe scheduled concurrently.
		 */
		private final ConcurrentHashMap<Test, DumpThreadTask> taskByTest = new ConcurrentHashMap<>();

		private final Timer timer = new Timer(true);

		@Override
		public void addError(Test test, Throwable t) {
			System.err.println("[ERROR]");
		}

		@Override
		public void addFailure(Test test, AssertionFailedError t) {
			System.err.println("[FAILURE]");
		}

		private void dumpList(String header, Enumeration<TestFailure> failures) {
			System.err.println(header);
			while (failures.hasMoreElements()) {
				TestFailure failure = failures.nextElement();
				System.err.print("  ");
				System.err.println(failure.toString());
			}
		}

		public void dumpResults(TestResult result) {
			if (result.failureCount() > 0) {
				System.err.println();
				dumpList("Failures: ", result.failures());
			}

			if (result.errorCount() > 0) {
				System.err.println();
				dumpList("Errors: ", result.errors());
			}

			int failedCount = result.errorCount() + result.failureCount();
			System.err.println();
			System.err.println(MessageFormat.format("{0} out of {1} tests failed", failedCount, result.runCount()));
		}

		@Override
		public void endTest(Test test) {
			DumpThreadTask task = taskByTest.remove(test);
			if (task != null) {
				task.cancel();
			}
			// clear flag in case timeout occurred
			Thread.interrupted();
		}

		@Override
		public void startTest(Test test) {
			startTest(test, false);
		}

		public void startTest(Test test, boolean silent) {
			if (!silent) {
				System.err.println("Running " + test.toString());
			}
			DumpThreadTask task = new DumpThreadTask(test.toString());
			taskByTest.put(test, task);
			timer.schedule(task, DELAY);
		}

	}

	public final static long DELAY = 10 * 60 * 1000;

	private final Listener listener = new Listener();

	public ManagedTestSuite() {
	}

	public ManagedTestSuite(String name) {
		super(name);
	}

	@Override
	public void run(TestResult result) {
		if (JUnitExecutionListener.getDefault() == null) {
			result.addListener(listener);
		}
		CommonTestUtil.fixProxyConfiguration();
		CommonTestUtil.dumpSystemInfo(System.err);
		super.run(result);
		if (JUnitExecutionListener.getDefault() == null) {
			listener.dumpResults(result);

			// add dummy test to dump threads in case shutdown hangs
			listener.startTest(new Test() {
				@Override
				public int countTestCases() {
					return 1;
				}

				@Override
				public void run(TestResult result) {
					// do nothing
				}

				@Override
				public String toString() {
					return "ShutdownWatchdog";
				}
			}, true);
		}
	}

}
