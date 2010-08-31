/*******************************************************************************
 * Copyright (c) 2009 Hiroyuki Inaba and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Hiroyuki Inaba - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.screenshots;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Hiroyuki Inaba
 */
public class ScreenshotImages {

	private static final String ID_PLUGIN = "org.eclipse.mylyn.commons.screenshots"; //$NON-NLS-1$

	private static final URL baseURL = Platform.getBundle(ID_PLUGIN).getEntry("/icons/"); //$NON-NLS-1$

	private static final String T_TOOL = "etool16"; //$NON-NLS-1$

	private static final String T_DRAW = "draw16"; //$NON-NLS-1$

	public static final ImageDescriptor EDIT_FREE = create(T_DRAW, "edit_free.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_LINE = create(T_DRAW, "edit_line.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_BOX = create(T_DRAW, "edit_box.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_RBOX = create(T_DRAW, "edit_rbox.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_OVAL = create(T_DRAW, "edit_oval.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_FILL_BOX = create(T_DRAW, "edit_fill_box.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_FILL_RBOX = create(T_DRAW, "edit_fill_rbox.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_FILL_OVAL = create(T_DRAW, "edit_fill_oval.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_ARROW1 = create(T_DRAW, "edit_arrow1.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_ARROW2 = create(T_DRAW, "edit_arrow2.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_UNDO = create(T_TOOL, "undo_edit.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_REDO = create(T_TOOL, "redo_edit.gif"); //$NON-NLS-1$

	public static final ImageDescriptor FILE_OBJ = create(T_TOOL, "file_obj.gif"); //$NON-NLS-1$

	public static final ImageDescriptor CLIPBOARD_OBJ = create(T_TOOL, "clipboard_obj.gif"); //$NON-NLS-1$

	public static final ImageDescriptor MONITOR_OBJ = create(T_TOOL, "monitor_obj.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_SOLD = create(T_DRAW, "line_sold.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_DOT = create(T_DRAW, "line_dot.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_DASH = create(T_DRAW, "line_dash.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_DASH1D = create(T_DRAW, "line_dash1d.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_DASH2D = create(T_DRAW, "line_dash2d.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_BOLD1 = create(T_DRAW, "line_bold1.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_BOLD2 = create(T_DRAW, "line_bold2.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_BOLD4 = create(T_DRAW, "line_bold4.gif"); //$NON-NLS-1$

	public static final ImageDescriptor LINE_BOLD8 = create(T_DRAW, "line_bold8.gif"); //$NON-NLS-1$

	public static final ImageDescriptor SEL_RECT = create(T_TOOL, "select_rect.gif"); //$NON-NLS-1$

	public static final ImageDescriptor CLEAR = create(T_TOOL, "clear.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT = create(T_TOOL, "edit.gif"); //$NON-NLS-1$

	public static final ImageDescriptor EDIT_SMALL = create(T_TOOL, "edit-small.png"); //$NON-NLS-1$

	public static final ImageDescriptor CUT = create(T_TOOL, "cut.gif"); //$NON-NLS-1$

	public static final ImageDescriptor UNDO = create(T_TOOL, "undo_edit.gif"); //$NON-NLS-1$

	public static final ImageDescriptor REDO = create(T_TOOL, "redo_edit.gif"); //$NON-NLS-1$

	public static final ImageDescriptor IMAGE_FIT = create(T_TOOL, "capture-fit.png"); //$NON-NLS-1$

	public static final ImageDescriptor IMAGE_CAPTURE = create(T_TOOL, "capture-screen.png"); //$NON-NLS-1$

	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (baseURL == null) {
			throw new MalformedURLException();
		}

		StringBuffer buffer = new StringBuffer(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

}
