/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.text.MessageFormat;
import java.util.Map;
import java.util.TimerTask;

public class DumpThreadTask extends TimerTask {

	private final String testName;

	private final Thread testThread;

	public DumpThreadTask(String testName) {
		this.testName = testName;
		this.testThread = Thread.currentThread();
	}

	@Override
	public void run() {
		StringBuffer sb = new StringBuffer();
		sb.append(MessageFormat.format("Test {0} is taking too long:\n", testName));
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
