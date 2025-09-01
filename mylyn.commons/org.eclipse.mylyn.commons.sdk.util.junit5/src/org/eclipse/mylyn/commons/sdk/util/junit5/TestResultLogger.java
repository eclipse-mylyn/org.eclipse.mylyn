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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * JUnit 5 extension that logs test results to stderr. <br/>
 * To use, annotate test classes with @ExtendWith(TestResultLogger.class) or create a custom annotation that includes it, e.g.
 * {@link MylynTestExtension}.
 */
@SuppressWarnings("nls")
public class TestResultLogger implements TestWatcher {

	private static String formatTestName(ExtensionContext context) {
		String className = context.getRequiredTestClass().getSimpleName();
		String methodName = context.getRequiredTestMethod().getName();
		return className + "." + methodName;
	}

	private static void printWithReason(String prefix, ExtensionContext context, String reason) {
		System.err.println(prefix + formatTestName(context) + (reason != null && !reason.isEmpty() ? ", reason: " + reason : ""));
	}

	@Override
	public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
		printWithReason("Test disabled: ", context, reason.orElse(""));
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		// No-op or log success if desired
	}

	@Override
	public void testAborted(ExtensionContext context, Throwable cause) {
		printWithReason("Test aborted: ", context, String.valueOf(cause));
	}

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		printWithReason("Test failed: ", context, String.valueOf(cause));
	}
}