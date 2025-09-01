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

/**
 * JUnit 5 listener that prints a summary of failed tests to stderr after a test suite
 * has finished executing. <br/>
 *
 * To use, register this listener in
 * META-INF/services/org.junit.platform.launcher.TestExecutionListener
 * in the test bundle.
 *
 */
package org.eclipse.mylyn.commons.sdk.util.junit5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;


@SuppressWarnings("nls")
public class SuiteFailureSummaryListener implements TestExecutionListener {
	private final SummaryGeneratingListener delegate = new SummaryGeneratingListener();

	// Track non-successful tests and their reasons
	private final Map<TestIdentifier, String> skippedTests = new HashMap<>();

	@Override
	public void testPlanExecutionStarted(TestPlan testPlan) {
		delegate.testPlanExecutionStarted(testPlan);
	}

	@Override
	public void executionStarted(TestIdentifier testIdentifier) {
		delegate.executionStarted(testIdentifier);
	}

	@Override
	public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
		delegate.executionFinished(testIdentifier, testExecutionResult);
		if (testExecutionResult.getStatus() == TestExecutionResult.Status.ABORTED) {
			Throwable cause = testExecutionResult.getThrowable().orElse(null);
			if (cause != null) {
				skippedTests.put(testIdentifier, cause.getMessage());
			}
		}
	}

	@Override
	public void executionSkipped(TestIdentifier testIdentifier, String reason) {
		delegate.executionSkipped(testIdentifier, reason);
		skippedTests.put(testIdentifier, reason);
	}

	@Override
	public void testPlanExecutionFinished(TestPlan testPlan) {
		delegate.testPlanExecutionFinished(testPlan);
		TestExecutionSummary summary = delegate.getSummary();
		List<TestExecutionSummary.Failure> failures = summary.getFailures();
		if (!failures.isEmpty() || !skippedTests.isEmpty()) {
			System.err.println("\n=== Non-successful tests summary ===\n");
			String bodyBreak = "";
			if (!failures.isEmpty()) {
				System.err.println("-- Failed Tests --");
				for (TestExecutionSummary.Failure failure : failures) {
					TestIdentifier id = failure.getTestIdentifier();
					printTestDetail(id, failure.getException().toString());
					bodyBreak = "\n";
				}
			}
			if (!skippedTests.isEmpty()) {
				System.err.println(bodyBreak + "-- 'Skipped' Tests --");
				for (Map.Entry<TestIdentifier, String> entry : skippedTests.entrySet()) {
					TestIdentifier id = entry.getKey();
					printTestDetail(id, entry.getValue());
				}
			}
			System.err.println("===========================\n");
		}
	}

	private void printTestDetail(TestIdentifier id, String reason) {
		String className = null;
		String methodName = id.getDisplayName();
		if (id.getSource().isPresent()) {
			Object source = id.getSource().get();
			if (source instanceof MethodSource ms) {
				className = ms.getJavaClass().getSimpleName();
				methodName = ms.getMethodName();
			} else if (source instanceof ClassSource cs) {
				className = cs.getJavaClass().getSimpleName();
			}
		}
		System.err.println(testDetail(className, methodName, reason));
	}

	private String testDetail(String className, String methodName, String reson) {
		return (className != null ? className + "." : "") + methodName + (reson != null ? ": " + reson : "");
	}
}