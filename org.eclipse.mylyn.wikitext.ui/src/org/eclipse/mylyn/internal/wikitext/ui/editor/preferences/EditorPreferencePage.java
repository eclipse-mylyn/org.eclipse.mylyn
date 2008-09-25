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

import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main
 * plug-in class. That way, preferences can be accessed directly via the preference store.
 * 
 * @author David Green
 */
public class EditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EditorPreferencePage() {
		super(GRID);
		setPreferenceStore(WikiTextUiPlugin.getDefault().getPreferenceStore());
		setDescription("WikiText editor preferences for syntax highlighting.  CSS is used to control font size, color and style.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		Preferences prefs = new Preferences();

		Group group = new Group(getFieldEditorParent(), SWT.NULL);
		group.setText("Block Modifiers");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		for (Map.Entry<String, String> ent : prefs.getCssByBlockModifierType().entrySet()) {
			String preferenceKey = Preferences.toPreferenceKey(ent.getKey(), true);
			addField(new StringFieldEditor(preferenceKey, ent.getKey(), group));
		}

		group = new Group(getFieldEditorParent(), SWT.NULL);
		group.setText("Phrase Modifiers");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		for (Map.Entry<String, String> ent : prefs.getCssByPhraseModifierType().entrySet()) {
			String preferenceKey = Preferences.toPreferenceKey(ent.getKey(), false);
			addField(new StringFieldEditor(preferenceKey, ent.getKey(), group));
		}
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
				WikiTextUiPlugin.getDefault().getPluginId() + ".preferences");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}