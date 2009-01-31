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

package org.eclipse.mylyn.internal.wikitext.tasks.ui.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.Preferences;
import org.eclipse.mylyn.internal.wikitext.ui.util.css.editor.CssConfiguration;
import org.eclipse.mylyn.internal.wikitext.ui.util.css.editor.CssPartitioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class MarkupViewerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private SourceViewer sourceViewer;

	private Colors colorRegistry;

	public MarkupViewerPreferencePage() {
		super(Messages.getString("MarkupViewerPreferencePage.0")); //$NON-NLS-1$
	}

	@Override
	protected Control createContents(Composite parent) {
		colorRegistry = new Colors();

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayoutFactory.fillDefaults().margins(5, 5).numColumns(1).applyTo(composite);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.getString("MarkupViewerPreferencePage.1")); //$NON-NLS-1$
		GridDataFactory.fillDefaults().applyTo(label);

		Preferences preferences = WikiTextUiPlugin.getDefault().getPreferences();

		Composite viewerContainer = new Composite(composite, SWT.BORDER);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(1).applyTo(viewerContainer);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewerContainer);
		sourceViewer = new SourceViewer(viewerContainer, new VerticalRuler(0), SWT.WRAP | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(sourceViewer.getControl());

		Document document = new Document(preferences.getMarkupViewerCss());
		CssPartitioner partitioner = new CssPartitioner();
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		sourceViewer.setDocument(document);

		CssConfiguration configuration = new CssConfiguration(colorRegistry);
		sourceViewer.configure(configuration);

		return composite;
	}

	@Override
	public void dispose() {
		if (colorRegistry != null) {
			colorRegistry.dispose();
			colorRegistry = null;
		}
		super.dispose();
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		Preferences preferences = WikiTextUiPlugin.getDefault().getPreferences().clone();
		preferences.setMarkupViewerCss(sourceViewer.getDocument().get());
		preferences.save(WikiTextUiPlugin.getDefault().getPreferenceStore(), false);
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		sourceViewer.getDocument().set(new Preferences().getMarkupViewerCss());
		super.performDefaults();
	}

	private static class Colors extends ColorRegistry {
		public Colors() {
			super(Display.getCurrent(), false);
		}

		public void dispose() {
			super.clearCaches();
		}
	}
}
