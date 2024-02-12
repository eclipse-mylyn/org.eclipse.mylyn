/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.sdk.util.AbstractResourceContextTest;
import org.eclipse.mylyn.internal.resources.ui.ResourceModifiedDateExclusionStrategy;

/**
 * @author Shawn Minto
 */
@SuppressWarnings("nls")
public class ResourceModificationDateExclusionStrategyTest extends AbstractResourceContextTest {

	private ResourceModifiedDateExclusionStrategy exclusionStrategy;

	private IFolder folder;

	private IFile fileInFolder;

	private IFile file;

	@SuppressWarnings("deprecation")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// we need to make sure the files are created and modified before the context is activated
		ContextCore.getContextManager().deactivateContext(taskId);

		exclusionStrategy = new ResourceModifiedDateExclusionStrategy();
		// make sure that the strategy is enabled
		exclusionStrategy.init();
		exclusionStrategy.setEnabled(true);
		assertTrue(exclusionStrategy.isEnabled());

		long oldDate = new Date().getTime() - 5000;

		// we need to have contents for teh file to be local
		StringBuilder fileContents = new StringBuilder("FileContents");
		ByteArrayInputStream fileInput = new ByteArrayInputStream(fileContents.toString().getBytes("UTF-8"));
		file = project.getProject().getFile("test.txt");
		file.create(fileInput, true, null);
		assertTrue(file.exists());
		file.setLocalTimeStamp(oldDate);

		folder = project.getProject().getFolder("testFolder");
		folder.create(true, true, null);
		folder.setLocalTimeStamp(oldDate);
		assertTrue(folder.exists());

		// we need to have contents for the file to be local
		ByteArrayInputStream fileInFolderInput = new ByteArrayInputStream(fileContents.toString().getBytes("UTF-8"));
		fileInFolder = folder.getFile("test.txt");
		fileInFolder.create(fileInFolderInput, true, null);
		fileInFolder.setLocalTimeStamp(oldDate);
		assertTrue(fileInFolder.exists());

		assertTrue(file.isLocal(0));
		assertTrue(fileInFolder.isLocal(0));

		manager.internalActivateContext(context);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		exclusionStrategy.dispose();
		file.delete(true, null);
		folder.delete(true, null);
		fileInFolder.delete(true, null);
		ContextCore.getContextManager().deactivateContext(taskId);
	}

	public void testIsExcludedFolder() {
		assertFalse(exclusionStrategy.isExcluded(ResourcesPlugin.getWorkspace().getRoot()));
		assertFalse(exclusionStrategy.isExcluded(project.getProject()));
		assertFalse(exclusionStrategy.isExcluded(folder));
	}

	public void testIsExcludedNotEnabled() {
		exclusionStrategy.setEnabled(false);
		assertFalse(exclusionStrategy.isEnabled());

		assertFalse(exclusionStrategy.isExcluded(ResourcesPlugin.getWorkspace().getRoot()));
		assertFalse(exclusionStrategy.isExcluded(project.getProject()));
		assertFalse(exclusionStrategy.isExcluded(folder));
	}

	public void testIsExcludedFileNoContextActive() {
		ContextCore.getContextManager().deactivateContext(taskId);
		assertFalse(ContextCore.getContextManager().isContextActive());
		assertTrue(exclusionStrategy.isExcluded(file));
		assertTrue(exclusionStrategy.isExcluded(fileInFolder));
	}

	public void testIsExcludedFileContextActiveNoChange() {
		assertTrue(ContextCore.getContextManager().isContextActive());
		assertTrue(exclusionStrategy.isExcluded(file));
		assertTrue(exclusionStrategy.isExcluded(fileInFolder));
	}

	public void testIsExcludedFileContextActiveChanged() throws CoreException {
		assertTrue(ContextCore.getContextManager().isContextActive());

		assertNotNull(exclusionStrategy.getLastActivatedDate());

		file.setLocalTimeStamp(new Date().getTime());

		assertFalse(exclusionStrategy.isExcluded(file));
		assertFalse(exclusionStrategy.isExcluded(folder));
		assertTrue(exclusionStrategy.isExcluded(fileInFolder));
	}

	public void testIsExcludedFileContextActiveChangedDateRounded() throws CoreException {
		// some OS's round to the nearest second, so we need to make sure to handle this case
		assertTrue(ContextCore.getContextManager().isContextActive());

		Date lastActivatedDate = exclusionStrategy.getLastActivatedDate();
		assertNotNull(lastActivatedDate);

		long time = lastActivatedDate.getTime();
		time -= time % 1000d;

		file.setLocalTimeStamp(time);

		assertFalse(exclusionStrategy.isExcluded(file));
		assertFalse(exclusionStrategy.isExcluded(folder));
		assertTrue(exclusionStrategy.isExcluded(fileInFolder));
	}

	public void testIsExcludedFileContextActiveReallyOld() throws CoreException {
		// some OS's round to the nearest second, so we need to make sure to handle this case
		assertTrue(ContextCore.getContextManager().isContextActive());

		Date lastActivatedDate = exclusionStrategy.getLastActivatedDate();
		assertNotNull(lastActivatedDate);

		long time = lastActivatedDate.getTime();
		time -= 1000 * 60 * 10;

		file.setLocalTimeStamp(time);

		assertFalse(exclusionStrategy.isExcluded(folder));
		assertTrue(exclusionStrategy.isExcluded(file));
		assertTrue(exclusionStrategy.isExcluded(fileInFolder));
	}

	public void testIsExcludedFileContextActiveReallyNew() throws CoreException {
		// some OS's round to the nearest second, so we need to make sure to handle this case
		assertTrue(ContextCore.getContextManager().isContextActive());

		Date lastActivatedDate = exclusionStrategy.getLastActivatedDate();
		assertNotNull(lastActivatedDate);

		long time = lastActivatedDate.getTime();
		time += 1000 * 60 * 10;

		fileInFolder.setLocalTimeStamp(time);

		assertFalse(exclusionStrategy.isExcluded(fileInFolder));
		assertFalse(exclusionStrategy.isExcluded(folder));
		assertTrue(exclusionStrategy.isExcluded(file));
	}

	public void testWasModifiedAfterNullDate() {
		assertFalse(exclusionStrategy.wasModifiedAfter(null, null));
		assertFalse(exclusionStrategy.wasModifiedAfter(file, null));
		assertFalse(exclusionStrategy.wasModifiedAfter(folder, null));
	}
}
