/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.builds.ui.editor.messages"; //$NON-NLS-1$

	public static String ActionPart_showOutputInAConsole;

	public static String ActionPart_showTestResultsInJUnitView;

	public static String ArtifactsPart_artifacts;

	public static String ArtifactsPart_artifactsLabel;

	public static String ArtifactsPart_collapseAll;

	public static String ArtifactsPart_noArtifacts;

	public static String ArtifactsPart_rootFolder;

	public static String BuildEditor_build;

	public static String BuildEditor_buildLabel;

	public static String BuildEditor_details;

	public static String BuildEditor_openWithWebBrowser;

	public static String BuildEditor_failedToRetrieveBuildInformation;

	public static String BuildEditor_retrievingBuild;

	public static String BuildOutputPart_output;

	public static String BuildOutputPart_showOutputInConsole;

	public static String ChangesPart_changesPartName;

	public static String ChangesPart_couldNotDetermineChangeRevisionsForTheSelectedFile;

	public static String ChangesPart_fileNotAvailable;

	public static String ChangesPart_noChanges;

	public static String ChangesPart_noExtensionAvailbleForFile;

	public static String ChangesPart_unexpectedError;

	public static String HeaderPart_build;

	public static String HeaderPart_duration;

	public static String HeaderPart_executingFor;

	public static String HeaderPart_plan;

	public static String HeaderPart_running;

	public static String HeaderPart_status;

	public static String HeaderPart_unknown;

	public static String SummaryPart_cause;

	public static String SummaryPart_startedOn;

	public static String SummaryPart_summary;

	public static String TestResultPart_failed;

	public static String TestResultPart_ignored;

	public static String TestResultPart_noResults;

	public static String TestResultPart_passed;

	public static String TestResultPart_showFailuresOnly;

	public static String TestResultPart_testResults;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
