/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.core;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.gerrit.core.GerritTask;

/**
 * Testclass for GerritTask.
 * 
 * @author Mikael Kober
 */
public class GerritTaskTest extends TestCase {

	/**
	 * tests some getters.
	 */
	public void testGetters() {
		Date now = new Date();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date then = new Date();

		GerritTask task = new GerritTask("14147", "Ia0702e33",
				"Added the com.stericsson.gerrit.core project, first draft", "Test Owner",
				"semctools/eclipse/stericsson", "master", now, then, "Merged");
		assertEquals("wrong id", "14147", task.getId());
		assertEquals("wrong owner", "Test Owner", task.getOwner());
		assertEquals("wrong title", "Added the com.stericsson.gerrit.core project, first draft", task.getTitle());
		assertEquals("wrong project", "semctools/eclipse/stericsson", task.getProject());
		assertEquals("wrong creation date", now, task.getUploaded());
		assertEquals("wrong modification date", then, task.getUpdated());
		assertEquals("wrong status", "Merged", task.getStatus());
	}

}
