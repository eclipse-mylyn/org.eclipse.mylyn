/*******************************************************************************
 * Copyright (c) 2004, 2008 Jeff Pound and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A wizard page to enter details of a new attachment.
 * 
 * @author Jeff Pound
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskAttachmentPage extends WizardPage {

	private static List<String> contentTypes;

	static {
		contentTypes = new LinkedList<String>();
		contentTypes.add("text/plain"); //$NON-NLS-1$
		contentTypes.add("text/html"); //$NON-NLS-1$
		contentTypes.add("application/xml"); //$NON-NLS-1$
		contentTypes.add("image/gif"); //$NON-NLS-1$
		contentTypes.add("image/jpeg"); //$NON-NLS-1$
		contentTypes.add("image/png"); //$NON-NLS-1$
		contentTypes.add(FileTaskAttachmentSource.APPLICATION_OCTET_STREAM);
	}

	private Button attachContextButton;

	private Text commentText;

	private Text descriptionText;

	private Combo contentTypeList;

	private Text fileNameText;

	private Button isPatchButton;

	private final TaskAttachmentModel model;

	private boolean needsDescription;

	private final TaskAttachmentMapper taskAttachment;

	private boolean first = true;

	public TaskAttachmentPage(TaskAttachmentModel model) {
		super("AttachmentDetails"); //$NON-NLS-1$
		this.model = model;
		this.taskAttachment = TaskAttachmentMapper.createFrom(model.getAttribute());
		setTitle("Attachment Details"); //$NON-NLS-1$
		setNeedsDescription(true);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		setControl(composite);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(3, false));

		new Label(composite, SWT.NONE).setText(Messages.TaskAttachmentPage_File);
		fileNameText = new Text(composite, SWT.BORDER);
		fileNameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		if (needsDescription) {
			new Label(composite, SWT.NONE).setText(Messages.TaskAttachmentPage_Description);
			descriptionText = new Text(composite, SWT.BORDER);
			descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			descriptionText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					taskAttachment.setDescription(descriptionText.getText().trim());
					validate();
				}

			});
		}

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		label.setText(Messages.TaskAttachmentPage_Comment);
		commentText = new Text(composite, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
		commentText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		commentText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				apply();
			}

		});

		new Label(composite, SWT.NONE).setText(Messages.TaskAttachmentPage_Content_Type);// .setBackground(parent.getBackground());

		contentTypeList = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		contentTypeList.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));
		Iterator<String> iter = contentTypes.iterator();
		int i = 0;
		while (iter.hasNext()) {
			String next = iter.next();
			contentTypeList.add(next);
			i++;
		}

		/* Update attachment on select content type */
		contentTypeList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				taskAttachment.setContentType(contentTypeList.getItem(contentTypeList.getSelectionIndex()));
				validate();
			}
		});
		contentTypeList.select(0);
		taskAttachment.setContentType(contentTypeList.getItem(0));

		// TODO: is there a better way to pad?
		new Label(composite, SWT.NONE);

		isPatchButton = new Button(composite, SWT.CHECK);
		isPatchButton.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));
		isPatchButton.setText(Messages.TaskAttachmentPage_Patch);

		// TODO: is there a better way to pad?
		new Label(composite, SWT.NONE);

		attachContextButton = new Button(composite, SWT.CHECK);
		attachContextButton.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));
		attachContextButton.setText(Messages.TaskAttachmentPage_ATTACHE_CONTEXT);
		attachContextButton.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_ATTACH));
		attachContextButton.setEnabled(ContextCore.getContextManager()
				.hasContext(model.getTask().getHandleIdentifier()));

		/*
		 * Attachment file name listener, update the local attachment
		 * accordingly
		 */
		fileNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// determine type by extension
				taskAttachment.setFileName(fileNameText.getText());
				setContentTypeFromFilename(fileNameText.getText());
				validate();
			}
		});

		fileNameText.setText(taskAttachment.getFileName() == null ? "" : taskAttachment.getFileName()); //$NON-NLS-1$

		/* Listener for isPatch */
		isPatchButton.addSelectionListener(new SelectionAdapter() {
			private int lastSelected;

			@Override
			public void widgetSelected(SelectionEvent e) {
				taskAttachment.setPatch(isPatchButton.getSelection());
				if (isPatchButton.getSelection()) {
					lastSelected = contentTypeList.getSelectionIndex();
					contentTypeList.select(0);
					contentTypeList.setEnabled(false);
					if (attachContextButton.isEnabled()) {
						attachContextButton.setSelection(true);
					}
				} else {
					contentTypeList.setEnabled(true);
					contentTypeList.select(lastSelected);
				}
				validate();
			}
		});
		attachContextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});

		validate();
		setErrorMessage(null);

		if (descriptionText != null) {
			descriptionText.setFocus();
		} else {
			commentText.setFocus();
		}
	}

	private void validate() {
		apply();
		if (fileNameText != null && "".equals(fileNameText.getText().trim())) { //$NON-NLS-1$
			setMessage(Messages.TaskAttachmentPage_Enter_a_file_name);
			setPageComplete(false);
		} else if (descriptionText != null && "".equals(descriptionText.getText().trim())) { //$NON-NLS-1$
			setMessage(Messages.TaskAttachmentPage_Enter_a_description);
			setPageComplete(false);
		} else {
			setMessage(Messages.TaskAttachmentPage_Verify_the_content_type_of_the_attachment);
			setPageComplete(true);
		}
	}

	public TaskAttachmentModel getModel() {
		return model;
	}

	private void apply() {
		taskAttachment.applyTo(model.getAttribute());
		model.setComment(commentText.getText());
		model.setAttachContext(attachContextButton.getSelection());
		model.setContentType(taskAttachment.getContentType());
	}

	private void setContentType(String contentType) {
		String[] typeList = contentTypeList.getItems();
		for (int i = 0; i < typeList.length; i++) {
			if (typeList[i].equals(contentType)) {
				contentTypeList.select(i);
				taskAttachment.setContentType(contentType);
				validate();
				break;
			}
		}
	}

	private void setContentTypeFromFilename(String fileName) {
		setContentType(FileTaskAttachmentSource.getContentTypeFromFilename(fileName));
	}

	private void setFilePath(String path) {
		fileNameText.setText(path);
		taskAttachment.setFileName(path);
		if (path.endsWith(".patch")) { //$NON-NLS-1$
			isPatchButton.setSelection(true);
			taskAttachment.setPatch(true);
			if (attachContextButton.isEnabled()) {
				attachContextButton.setSelection(true);
			}
		}
		validate();
	}

	public void setNeedsDescription(boolean supportsDescription) {
		this.needsDescription = supportsDescription;
	}

	public boolean supportsDescription() {
		return needsDescription;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			if (fileNameText.getText().length() == 0) {
				setFilePath(getModel().getSource().getName());
				setContentType(getModel().getSource().getContentType());
			}
		}
		super.setVisible(visible);
		if (first) {
			if (descriptionText != null) {
				descriptionText.setFocus();
			} else {
				commentText.setFocus();
			}
			first = false;
		}
	}

}
