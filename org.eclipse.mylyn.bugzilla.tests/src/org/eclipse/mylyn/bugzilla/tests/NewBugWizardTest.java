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

package org.eclipse.mylar.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.bugzilla.core.NewBugModel;
import org.eclipse.mylar.bugzilla.ui.wizard.AbstractWizardDataPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class NewBugWizardTest extends TestCase {
	
	public void testPlatformOptions() {
		NewBugModel model = new NewBugModel();
		AbstractWizardDataPage page = new TestWizardDataPage();
		page.setPlatformOptions(model);
		
		String os = Platform.getOS();
		String platform = Platform.getOSArch();
		
		assertEquals(os, model.getAttribute(AbstractWizardDataPage.ATTRIBUTE_OS));
		assertEquals(platform, model.getAttribute(AbstractWizardDataPage.ATTRIBUTE_PLATFORM));
	}
	
	static class TestWizardDataPage extends AbstractWizardDataPage {

		public TestWizardDataPage() {
			super("", "", "", PlatformUI.getWorkbench());
		}
	}
	
}
