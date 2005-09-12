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
/*
 * Created on Jul 8, 2004
  */
package org.eclipse.mylar.core.tests;

import junit.framework.TestCase;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.Workbench;
 
/**
 * @author Mik Kersten
 */
public abstract class AbstractManualTest extends TestCase {
    
    private boolean questionResponse = false;
    
	public synchronized boolean confirmWithUser(String message) {		
		
	    questionResponse = false;
		final String finalMsg = message;
		Workbench.getInstance().getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					IWorkbenchWindow iww = Workbench.getInstance().getActiveWorkbenchWindow();

					Shell shell = iww.getShell();
					questionResponse = MessageDialog.openQuestion(
						shell,
						"JUnit Verification",
						finalMsg);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});

		return questionResponse;
	}

}
