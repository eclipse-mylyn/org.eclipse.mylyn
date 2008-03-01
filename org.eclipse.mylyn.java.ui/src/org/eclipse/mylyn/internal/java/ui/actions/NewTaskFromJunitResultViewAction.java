/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.actions;

import org.eclipse.jdt.internal.junit.model.TestCaseElement;
import org.eclipse.jdt.internal.junit.model.TestElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.mylyn.tasks.core.TaskSelection;
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

	public static final String ID = "org.eclipse.mylyn.bugzilla.actions.newTaskFromJunitResultView";

	private String traceString;

	private TestCaseElement testCaseElement;

	public void run(IAction action) {
		run();
	}

	public void run() {
		if (traceString == null || testCaseElement == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\n-- Error Log from JUnit --\nClass: ");
		sb.append(testCaseElement.getTestClassName());
		sb.append("\nMethod: ");
		sb.append(testCaseElement.getTestMethodName());
		sb.append("\nActual: ");
		sb.append(testCaseElement.getActual());
		sb.append("\nExpected: ");
		sb.append(testCaseElement.getExpected());
		sb.append("\nStack Trace:\n");
		sb.append(traceString);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		TaskSelection taskSelection = new TaskSelection("", sb.toString());
		TasksUiUtil.openNewTaskEditor(shell, taskSelection, null);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		// ignore		
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void selectionChanged(IAction action, ISelection selection) {
		traceString = null;
		testCaseElement = null;
		if (selection instanceof TreeSelection) {
			TreeSelection t = (TreeSelection) selection;
			TestElement testElement = (TestElement) t.getFirstElement();
			if (testElement instanceof TestCaseElement) {
				testCaseElement = (TestCaseElement) testElement;
				traceString = testCaseElement.getTrace();
			}
		}
		action.setEnabled(traceString != null);
	}
}
