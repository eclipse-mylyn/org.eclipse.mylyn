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

package org.eclipse.mylar.internal.trac.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Wizard page for web-based new Trac task wizard.
 * 
 * @author Steffen Pingel
 */
public class NewTracTaskPage extends WizardPage {

	public NewTracTaskPage() {
		super("New Trac Task");

		setTitle("Create via Web Browser");
		setDescription("Once submitted synchronize queries or add the task to a category.\n"
				+ "Note: you may need to log in via the Web UI.");
	}

	public void createControl(Composite parent) {
		Label label = new Label(parent, SWT.NULL);
		setControl(label);
	}

}
