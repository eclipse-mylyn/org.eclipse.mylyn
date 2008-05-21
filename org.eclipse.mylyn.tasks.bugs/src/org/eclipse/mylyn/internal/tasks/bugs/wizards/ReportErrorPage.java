/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Steffen Pingel
 */
public class ReportErrorPage extends WizardPage {

	private final IStatus status;

	public ReportErrorPage(IStatus status) {
		super("reportError");
		Assert.isNotNull(status);
		this.status = status;
		setTitle("Unexpected Error");
		setMessage("An unexcpeted error has occured");
	}

	public void createControl(Composite parent) {		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		composite.setLayout(layout);
	
		Label label = new Label(parent, SWT.NONE);
		label.setText(status.getMessage());
		
		setControl(composite);
	}
	
}