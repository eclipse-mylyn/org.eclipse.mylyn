/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.support;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Prints the name of each test to System.err when it started and dumps a stack trace of all thread to System.err if a
 * test takes longer than 10 minutes.
 * 
 * @author Steffen Pingel
 */
public class ManagedTestSuite extends TestSuite {

	private class DumpTreadTask extends TimerTask {

		private final Test test;

		public DumpTreadTask(Test test) {
			this.test = test;
		}

		@Override
		public void run() {
			StringBuffer sb = new StringBuffer();
			sb.append(MessageFormat.format("Test {0} is taking too long:\n", test.toString()));
			Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
			for (Map.Entry<Thread, StackTraceElement[]> entry : traces.entrySet()) {
				sb.append(entry.getKey().toString());
				sb.append("\n");
				for (StackTraceElement element : entry.getValue()) {
					sb.append("  ");
					sb.append(element.toString());
					sb.append("\n");
				}
				sb.append("\n");
			}
			System.err.println(sb.toString());
		}

	}

	private class Listener implements TestListener {

		private DumpTreadTask task;

		private final Timer timer = new Timer();

		public void addError(Test test, Throwable t) {
			System.err.println("[ERROR]");
		}

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
			System.err.println();
			dumpList("Failures: ", result.failures());

			System.err.println();
			dumpList("Errors: ", result.errors());

			int failedCount = result.errorCount() + result.failureCount();
			System.err.println();
			System.err.println(MessageFormat.format("{0} out of {1} tests failed", failedCount, result.runCount()));
		}

		public void endTest(Test test) {
			if (task != null) {
				task.cancel();
				task = null;
			}
		}

		public void startTest(Test test) {
			System.err.println("Running " + test.toString());
			task = new DumpTreadTask(test);
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
		result.addListener(listener);
		super.run(result);
		listener.dumpResults(result);
	}

}
