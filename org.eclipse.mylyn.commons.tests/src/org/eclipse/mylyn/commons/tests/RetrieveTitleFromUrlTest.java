/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class RetrieveTitleFromUrlTest extends TestCase {

	private String retrievedTitle;

	public void testRetrieve() throws InterruptedException, InvocationTargetException {
		final String url = "http://eclipse.org/mylyn";
		final String knownTitle = "Eclipse Mylyn Open Source Project";
		AbstractRetrieveTitleFromUrlJob job = new AbstractRetrieveTitleFromUrlJob(url) {
			@Override
			public void titleRetrieved(String title) {
				retrievedTitle = title;
			}
		};
		job.schedule();
		job.join();
		assertEquals(knownTitle, job.getPageTitle());
		// process pending events
		while (PlatformUI.getWorkbench().getDisplay().readAndDispatch()) {
		}
		assertEquals(knownTitle, retrievedTitle);
	}

}
