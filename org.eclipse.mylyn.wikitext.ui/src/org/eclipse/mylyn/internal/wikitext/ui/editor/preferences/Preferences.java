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
package org.eclipse.mylyn.internal.wikitext.ui.editor.preferences;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;

// FIXME: move to internal

/**
 *
 *
 * @author David Green
 */
public class Preferences {

	private static final Pattern BAD_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
	public static final String PHRASE_CODE = "@code@";
	public static final String PHRASE_SPAN = "%span%";
	public static final String PHRASE_SUBSCRIPT = "~subscript~";
	public static final String PHRASE_SUPERSCRIPT = "^superscript^";
	public static final String PHRASE_INSERTED_TEXT = "+inserted text+";
	public static final String PHRASE_DELETED_TEXT = "-deleted text-";
	public static final String PHRASE_CITATION = "??citation??";
	public static final String PHRASE_BOLD = "**bold**";
	public static final String PHRASE_ITALIC = "__italic__";
	public static final String PHRASE_STRONG = "*strong*";
	public static final String PHRASE_EMPHASIS = "_emphasis_";
	public static final String PHRASE_MONOSPACE = "monospace";
	public static final String PHRASE_UNDERLINED = "underlined";

	public static final String BLOCK_QUOTE = "bq.";
	public static final String BLOCK_PRE = "pre.";
	public static final String BLOCK_BC = "bc.";
	public static final String BLOCK_H6 = "h6.";
	public static final String BLOCK_H5 = "h5.";
	public static final String BLOCK_H4 = "h4.";
	public static final String BLOCK_H3 = "h3.";
	public static final String BLOCK_H2 = "h2.";
	public static final String BLOCK_H1 = "h1.";

	/**
	 * heading preferences key indexed by level (0 is null, 1 is {@link #BLOCK_H1}, etc.)
	 */
	public static final String[] HEADING_PREFERENCES = new String[] {
		null,
		BLOCK_H1,
		BLOCK_H2,
		BLOCK_H3,
		BLOCK_H4,
		BLOCK_H5,
		BLOCK_H6
	};

	private Map<String,String> cssByBlockModifierType = new LinkedHashMap<String, String>();
	{
		cssByBlockModifierType.put(BLOCK_H1, "font-size: 150%; font-weight: bold;");
		cssByBlockModifierType.put(BLOCK_H2, "font-size: 125%; font-weight: bold;");
		cssByBlockModifierType.put(BLOCK_H3, "font-size: 110%; font-weight: bold;");
		cssByBlockModifierType.put(BLOCK_H4, "font-weight: bold;");
		cssByBlockModifierType.put(BLOCK_H5, "font-size: 83%; font-weight: bold;");
		cssByBlockModifierType.put(BLOCK_H6, "font-size: 75%; font-weight: bold;");
		cssByBlockModifierType.put(BLOCK_BC, "font-family: monospace; color: Blue;");
		cssByBlockModifierType.put(BLOCK_PRE, "font-family: monospace; color: Blue;");
		cssByBlockModifierType.put(BLOCK_QUOTE, "font-family: monospace; color: rgb(38,86,145);");
	}
	private Map<String,String> cssByPhraseModifierType = new LinkedHashMap<String, String>();
	{

		cssByPhraseModifierType.put(PHRASE_EMPHASIS, "font-style: italic;");
		cssByPhraseModifierType.put(PHRASE_STRONG, "font-weight: bold;");
		cssByPhraseModifierType.put(PHRASE_ITALIC, "font-style: italic;");
		cssByPhraseModifierType.put(PHRASE_BOLD, "font-weight: bold;");
		cssByPhraseModifierType.put(PHRASE_CITATION, "font-style: italic;");
		cssByPhraseModifierType.put(PHRASE_DELETED_TEXT, "text-decoration: line-through;");
		cssByPhraseModifierType.put(PHRASE_INSERTED_TEXT, "text-decoration: underline;");
		cssByPhraseModifierType.put(PHRASE_SUPERSCRIPT, "font-size: smaller; vertical-align: super;");
		cssByPhraseModifierType.put(PHRASE_SUBSCRIPT, "font-size: smaller; vertical-align: sub;");
		cssByPhraseModifierType.put(PHRASE_SPAN, "");
		cssByPhraseModifierType.put(PHRASE_CODE, "font-family: monospace; color: Blue;");
		cssByPhraseModifierType.put(PHRASE_MONOSPACE, "font-family: monospace;");
		cssByPhraseModifierType.put(PHRASE_UNDERLINED, "text-decoration: underline;");
	}

	public Map<String, String> getCssByBlockModifierType() {
		return cssByBlockModifierType;
	}

	public Map<String, String> getCssByPhraseModifierType() {
		return cssByPhraseModifierType;
	}

	/**
	 * Save the settings to the given store
	 * @param store the store to which the settings should be saved
	 * @param asDefault if true, then the settings are saved as defaults.
	 */
	public void save(IPreferenceStore store,boolean asDefault) {
		for (Map.Entry<String,String> ent: cssByBlockModifierType.entrySet()) {
			String propKey = toPreferenceKey(ent.getKey(), true);
			if (asDefault) {
				store.setDefault(propKey, ent.getValue());
			} else {
				store.setValue(propKey, ent.getValue());
			}
		}
		for (Map.Entry<String,String> ent: cssByPhraseModifierType.entrySet()) {
			String propKey = toPreferenceKey(ent.getKey(), false);
			if (asDefault) {
				store.setDefault(propKey, ent.getValue());
			} else {
				store.setValue(propKey, ent.getValue());
			}
		}
	}

	public static String toPreferenceKey(String key,boolean block) {
		String propKey = (block?"block-":"phrase-")+BAD_CHAR_PATTERN.matcher(key).replaceAll("");
		return propKey;
	}

	public void load(IPreferenceStore store) {
		for (Map.Entry<String,String> ent: cssByBlockModifierType.entrySet()) {
			String propKey = toPreferenceKey(ent.getKey(), true);

			String value = store.getString(propKey);
			if (value != null) {
				ent.setValue(value);
			}
		}
		for (Map.Entry<String,String> ent: cssByPhraseModifierType.entrySet()) {
			String propKey = toPreferenceKey(ent.getKey(), false);
			String value = store.getString(propKey);
			if (value != null) {
				ent.setValue(value);
			}
		}
	}
}
