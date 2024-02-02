package org.eclipse.mylyn.wikitext.toolkit;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A rule that performs an action on timeout.
 *
 * @since 3.0
 */
public abstract class TimeoutActionRule implements TestRule {
	private Duration timeoutDuration;

	/**
	 * Creates the rule with the specified timeout.
	 */
	public TimeoutActionRule(Duration timeoutDuration) {
		this.timeoutDuration = requireNonNull(timeoutDuration, "Must specify a timeout duration");
		checkArgument(timeoutDuration.toMillis() > 100L, "Timeout must be > 100ms");
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
