/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.commons.ui.CommonsUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Provides a spinner animation for the tab title of an editor.
 * 
 * @author Shawn Minto
 * @author Steffen Pingel
 */
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
					PlatformUI.getWorkbench().getDisplay().timerExec(UPDATE_INTERVAL, this);
				}
			} catch (Exception e) {
				CommonsUiPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN, "Failed to update animation", e)); //$NON-NLS-1$
			}
		}

		public void stop() {
			stopped = true;
		}
	}

	private static final int UPDATE_INTERVAL = 90;

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
			CommonsUiPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN, "Failed to start animation", e)); //$NON-NLS-1$
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
