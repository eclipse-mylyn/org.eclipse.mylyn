/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaLineBreakpoint;
import org.eclipse.mylyn.commons.core.XmlMemento;
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

	@Before
	public void setUp() throws IOException, CoreException {
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
	}

	/**
	 * If the project isn't in the workspace, breakpoints from context should be ignored
	 */
	@Test
	public void testImportBreakpointsWithMissingProject() {
		contextManager.activateContext("contextWithBreakpoints"); //$NON-NLS-1$
		IInteractionContext testContext = contextManager.getActiveContext();
		List<IBreakpoint> breakpoints = BreakpointsContextUtil.importBreakpoints(testContext, null);
		assertEquals(Collections.emptyList(), breakpoints);
	}

	@Test
	public void testImportBreakpoints() throws Exception {
		BreakpointsTestUtil.createProject();
		contextManager.activateContext("contextWithBreakpoints"); //$NON-NLS-1$
		IInteractionContext testContext = contextManager.getActiveContext();
		List<IBreakpoint> breakpoints = BreakpointsContextUtil.importBreakpoints(testContext, null);
		assertTrue(breakpoints.size() == 2);

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
	public void testExportBreakpoints() throws Exception {
		BreakpointsTestUtil.createProject();
		InputStream expectedResult = new FileInputStream(new File("testdata/breakpointFile.xml")); //$NON-NLS-1$
		IBreakpoint breakpoint = BreakpointsTestUtil.createTestBreakpoint();
		List<IBreakpoint> breakpoints = new ArrayList<IBreakpoint>();
		breakpoints.add(breakpoint);
		InputStream exportedBreakpoints = BreakpointsContextUtil.exportBreakpoints(breakpoints, null);
		assertEquals(toCanonicalXml(expectedResult), toCanonicalXml(exportedBreakpoints));
	}

	private String toCanonicalXml(InputStream in) throws Exception {
		XmlMemento memento = XmlMemento.createReadRoot(new StringReader(IOUtils.toString(in)));
		StringWriter out = new StringWriter();
		memento.save(out);
		return out.toString();
	}

	@Test
	public void testRemoveBreakpoints() throws Exception {
		BreakpointsTestUtil.createProject();
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
		int currentBreakpoints = breakpoints.length;

		IBreakpoint breakpoint = BreakpointsTestUtil.createTestBreakpoint();
		breakpointManager.addBreakpoint(breakpoint);
		List<IBreakpoint> breakpointsToRemove = new ArrayList<IBreakpoint>();
		breakpointsToRemove.add(breakpoint);

		breakpointManager.addBreakpoint(breakpoint);
		assertEquals(currentBreakpoints + 1, breakpointManager.getBreakpoints().length);

		BreakpointsContextUtil.removeBreakpoints(breakpointsToRemove);
		assertEquals(currentBreakpoints, breakpointManager.getBreakpoints().length);
	}
}
