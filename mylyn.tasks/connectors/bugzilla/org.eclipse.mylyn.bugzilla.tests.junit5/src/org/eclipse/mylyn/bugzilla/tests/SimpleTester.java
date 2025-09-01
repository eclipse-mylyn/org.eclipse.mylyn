/*******************************************************************************
 * Copyright (c) 2025 george
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.TimeUnit;

import org.eclipse.mylyn.commons.sdk.util.junit5.TestMethodWrapper;
import org.eclipse.mylyn.commons.sdk.util.junit5.TestResultLogger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Simple test class to demonstrate the extensions.
 */
@Timeout(value = 1, unit = TimeUnit.SECONDS) // overall timeout for each test
@ExtendWith({ TestMethodWrapper.class, TestResultLogger.class })
public class SimpleTester {
	// Simple tests to demonstrate the extensions

	@Test
	public void pass() {
		System.out.println("Hello world");
		assertTrue(true);
	}

	@Test
	public void fail() {
		System.out.println("This should fail");
		assertTrue(false);
	}

	@Test
	@Disabled("This test is disabled")
	public void disabled() {
		System.out.println("This should be disabled");
	}

	@Test
	public void timeout() throws InterruptedException {
		Thread.sleep(2000);
		System.out.println("This should timeout");
		assertTrue(true);
	}

	@Test
	public void dynamicDisabled() {
		assumeTrue(false, "Dynamically disabled");
	}

}