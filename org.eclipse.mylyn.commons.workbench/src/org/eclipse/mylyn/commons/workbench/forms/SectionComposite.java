/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.forms;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.commons.ui.WindowUtil;
import org.eclipse.mylyn.internal.commons.workbench.CommonsWorkbenchPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
 * @since 3.7
 */
public class SectionComposite extends SharedScrolledComposite {

	private FormToolkit toolkit;

	private final Composite content;

	public SectionComposite(Composite parent, int style) {
		super(parent, style | SWT.V_SCROLL);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (toolkit != null) {
					toolkit.dispose();
					toolkit = null;
				}
			}
		});
		content = new Composite(this, SWT.NONE);
		content.setLayout(GridLayoutFactory.fillDefaults().create());
		setContent(content);
		content.setBackground(null);
		setExpandVertical(true);
		setExpandHorizontal(true);
	}

	@Override
	public Composite getContent() {
		return content;
	}

	public ExpandableComposite createSection(String title) {
		return createSection(title, SWT.NONE, false);
	}

	public ExpandableComposite createSection(String title, int expansionStyle) {
		return createSection(title, expansionStyle, false);
	}

	public ExpandableComposite createSection(String title, int expansionStyle, final boolean grabExcessVerticalSpace) {
		final ExpandableComposite section = getToolkit().createExpandableComposite(
				getContent(),
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

				resizeAndReflow();
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

	public FormToolkit getToolkit() {
		checkWidget();
		if (toolkit == null) {
			toolkit = new FormToolkit(CommonsWorkbenchPlugin.getDefault().getFormColors(getDisplay()));
		}
		return toolkit;
	}

	/**
	 * Invokes {@link #layout(boolean)} and resizes the shell if the preferred size of its children is larger than its
	 * current size and invokes {@link #reflow(boolean)} to update the scroll bar.
	 * <p>
	 * This method is invoked when sections are expanded. Clients should invoke this method when the contents of the
	 * {@link SectionComposite} are changed.
	 * 
	 * @since 3.10
	 */
	public void resizeAndReflow() {
		layout(true);

		Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		Rectangle currentbounds = getShell().getBounds();
		if (newSize.x > currentbounds.width || newSize.y > currentbounds.height) {
			Object shellData = getShell().getData();
			if (shellData instanceof Window) {
				Window window = (Window) shellData;
				Rectangle preferredSize = new Rectangle(currentbounds.x, currentbounds.y, Math.max(currentbounds.width,
						newSize.x), Math.max(currentbounds.height, newSize.y));
				Rectangle result = WindowUtil.getConstrainedShellBounds(window, preferredSize);
				getShell().setBounds(result);
			}
		}

		reflow(true);
		getParent().layout(true, true);
	}

}
