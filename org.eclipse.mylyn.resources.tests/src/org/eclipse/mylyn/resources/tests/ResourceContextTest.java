/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;

/**
 * @author Mik Kersten
 */
public class ResourceContextTest extends AbstractResourceContextTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ResourcesUiBridgePlugin.getDefault().setResourceMonitoringEnabled(true);
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(false);
	}

	public void testResourceSelect() throws CoreException {
		ContextCorePlugin.getContextManager().setContextCapturePaused(true);
		IFile file = project.getProject().getFile("file");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ContextCorePlugin.getContextManager().setContextCapturePaused(false);

		monitor.selectionChanged(navigator, new StructuredSelection(file));
		element = ContextCorePlugin.getContextManager().getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testFileNotAddedIfExcluded() throws CoreException {
		Set<String> previousExcludions = ResourcesUiBridgePlugin.getDefault().getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<String>();
		exclude.add("boring");
		ResourcesUiBridgePlugin.getDefault().setExcludedResourcePatterns(exclude);

		IFile file = project.getProject().getFile("boring");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ResourcesUiBridgePlugin.getDefault().setExcludedResourcePatterns(previousExcludions);
	}

	public void testPatternNotAddedIfExcluded() throws CoreException {
		Set<String> previousExcludions = ResourcesUiBridgePlugin.getDefault().getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<String>();
		exclude.add("b*.txt");
		ResourcesUiBridgePlugin.getDefault().setExcludedResourcePatterns(exclude);

		IFile file = project.getProject().getFile("boring.txt");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());
		ResourcesUiBridgePlugin.getDefault().setExcludedResourcePatterns(previousExcludions);
	}

	public void testPatternNotAddedMatching() throws CoreException {
		Set<String> previousExcludions = ResourcesUiBridgePlugin.getDefault().getExcludedResourcePatterns();
		Set<String> exclude = new HashSet<String>();
		exclude.add(".*");
		ResourcesUiBridgePlugin.getDefault().setExcludedResourcePatterns(exclude);

		String pattern = ".*";
		String segment = "boring";

		String s = pattern.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
		assertFalse(segment.matches(s));
		assertTrue(".boring".matches(s));

		IFile file = project.getProject().getFile(".boring");
		file.create(null, true, null);
		assertTrue(file.exists());
		IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertFalse(element.getInterest().isInteresting());

		file = project.getProject().getFile("boring");
		file.create(null, true, null);
		assertTrue(file.exists());
		element = ContextCorePlugin.getContextManager().getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());

		ResourcesUiBridgePlugin.getDefault().setExcludedResourcePatterns(previousExcludions);
	}

	public void testFileAdded() throws CoreException {
		IFile file = project.getProject().getFile("new-file.txt");
		file.create(null, true, null);
		assertTrue(file.exists());

		IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testFolderAddedOnCreation() throws CoreException {
		IFolder folder = project.getProject().getFolder("folder");
		folder.create(true, true, null);
		assertTrue(folder.exists());

		IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(folder));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testDecrementOfFile() throws CoreException, InvocationTargetException, InterruptedException {
		IFolder folder = project.getProject().getFolder("folder");
		folder.create(true, true, null);
		IFile file = project.getProject().getFile(new Path("folder/foo.txt"));
		file.create(null, true, null);

		monitor.selectionChanged(navigator, new StructuredSelection(file));
		monitor.selectionChanged(navigator, new StructuredSelection(folder));

		IInteractionElement fileElement = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		IInteractionElement folderElement = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(folder));

		assertTrue(fileElement.getInterest().isInteresting());
		assertTrue(folderElement.getInterest().isInteresting());

		assertTrue(ContextCorePlugin.getContextManager().manipulateInterestForElement(folderElement, false, false,
				"test"));

		assertFalse(folderElement.getInterest().isInteresting());
		assertFalse(fileElement.getInterest().isInteresting());
	}
}
