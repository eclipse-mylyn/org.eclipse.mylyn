/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Yatta Solutions -  WorkingSet tests (bug 334024)
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.sdk.util.ResourceTestUtil;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.util.AbstractResourceContextTest;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Carsten Reckord (bug 334024: focused package explorer not working if top level element is working set)
 */
@SuppressWarnings("nls")
public class ResourceContextTest extends AbstractResourceContextTest {

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

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(false);
		// re-enable ResourceModifiedDateExclusionStrategy
		ResourcesUiBridgePlugin.getDefault()
		.getPreferenceStore()
		.setValue(ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS, true);

		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.getWorkingSet("TestWorkingSet");
		if (workingSet != null) {
			workingSetManager.removeWorkingSet(workingSet);
		}
	}

	public void testResourceSelect() throws CoreException {
		ContextCore.getContextManager().setContextCapturePaused(true);
		IFile file = project.getProject().getFile("file");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ContextCore.getContextManager().setContextCapturePaused(false);

		monitor.selectionChanged(navigator, new StructuredSelection(file));
		element = ContextCore.getContextManager().getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testFileNotAddedIfExcluded() throws CoreException {
		Set<String> previousExcludions = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<>();
		exclude.add("boring");
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(exclude);

		IFile file = project.getProject().getFile("boring");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(previousExcludions);
	}

	public void testPatternNotAddedIfExcluded() throws CoreException {
		Set<String> previousExcludions = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<>();
		exclude.add("b*.txt");
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(exclude);

		IFile file = project.getProject().getFile("boring.txt");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(previousExcludions);
	}

	public void testPatternNotAddedMatching() throws CoreException {

		Set<String> previousExcludions = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<>();
		exclude.add("**/.*");
		exclude.add(".*");
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(exclude);

		IFile file = project.getProject().getFile(".boring");
		file.create(null, true, null);
		assertTrue(file.exists());
		IInteractionElement element = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());

		file = project.getProject().getFile("boring");
		file.create(null, true, null);
		assertTrue(file.exists());
		element = ContextCore.getContextManager().getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());

		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(previousExcludions);
	}

	public void testFileAdded() throws CoreException {
		IFile file = project.getProject().getFile("new-file" + new Date().getTime() + ".txt");
		assertFalse(file.exists());
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testFolderAddedOnCreation() throws CoreException {
		IFolder folder = project.getProject().getFolder("folder");
		folder.create(true, true, null);
		assertTrue(folder.exists());

		IInteractionElement element = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(folder));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testProjectClose() throws CoreException, UnsupportedEncodingException {
		IProject project2 = project.getProject();
		createRealFiles(project2);
		context.reset();

		assertEquals(0, context.getInteractionHistory().size());
		project2.close(null);
		assertEquals(0, context.getInteractionHistory().size());
	}

	public void testProjectOpen() throws CoreException, UnsupportedEncodingException {
		IProject project2 = project.getProject();
		createRealFiles(project2);
		context.reset();

		assertEquals(0, context.getInteractionHistory().size());
		project2.close(null);
		assertEquals(0, context.getInteractionHistory().size());
		project2.open(null);
		assertEquals(0, context.getInteractionHistory().size());
	}

	public void testProjectDelete() throws CoreException, UnsupportedEncodingException {
		IProject project2 = project.getProject();
		createRealFiles(project2);
		context.reset();

		assertEquals(0, context.getInteractionHistory().size());
		ResourceTestUtil.deleteProject(project2);
		assertEquals(0, context.getInteractionHistory().size());
	}

	/**
	 * Test that working sets are properly handled by the resource bridge
	 */
	public void testWorkingSetHandledByResourceBridge() {
		IProject project2 = project.getProject();

		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.createWorkingSet("TestWorkingSet", new IAdaptable[] { project2 });

		assertTrue(structureBridge.acceptsObject(workingSet));

		AbstractContextStructureBridge workingSetBridge = ContextCore.getStructureBridge(workingSet);
		assertEquals(ContextCore.CONTENT_TYPE_RESOURCE, workingSetBridge.getContentType());
	}

	/**
	 * Test that working sets are filtered based on the interest of their contents
	 */
	public void testWorkingSetFiltering() throws CoreException {
		IProject project2 = project.getProject();
		IFile file = project2.getFile("file");
		file.create(null, true, null);
		assertTrue(file.exists());

		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.createWorkingSet("TestWorkingSet", new IAdaptable[] { project2 });
		workingSetManager.addWorkingSet(workingSet);

		context.reset();
		assertEquals(0, context.getInteractionHistory().size());

		assertTrue(structureBridge.canFilter(workingSet));

		monitor.selectionChanged(navigator, new StructuredSelection(file));
		IInteractionElement element = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
		assertFalse(structureBridge.canFilter(workingSet));
	}

	@SuppressWarnings("deprecation")
	private void createRealFiles(IProject project) throws CoreException, UnsupportedEncodingException {
		// we need to have contents for the file to be local
		StringBuilder fileContents = new StringBuilder("FileContents");
		ByteArrayInputStream fileInput = new ByteArrayInputStream(fileContents.toString().getBytes("UTF-8"));
		IFile file = project.getFile("test.txt");
		file.create(fileInput, true, null);
		assertTrue(file.exists());

		IFolder folder = project.getFolder("testFolder");
		folder.create(true, true, null);
		assertTrue(folder.exists());

		// we need to have contents for the file to be local
		ByteArrayInputStream fileInFolderInput = new ByteArrayInputStream(fileContents.toString().getBytes("UTF-8"));
		IFile fileInFolder = folder.getFile("test.txt");
		fileInFolder.create(fileInFolderInput, true, null);
		assertTrue(fileInFolder.exists());

		assertTrue(file.isLocal(0));
		assertTrue(fileInFolder.isLocal(0));
	}

	// XXX: Put back
//	public void testDecrementOfFile() throws CoreException, InvocationTargetException, InterruptedException {
//		IFolder folder = project.getProject().getFolder("folder");
//		folder.create(true, true, null);
//		IFile file = project.getProject().getFile(new Path("folder/foo.txt"));
//		file.create(null, true, null);
//
//		monitor.selectionChanged(navigator, new StructuredSelection(file));
//		monitor.selectionChanged(navigator, new StructuredSelection(folder));
//
//		IInteractionElement fileElement = ContextCorePlugin.getContextManager().getElement(
//				structureBridge.getHandleIdentifier(file));
//		IInteractionElement folderElement = ContextCorePlugin.getContextManager().getElement(
//				structureBridge.getHandleIdentifier(folder));
//
//		assertTrue(fileElement.getInterest().isInteresting());
//		assertTrue(folderElement.getInterest().isInteresting());
//
//		assertTrue(ContextCorePlugin.getContextManager().manipulateInterestForElement(folderElement, false, false,
//				"test"));
//
//		assertFalse(folderElement.getInterest().isInteresting());
//		assertFalse(fileElement.getInterest().isInteresting());
//	}
}
