/*******************************************************************************
 * Copyright (c) 2009, 2021 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * detects CSS selectors
 *
 * @author David Green
 */
class SelectorRule implements IPredicateRule {

	private final Token token;

	private int readCount = 0;

	public SelectorRule(Token token) {
		this.token = token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		readCount = 0;
		if (resume) {
			if (endSequenceDetected(scanner)) {
				return token;
			}
		} else if (startSequenceDetected(scanner)) {
			if (endSequenceDetected(scanner)) {
				return token;
			}
		}
		while (readCount > 0) {
			--readCount;
			scanner.unread();
		}
		return Token.UNDEFINED;
	}

	private boolean endSequenceDetected(ICharacterScanner scanner) {
		// must hit EOF or ':' but not ';' '{' '}' '/' or '*'
		int read = read(scanner);
		for (;; read = read(scanner)) {
			if (read == ICharacterScanner.EOF || read == '/' || read == '{' || read == '}') {
				scanner.unread();
				--readCount;
				return true;
			}
			if (read == '{' || read == '}' || read == '/') {
				return false;
			}
		}
	}

	private boolean startSequenceDetected(ICharacterScanner scanner) {
		int read = read(scanner);
		if (isWordChar(read)) {
			return true;
		}
		return false;
	}

	private boolean isWordChar(int read) {
		return read != ICharacterScanner.EOF && read != '{' && read != '}' && read != '/'
				&& !Character.isWhitespace(read);
	}

	private int read(ICharacterScanner scanner) {
		++readCount;
		return scanner.read();
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

}
