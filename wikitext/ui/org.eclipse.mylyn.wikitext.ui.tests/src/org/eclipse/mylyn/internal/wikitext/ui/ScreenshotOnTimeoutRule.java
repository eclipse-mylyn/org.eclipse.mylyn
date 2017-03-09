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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;

import org.eclipse.mylyn.wikitext.toolkit.TimeoutActionRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

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

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };

		try {
			File screenshotFile = File.createTempFile("screenshot", ".png", new File("target"));
			try (OutputStream stream = new FileOutputStream(screenshotFile)) {
				loader.save(stream, SWT.IMAGE_PNG);
			}
			System.out.println("Saved screeenshot to " + screenshotFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		image.dispose();
		gc.dispose();
	}
}
