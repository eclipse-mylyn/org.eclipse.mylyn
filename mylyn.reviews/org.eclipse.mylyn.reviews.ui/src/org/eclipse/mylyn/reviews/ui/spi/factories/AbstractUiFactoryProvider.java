/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.factories;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Supports UI context and implementation neutral creation of interface components that modify the state of model objects and their related
 * remote objects.
 *
 * @author Miles Parker
 */
public abstract class AbstractUiFactoryProvider<EObjectType> {

	public abstract List<AbstractUiFactory<EObjectType>> createFactories(IUiContext context, EObjectType type);

	public Composite createControls(IUiContext context, Composite parent, FormToolkit toolkit, EObjectType object) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout();
		layout.center = true;
		layout.spacing = 10;
		buttonComposite.setLayout(layout);
		List<AbstractUiFactory<EObjectType>> factories = createFactories(context, object);
		for (AbstractUiFactory<EObjectType> factory : factories) {
			factory.createControl(context, buttonComposite, toolkit);
		}
		return buttonComposite;
	}
}
