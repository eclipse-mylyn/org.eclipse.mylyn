/*******************************************************************************
 * Copyright (c) 2011 Manuel Doninger.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Manuel Doninger - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.sdk.util.TestProject;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.sdk.util.AbstractResourceContextTest;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;
import org.eclipse.mylyn.resources.ui.ResourcesUi;

/**
 * @author Manuel Doninger
 * @author Steffen Pingel
 */
public class ResourcesUiTest extends AbstractResourceContextTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		ResourcesUiBridgePlugin.getDefault().setResourceMonitoringEnabled(true);
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(true);

		ContextTestUtil.triggerContextUiLazyStart();
		// disable ResourceModifiedDateExclusionStrategy
		ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.setValue(ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS, false);
	}

	public void testGetProjects() throws CoreException {
		Set<IProject> projects = ResourcesUi.getProjects(ContextCore.getContextManager().getActiveContext());
		assertEquals(Collections.emptySet(), projects);

		IFile testfile1 = project.getProject().getFile("testfile1");
		testfile1.create(null, true, null);
		IFile testfile2 = project.getProject().getFile("testfile2");
		testfile2.create(null, true, null);

		projects = ResourcesUi.getProjects(ContextCore.getContextManager().getActiveContext());
		assertEquals(Collections.singleton(project.getProject()), projects);
	}

	public void testGetProjectsInWithInvalidProject() throws CoreException, InvocationTargetException,
			InterruptedException {
		Set<IProject> projects = ResourcesUi.getProjects(ContextCore.getContextManager().getActiveContext());
		assertEquals(Collections.emptySet(), projects);

		IFile testfile1 = project.getProject().getFile("testfile1");
		testfile1.create(null, true, null);
		IFile testfile2 = project.getProject().getFile("testfile2");
		testfile2.create(null, true, null);
		projects = ResourcesUi.getProjects(ContextCore.getContextManager().getActiveContext());
		assertEquals(Collections.singleton(project.getProject()), projects);

		TestProject project2 = new TestProject(this.getClass().getName() + "Invalid");
		IFile testfile3 = project2.getProject().getFile("testfile1");
		testfile3.create(null, true, null);
		ContextCore.getContextManager().setContextCapturePaused(true);
		testfile3.delete(true, null);
		project2.getProject().delete(true, null);
		ContextCore.getContextManager().setContextCapturePaused(false);
		projects = ResourcesUi.getProjects(ContextCore.getContextManager().getActiveContext());
		assertEquals(Collections.singleton(project.getProject()), projects);
	}

}
