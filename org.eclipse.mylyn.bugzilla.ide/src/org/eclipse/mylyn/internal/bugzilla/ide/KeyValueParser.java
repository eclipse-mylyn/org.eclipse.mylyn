/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steffen Pingel
 */
public class KeyValueParser {

	private final String text;

	private enum Mode {
		KEY, VALUE
	};

	private Map<String, String> pairs;

	private StringBuilder token;

	private boolean escaping;

	private Mode mode;
	
	private String key;

	public KeyValueParser(String text) {
		this.text = text;

	}

	public Map<String, String> parse() throws ParseException {
		init();

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
			case '\\':
				if (escaping) {
					append(c);
				} else {
					escaping = true;
				}
				break;
			case '=':
				if (escaping) {
					append(c);
				} else if (mode == Mode.VALUE) {
					throw new ParseException("Unexpected character '='", i);
				} else {
					key = token.toString();
					token.setLength(0);
					mode = Mode.VALUE;
				}
				break;
			case ';':
				if (escaping) {
					append(c);
				} else if (mode == Mode.KEY) {
					throw new ParseException("Unexpected character ';'", i);
				} else {
					pairs.put(key, token.toString());
					token.setLength(0);
					mode = Mode.KEY;
				}
				break;
			default:
				append(c);
			}
		}
		
		if (mode == Mode.KEY) {
			throw new ParseException("Unexpected end of input", text.length());
		}
		
		pairs.put(key, token.toString());
		
		return Collections.unmodifiableMap(pairs);
	}

	private void append(char c) {
		escaping = false;
		token.append(c);
	}

	private void init() {
		pairs = new HashMap<String, String>();
		token = new StringBuilder();
		mode = Mode.KEY;
		escaping = false;
	}
	
}
