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

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 condition that enables a test if it needs a CI server to complete. <br/>
 * Usage: @EnabledIfCICondition
 */
@SuppressWarnings({ "nls", "restriction" })
public class EnabledIfCICondition implements ExecutionCondition {
	private static final ConditionEvaluationResult ENABLED = ConditionEvaluationResult
			.enabled("CI Server can be used for unit tests");

	private static final ConditionEvaluationResult DISABLED = ConditionEvaluationResult
			.disabled("Do not try to use CI server for unit tests");

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		boolean isLocalOnly = CommonTestUtil.runNonCIServerTestsOnly();
		return isLocalOnly ? DISABLED : ENABLED;
	}
}
