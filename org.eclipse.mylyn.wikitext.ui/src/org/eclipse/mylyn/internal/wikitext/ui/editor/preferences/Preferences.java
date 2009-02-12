/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor.preferences;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.ui.util.css.Stylesheet;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.HtmlTextPresentationParser;

/**
 * @see WikiTextUiPlugin#getPreferences()
 * 
 * @author David Green
 */
public class Preferences implements Cloneable {

	private static final String KEY_MARKUP_VIEWER_CSS = "preview.css"; //$NON-NLS-1$

	private static final String KEY_EDITOR_FOLDING = "editorFolding"; //$NON-NLS-1$

	private static final Pattern BAD_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]"); //$NON-NLS-1$

	public static final String PHRASE_CODE = "@code@"; //$NON-NLS-1$

	public static final String PHRASE_SPAN = "%span%"; //$NON-NLS-1$

	public static final String PHRASE_SUBSCRIPT = "~subscript~"; //$NON-NLS-1$

	public static final String PHRASE_SUPERSCRIPT = "^superscript^"; //$NON-NLS-1$

	public static final String PHRASE_INSERTED_TEXT = "+inserted text+"; //$NON-NLS-1$

	public static final String PHRASE_DELETED_TEXT = "-deleted text-"; //$NON-NLS-1$

	public static final String PHRASE_CITATION = "??citation??"; //$NON-NLS-1$

	public static final String PHRASE_BOLD = "**bold**"; //$NON-NLS-1$

	public static final String PHRASE_ITALIC = "__italic__"; //$NON-NLS-1$

	public static final String PHRASE_STRONG = "*strong*"; //$NON-NLS-1$

	public static final String PHRASE_EMPHASIS = "_emphasis_"; //$NON-NLS-1$

	public static final String PHRASE_MONOSPACE = "monospace"; //$NON-NLS-1$

	public static final String PHRASE_UNDERLINED = "underlined"; //$NON-NLS-1$

	public static final String BLOCK_QUOTE = "bq."; //$NON-NLS-1$

	public static final String BLOCK_PRE = "pre."; //$NON-NLS-1$

	public static final String BLOCK_BC = "bc."; //$NON-NLS-1$

	public static final String BLOCK_H6 = "h6."; //$NON-NLS-1$

	public static final String BLOCK_H5 = "h5."; //$NON-NLS-1$

	public static final String BLOCK_H4 = "h4."; //$NON-NLS-1$

	public static final String BLOCK_H3 = "h3."; //$NON-NLS-1$

	public static final String BLOCK_H2 = "h2."; //$NON-NLS-1$

	public static final String BLOCK_H1 = "h1."; //$NON-NLS-1$

	/**
	 * heading preferences key indexed by level (0 is null, 1 is {@link #BLOCK_H1}, etc.)
	 */
	public static final String[] HEADING_PREFERENCES = new String[] { null, BLOCK_H1, BLOCK_H2, BLOCK_H3, BLOCK_H4,
			BLOCK_H5, BLOCK_H6 };

	private Map<String, String> cssByBlockModifierType = new LinkedHashMap<String, String>();
	{
		cssByBlockModifierType.put(BLOCK_H1, "font-size: 130%; font-weight: bold;"); //$NON-NLS-1$
		cssByBlockModifierType.put(BLOCK_H2, "font-size: 125%; font-weight: bold;"); //$NON-NLS-1$
		cssByBlockModifierType.put(BLOCK_H3, "font-size: 110%; font-weight: bold;"); //$NON-NLS-1$
		cssByBlockModifierType.put(BLOCK_H4, "font-weight: bold;"); //$NON-NLS-1$
		cssByBlockModifierType.put(BLOCK_H5, "font-size: 83%; font-weight: bold;"); //$NON-NLS-1$
		cssByBlockModifierType.put(BLOCK_H6, "font-size: 75%; font-weight: bold;"); //$NON-NLS-1$
		cssByBlockModifierType.put(BLOCK_BC, "font-family: monospace; color: #4444CC;"); //$NON-NLS-1$
		cssByBlockModifierType.put(BLOCK_PRE, "font-family: monospace; color: #4444CC;"); //$NON-NLS-1$
		cssByBlockModifierType.put(BLOCK_QUOTE, "color: rgb(38,86,145);"); //$NON-NLS-1$
	}

	private Map<String, String> cssByPhraseModifierType = new LinkedHashMap<String, String>();
	{

		cssByPhraseModifierType.put(PHRASE_EMPHASIS, "font-style: italic;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_STRONG, "font-weight: bold;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_ITALIC, "font-style: italic;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_BOLD, "font-weight: bold;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_CITATION, "font-style: italic;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_DELETED_TEXT, "text-decoration: line-through;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_INSERTED_TEXT, "text-decoration: underline;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_SUPERSCRIPT, "font-size: smaller; vertical-align: super;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_SUBSCRIPT, "font-size: smaller; vertical-align: sub;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_SPAN, ""); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_CODE, "font-family: monospace; color: #4444CC;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_MONOSPACE, "font-family: monospace;"); //$NON-NLS-1$
		cssByPhraseModifierType.put(PHRASE_UNDERLINED, "text-decoration: underline;"); //$NON-NLS-1$
	}

	private boolean editorFolding = true;

	private String markupViewerCss;

	private Stylesheet stylesheet;

	private boolean mutable = true;

	public Map<String, String> getCssByBlockModifierType() {
		return cssByBlockModifierType;
	}

	public Map<String, String> getCssByPhraseModifierType() {
		return cssByPhraseModifierType;
	}

	/**
	 * indicate if editor folding is enabled
	 */
	public boolean isEditorFolding() {
		return editorFolding;
	}

	/**
	 * indicate if editor folding is enabled
	 */
	public void setEditorFolding(boolean editorFolding) {
		if (!mutable) {
			throw new UnsupportedOperationException();
		}
		this.editorFolding = editorFolding;
	}

	/**
	 * get the CSS content used to render the markup viewer
	 */
	public String getMarkupViewerCss() {
		if (markupViewerCss == null) {
			markupViewerCss = getDefaultMarkupViewerCss();
		}
		return markupViewerCss;
	}

	/**
	 * set the CSS content used to render the markup viewer
	 */
	public void setMarkupViewerCss(String markupViewerCss) {
		if (!mutable) {
			throw new UnsupportedOperationException();
		}
		this.markupViewerCss = markupViewerCss;
		stylesheet = null;
	}

	public Stylesheet getStylesheet() {
		if (stylesheet == null) {
			stylesheet = new CssParser().parse(getMarkupViewerCss());
		}
		return stylesheet;
	}

	/**
	 * indicate if this preferences is mutable
	 * 
	 * @see #makeImmutable()
	 */
	public boolean isMutable() {
		return mutable;
	}

	/**
	 * @see #isMutable()
	 */
	public void makeImmutable() {
		if (mutable) {
			mutable = false;
			cssByBlockModifierType = Collections.unmodifiableMap(cssByBlockModifierType);
			cssByPhraseModifierType = Collections.unmodifiableMap(cssByPhraseModifierType);
		}
	}

	/**
	 * Save the settings to the given store
	 * 
	 * @param store
	 *            the store to which the settings should be saved
	 * @param asDefault
	 *            if true, then the settings are saved as defaults.
	 */
	public void save(IPreferenceStore store, boolean asDefault) {
		for (Map.Entry<String, String> ent : cssByBlockModifierType.entrySet()) {
			String propKey = toPreferenceKey(ent.getKey(), true);
			if (asDefault) {
				store.setDefault(propKey, ent.getValue());
			} else {
				store.setValue(propKey, ent.getValue());
			}
		}
		for (Map.Entry<String, String> ent : cssByPhraseModifierType.entrySet()) {
			String propKey = toPreferenceKey(ent.getKey(), false);
			if (asDefault) {
				store.setDefault(propKey, ent.getValue());
			} else {
				store.setValue(propKey, ent.getValue());
			}
		}
		if (asDefault) {
			store.setDefault(KEY_EDITOR_FOLDING, editorFolding);
			store.setDefault(KEY_MARKUP_VIEWER_CSS, getDefaultMarkupViewerCss());
		} else {
			store.setValue(KEY_EDITOR_FOLDING, editorFolding);
			store.setValue(KEY_MARKUP_VIEWER_CSS, markupViewerCss);
		}
	}

	public static String toPreferenceKey(String key, boolean block) {
		String propKey = (block ? "block-" : "phrase-") + BAD_CHAR_PATTERN.matcher(key).replaceAll(""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return propKey;
	}

	public void load(IPreferenceStore store) {
		if (!mutable) {
			throw new UnsupportedOperationException();
		}
		for (Map.Entry<String, String> ent : cssByBlockModifierType.entrySet()) {
			String propKey = toPreferenceKey(ent.getKey(), true);

			String value = store.getString(propKey);
			if (value != null) {
				ent.setValue(value);
			}
		}
		for (Map.Entry<String, String> ent : cssByPhraseModifierType.entrySet()) {
			String propKey = toPreferenceKey(ent.getKey(), false);
			String value = store.getString(propKey);
			if (value != null) {
				ent.setValue(value);
			}
		}
		editorFolding = store.getBoolean(KEY_EDITOR_FOLDING);
		markupViewerCss = store.getString(KEY_MARKUP_VIEWER_CSS);
		if (isEmpty(markupViewerCss)) {
			markupViewerCss = getDefaultMarkupViewerCss();
		}
		stylesheet = null;
	}

	private String getDefaultMarkupViewerCss() {
		try {
			return readFully(HtmlTextPresentationParser.getDefaultStylesheetContent());
		} catch (IOException e) {
			WikiTextUiPlugin.getDefault().log(e);
			return ""; //$NON-NLS-1$
		}
	}

	private String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			int c;
			while ((c = reader.read()) != -1) {
				writer.write(c);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	private boolean isEmpty(String text) {
		if (text == null || text.length() == 0) {
			return true;
		}
		final int length = text.length();
		for (int x = 0; x < length; ++x) {
			char c = text.charAt(x);
			if (!Character.isWhitespace(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * clone the preferences, returning a mutable copy.
	 */
	@Override
	public Preferences clone() {
		try {
			Preferences copy = (Preferences) super.clone();
			copy.mutable = true;
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}
}
