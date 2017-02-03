/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.toolkit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.text.MessageFormat.format;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A {@link TestRule} that dumps a stack trace to {@code System.out}.
 */
public class StackDumpOnTimeoutRule implements TestRule {

	private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(1L);
	private Duration timeoutDuration;

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
		this.timeoutDuration = checkNotNull(timeoutDuration,"Must specify a timeout duration");
		checkArgument(timeoutDuration.toMillis() > 100L,"Timeout must be > 100ms");
	}
	
	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Timer timer = new Timer(true);
				try {
					timer.schedule(createDumpStackTraceTask(), timeoutDuration.toMillis(), timeoutDuration.toMillis());
					base.evaluate();
				} finally {
					timer.cancel();
				}
			}

		};
	}

	private TimerTask createDumpStackTraceTask() {
		return new TimerTask() {
			
			@Override
			public void run() {
				dumpStackTrace(System.out);
			}
		};
	}
	
	private void dumpStackTrace(PrintStream writer) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] infos = bean.dumpAllThreads(true, true);
		writer.println();
		// ThreadInfo doesn't give a complete stack trace, so we do it again here
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
