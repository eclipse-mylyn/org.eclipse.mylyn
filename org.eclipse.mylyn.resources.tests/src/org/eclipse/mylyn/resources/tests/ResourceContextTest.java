/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.tests.support.ContextTestUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

/**
 * @author Mik Kersten
 */
public class ResourceContextTest extends AbstractResourceContextTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		ResourcesUiBridgePlugin.getDefault().setResourceMonitoringEnabled(true);
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(true);

		ContextTestUtil.triggerContextUiLazyStart();
		// disable ResourceModifiedDateExclusionStrategy
		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().setValue(
				ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS, false);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(false);
		// re-enable ResourceModifiedDateExclusionStrategy
		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().setValue(
				ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS, true);
	}

	public void testResourceSelect() throws CoreException {
		ContextCore.getContextManager().setContextCapturePaused(true);
		IFile file = project.getProject().getFile("file");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCore.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ContextCore.getContextManager().setContextCapturePaused(false);

		monitor.selectionChanged(navigator, new StructuredSelection(file));
		element = ContextCore.getContextManager().getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testFileNotAddedIfExcluded() throws CoreException {
		Set<String> previousExcludions = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<String>();
		exclude.add("boring");
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(exclude);

		IFile file = project.getProject().getFile("boring");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCore.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(previousExcludions);
	}

	public void testPatternNotAddedIfExcluded() throws CoreException {
		Set<String> previousExcludions = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<String>();
		exclude.add("b*.txt");
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(exclude);

		IFile file = project.getProject().getFile("boring.txt");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCore.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(previousExcludions);
	}

	public void testPatternNotAddedMatching() throws CoreException {

		Set<String> previousExcludions = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<String>();
		exclude.add("**/.*");
		exclude.add(".*");
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(exclude);

		IFile file = project.getProject().getFile(".boring");
		file.create(null, true, null);
		assertTrue(file.exists());
		IInteractionElement element = ContextCore.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
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

		IInteractionElement element = ContextCore.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testFolderAddedOnCreation() throws CoreException {
		IFolder folder = project.getProject().getFolder("folder");
		folder.create(true, true, null);
		assertTrue(folder.exists());

		IInteractionElement element = ContextCore.getContextManager().getElement(
				structureBridge.getHandleIdentifier(folder));
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

	@SuppressWarnings("deprecation")
	private void createRealFiles(IProject project) throws CoreException, UnsupportedEncodingException {
		// we need to have contents for the file to be local
		StringBuffer fileContents = new StringBuffer("FileContents");
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
