/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.internal.events.ResourceDelta;
import org.eclipse.core.internal.events.ResourceDeltaInfo;
import org.eclipse.core.internal.resources.ResourceInfo;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.resources.ui.ResourceChangeMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

/**
 * @author Mik Kersten
 */
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

			Set<MockResourceDelta> children = new HashSet<MockResourceDelta>();

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		changeMonitor = new ResourceChangeMonitor();
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(true);
	}

	@Override
	protected void tearDown() throws Exception {
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(false);
		super.tearDown();
	}

	public void testForcedExclusionPatterns() {
		String pattern = "file:/foo";
		try {
			ResourcesUiPreferenceInitializer.addForcedExclusionPattern(pattern);
			assertTrue(ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns().contains(pattern));
			assertFalse(ResourcesUiPreferenceInitializer.getExcludedResourcePatterns().contains(pattern));
		} finally {
			ResourcesUiPreferenceInitializer.removeForcedExclusionPattern(pattern);
		}
	}

	public void testFileUriExclusionPattern() throws URISyntaxException {
		URI uri = new URI("file:/C:");
		assertTrue(ResourceChangeMonitor.isUriExcluded(uri.toString(),
				ResourceChangeMonitor.createRegexFromPattern("file:/C:")));

		uri = new URI("file:/C:/foo/bar");
		assertTrue(ResourceChangeMonitor.isUriExcluded(uri.toString(),
				ResourceChangeMonitor.createRegexFromPattern("file:/C:")));
	}

	public void testExclusionPattern() {
		Set<String> patterns = new HashSet<String>();
		patterns.add(ResourceChangeMonitor.createRegexFromPattern(".*"));
		patterns.add(ResourceChangeMonitor.createRegexFromPattern("target"));

		IPath path1 = new Path(".foo");
		assertTrue(ResourceChangeMonitor.isExcluded(path1, null, patterns));

		IPath path2 = new Path("target/bar");
		assertTrue(ResourceChangeMonitor.isExcluded(path2, null, patterns));

		IPath path3 = new Path("bar/target/bar");
		assertTrue(ResourceChangeMonitor.isExcluded(path3, null, patterns));

		IPath path4 = new Path("bla/bla");
		assertFalse(ResourceChangeMonitor.isExcluded(path4, null, patterns));
	}

	public void testInclusion() {
		IPath path4 = new Path("bla/bla");
		assertFalse(ResourceChangeMonitor.isExcluded(path4, null, new HashSet<String>()));
	}

	public void testCreatedFile() throws CoreException {
		IProject proj = project.getProject();
		IFile file = project.getProject().getFile("test.txt");
		file.create(null, true, null);
		assertTrue(file.exists());

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + proj.getName(), new String[] { "/test.txt" },
				(IResourceDelta.ADDED | IResourceDelta.CONTENT), IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNotNull(element);
		assertTrue(element.getInterest().isPropagated());
	}

	public void testModifiedFile() throws CoreException {
		IProject proj = project.getProject();
		IFile file = project.getProject().getFile("test.txt");
		file.create(null, true, null);
		assertTrue(file.exists());

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + proj.getName(), new String[] { "/test.txt" },
				(IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNotNull(element);
		assertTrue(element.getInterest().isPredicted());
	}

	public void testDerrivedFileChanged() throws CoreException {
		IProject proj = project.getProject();
		IFile file = project.getProject().getFile("test.txt");
		file.create(null, true, null);
		file.setDerived(true);
		assertTrue(file.exists());

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + proj.getName(), new String[] { "/test.txt" },
				(IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
	}

	public void testDerrivedFolderChanged() throws CoreException {
		IProject proj = project.getProject();
		IFolder folder = project.getProject().getFolder("testFolder");
		folder.create(true, true, null);
		folder.setDerived(true);

		IFile file = folder.getFile("test.txt");
		file.create(null, true, null);
		file.setDerived(false);
		assertTrue(file.exists());

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + proj.getName(), null,
				(IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.PROJECT);

		MockResourceDelta child = MockResourceDelta.createMockDelta("/" + proj.getName() + "/" + folder.getName(),
				new String[] { "/" + folder.getName() + "/test.txt" },
				(IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.FOLDER);

		delta.setChildren(new ResourceDelta[] { child });

		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(folder);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
		handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		element = context.get(handle);
		assertNull(element);
	}

	public void testExcluded() throws CoreException {
		try {
			ResourcesUiPreferenceInitializer.addForcedExclusionPattern("*.txt");
			IProject proj = project.getProject();
			IFile file = project.getProject().getFile("test.txt");
			file.create(null, true, null);
			assertTrue(file.exists());

			MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + proj.getName(),
					new String[] { "/test.txt" }, (IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.PROJECT);
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
