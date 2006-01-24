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

package org.eclipse.mylar.bugzilla.ui.wizard;

import org.eclipse.ui.IWorkbench;

/**
 * Wizard page shown when the user has chosen a product to log a bug for.
 */
public class WizardAttributesPage extends AbstractWizardDataPage {

	public WizardAttributesPage(IWorkbench workbench) {
		super("Page2", "New Bug Report", "Enter Bug", workbench);
	}

}
