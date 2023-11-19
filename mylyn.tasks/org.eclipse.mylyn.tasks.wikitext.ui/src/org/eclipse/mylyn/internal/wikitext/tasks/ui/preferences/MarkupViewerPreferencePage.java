/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.tasks.ui.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.mylyn.internal.wikitext.tasks.ui.WikiTextTasksUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.Preferences;
import org.eclipse.mylyn.internal.wikitext.ui.util.css.editor.CssConfiguration;
import org.eclipse.mylyn.internal.wikitext.ui.util.css.editor.CssPartitioner;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.HtmlTextPresentationParser;
import org.eclipse.mylyn.wikitext.parser.css.CssParser;
import org.eclipse.mylyn.wikitext.ui.viewer.HtmlViewer;
import org.eclipse.mylyn.wikitext.ui.viewer.HtmlViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.progress.UIJob;
import org.xml.sax.SAXException;

/**
 * A preference page that allows for changing the {@link Preferences#getMarkupViewerCss()}
 *
 * @author David Green
 * @author Hiroyuki Inaba fix for bug 265079: Dialog font not apply WikiText preference pages
 */
public class MarkupViewerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String WHITE = "white"; //$NON-NLS-1$

	private SourceViewer sourceViewer;

	private Colors colorRegistry;

	private HtmlViewer previewViewer;

	private UIJob previewUpdateJob;

	private HtmlViewerConfiguration htmlViewerConfiguration;

	public MarkupViewerPreferencePage() {
		super(Messages.MarkupViewerPreferencePage_appearance);
	}

	@Override
	protected Control createContents(Composite parent) {
		colorRegistry = new Colors();
		colorRegistry.put(WHITE, new RGB(255, 255, 255));

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayoutFactory.fillDefaults().margins(5, 5).numColumns(1).applyTo(composite);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.MarkupViewerPreferencePage_appearanceInfo);
		GridDataFactory.fillDefaults().applyTo(label);

		Preferences preferences = WikiTextUiPlugin.getDefault().getPreferences();

		Composite viewerContainer = new Composite(composite, SWT.BORDER);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(1).applyTo(viewerContainer);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewerContainer);
		{
			sourceViewer = new SourceViewer(viewerContainer, new VerticalRuler(0), SWT.WRAP | SWT.V_SCROLL);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(sourceViewer.getControl());

			Document document = new Document(preferences.getMarkupViewerCss());
			CssPartitioner partitioner = new CssPartitioner();
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
			sourceViewer.setDocument(document);

			CssConfiguration configuration = new CssConfiguration(colorRegistry);
			sourceViewer.configure(configuration);
		}

		label = new Label(composite, SWT.WRAP);
		label.setText(Messages.MarkupViewerPreferencePage_preview);
		GridDataFactory.fillDefaults().applyTo(label);

		applyDialogFont(composite);

		Composite previewViewerContainer = new Composite(composite, SWT.BORDER);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(1).applyTo(previewViewerContainer);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(previewViewerContainer);
		{
			previewViewer = new HtmlViewer(previewViewerContainer, new VerticalRuler(0), SWT.WRAP | SWT.V_SCROLL);
			previewViewer.getTextWidget().setBackground(colorRegistry.get(WHITE));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(previewViewer.getControl());

			htmlViewerConfiguration = new HtmlViewerConfiguration(previewViewer);
			previewViewer.configure(htmlViewerConfiguration);
			previewViewer.getTextWidget().setEditable(false);
			previewViewer.setStylesheet(preferences.getStylesheet());

			if (JFaceResources.getFontRegistry().hasValueFor(WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_DEFAULT_FONT)) {
				previewViewer.getTextWidget()
						.setFont(
								JFaceResources.getFontRegistry()
										.get(WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_DEFAULT_FONT));
			}
			if (JFaceResources.getFontRegistry().hasValueFor(WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_MONOSPACE_FONT)) {
				previewViewer.setDefaultMonospaceFont(
						JFaceResources.getFontRegistry().get(WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_MONOSPACE_FONT));
			}

			previewViewer.setHtml(createPreviewHtml());

			sourceViewer.getDocument().addDocumentListener(new IDocumentListener() {
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
				}

				@Override
				public void documentChanged(DocumentEvent event) {
					schedulePreviewUpdate();
				}
			});
		}
		return composite;
	}

	private String createPreviewHtml() {
		return Messages.MarkupViewerPreferencePage_previewHtml;
	}

	private void updatePreview() {
		TextPresentation textPresentation = new TextPresentation();
		HtmlTextPresentationParser parser = new HtmlTextPresentationParser();
		parser.setDefaultFont(previewViewer.getTextWidget().getFont());
		parser.setAnnotationModel(previewViewer.getAnnotationModel());
		parser.setPresentation(textPresentation);
		parser.setStylesheet(new CssParser().parse(sourceViewer.getDocument().get()));

		GC gc = new GC(previewViewer.getTextWidget());
		try {
			parser.setGC(gc);

			parser.parse(createPreviewHtml());
		} catch (SAXException | IOException e) {
			throw new IllegalStateException(e);
		} finally {
			gc.dispose();
		}
		htmlViewerConfiguration.setTextPresentation(textPresentation);
		previewViewer.changeTextPresentation(textPresentation, true);
	}

	@Override
	public void dispose() {
		if (colorRegistry != null) {
			colorRegistry.dispose();
			colorRegistry = null;
		}
		super.dispose();
	}

	@Override
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
		schedulePreviewUpdate();
	}

	private void schedulePreviewUpdate() {
		if (previewUpdateJob != null) {
			previewUpdateJob.cancel();
		}
		previewUpdateJob = new UIJob(Display.getCurrent(), Messages.MarkupViewerPreferencePage_updatePreview) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				previewUpdateJob = null;
				updatePreview();
				return Status.OK_STATUS;
			}
		};
		previewUpdateJob.schedule(400L);
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
