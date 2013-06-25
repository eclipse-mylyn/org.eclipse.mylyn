/*******************************************************************************
 * Copyright (c) 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class IOUtil {
	/**
	 * Reads the content of the given file into a string.
	 */
	public static String readFully(IFile file) throws UnsupportedEncodingException, CoreException, IOException {
		StringWriter w = new StringWriter();
		Reader r = new InputStreamReader(new BufferedInputStream(file.getContents()), file.getCharset());
		try {
			int i;
			while ((i = r.read()) != -1) {
				w.write((char) i);
			}
		} finally {
			r.close();
		}
		String inputContent = w.toString();
		return inputContent;
	}
}
