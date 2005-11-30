/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.ide.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.ide.ResourceStructureBridge;

/**
 * @author Mik Kersten
 */
public class ResourcesContextTest extends AbstractResourceContextTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MylarIdePlugin.getDefault().setResourceMonitoringEnabled(true);
		MylarIdePlugin.getDefault().getInterestUpdater().setSyncExec(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		MylarIdePlugin.getDefault().getInterestUpdater().setSyncExec(false);
	}
	
	public void testResourceSelect() throws CoreException {
		IFile file = project.getProject().getFile("file");
		file.create(null, true, null);
		assertTrue(file.exists());
		
		monitor.selectionChanged(navigator, new StructuredSelection(file));
		IMylarElement element = MylarPlugin.getContextManager().getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testFileAdded() throws CoreException {
		IFile file = project.getProject().getFile("new-file.txt");
		file.create(null, true, null);
		assertTrue(file.exists());
		
		IMylarElement element = MylarPlugin.getContextManager().getElement(structureBridge.getHandleIdentifier(file));
		assertTrue(element.getInterest().isInteresting());
	}

	public void testFolderAdded() throws CoreException {
		IFolder folder = project.getProject().getFolder("folder");
		folder.create(true, true, null);
		assertTrue(folder.exists());
		
		IMylarElement element = MylarPlugin.getContextManager().getElement(structureBridge.getHandleIdentifier(folder));
		assertTrue(element.getInterest().isInteresting());
	}
	
	public void testDecrementOfFile() throws CoreException, InvocationTargetException, InterruptedException {
		IFolder folder = project.getProject().getFolder("folder");
		folder.create(true, true, null); 
		IFile file = project.getProject().getFile(new Path("folder/foo.txt"));
		file.create(null, true, null);
		
		ResourceStructureBridge bridge = new ResourceStructureBridge();
		
		monitor.selectionChanged(navigator, new StructuredSelection(file));
		monitor.selectionChanged(navigator, new StructuredSelection(folder));
		
		IMylarElement fileElement = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(file));
		IMylarElement folderElement = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(folder));
        
        assertTrue(fileElement.getInterest().isInteresting());
		assertTrue(folderElement.getInterest().isInteresting());
        
        MylarPlugin.getContextManager().manipulateInterestForNode(folderElement, false, false, "test");

        assertFalse(folderElement.getInterest().isInteresting());
        assertFalse(fileElement.getInterest().isInteresting());
    }
}
