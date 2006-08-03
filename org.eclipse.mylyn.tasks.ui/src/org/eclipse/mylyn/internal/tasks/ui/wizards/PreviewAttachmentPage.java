/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.wizards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.tasks.core.LocalAttachment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;

/**
 * Shows a preview of a LocalAttachment.
 * 
 * @author Jeff Pound
 */
public class PreviewAttachmentPage extends WizardPage {

	private static final String PAGE_NAME = "PreviewAttachmentPage";

	private static final String PAGE_DESCRIPTION = "Attachment preview";

	private LocalAttachment attachment;

	private static HashMap<String, String> textTypes;

	private static HashMap<String, String> imageTypes;

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

		// ("application/octet-stream", );
	}

	private static boolean isTextAttachment(String contentType) {
		return textTypes.get(contentType) != null;
	}

	private static boolean isImageAttachment(String contentType) {
		return imageTypes.get(contentType) != null;
	}

	protected PreviewAttachmentPage(LocalAttachment attachment) {
		super(PAGE_NAME);
		setDescription(PAGE_DESCRIPTION);
		this.attachment = attachment;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);

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
		final Image image = new Image(composite.getDisplay(), attachment.getFilePath());
		final Canvas canvas = new Canvas(composite, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Adapted from snippit 48
		// http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet48.java?rev=HEAD
		final Point origin = new Point(0, 0);
		final ScrollBar hBar = canvas.getHorizontalBar();
		hBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int hSelection = hBar.getSelection();
				int destX = -hSelection - origin.x;
				Rectangle rect = image.getBounds();
				canvas.scroll(destX, 0, 0, 0, rect.width, rect.height, false);
				origin.x = -hSelection;
			}
		});
		final ScrollBar vBar = canvas.getVerticalBar();
		vBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int vSelection = vBar.getSelection();
				int destY = -vSelection - origin.y;
				Rectangle rect = image.getBounds();
				canvas.scroll(0, destY, 0, 0, rect.width, rect.height, false);
				origin.y = -vSelection;
			}
		});
		canvas.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				Rectangle rect = image.getBounds();
				Rectangle client = canvas.getClientArea();
				hBar.setMaximum(rect.width);
				vBar.setMaximum(rect.height);
				hBar.setThumb(Math.min(rect.width, client.width));
				vBar.setThumb(Math.min(rect.height, client.height));
				int hPage = rect.width - client.width;
				int vPage = rect.height - client.height;
				int hSelection = hBar.getSelection();
				int vSelection = vBar.getSelection();
				if (hSelection >= hPage) {
					if (hPage <= 0)
						hSelection = 0;
					origin.x = -hSelection;
				}
				if (vSelection >= vPage) {
					if (vPage <= 0)
						vSelection = 0;
					origin.y = -vSelection;
				}
				canvas.redraw();
			}
		});
		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				GC gc = e.gc;
				gc.drawImage(image, origin.x, origin.y);
				Rectangle rect = image.getBounds();
				Rectangle client = canvas.getClientArea();
				int marginWidth = client.width - rect.width;
				if (marginWidth > 0) {
					gc.fillRectangle(rect.width, 0, marginWidth, client.height);
				}
				int marginHeight = client.height - rect.height;
				if (marginHeight > 0) {
					gc.fillRectangle(0, rect.height, client.width, marginHeight);
				}
			}
		});
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
