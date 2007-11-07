/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Shell;

/**
 * @author Mik Kersten
 */
public class SwtUtil {

	/**
	 * API-3.0: get rid of reflection on 3.4 branch
	 */
	public static void setAlpha(Shell shell, int value) {
		Method method = null;
		try {
			method = shell.getClass().getMethod("setAlpha", new Class[] { int.class });
			method.setAccessible(true);
			//shell.setAlpha(value);
			method.invoke(shell, new Object[] { value });
		} catch (Exception e) {
			// ignore, not supported on Eclipse 3.3
		}
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

}
