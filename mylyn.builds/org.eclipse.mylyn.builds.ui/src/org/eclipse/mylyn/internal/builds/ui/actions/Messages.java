/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.builds.ui.actions.messages"; //$NON-NLS-1$

	public static String AbortBuildAction_abortBuild;

	public static String NewTaskFromBuildAction_buildLabel;

	public static String NewTaskFromBuildAction_buildLabelStatus;

	public static String NewTaskFromBuildAction_buildResultsAt;

	public static String NewTaskFromBuildAction_ChangedFiles;

	public static String NewTaskFromBuildAction_changes;

	public static String NewTaskFromBuildAction_duration;

	public static String NewTaskFromBuildAction_failedTests;

	public static String NewTaskFromBuildAction_newTaskFromBuild;

	public static String NewTaskFromBuildAction_newTaskFromBuildToolTip;

	public static String NewTaskFromBuildAction_testResults;

	public static String RefreshBuildEditorAction_RefreshBuildEditor;

	public static String RunBuildAction_runBuild;

	public static String RunBuildAction_runBuildToolTip;

	public static String ShowBuildOutputAction_showOutput;

	public static String ShowBuildOutputAction_showOutputInConsole;

	public static String ShowHistoryAction_showHistory;

	public static String ShowHistoryAction_showPlanInHistoryView;

	public static String ShowTestResultsAction_showTestResults;

	public static String ShowTestResultsAction_showTestResultsInJUnitView;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
