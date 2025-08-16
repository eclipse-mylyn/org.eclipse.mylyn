/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactory;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class GerritReviewerUiFactoryProvider extends AbstractUiFactoryProvider<IUser> {

	@Override
	public List<AbstractUiFactory<IUser>> createFactories(IUiContext context, IUser type) {
		List<AbstractUiFactory<IUser>> factories = new ArrayList<>();
		factories.add(new RemoveReviewerUiFactory(context, type));
		return factories;
	}

	@Override
	public Composite createControls(IUiContext context, Composite parent, FormToolkit toolkit, IUser object) {
		Composite buttonComposite = super.createControls(context, parent, toolkit, object);
		RowLayout layout = (RowLayout) buttonComposite.getLayout();
		layout.marginBottom = 0;
		layout.marginTop = 0;
		return buttonComposite;
	}

}
