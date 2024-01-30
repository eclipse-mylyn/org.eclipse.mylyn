/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.identity.core.IIdentity;
import org.eclipse.mylyn.commons.identity.core.IIdentityService;

/**
 * @author Steffen Pingel
 */
public class PeopleContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = {};

	public PeopleContentProvider() {
	}

	@Override
	public void dispose() {
		// ignore

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

	@Override
	public Object[] getElements(Object inputElement) {
		IIdentityService identityService = IdentityUiPlugin.getDefault().getIdentityService();
		if (identityService != null) {
			return identityService.getIdentities();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IIdentity) {
			return ((IIdentity) parentElement).getAccounts();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IIdentity) {
			return true;
		}
		return false;
	}

}
