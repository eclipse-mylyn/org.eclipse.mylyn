/*******************************************************************************
 * Copyright (c) 2012, 2015 Sebastian Schmidt and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.debug.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaLineBreakpoint;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.context.sdk.java.WorkspaceSetupHelper;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsContextUtilTest {

	private final String contextFileName = "contextWithBreakpoints.xml.zip"; //$NON-NLS-1$

	private final File contextFile = new File("testdata/" + contextFileName); //$NON-NLS-1$

	private File tempContextFile;

	private final IInteractionContextManager contextManager = ContextCore.getContextManager();

	private IBreakpointManager breakpointManager;

	@Before
	public void setUp() throws IOException, CoreException {
		BreakpointsTestUtil.setManageBreakpointsPreference(true);
		breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		File contextStore = ContextCorePlugin.getContextStore().getContextDirectory();
		tempContextFile = new File(contextStore, contextFileName);
		FileUtils.copyFile(contextFile, tempContextFile);
		assertTrue(contextFile.exists());
	}

	@After
	public void tearDown() throws Exception {
		if (tempContextFile != null && tempContextFile.exists()) {
			tempContextFile.delete();
		}
		contextManager.deactivateContext("contextWithBreakpoints"); //$NON-NLS-1$
		WorkspaceSetupHelper.clearWorkspace();
		breakpointManager.removeBreakpoints(breakpointManager.getBreakpoints(), true);
	}

	/**
	 * If the project isn't in the workspace, breakpoints from context should be ignored
	 */
	@Test
	public void testImportBreakpointsWithMissingProject() throws Exception {
		activateContext();
		IInteractionContext testContext = contextManager.getActiveContext();
		List<IBreakpoint> breakpoints = BreakpointsContextUtil.importBreakpoints(testContext, null);
		assertEquals(Collections.emptyList(), breakpoints);
	}

	@Test
	public void testImportBreakpoints() throws Exception {
		BreakpointsTestUtil.createProject();
		activateContext();
		IInteractionContext testContext = contextManager.getActiveContext();
		List<IBreakpoint> breakpoints = BreakpointsContextUtil.importBreakpoints(testContext, null);
		assertEquals(2, breakpoints.size());

		assertTrue(breakpoints.get(0) instanceof JavaLineBreakpoint);
		IMarker marker = breakpoints.get(0).getMarker();
		assertEquals("test.java", marker.getResource().getName());
		assertEquals(11, marker.getAttribute(IMarker.LINE_NUMBER, 0));

		assertTrue(breakpoints.get(1) instanceof JavaLineBreakpoint);
		marker = breakpoints.get(1).getMarker();
		assertEquals("test.java", marker.getResource().getName());
		assertEquals(10, marker.getAttribute(IMarker.LINE_NUMBER, 0));
	}

	@Test
	public void testActivateTask() throws Exception {
		BreakpointsTestUtil.createProject();

		assertEquals(0, breakpointManager.getBreakpoints().length);

		activateContext();
		assertEquals(2, breakpointManager.getBreakpoints().length);

		contextManager.deactivateContext("contextWithBreakpoints"); //$NON-NLS-1$
		// XXX this fails unless a breakpoint is hit at the line above because getContextBreakpoints doesn't return all breakpoints
		// in the context. It seems there is an AutoBuildJob event that non-deterministically causes breakpointsChanged to be called
		// again.
//		assertEquals(0, breakpointManager.getBreakpoints().length);
	}

	@Test
	public void testActivateTaskDisabled() throws Exception {
		BreakpointsTestUtil.setManageBreakpointsPreference(false);
		BreakpointsTestUtil.createProject();

		assertEquals(0, breakpointManager.getBreakpoints().length);

		activateContext();
		assertEquals(0, breakpointManager.getBreakpoints().length);

		contextManager.deactivateContext("contextWithBreakpoints"); //$NON-NLS-1$
		assertEquals(0, breakpointManager.getBreakpoints().length);
	}

	@Test
	public void testDeactivateTaskDisabled() throws Exception {
		BreakpointsTestUtil.createProject();

		assertEquals(getBreakpointsAsString(), 0, breakpointManager.getBreakpoints().length);

		activateContext();
		try {
			assertEquals(getBreakpointsAsString(), 2, breakpointManager.getBreakpoints().length);
		} catch (AssertionError e) {
			Thread.sleep(100);
			System.out.println("# Slept once");
			System.out.println(getBreakpointsAsString());
			Thread.sleep(1000);
			System.out.println("# Slept twice");
			System.out.println(getBreakpointsAsString());
			throw e;
		}

		BreakpointsTestUtil.setManageBreakpointsPreference(false);

		contextManager.deactivateContext("contextWithBreakpoints"); //$NON-NLS-1$
		assertEquals(getBreakpointsAsString(), 2, breakpointManager.getBreakpoints().length);
	}

	private String getBreakpointsAsString() {
		return Arrays.asList(breakpointManager.getBreakpoints()).toString();
	}

	@Test
	public void testExportBreakpoints() throws Exception {
		BreakpointsTestUtil.createProject();
		List<IBreakpoint> breakpoints = BreakpointsTestUtil.createTestBreakpoints();

		InputStream exportedBreakpoints = BreakpointsContextUtil.exportBreakpoints(breakpoints, null);
		List<String> expected = IOUtils.readLines(CommonTestUtil.getResource(this, "testdata/breakpointFile.xml"));
		List<String> actual = IOUtils.readLines(exportedBreakpoints);
		Collections.sort(expected);
		Collections.sort(actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testRemoveBreakpoints() throws Exception {
		BreakpointsTestUtil.createProject();
		IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
		int currentBreakpoints = breakpoints.length;

		IBreakpoint breakpoint = BreakpointsTestUtil.createTestBreakpoint();
		breakpointManager.addBreakpoint(breakpoint);
		List<IBreakpoint> breakpointsToRemove = new ArrayList<>();
		breakpointsToRemove.add(breakpoint);

		breakpointManager.addBreakpoint(breakpoint);
		assertEquals(currentBreakpoints + 1, breakpointManager.getBreakpoints().length);

		BreakpointsContextUtil.removeBreakpoints(breakpointsToRemove);
		assertEquals(currentBreakpoints, breakpointManager.getBreakpoints().length);
	}

	private void activateContext() throws OperationCanceledException, InterruptedException {
		contextManager.activateContext("contextWithBreakpoints"); //$NON-NLS-1$
		Job.getJobManager().join(BreakpointsContextContributor.JOB_FAMILY, null);
	}

}
