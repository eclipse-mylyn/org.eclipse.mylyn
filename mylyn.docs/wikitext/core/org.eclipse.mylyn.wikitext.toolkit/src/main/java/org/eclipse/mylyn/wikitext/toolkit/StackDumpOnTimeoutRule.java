/*******************************************************************************
 * Copyright (c) 2016, 2024 Tasktop Technologies Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.toolkit;

import static java.text.MessageFormat.format;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Duration;

import org.junit.rules.TestRule;

/**
 * A {@link TestRule} that dumps a stack trace to {@code System.out}.
 *
 * @since 3.0
 */
@SuppressWarnings("nls")
public class StackDumpOnTimeoutRule extends TimeoutActionRule {

	private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(1L);

	/**
	 * Creates the rule with the default timeout of 1 minute.
	 */
	public StackDumpOnTimeoutRule() {
		this(DEFAULT_TIMEOUT);
	}

	/**
	 * Creates the rule with the specified timeout.
	 */
	public StackDumpOnTimeoutRule(Duration timeoutDuration) {
		super(timeoutDuration);
	}

	@Override
	protected void performAction() {
		dumpStackTrace(System.out);
	}

	private void dumpStackTrace(PrintStream writer) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] infos = bean.dumpAllThreads(true, true);
		writer.println();
		// ThreadInfo doesn't give a complete stack trace, so we do it again
		// here
		writer.println("***********");
		writer.println("All threads complete stack trace:");
		writer.println();
		for (ThreadInfo info : infos) {
			writer.println(format("Thread id {0} name {1}", info.getThreadId(), info.getThreadName()));
			for (StackTraceElement stackElement : info.getStackTrace()) {
				writer.print("\tat ");
				writer.println(stackElement);
			}
		}
	}
}
