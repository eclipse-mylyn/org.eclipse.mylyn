/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

/**
 * @author Steffen Pingel
 * @deprecated use {@link org.eclipse.mylyn.commons.ui.forms.SectionComposite} instead
 */
@Deprecated
public class SectionComposite extends SharedScrolledComposite {

	private FormToolkit toolkit;

	private final Composite content;

	@Deprecated
	public SectionComposite(Composite parent, int style) {
		super(parent, style | SWT.V_SCROLL);
		addDisposeListener(e -> {
			if (toolkit != null) {
				toolkit.dispose();
				toolkit = null;
			}
		});
		content = new Composite(this, SWT.NONE);
		content.setLayout(GridLayoutFactory.fillDefaults().create());
		setContent(content);
		content.setBackground(null);
		setExpandVertical(true);
		setExpandHorizontal(true);
	}

	@Deprecated
	@Override
	public Composite getContent() {
		return content;
	}

	@Deprecated
	public ExpandableComposite createSection(String title) {
		return createSection(title, SWT.NONE, false);
	}

	@Deprecated
	public ExpandableComposite createSection(String title, int expansionStyle) {
		return createSection(title, SWT.NONE, false);
	}

	@Deprecated
	public ExpandableComposite createSection(String title, int expansionStyle, final boolean grabExcessVerticalSpace) {
		final ExpandableComposite section = getToolkit().createExpandableComposite(getContent(),
				ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.COMPACT
						| expansionStyle);
		section.titleBarTextMarginWidth = 0;
		section.setBackground(null);
		section.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				if ((Boolean) e.data == true && grabExcessVerticalSpace) {
					GridData g = (GridData) section.getLayoutData();
					g.verticalAlignment = GridData.FILL;
					g.grabExcessVerticalSpace = true;
					section.setLayoutData(g);
				} else {
					GridData g = (GridData) section.getLayoutData();
					g.verticalAlignment = GridData.BEGINNING;
					g.grabExcessVerticalSpace = false;
					section.setLayoutData(g);
				}
				Point newSize = section.getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
				Rectangle currentbounds = section.getShell().getBounds();
				if (newSize.x > currentbounds.width || newSize.y > currentbounds.height) {
					Object shellData = section.getShell().getData();
					if (shellData instanceof Window window) {
						Rectangle preferredSize = new Rectangle(currentbounds.x, currentbounds.y, newSize.x, newSize.y);
						Rectangle result = WindowUtil.getConstrainedShellBounds(window, preferredSize);
						section.getShell().setBounds(result);
					}
				} else {
					layout(true);
					reflow(true);
				}
			}
		});
		section.setText(title);
		if (content.getLayout() instanceof GridLayout) {
			GridDataFactory.fillDefaults()
					.indent(0, 5)
					.grab(true, false)
					.span(((GridLayout) content.getLayout()).numColumns, SWT.DEFAULT)
					.applyTo(section);
		}
		return section;
	}

	@Deprecated
	public FormToolkit getToolkit() {
		checkWidget();
		if (toolkit == null) {
			toolkit = new FormToolkit(CommonsUiPlugin.getDefault().getFormColors(getDisplay()));
		}
		return toolkit;
	}

}
