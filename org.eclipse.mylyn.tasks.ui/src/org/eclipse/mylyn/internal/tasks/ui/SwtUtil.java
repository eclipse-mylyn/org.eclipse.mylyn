/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class SwtUtil {

	public static final long FADE_RESCHEDULE_DELAY = 80;

	public static final int FADE_IN_INCREMENT = 15;

	public static final int FADE_OUT_INCREMENT = 20;

	/**
	 * API-3.0: get rid of reflection on 3.4 branch
	 * 
	 * @return
	 */
	public static boolean setAlpha(Shell shell, int value) {
		Method method = null;
		try {
			method = shell.getClass().getMethod("setAlpha", new Class[] { int.class });
			method.setAccessible(true);
			//shell.setAlpha(value);
			method.invoke(shell, new Object[] { value });
			return true;
		} catch (Exception e) {
			// ignore, not supported on Eclipse 3.3
			return false;
		}
	}

	public static FadeJob fadeIn(Shell shell) {
		return new FadeJob(shell, 0, FADE_IN_INCREMENT, FADE_RESCHEDULE_DELAY);		
	}

	public static FadeJob fadeOut(Shell shell) {
		return new FadeJob(shell, 0, FADE_OUT_INCREMENT, FADE_RESCHEDULE_DELAY);		
	}

	/**
	 * API-3.0: get rid of reflection on 3.4 branch
	 */
	public static void fade(Shell shell, boolean fadeIn, int increment, int speed) {
		try {
			Method method = shell.getClass().getMethod("setAlpha", new Class[] { int.class });
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

		public FadeJob(Shell shell, int initialAlpha, int increment, long delay) {
			super("Fading");
			if (increment < -255 || increment == 0 || increment > 255) {
				throw new IllegalArgumentException("-255 <= increment <= 255 && increment != 0");
			}
			if (initialAlpha < 0 || initialAlpha > 255) {
				throw new IllegalArgumentException("0 <= initialAlpha <= 255");
			}
			if (delay < 1) {
				throw new IllegalArgumentException("delay must be > 0");
			}
			this.currentAlpha = initialAlpha;
			this.shell = shell;
			this.increment = increment;
			this.delay = delay;

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

		public void cancelAndWait() {
			if (stopped) {
				return;
			}
			cancel();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					SwtUtil.setAlpha(shell, getLastAlpha());
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
				stopped = true;
			} else if (currentAlpha >= 255) {
				currentAlpha = 255;
				stopped = true;
			}

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (stopped) { 
						return;
					}
					
					if (!SwtUtil.setAlpha(shell, currentAlpha)) {
						// just in case it failed for some other reason than lack of support on the platform
						SwtUtil.setAlpha(shell, getLastAlpha());
						stopped = true;
					}
				}				
			});

			reschedule();
			return Status.OK_STATUS;
		}

		private int getLastAlpha() {
			return (increment < 0) ? 0 : 255;
		}

	}

}
