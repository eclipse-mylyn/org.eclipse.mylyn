/*******************************************************************************
 * Copyright (c) 2004, 2023 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Yatta Solutions -  WorkingSet tests (bug 334024)
 *     ArSysOp - adapt to SimRel 2023-06
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.java.AbstractJavaContextTest;
import org.eclipse.mylyn.internal.ide.ui.IdeUiUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Carsten Reckord (bug 334024: focused package explorer not working if top level element is working set)
 */
@SuppressWarnings("nls")
public class ResourceStructureMappingTest extends AbstractJavaContextTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// make sure some part is active
		IdeUiUtil.getNavigatorFromActivePage();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.getWorkingSet("TestWorkingSet");
		if (workingSet != null) {
			workingSetManager.removeWorkingSet(workingSet);
		}
	}

	public void testParentResourceMapping() throws CoreException {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("public void m1() { }", null, true, null);

		monitor.selectionChanged(part, new StructuredSelection(m1));
		IInteractionElement m1Node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(m1Node.getInterest().isInteresting());

		IResource containingResource = ResourcesUiBridgePlugin.getDefault().getResourceForElement(m1Node, true);
		assertEquals(m1.getCompilationUnit().getAdapter(IResource.class), containingResource);
	}

	/**
	 * Test that working sets are correctly handled in the presence of the Java bridge
	 */
	public void testWorkingSetFiltering() throws CoreException {
		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.createWorkingSet("TestWorkingSet",
				new IAdaptable[] { project.getProject() });
		workingSet.setId("org.eclipse.jdt.ui.JavaWorkingSetPage");
		workingSetManager.addWorkingSet(workingSet);

		context.reset();
		assertEquals(0, context.getInteractionHistory().size());

		// make sure the correct bridge is used in the presence of the java bridge
		AbstractContextStructureBridge workingSetBridge = ContextCore.getStructureBridge(workingSet);
		assertEquals(ContextCore.CONTENT_TYPE_RESOURCE, workingSetBridge.getContentType());

		// without an interesting project, the working set should be filtered
		assertTrue(workingSetBridge.canFilter(workingSet));

		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		monitor.selectionChanged(part, new StructuredSelection(type1));
		IInteractionElement element = ContextCore.getContextManager().getElement(type1.getHandleIdentifier());
		assertTrue(element.getInterest().isInteresting());

		// with an interesting project, the working set should no longer be filtered
		assertFalse(workingSetBridge.canFilter(workingSet));

	}
}
