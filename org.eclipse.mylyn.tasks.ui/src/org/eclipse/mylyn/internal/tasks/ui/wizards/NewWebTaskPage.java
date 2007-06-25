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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for creating new tickets through a web browser.
 * 
 * @author Eugene Kuleshov
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class NewWebTaskPage extends WizardPage {

	public NewWebTaskPage() {
		super("New Task");

		setTitle("Create via Web Browser");
		setDescription("This will open a web browser that can be used to create a new task.\n"
				+ "NOTE: you may need to log in via the web UI.");
	}

	public void createControl(Composite parent) {
		Text text = new Text(parent, SWT.WRAP);
		text.setEditable(false);
		text.setText("Once submitted synchronize queries or add the task to a category.\n");
		setControl(text);
	}

}
