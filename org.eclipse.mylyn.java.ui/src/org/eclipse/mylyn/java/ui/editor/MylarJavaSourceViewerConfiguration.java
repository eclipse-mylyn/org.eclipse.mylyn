/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 14, 2005
 */
package org.eclipse.mylar.java.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.ContentAssistPreference;
import org.eclipse.jdt.internal.ui.text.java.ContentAssistProcessor;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProcessor;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocCompletionProcessor;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Installs Mylar content assist and hyperlink detection
 * 
 * @author Mik Kersten
 */
public class MylarJavaSourceViewerConfiguration extends JavaSourceViewerConfiguration {

	private List<AbstractMylarHyperlinkDetector> hyperlinkDetectors;

	private boolean extensionsRead = false;

	public static final String JAVA_EDITOR_CONTRIBUTOR_EXTENSION_POINT_ID = "org.eclipse.mylar.java.javaEditorContributor";

	public static final String JAVA_HYPERLINK_DETECTOR_ELEMENT = "hyperlinkDetector";

	public static final String HYPERLINK_DETECTOR_CLASS = "class";

	public MylarJavaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}
	
	/**
	 * Copied from super
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		if (getEditor() != null) {

			ContentAssistant assistant= new ContentAssistant();
			assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

			assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$

			IContentAssistProcessor javaProcessor= new MylarJavaCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
			assistant.setContentAssistProcessor(javaProcessor, IDocument.DEFAULT_CONTENT_TYPE);

			ContentAssistProcessor singleLineProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
			assistant.setContentAssistProcessor(singleLineProcessor, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);

			ContentAssistProcessor stringProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_STRING);
			assistant.setContentAssistProcessor(stringProcessor, IJavaPartitions.JAVA_STRING);
			
			ContentAssistProcessor multiLineProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
			assistant.setContentAssistProcessor(multiLineProcessor, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

			ContentAssistProcessor javadocProcessor= new JavadocCompletionProcessor(getEditor(), assistant);
			assistant.setContentAssistProcessor(javadocProcessor, IJavaPartitions.JAVA_DOC);

			ContentAssistPreference.configure(assistant, fPreferenceStore);

			assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
			
			return assistant;
		}

		return null;
	}

    protected IDialogSettings getSettings(String sectionName) {
        IDialogSettings settings= JavaPlugin.getDefault().getDialogSettings().getSection(sectionName);
        if (settings == null)
            settings= JavaPlugin.getDefault().getDialogSettings().addNewSection(sectionName);
        
        return settings;
    }
	
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (!fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;

		IHyperlinkDetector[] inheritedDetectors = super.getHyperlinkDetectors(sourceViewer);

		if (super.getEditor() == null)
			return inheritedDetectors;

		readDetectorsExtension();

		if (hyperlinkDetectors == null)
			return inheritedDetectors;

		List<IHyperlinkDetector> detectors = new ArrayList<IHyperlinkDetector>();
		detectors.addAll(Arrays.asList(inheritedDetectors));
		detectors.addAll(hyperlinkDetectors);
		return detectors.toArray(new IHyperlinkDetector[hyperlinkDetectors.size()]);
	}

	private void readDetectorsExtension() {
		if (!extensionsRead) {
			hyperlinkDetectors = new ArrayList<AbstractMylarHyperlinkDetector>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint(JAVA_EDITOR_CONTRIBUTOR_EXTENSION_POINT_ID);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if (elements[j].getName().compareTo(JAVA_HYPERLINK_DETECTOR_ELEMENT) == 0) {
						try {
							Object detector = elements[j].createExecutableExtension(HYPERLINK_DETECTOR_CLASS);
							if (detector instanceof AbstractMylarHyperlinkDetector) {
								((AbstractMylarHyperlinkDetector) detector).setEditor(super.getEditor());
								hyperlinkDetectors.add((AbstractMylarHyperlinkDetector) detector);
							} else {
								MylarStatusHandler.log("Could not load hyperlink detector: "
										+ detector.getClass().getCanonicalName() + " must implement "
										+ AbstractMylarHyperlinkDetector.class.getCanonicalName(), this);
							}
						} catch (CoreException e) {
							MylarStatusHandler.log(e, "Could not load java editor contributor");
						}
					}
				}
			}
			extensionsRead = true;
		}
	}
}
