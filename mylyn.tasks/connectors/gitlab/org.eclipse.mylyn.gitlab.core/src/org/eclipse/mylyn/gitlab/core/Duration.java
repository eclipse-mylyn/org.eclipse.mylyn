package org.eclipse.mylyn.gitlab.core;

import java.util.concurrent.TimeUnit;

public class Duration {
	final TimeUnit unit;

	final long value;

	public Duration(long value, TimeUnit unit) {
		this.unit = unit;
		this.value = value;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public long getValue() {
		return value;
	}

}
