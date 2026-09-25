/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;

/**
 * @author Steffen Pingel
 */
public class PeopleView extends CommonNavigator {

	private final PeopleCategory rootCategory;

	public PeopleView() {
		rootCategory = new PeopleCategory();
	}

	@Override
	protected Object getInitialInput() {
		return rootCategory;
	}

	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		getCommonViewer().expandAll();
	}

}
