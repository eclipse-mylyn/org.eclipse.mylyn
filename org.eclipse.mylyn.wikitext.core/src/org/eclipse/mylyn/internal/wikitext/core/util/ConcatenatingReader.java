/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.util;

import java.io.IOException;
import java.io.Reader;

public class ConcatenatingReader extends Reader {

	private final Reader[] readers;

	private int index = 0;

	public ConcatenatingReader(Reader... readers) {
		this.readers = readers;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (index < readers.length) {
			int i;
			do {
				i = readers[index].read(cbuf, off, len);
				if (i == -1) {
					++index;
				}
			} while (i == -1 && index < readers.length);
			return i;
		}
		return -1;
	}

	@Override
	public void close() throws IOException {
		for (int x = 0; x < readers.length; ++x) {
			readers[x].close();
		}
	}

}
