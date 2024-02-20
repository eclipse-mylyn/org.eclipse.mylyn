/*******************************************************************************
 * Copyright (c) 2010, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.builds.ui.util;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.junit.model.TestRunSession;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.osgi.util.NLS;

class TestResultSession extends TestRunSession {

	TestResultSession(IBuild build) {
		super(NLS.bind(Messages.TestResultSession_testResultsForBuild,
				build.getPlan() == null ? Messages.TestResultSession_unknownPlan : build.getPlan().getLabel(), build.getLabel()), null);
	}

	// Eclipse 3.5 and earlier
	public boolean rerunTest(String testId, String className, String testName, String launchMode) throws CoreException {
		return rerunTest(testId, className, testName, launchMode, false);
	}

	// Eclipse 3.6 and later
	public boolean rerunTest(String testId, final String className, final String testName, String launchMode,
			boolean buildBeforeLaunch) throws CoreException {
		final AtomicReference<IJavaElement> result = new AtomicReference<>();
		WorkbenchUtil.busyCursorWhile(monitor -> {
			IType type = TestResultManager.Runner.findType(className, monitor);
			if (type == null) {
				return;
			}
			if (testName != null) {
				IMethod method = type.getMethod(testName, new String[0]);
				if (method != null && method.exists()) {
					result.set(method);
				} else {
					result.set(type);
				}
			}
		});
		if (result.get() == null) {
			String typeName = className;
			if (testName != null) {
				typeName += "." + testName + "()"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
					NLS.bind(Messages.TestResultSession_launchFailedFor, typeName)));
		}
		JUnitLaunchShortcut shortcut = new JUnitLaunchShortcut();
		shortcut.launch(new StructuredSelection(result.get()), launchMode);
		return true;
	}

}