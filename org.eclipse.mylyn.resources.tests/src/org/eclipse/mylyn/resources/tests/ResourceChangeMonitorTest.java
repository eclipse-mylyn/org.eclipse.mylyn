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

package org.eclipse.mylyn.resources.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.resources.ui.ResourceChangeMonitor;

/**
 * @author Mik Kersten
 */
public class ResourceChangeMonitorTest extends TestCase {

	private ResourceChangeMonitor changeMonitor = new ResourceChangeMonitor();
	
	public void testExclusionPattern() {
		Set<String> patterns = new HashSet<String>();
		patterns.add(".*");
		patterns.add("target");
		
		IPath path1 = new Path(".foo");
		assertTrue(changeMonitor.isExcluded(path1, patterns));
		
		IPath path2 = new Path("target/bar");
		assertTrue(changeMonitor.isExcluded(path2, patterns));
		
		IPath path3 = new Path("bar/target/bar");
		assertTrue(changeMonitor.isExcluded(path3, patterns));
		
		IPath path4 = new Path("bla/bla");
		assertFalse(changeMonitor.isExcluded(path4, patterns));
	}
	
	public void testInclusion() {
		IPath path4 = new Path("bla/bla");
		assertFalse(changeMonitor.isExcluded(path4, new HashSet<String>()));
	}
	
}
