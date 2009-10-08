/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTaskEditorSection extends AbstractTaskEditorPart {

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		boolean expand = shouldExpandOnCreate();
		final Section section = createSection(parent, toolkit, expand);
		if (expand) {
			Control sectionClient = createSectionClient(toolkit, section);
			section.setClient(sectionClient);
		} else {
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					if (section.getClient() == null) {
						Control sectionClient = createSectionClient(toolkit, section);
						section.setClient(sectionClient);
						getTaskEditorPage().reflow();
					}
				}
			});
		}
		setSection(toolkit, section);
	}

	protected abstract boolean shouldExpandOnCreate();

	protected abstract Control createSectionClient(FormToolkit toolkit, Section section);

	@Override
	protected void setSection(FormToolkit toolkit, Section section) {
		if (section.getTextClient() == null) {
			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
			fillToolBar(toolBarManager);

			// TODO toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

			if (toolBarManager.getSize() > 0) {
				Composite toolbarComposite = toolkit.createComposite(section);
				toolbarComposite.setBackground(null);
				RowLayout rowLayout = new RowLayout();
				rowLayout.marginTop = 2;
				rowLayout.marginBottom = 0;
				rowLayout.center = true;
				toolbarComposite.setLayout(rowLayout);

				createInfoOverlay(toolbarComposite, section, toolkit);

				toolBarManager.createControl(toolbarComposite);
				section.clientVerticalSpacing = 0;
				section.descriptionVerticalSpacing = 0;
				section.setTextClient(toolbarComposite);
			}
		}
		setControl(section);
	}

	/**
	 * Clients can implement to provide attribute overlay text
	 * 
	 * @param section
	 */
	private void createInfoOverlay(Composite composite, Section section, FormToolkit toolkit) {
		String text = getInfoOverlayText();
		if (text == null) {
			return;
		}

		final Label label = toolkit.createLabel(composite, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setBackground(null);
		label.setVisible(!section.isExpanded());

		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				label.setVisible(!e.getState());
			}
		});
	}

	/**
	 * Clients can override to show summary information for the part.
	 */
	protected String getInfoOverlayText() {
		return null;
	}

}
