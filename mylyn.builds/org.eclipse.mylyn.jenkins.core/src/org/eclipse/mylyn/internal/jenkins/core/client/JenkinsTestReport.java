/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core.client;

import org.eclipse.mylyn.internal.hudson.model.HudsonTasksJunitTestResult;
import org.eclipse.mylyn.internal.hudson.model.HudsonTasksTestAggregatedTestResultAction;

/**
 * Container class for different kinds of Hudson test reports.
 *
 * @author Steffen Pingel
 */
public class JenkinsTestReport {

	private final HudsonTasksTestAggregatedTestResultAction aggregatedResult;

	private final HudsonTasksJunitTestResult junitResult;

	public JenkinsTestReport(HudsonTasksJunitTestResult junitResult) {
		aggregatedResult = null;
		this.junitResult = junitResult;
	}

	public JenkinsTestReport(HudsonTasksTestAggregatedTestResultAction aggregatedResult) {
		this.aggregatedResult = aggregatedResult;
		junitResult = null;
	}

	public HudsonTasksTestAggregatedTestResultAction getAggregatedResult() {
		return aggregatedResult;
	}

	public HudsonTasksJunitTestResult getJunitResult() {
		return junitResult;
	}

}
