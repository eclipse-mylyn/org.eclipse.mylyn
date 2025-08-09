/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.actions;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.TestCaseResult;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class NewTaskFromBuildAction extends BaseSelectionListenerAction {

	public NewTaskFromBuildAction() {
		super(Messages.NewTaskFromBuildAction_newTaskFromBuild);
		setToolTipText(Messages.NewTaskFromBuildAction_newTaskFromBuildToolTip);
		setImageDescriptor(TasksUiImages.TASK_NEW);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection.getFirstElement() instanceof IBuildPlan || selection.getFirstElement() instanceof IBuild;
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof IBuild) {
			createTask((IBuild) selection);
		}
	}

	public static boolean createTask(final IBuild build) {
		TaskMapping mapping = new TaskMapping() {
			@Override
			public String getSummary() {
				BuildStatus status = build.getStatus();
				if (status != null) {
					return NLS.bind(Messages.NewTaskFromBuildAction_buildLabelStatus, build.getLabel(),
							status.toString());
				} else {
					return NLS.bind(Messages.NewTaskFromBuildAction_buildLabel, build.getLabel());
				}
			}

			@Override
			public String getDescription() {
				StringBuilder sb = new StringBuilder();
				sb.append(NLS.bind(Messages.NewTaskFromBuildAction_buildResultsAt, build.getUrl()));
				sb.append("\n\n"); //$NON-NLS-1$
				if (build.getChangeSet() != null && build.getChangeSet().getChanges().size() > 0) {
					sb.append(Messages.NewTaskFromBuildAction_changes);
					sb.append("\n\n"); //$NON-NLS-1$
					sb.append(Messages.NewTaskFromBuildAction_ChangedFiles);
					sb.append("\n"); //$NON-NLS-1$
					for (IChange change : build.getChangeSet().getChanges()) {
//						if (change.getAuthor() != null) {
//							sb.append(NLS.bind("Changes by {0}", change.getAuthor().getLabel()));
//							sb.append("\n");
//						}
						for (IChangeArtifact artifact : change.getArtifacts()) {
							sb.append(" " + artifact.getFile()); //$NON-NLS-1$
						}
						sb.append("\n"); //$NON-NLS-1$
					}
					sb.append("\n"); //$NON-NLS-1$
				}
				if (build.getTestResult() != null) {
					sb.append(Messages.NewTaskFromBuildAction_testResults);
					sb.append("\n"); //$NON-NLS-1$
					sb.append(NLS.bind(Messages.NewTaskFromBuildAction_duration,
							DateUtil.getFormattedDurationShort(build.getTestResult().getDuration())));
					sb.append("\n"); //$NON-NLS-1$
					sb.append(Messages.NewTaskFromBuildAction_failedTests);
					sb.append("\n"); //$NON-NLS-1$
					appendFailed(sb, build.getTestResult().getSuites());
					sb.append("\n"); //$NON-NLS-1$
				}
				return sb.toString();
			}
		};

		return TasksUiUtil.openNewTaskEditor(WorkbenchUtil.getShell(), mapping, null);
	}

	public static void appendFailed(StringBuilder sb, List<ITestSuite> suites) {
		for (ITestSuite suite : suites) {
			for (ITestCase testCase : suite.getCases()) {
				if (testCase.getStatus() == TestCaseResult.FAILED
						|| testCase.getStatus() == TestCaseResult.REGRESSION) {
					append(sb, testCase);
				}
			}
		}
	}

	public static void append(StringBuilder sb, ITestSuite suite) {
		for (ITestCase testCase : suite.getCases()) {
			append(sb, testCase);
		}
	}

	public static void append(StringBuilder sb, ITestCase testCase) {
		if (testCase.getStackTrace() != null) {
			sb.append(testCase.getStackTrace());
		} else {
			sb.append(" "); //$NON-NLS-1$
			sb.append(testCase.getClassName());
			if (testCase.getLabel() != null) {
				sb.append("."); //$NON-NLS-1$
				sb.append(testCase.getLabel());

				// emulate stack trace format to hyperlink tests in task editor
				sb.append("("); //$NON-NLS-1$
				String className = testCase.getClassName();
				int i = className.lastIndexOf("."); //$NON-NLS-1$
				if (i != -1) {
					className = className.substring(i + 1);
				}
				i = className.lastIndexOf("$"); //$NON-NLS-1$
				if (i != -1) {
					className = className.substring(0, i);
				}
				sb.append(className);
				sb.append(".java:0)"); //$NON-NLS-1$
			}
		}
		sb.append("\n"); //$NON-NLS-1$
	}

}
