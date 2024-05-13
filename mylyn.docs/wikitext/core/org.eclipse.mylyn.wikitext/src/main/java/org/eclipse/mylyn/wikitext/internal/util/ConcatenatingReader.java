/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.internal.util;

import java.io.IOException;
import java.io.Reader;

/**
 * a reader that concatenates character streams
 *
 * @author David Green
 * @since 4.3
 */
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
		for (Reader reader : readers) {
			reader.close();
		}
	}

}
