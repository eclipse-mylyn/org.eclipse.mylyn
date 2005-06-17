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

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.CompoundContentAssistProcessor;
import org.eclipse.jdt.internal.ui.text.ContentAssistPreference;
import org.eclipse.jdt.internal.ui.text.javadoc.JavaDocCompletionProcessor;
import org.eclipse.jdt.internal.ui.text.spelling.WordCompletionProcessor;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class MylarJavaSourceViewerConfiguration extends
        JavaSourceViewerConfiguration {

    public MylarJavaSourceViewerConfiguration(IColorManager colorManager,
            IPreferenceStore preferenceStore, ITextEditor editor,
            String partitioning) {
        super(colorManager, preferenceStore, editor, partitioning);
        
    }

    
    /*
     * Copied from: @see JavaSourceViewerConfiguration#getContentAssistant(ISourceViewer)
     */
    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        if (getEditor() != null) {
            
            ContentAssistant assistant= new ContentAssistant();
            assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
            
            assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$
            
            IContentAssistProcessor javaProcessor= new MylarJavaCompletionProcessor(getEditor());
            assistant.setContentAssistProcessor(javaProcessor, IDocument.DEFAULT_CONTENT_TYPE);
            
            // Register the java processor for single line comments to get the NLS template working inside comments
            IContentAssistProcessor wordProcessor= new WordCompletionProcessor();
            CompoundContentAssistProcessor compoundProcessor= new CompoundContentAssistProcessor();
            compoundProcessor.add(javaProcessor);
            compoundProcessor.add(wordProcessor);
            
            assistant.setContentAssistProcessor(compoundProcessor, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
            
            assistant.setContentAssistProcessor(wordProcessor, IJavaPartitions.JAVA_STRING);
            assistant.setContentAssistProcessor(wordProcessor, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

            assistant.setContentAssistProcessor(new JavaDocCompletionProcessor(getEditor()), IJavaPartitions.JAVA_DOC);
            
            ContentAssistPreference.configure(assistant, fPreferenceStore);
            
            assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
            assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
                    
            return assistant;
        }
        
        return null;
    }
    
    /**
     * Copied from super
     */
    protected IDialogSettings getSettings(String sectionName) {
        IDialogSettings settings= JavaPlugin.getDefault().getDialogSettings().getSection(sectionName);
        if (settings == null)
            settings= JavaPlugin.getDefault().getDialogSettings().addNewSection(sectionName);
        
        return settings;
    }
}
