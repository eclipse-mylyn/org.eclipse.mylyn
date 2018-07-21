/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.builds.ui.actions.NewTaskFromBuildAction;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public class NewTaskFromTestHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			List<?> tests = ((IStructuredSelection) selection).toList();
			if (!tests.isEmpty()) {
				return createTask(tests);
			}
		}
		return null;
	}

	public static boolean createTask(final List<?> tests) {
		TaskMapping mapping = new TaskMapping() {
			@Override
			public String getSummary() {
				return "";
			}

			@Override
			public String getDescription() {
				StringBuilder sb = new StringBuilder();
				for (Object object : tests) {
					if (object instanceof ITestSuite) {
						NewTaskFromBuildAction.append(sb, (ITestSuite) object);
					} else if (object instanceof ITestCase) {
						NewTaskFromBuildAction.append(sb, (ITestCase) object);
					}
				}
				return sb.toString();
			}
		};
		return TasksUiUtil.openNewTaskEditor(WorkbenchUtil.getShell(), mapping, null);
	}

}
