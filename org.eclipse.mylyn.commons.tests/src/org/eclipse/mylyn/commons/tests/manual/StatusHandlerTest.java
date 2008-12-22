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

package org.eclipse.mylyn.commons.tests.manual;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class StatusHandlerTest extends TestCase {

	public void testErrorDialog() {
		try {
			int i = 10 / 0;
			System.out.println(i);
		} catch (Throwable t) {
			StatusHandler.fail(new Status(IStatus.ERROR, "org.eclipse.mylyn", "whoops", t));
		}
		StatusHandler.fail(new Status(IStatus.ERROR, "org.eclipse.mylyn", "whoops"));

		assertTrue(confirmWithUser("Did an error dialog show up correctly?"));
	}

	public boolean confirmWithUser(String message) {
		final boolean[] questionResponse = new boolean[1];
		final String finalMsg = message;
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					IWorkbenchWindow iww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

					Shell shell = iww.getShell();
					questionResponse[0] = MessageDialog.openQuestion(shell, "JUnit Verification", finalMsg);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
		return questionResponse[0];
	}

}
