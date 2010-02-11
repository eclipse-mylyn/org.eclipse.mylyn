/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public abstract class ScrollableWizardPage extends WizardPage {

	private ScrolledComposite scrolledComposite;

	private Composite scrolledBodyComposite;

	public ScrollableWizardPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		createScrolledComposite(parent);
		updateSize4ScrolledComposite();
	}

	protected void updateSize4ScrolledComposite() {
		scrolledComposite.setMinSize(scrolledBodyComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
	}

	protected void createScrolledComposite(Composite parent) {
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL /*| SWT.BORDER*/) {
			@Override
			public Point computeSize(int hint, int hint2, boolean changed) {
				return new Point(64, 64);
			}
		};
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledBodyComposite = new Composite(scrolledComposite, SWT.NONE);
		scrolledBodyComposite.setLayout(new GridLayout());
		scrolledComposite.setContent(scrolledBodyComposite);
		setControl(scrolledComposite);
		Dialog.applyDialogFont(scrolledBodyComposite);
	}

	public Composite getScrolledBodyComposite() {
		return scrolledBodyComposite;
	}

}
