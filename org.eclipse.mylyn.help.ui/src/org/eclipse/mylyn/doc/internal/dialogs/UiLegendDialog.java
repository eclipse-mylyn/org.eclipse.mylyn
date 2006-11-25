/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.doc.internal.dialogs;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Mik Kersten
 */
public class UiLegendDialog extends PopupDialog {

	private static final String LABEL = "Mylar UI Legend";

	private FormToolkit toolkit;

	private Form form;

	private Rectangle bounds;

	private Composite sectionClient;

	public UiLegendDialog(Shell parent) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE | SWT.ON_TOP, false, false, false, false, null, null);
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		return createDialogArea(parent);
	}

	@Override
	protected final Control createDialogArea(final Composite parent) {

		getShell().setText("Mylar UI Legend");

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.getBody().setLayout(new GridLayout());

		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);

		section.setText(LABEL);
		section.setLayout(new GridLayout());

		sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));

		String descriptionText = "xxxxxxxxxxxxxxx";
		Label descriptionLabel = toolkit.createLabel(sectionClient, descriptionText);
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(descriptionLabel);

		section.setClient(sectionClient);

		Composite buttonsComposite = toolkit.createComposite(section);
		section.setTextClient(buttonsComposite);
		buttonsComposite.setLayout(new RowLayout());
		buttonsComposite.setBackground(section.getTitleBarBackground());
		final ImageHyperlink closeHyperlink = toolkit.createImageHyperlink(buttonsComposite, SWT.NONE);
		// closeHyperlink.setBackgroundMode(SWT.INHERIT_FORCE);
		closeHyperlink.setBackground(section.getTitleBarBackground());
		closeHyperlink.setImage(TaskListImages.getImage(TaskListImages.NOTIFICATION_CLOSE));
		closeHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				close();
			}
		});

		form.pack();
		return parent;
	}

	/**
	 * Initialize the shell's bounds.
	 */
	@Override
	public void initializeBounds() {
		getShell().setBounds(restoreBounds());
	}

	private Rectangle restoreBounds() {
		bounds = form.getBounds();
		Rectangle maxBounds = null;
		if (getShell() != null && !getShell().isDisposed())
			maxBounds = getShell().getDisplay().getClientArea();
		else {
			// fallback
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			if (display != null && !display.isDisposed())
				maxBounds = display.getBounds();
		}

		if (bounds.width > -1 && bounds.height > -1) {
			if (maxBounds != null) {
				bounds.width = Math.min(bounds.width, maxBounds.width);
				bounds.height = Math.min(bounds.height, maxBounds.height);
			}
			// Enforce an absolute minimal size
			bounds.width = Math.max(bounds.width, 30);
			bounds.height = Math.max(bounds.height, 30);
		}

		if (bounds.x > -1 && bounds.y > -1 && maxBounds != null) {
			bounds.x = Math.max(bounds.x, maxBounds.x);
			bounds.y = Math.max(bounds.y, maxBounds.y);

			if (bounds.width > -1 && bounds.height > -1) {
				bounds.x = maxBounds.width - bounds.width;
				bounds.y = maxBounds.height - bounds.height;
			}
		}
		return bounds;
	}
}
