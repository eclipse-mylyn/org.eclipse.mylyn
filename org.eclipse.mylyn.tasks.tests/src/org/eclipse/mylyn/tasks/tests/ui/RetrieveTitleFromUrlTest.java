/*******************************************************************************
 * Copyright (c) 2000, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.util.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class RetrieveTitleFromUrlTest extends TestCase {

	private final AtomicReference<String> retrievedTitle = new AtomicReference<String>();

	public void testRetrieve() throws InterruptedException, InvocationTargetException {
		final String url = "http://eclipse.org/mylyn";
		final String knownTitle = "Eclipse Mylyn Open Source Project";
		AbstractRetrieveTitleFromUrlJob job = new AbstractRetrieveTitleFromUrlJob(url) {
			@Override
			public void titleRetrieved(String title) {
				retrievedTitle.set(title);
			}
		};
		job.schedule();
		job.join();
		assertEquals(knownTitle, job.getPageTitle());

		// process pending events
		while (PlatformUI.getWorkbench().getDisplay().readAndDispatch()) {
		}
		assertEquals(knownTitle, retrievedTitle.get());
	}

}
