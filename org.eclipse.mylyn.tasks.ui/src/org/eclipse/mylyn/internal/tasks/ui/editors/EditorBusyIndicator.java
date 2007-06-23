package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * @author Shawn Minto
 */
public class EditorBusyIndicator {

	private boolean showingBusy = false;

	private Image[] images;

	private TimerTask animateTask;

	private IBusyEditor editor;

	private Image oldImage;

	public EditorBusyIndicator(IBusyEditor editor) {
		this.editor = editor;
	}

	/**
	 * Stop showing the busy cursor.
	 */
	public void stopBusy() {

		if (!showingBusy || isDisposed())
			return;

		showingBusy = false;
		if (animateTask != null) {
			animateTask.cancel();
			animateTask = null;
		}

		updateTitleImage(oldImage);
	}

	/**
	 * Start the busy indication.
	 */
	public void startBusy() {

		if (showingBusy || isDisposed())
			return;

		showingBusy = true;

		oldImage = editor.getTitleImage();

		try {
			if (images == null) {
				images = TasksUiImages.getProgressImages();// If we fail to
															// load do not
															// continue
				if (images == null) {
					showingBusy = false;
					return;
				}
			}

			if (images.length > 1) {

				Timer animateTimer = new Timer();
				if (animateTask == null)
					animateTimer.schedule(getTimerTask(), 0);
			}
		} catch (SWTException ex) {
			WorkbenchPlugin.log(ex);
		}
	}

	/**
	 * Get the timer task for the receiver.
	 * 
	 * @param background
	 * @param display
	 * @return TimerTask
	 */
	private TimerTask getTimerTask() {

		animateTask = new TimerTask() {

			public void run() {

				try {
					int imageDataIndex = 0;

					Image image = images[imageDataIndex];

					while (showingBusy) {

						updateTitleImage(image);

						imageDataIndex = (imageDataIndex + 1) % images.length;
						image = images[imageDataIndex];

						try {
							Thread.sleep(90);
						} catch (InterruptedException e) {
						}

						if (images == null)
							return;
					}
				} catch (SWTException ex) {
					WorkbenchPlugin.log(ex);
				} catch (NullPointerException e) {
					// we will get this if the timer continues to run after the
					// images have been disposed
				} finally {

				}
			}

		};

		return animateTask;
	}

	private void updateTitleImage(final Image image) {
		if (PlatformUI.getWorkbench().isClosing()) {
			return;
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					if ((image == null || !image.isDisposed()) && !isDisposed() && showingBusy) {
						editor.setTitleImage(image);
					} else {
						if (oldImage != null && !oldImage.isDisposed()) {
							editor.setTitleImage(oldImage);
						}
					}

				}
			});
		}
	}

	private boolean isDisposed = false;

	public void dispose() {
		if (!isDisposed) {
			if (animateTask != null)
				animateTask.cancel();
			// if (images != null) {
			// for (int i = 0; i < images.length; i++) {
			// images[i].dispose();
			// }
			// }
			// images = null;
			isDisposed = true;
		}
	}

	public boolean isDisposed() {
		return isDisposed;
	}

}
