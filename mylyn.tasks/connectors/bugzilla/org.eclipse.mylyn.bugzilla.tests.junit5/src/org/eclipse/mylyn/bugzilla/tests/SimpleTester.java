/*******************************************************************************
 * Copyright (c) 2025 George Lindholm
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

import org.eclipse.mylyn.commons.sdk.util.junit5.MylynTestExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Simple test class to test the new junit5 extensions.
 */
@Timeout(value = 1, unit = TimeUnit.SECONDS) // overall timeout for each test
@MylynTestExtension
@SuppressWarnings("nls")
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
		System.out.println("This should timeout");
		Thread.sleep(2000);
		assertTrue(true);
	}

	@Test
	public void dynamicDisabled() {
		System.out.println("This should be disabled dynamically");
		assumeTrue(false, "Dynamically disabled");
	}

}