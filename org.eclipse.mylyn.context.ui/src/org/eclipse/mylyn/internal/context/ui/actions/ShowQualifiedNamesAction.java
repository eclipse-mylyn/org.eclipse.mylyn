/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.context.ui.views.ActiveSearchView;

/**
 * @author Mik Kersten
 */
public class ShowQualifiedNamesAction extends Action {

	public static final String LABEL = "Qualify Member Names";

	public static final String ID = "org.eclipse.mylyn.ui.views.elements.qualify";

	private final ActiveSearchView view;

	public ShowQualifiedNamesAction(ActiveSearchView view) {
		super(LABEL, IAction.AS_CHECK_BOX);
		this.view = view;
		setId(ID);
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(ContextUiImages.QUALIFY_NAMES);
		update(ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(ID));
	}

	public void update(boolean on) {
		view.setQualifiedNameMode(on);
		setChecked(on);
		ContextUiPlugin.getDefault().getPreferenceStore().setValue(ID, on);
	}

	@Override
	public void run() {
		update(!ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(ID));
	}
}
