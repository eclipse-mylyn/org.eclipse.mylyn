/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Scans for quoted text inside a comment
 * <p>
 * A quoted text is defined by having a ">" at the start of line and extends until the end of line
 * 
 * @author Willian Mitsuda
 */
public class QuotedCommentRule implements IRule {

	private IToken successToken;

	public QuotedCommentRule(IToken successToken) {
		this.successToken = successToken;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		if (scanner.getColumn() != 0) {
			return Token.UNDEFINED;
		}
		int c = scanner.read();
		if ((char) c != '>') {
			scanner.unread();
			return Token.UNDEFINED;
		}

		char[][] lineDelimiters = scanner.getLegalLineDelimiters();
		do {
			c = scanner.read();
			for (int i = 0; i < lineDelimiters.length; i++) {
				if ((char) c == lineDelimiters[i][0] && matchesRemainingSequence(scanner, lineDelimiters[i])) {
					return successToken;
				}
			}
		} while (c != ICharacterScanner.EOF);
		return successToken;
	}

	/**
	 * Tests the scanner against a character sequence, starting from position 1
	 * <p>
	 * It presumes that if this method is called, sequence[0] matches a previous read character from scanner
	 */
	private boolean matchesRemainingSequence(ICharacterScanner scanner, char[] sequence) {
		for (int i = 1; i < sequence.length; i++) {
			int c = scanner.read();
			if (c == ICharacterScanner.EOF) {
				return true;
			} else if ((char) c != sequence[i]) {
				// Rewinds the scanner back to initial state
				for (int j = 1; j <= i; j++) {
					scanner.unread();
				}
				return false;
			}
		}
		return true;
	}

}