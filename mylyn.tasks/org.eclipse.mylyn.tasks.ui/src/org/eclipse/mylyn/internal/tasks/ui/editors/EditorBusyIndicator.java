/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * Provides a spinner animation for the tab title of an editor.
 * 
 * @author Shawn Minto
 * @author Steffen Pingel
 * @deprecated use {@link org.eclipse.mylyn.commons.workbench.BusyAnimator} instead
 */
@Deprecated
public class EditorBusyIndicator {

	private class Animator implements Runnable {

		int imageDataIndex = 0;

		private final Image[] images;

		private boolean stopped;

		public Animator(Image[] images) {
			this.images = images;
		}

		public void run() {
			if (stopped) {
				return;
			}

			try {
				Image image = images[imageDataIndex];
				imageDataIndex = (imageDataIndex + 1) % images.length;

				if (updateTitleImage(image)) {
					PlatformUI.getWorkbench().getDisplay().timerExec(DELAY, this);
				}
			} catch (Exception e) {
				WorkbenchPlugin.log(e);
			}
		}

		public void stop() {
			stopped = true;
		}
	}

	public static final int DELAY = 90;

	private Animator animator;

	private final IBusyEditor editor;

	private Image[] images;

	private Image oldImage;

	public EditorBusyIndicator(IBusyEditor editor) {
		this.editor = editor;
	}

	/**
	 * Starts the busy indication.
	 * 
	 * @see #stop()
	 */
	public void start() {
		if (animator != null) {
			stop();
		}

		try {
			if (images == null) {
				images = CommonImages.getProgressImages();
				// if image fails to load do not continue
				if (images == null) {
					return;
				}
			}

			oldImage = editor.getTitleImage();

			if (images.length > 1) {
				animator = new Animator(images);
				animator.run();
			}
		} catch (SWTException e) {
			WorkbenchPlugin.log(e);
		}
	}

	/**
	 * Stops the animation.
	 * 
	 * @see #start()
	 */
	public void stop() {
		if (animator != null) {
			animator.stop();
			animator = null;

			updateTitleImage(oldImage);
			oldImage = null;
		}
	}

	private boolean updateTitleImage(final Image image) {
		if (!PlatformUI.getWorkbench().isClosing()) {
			if (image != null && !image.isDisposed()) {
				editor.setTitleImage(image);
				return true;
			} else {
				if (oldImage != null && !oldImage.isDisposed()) {
					editor.setTitleImage(oldImage);
				}
			}
		}
		return false;
	}

}
