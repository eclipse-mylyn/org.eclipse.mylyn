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

package org.eclipse.mylyn.commons.ui.team;

import java.util.UUID;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.commons.repositories.InMemoryCredentialsStore;
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
			RepositoryLocation element = (RepositoryLocation) getElement().getAdapter(RepositoryLocation.class);
			workingCopy = new RepositoryLocation(element);
			if (workingCopy.getId() == null) {
				workingCopy.setProperty(RepositoryLocation.PROPERTY_ID, UUID.randomUUID().toString());
			}
			workingCopy.setCredentialsStore(new InMemoryCredentialsStore(workingCopy.getCredentialsStore()));
		}
		return workingCopy;
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
