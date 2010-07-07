/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.repositories;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.commons.ui.repositories.RepositoryControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 */
public class RepositoryWizardPage extends WizardPage {

	private RepositoryControl control;

	private IAdaptable element;

	private RepositoryLocation workingCopy;

	public RepositoryWizardPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		control = new RepositoryControl(parent, SWT.NONE) {
			@Override
			protected RepositoryLocation getWorkingCopy() {
				return RepositoryWizardPage.this.getWorkingCopy();
			}
		};

		Dialog.applyDialogFont(control);
		setControl(control);
	}

	public IAdaptable getElement() {
		return element;
	}

	RepositoryLocation getWorkingCopy() {
		if (workingCopy == null) {
			RepositoryLocation element = (RepositoryLocation) getElement().getAdapter(RepositoryLocation.class);
			workingCopy = new RepositoryLocation(element.getProperties());
		}
		return workingCopy;
	}

	/**
	 * Sets the element that owns properties shown on this page.
	 * 
	 * @param element
	 *            the element
	 */
	public void setElement(IAdaptable element) {
		this.element = element;
	}

}
