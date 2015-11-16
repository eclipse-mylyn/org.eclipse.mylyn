/*******************************************************************************
 * Copyright (c) 2012, 2014 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.sdk.java.WorkspaceSetupHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsStateUtilTest {

	private final IPath pluginStateDir = Platform.getStateLocation(DebugUiPlugin.getDefault().getBundle());

	private final File pluginStateFile = pluginStateDir.append(BreakpointsStateUtil.STATE_FILE).toFile();

	private final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();

	private final BreakpointsStateUtil objectUnderTest = new BreakpointsStateUtil(pluginStateDir);

	private IBreakpoint breakpoint;

	@Before
	public void setUp() throws Exception {
		BreakpointsTestUtil.setManageBreakpointsPreference(true);
		BreakpointsTestUtil.createProject();
		deleteAllBreakpoints();
		breakpoint = BreakpointsTestUtil.createTestBreakpoint();
	}

	@After
	public void tearDown() throws IOException, CoreException {
		deleteAllBreakpoints();
		FileUtils.deleteDirectory(pluginStateDir.toFile());
		WorkspaceSetupHelper.clearWorkspace();
	}

	@Test
	public void testSaveState() throws Exception {
		breakpointManager.addBreakpoint(breakpoint);
		assertEquals(1, breakpointManager.getBreakpoints().length);

		objectUnderTest.saveState();

		assertTrue(pluginStateFile.exists());
		FileInputStream stateFile = new FileInputStream(pluginStateFile);
		InputStream expectedResult = new FileInputStream(new File("testdata/breakpointFile.xml")); //$NON-NLS-1$
		assertEquals(IOUtils.readLines(expectedResult), IOUtils.readLines(stateFile));
	}

	@Test
	public void testSaveStateWithoutBreakpoint() throws CoreException {
		deleteAllBreakpoints();

		objectUnderTest.saveState();

		assertFalse(pluginStateFile.exists()); // nothing to save;
	}

	@Test
	public void testRestoreState() throws CoreException, IOException {
		FileUtils.copyFile(new File("testdata/breakpointFile.xml"), pluginStateFile);

		objectUnderTest.restoreState();

		assertEquals(1, breakpointManager.getBreakpoints().length);
	}

	private void deleteAllBreakpoints() throws CoreException {
		IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
		breakpointManager.removeBreakpoints(breakpoints, true);
		assertEquals(0, breakpointManager.getBreakpoints().length);
	}
}
