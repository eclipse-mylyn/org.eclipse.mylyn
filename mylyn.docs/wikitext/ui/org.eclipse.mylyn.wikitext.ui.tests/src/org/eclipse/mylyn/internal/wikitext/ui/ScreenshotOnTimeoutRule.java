/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.mylyn.wikitext.toolkit.TimeoutActionRule;
import org.eclipse.mylyn.wikitext.util.WikiToStringStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings({ "nls", "restriction" })
public class ScreenshotOnTimeoutRule extends TimeoutActionRule {

	private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(1L);

	public ScreenshotOnTimeoutRule() {
		super(DEFAULT_TIMEOUT);
	}

	@Override
	protected void performAction() {
		Display.getDefault().asyncExec(this::createScreenshot);
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
			final Image image = new Image(display, display.getBounds());
			shell.print(gc);

			writeScreenshot(image, format("shell-{0}", index));

			image.dispose();
			gc.dispose();

			dumpControlHierarchy(0, List.of(shell));
		}
	}

	private void dumpControlHierarchy(int level, List<Control> controls) {
		for (Control control : controls) {
			String indent = "  ".repeat(level);
			System.out.println(indent + description(control));
			if (control instanceof Composite comp) {
				dumpControlHierarchy(level + 1, Arrays.asList(comp.getChildren()));
			}
		}
	}

	private String description(Control control) {
		ToStringBuilder builder = new ToStringBuilder(this, WikiToStringStyle.WIKI_TO_STRING_STYLE);
		if (control instanceof StyledText styled) {
			builder.append("text", styled.getText());
		}
		if (control instanceof Text text) {
			builder.append("text", text.getText());
		}
		if (control instanceof Button b) {
			builder.append("text", b.getText());
		}
		if (control instanceof Label l) {
			builder.append("text", l.getText());
		}
		return builder.append("tooltip", control.getToolTipText()).toString();
	}
}
