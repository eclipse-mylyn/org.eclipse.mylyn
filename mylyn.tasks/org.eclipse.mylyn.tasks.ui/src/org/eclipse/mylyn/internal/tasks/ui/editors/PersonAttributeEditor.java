/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Steffen Pingel
 */
public class PersonAttributeEditor extends TextAttributeEditor {

	private ContentAssistCommandAdapter contentAssistCommandAdapter = null;

	public PersonAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public Text getText() {
		return super.getText();
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		String userName = getModel().getTaskRepository().getUserName();
		if (isReadOnly() || userName == null || userName.length() == 0) {
			super.createControl(parent, toolkit);
		} else {
			final Composite composite = new Composite(parent, SWT.NONE);
			GridLayout parentLayout = new GridLayout(2, false);
			parentLayout.marginHeight = 0;
			parentLayout.marginWidth = 0;
			parentLayout.horizontalSpacing = 0;
			composite.setLayout(parentLayout);
			composite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);

			super.createControl(composite, toolkit);
			getText().setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(getText());

			final ImageHyperlink selfLink = new ImageHyperlink(composite, SWT.NO_FOCUS);
			selfLink.setToolTipText(Messages.PersonAttributeEditor_Insert_My_User_Id_Tooltip);
			//this is inserted to not get org.eclipse.swt.SWTException: Graphic is disposed on Mac OS Lion
			selfLink.setImage(CommonImages.getImage(CommonImages.PERSON_ME_SMALL));
			selfLink.setActiveImage(CommonImages.getImage(CommonImages.PERSON_ME_SMALL));
			selfLink.setHoverImage(CommonImages.getImage(CommonImages.PERSON_ME_SMALL));
			selfLink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					String userName = getModel().getTaskRepository().getUserName();
					if (userName != null && userName.length() > 0) {
						getText().setText(userName);
						setValue(userName);
					}
				}
			});
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).exclude(true).applyTo(selfLink);
			MouseTrackListener mouseListener = new MouseTrackAdapter() {
				int version = 0;

				@Override
				public void mouseEnter(MouseEvent e) {
					((GridData) selfLink.getLayoutData()).exclude = false;
					composite.layout();
					selfLink.redraw();
					version++;
				}

				@Override
				public void mouseExit(MouseEvent e) {
					final int lastVersion = version;
					Display.getDefault().timerExec(100, () -> {
						if (version != lastVersion || selfLink.isDisposed()) {
							return;
						}
						selfLink.redraw();
						((GridData) selfLink.getLayoutData()).exclude = true;
						composite.layout();
					});
				}
			};
			composite.addMouseTrackListener(mouseListener);
			getText().addMouseTrackListener(mouseListener);
			selfLink.addMouseTrackListener(mouseListener);

			toolkit.paintBordersFor(composite);
			setControl(composite);
		}
	}

	@Override
	public String getValue() {
		IRepositoryPerson repositoryPerson = getAttributeMapper().getRepositoryPerson(getTaskAttribute());
		if (repositoryPerson != null) {
			return isReadOnly() ? repositoryPerson.toString() : repositoryPerson.getPersonId();
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public void setValue(String text) {
		IRepositoryPerson person = getAttributeMapper().getTaskRepository().createPerson(text);
		getAttributeMapper().setRepositoryPerson(getTaskAttribute(), person);
		attributeChanged();
	}

	@Override
	protected void decorateIncoming(Color color) {
		if (getControl() != null) {
			getControl().setBackground(color);
		}
		if (getText() != null && getText() != getControl()) {
			getText().setBackground(color);
		}
	}

	public ContentAssistCommandAdapter getContentAssistCommandAdapter() {
		return contentAssistCommandAdapter;
	}

	public void setContentAssistCommandAdapter(ContentAssistCommandAdapter contentAssistCommandAdapter) {
		this.contentAssistCommandAdapter = contentAssistCommandAdapter;
	}
}
