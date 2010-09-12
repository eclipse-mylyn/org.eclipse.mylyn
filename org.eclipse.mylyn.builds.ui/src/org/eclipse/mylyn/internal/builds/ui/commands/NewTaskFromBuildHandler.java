/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.TestCaseResult;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public class NewTaskFromBuildHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IBuildPlan) {
				IBuildPlan plan = (IBuildPlan) element;
				if (plan.getLastBuild() != null) {
					return createTask(plan.getLastBuild());
				}
			}
		}
		return null;
	}

	private boolean createTask(final IBuild build) {
		TaskMapping mapping = new TaskMapping() {

			@Override
			public String getSummary() {
				BuildStatus status = build.getStatus();
				if (status != null) {
					return NLS.bind("Build {0}: {1}", build.getLabel(), status.toString());
				} else {
					return NLS.bind("Build {0}", build.getLabel());
				}
			}

			@Override
			public String getDescription() {
				StringBuffer sb = new StringBuffer();
				sb.append(NLS.bind("Build Results at {0} .\n", build.getUrl()));
				sb.append("\n");
				if (build.getChangeSet() != null) {
					sb.append("Code Changes\n");
					sb.append("============\n");
					sb.append("\n");
				}
				if (build.getTestResult() != null) {
					sb.append("Tests Results\n");
					sb.append("=============\n");
					sb.append(NLS.bind("Duration: {0}", DateUtil.getFormattedDurationShort(build.getTestResult()
							.getDuration())));
					sb.append("\n");
					sb.append("Failed Tests:\n");
					for (ITestSuite suite : build.getTestResult().getSuites()) {
						for (ITestCase testCase : suite.getCases()) {
							if (testCase.getStatus() == TestCaseResult.FAILED
									|| testCase.getStatus() == TestCaseResult.REGRESSION) {
								sb.append(testCase.getClassName());
								sb.append("\n");
							}
						}
					}
				}
				return sb.toString();
			}

		};

		return TasksUiUtil.openNewTaskEditor(WorkbenchUtil.getShell(), mapping, null);
	}
}
