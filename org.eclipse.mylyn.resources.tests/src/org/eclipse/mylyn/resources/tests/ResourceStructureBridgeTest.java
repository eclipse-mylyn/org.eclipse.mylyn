/*******************************************************************************
 * Copyright (c) 2011 Manuel Doninger.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Manuel Doninger - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.sdk.util.TestProject;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.sdk.util.AbstractResourceContextTest;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

public class ResourceStructureBridgeTest extends AbstractResourceContextTest {

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

	public void testGetProjectsInActiveContext() throws CoreException {
		assertEquals(0, structureBridge.getProjectsInActiveContext().size());
		IFile testfile1 = project.getProject().getFile("testfile1");
		testfile1.create(null, true, null);
		IFile testfile2 = project.getProject().getFile("testfile2");
		testfile2.create(null, true, null);
		assertEquals(1, structureBridge.getProjectsInActiveContext().size());
	}

	public void testGetProjectsInActiveContextWithInvalidProject() throws CoreException, InvocationTargetException,
			InterruptedException {
		assertEquals(0, structureBridge.getProjectsInActiveContext().size());
		IFile testfile1 = project.getProject().getFile("testfile1");
		testfile1.create(null, true, null);
		IFile testfile2 = project.getProject().getFile("testfile2");
		testfile2.create(null, true, null);
		assertEquals(1, structureBridge.getProjectsInActiveContext().size());
		TestProject project2 = new TestProject(this.getClass().getName() + "Invalid");
		IFile testfile3 = project2.getProject().getFile("testfile1");
		testfile3.create(null, true, null);
		ContextCore.getContextManager().setContextCapturePaused(true);
		testfile3.delete(true, null);
		project2.getProject().delete(true, null);
		ContextCore.getContextManager().setContextCapturePaused(false);
		assertEquals(1, structureBridge.getProjectsInActiveContext().size());
	}
}
