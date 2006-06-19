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

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasklist.LocalAttachment;
import org.eclipse.mylar.provisional.core.MylarPlugin;

public class NewAttachmentWizard extends Wizard {

	private LocalAttachment attachment;
	private InputAttachmentSourcePage inputPage; 
	private NewAttachmentPage attachPage;
	
	public NewAttachmentWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("Add a new attachment");
		attachment = new LocalAttachment();
	}
	
	@Override
	public boolean performFinish() {
		/* TODO jpound - support non-text in clipboard */
		attachPage.populateAttachment();
		String path = inputPage.getAbsoluteAttachmentPath();
		if (InputAttachmentSourcePage.CLIPBOARD_LABEL.equals(path)) {
			// write temporary file
			String contents = inputPage.getClipboardContents();
			if (contents == null) {
				// TODO Handle error
			}
			
			File file = new File(MylarPlugin.getDefault().getDefaultDataDirectory() + System.getProperty("file.separator").charAt(0) + "Clipboard-attachment");
			try {
				FileWriter writer = new FileWriter(file);	
				writer.write(contents);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Handle error
			}
			path = file.getAbsolutePath();
			attachment.setDeleteAfterUpload(true);
		}
		attachment.setFilePath(path);
		return true;
	}
	
	@Override
	public boolean canFinish() {
		return attachPage.isPageComplete();
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage((inputPage = new InputAttachmentSourcePage(this)));
		addPage((attachPage = new NewAttachmentPage(attachment)));
	}

	public LocalAttachment getAttachment() {
		return attachment;
	}

	protected String getFilePath() {
		return inputPage.getAttachmentName();
	}

	public IWizardPage getNextPage(IWizardPage page) {
		attachPage.setFilePath(inputPage.getAttachmentName());
		return super.getNextPage(page);
	}
}
