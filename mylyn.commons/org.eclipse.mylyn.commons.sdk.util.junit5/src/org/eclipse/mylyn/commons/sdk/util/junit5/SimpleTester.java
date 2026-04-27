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

package org.eclipse.mylyn.commons.sdk.util.junit5;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * Simple test class to test the new junit5 setup.
 */
@Timeout(value = 1, unit = TimeUnit.SECONDS) // overall timeout for each test
@MylynTestSetup
@TestMethodOrder(MethodOrderer.MethodName.class)
@SuppressWarnings("nls")
public class SimpleTester {
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

	@Test
	@EnabledOnOs(value = OS.AIX, disabledReason = "This test is enabled on AIX")
	public void disabledOnAix() {
		System.out.println("Unlikely to run since AIX is not common");
	}
}
