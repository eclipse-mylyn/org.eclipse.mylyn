/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.internal.provisional.commons.ui.ScreenshotCreationPage;

/**
 * @author Mik Kersten
 */
public class ScreenshotAttachmentPage extends ScreenshotCreationPage {

	@Override
	public void setImageDirty(boolean pageDirty) {
		super.setImageDirty(pageDirty);
		if (pageDirty) {
			// API 3.0 remove
			if (getWizard() instanceof NewAttachmentWizard) {
				NewAttachmentWizard wizard = (NewAttachmentWizard) getWizard();
				((ImageAttachment) wizard.getAttachment()).markDirty();
			}
		}
	}

	@Override
	public IWizardPage getNextPage() {
		NewAttachmentPage page = (NewAttachmentPage) getWizard().getPage("AttachmentDetails");
		page.setFilePath(InputAttachmentSourcePage.SCREENSHOT_LABEL);
		page.setContentType();
		return page;
	}

}
