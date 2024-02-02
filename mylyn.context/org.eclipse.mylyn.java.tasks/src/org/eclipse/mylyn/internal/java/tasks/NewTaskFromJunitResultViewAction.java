/*******************************************************************************
 * Copyright (c) 2004, 2011 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.tasks;

import org.eclipse.jdt.internal.junit.model.TestCaseElement;
import org.eclipse.jdt.internal.junit.model.TestElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Creates a new task from the selected JUnit Test.
 * 
 * @author Frank Becker
 */
public class NewTaskFromJunitResultViewAction implements IViewActionDelegate, ISelectionChangedListener {

	public static final String ID = "org.eclipse.mylyn.bugzilla.actions.newTaskFromJunitResultView"; //$NON-NLS-1$

	private String traceString;

	private TestCaseElement testCaseElement;

	@Override
	public void run(IAction action) {
		run();
	}

	public void run() {
		if (traceString == null || testCaseElement == null) {
			return;
		}

		// TODO NLS externalize strings
		final StringBuilder sb = new StringBuilder();
		sb.append("\n-- Error Log from JUnit --\nClass: "); //$NON-NLS-1$
		sb.append(testCaseElement.getTestClassName());
		sb.append("\nMethod: "); //$NON-NLS-1$
		sb.append(testCaseElement.getTestMethodName());
		sb.append("\nActual: "); //$NON-NLS-1$
		sb.append(testCaseElement.getActual());
		sb.append("\nExpected: "); //$NON-NLS-1$
		sb.append(testCaseElement.getExpected());
		sb.append("\nStack Trace:\n"); //$NON-NLS-1$
		sb.append(traceString);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		TaskMapping taskMapping = new TaskMapping() {
			@Override
			public String getDescription() {
				return sb.toString();
			}
		};
		TasksUiUtil.openNewTaskEditor(shell, taskMapping, null);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// ignore
	}

	@Override
	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		traceString = null;
		testCaseElement = null;
		if (selection instanceof TreeSelection t) {
			TestElement testElement = (TestElement) t.getFirstElement();
			if (testElement instanceof TestCaseElement) {
				testCaseElement = (TestCaseElement) testElement;
				traceString = testCaseElement.getTrace();
			}
		}
		action.setEnabled(traceString != null);
	}
}
