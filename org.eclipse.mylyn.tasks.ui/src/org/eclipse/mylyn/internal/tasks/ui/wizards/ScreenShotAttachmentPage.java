/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.LocalAttachment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A wizard page to create a screenshot from the display.
 * 
 * @author Balazs Brinkus (bug 160572)
 */
public class ScreenShotAttachmentPage extends WizardPage {

	private ScreenShotAttachmentPage page;

	private LocalAttachment attachment;

	private Button makeShotButton;

	private Button showShotButton;

	private Image screenshotImage;

	private Canvas canvas;

	protected ScreenShotAttachmentPage(LocalAttachment attachment) {
		super("ScreenShotAttachment");
		setTitle("Create a screenshot");
		this.attachment = attachment;
		this.page = this;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		setControl(composite);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(3, false));

		makeShotButton = new Button(composite, SWT.PUSH);
		makeShotButton.setText("Take a screenshot");
		makeShotButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				storeScreenshotContent();
				page.setErrorMessage(null);
				showShotButton.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		showShotButton = new Button(composite, SWT.PUSH);
		showShotButton.setText("Show the screenshot");
		showShotButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				showScreenshotContent();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		canvas = new Canvas(composite, SWT.BORDER);
		canvas.setLayoutData(GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, true)
				.span(2, 1)
				.create());

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (screenshotImage != null) {
					Rectangle screenBounds = screenshotImage.getBounds();
					Rectangle canvasBounds = canvas.getBounds();
					e.gc.drawImage(screenshotImage, 0, 0, screenBounds.width, screenBounds.height, 0, 0,
							canvasBounds.width, canvasBounds.height);
				} else {
					page.setErrorMessage("Screenshot required");
					showShotButton.setEnabled(false);
				}
			}
		});

	}

	@Override
	public boolean isPageComplete() {
		if (screenshotImage == null)
			return false;
		return true;
	}

	@Override
	public IWizardPage getNextPage() {
		NewAttachmentPage page = (NewAttachmentPage) getWizard().getPage("AttachmentDetails");
		attachment.setContentType("image/jpeg");
		page.setFilePath(InputAttachmentSourcePage.SCREENSHOT_LABEL);
		page.setContentType();
		return page;
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	private void storeScreenshotContent() {

		final Display display = Display.getDefault();
		final Shell wizardShell = getWizard().getContainer().getShell();
		wizardShell.setVisible(false);

		display.asyncExec(new Runnable() {
			public void run() {
				GC gc = new GC(display);
				screenshotImage = new Image(display, display.getBounds());
				gc.copyArea(screenshotImage, 0, 0);
				gc.dispose();
				canvas.redraw();
				wizardShell.setVisible(true);
				if (screenshotImage != null)
					setPageComplete(true);
			}
		});
	}

	private void showScreenshotContent() {
		Display display = Display.getDefault();

		Shell popup = new Shell(display.getActiveShell(), SWT.SHELL_TRIM);
		popup.setLayout(new FillLayout());
		popup.setText("Screenshot Image");

		Rectangle displayBounds = Display.getDefault().getBounds();
		Point dialogSize = new Point(0, 0);
		dialogSize.x = displayBounds.width / 2;
		dialogSize.y = displayBounds.height / 2;
		Point dialoglocation = new Point(0, 0);
		dialoglocation.x = displayBounds.x + displayBounds.width / 2 - dialogSize.x / 2;
		dialoglocation.y = displayBounds.y + displayBounds.height / 2 - dialogSize.y / 2;
		popup.setSize(dialogSize);
		popup.setLocation(dialoglocation);

		ScrolledComposite sc = new ScrolledComposite(popup, SWT.V_SCROLL | SWT.H_SCROLL);
		Canvas canvas = new Canvas(sc, SWT.NONE);
		sc.setContent(canvas);
		canvas.setBounds(display.getBounds());
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (screenshotImage != null)
					e.gc.drawImage(screenshotImage, 0, 0);
			}
		});
		popup.open();
	}

	public Image getScreenshotImage() {
		return screenshotImage;
	}

}
