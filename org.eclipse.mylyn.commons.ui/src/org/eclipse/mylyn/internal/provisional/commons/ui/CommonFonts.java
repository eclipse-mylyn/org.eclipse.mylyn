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

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public class CommonFonts {

	public static Font BOLD;

	public static Font ITALIC;

	public static Font STRIKETHROUGH = null;

	static {
		if (Display.getCurrent() != null) {
			init();
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					init();
				}
			});
		}
	}

	private static void init() {
		BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
		ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);

		Font defaultFont = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
		FontData[] defaultData = defaultFont.getFontData();
		if (defaultData != null && defaultData.length == 1) {
			FontData data = new FontData(defaultData[0].getName(), defaultData[0].getHeight(),
					defaultData[0].getStyle());

			if ("win32".equals(SWT.getPlatform())) {
				// NOTE: Windows only, for: data.data.lfStrikeOut = 1;
				try {
					Field dataField = data.getClass().getDeclaredField("data");
					Object dataObject = dataField.get(data);
					Class<?> clazz = dataObject.getClass().getSuperclass();
					Field strikeOutFiled = clazz.getDeclaredField("lfStrikeOut");
					strikeOutFiled.set(dataObject, (byte) 1);
					CommonFonts.STRIKETHROUGH = new Font(Display.getCurrent(), data);
				} catch (Throwable t) {
					// ignore
				}
			}
		}
		if (CommonFonts.STRIKETHROUGH == null) {
			CommonFonts.STRIKETHROUGH = defaultFont;
		}
	}

	/**
	 * NOTE: disposal of JFaceResources fonts handled by registry.
	 */
	public static void dispose() {
		if (CommonFonts.STRIKETHROUGH != null && !CommonFonts.STRIKETHROUGH.isDisposed()) {
			CommonFonts.STRIKETHROUGH.dispose();
		}
	}

}
