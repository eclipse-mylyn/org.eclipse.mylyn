/*******************************************************************************
 * Copyright (c) 2013, 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class IOUtil {
	/**
	 * Reads the content of the given file into a string.
	 */
	public static String readFully(IFile file) throws CoreException, IOException {
		try (Reader r = new InputStreamReader(new BufferedInputStream(file.getContents()), file.getCharset())) {
			return IOUtils.toString(r);
		}
	}
}
