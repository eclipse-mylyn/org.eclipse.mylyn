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

package org.eclipse.mylyn.tests.integration;

import junit.framework.TestCase;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class ExtensionPointInitializationTest extends TestCase {

//	public void testBugzillaHyperlinkListener() {
//		IHyperlinkListener listener = MylarTaskListPlugin.getDefault().getTaskHyperlinkListeners().get(AbstractTaskEditor.HYPERLINK_TYPE_JAVA);
//		assertTrue(listener instanceof JavaStackTraceHyperlinkAdapter);
//		listener = MylarTaskListPlugin.getDefault().getTaskHyperlinkListeners().get(AbstractTaskEditor.HYPERLINK_TYPE_TASK);
//		assertTrue(listener instanceof TaskHyperlinkAdapter);
//	}
	
	public void testBugzillaHyperlinkDetector() {
		IHyperlinkDetector[] detectors = TasksUiPlugin.getDefault().getTaskHyperlinkDetectors();		
		assertNotNull(detectors);
		assertTrue(detectors.length > 0);
	}
	
}
