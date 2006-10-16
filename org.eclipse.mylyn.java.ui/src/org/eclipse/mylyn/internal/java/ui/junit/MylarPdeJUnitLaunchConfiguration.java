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

import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.junit.launcher.TestSearchResult;
import org.eclipse.jdt.internal.junit.ui.JUnitMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.pde.core.plugin.IFragmentModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.ui.IPDEUIConstants;
import org.eclipse.pde.internal.ui.PDEUIMessages;

/**
 * @author Mik Kersten
 */
public class MylarPdeJUnitLaunchConfiguration extends JUnitLaunchConfigurationDelegateCOPY {

	protected TestSearchResult customFindTestTypes(ILaunchConfiguration configuration, IProgressMonitor pm)
			throws CoreException {
		TestSearchResult testSearchResult = MylarContextTestUtil.findTestTypes(configuration, pm);
		
		if (testSearchResult.getTypes().length == 0) {
			abort(JUnitMessages.JUnitBaseLaunchConfiguration_error_notests, null,
					IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_MAIN_TYPE);
		}
		return testSearchResult;
	}

	protected String getTestPluginId(ILaunchConfiguration configuration) throws CoreException {
		Set<IType> contextTestCases = MylarContextTestUtil.getTestCasesInContext();
		IJavaProject javaProject = null;
		for (IType type : contextTestCases) {
			IProjectNature nature = type.getJavaProject().getProject().getNature("org.eclipse.pde.PluginNature");
			if (nature != null) {
				javaProject = type.getJavaProject(); // HACK: might want
				// another project
			}
		}
		
		IPluginModelBase model = PDECore.getDefault().getModelManager().findModel(javaProject.getProject());
		if (model == null)
			throw new CoreException(new Status(IStatus.ERROR, IPDEUIConstants.PLUGIN_ID, IStatus.ERROR,
					PDEUIMessages.JUnitLaunchConfiguration_error_notaplugin, null));
		if (model instanceof IFragmentModel)
			return ((IFragmentModel) model).getFragment().getPluginId();

		return model.getPluginBase().getId();
	}
	
//	protected String getTestPluginId(ILaunchConfiguration configuration) throws CoreException {
//		Set<IType> contextTestCases = MylarContextTestUtil.getTestCasesInContext();
//		IJavaProject javaProject = null;
//		for (IType type : contextTestCases) {
//			IProjectNature nature = type.getJavaProject().getProject().getNature("org.eclipse.pde.PluginNature");
//			if (nature != null) {
//				javaProject = type.getJavaProject(); // HACK: might want
//				// another project
//			}
//		}
//		// IJavaProject javaProject = getJavaProject(configuration);
//		IPluginModelBase model = null;
//		if (javaProject != null) {
//			model = PDECore.getDefault().getModelManager().findModel(javaProject.getProject());
//		}
//		if (javaProject == null || model == null)
//			throw new CoreException(new Status(IStatus.ERROR, PDEPlugin.PLUGIN_ID, IStatus.ERROR,
//					"Could not find JUnit Plug-in Test in Task Context", null));
//		if (model instanceof IFragmentModel)
//			return ((IFragmentModel) model).getFragment().getPluginId();
//		
//		return model.getPluginBase().getId();
//	}

	/*
	 * ---------------------------------------------------------------- 
	 * HACK: below copied from JUnitLaunchConfigurationDelegate 
	 * ----------------------------------------------------------------
	 */

//	protected VMRunnerConfiguration createVMRunner(ILaunchConfiguration configuration, TestSearchResult testTypes,
//			int port, String runMode) throws CoreException {
//
//		System.err.println(">>>> " + Arrays.asList(getProgramArgumentsArray(configuration, testTypes, port, runMode)));
//		
//		VMRunnerConfiguration runnerConfig = new VMRunnerConfiguration(
//				"org.eclipse.core.launcher.Main", getClasspath(configuration)); //$NON-NLS-1$
//		runnerConfig.setVMArguments(new ExecutionArguments(getVMArguments(configuration), "").getVMArgumentsArray()); //$NON-NLS-1$
//		runnerConfig.setProgramArguments(getProgramArgumentsArray(configuration, testTypes, port, runMode));
//		runnerConfig.setEnvironment(getEnvironment(configuration));
//		runnerConfig.setWorkingDirectory(getWorkingDirectory(configuration).getAbsolutePath());
//		runnerConfig.setVMSpecificAttributesMap(getVMSpecificAttributesMap(configuration));
//		return runnerConfig;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.eclipse.jdt.internal.junit.launcher.JUnitBaseLaunchConfiguration#launch(org.eclipse.debug.core.ILaunchConfiguration,
//	 *      java.lang.String, org.eclipse.debug.core.ILaunch,
//	 *      org.eclipse.core.runtime.IProgressMonitor)
//	 */
//	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
//			throws CoreException {
//		try {
//			fConfigDir = null;
//			monitor.beginTask("", 4); //$NON-NLS-1$
//			TestSearchResult testSearchResult = customFindTestTypes(configuration, new SubProgressMonitor(monitor, 1));
//			IType[] testTypes = testSearchResult.getTypes();
//
//			// Get the list of plug-ins to run
//			fPluginMapFromSuper = LaunchPluginValidator.getPluginsToRun(configuration);
//
//			// implicitly add the plug-ins required for JUnit testing if
//			// necessary
//			for (int i = 0; i < REQUIRED_PLUGINS.length; i++) {
//				String id = REQUIRED_PLUGINS[i];
//				if (!fPluginMapFromSuper.containsKey(id)) {
//					fPluginMapFromSuper.put(id, findPlugin(id));
//				}
//			}
//
//			try {
//				preLaunchCheck(configuration, launch, new SubProgressMonitor(monitor, 2));
//			} catch (CoreException e) {
//				if (e.getStatus().getSeverity() == IStatus.CANCEL) {
//					monitor.setCanceled(true);
//					return;
//				}
//				throw e;
//			}
//
//			int port = SocketUtil.findFreePort();
//			launch.setAttribute(PORT_ATTR, Integer.toString(port));
//			launch.setAttribute(TESTTYPE_ATTR, testTypes[0].getHandleIdentifier());
//			VMRunnerConfiguration runnerConfig = createVMRunner(configuration, testSearchResult, port, mode);
//			monitor.worked(1);
//
//			setDefaultSourceLocator(launch, configuration);
//			manageLaunch(launch);
//			IVMRunner runner = getVMRunner(configuration, mode);
//			if (runner != null)
//				runner.run(runnerConfig, launch, monitor);
//			else
//				monitor.setCanceled(true);
//			monitor.done();
//		} catch (CoreException e) {
//			monitor.setCanceled(true);
//			throw e;
//		}
//	}
//
//	private IPluginModelBase findPlugin(String id) throws CoreException {
//		PluginModelManager manager = PDECore.getDefault().getModelManager();
//		IPluginModelBase model = manager.findModel(id);
//		if (model == null)
//			model = PDECore.getDefault().findPluginInHost(id);
//		if (model == null)
//			abort(NLS.bind(PDEUIMessages.JUnitLaunchConfiguration_error_missingPlugin, id), null, IStatus.OK);
//		return model;
//	}
//
//	@SuppressWarnings("unchecked")
//	private String[] getProgramArgumentsArray(ILaunchConfiguration configuration, TestSearchResult testSearchResult,
//			int port, String runMode) throws CoreException {
//		ArrayList programArgs = new ArrayList();
//
//		programArgs.addAll(getBasicArguments(configuration, port, runMode, testSearchResult));
//
//		// Specify the JUnit Plug-in test application to launch
//		programArgs.add("-application"); //$NON-NLS-1$
//		String application = null;
//		try {
//			application = configuration.getAttribute(IPDELauncherConstants.APPLICATION, (String) null);
//		} catch (CoreException e) {
//		}
//		programArgs.add(application != null ? application : UI_APPLICATION);
//
//		// If a product is specified, then add it to the program args
//		if (configuration.getAttribute(IPDELauncherConstants.USE_PRODUCT, false)) {
//			programArgs.add("-product"); //$NON-NLS-1$
//			programArgs.add(configuration.getAttribute(IPDELauncherConstants.PRODUCT, "")); //$NON-NLS-1$
//		} else {
//			// Specify the application to test
//			String testApplication = configuration.getAttribute(IPDELauncherConstants.APP_TO_TEST, (String) null);
//			if (testApplication != null && testApplication.length() > 0) {
//				programArgs.add("-testApplication"); //$NON-NLS-1$
//				programArgs.add(testApplication);
//			}
//		}
//
//		// Specify the location of the runtime workbench
//		String targetWorkspace = LaunchArgumentsHelper.getWorkspaceLocation(configuration);
//		if (targetWorkspace.length() > 0) {
//			programArgs.add("-data"); //$NON-NLS-1$
//			programArgs.add(targetWorkspace);
//		}
//
//		// Create the platform configuration for the runtime workbench
//		String productID = LaunchConfigurationHelper.getProductID(configuration);
//		LaunchConfigurationHelper.createConfigIniFile(configuration, productID, fPluginMapFromSuper,
//				getConfigurationDirectory(configuration));
//		TargetPlatform.createPlatformConfigurationArea(fPluginMapFromSuper, getConfigurationDirectory(configuration),
//				LaunchConfigurationHelper.getContributingPlugin(productID));
//
//		programArgs.add("-configuration"); //$NON-NLS-1$
//		programArgs
//				.add("file:" + new Path(getConfigurationDirectory(configuration).getPath()).addTrailingSeparator().toString()); //$NON-NLS-1$
//
//		// Specify the output folder names
//		programArgs.add("-dev"); //$NON-NLS-1$
//		programArgs.add(ClasspathHelper.getDevEntriesProperties(getConfigurationDirectory(configuration).toString()
//				+ "/dev.properties", fPluginMapFromSuper)); //$NON-NLS-1$
//
//		// necessary for PDE to know how to load plugins when target platform =
//		// host platform
//		// see PluginPathFinder.getPluginPaths()
//		if (fPluginMapFromSuper.containsKey(PDECore.getPluginId()))
//			programArgs.add("-pdelaunch"); //$NON-NLS-1$	
//
//		// Create the .options file if tracing is turned on
//		if (configuration.getAttribute(IPDELauncherConstants.TRACING, false)
//				&& !IPDELauncherConstants.TRACING_NONE.equals(configuration.getAttribute(
//						IPDELauncherConstants.TRACING_CHECKED, (String) null))) {
//			programArgs.add("-debug"); //$NON-NLS-1$
//			String path = getConfigurationDirectory(configuration).getPath() + IPath.SEPARATOR + ".options"; //$NON-NLS-1$
//			programArgs.add(LaunchArgumentsHelper.getTracingFileArgument(configuration, path));
//		}
//
//		// add the program args specified by the user
//		String[] userArgs = LaunchArgumentsHelper.getUserProgramArgumentArray(configuration);
//		for (int i = 0; i < userArgs.length; i++) {
//			// be forgiving if people have tracing turned on and forgot
//			// to remove the -debug from the program args field.
//			if (userArgs[i].equals("-debug") && programArgs.contains("-debug")) //$NON-NLS-1$ //$NON-NLS-2$
//				continue;
//			programArgs.add(userArgs[i]);
//		}
//
//		if (!programArgs.contains("-os")) { //$NON-NLS-1$
//			programArgs.add("-os"); //$NON-NLS-1$
//			programArgs.add(TargetPlatform.getOS());
//		}
//		if (!programArgs.contains("-ws")) { //$NON-NLS-1$
//			programArgs.add("-ws"); //$NON-NLS-1$
//			programArgs.add(TargetPlatform.getWS());
//		}
//		if (!programArgs.contains("-arch")) { //$NON-NLS-1$
//			programArgs.add("-arch"); //$NON-NLS-1$
//			programArgs.add(TargetPlatform.getOSArch());
//		}
//
//		programArgs.add("-testpluginname"); //$NON-NLS-1$
//		programArgs.add(getTestPluginId(configuration));
//
//		programArgs.add("-loaderpluginname"); //$NON-NLS-1$
//		programArgs.add(testSearchResult.getTestKind().getLoaderPluginId());
//
//		String testFailureNames = configuration.getAttribute(JUnitBaseLaunchConfiguration.FAILURES_FILENAME_ATTR, ""); //$NON-NLS-1$
//		if (testFailureNames.length() > 0) {
//			programArgs.add("-testfailures"); //$NON-NLS-1$
//			programArgs.add(testFailureNames);
//		}
//
//		// a testname was specified just run the single test
//		IType[] testTypes = testSearchResult.getTypes();
//		String testName = configuration.getAttribute(JUnitBaseLaunchConfiguration.TESTNAME_ATTR, ""); //$NON-NLS-1$
//		if (testName.length() > 0) {
//			programArgs.add("-test"); //$NON-NLS-1$
//			programArgs.add(testTypes[0].getFullyQualifiedName() + ":" + testName); //$NON-NLS-1$
//		} else {
//			programArgs.add("-classnames"); //$NON-NLS-1$
//			for (int i = 0; i < testTypes.length; i++)
//				programArgs.add(testTypes[i].getFullyQualifiedName());
//		}
//		return (String[]) programArgs.toArray(new String[programArgs.size()]);
//	}

}
