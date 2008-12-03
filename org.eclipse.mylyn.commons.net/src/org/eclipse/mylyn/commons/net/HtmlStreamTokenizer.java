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

package org.eclipse.mylyn.commons.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Parses HTML into tokens.
 * 
 * @author Shawn Minto
 * @since 2.0
 */
public class HtmlStreamTokenizer {

	/** parser state */
	private State state;

	/** reader from which to parse the text */
	private final BufferedReader in;

	/** base URL for resolving relative URLs */
	private final URL base;

	/** buffer holding the text of the current token */
	private final StringBuffer textBuffer;

	/** buffer holding whitespace preceding the current token */
	private final StringBuffer whitespaceBuffer;

	/**
	 * holds a token that was read and then put back in the queue to be returned again on <code>nextToken</code> call
	 */
	private Token pushbackToken;

	/**
	 * holds a character that was read and then determined not to be part of the current token
	 */
	private int pushbackChar;

	/** current quote delimiter (single or double) */
	private int quoteChar;

	/** Allow class client to choose if tag attributes are escaped or not */
	private boolean escapeTagValues;

	/**
	 * Constructor.
	 * 
	 * @param in
	 *            reader for the HTML document to tokenize
	 * @param base
	 *            URL for resolving relative URLs
	 */
	public HtmlStreamTokenizer(Reader in, URL base) {
		textBuffer = new StringBuffer();
		whitespaceBuffer = new StringBuffer();
		pushbackChar = 0;
		state = State.TEXT;
		this.in = new BufferedReader(in);
		this.base = base;
		escapeTagValues = true;
	}

	public void escapeTagAttributes(boolean value) {
		escapeTagValues = value;
	}

	/**
	 * Returns the next token from the stream.
	 */
	public Token nextToken() throws IOException, ParseException {
		if (pushbackToken != null) {
			Token token = pushbackToken;
			pushbackToken = null;
			return token;
		}

		int closingComment = 0;

		textBuffer.setLength(0);
		whitespaceBuffer.setLength(0);
		do {
			int ch;
			if (pushbackChar != 0) {
				ch = pushbackChar;
				pushbackChar = 0;
			} else {
				ch = in.read();
			}
			if (ch < 0) {
				State oldState = state;
				state = State.EOF;
				if (textBuffer.length() > 0 && oldState == State.TEXT) {
					return new Token(textBuffer, whitespaceBuffer, false);
				} else {
					return new Token();
				}
			}
			if (state == State.TEXT) {
				if (ch == '<') {
					state = State.TAG;
					if (textBuffer.length() > 0) {
						return new Token(textBuffer, whitespaceBuffer, false);
					}
				} else if (Character.isWhitespace((char) ch)) {
					pushbackChar = ch;
					state = State.WS;
					if (textBuffer.length() > 0) {
						return new Token(textBuffer, whitespaceBuffer, false);
					}
				} else {
					textBuffer.append((char) ch);
				}
			} else if (state == State.WS) {
				if (!Character.isWhitespace((char) ch)) {
					pushbackChar = ch;
					state = State.TEXT;
				} else {
					whitespaceBuffer.append((char) ch);
				}
			} else if (state == State.TAG) {
				if (ch == '>') {
					state = State.TEXT;
					HtmlTag tag = new HtmlTag(base);
					parseTag(textBuffer.toString(), tag, escapeTagValues);
					return new Token(tag, whitespaceBuffer);
				}
				if (ch == '<' && textBuffer.length() == 0) {
					textBuffer.append("<<"); //$NON-NLS-1$
					state = State.TEXT;
				} else if (ch == '-' && textBuffer.length() == 2 && textBuffer.charAt(1) == '-'
						&& textBuffer.charAt(0) == '!') {
					textBuffer.setLength(0);
					state = State.COMMENT;
				} else if (ch == '\'' || ch == '"') {
					quoteChar = ch;
					textBuffer.append((char) ch);
					state = State.TAG_QUOTE;
				} else {
					textBuffer.append((char) ch);
				}
			} else if (state == State.TAG_QUOTE) {
				if (ch == '>') {
					pushbackChar = ch;
					state = State.TAG;
				} else {
					textBuffer.append((char) ch);
					if (ch == quoteChar) {
						state = State.TAG;
					}
				}
			} else if (state == State.COMMENT) {
				if (ch == '>' && closingComment >= 2) {
					textBuffer.setLength(textBuffer.length() - 2);
					closingComment = 0;
					state = State.TEXT;
					return new Token(textBuffer, whitespaceBuffer, true);
				}
				if (ch == '-') {
					closingComment++;
				} else {
					closingComment = 0;
				}
				textBuffer.append((char) ch);
			}
		} while (true);
	}

	/**
	 * Pushes the token back into the queue, to be returned by the subsequent call to <code>nextToken</code>
	 */
	public void pushback(Token token) {
		pushbackToken = token;
	}

	/**
	 * Parses an HTML tag out of a string of characters.
	 */
	private static void parseTag(String s, HtmlTag tag, boolean escapeValues) throws ParseException {

		int i = 0;
		for (; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {
			// just move forward
		}
		if (i == s.length()) {
			throw new ParseException("parse empty tag", 0); //$NON-NLS-1$
		}

		int start = i;
		for (; i < s.length() && !Character.isWhitespace(s.charAt(i)); i++) {
			// just move forward
		}
		tag.setTagName(s.substring(start, i));

		for (; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {
			// just move forward
		}
		if (i == s.length()) {
			return;
		} else {
			parseAttributes(tag, s, i, escapeValues);
			return;
		}
	}

	/**
	 * parses HTML tag attributes from a buffer and sets them in an HtmlTag
	 */
	private static void parseAttributes(HtmlTag tag, String s, int i, boolean escapeValues) throws ParseException {
		while (i < s.length()) {
			// skip whitespace
			while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
				i++;
			}

			if (i == s.length()) {
				return;
			}

			// read the attribute name -- the rule might be looser than the RFC
			// specifies:
			// everything up to a space or an equal sign is included
			int start = i;
			for (; i < s.length() && !Character.isWhitespace(s.charAt(i)) && s.charAt(i) != '='; i++) {
				// just move forward
			}
			String attributeName = s.substring(start, i).toLowerCase(Locale.ENGLISH);

			if (attributeName.equals("/")) { //$NON-NLS-1$
				tag.setSelfTerminating(true);
				continue;
			}

			for (; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {
				// just move forward
			}
			if (i == s.length() || s.charAt(i) != '=') {
				// no attribute value
				tag.setAttribute(attributeName, ""); //$NON-NLS-1$
				continue;
			}

			// skip whitespace to the start of attribute value
			for (i = i + 1; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {
				// just move forward
			}
			if (i == s.length()) {
				return;
			}

			// read the attribute value -- the rule for unquoted attribute value
			// is
			// looser than the one in Conolly's W3C 1996 lexical analyzer draft:
			// everything
			// is included up to the next space
			String attributeValue;
			if (s.charAt(i) == '"') {
				start = ++i;
				for (; i < s.length() && s.charAt(i) != '"'; i++) {
					// just move forward
				}
				if (i == s.length()) {
					return; // shouldn't happen if input returned by nextToken
				}
				if (escapeValues) {
					attributeValue = unescape(s.substring(start, i));
				} else {
					attributeValue = s.substring(start, i);
				}
				i++;
			} else if (s.charAt(i) == '\'') {
				start = ++i;
				for (; i < s.length() && s.charAt(i) != '\''; i++) {
					// just move forward
				}
				if (i == s.length()) {
					return; // shouldn't happen if input returned by nextToken
				}
				attributeValue = unescape(s.substring(start, i));
				i++;
			} else {
				start = i;
				for (; i < s.length() && !Character.isWhitespace(s.charAt(i)); i++) {
					// just move forward
				}
				attributeValue = s.substring(start, i);
			}
			tag.setAttribute(attributeName, attributeValue);
		}
	}

	/**
	 * Returns a string with HTML escapes changed into their corresponding characters.
	 * 
	 * @deprecated use {@link StringEscapeUtils#unescapeHtml(String)} instead
	 */
	@Deprecated
	public static String unescape(String s) {
		if (s.indexOf('&') == -1) {
			return s;
		} else {
			StringBuffer sb = new StringBuffer(s);
			unescape(sb);
			return sb.toString();
		}
	}

	/**
	 * Replaces (in-place) HTML escapes in a StringBuffer with their corresponding characters.
	 * 
	 * @deprecated use {@link StringEscapeUtils#unescapeHtml(String)} instead
	 */
	@Deprecated
	public static StringBuffer unescape(StringBuffer sb) {
		int i = 0; // index into the unprocessed section of the buffer
		int j = 0; // index into the processed section of the buffer

		while (i < sb.length()) {
			char ch = sb.charAt(i);
			if (ch == '&') {
				int start = i;
				String escape = null;
				for (i = i + 1; i < sb.length(); i++) {
					ch = sb.charAt(i);
					if (!Character.isLetterOrDigit(ch) && !(ch == '#' && i == (start + 1))) {
						escape = sb.substring(start + 1, i);
						break;
					}
				}
				if (i == sb.length() && i != (start + 1)) {
					escape = sb.substring(start + 1);
				}
				if (escape != null) {
					Character character = parseReference(escape);
					if (character != null
							&& !((0x0A == character || 0x0D == character || 0x09 == ch)
									|| (character >= 0x20 && character <= 0xD7FF)
									|| (character >= 0xE000 && character <= 0xFFFD) || (character >= 0x10000 && character <= 0x10FFFF))) {
						// Character is an invalid xml character
						// http://www.w3.org/TR/REC-xml/#charsets
						character = null;
					}
					if (character != null) {
						ch = character.charValue();
					} else {
						// not an HTML escape; rewind
						i = start;
						ch = '&';
					}
				}
			}
			sb.setCharAt(j, ch);
			i++;
			j++;
		}

		sb.setLength(j);
		return sb;
	}

	/**
	 * Parses HTML character and entity references and returns the corresponding character.
	 */
	private static Character parseReference(String s) {
		if (s.length() == 0) {
			return null;
		}

		if (s.charAt(0) == '#') {
			// character reference
			if (s.length() == 1) {
				return null;
			}

			try {
				int value;
				if (s.charAt(1) == 'x') {
					// Hex reference
					value = Integer.parseInt(s.substring(2), 16);
				} else {
					// Decimal reference
					value = Integer.parseInt(s.substring(1));
				}
				return new Character((char) value);
			} catch (NumberFormatException e) {
				return null;
			}
		} else {
			return entities.get(s);
		}
	}

	/**
	 * Class for current token.
	 */
	public static class Token {
		public static final Type EOF = new Type();

		public static final Type TEXT = new Type();

		public static final Type TAG = new Type();

		public static final Type COMMENT = new Type();

		/** token's type */
		private Type type;

		/** token's value */
		private final Object value;

		/** whitespace preceding the token */
		private final StringBuffer whitespace;

		/**
		 * Constructor for the EOF token.
		 */
		protected Token() {
			type = EOF;
			value = null;
			whitespace = null;
		}

		/**
		 * Constructor for the HTML tag tokens.
		 */
		protected Token(HtmlTag tag, StringBuffer whitespace) {
			type = TAG;
			value = tag;
			this.whitespace = whitespace;
		}

		/**
		 * Constructor for regular text and comments.
		 */
		protected Token(StringBuffer text, StringBuffer whitespace, boolean comment) {
			if (comment) {
				type = COMMENT;
			} else {
				type = TEXT;
			}
			this.value = text;
			this.whitespace = whitespace;
		}

		/**
		 * Returns the token's type.
		 */
		public Type getType() {
			return type;
		}

		/**
		 * Returns the whitespace preceding the token.
		 */
		public StringBuffer getWhitespace() {
			return whitespace;
		}

		/**
		 * Returns the token's value. This is an HtmlTag for tokens of type <code>TAG</code> and a StringBuffer for
		 * tokens of type <code>TEXT</code> and <code>COMMENT</code>. For tokens of type <code>EOF</code>, the value is
		 * <code>null</code>.
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * Returns the string representation of the token, including the preceding whitespace.
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if (whitespace != null) {
				sb.append(whitespace);
			}
			if (value != null) {
				if (type == TAG) {
					// sb.append('<');
				} else if (type == COMMENT) {
					sb.append("<!--"); //$NON-NLS-1$
				}
				sb.append(value);
				if (type == TAG) {
					// if(value instanceof HtmlTag) {
					// HtmlTag htmlTag = (HtmlTag)value;
					// if(htmlTag.getTagName().startsWith("?xml")) {
					// sb.append("?>");
					// }
					// } else {
					// sb.append('>');

				} else if (type == COMMENT) {
					sb.append("-->"); //$NON-NLS-1$
				}

			}
			return sb.toString();
		}

		/**
		 * Private enum class for token type.
		 */
		private static class Type {
			private Type() {
				// don't need to do anything
			}
		}
	}

	/**
	 * Enum class for parser state.
	 */
	private static class State {
		static final State EOF = new State();

		static final State COMMENT = new State();

		static final State TEXT = new State();

		static final State TAG = new State();

		static final State WS = new State();

		static final State TAG_QUOTE = new State();

		private State() {
			// don't need to do anything
		}
	}

	/** names and values of HTML entity references */
	private static HashMap<String, Character> entities;

	/*
	 * Based on ISO 8879.
	 * 
	 * Portions (c) International Organization for Standardization 1986
	 * Permission to copy in any form is granted for use with conforming SGML
	 * systems and applications as defined in ISO 8879, provided this notice is
	 * included in all copies.
	 * 
	 */
	static {
		entities = new HashMap<String, Character>();
		entities.put("nbsp", Character.valueOf('\240')); // no-break //$NON-NLS-1$
		// space =
		// non-breaking
		// space
		entities.put("iexcl", Character.valueOf('\241')); // inverted //$NON-NLS-1$
		// exclamation
		// mark
		entities.put("cent", Character.valueOf('\242')); // cent sign //$NON-NLS-1$
		entities.put("pound", Character.valueOf('\243')); // pound //$NON-NLS-1$
		// sign
		entities.put("curren", Character.valueOf('\244')); // currency //$NON-NLS-1$
		// sign
		entities.put("yen", Character.valueOf('\245')); // yen sign = //$NON-NLS-1$
		// yuan sign
		entities.put("brvbar", Character.valueOf('\246')); // broken //$NON-NLS-1$
		// bar =
		// broken
		// vertical
		// bar
		entities.put("sect", Character.valueOf('\247')); // section //$NON-NLS-1$
		// sign
		entities.put("uml", Character.valueOf('\250')); // diaeresis = //$NON-NLS-1$
		// spacing
		// diaeresis
		entities.put("copy", Character.valueOf('\251')); // copyright //$NON-NLS-1$
		// sign
		entities.put("ordf", Character.valueOf('\252')); // feminine //$NON-NLS-1$
		// ordinal
		// indicator
		entities.put("laquo", Character.valueOf('\253')); // left-pointing //$NON-NLS-1$
		// double
		// angle
		// quotation
		// mark =
		// left
		// pointing
		// guillemet
		entities.put("not", Character.valueOf('\254')); // not sign //$NON-NLS-1$
		entities.put("shy", Character.valueOf('\255')); // soft hyphen = //$NON-NLS-1$
		// discretionary
		// hyphen
		entities.put("reg", Character.valueOf('\256')); // registered //$NON-NLS-1$
		// sign =
		// registered
		// trade mark
		// sign
		entities.put("macr", Character.valueOf('\257')); // macron = //$NON-NLS-1$
		// spacing
		// macron =
		// overline
		// = APL
		// overbar
		entities.put("deg", Character.valueOf('\260')); // degree sign //$NON-NLS-1$
		entities.put("plusmn", Character.valueOf('\261')); // plus-minus //$NON-NLS-1$
		// sign =
		// plus-or-minus
		// sign
		entities.put("sup2", Character.valueOf('\262')); // superscript //$NON-NLS-1$
		// two =
		// superscript
		// digit two
		// = squared
		entities.put("sup3", Character.valueOf('\263')); // superscript //$NON-NLS-1$
		// three =
		// superscript
		// digit
		// three =
		// cubed
		entities.put("acute", Character.valueOf('\264')); // acute //$NON-NLS-1$
		// accent =
		// spacing
		// acute
		entities.put("micro", Character.valueOf('\265')); // micro //$NON-NLS-1$
		// sign
		entities.put("para", Character.valueOf('\266')); // pilcrow //$NON-NLS-1$
		// sign =
		// paragraph
		// sign
		entities.put("middot", Character.valueOf('\267')); // middle //$NON-NLS-1$
		// dot =
		// Georgian
		// comma =
		// Greek
		// middle
		// dot
		entities.put("cedil", Character.valueOf('\270')); // cedilla = //$NON-NLS-1$
		// spacing
		// cedilla
		entities.put("sup1", Character.valueOf('\271')); // superscript //$NON-NLS-1$
		// one =
		// superscript
		// digit one
		entities.put("ordm", Character.valueOf('\272')); // masculine //$NON-NLS-1$
		// ordinal
		// indicator
		entities.put("raquo", Character.valueOf('\273')); // right-pointing //$NON-NLS-1$
		// double
		// angle
		// quotation
		// mark =
		// right
		// pointing
		// guillemet
		entities.put("frac14", Character.valueOf('\274')); // vulgar //$NON-NLS-1$
		// fraction
		// one
		// quarter =
		// fraction
		// one
		// quarter
		entities.put("frac12", Character.valueOf('\275')); // vulgar //$NON-NLS-1$
		// fraction
		// one half
		// =
		// fraction
		// one half
		entities.put("frac34", Character.valueOf('\276')); // vulgar //$NON-NLS-1$
		// fraction
		// three
		// quarters
		// =
		// fraction
		// three
		// quarters
		entities.put("iquest", Character.valueOf('\277')); // inverted //$NON-NLS-1$
		// question
		// mark =
		// turned
		// question
		// mark
		entities.put("Agrave", Character.valueOf('\300')); // latin //$NON-NLS-1$
		// capital
		// letter A
		// with
		// grave =
		// latin
		// capital
		// letter A
		// grave
		entities.put("Aacute", Character.valueOf('\301')); // latin //$NON-NLS-1$
		// capital
		// letter A
		// with
		// acute
		entities.put("Acirc", Character.valueOf('\302')); // latin //$NON-NLS-1$
		// capital
		// letter A
		// with
		// circumflex
		entities.put("Atilde", Character.valueOf('\303')); // latin //$NON-NLS-1$
		// capital
		// letter A
		// with
		// tilde
		entities.put("Auml", Character.valueOf('\304')); // latin //$NON-NLS-1$
		// capital
		// letter A
		// with
		// diaeresis
		entities.put("Aring", Character.valueOf('\305')); // latin //$NON-NLS-1$
		// capital
		// letter A
		// with ring
		// above =
		// latin
		// capital
		// letter A
		// ring
		entities.put("AElig", Character.valueOf('\306')); // latin //$NON-NLS-1$
		// capital
		// letter AE
		// = latin
		// capital
		// ligature
		// AE
		entities.put("Ccedil", Character.valueOf('\307')); // latin //$NON-NLS-1$
		// capital
		// letter C
		// with
		// cedilla
		entities.put("Egrave", Character.valueOf('\310')); // latin //$NON-NLS-1$
		// capital
		// letter E
		// with
		// grave
		entities.put("Eacute", Character.valueOf('\311')); // latin //$NON-NLS-1$
		// capital
		// letter E
		// with
		// acute
		entities.put("Ecirc", Character.valueOf('\312')); // latin //$NON-NLS-1$
		// capital
		// letter E
		// with
		// circumflex
		entities.put("Euml", Character.valueOf('\313')); // latin //$NON-NLS-1$
		// capital
		// letter E
		// with
		// diaeresis
		entities.put("Igrave", Character.valueOf('\314')); // latin //$NON-NLS-1$
		// capital
		// letter I
		// with
		// grave
		entities.put("Iacute", Character.valueOf('\315')); // latin //$NON-NLS-1$
		// capital
		// letter I
		// with
		// acute
		entities.put("Icirc", Character.valueOf('\316')); // latin //$NON-NLS-1$
		// capital
		// letter I
		// with
		// circumflex
		entities.put("Iuml", Character.valueOf('\317')); // latin //$NON-NLS-1$
		// capital
		// letter I
		// with
		// diaeresis
		entities.put("ETH", Character.valueOf('\320')); // latin capital //$NON-NLS-1$
		// letter ETH
		entities.put("Ntilde", Character.valueOf('\321')); // latin //$NON-NLS-1$
		// capital
		// letter N
		// with
		// tilde
		entities.put("Ograve", Character.valueOf('\322')); // latin //$NON-NLS-1$
		// capital
		// letter O
		// with
		// grave
		entities.put("Oacute", Character.valueOf('\323')); // latin //$NON-NLS-1$
		// capital
		// letter O
		// with
		// acute
		entities.put("Ocirc", Character.valueOf('\324')); // latin //$NON-NLS-1$
		// capital
		// letter O
		// with
		// circumflex
		entities.put("Otilde", Character.valueOf('\325')); // latin //$NON-NLS-1$
		// capital
		// letter O
		// with
		// tilde
		entities.put("Ouml", Character.valueOf('\326')); // latin //$NON-NLS-1$
		// capital
		// letter O
		// with
		// diaeresis
		entities.put("times", Character.valueOf('\327')); // multiplication //$NON-NLS-1$
		// sign
		entities.put("Oslash", Character.valueOf('\330')); // latin //$NON-NLS-1$
		// capital
		// letter O
		// with
		// stroke =
		// latin
		// capital
		// letter O
		// slash
		entities.put("Ugrave", Character.valueOf('\331')); // latin //$NON-NLS-1$
		// capital
		// letter U
		// with
		// grave
		entities.put("Uacute", Character.valueOf('\332')); // latin //$NON-NLS-1$
		// capital
		// letter U
		// with
		// acute
		entities.put("Ucirc", Character.valueOf('\333')); // latin //$NON-NLS-1$
		// capital
		// letter U
		// with
		// circumflex
		entities.put("Uuml", Character.valueOf('\334')); // latin //$NON-NLS-1$
		// capital
		// letter U
		// with
		// diaeresis
		entities.put("Yacute", Character.valueOf('\335')); // latin //$NON-NLS-1$
		// capital
		// letter Y
		// with
		// acute
		entities.put("THORN", Character.valueOf('\336')); // latin //$NON-NLS-1$
		// capital
		// letter
		// THORN
		entities.put("szlig", Character.valueOf('\337')); // latin //$NON-NLS-1$
		// small
		// letter
		// sharp s =
		// ess-zed
		entities.put("agrave", Character.valueOf('\340')); // latin //$NON-NLS-1$
		// small
		// letter a
		// with
		// grave =
		// latin
		// small
		// letter a
		// grave
		entities.put("aacute", Character.valueOf('\341')); // latin //$NON-NLS-1$
		// small
		// letter a
		// with
		// acute
		entities.put("acirc", Character.valueOf('\342')); // latin //$NON-NLS-1$
		// small
		// letter a
		// with
		// circumflex
		entities.put("atilde", Character.valueOf('\343')); // latin //$NON-NLS-1$
		// small
		// letter a
		// with
		// tilde
		entities.put("auml", Character.valueOf('\344')); // latin //$NON-NLS-1$
		// small
		// letter a
		// with
		// diaeresis
		entities.put("aring", Character.valueOf('\345')); // latin //$NON-NLS-1$
		// small
		// letter a
		// with ring
		// above =
		// latin
		// small
		// letter a
		// ring
		entities.put("aelig", Character.valueOf('\346')); // latin //$NON-NLS-1$
		// small
		// letter ae
		// = latin
		// small
		// ligature
		// ae
		entities.put("ccedil", Character.valueOf('\347')); // latin //$NON-NLS-1$
		// small
		// letter c
		// with
		// cedilla
		entities.put("egrave", Character.valueOf('\350')); // latin //$NON-NLS-1$
		// small
		// letter e
		// with
		// grave
		entities.put("eacute", Character.valueOf('\351')); // latin //$NON-NLS-1$
		// small
		// letter e
		// with
		// acute
		entities.put("ecirc", Character.valueOf('\352')); // latin //$NON-NLS-1$
		// small
		// letter e
		// with
		// circumflex
		entities.put("euml", Character.valueOf('\353')); // latin //$NON-NLS-1$
		// small
		// letter e
		// with
		// diaeresis
		entities.put("igrave", Character.valueOf('\354')); // latin //$NON-NLS-1$
		// small
		// letter i
		// with
		// grave
		entities.put("iacute", Character.valueOf('\355')); // latin //$NON-NLS-1$
		// small
		// letter i
		// with
		// acute
		entities.put("icirc", Character.valueOf('\356')); // latin //$NON-NLS-1$
		// small
		// letter i
		// with
		// circumflex
		entities.put("iuml", Character.valueOf('\357')); // latin //$NON-NLS-1$
		// small
		// letter i
		// with
		// diaeresis
		entities.put("eth", Character.valueOf('\360')); // latin small //$NON-NLS-1$
		// letter eth
		entities.put("ntilde", Character.valueOf('\361')); // latin //$NON-NLS-1$
		// small
		// letter n
		// with
		// tilde
		entities.put("ograve", Character.valueOf('\362')); // latin //$NON-NLS-1$
		// small
		// letter o
		// with
		// grave
		entities.put("oacute", Character.valueOf('\363')); // latin //$NON-NLS-1$
		// small
		// letter o
		// with
		// acute
		entities.put("ocirc", Character.valueOf('\364')); // latin //$NON-NLS-1$
		// small
		// letter o
		// with
		// circumflex
		entities.put("otilde", Character.valueOf('\365')); // latin //$NON-NLS-1$
		// small
		// letter o
		// with
		// tilde
		entities.put("ouml", Character.valueOf('\366')); // latin //$NON-NLS-1$
		// small
		// letter o
		// with
		// diaeresis
		entities.put("divide", Character.valueOf('\367')); // division //$NON-NLS-1$
		// sign
		entities.put("oslash", Character.valueOf('\370')); // latin //$NON-NLS-1$
		// small
		// letter o
		// with
		// stroke =
		// latin
		// small
		// letter o
		// slash
		entities.put("ugrave", Character.valueOf('\371')); // latin //$NON-NLS-1$
		// small
		// letter u
		// with
		// grave
		entities.put("uacute", Character.valueOf('\372')); // latin //$NON-NLS-1$
		// small
		// letter u
		// with
		// acute
		entities.put("ucirc", Character.valueOf('\373')); // latin //$NON-NLS-1$
		// small
		// letter u
		// with
		// circumflex
		entities.put("uuml", Character.valueOf('\374')); // latin //$NON-NLS-1$
		// small
		// letter u
		// with
		// diaeresis
		entities.put("yacute", Character.valueOf('\375')); // latin //$NON-NLS-1$
		// small
		// letter y
		// with
		// acute
		entities.put("thorn", Character.valueOf('\376')); // latin //$NON-NLS-1$
		// small
		// letter
		// thorn
		entities.put("yuml", Character.valueOf('\377')); // latin //$NON-NLS-1$
		// small
		// letter y
		// with
		// diaeresis

		// Special characters
		entities.put("quot", Character.valueOf('\42')); // quotation //$NON-NLS-1$
		// mark = APL
		// quote
		entities.put("amp", Character.valueOf('\46')); // ampersand //$NON-NLS-1$
		entities.put("lt", Character.valueOf('\74')); // less-than //$NON-NLS-1$
		// sign
		entities.put("gt", Character.valueOf('\76')); // greater-than //$NON-NLS-1$
		// sign
		// Latin Extended-A
		entities.put("OElig", Character.valueOf('\u0152')); // latin //$NON-NLS-1$
		// capital
		// ligature
		// OE
		entities.put("oelig", Character.valueOf('\u0153')); // latin //$NON-NLS-1$
		// small
		// ligature
		// oe,
		// ligature
		// is a
		// misnomer,
		// this is a
		// separate
		// character
		// in some
		// languages
		entities.put("Scaron", Character.valueOf('\u0160')); // latin //$NON-NLS-1$
		// capital
		// letter
		// S
		// with
		// caron
		entities.put("scaron", Character.valueOf('\u0161')); // latin //$NON-NLS-1$
		// small
		// letter
		// s
		// with
		// caron
		entities.put("Yuml", Character.valueOf('\u0178')); // latin //$NON-NLS-1$
		// capital
		// letter Y
		// with
		// diaeresis
		// Spacing Modifier Letters
		entities.put("circ", Character.valueOf('\u02c6')); // modifier //$NON-NLS-1$
		// letter
		// circumflex
		// accent
		entities.put("tilde", Character.valueOf('\u02dc')); // small //$NON-NLS-1$
		// tilde
		// General punctuation
		entities.put("ensp", Character.valueOf('\u2002')); // en space //$NON-NLS-1$
		entities.put("emsp", Character.valueOf('\u2003')); // em space //$NON-NLS-1$
		entities.put("thinsp", Character.valueOf('\u2009')); // thin //$NON-NLS-1$
		// space
		entities.put("zwnj", Character.valueOf('\u200c')); // zero //$NON-NLS-1$
		// width
		// non-joiner
		entities.put("zwj", Character.valueOf('\u200d')); // zero //$NON-NLS-1$
		// width
		// joiner
		entities.put("lrm", Character.valueOf('\u200e')); // left-to-right //$NON-NLS-1$
		// mark
		entities.put("rlm", Character.valueOf('\u200f')); // right-to-left //$NON-NLS-1$
		// mark
		entities.put("ndash", Character.valueOf('\u2013')); // en dash //$NON-NLS-1$
		entities.put("mdash", Character.valueOf('\u2014')); // em dash //$NON-NLS-1$
		entities.put("lsquo", Character.valueOf('\u2018')); // left //$NON-NLS-1$
		// single
		// quotation
		// mark
		entities.put("rsquo", Character.valueOf('\u2019')); // right //$NON-NLS-1$
		// single
		// quotation
		// mark
		entities.put("sbquo", Character.valueOf('\u201a')); // single //$NON-NLS-1$
		// low-9
		// quotation
		// mark
		entities.put("ldquo", Character.valueOf('\u201c')); // left //$NON-NLS-1$
		// double
		// quotation
		// mark
		entities.put("rdquo", Character.valueOf('\u201d')); // right //$NON-NLS-1$
		// double
		// quotation
		// mark
		entities.put("bdquo", Character.valueOf('\u201e')); // double //$NON-NLS-1$
		// low-9
		// quotation
		// mark
		entities.put("dagger", Character.valueOf('\u2020')); // dagger //$NON-NLS-1$
		entities.put("Dagger", Character.valueOf('\u2021')); // double //$NON-NLS-1$
		// dagger
		entities.put("permil", Character.valueOf('\u2030')); // per //$NON-NLS-1$
		// mille
		// sign
		entities.put("lsaquo", Character.valueOf('\u2039')); // single //$NON-NLS-1$
		// left-pointing
		// angle
		// quotation
		// mark,
		// not
		// yet
		// standardized
		entities.put("rsaquo", Character.valueOf('\u203a')); // single //$NON-NLS-1$
		// right-pointing
		// angle
		// quotation
		// mark,
		// not
		// yet
		// standardized
		entities.put("euro", Character.valueOf('\u20ac')); // euro sign //$NON-NLS-1$
	}
}
