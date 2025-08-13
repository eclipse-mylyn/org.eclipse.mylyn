/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.commons.workbench.CommonsWorkbenchPlugin;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * Provides a spinner animation for the tab title of an editor.
 *
 * @author Shawn Minto
 * @author Steffen Pingel
 * @since 3.7
 */
public class BusyAnimator {

	/**
	 * A callback for modifying the title image of an editor. Clients that provide busy animations should implement this interface and
	 * delegate to the respective methods in {@link WorkbenchPart}.
	 *
	 * @author Shawn Minto
	 * @see BusyAnimator
	 * @since 3.7
	 */
	public interface IBusyClient {

		/**
		 * Updates the title image of the editor to <code>image</code>.
		 *
		 * @param image
		 *            the image
		 */
		void setImage(Image image);

		/**
		 * Returns the current title image of the editor.
		 */
		Image getImage();

	}

	private class Animator implements Runnable {

		int imageDataIndex = 0;

		private final Image[] images;

		private boolean stopped;

		public Animator(Image[] images) {
			this.images = images;
		}

		@Override
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
				CommonsWorkbenchPlugin.getDefault()
						.getLog()
						.log(new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN, "Failed to update animation", //$NON-NLS-1$
								e));
			}
		}

		public void stop() {
			stopped = true;
		}
	}

	private static final int UPDATE_INTERVAL = 90;

	private Animator animator;

	private final IBusyClient client;

	private Image[] images;

	private Image oldImage;

	public BusyAnimator(IBusyClient client) {
		this.client = client;
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

			oldImage = client.getImage();

			if (images.length > 1) {
				animator = new Animator(images);
				animator.run();
			}
		} catch (SWTException e) {
			CommonsWorkbenchPlugin.getDefault()
					.getLog()
					.log(new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN, "Failed to start animation", e)); //$NON-NLS-1$
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
				client.setImage(image);
				return true;
			} else if (oldImage != null && !oldImage.isDisposed()) {
				client.setImage(oldImage);
			}
		}
		return false;
	}

}
