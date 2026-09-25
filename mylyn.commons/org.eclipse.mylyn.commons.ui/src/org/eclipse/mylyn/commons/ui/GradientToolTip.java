/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * A Custom JFace ToolTip that paints its contents with the theme's information colors.
 *
 * @author Shawn Minto
 * @since 3.7
 */
public abstract class GradientToolTip extends ToolTip {

	public GradientToolTip(Control control, int style, boolean manualActivation) {
		super(control, style, manualActivation);
	}

	public GradientToolTip(Control control) {
		super(control);
	}

	@Override
	protected final Composite createToolTipContentArea(Event event, final Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		GridLayout headLayout = new GridLayout();
		headLayout.marginHeight = 0;
		headLayout.marginWidth = 0;
		headLayout.horizontalSpacing = 0;
		headLayout.verticalSpacing = 0;
		headLayout.numColumns = 1;
		content.setLayout(headLayout);
		content.setBackground(JFaceColors.getInformationViewerBackgroundColor(parent.getDisplay()));
		content.setForeground(JFaceColors.getInformationViewerForegroundColor(parent.getDisplay()));
		content.setBackgroundMode(SWT.INHERIT_FORCE);

		createToolTipArea(event, content);

		// force a null background so that the content background shines through
		for (Control c : content.getChildren()) {
			setNullBackground(c);
		}

		return content;
	}

	private void setNullBackground(final Control outerCircle) {
		outerCircle.setBackground(null);
		if (outerCircle instanceof Composite) {
			((Composite) outerCircle).setBackgroundMode(SWT.INHERIT_FORCE);
			for (Control c : ((Composite) outerCircle).getChildren()) {
				setNullBackground(c);
			}
		}
	}

	protected abstract Composite createToolTipArea(Event event, Composite parent);
}
