/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Gist attachment wizard page class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class GistAttachmentPage extends WizardPage {

	private TaskAttachmentMapper mapper;
	private TaskAttachmentModel model;

	private Text nameText;

	/**
	 * Create page for task attachment model
	 * 
	 * @param model
	 */
	protected GistAttachmentPage(TaskAttachmentModel model) {
		super("gistAttachmentPage"); //$NON-NLS-1$
		setTitle(Messages.GistAttachmentPage_Title);
		setDescription(Messages.GistAttachmentPage_Description);
		model.setAttachContext(false);
		mapper = TaskAttachmentMapper.createFrom(model.getAttribute());
		this.model = model;
		setPageComplete(false);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(displayArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(displayArea);

		new Label(displayArea, SWT.NONE).setText(Messages.GistAttachmentPage_LabelFile);

		nameText = new Text(displayArea, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(nameText);
		nameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateFilename();
			}
		});

		Label binaryLabel = new Label(displayArea, SWT.WRAP);
		binaryLabel
				.setText(Messages.GistAttachmentPage_LabelBinaryWarning);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
				.applyTo(binaryLabel);

		setControl(displayArea);
	}

	private void updateFilename() {
		mapper.setFileName(nameText.getText().trim());
		mapper.applyTo(model.getAttribute());
		validate();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			String current = mapper.getFileName();
			if (current == null)
				current = model.getSource().getName();
			if (current != null)
				nameText.setText(current);
			nameText.selectAll();
			nameText.setFocus();
			updateFilename();
		}
	}

	private void validate() {
		setPageComplete(!"".equals(nameText.getText().trim())); //$NON-NLS-1$
	}
}
