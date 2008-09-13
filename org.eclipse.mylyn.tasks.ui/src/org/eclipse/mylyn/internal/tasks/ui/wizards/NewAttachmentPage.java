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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.LocalAttachment;
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
 */
/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class NewAttachmentPage extends WizardPage {

	private final LocalAttachment attachment;

	private Text filePath;

	private Text attachmentDesc;

	private Text attachmentComment;

	private Button isPatchButton;

	private Button attachContextButton;

	private Combo contentTypeList;

	private boolean supportsDescription = true;

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

	protected NewAttachmentPage(LocalAttachment att) {
		super("AttachmentDetails");
		setTitle("Attachment Details");
		setMessage("Enter a description and verify the content type of the attachment");
		attachment = att;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		setControl(composite);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(3, false));

		final NewAttachmentPage thisPage = this;

		new Label(composite, SWT.NONE).setText("File");
		filePath = new Text(composite, SWT.BORDER);
		filePath.setEditable(false);
		filePath.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		if (supportsDescription) {
			new Label(composite, SWT.NONE).setText("Description");
			attachmentDesc = new Text(composite, SWT.BORDER);
			attachmentDesc.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

			attachmentDesc.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if ("".equals(attachmentDesc.getText().trim())) {
						thisPage.setErrorMessage("Description required");
					} else {
						if (!"".equals(filePath.getText())) {
							thisPage.setPageComplete(true);
							thisPage.setErrorMessage(null);
						}
					}
				}

			});
		}

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		label.setText("Comment");
		attachmentComment = new Text(composite, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
		attachmentComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

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
				attachment.setContentType(contentTypeList.getItem(contentTypeList.getSelectionIndex()));
			}
		});
		contentTypeList.select(0);
		attachment.setContentType(contentTypeList.getItem(0));

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
		attachContextButton.setEnabled((((NewAttachmentWizard) getWizard()).hasContext()));

		/*
		 * Attachment file name listener, update the local attachment
		 * accordingly
		 */
		filePath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// Determine type by extension
				int index = filePath.getText().lastIndexOf(".");
				if (index > 0 && index < filePath.getText().length()) {
					String ext = filePath.getText().substring(index + 1);
					String type = extensions2Types.get(ext.toLowerCase(Locale.ENGLISH));
					if (type != null) {
						contentTypeList.select(contentTypeIndices.get(type));
						attachment.setContentType(type);
					}
				}

				// check page completenes
				if (attachmentDesc != null && "".equals(attachmentDesc.getText())) {
					thisPage.setErrorMessage("Description required");
				} else {
					if (!"".equals(filePath.getText())) {
						thisPage.setPageComplete(true);
						thisPage.setErrorMessage(null);
					}
				}
			}
		});

		filePath.setText(attachment.getFilePath() == null ? "" : attachment.getFilePath()); //$NON-NLS-1$

		/* Listener for isPatch */
		isPatchButton.addSelectionListener(new SelectionListener() {
			private int lastSelected;

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				attachment.setPatch(isPatchButton.getSelection());
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

		thisPage.setErrorMessage(null);
	}

	@Override
	public boolean isPageComplete() {
		return !"".equals(filePath.getText().trim())
				&& (attachmentDesc == null || !"".equals(attachmentDesc.getText().trim()));
	}

	public void populateAttachment() {
		if (attachmentDesc != null) {
			attachment.setDescription(attachmentDesc.getText());
		}
		attachment.setComment(attachmentComment.getText());
	}

	public LocalAttachment getAttachment() {
		return attachment;
	}

	public void setFilePath(String path) {
		filePath.setText(path);
		if (path.endsWith(".patch")) {
			isPatchButton.setSelection(true);
			if (attachContextButton.isEnabled()) {
				attachContextButton.setSelection(true);
			}
		}
	}

	@Override
	public IWizardPage getNextPage() {
		populateAttachment();
		PreviewAttachmentPage page = new PreviewAttachmentPage(getAttachment());
		page.setWizard(getWizard());
		return page;
	}

	public boolean getAttachContext() {
		return attachContextButton.getSelection();
	}

	public void setContentType() {
		String type = attachment.getContentType();
		String[] typeList = contentTypeList.getItems();
		for (int i = 0; i < typeList.length; i++) {
			if (typeList[i].equals(type)) {
				contentTypeList.select(i);
				contentTypeList.setEnabled(false);
				isPatchButton.setEnabled(false);
				return;
			}
		}
	}

	public boolean supportsDescription() {
		return supportsDescription;
	}

	public void setSupportsDescription(boolean supportsDescription) {
		this.supportsDescription = supportsDescription;
	}

}
