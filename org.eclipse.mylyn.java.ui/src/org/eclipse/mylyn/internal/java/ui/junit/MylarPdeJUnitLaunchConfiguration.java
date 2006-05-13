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
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.launcher.JUnitLaunchConfiguration;

/**
 * @author Mik Kersten
 */
public class MylarPdeJUnitLaunchConfiguration extends JUnitLaunchConfiguration {

	protected TestSearchResult findTestTypes(ILaunchConfiguration configuration, IProgressMonitor pm) throws CoreException {
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
		// IJavaProject javaProject = getJavaProject(configuration);
		IPluginModelBase model = null;
		if (javaProject != null) {
			model = PDECore.getDefault().getModelManager().findModel(javaProject.getProject());
		}
		if (javaProject == null || model == null)
			throw new CoreException(new Status(IStatus.ERROR, PDEPlugin.PLUGIN_ID, IStatus.ERROR,
					"Could not find JUnit Plug-in Test in Task Context", null));
		if (model instanceof IFragmentModel)
			return ((IFragmentModel) model).getFragment().getPluginId();

		return model.getPluginBase().getId();
	}
}
