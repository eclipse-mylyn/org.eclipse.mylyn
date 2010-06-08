/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
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

	private Section section;

	private final List<AbstractTaskEditorPart> subParts = new ArrayList<AbstractTaskEditorPart>();

	public void addSubPart(String path, AbstractTaskEditorPart part) {
		subParts.add(part);
	}

	protected abstract Control createContent(FormToolkit toolkit, Composite parent);

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		boolean expand = shouldExpandOnCreate();
		section = createSection(parent, toolkit, expand);
		if (expand) {
			createSectionClient(toolkit, section);
		} else {
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					if (section.getClient() == null) {
						createSectionClient(toolkit, section);
						getTaskEditorPage().reflow();
					}
				}
			});
		}
		setSection(toolkit, section);
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

		final Label label = toolkit.createLabel(composite, CommonUiUtil.toLabel(text));
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

	private void createSectionClient(final FormToolkit toolkit, final Section section) {
		if (subParts.size() > 0) {
			final Composite sectionClient = toolkit.createComposite(section);
			GridLayout layout = EditorUtil.createSectionClientLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			sectionClient.setLayout(layout);

			Control content = createContent(toolkit, sectionClient);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(content);
			section.setClient(sectionClient);

			for (final AbstractTaskEditorPart part : subParts) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Error creating task editor part: \"" + part.getPartId() + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
					}

					public void run() throws Exception {
						part.createControl(sectionClient, toolkit);
						if (part.getControl() != null) {
							GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(
									part.getControl());
						}
					}
				});
			}
		} else {
			Control content = createContent(toolkit, section);
			section.setClient(content);
		}
	}

	/**
	 * Clients can override to show summary information for the part.
	 */
	protected String getInfoOverlayText() {
		return null;
	}

	public Section getSection() {
		return section;
	}

	public void removeSubPart(AbstractTaskEditorPart part) {
		subParts.remove(part);
	}

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
				rowLayout.marginLeft = 0;
				rowLayout.marginRight = 0;
				rowLayout.marginTop = 0;
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

	protected abstract boolean shouldExpandOnCreate();

}
