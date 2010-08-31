/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balazs Brinkus - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Willian Mitsuda - improvements
 *     Hiroyuki Inaba - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.ui.screenshots.ScreenshotViewer;
import org.eclipse.mylyn.internal.commons.ui.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * A wizard page to create a screenshot from the display.
 * <p>
 * NOTE: this class exposes a lot of implementation detail and is likely to change.
 * 
 * @author Balazs Brinkus
 * @author Willian Mitsuda
 * @author Mik Kersten
 * @author Hiroyuki Inaba
 */
public class ScreenshotCreationPage extends WizardPage {

	private ScreenshotViewer viewer;

	public ScreenshotCreationPage() {
		super("ScreenShotAttachment"); //$NON-NLS-1$
		setTitle(Messages.ScreenshotCreationPage_CAPTURE_SCRRENSHOT);
		setDescription(Messages.ScreenshotCreationPage_After_capturing
				+ Messages.ScreenshotCreationPage_NOTE_THAT_YOU_CONTINUTE);
	}

	public void createControl(Composite parent) {
		viewer = new ScreenshotViewer(parent, SWT.BORDER | SWT.FLAT) {
			@Override
			protected void stateChanged() {
				getContainer().updateButtons();
			}
		};
		setControl(viewer.getControl());
	}

	@Override
	public boolean isPageComplete() {
		return (viewer != null) && viewer.isComplete();
	}

	public boolean isImageDirty() {
		return viewer.isDirty();
	}

	public Image createImage() {
		// NOTE: may get invoked from non UI thread
		return viewer.createImage();
	}

	public void setImageDirty(boolean imageDirty) {
		viewer.setDirty(imageDirty);
	}

}
