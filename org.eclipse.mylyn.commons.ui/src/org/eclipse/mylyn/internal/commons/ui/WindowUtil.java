/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     IBM Corporation - initial API and implementation
 *     Frank Becker - fixes for Mylyn
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.jface.util.Geometry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;

/**
 * Based on {@link org.eclipse.jface.window.Window#getConstrainedShellBounds(Rectangle)}.
 * 
 * @author Frank Becker
 */
public class WindowUtil {

	/**
	 * Returns the monitor whose client area contains the given point. If no monitor contains the point, returns the
	 * monitor that is closest to the point. If this is ever made public, it should be moved into a separate utility
	 * class.
	 * 
	 * @param toSearch
	 *            point to find (display coordinates)
	 * @param toFind
	 *            point to find (display coordinates)
	 * @return the montor closest to the given point
	 */
	private static Monitor getClosestMonitor(Display toSearch, Point toFind) {
		int closest = Integer.MAX_VALUE;

		Monitor[] monitors = toSearch.getMonitors();
		Monitor result = monitors[0];

		for (Monitor current : monitors) {
			Rectangle clientArea = current.getClientArea();

			if (clientArea.contains(toFind)) {
				return current;
			}

			int distance = Geometry.distanceSquared(Geometry.centerPoint(clientArea), toFind);
			if (distance < closest) {
				closest = distance;
				result = current;
			}
		}

		return result;
	}

	/**
	 * Given the desired position of the window, this method returns an adjusted position such that the window is no
	 * larger than its monitor, and does not extend beyond the edge of the monitor. This is used for computing the
	 * initial window position, and subclasses can use this as a utility method if they want to limit the region in
	 * which the window may be moved.
	 * 
	 * @param window
	 *            the window
	 * @param preferredSize
	 *            the preferred position of the window
	 * @return a rectangle as close as possible to preferredSize that does not extend outside the monitor
	 * @see Window#getConstrainedShellBounds
	 */
	public static Rectangle getConstrainedShellBounds(Window window, Rectangle preferredSize) {
		Rectangle result = new Rectangle(preferredSize.x, preferredSize.y, preferredSize.width, preferredSize.height);

		Monitor mon = getClosestMonitor(window.getShell().getDisplay(), Geometry.centerPoint(result));

		Rectangle bounds = mon.getClientArea();

		if (result.height > bounds.height) {
			result.height = bounds.height;
		}

		if (result.width > bounds.width) {
			result.width = bounds.width;
		}

		result.x = Math.max(bounds.x, Math.min(result.x, bounds.x + bounds.width - result.width));
		result.y = Math.max(bounds.y, Math.min(result.y, bounds.y + bounds.height - result.height));

		return result;
	}

}
