/*******************************************************************************
 * Copyright (c) 2016, 2024 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *     ArSysOp - ongoing support
  *     See git history
*******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util.junit4;

import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.mylyn.commons.sdk.util.DumpThreadTask;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

@SuppressWarnings("nls")
@RunListener.ThreadSafe
public class JUnitExecutionListener extends RunListener {

	private static JUnitExecutionListener INSTANCE;

	public static JUnitExecutionListener getDefault() {
		return INSTANCE;
	}

	public static JUnitExecutionListener createDefault() {
		if (INSTANCE == null) {
			INSTANCE = new JUnitExecutionListener();
			return INSTANCE;
		}
		return null;
	}

	private final CopyOnWriteArrayList<String> ingored = new CopyOnWriteArrayList<>();

	/**
	 * Tests may execute in parallel and hence multiple dump threads maybe scheduled concurrently.
	 */
	private final ConcurrentHashMap<Description, DumpThreadTask> taskByTest = new ConcurrentHashMap<>();

	public final static long DELAY = 10 * 60 * 1000;

	private final Timer timer = new Timer(true);

	@Override
	public void testRunFinished(Result result) throws Exception {
		dumpResults(result);
	}

	@Override
	public void testStarted(Description description) throws Exception {
		System.err.println("Running " + description.toString());
		DumpThreadTask task = new DumpThreadTask(description.toString());
		taskByTest.put(description, task);
		timer.schedule(task, DELAY);
	}

	@Override
	public void testFinished(Description description) throws Exception {
		DumpThreadTask task = taskByTest.remove(description);
		if (task != null) {
			task.cancel();
		}
		// clear flag in case timeout occurred
		Thread.interrupted();
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		if (failure.getException() instanceof AssertionError) {
			System.err.println("[FAILURE]");
		} else {
			System.err.println("[ERROR]");
		}
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		System.err.println("Ignored " + description.getMethodName());
		ingored.add(description.getMethodName());
	}

	private void dumpErrorList(List<Failure> failures) {
		System.err.println("Error: ");
		for (Failure failure : failures) {
			if (failure.getException() instanceof AssertionError) {
				continue;
			}

			System.err.print("  ");
			System.err.println(failure.toString());
		}
	}

	private void dumpFailureList(List<Failure> failures) {
		System.err.println("Failures: ");
		for (Failure failure : failures) {
			if (!(failure.getException() instanceof AssertionError)) {
				continue;
			}

			System.err.print("  ");
			System.err.println(failure.toString());
		}
	}

	private void dumpIgnored() {
		System.err.println("Ignored: ");
		for (String element : ingored) {
			System.err.print("  ");
			System.err.println(element);
		}
	}

	private void dumpResults(Result result) {
		if (result.getFailureCount() > 0) {
			System.err.println();
			dumpFailureList(result.getFailures());
			dumpErrorList(result.getFailures());
		}
		if (result.getIgnoreCount() > 0) {
			System.err.println();
			dumpIgnored();
		}

		System.err.println();
		System.err.println(MessageFormat.format("{0} out of {1} tests failed ({2} tests skipped)",
				result.getFailureCount(), result.getRunCount() + result.getIgnoreCount(), result.getIgnoreCount()));
	}
}
