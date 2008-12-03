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

package org.eclipse.mylyn.internal.commons.ui;

import java.lang.reflect.Method;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class SwtUtil {

	public static final long FADE_RESCHEDULE_DELAY = 80;

	public static final int FADE_IN_INCREMENT = 15;

	public static final int FADE_OUT_INCREMENT = -20;

	public static void collectItemData(TreeItem[] items, Set<Object> allVisible) {
		for (TreeItem item : items) {
			allVisible.add(item.getData());
			collectItemData(item.getItems(), allVisible);
		}
	}

	// TODO e3.4 get rid of reflection on 3.4 branch
	public static boolean setAlpha(Shell shell, int value) {
		Method method = null;
		try {
			method = shell.getClass().getMethod("setAlpha", new Class[] { int.class }); //$NON-NLS-1$
			method.setAccessible(true);
			//shell.setAlpha(value);
			method.invoke(shell, new Object[] { value });
			return true;
		} catch (Exception e) {
			// ignore, not supported on Eclipse 3.3
			return false;
		}
	}

	// TODO e3.4 get rid of reflection on 3.4 branch
	public static int getAlpha(Shell shell) {
		Method method = null;
		try {
			method = shell.getClass().getMethod("getAlpha"); //$NON-NLS-1$
			method.setAccessible(true);
			return (Integer) method.invoke(shell);
		} catch (Exception e) {
			return 0xFF;
		}
	}

	public static FadeJob fastFadeIn(Shell shell, IFadeListener listener) {
		return new FadeJob(shell, 2 * FADE_IN_INCREMENT, FADE_RESCHEDULE_DELAY, listener);
	}

	public static FadeJob fadeIn(Shell shell, IFadeListener listener) {
		return new FadeJob(shell, FADE_IN_INCREMENT, FADE_RESCHEDULE_DELAY, listener);
	}

	public static FadeJob fadeOut(Shell shell, IFadeListener listener) {
		return new FadeJob(shell, FADE_OUT_INCREMENT, FADE_RESCHEDULE_DELAY, listener);
	}

	// TODO e3.4 get rid of reflection on 3.4 branch
	public static void fade(Shell shell, boolean fadeIn, int increment, int speed) {
		try {
			Method method = shell.getClass().getMethod("setAlpha", new Class[] { int.class }); //$NON-NLS-1$
			method.setAccessible(true);

			if (fadeIn) {
				for (int i = 0; i <= 255; i += increment) {
					// shell.setAlpha(i);
					method.invoke(shell, new Object[] { i });
					try {
						Thread.sleep(speed);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				// shell.setAlpha(255);
				method.invoke(shell, new Object[] { 255 });
			} else {
				for (int i = 244; i >= 0; i -= increment) {
					// shell.setAlpha(i);
					method.invoke(shell, new Object[] { i });
					try {
						Thread.sleep(speed);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				// shell.setAlpha(0);
				method.invoke(shell, new Object[] { 0 });
			}
		} catch (Exception e) {
			// ignore, not supported on Eclipse 3.3
		}
	}

	public static class FadeJob extends Job {

		private final Shell shell;

		private final int increment;

		private volatile boolean stopped;

		private volatile int currentAlpha;

		private final long delay;

		private final IFadeListener fadeListener;

		public FadeJob(Shell shell, int increment, long delay, IFadeListener fadeListener) {
			super(Messages.SwtUtil_Fading);
			if (increment < -255 || increment == 0 || increment > 255) {
				throw new IllegalArgumentException("-255 <= increment <= 255 && increment != 0"); //$NON-NLS-1$
			}
			if (delay < 1) {
				throw new IllegalArgumentException("delay must be > 0"); //$NON-NLS-1$
			}
			this.currentAlpha = getAlpha(shell);
			this.shell = shell;
			this.increment = increment;
			this.delay = delay;
			this.fadeListener = fadeListener;

			setSystem(true);
			schedule(delay);
		}

		@Override
		protected void canceling() {
			stopped = true;
		}

		private void reschedule() {
			if (stopped) {
				return;
			}
			schedule(delay);
		}

		public void cancelAndWait(final boolean setAlpha) {
			if (stopped) {
				return;
			}
			cancel();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (setAlpha) {
						SwtUtil.setAlpha(shell, getLastAlpha());
					}
				}
			});
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (stopped) {
				return Status.OK_STATUS;
			}

			currentAlpha += increment;
			if (currentAlpha <= 0) {
				currentAlpha = 0;
			} else if (currentAlpha >= 255) {
				currentAlpha = 255;
			}

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (stopped) {
						return;
					}

					if (shell.isDisposed()) {
						stopped = true;
						return;
					}

					if (!SwtUtil.setAlpha(shell, currentAlpha)) {
						// just in case it failed for some other reason than lack of support on the platform
						currentAlpha = getLastAlpha();
						SwtUtil.setAlpha(shell, currentAlpha);
						stopped = true;
					}

					if (fadeListener != null) {
						fadeListener.faded(shell, currentAlpha);
					}
				}
			});

			if (currentAlpha == 0 || currentAlpha == 255) {
				stopped = true;
			}

			reschedule();
			return Status.OK_STATUS;
		}

		private int getLastAlpha() {
			return (increment < 0) ? 0 : 255;
		}

	}

	public static interface IFadeListener {

		public void faded(Shell shell, int alpha);

	}

}
