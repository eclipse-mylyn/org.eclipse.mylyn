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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.internal.events.ResourceDelta;
import org.eclipse.core.internal.events.ResourceDeltaInfo;
import org.eclipse.core.internal.resources.ResourceInfo;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.util.AbstractResourceContextTest;
import org.eclipse.mylyn.internal.resources.ui.ResourceChangeMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class ResourceChangeMonitorTest extends AbstractResourceContextTest {

	private static class MockResourceDelta extends ResourceDelta {

		protected MockResourceDelta(IPath path, ResourceDeltaInfo deltaInfo) {
			super(path, deltaInfo);

		}

		static MockResourceDelta createMockDelta(String path, String[] childPaths, int status, int resourceType) {
			// create the delta and fill it with information

			ResourceDeltaInfo deltaInfo = new ResourceDeltaInfo((Workspace) ResourcesPlugin.getWorkspace(), null, null);
			MockResourceDelta result = new MockResourceDelta(new Path(path), deltaInfo);

			ResourceInfo info = new ResourceInfo();

			info.setType(resourceType);

			result.setNewInfo(info);
			result.setOldInfo(info);

			Set<MockResourceDelta> children = new HashSet<>();

			if (childPaths != null) {
				for (String childPath : childPaths) {
					children.add(createMockDelta(path + childPath, null, status, IResource.FILE));
				}
			}

			result.setChildren(children.toArray(new MockResourceDelta[0]));
			result.setStatus(status);

			return result;
		}

		@Override
		public void setChildren(ResourceDelta[] children) {
			super.setChildren(children);
		}
	}

	private ResourceChangeMonitor changeMonitor;

	private IFolder folder;

	private IFile fileInFolder;

	private IFile file;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		changeMonitor = new ResourceChangeMonitor();
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(true);

		ContextCore.getContextManager().setContextCapturePaused(true);

		file = project.getProject().getFile("test.txt");
		file.create(null, true, null);
		assertTrue(file.exists());

		folder = project.getProject().getFolder("testFolder");
		folder.create(true, true, null);
		assertTrue(folder.exists());

		fileInFolder = folder.getFile("test.txt");
		fileInFolder.create(null, true, null);
		assertTrue(fileInFolder.exists());

		ContextCore.getContextManager().setContextCapturePaused(false);
		// disable ResourceModifiedDateExclusionStrategy
		ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.setValue(ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS, false);

	}

	@Override
	protected void tearDown() throws Exception {
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(false);
		super.tearDown();
		// re-enable ResourceModifiedDateExclusionStrategy
		ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.setValue(ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS, true);
	}

	public void testCreatedFile() throws CoreException {
		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				new String[] { "/test.txt" }, IResourceDelta.ADDED | IResourceDelta.CONTENT, IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNotNull(element);
		assertTrue(element.getInterest().isPropagated());
	}

	public void testLargeFileChangeNotAddedToContext() throws CoreException {

		List<String> filePaths = new ArrayList<>();
		filePaths.add("/" + file.getProjectRelativePath().toPortableString());
		for (int i = 0; i < 10; i++) {
			IFile newFile = project.getProject().getFile("test" + i + ".txt");
			newFile.create(null, true, null);
			assertTrue(newFile.exists());
			filePaths.add("/" + newFile.getProjectRelativePath().toPortableString());
		}

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				filePaths.toArray(new String[filePaths.size()]), IResourceDelta.CHANGED | IResourceDelta.CONTENT,
				IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
	}

	public void testLargeFileAddedNotAddedToContext() throws CoreException {

		List<String> filePaths = new ArrayList<>();
		filePaths.add("/" + file.getProjectRelativePath().toPortableString());
		for (int i = 0; i < 10; i++) {
			IFile newFile = project.getProject().getFile("test" + i + ".txt");
			newFile.create(null, true, null);
			assertTrue(newFile.exists());
			filePaths.add("/" + newFile.getProjectRelativePath().toPortableString());
		}

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				filePaths.toArray(new String[filePaths.size()]), IResourceDelta.ADDED | IResourceDelta.CONTENT,
				IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
	}

	public void testLargeFolderAddedNotAddedToContext() throws CoreException {

		List<String> folderPaths = new ArrayList<>();
		folderPaths.add("/" + folder.getProjectRelativePath().toPortableString());
		for (int i = 0; i < 3; i++) {
			IFolder newFolder = project.getProject().getFolder("testFolder" + i);
			newFolder.create(true, true, null);
			assertTrue(newFolder.exists());
			folderPaths.add("/" + newFolder.getProjectRelativePath().toPortableString());
		}

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				folderPaths.toArray(new String[folderPaths.size()]), IResourceDelta.ADDED | IResourceDelta.CONTENT,
				IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(folder).getHandleIdentifier(folder);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
	}

	public void testLargeFolderChangeNotAddedToContext() throws CoreException {

		List<String> folderPaths = new ArrayList<>();
		folderPaths.add("/" + folder.getProjectRelativePath().toPortableString());
		for (int i = 0; i < 3; i++) {
			IFolder newFolder = project.getProject().getFolder("testFolder" + i);
			newFolder.create(true, true, null);
			assertTrue(newFolder.exists());
			folderPaths.add("/" + newFolder.getProjectRelativePath().toPortableString());
		}

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				folderPaths.toArray(new String[folderPaths.size()]), IResourceDelta.CHANGED | IResourceDelta.CONTENT,
				IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(folder).getHandleIdentifier(folder);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
	}

	public void testFolderAdded() throws CoreException {

		List<String> folderPaths = new ArrayList<>();
		folderPaths.add("/" + folder.getProjectRelativePath().toPortableString());

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				folderPaths.toArray(new String[folderPaths.size()]), IResourceDelta.ADDED | IResourceDelta.CONTENT,
				IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(folder).getHandleIdentifier(folder);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
	}

	public void testModifiedFile() throws CoreException {
		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				new String[] { "/test.txt" }, IResourceDelta.CHANGED | IResourceDelta.CONTENT, IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNotNull(element);
		assertTrue(element.getInterest().isPredicted());
	}

	public void testDerrivedFileChanged() throws CoreException {
		fileInFolder.setDerived(true, null);

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				new String[] { "/test.txt" }, IResourceDelta.CHANGED | IResourceDelta.CONTENT, IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(fileInFolder).getHandleIdentifier(fileInFolder);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
	}

	public void testDerrivedFolderChanged() throws CoreException {
		folder.setDerived(true, null);
		fileInFolder.setDerived(false, null);

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(), null,
				IResourceDelta.CHANGED | IResourceDelta.CONTENT, IResource.PROJECT);

		MockResourceDelta child = MockResourceDelta.createMockDelta(
				"/" + project.getProject().getName() + "/" + folder.getName(),
				new String[] { "/" + folder.getName() + "/test.txt" }, IResourceDelta.CHANGED | IResourceDelta.CONTENT,
				IResource.FOLDER);

		delta.setChildren(new ResourceDelta[] { child });

		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(fileInFolder).getHandleIdentifier(folder);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
		handle = ContextCore.getStructureBridge(fileInFolder).getHandleIdentifier(fileInFolder);
		assertNotNull(handle);
		element = context.get(handle);
		assertNull(element);
	}

	public void testExcluded() throws CoreException {
		try {
			ResourcesUiPreferenceInitializer.addForcedExclusionPattern("*.txt");

			MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
					new String[] { "/test.txt" }, IResourceDelta.CHANGED | IResourceDelta.CONTENT, IResource.PROJECT);
			IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
			changeMonitor.resourceChanged(event);
			String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
			assertNotNull(handle);
			IInteractionElement element = context.get(handle);
			assertNull(element);
		} finally {
			ResourcesUiPreferenceInitializer.removeForcedExclusionPattern("*.txt");
		}
	}

}
