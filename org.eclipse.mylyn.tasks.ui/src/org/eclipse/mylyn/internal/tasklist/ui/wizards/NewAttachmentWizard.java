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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasklist.LocalAttachment;

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
		/* TODO jpound - handle clipboard and workspace resources */
		attachPage.populateAttachment();
		//attachment.setFilePath(inputPage.getPatchName());
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
