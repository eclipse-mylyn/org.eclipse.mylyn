/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author Steffen Pingel
 */
public class RepositoryPropertyPage extends PropertyPage implements IAdaptable {

	private RepositoryLocationPart part;

	private RepositoryLocation workingCopy;

	public RepositoryPropertyPage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);

		part = new RepositoryLocationPart(getWorkingCopy());
		part.setServiceLocator(this);
		setControl(part.createContents(parent));
		Dialog.applyDialogFont(parent);
		return getControl();
	}

	RepositoryLocation getWorkingCopy() {
		if (workingCopy == null) {
			RepositoryLocation element = getElement().getAdapter(RepositoryLocation.class);
			workingCopy = new RepositoryLocation(element);
			// use an in memory credentials store that is backed by the actual credentials store
			workingCopy.setCredentialsStore(new InMemoryCredentialsStore(workingCopy.getCredentialsStore()));
		}
		return workingCopy;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if ((adapter == DialogPage.class) || (adapter == IPartContainer.class)) {
			return this;
		}
		return null;
	}

}
