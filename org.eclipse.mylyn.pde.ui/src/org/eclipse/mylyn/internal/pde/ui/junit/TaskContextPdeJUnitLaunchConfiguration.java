/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.pde.ui.junit;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.java.ui.junit.InteractionContextTestUtil;
import org.eclipse.pde.ui.launcher.JUnitLaunchConfigurationDelegate;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class TaskContextPdeJUnitLaunchConfiguration extends JUnitLaunchConfigurationDelegate {

	@Override
	protected IMember[] evaluateTests(ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		Set<IType> contextTestCases = InteractionContextTestUtil.getTestCasesInContext();
		InteractionContextTestUtil.setupTestConfiguration(contextTestCases, configuration, monitor);

		if (contextTestCases.isEmpty()) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Context Test Suite",
							"No test types found in the active task context.");
				}
			});
		}
		return contextTestCases.toArray(new IMember[contextTestCases.size()]);
	}
}
