/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 * @author Rob Elves
 */
public class RepositoryViewerConfig extends TextSourceViewerConfiguration {

	private RepositoryTextScanner scanner = null;

	private boolean spellcheck = false;

	public RepositoryViewerConfig(boolean spellchecking) {
		super(EditorsUI.getPreferenceStore());
		this.spellcheck = spellchecking;
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

//	@Override
//	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
//		List<IHyperlinkDetector> detectors = new ArrayList<IHyperlinkDetector>();
//		detectors.addAll(Arrays.asList(TasksUiPlugin.getDefault().getTaskHyperlinkDetectors()));
//		return detectors.toArray(new IHyperlinkDetector[detectors.size()]);
//	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected Map getHyperlinkDetectorTargets(final ISourceViewer sourceViewer) {
		IAdaptable context = new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if(adapter==TaskRepository.class) {
					if(sourceViewer instanceof RepositoryTextViewer) {
						return ((RepositoryTextViewer) sourceViewer).getRepository();
					}
				}
				return null;
			}
		};
		
		Map targets = new HashMap();
		targets.put("org.eclipse.ui.DefaultTextEditor", context);
		targets.put("org.eclipse.mylyn.tasks.ui.TaskEditor", context);
		return targets;
	}

	@Override
	public IHyperlinkPresenter getHyperlinkPresenter(final ISourceViewer sourceViewer) {
		return new DefaultHyperlinkPresenter(new RGB(0, 0, 200)) {
			@Override
			public void showHyperlinks(IHyperlink[] hyperlinks) {
				super.showHyperlinks(hyperlinks);

				if (hyperlinks != null && hyperlinks.length > 0 && hyperlinks[0] instanceof TaskHyperlink) {
					TaskHyperlink hyperlink = (TaskHyperlink) hyperlinks[0];

					TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
					String repositoryUrl = hyperlink.getRepository().getUrl();

					AbstractTask task = taskList.getTask(repositoryUrl, hyperlink.getTaskId());
					if (task == null) {
						task = taskList.getTaskByKey(repositoryUrl, hyperlink.getTaskId());
					}

					if (task != null) {
						Control cursorControl = sourceViewer.getTextWidget().getDisplay().getCursorControl();
						if (task.getTaskKey() == null) {
							cursorControl.setToolTipText(task.getSummary());
						} else {
							cursorControl.setToolTipText(task.getTaskKey() + ": " + task.getSummary());
						}
					}
				}
			}

			@Override
			public void hideHyperlinks() {
				Control cursorControl = sourceViewer.getTextWidget().getDisplay().getCursorControl();
				if (cursorControl != null) {
					cursorControl.setToolTipText(null);
				}

				super.hideHyperlinks();
			}

			public void uninstall() {
				// ignore
				super.uninstall();
			}
		};
	}

	@Override
	public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
		return SWT.NONE;
	}

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (spellcheck) {
			return super.getReconciler(sourceViewer);
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