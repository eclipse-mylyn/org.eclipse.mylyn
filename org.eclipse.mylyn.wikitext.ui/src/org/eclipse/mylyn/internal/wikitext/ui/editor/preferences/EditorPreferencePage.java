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

import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.CssStyleManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
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
		setDescription(Messages.getString("EditorPreferencePage.0")); //$NON-NLS-1$
	}

	@Override
	public Control createContents(Composite parent) {
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL) {
			@Override
			public Point computeSize(int hint, int hint2, boolean changed) {
				return new Point(64, 64);
			}
		};
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite body = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(body);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(1).applyTo(body);

		Control contents = super.createContents(body);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(contents);

		scrolledComposite.setMinSize(body.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
		return scrolledComposite;
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		Preferences prefs = new Preferences();

		Layout fieldEditorParentLayout = getFieldEditorParent().getLayout();
		if (fieldEditorParentLayout instanceof GridLayout) {
			GridLayout layout = (GridLayout) fieldEditorParentLayout;
			layout.marginRight = 5;
		}

		Group group = new Group(getFieldEditorParent(), SWT.NULL);
		group.setText(Messages.getString("EditorPreferencePage.1")); //$NON-NLS-1$
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		CssStyleManager cssStyleManager = new CssStyleManager(getFont());

		for (Map.Entry<String, String> ent : prefs.getCssByBlockModifierType().entrySet()) {
			String preferenceKey = Preferences.toPreferenceKey(ent.getKey(), true);
			addField(new CssStyleFieldEditor(cssStyleManager, preferenceKey, ent.getKey(), group));
		}
		// bug 260427
		Layout layout = group.getLayout();
		if (layout instanceof GridLayout) {
			((GridLayout) layout).marginWidth = 5;
		}

		group = new Group(getFieldEditorParent(), SWT.NULL);
		group.setText(Messages.getString("EditorPreferencePage.2")); //$NON-NLS-1$
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		for (Map.Entry<String, String> ent : prefs.getCssByPhraseModifierType().entrySet()) {
			String preferenceKey = Preferences.toPreferenceKey(ent.getKey(), false);
			addField(new CssStyleFieldEditor(cssStyleManager, preferenceKey, ent.getKey(), group));
		}
		// bug 260427
		layout = group.getLayout();
		if (layout instanceof GridLayout) {
			((GridLayout) layout).marginWidth = 5;
		}

		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
				WikiTextUiPlugin.getDefault().getPluginId() + ".preferences"); //$NON-NLS-1$
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