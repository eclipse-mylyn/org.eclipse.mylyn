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

package org.eclipse.mylar.tests.integration;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugs.java.JavaStackTraceHyperlinkAdapter;
import org.eclipse.mylar.internal.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.ui.forms.events.IHyperlinkListener;

/**
 * @author Mik Kersten
 */
public class ExtensionPointInitializationTest extends TestCase {

	public void testBugzillaHyperlinkDetector() {
		IHyperlinkListener listener = MylarTaskListPlugin.getDefault().getTaskHyperlinkListeners().get(AbstractBugEditor.HYPERLINK_TYPE_JAVA);
		assertTrue(listener instanceof JavaStackTraceHyperlinkAdapter);
	}
	
}
