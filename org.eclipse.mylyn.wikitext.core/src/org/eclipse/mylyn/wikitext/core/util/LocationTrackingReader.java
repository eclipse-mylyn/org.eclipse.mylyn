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
package org.eclipse.mylyn.wikitext.core.util;

import java.io.IOException;
import java.io.Reader;

/**
 * 
 * 
 * @author David Green
 */
public class LocationTrackingReader extends Reader {
	private Reader delegate;

	private int offset = -1;

	private int lineOffset = -1;

	private int lineNumber = -1;

	private char[] buf;

	private int bufOffset = 0;

	private int bufLength = 0;

	public LocationTrackingReader(Reader delegate) {
		this(delegate, 2048);
	}

	public LocationTrackingReader(Reader delegate, int bufferSize) {
		this.delegate = delegate;
		buf = new char[bufferSize];
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (bufLength > 0) {
			int length = Math.min(len, bufLength);
			System.arraycopy(buf, bufOffset, cbuf, off, length);
			bufLength -= length;
			bufOffset += length;
			if (bufLength == 0) {
				bufOffset = 0;
			}
			offset += length;
			return length;
		} else {
			int read = delegate.read(cbuf, off, len);
			if (read != -1) {
				offset += read;
			}
			return read;
		}
	}

	@Override
	public int read() throws IOException {
		if (bufLength > 0) {
			int c = buf[bufOffset];
			bufLength -= 1;
			bufOffset += 1;
			if (bufLength == 0) {
				bufOffset = 0;
			}
			offset += 1;
			return c;
		} else {
			int read = delegate.read();
			if (read != -1) {
				++offset;
			}
			return read;
		}
	}

	/**
	 * Read a line of text, omitting the line delimiters.
	 * 
	 * @return the text or null if the end of input has been reached
	 * 
	 * @see #getLineOffset()
	 */
	public String readLine() throws IOException {
		lineOffset = offset + 1;

		int lineBufOffset = bufOffset;
		int c = -1;
		for (int x = lineBufOffset;; ++x) {
			if (x >= (bufOffset + bufLength)) {
				if (bufOffset > 0 && bufLength > 0) {
					System.arraycopy(buf, bufOffset, buf, 0, bufLength);
					x -= bufOffset;
					bufOffset = 0;
					lineBufOffset = 0;
				}
				if (bufOffset + bufLength >= buf.length) {
					// expand the buffer
					char[] newBuf = new char[buf.length * 2];
					if (bufLength > 0) {
						System.arraycopy(buf, bufOffset, newBuf, 0, bufLength);
					}
					x -= bufOffset;
					bufOffset = 0;
					lineBufOffset = 0;
					buf = newBuf;
				}
				int emptyOffset = bufOffset + bufLength;
				int read = delegate.read(buf, emptyOffset, buf.length - emptyOffset);
				if (read > 0) {
					bufLength += read;
				} else {
					// end of input
					break;
				}
			}
			if (x >= (bufOffset + bufLength)) {
				// end of input
				break;
			}
			int nc = buf[x];
			if (nc == '\n') {
				// eol
				int length = x - lineBufOffset + 1;
				bufOffset += length;
				bufLength -= length;
				offset += length;

				int stringLength = c == '\r' ? length - 2 : length - 1;
				++lineNumber;
				return new String(buf, lineBufOffset, stringLength);
			} else if (c == '\r') {
				int length = x - lineBufOffset;
				bufOffset += length;
				bufLength -= length;
				offset += length;
				int stringLength = length - 1;
				++lineNumber;
				return new String(buf, lineBufOffset, stringLength);
			}
			c = nc;
		}
		if (bufLength > 0) {
			String line = new String(buf, bufOffset, c == '\r' ? bufLength - 1 : bufLength);
			bufOffset = 0;
			offset += bufLength;
			bufLength = 0;
			++lineNumber;
			return line;
		} else {
			++lineNumber;
			return null;
		}
	}

	/**
	 * Get the character offset of the last character read.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Get the character offset of the first character of the last line read. The result of calling this method is only
	 * meaningful immediately after having called {@link #readLine()}.
	 * 
	 * @see #readLine()
	 */
	public int getLineOffset() {
		return lineOffset;
	}

	/**
	 * get the 0-based line number of the last line read. The result of calling this method is only meaningful
	 * immediately after having called {@link #readLine()}.
	 * 
	 * @see #readLine()
	 */
	public int getLineNumber() {
		return lineNumber;
	}

}
