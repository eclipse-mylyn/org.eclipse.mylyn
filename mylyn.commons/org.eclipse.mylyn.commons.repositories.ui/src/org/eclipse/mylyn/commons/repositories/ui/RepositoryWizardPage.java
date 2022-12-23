/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.ui.dialogs.IValidatable;
import org.eclipse.mylyn.internal.commons.repositories.ui.RepositoryUiUtil;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 */
public class RepositoryWizardPage extends WizardPage implements IPartContainer, IAdaptable, IValidatable {

	private IAdaptable element;

	private RepositoryLocationPart part;

	private RepositoryLocation workingCopy;

	public RepositoryWizardPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	public boolean canValidate() {
		return part.canValidate();
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		String message = getMessage();

		part = doCreateRepositoryPart();
		part.setServiceLocator(this);
		setControl(part.createContents(parent));
		Dialog.applyDialogFont(parent);

		setMessage(message);
		RepositoryUiUtil.testCredentialsStore(getWorkingCopy().getId(), this);
	}

	protected RepositoryLocationPart doCreateRepositoryPart() {
		return new RepositoryLocationPart(getWorkingCopy());
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == WizardPage.class) {
			return this;
		}
		if (adapter == DialogPage.class) {
			return this;
		}
		if (adapter == IPartContainer.class) {
			return this;
		}
		return null;
	}

	public IAdaptable getElement() {
		return element;
	}

	public RepositoryLocationPart getPart() {
		return part;
	}

	protected RepositoryLocation getWorkingCopy() {
		if (workingCopy == null) {
			workingCopy = (RepositoryLocation) getElement().getAdapter(RepositoryLocation.class);
		}
		return workingCopy;
	}

	public boolean needsValidation() {
		return part.needsValidation();
	}

	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {
		getContainer().run(fork, cancelable, runnable);
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

	public void updateButtons() {
		getContainer().updateButtons();
	}

	public void validate() {
		part.validate();
	}

}
