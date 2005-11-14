/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.java.internal.junit;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfiguration;
import org.eclipse.jdt.internal.junit.ui.JUnitMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * @author Mik Kersten
 */
public class MylarJUnitLaunchConfiguration extends JUnitLaunchConfiguration  {
	
	protected IType[] getTestTypes(ILaunchConfiguration config, IProgressMonitor pm) throws CoreException {
		Set<IType> contextTestCases = JUnitTestUtil.getTestCasesInContext();
		if (contextTestCases.isEmpty()) {
			abort(JUnitMessages.JUnitBaseLaunchConfiguration_error_notests, null, IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_MAIN_TYPE); 
		}
		return contextTestCases.toArray(new IType[contextTestCases.size()]);
	}
}
