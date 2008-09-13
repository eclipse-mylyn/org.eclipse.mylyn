/*******************************************************************************
 * Copyright (c) 2004, 2008 Jeff Pound and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.LocalAttachment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;

/**
 * Shows a preview of a LocalAttachment.
 * 
 * @author Jeff Pound
 */
/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class PreviewAttachmentPage extends WizardPage {

	private static final String PAGE_NAME = "PreviewAttachmentPage";

	private static final String TITLE = "Attachment Preview";

	private static final String DESCRIPTION = "Review the attachment before submitting";

	private final LocalAttachment attachment;

	private static HashMap<String, String> textTypes;

	private static HashMap<String, String> imageTypes;

	private ScrolledComposite scrolledComposite;

	static {
		textTypes = new HashMap<String, String>();
		imageTypes = new HashMap<String, String>();

		textTypes.put("text/plain", "");
		textTypes.put("text/html", "");
		textTypes.put("text/html", "");
		textTypes.put("application/xml", "");

		imageTypes.put("image/jpeg", "");
		imageTypes.put("image/gif", "");
		imageTypes.put("image/png", "");
	}

	private static boolean isTextAttachment(String contentType) {
		return textTypes.get(contentType) != null;
	}

	private static boolean isImageAttachment(String contentType) {
		return imageTypes.get(contentType) != null;
	}

	protected PreviewAttachmentPage(LocalAttachment attachment) {
		super(PAGE_NAME);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		this.attachment = attachment;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);

		if (attachment instanceof ImageAttachment) {
			try {
				getContainer().run(true, false, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask("Preparing image for preview...", 1);
						((ImageAttachment) attachment).ensureImageFileWasCreated();
						monitor.worked(1);
						monitor.done();
					}
				});
			} catch (InvocationTargetException e) {
				createErrorPreview(composite, "Could not create image for preview");
				return;
			} catch (InterruptedException e) {
				createErrorPreview(composite, "Could not create image for preview");
				return;
			}
		}

		if (InputAttachmentSourcePage.CLIPBOARD_LABEL.equals(attachment.getFilePath())) {
			createTextPreview(composite, ((NewAttachmentWizard) getWizard()).getClipboardContents());
		} else if (PreviewAttachmentPage.isTextAttachment(attachment.getContentType())) {
			createTextPreview(composite, attachment);
		} else if (PreviewAttachmentPage.isImageAttachment(attachment.getContentType())) {
			createImagePreview(composite, attachment);
		} else {
			createGenericPreview(composite, attachment);
		}
	}

	private void createTextPreview(Composite composite, String contents) {
		Text text = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = composite.getBounds().y;
		gd.widthHint = composite.getBounds().x;
		text.setLayoutData(gd);
		text.setText(contents);
	}

	private void createTextPreview(Composite composite, LocalAttachment attachment) {
		try {
			StringBuffer content = new StringBuffer();
			BufferedReader in = new BufferedReader(new FileReader(new File(attachment.getFilePath())));
			String line;
			while ((line = in.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
			in.close();
			createTextPreview(composite, content.toString());

		} catch (FileNotFoundException e) {
			createErrorPreview(composite, "Could not locate file '" + attachment.getFilePath() + "'");
		} catch (IOException e) {
			createErrorPreview(composite, "Error reading file '" + attachment.getFilePath() + "'");
		}
	}

	private void createImagePreview(Composite composite, LocalAttachment attachment) {
		// Uses double buffering to paint the image; there was a weird behavior
		// with transparent images and flicker with large images
		Image originalImage = new Image(composite.getDisplay(), attachment.getFilePath());
		final Image bufferedImage = new Image(composite.getDisplay(), originalImage.getBounds());
		GC gc = new GC(bufferedImage);
		gc.setBackground(composite.getBackground());
		gc.fillRectangle(originalImage.getBounds());
		gc.drawImage(originalImage, 0, 0);
		gc.dispose();
		originalImage.dispose();

		scrolledComposite = new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite canvasComposite = new Composite(scrolledComposite, SWT.NONE);
		canvasComposite.setLayout(GridLayoutFactory.fillDefaults().create());
		Canvas canvas = new Canvas(canvasComposite, SWT.NO_BACKGROUND);
		final Rectangle imgSize = bufferedImage.getBounds();
		canvas.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).hint(
				imgSize.width, imgSize.height).create());
		canvas.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				e.gc.drawImage(bufferedImage, 0, 0);
			}

		});
		canvas.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				bufferedImage.dispose();
			}

		});
		canvas.setSize(imgSize.width, imgSize.height);
		scrolledComposite.setMinSize(imgSize.width, imgSize.height);
		scrolledComposite.setContent(canvasComposite);
		scrolledComposite.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				adjustScrollbars(imgSize);
			}

		});
		adjustScrollbars(imgSize);
	}

	private void adjustScrollbars(Rectangle imgSize) {
		Rectangle clientArea = scrolledComposite.getClientArea();

		ScrollBar hBar = scrolledComposite.getHorizontalBar();
		hBar.setMinimum(0);
		hBar.setMaximum(imgSize.width - 1);
		hBar.setPageIncrement(clientArea.width);
		hBar.setIncrement(10);

		ScrollBar vBar = scrolledComposite.getVerticalBar();
		vBar.setMinimum(0);
		vBar.setMaximum(imgSize.height - 1);
		vBar.setPageIncrement(clientArea.height);
		vBar.setIncrement(10);
	}

	private void createGenericPreview(Composite composite, LocalAttachment attachment) {
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_BOTH));
		label.setText("Attaching File '" + attachment.getFilePath() + "'\nA preview the type '"
				+ attachment.getContentType() + "' is currently not available");
	}

	private void createErrorPreview(Composite composite, String message) {
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_BOTH));
		label.setText(message);
	}

}
