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
package org.eclipse.mylyn.java.tests.search;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.tests.support.search.ISearchPluginTest;
import org.eclipse.mylyn.context.tests.support.search.TestActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.IMylarSearchOperation;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.search.JavaReferencesProvider;
import org.eclipse.mylyn.internal.pde.ui.PdeStructureBridge;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class JavaReferencesSearchTest extends TestCase implements ISearchPluginTest {

	private IType type1;

	private IType type11;

	private IType type2;

	private IFile plugin1;

	private IJavaProject jp1;

	private IJavaProject jp2;

	private static final String SOURCE_ID = "JavaReferencesSearchTest";

	private SearchPluginTestHelper helper;

	@Override
	protected void setUp() throws Exception {
		// TODO: clear the relationship providers?
		WorkspaceSetupHelper.setupWorkspace();
		jp1 = WorkspaceSetupHelper.getProject1();
		jp2 = WorkspaceSetupHelper.getProject2();
		type1 = WorkspaceSetupHelper.getType(jp1, "org.eclipse.mylyn.tests.project1.views.SampleView");
		type11 = WorkspaceSetupHelper.getType(jp1, "org.eclipse.mylyn.tests.project1.Project1Plugin");
		type2 = WorkspaceSetupHelper.getType(jp2, "org.eclipse.mylyn.tests.project2.builder.ToggleNatureAction");
		plugin1 = WorkspaceSetupHelper.getFile(jp1, "plugin.xml");

		InteractionContext context = WorkspaceSetupHelper.getContext();
		ContextCorePlugin.getContextManager().activateContext(context.getHandleIdentifier());
		helper = new SearchPluginTestHelper(this);
	}

	@Override
	protected void tearDown() throws Exception {
		WorkspaceSetupHelper.clearDoiModel();
		ContextCorePlugin.getContextManager().deactivateContext(WorkspaceSetupHelper.getContext().getHandleIdentifier());
		assertFalse(ContextCorePlugin.getContextManager().isContextActive());
	}

	public void testJavaReferencesSearchDOS1() throws IOException, CoreException {

		int dos = 1;

		CompositeInteractionContext t = (CompositeInteractionContext) ContextCorePlugin.getContextManager().getActiveContext();
		ActiveSearchNotifier notifier = new ActiveSearchNotifier(t, SOURCE_ID);
		IInteractionElement searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);

		// results should be null since the scope would be null.
		// There are no landmarks to search over
		helper.searchResultsNull(notifier, searchNode, dos);

		// results should be not null, but have no references since the landmark
		// is an element in a different project
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type2.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 0);

		// results should be not null, but have no java references since the
		// landmark
		// is an element in the same project, but there are no references in it
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type11.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 1);

		// results should be not null, but have no references
		// This file type should never affect the scope
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNull(notifier, plugin1.getFullPath().toString(), PdeStructureBridge.CONTENT_TYPE,
				searchNode, dos);

		// results should be not null, and there should be 1 reference since we
		// are searching
		// the file with the element in it
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);
	}

	public void testJavaReferencesSearchDOS2() throws CoreException, IOException {
		int dos = 2;

		CompositeInteractionContext t = (CompositeInteractionContext) ContextCorePlugin.getContextManager().getActiveContext();
		ActiveSearchNotifier notifier = new ActiveSearchNotifier(t, SOURCE_ID);
		IInteractionElement searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);

		// results should be null since the scope would be null.
		// There are no landmarks to search over
		helper.searchResultsNull(notifier, searchNode, dos);

		// results should be not null, but have no references since the landmark
		// is an element in a different project
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type2.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 0);

		// results should be not null, but have no references since the
		// interesting element
		// is an element in the same project, but no references in it
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type11.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 1);

		// results should be not null, but have no references
		// This file type should never affect the scope
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNull(notifier, plugin1.getFullPath().toString(), PdeStructureBridge.CONTENT_TYPE,
				searchNode, dos);

		// results should be not null, and we should get 1 result back
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);

		// results should be null, since we have nothing to search
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNull(notifier, searchNode, dos);

		// results should be not null, and we should get 1 result back
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNullInteresting(notifier, type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);
	}

	public void testJavaReferencesSearchDOS3() throws Exception {
		int dos = 3;

		CompositeInteractionContext t = (CompositeInteractionContext) ContextCorePlugin.getContextManager().getActiveContext();
		ActiveSearchNotifier notifier = new ActiveSearchNotifier(t, SOURCE_ID);
		IInteractionElement searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);

		// results should be null since the scope would be null.
		// There are no landmarks to search over
		helper.searchResultsNull(notifier, searchNode, dos);

		// results should be not null, but have no references since the landmark
		// is an element in a different project
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type2.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 0);

		// results should be not null, and have 1 reference since the project is
		// the same
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNullInteresting(notifier, type11.getHandleIdentifier(),
				JavaStructureBridge.CONTENT_TYPE, searchNode, dos, 2);

		// results should be not null, and have 1 reference
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, plugin1.getFullPath().toString(), PdeStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);

		// results should be not null, and we should get 1 result back
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);

		// results should be null, since we have nothing to search
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNull(notifier, searchNode, dos);

		// results should be not null, and we should get 1 result back
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNullInteresting(notifier, type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);
	}

	public void testJavaReferencesSearchDOS4() throws Exception {
		// TODO this is the same as 3, but there are some flags to search
		// libraries...we should check this too

		int dos = 4;

		CompositeInteractionContext t = (CompositeInteractionContext) ContextCorePlugin.getContextManager().getActiveContext();
		ActiveSearchNotifier notifier = new ActiveSearchNotifier(t, SOURCE_ID);
		IInteractionElement searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);

		// results should be null since the scope would be null.
		// There are no landmarks to search over
		helper.searchResultsNull(notifier, searchNode, dos);

		// results should be not null, but have no references since the landmark
		// is an element in a different project
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type2.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 0);

		// results should be not null, and have 1 reference since the project is
		// the same
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNullInteresting(notifier, type11.getHandleIdentifier(),
				JavaStructureBridge.CONTENT_TYPE, searchNode, dos, 2);

		// results should be not null, and have 1 reference
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, plugin1.getFullPath().toString(), PdeStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);

		// results should be not null, and we should get 1 result back
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);

		// results should be null, since we have nothing to search
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNull(notifier, searchNode, dos);

		// results should be not null, and we should get 1 result back
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNullInteresting(notifier, type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE,
				searchNode, dos, 2);
	}

	public void testJavaReferencesSearchDOS5() throws IOException, CoreException {
		int dos = 5;

		// NOTE: as of 3.2M3 there is a plugin.xml reference
		CompositeInteractionContext t = (CompositeInteractionContext) ContextCorePlugin.getContextManager().getActiveContext();
		ActiveSearchNotifier notifier = new ActiveSearchNotifier(t, SOURCE_ID);
		IInteractionElement searchNode = notifier.getElement(type1.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);

		// we should have 1 result since we are searching the entire workspace
		helper.searchResultsNotNull(notifier, searchNode, dos, 2);

		// we should have no results since there are no java references in the
		// workspace
		// NOTE: as of 3.2M3 there is a plugin.xml reference
		searchNode = notifier.getElement(type2.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE);
		helper.searchResultsNotNull(notifier, searchNode, dos, 1);
	}

	public List<?> search(int dos, IInteractionElement node) {
		if (node == null)
			return null;

		// test with each of the sepatations
		JavaReferencesProvider prov = new JavaReferencesProvider();

		TestActiveSearchListener l = new TestActiveSearchListener(prov);
		IMylarSearchOperation o = prov.getSearchOperation(node, IJavaSearchConstants.REFERENCES, dos);
		if (o == null)
			return null;

		SearchPluginTestHelper.search(o, l);
		return l.getResults();
	}
}
