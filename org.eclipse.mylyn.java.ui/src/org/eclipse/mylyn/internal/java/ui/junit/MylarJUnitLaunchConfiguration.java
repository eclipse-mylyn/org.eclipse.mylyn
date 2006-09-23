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

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.internal.junit.Messages;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfiguration;
import org.eclipse.jdt.internal.junit.launcher.TestSearchResult;
import org.eclipse.jdt.internal.junit.ui.JUnitMessages;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * @author Mik Kersten
 */
public class MylarJUnitLaunchConfiguration extends JUnitLaunchConfiguration {
	
	protected TestSearchResult customFindTestTypes(ILaunchConfiguration configuration, IProgressMonitor pm) throws CoreException {
		TestSearchResult testSearchResult = MylarContextTestUtil.findTestTypes(configuration, pm);
		if (testSearchResult.getTypes().length == 0) {
			abort(JUnitMessages.JUnitBaseLaunchConfiguration_error_notests, null,
					IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_MAIN_TYPE);
		}
		return testSearchResult;
	}

	/* --------- HACK: below copied from JUnitBaseLaunchConfiguration ----------- */
	
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor pm) throws CoreException {		
		if (mode.equals(RUN_QUIETLY_MODE)) {
			launch.setAttribute(NO_DISPLAY_ATTR, "true"); //$NON-NLS-1$
			mode = ILaunchManager.RUN_MODE;
		}
			
		TestSearchResult testTypes = customFindTestTypes(configuration, pm);
		IVMInstall install= getVMInstall(configuration);
		IVMRunner runner = install.getVMRunner(mode);
		if (runner == null) {
			abort(Messages.format(JUnitMessages.JUnitBaseLaunchConfiguration_error_novmrunner, new String[]{install.getId()}), null, IJavaLaunchConfigurationConstants.ERR_VM_RUNNER_DOES_NOT_EXIST); 
		}
		
		int port= SocketUtil.findFreePort();
		VMRunnerConfiguration runConfig= launchTypes(configuration, mode, testTypes, port);
		setDefaultSourceLocator(launch, configuration);
		
		launch.setAttribute(PORT_ATTR, Integer.toString(port));
		launch.setAttribute(TESTTYPE_ATTR, testTypes.getTypes()[0].getHandleIdentifier());
		runner.run(runConfig, launch, pm);		
	}

	private final VMRunnerConfiguration launchTypes(ILaunchConfiguration configuration, String mode, TestSearchResult tests, int port) throws CoreException {
		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null) 
			workingDirName = workingDir.getAbsolutePath();
		
		// Program & VM args
		String vmArgs= getVMArguments(configuration);
		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, ""); //$NON-NLS-1$
		String[] envp= getEnvironment(configuration);
	
		VMRunnerConfiguration runConfig= createVMRunner(configuration, tests, port, mode);
		runConfig.setVMArguments(execArgs.getVMArgumentsArray());
		runConfig.setWorkingDirectory(workingDirName);
		runConfig.setEnvironment(envp);
	
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);
	
		String[] bootpath = getBootpath(configuration);
		runConfig.setBootClassPath(bootpath);
		
		return runConfig;
	}
}
