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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.commons.ui.repositories.IPartContainer;
import org.eclipse.mylyn.internal.commons.ui.repositories.RepositoryLocationPart;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 */
public class RepositoryWizardPage extends WizardPage implements IPartContainer, IAdaptable {

	private RepositoryLocationPart part;

	private IAdaptable element;

	private RepositoryLocation workingCopy;

	public RepositoryWizardPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		part = doCreateRepositoryPart();
		part.setServiceLocator(this);
		setControl(part.createContents(parent));
		Dialog.applyDialogFont(parent);
	}

	protected RepositoryLocationPart doCreateRepositoryPart() {
		return new RepositoryLocationPart(getWorkingCopy());
	}

	public IAdaptable getElement() {
		return element;
	}

	protected RepositoryLocation getWorkingCopy() {
		if (workingCopy == null) {
			workingCopy = (RepositoryLocation) getElement().getAdapter(RepositoryLocation.class);
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

	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {
		getContainer().run(fork, cancelable, runnable);
	}

	public void updateButtons() {
		getContainer().updateButtons();
	}

	public Object getAdapter(Class adapter) {
		if (adapter == DialogPage.class) {
			return this;
		}
		if (adapter == IPartContainer.class) {
			return this;
		}
		return null;
	}

}
