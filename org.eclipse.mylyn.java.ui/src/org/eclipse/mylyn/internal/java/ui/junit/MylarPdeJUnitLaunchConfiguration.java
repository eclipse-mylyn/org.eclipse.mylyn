/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.java.ui.junit;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.pde.ui.launcher.JUnitLaunchConfigurationDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class MylarPdeJUnitLaunchConfiguration extends JUnitLaunchConfigurationDelegate {

	@Override
	protected IMember[] evaluateTests(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		Set<IType> contextTestCases = InteractionContextTestUtil.getTestCasesInContext();
		InteractionContextTestUtil.setupTestConfiguration(contextTestCases, configuration, monitor);

		if (contextTestCases.isEmpty()) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					"Context Test Suite", 
					"No test types found in the active task context.");
		} 
		return (IMember[])contextTestCases.toArray(new IMember[contextTestCases.size()]);
	}
}
