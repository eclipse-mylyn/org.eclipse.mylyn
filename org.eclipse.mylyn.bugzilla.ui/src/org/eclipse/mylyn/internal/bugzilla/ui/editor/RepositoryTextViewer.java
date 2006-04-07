/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.internal.editors.text.URLHyperlinkDetector;

/**
 * @author Rob Elves
 */
public class RepositoryTextViewer extends SourceViewer {

	private TaskRepository repository;

	public RepositoryTextViewer(TaskRepository repository, Composite composite, int style) {
		super(composite, null, style);
		this.configure(new RepositoryViewerConfig());
		this.repository = repository;
	}

	public TaskRepository getRepository() {		
		return repository;
	}

	public void setRepository(TaskRepository repository) {
		this.repository = repository;
	}

	class RepositoryViewerConfig extends TextSourceViewerConfiguration {

		private RepositoryTextScanner scanner = null;

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
		
		public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
			URLHyperlinkDetector hyperlinkDetector = new URLHyperlinkDetector(sourceViewer);
			List<IHyperlinkDetector> detectors = new ArrayList<IHyperlinkDetector>();
			detectors.add(hyperlinkDetector);
			detectors.addAll(Arrays.asList(MylarTaskListPlugin.getDefault().getTaskHyperlinkDetectors()));
			return detectors.toArray(new IHyperlinkDetector[detectors.size()]);
//			 return MylarTaskListPlugin.getDefault().getTaskHyperlinkDetectors();
		} 
		
		public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
			return new DefaultHyperlinkPresenter(new RGB(0, 0, 200));
		}
		
		public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
			return SWT.NONE;
		}
	}

	class RepositoryTextScanner extends RuleBasedScanner {
		private Color URL_COLOR = new Color(Display.getCurrent(), new RGB(0, 0, 200));

		public RepositoryTextScanner() {
			IToken bugToken = new Token(new TextAttribute(URL_COLOR));
			IRule[] rules = new IRule[7];
			rules[0] = (new SingleLineRule("http://", " ", bugToken));
			rules[1] = (new SingleLineRule("https://", " ", bugToken));
			rules[2] = (new MultiLineRule("bug#",  " ", bugToken));
			rules[3] = (new MultiLineRule("bug #",  " ", bugToken));
			rules[4] = (new SingleLineRule("bug #",  "\n", bugToken));
			rules[5] = (new SingleLineRule("http://", "\n", bugToken));
			rules[6] = (new SingleLineRule("https://", "\n", bugToken));
//			rules[7] = (new MultiLineRule(" bug ",  " ", bugToken));
//			rules[8] = (new SingleLineRule(" at ",  ")", bugToken));
			setRules(rules);
		}

	}

}
