/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.java.tests.xml;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.tests.support.ResourceHelper;
import org.eclipse.mylyn.context.tests.support.search.ISearchPluginTest;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextRelation;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.pde.ui.XmlJavaRelationProvider;
import org.eclipse.mylyn.java.tests.search.ActiveSearchNotifier;
import org.eclipse.mylyn.java.tests.search.SearchPluginTestHelper;
import org.eclipse.mylyn.java.tests.search.WorkspaceSetupHelper;

public class ResultUpdaterTest extends TestCase implements ISearchPluginTest {
	private IType type1;

	private IFile plugin1;

	private IJavaProject jp1;

	private static final String SOURCE_ID = "XMLSearchResultUpdaterTest";

	private SearchPluginTestHelper helper;

	@Override
	protected void setUp() throws Exception {
		// TODO: clear the relationship providers?
		WorkspaceSetupHelper.setupWorkspace();
		jp1 = WorkspaceSetupHelper.getProject1();
		type1 = WorkspaceSetupHelper.getType(jp1, "org.eclipse.mylar.tests.project1.views.SampleView");
		plugin1 = WorkspaceSetupHelper.getFile(jp1, "plugin.xml");

		InteractionContext t = WorkspaceSetupHelper.getContext();
		ContextCore.getContextManager().activateContext(t.getHandleIdentifier());// ,
		// t.getId());
		helper = new SearchPluginTestHelper(this);
	}

	@Override
	protected void tearDown() throws Exception {
		WorkspaceSetupHelper.clearWorkspace();
		WorkspaceSetupHelper.clearDoiModel();
	}

	public void testRemoveFile() throws Exception {

		int dos = 4;

		CompositeInteractionContext t = (CompositeInteractionContext) ContextCore.getContextManager()
				.getActiveContext();
		ActiveSearchNotifier notifier = new ActiveSearchNotifier(t, SOURCE_ID);
		IInteractionElement searchNode = notifier.getElement(type1.getHandleIdentifier(),
				JavaStructureBridge.CONTENT_TYPE);

		//
		// we should get all results since we are searching the entire workspace
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, searchNode, dos, 3);
		//
		//

		Collection<InteractionContextRelation> edges = searchNode.getRelations();
		assertEquals(3, edges.size());

		ResourceHelper.delete(plugin1);

		Collection<InteractionContextRelation> edgesAfterRemove = searchNode.getRelations();
		assertEquals(0, edgesAfterRemove.size());
	}

	public void testRemoveProject() throws Exception {
		int dos = 4;

		CompositeInteractionContext t = (CompositeInteractionContext) ContextCore.getContextManager()
				.getActiveContext();
		ActiveSearchNotifier notifier = new ActiveSearchNotifier(t, SOURCE_ID);
		IInteractionElement searchNode = notifier.getElement(type1.getHandleIdentifier(),
				JavaStructureBridge.CONTENT_TYPE);

		//
		// we should get all results since we are searching the entire workspace
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, searchNode, dos, 3);
		//
		//

		Collection<InteractionContextRelation> edges = searchNode.getRelations();
		assertEquals(3, edges.size());

		ResourceHelper.deleteProject(jp1.getProject().getName());

		Collection<InteractionContextRelation> edgesAfterRemove = searchNode.getRelations();
		assertEquals(0, edgesAfterRemove.size());
		;
	}

	public List<?> search(int dos, IInteractionElement node) throws IOException, CoreException {
		if (node == null) {
			return null;
		}

		// test with each of the sepatations
		XmlJavaRelationProvider prov = new XmlJavaRelationProvider();

		IActiveSearchOperation o = prov.getSearchOperation(node, 0, dos);
		if (o == null) {
			return null;
		}

		XmlResultUpdaterSearchListener l = new XmlResultUpdaterSearchListener(prov, node, dos);
		SearchPluginTestHelper.search(o, l);

		return l.getResults();
	}
}
