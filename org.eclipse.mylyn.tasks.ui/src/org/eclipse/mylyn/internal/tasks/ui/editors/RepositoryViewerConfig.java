/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Rob Elves
 */
public class RepositoryViewerConfig extends SourceViewerConfiguration {

	private RepositoryTextScanner scanner = null;

	private TaskSpellingReconcileStrategy strategy = new TaskSpellingReconcileStrategy();

	private boolean spellcheck = false;

	public RepositoryViewerConfig(boolean spellchecking) {
		this.spellcheck = spellchecking;
	}

	public void setAnnotationModel(IAnnotationModel model, IDocument doc) {
		strategy.setAnnotationModel(model);
		strategy.setDocument(doc);
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		if (spellcheck) {
			return new DefaultAnnotationHover();
		} else {
			return null;
		}
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getDefaultScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

	private RepositoryTextScanner getDefaultScanner() {
		if (scanner == null) {
			scanner = new RepositoryTextScanner();
		}
		return scanner;
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		List<IHyperlinkDetector> detectors = new ArrayList<IHyperlinkDetector>();
		detectors.addAll(Arrays.asList(TasksUiPlugin.getDefault().getTaskHyperlinkDetectors()));
		return detectors.toArray(new IHyperlinkDetector[detectors.size()]);
	}

	@Override
	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
		return new DefaultHyperlinkPresenter(new RGB(0, 0, 200));
	}

	@Override
	public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
		return SWT.NONE;
	}

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (spellcheck) {
			MonoReconciler reconciler = new MonoReconciler(strategy, false);
			reconciler.setIsIncrementalReconciler(false);
			reconciler.setProgressMonitor(new NullProgressMonitor());
			reconciler.setDelay(500);
			return reconciler;
		} else {
			return null;
		}
	}

	static class RepositoryTextScanner extends RuleBasedScanner {

		public RepositoryTextScanner() {
			IToken bugToken = new Token(new TextAttribute(TaskListColorsAndFonts.COLOR_HYPERLINK_TEXT));
			IRule[] rules = new IRule[15];
			rules[0] = (new SingleLineRule("http://", " ", bugToken));
			rules[1] = (new SingleLineRule("https://", " ", bugToken));
			rules[2] = (new SingleLineRule("bug#", " ", bugToken));
			rules[3] = (new SingleLineRule("bug#", "", bugToken));
			rules[4] = (new SingleLineRule("bug #", "", bugToken));
//			rules[2] = (new MultiLineRule("bug#", " ", bugToken));
//			rules[3] = (new MultiLineRule("bug #", " ", bugToken));
//			rules[4] = (new SingleLineRule("bug #", "\n", bugToken));
			rules[5] = (new SingleLineRule("http://", "\n", bugToken));
			rules[6] = (new SingleLineRule("https://", "\n", bugToken));
			rules[7] = (new SingleLineRule("task#", " ", bugToken));
			rules[8] = (new MultiLineRule("task#", "\n", bugToken));
			rules[9] = (new MultiLineRule("task# ", " ", bugToken));
			rules[10] = (new SingleLineRule("task #", "\n", bugToken));
			rules[11] = (new SingleLineRule("*** This bug has been ", "***", bugToken));
			rules[12] = (new SingleLineRule("http://", "", bugToken));
			rules[13] = (new SingleLineRule("https://", "", bugToken));
			rules[14] = (new MultiLineRule("task #", " ", bugToken));
			setRules(rules);
		}

	}

}