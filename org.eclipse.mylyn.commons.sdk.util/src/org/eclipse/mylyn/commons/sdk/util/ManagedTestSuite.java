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

package org.eclipse.mylyn.commons.sdk.util;

import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;

/**
 * Prints the name of each test to System.err when it started and dumps a stack trace of all thread to System.err if a
 * test takes longer than 10 minutes.
 * 
 * @author Steffen Pingel
 */
public class ManagedTestSuite extends TestSuite {

	private class DumpThreadTask extends TimerTask {

		private final Test test;

		private final Thread testThread;

		public DumpThreadTask(Test test) {
			this.test = test;
			this.testThread = Thread.currentThread();
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

			System.err.println("Sending interrupt to thread: " + testThread.toString());
			testThread.interrupt();
		}

	}

	private class Listener implements TestListener {

		/**
		 * Tests may execute in parallel and hence multiple dump threads maybe scheduled concurrently.
		 */
		private final ConcurrentHashMap<Test, DumpThreadTask> taskByTest = new ConcurrentHashMap<Test, ManagedTestSuite.DumpThreadTask>();

		private final Timer timer = new Timer(true);

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

		public void endTest(Test test) {
			DumpThreadTask task = taskByTest.remove(test);
			if (task != null) {
				task.cancel();
			}
		}

		public void startTest(Test test) {
			startTest(test, false);
		}

		public void startTest(Test test, boolean silent) {
			if (!silent) {
				System.err.println("Running " + test.toString());
			}
			DumpThreadTask task = new DumpThreadTask(test);
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
		result.addListener(listener);
		dumpSystemInfo();
		super.run(result);
		listener.dumpResults(result);

		// add dummy test to dump threads in case shutdown hangs
		listener.startTest(new Test() {
			public int countTestCases() {
				return 1;
			}

			public void run(TestResult result) {
				// do nothing
			}

			@Override
			public String toString() {
				return "ShutdownWatchdog";
			}
		}, true);
	}

	private static void dumpSystemInfo() {
		if (Platform.isRunning() && CommonsNetPlugin.getProxyService() != null
				&& CommonsNetPlugin.getProxyService().isSystemProxiesEnabled()
				&& !CommonsNetPlugin.getProxyService().hasSystemProxies()) {
			// XXX e3.5/gtk.x86_64 activate manual proxy configuration which
			// defaults to Java system properties if system proxy support is
			// not available
			System.err.println("Forcing manual proxy configuration");
			CommonsNetPlugin.getProxyService().setSystemProxiesEnabled(false);
			CommonsNetPlugin.getProxyService().setProxiesEnabled(true);
		}

		Properties p = System.getProperties();
		if (Platform.isRunning()) {
			p.put("build.system", Platform.getOS() + "-" + Platform.getOSArch() + "-" + Platform.getWS());
		} else {
			p.put("build.system", "standalone");
		}
		String info = "System: ${os.name} ${os.version} (${os.arch}) / ${build.system} / ${java.vendor} ${java.vm.name} ${java.version}";
		for (Entry<Object, Object> entry : p.entrySet()) {
			info = info.replaceFirst(Pattern.quote("${" + entry.getKey() + "}"), entry.getValue().toString());
		}
		System.err.println(info);
		System.err.print("Proxy : " + WebUtil.getProxyForUrl("http://mylyn.eclipse.org") + " (Platform)");
		try {
			System.err.print(" / " + ProxySelector.getDefault().select(new URI("http://mylyn.eclipse.org")) + " (Java)");
		} catch (URISyntaxException e) {
			// ignore
		}
		System.err.println();
		System.err.println();
	}

}
