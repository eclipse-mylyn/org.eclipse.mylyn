/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

	private static Map<String, String> extensions2Types;

	static {
		/* For UI */
		contentTypes = new LinkedList<String>();
		contentTypes.add("text/plain");
		contentTypes.add("text/html");
		contentTypes.add("application/xml");
		contentTypes.add("image/gif");
		contentTypes.add("image/jpeg");
		contentTypes.add("image/png");
		contentTypes.add("application/octet-stream");

		/* For auto-detect */
		extensions2Types = new HashMap<String, String>();
		extensions2Types.put("txt", "text/plain");
		extensions2Types.put("html", "text/html");
		extensions2Types.put("htm", "text/html");
		extensions2Types.put("jpg", "image/jpeg");
		extensions2Types.put("jpeg", "image/jpeg");
		extensions2Types.put("gif", "image/gif");
		extensions2Types.put("png", "image/png");
		extensions2Types.put("xml", "application/xml");
		extensions2Types.put("zip", "application/octet-stream");
		extensions2Types.put("tar", "application/octet-stream");
		extensions2Types.put("gz", "application/octet-stream");
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
		super("AttachmentDetails");
		this.model = model;
		this.taskAttachment = TaskAttachmentMapper.createFrom(model.getAttribute());
		setTitle("Attachment Details");
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

		new Label(composite, SWT.NONE).setText("File");
		fileNameText = new Text(composite, SWT.BORDER);
		fileNameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		if (needsDescription) {
			new Label(composite, SWT.NONE).setText("Description");
			descriptionText = new Text(composite, SWT.BORDER);
			descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			descriptionText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validate();
					taskAttachment.setDescription(descriptionText.getText().trim());
				}

			});
		}

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		label.setText("Comment");
		commentText = new Text(composite, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
		commentText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		new Label(composite, SWT.NONE).setText("Content Type");// .setBackground(parent.getBackground());

		contentTypeList = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		contentTypeList.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));
		final HashMap<String, Integer> contentTypeIndices = new HashMap<String, Integer>();
		Iterator<String> iter = contentTypes.iterator();
		int i = 0;
		while (iter.hasNext()) {
			String next = iter.next();
			contentTypeList.add(next);
			contentTypeIndices.put(next, new Integer(i));
			i++;
		}

		/* Update attachment on select content type */
		contentTypeList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				taskAttachment.setContentType(contentTypeList.getItem(contentTypeList.getSelectionIndex()));
			}
		});
		contentTypeList.select(0);
		taskAttachment.setContentType(contentTypeList.getItem(0));

		// TODO: is there a better way to pad?
		new Label(composite, SWT.NONE);

		isPatchButton = new Button(composite, SWT.CHECK);
		isPatchButton.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));
		isPatchButton.setText("Patch");

		// TODO: is there a better way to pad?
		new Label(composite, SWT.NONE);

		attachContextButton = new Button(composite, SWT.CHECK);
		attachContextButton.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));
		attachContextButton.setText("Attach Context");
		attachContextButton.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_ATTACH));
		attachContextButton.setEnabled(ContextCore.getContextManager()
				.hasContext(model.getTask().getHandleIdentifier()));

		/*
		 * Attachment file name listener, update the local attachment
		 * accordingly
		 */
		fileNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();

				// determine type by extension
				int index = fileNameText.getText().lastIndexOf(".");
				if (index > 0 && index < fileNameText.getText().length()) {
					String ext = fileNameText.getText().substring(index + 1);
					String type = extensions2Types.get(ext.toLowerCase(Locale.ENGLISH));
					if (type != null) {
						contentTypeList.select(contentTypeIndices.get(type));
						taskAttachment.setContentType(type);
					}
				}
			}
		});

		fileNameText.setText(taskAttachment.getFileName() == null ? "" : taskAttachment.getFileName()); //$NON-NLS-1$

		/* Listener for isPatch */
		isPatchButton.addSelectionListener(new SelectionListener() {
			private int lastSelected;

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

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
		if (fileNameText != null && "".equals(fileNameText.getText().trim())) {
			setErrorMessage("File name is empty");
			setPageComplete(false);
		} else if (descriptionText != null && "".equals(descriptionText.getText().trim())) {
			setErrorMessage("Description is empty");
			setPageComplete(false);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	public TaskAttachmentModel getModel() {
		return model;
	}

	@Override
	public IWizardPage getNextPage() {
		taskAttachment.applyTo(model.getAttribute());
		model.setComment(commentText.getText());
		model.setAttachContext(attachContextButton.getSelection());
		model.setContentType(taskAttachment.getContentType());
		return super.getNextPage();
	}

	private void setContentType(String contentType) {
		String[] typeList = contentTypeList.getItems();
		for (int i = 0; i < typeList.length; i++) {
			if (typeList[i].equals(contentType)) {
				contentTypeList.select(i);
				break;
			}
		}
	}

	private void setFilePath(String path) {
		fileNameText.setText(path);
		if (path.endsWith(".patch")) {
			isPatchButton.setSelection(true);
			if (attachContextButton.isEnabled()) {
				attachContextButton.setSelection(true);
			}
		}
	}

	public void setNeedsDescription(boolean supportsDescription) {
		this.needsDescription = supportsDescription;
		if (supportsDescription) {
			setMessage("Enter a description and verify the content type of the attachment");
		} else {
			setMessage("Verify the content type of the attachment");
		}
	}

	public boolean supportsDescription() {
		return needsDescription;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			if (fileNameText.getText().length() == 0) {
				setFilePath(getModel().getSource().getName());
			}
			setContentType(getModel().getSource().getContentType());
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
