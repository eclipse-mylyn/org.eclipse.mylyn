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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.commons.ui.repositories.RepositoryControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author Steffen Pingel
 */
public class RepositoryPropertyPage extends PropertyPage {

	private RepositoryControl control;

	private RepositoryLocation workingCopy;

	public RepositoryPropertyPage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);

		control = new RepositoryControl(parent, SWT.NONE) {
			@Override
			protected RepositoryLocation getWorkingCopy() {
				return RepositoryPropertyPage.this.getWorkingCopy();
			}
		};

		Dialog.applyDialogFont(control);
		return control;
	}

	RepositoryLocation getWorkingCopy() {
		if (workingCopy == null) {
			RepositoryLocation element = (RepositoryLocation) getElement().getAdapter(RepositoryLocation.class);
			workingCopy = new RepositoryLocation(element.getProperties());
		}
		return workingCopy;
	}

}
