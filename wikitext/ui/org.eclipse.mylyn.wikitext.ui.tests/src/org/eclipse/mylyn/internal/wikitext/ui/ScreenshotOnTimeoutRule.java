/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.Duration;

import org.eclipse.mylyn.wikitext.toolkit.TimeoutActionRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ScreenshotOnTimeoutRule extends TimeoutActionRule {

	private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(1L);

	public ScreenshotOnTimeoutRule() {
		super(DEFAULT_TIMEOUT);
	}

	@Override
	protected void performAction() {
		Display.getDefault().asyncExec(() -> createScreenshot());
	}

	private void createScreenshot() {
		Display display = Display.getCurrent();
		if (display == null || display.isDisposed()) {
			return;
		}
		GC gc = new GC(display);
		final Image image = new Image(display, display.getBounds());
		gc.copyArea(image, 0, 0);

		writeScreenshot(image, "display");

		image.dispose();
		gc.dispose();

		screenshotShells(display);
	}

	private void writeScreenshot(final Image image, String screenshotName) {
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };
		try {
			File screenshotFile = createFile(screenshotName);

			try (OutputStream stream = new FileOutputStream(screenshotFile)) {
				loader.save(stream, SWT.IMAGE_PNG);
			}
			System.out.println("Saved screeenshot to " + screenshotFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File createFile(String name) throws IOException {
		return File.createTempFile(MessageFormat.format("screenshot-{0}-", name), ".png", new File("target"));
	}

	private void screenshotShells(Display display) {
		int index = -1;
		for (Shell shell : display.getShells()) {
			++index;

			GC gc = new GC(shell);
			final Image image = new Image(display, shell.getBounds());
			gc.copyArea(image, 0, 0);

			writeScreenshot(image, format("shell-{0}", index));

			image.dispose();
			gc.dispose();
		}
	}
}
