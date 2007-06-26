/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.tests.UiTestUtil;
import org.eclipse.mylyn.context.tests.support.search.TestActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;
import org.eclipse.mylyn.internal.context.ui.views.ActiveSearchView;
import org.eclipse.mylyn.internal.java.ui.search.JavaReferencesProvider;
import org.eclipse.mylyn.java.tests.search.SearchPluginTestHelper;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Perspective;
import org.eclipse.ui.internal.WorkbenchPage;

/**
 * @author Mik Kersten
 */
public class ActiveSearchTest extends AbstractJavaContextTest {

	private ActiveSearchView view;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testViewRecursion() throws JavaModelException, PartInitException {
		view = (ActiveSearchView) JavaPlugin.getActivePage().showView(ActiveSearchView.ID);
		ActiveSearchView.getFromActivePerspective().setSyncExecForTesting(false);

		for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
			assertTrue(provider.isEnabled());
		}
		assertEquals(0, view.getViewer().getTree().getItemCount());

		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m1() {\n m1(); \n}", null, true, null);
		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(part, sm1);
		IInteractionElement node = manager.processInteractionEvent(mockInterestContribution(m1.getHandleIdentifier(),
				scaling.getLandmark()));

		// force an edge on so that it shows up in the view
		// ((MylarContextElement)((CompositeContextElement)node).getNodes().iterator().next()).addEdge(new
		// MylarContextRelation("kind", "edgeKind", node, node, context));

		assertEquals(1, ContextCorePlugin.getContextManager().getActiveLandmarks().size());

		assertEquals(1, search(2, node).size());

		List<TreeItem> collectedItems = new ArrayList<TreeItem>();
		UiTestUtil.collectTreeItemsInView(view.getViewer().getTree().getItems(), collectedItems);

		// just make sure that the view didn't blow up.
		assertEquals(1, collectedItems.size());
		monitor.selectionChanged(part, sm1);
		manager.processInteractionEvent(mockInterestContribution(m1.getHandleIdentifier(), -scaling.getLandmark()));
	}

	public void testSearchNotRunIfViewDeactivated() throws PartInitException, JavaModelException {
		view = (ActiveSearchView) JavaPlugin.getActivePage().showView(ActiveSearchView.ID);
		for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
			assertTrue(provider.getCurrentDegreeOfSeparation() > 0);
		}
		JavaPlugin.getActivePage().showView("org.eclipse.ui.views.ProblemView");

		Perspective perspective = ((WorkbenchPage) JavaPlugin.getActivePage()).getActivePerspective();
		IViewReference reference = JavaPlugin.getActivePage().findViewReference(ActiveSearchView.ID);
		assertNotNull(reference);
//		assertTrue(perspective.canCloseView(view));
		assertTrue(perspective.hideView(reference));

		for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
			assertFalse(provider.isEnabled());
		}

		JavaPlugin.getActivePage().showView(ActiveSearchView.ID);
		for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
			assertTrue(provider.isEnabled());
		}
	}

	public void testSearchAfterDeletion() throws JavaModelException, PartInitException, IOException, CoreException {
		view = (ActiveSearchView) JavaPlugin.getActivePage().showView(ActiveSearchView.ID);
		if (view != null) {
			assertEquals(0, view.getViewer().getTree().getItemCount());

			IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
			IMethod m1 = type1.createMethod("void m1() {\n m2() \n}", null, true, null);
			IMethod m2 = type1.createMethod("void m2() { }", null, true, null);
			StructuredSelection sm2 = new StructuredSelection(m2);
			monitor.selectionChanged(part, sm2);
			IInteractionElement node = manager.processInteractionEvent(mockInterestContribution(
					m2.getHandleIdentifier(), scaling.getLandmark()));
			assertEquals(1, ContextCorePlugin.getContextManager().getActiveLandmarks().size());

			assertEquals(1, search(2, node).size());

			m1.delete(true, null);
			assertFalse(m1.exists());

			assertEquals(0, search(2, node).size());
		}
	}

	public List<?> search(int dos, IInteractionElement node) {
		if (node == null) {
			fail("null element");
		}

		JavaReferencesProvider prov = new JavaReferencesProvider();

		TestActiveSearchListener l = new TestActiveSearchListener(prov);
		IActiveSearchOperation o = prov.getSearchOperation(node, IJavaSearchConstants.REFERENCES, dos);
		if (o == null)
			return null;

		SearchPluginTestHelper.search(o, l);
		return l.getResults();
	}
}
