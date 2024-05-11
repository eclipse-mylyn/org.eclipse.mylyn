/*******************************************************************************
 * Copyright (c) 2017, 2024 Contributors to the Eclipse Foundation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     see git history
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.toolkit;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.Validate;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A rule that performs an action on timeout.
 *
 * @since 3.0
 */
@SuppressWarnings("nls")
public abstract class TimeoutActionRule implements TestRule {
	private Duration timeoutDuration;

	/**
	 * Creates the rule with the specified timeout.
	 */
	public TimeoutActionRule(Duration timeoutDuration) {
		this.timeoutDuration = requireNonNull(timeoutDuration, "Must specify a timeout duration");
		Validate.isTrue(timeoutDuration.toMillis() > 100L, "Timeout must be > 100ms");
	}

	/**
	 * Performs the action associated with the timeout.
	 */
	protected abstract void performAction();

	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Timer timer = new Timer(true);
				try {
					timer.schedule(createActionTask(), timeoutDuration.toMillis(), timeoutDuration.toMillis());
					base.evaluate();
				} finally {
					timer.cancel();
				}
			}

		};
	}

	TimerTask createActionTask() {
		return new TimerTask() {

			@Override
			public void run() {
				performAction();
			}
		};
	}
}
