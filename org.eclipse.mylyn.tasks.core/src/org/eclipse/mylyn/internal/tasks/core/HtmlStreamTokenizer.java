/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;

public class HtmlStreamTokenizer {

	/** parser state */
	private State state;

	/** reader from which to parse the text */
	private BufferedReader in;

	/** base URL for resolving relative URLs */
	private URL base;

	/** buffer holding the text of the current token */
	private StringBuffer textBuffer;

	/** buffer holding whitespace preceding the current token */
	private StringBuffer whitespaceBuffer;

	/**
	 * holds a token that was read and then put back in the queue to be returned
	 * again on <code>nextToken</code> call
	 */
	private Token pushbackToken;

	/**
	 * holds a character that was read and then determined not to be part of the
	 * current token
	 */
	private int pushbackChar;

	/** current quote delimiter (single or double) */
	private int quoteChar;

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
					if (textBuffer.length() > 0)
						return new Token(textBuffer, whitespaceBuffer, false);
				} else if (Character.isWhitespace((char) ch)) {
					pushbackChar = ch;
					state = State.WS;
					if (textBuffer.length() > 0)
						return new Token(textBuffer, whitespaceBuffer, false);
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
					parseTag(textBuffer.toString(), tag);
					return new Token(tag, whitespaceBuffer);
				}
				if (ch == '<' && textBuffer.length() == 0) {
					textBuffer.append("<<");
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
					if (ch == quoteChar)
						state = State.TAG;
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
	 * Pushes the token back into the queue, to be returned by the subsequent
	 * call to <code>nextToken</code>
	 */
	public void pushback(Token token) {
		pushbackToken = token;
	}

	/**
	 * Parses an HTML tag out of a string of characters.
	 */
	private static void parseTag(String s, HtmlTag tag) throws ParseException {

		int i = 0;
		for (; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {
			// just move forward
		}
		if (i == s.length())
			throw new ParseException("parse empty tag", 0);

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
			parseAttributes(tag, s, i);
			return;
		}
	}

	/**
	 * parses HTML tag attributes from a buffer and sets them in an HtmlTag
	 */
	private static void parseAttributes(HtmlTag tag, String s, int i) throws ParseException {
		while (i < s.length()) {
			// skip whitespace
			while (i < s.length() && Character.isWhitespace(s.charAt(i)))
				i++;

			if (i == s.length())
				return;

			// read the attribute name -- the rule might be looser than the RFC
			// specifies:
			// everything up to a space or an equal sign is included
			int start = i;
			for (; i < s.length() && !Character.isWhitespace(s.charAt(i)) && s.charAt(i) != '='; i++) {
				// just move forward
			}
			String attributeName = s.substring(start, i).toLowerCase();

			if(attributeName.equals("/")) {
				tag.setSelfTerminating(true);
				continue;
			}
			
			for (; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {
				// just move forward
			}
			if (i == s.length() || s.charAt(i) != '=') {
				// no attribute value				
				tag.setAttribute(attributeName, "");				
				continue;
			}

			// skip whitespace to the start of attribute value
			for (i = i + 1; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {
				// just move forward
			}
			if (i == s.length())
				return;

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
				if (i == s.length())
					return; // shouldn't happen if input returned by nextToken
				attributeValue = unescape(s.substring(start, i));
				i++;
			} else if (s.charAt(i) == '\'') {
				start = ++i;
				for (; i < s.length() && s.charAt(i) != '\''; i++) {
					// just move forward
				}
				if (i == s.length())
					return; // shouldn't happen if input returned by nextToken
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
	 * Returns a string with HTML escapes changed into their corresponding
	 * characters.
	 */
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
	 * Replaces (in-place) HTML escapes in a StringBuffer with their
	 * corresponding characters.
	 */
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
	 * Parses HTML character and entity references and returns the corresponding
	 * character.
	 */
	private static Character parseReference(String s) {
		if (s.length() == 0)
			return null;

		if (s.charAt(0) == '#') {
			// character reference
			if (s.length() == 1)
				return null;

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
		private Object value;

		/** whitespace preceding the token */
		private StringBuffer whitespace;

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
		 * Returns the token's value. This is an HtmlTag for tokens of type
		 * <code>TAG</code> and a StringBuffer for tokens of type
		 * <code>TEXT</code> and <code>COMMENT</code>. For tokens of type
		 * <code>EOF</code>, the value is <code>null</code>.
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * Returns the string representation of the token, including the
		 * preceding whitespace.
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
					sb.append("<!--");
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
					sb.append("-->");
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
	 * Portions (c) International Organization for Standardization 1986 Permission
	 * to copy in any form is granted for use with conforming SGML systems and
	 * applications as defined in ISO 8879, provided this notice is included in
	 * all copies.
	 * 
	 */
	static {
		entities = new HashMap<String, Character>();
		entities.put("nbsp", new Character('\240')); // no-break
		// space =
		// non-breaking
		// space
		entities.put("iexcl", new Character('\241')); // inverted
		// exclamation
		// mark
		entities.put("cent", new Character('\242')); // cent sign
		entities.put("pound", new Character('\243')); // pound
		// sign
		entities.put("curren", new Character('\244')); // currency
		// sign
		entities.put("yen", new Character('\245')); // yen sign =
		// yuan sign
		entities.put("brvbar", new Character('\246')); // broken
		// bar =
		// broken
		// vertical
		// bar
		entities.put("sect", new Character('\247')); // section
		// sign
		entities.put("uml", new Character('\250')); // diaeresis =
		// spacing
		// diaeresis
		entities.put("copy", new Character('\251')); // copyright
		// sign
		entities.put("ordf", new Character('\252')); // feminine
		// ordinal
		// indicator
		entities.put("laquo", new Character('\253')); // left-pointing
		// double
		// angle
		// quotation
		// mark =
		// left
		// pointing
		// guillemet
		entities.put("not", new Character('\254')); // not sign
		entities.put("shy", new Character('\255')); // soft hyphen =
		// discretionary
		// hyphen
		entities.put("reg", new Character('\256')); // registered
		// sign =
		// registered
		// trade mark
		// sign
		entities.put("macr", new Character('\257')); // macron =
		// spacing
		// macron =
		// overline
		// = APL
		// overbar
		entities.put("deg", new Character('\260')); // degree sign
		entities.put("plusmn", new Character('\261')); // plus-minus
		// sign =
		// plus-or-minus
		// sign
		entities.put("sup2", new Character('\262')); // superscript
		// two =
		// superscript
		// digit two
		// = squared
		entities.put("sup3", new Character('\263')); // superscript
		// three =
		// superscript
		// digit
		// three =
		// cubed
		entities.put("acute", new Character('\264')); // acute
		// accent =
		// spacing
		// acute
		entities.put("micro", new Character('\265')); // micro
		// sign
		entities.put("para", new Character('\266')); // pilcrow
		// sign =
		// paragraph
		// sign
		entities.put("middot", new Character('\267')); // middle
		// dot =
		// Georgian
		// comma =
		// Greek
		// middle
		// dot
		entities.put("cedil", new Character('\270')); // cedilla =
		// spacing
		// cedilla
		entities.put("sup1", new Character('\271')); // superscript
		// one =
		// superscript
		// digit one
		entities.put("ordm", new Character('\272')); // masculine
		// ordinal
		// indicator
		entities.put("raquo", new Character('\273')); // right-pointing
		// double
		// angle
		// quotation
		// mark =
		// right
		// pointing
		// guillemet
		entities.put("frac14", new Character('\274')); // vulgar
		// fraction
		// one
		// quarter =
		// fraction
		// one
		// quarter
		entities.put("frac12", new Character('\275')); // vulgar
		// fraction
		// one half
		// =
		// fraction
		// one half
		entities.put("frac34", new Character('\276')); // vulgar
		// fraction
		// three
		// quarters
		// =
		// fraction
		// three
		// quarters
		entities.put("iquest", new Character('\277')); // inverted
		// question
		// mark =
		// turned
		// question
		// mark
		entities.put("Agrave", new Character('\300')); // latin
		// capital
		// letter A
		// with
		// grave =
		// latin
		// capital
		// letter A
		// grave
		entities.put("Aacute", new Character('\301')); // latin
		// capital
		// letter A
		// with
		// acute
		entities.put("Acirc", new Character('\302')); // latin
		// capital
		// letter A
		// with
		// circumflex
		entities.put("Atilde", new Character('\303')); // latin
		// capital
		// letter A
		// with
		// tilde
		entities.put("Auml", new Character('\304')); // latin
		// capital
		// letter A
		// with
		// diaeresis
		entities.put("Aring", new Character('\305')); // latin
		// capital
		// letter A
		// with ring
		// above =
		// latin
		// capital
		// letter A
		// ring
		entities.put("AElig", new Character('\306')); // latin
		// capital
		// letter AE
		// = latin
		// capital
		// ligature
		// AE
		entities.put("Ccedil", new Character('\307')); // latin
		// capital
		// letter C
		// with
		// cedilla
		entities.put("Egrave", new Character('\310')); // latin
		// capital
		// letter E
		// with
		// grave
		entities.put("Eacute", new Character('\311')); // latin
		// capital
		// letter E
		// with
		// acute
		entities.put("Ecirc", new Character('\312')); // latin
		// capital
		// letter E
		// with
		// circumflex
		entities.put("Euml", new Character('\313')); // latin
		// capital
		// letter E
		// with
		// diaeresis
		entities.put("Igrave", new Character('\314')); // latin
		// capital
		// letter I
		// with
		// grave
		entities.put("Iacute", new Character('\315')); // latin
		// capital
		// letter I
		// with
		// acute
		entities.put("Icirc", new Character('\316')); // latin
		// capital
		// letter I
		// with
		// circumflex
		entities.put("Iuml", new Character('\317')); // latin
		// capital
		// letter I
		// with
		// diaeresis
		entities.put("ETH", new Character('\320')); // latin capital
		// letter ETH
		entities.put("Ntilde", new Character('\321')); // latin
		// capital
		// letter N
		// with
		// tilde
		entities.put("Ograve", new Character('\322')); // latin
		// capital
		// letter O
		// with
		// grave
		entities.put("Oacute", new Character('\323')); // latin
		// capital
		// letter O
		// with
		// acute
		entities.put("Ocirc", new Character('\324')); // latin
		// capital
		// letter O
		// with
		// circumflex
		entities.put("Otilde", new Character('\325')); // latin
		// capital
		// letter O
		// with
		// tilde
		entities.put("Ouml", new Character('\326')); // latin
		// capital
		// letter O
		// with
		// diaeresis
		entities.put("times", new Character('\327')); // multiplication
		// sign
		entities.put("Oslash", new Character('\330')); // latin
		// capital
		// letter O
		// with
		// stroke =
		// latin
		// capital
		// letter O
		// slash
		entities.put("Ugrave", new Character('\331')); // latin
		// capital
		// letter U
		// with
		// grave
		entities.put("Uacute", new Character('\332')); // latin
		// capital
		// letter U
		// with
		// acute
		entities.put("Ucirc", new Character('\333')); // latin
		// capital
		// letter U
		// with
		// circumflex
		entities.put("Uuml", new Character('\334')); // latin
		// capital
		// letter U
		// with
		// diaeresis
		entities.put("Yacute", new Character('\335')); // latin
		// capital
		// letter Y
		// with
		// acute
		entities.put("THORN", new Character('\336')); // latin
		// capital
		// letter
		// THORN
		entities.put("szlig", new Character('\337')); // latin
		// small
		// letter
		// sharp s =
		// ess-zed
		entities.put("agrave", new Character('\340')); // latin
		// small
		// letter a
		// with
		// grave =
		// latin
		// small
		// letter a
		// grave
		entities.put("aacute", new Character('\341')); // latin
		// small
		// letter a
		// with
		// acute
		entities.put("acirc", new Character('\342')); // latin
		// small
		// letter a
		// with
		// circumflex
		entities.put("atilde", new Character('\343')); // latin
		// small
		// letter a
		// with
		// tilde
		entities.put("auml", new Character('\344')); // latin
		// small
		// letter a
		// with
		// diaeresis
		entities.put("aring", new Character('\345')); // latin
		// small
		// letter a
		// with ring
		// above =
		// latin
		// small
		// letter a
		// ring
		entities.put("aelig", new Character('\346')); // latin
		// small
		// letter ae
		// = latin
		// small
		// ligature
		// ae
		entities.put("ccedil", new Character('\347')); // latin
		// small
		// letter c
		// with
		// cedilla
		entities.put("egrave", new Character('\350')); // latin
		// small
		// letter e
		// with
		// grave
		entities.put("eacute", new Character('\351')); // latin
		// small
		// letter e
		// with
		// acute
		entities.put("ecirc", new Character('\352')); // latin
		// small
		// letter e
		// with
		// circumflex
		entities.put("euml", new Character('\353')); // latin
		// small
		// letter e
		// with
		// diaeresis
		entities.put("igrave", new Character('\354')); // latin
		// small
		// letter i
		// with
		// grave
		entities.put("iacute", new Character('\355')); // latin
		// small
		// letter i
		// with
		// acute
		entities.put("icirc", new Character('\356')); // latin
		// small
		// letter i
		// with
		// circumflex
		entities.put("iuml", new Character('\357')); // latin
		// small
		// letter i
		// with
		// diaeresis
		entities.put("eth", new Character('\360')); // latin small
		// letter eth
		entities.put("ntilde", new Character('\361')); // latin
		// small
		// letter n
		// with
		// tilde
		entities.put("ograve", new Character('\362')); // latin
		// small
		// letter o
		// with
		// grave
		entities.put("oacute", new Character('\363')); // latin
		// small
		// letter o
		// with
		// acute
		entities.put("ocirc", new Character('\364')); // latin
		// small
		// letter o
		// with
		// circumflex
		entities.put("otilde", new Character('\365')); // latin
		// small
		// letter o
		// with
		// tilde
		entities.put("ouml", new Character('\366')); // latin
		// small
		// letter o
		// with
		// diaeresis
		entities.put("divide", new Character('\367')); // division
		// sign
		entities.put("oslash", new Character('\370')); // latin
		// small
		// letter o
		// with
		// stroke =
		// latin
		// small
		// letter o
		// slash
		entities.put("ugrave", new Character('\371')); // latin
		// small
		// letter u
		// with
		// grave
		entities.put("uacute", new Character('\372')); // latin
		// small
		// letter u
		// with
		// acute
		entities.put("ucirc", new Character('\373')); // latin
		// small
		// letter u
		// with
		// circumflex
		entities.put("uuml", new Character('\374')); // latin
		// small
		// letter u
		// with
		// diaeresis
		entities.put("yacute", new Character('\375')); // latin
		// small
		// letter y
		// with
		// acute
		entities.put("thorn", new Character('\376')); // latin
		// small
		// letter
		// thorn
		entities.put("yuml", new Character('\377')); // latin
		// small
		// letter y
		// with
		// diaeresis

		// Special characters
		entities.put("quot", new Character('\42')); // quotation
		// mark = APL
		// quote
		entities.put("amp", new Character('\46')); // ampersand
		entities.put("lt", new Character('\74')); // less-than
		// sign
		entities.put("gt", new Character('\76')); // greater-than
		// sign
		// Latin Extended-A
		entities.put("OElig", new Character('\u0152')); // latin
		// capital
		// ligature
		// OE
		entities.put("oelig", new Character('\u0153')); // latin
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
		entities.put("Scaron", new Character('\u0160')); // latin
		// capital
		// letter
		// S
		// with
		// caron
		entities.put("scaron", new Character('\u0161')); // latin
		// small
		// letter
		// s
		// with
		// caron
		entities.put("Yuml", new Character('\u0178')); // latin
		// capital
		// letter Y
		// with
		// diaeresis
		// Spacing Modifier Letters
		entities.put("circ", new Character('\u02c6')); // modifier
		// letter
		// circumflex
		// accent
		entities.put("tilde", new Character('\u02dc')); // small
		// tilde
		// General punctuation
		entities.put("ensp", new Character('\u2002')); // en space
		entities.put("emsp", new Character('\u2003')); // em space
		entities.put("thinsp", new Character('\u2009')); // thin
		// space
		entities.put("zwnj", new Character('\u200c')); // zero
		// width
		// non-joiner
		entities.put("zwj", new Character('\u200d')); // zero
		// width
		// joiner
		entities.put("lrm", new Character('\u200e')); // left-to-right
		// mark
		entities.put("rlm", new Character('\u200f')); // right-to-left
		// mark
		entities.put("ndash", new Character('\u2013')); // en dash
		entities.put("mdash", new Character('\u2014')); // em dash
		entities.put("lsquo", new Character('\u2018')); // left
		// single
		// quotation
		// mark
		entities.put("rsquo", new Character('\u2019')); // right
		// single
		// quotation
		// mark
		entities.put("sbquo", new Character('\u201a')); // single
		// low-9
		// quotation
		// mark
		entities.put("ldquo", new Character('\u201c')); // left
		// double
		// quotation
		// mark
		entities.put("rdquo", new Character('\u201d')); // right
		// double
		// quotation
		// mark
		entities.put("bdquo", new Character('\u201e')); // double
		// low-9
		// quotation
		// mark
		entities.put("dagger", new Character('\u2020')); // dagger
		entities.put("Dagger", new Character('\u2021')); // double
		// dagger
		entities.put("permil", new Character('\u2030')); // per
		// mille
		// sign
		entities.put("lsaquo", new Character('\u2039')); // single
		// left-pointing
		// angle
		// quotation
		// mark,
		// not
		// yet
		// standardized
		entities.put("rsaquo", new Character('\u203a')); // single
		// right-pointing
		// angle
		// quotation
		// mark,
		// not
		// yet
		// standardized
		entities.put("euro", new Character('\u20ac')); // euro sign
	}
}
