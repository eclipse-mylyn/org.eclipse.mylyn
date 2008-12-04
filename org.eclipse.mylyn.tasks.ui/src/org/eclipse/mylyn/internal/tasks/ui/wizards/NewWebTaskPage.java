/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
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

	public NewWebTaskPage(ITaskMapping taskSelection) {
		super(Messages.NewWebTaskPage_New_Task);

		setTitle(Messages.NewWebTaskPage_Create_via_Web_Browser);
		setDescription(Messages.NewWebTaskPage_This_will_open_a_web_browser_that_can_be_used_to_create_a_new_task);

		// TODO display selection 
	}

	public void createControl(Composite parent) {
		Text text = new Text(parent, SWT.WRAP);
		text.setEditable(false);
		text.setText(Messages.NewWebTaskPage_Once_submitted_synchronize_queries_or_add_the_task_to_a_category);
		setControl(text);
	}

}
