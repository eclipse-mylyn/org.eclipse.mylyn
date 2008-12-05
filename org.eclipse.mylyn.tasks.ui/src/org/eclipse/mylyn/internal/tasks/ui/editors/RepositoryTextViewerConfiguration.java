/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
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
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonColors;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlinkPresenter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public class RepositoryTextViewerConfiguration extends TextSourceViewerConfiguration {

	public enum Mode {
		URL, TASK, TASK_RELATION, DEFAULT
	}

	private static final String ID_CONTEXT_EDITOR_TASK = "org.eclipse.mylyn.tasks.ui.TaskEditor"; //$NON-NLS-1$

	private static final String ID_CONTEXT_EDITOR_TEXT = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$

	private RepositoryTextScanner scanner;

	private final boolean spellCheck;

	private final TaskRepository taskRepository;

	private Mode mode;

	public RepositoryTextViewerConfiguration(TaskRepository taskRepository, boolean spellCheck) {
		super(EditorsUI.getPreferenceStore());
		this.taskRepository = taskRepository;
		this.spellCheck = spellCheck;
		this.mode = Mode.DEFAULT;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		Assert.isNotNull(mode);
		this.mode = mode;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		if (getMode() == Mode.DEFAULT) {
			PresentationReconciler reconciler = new PresentationReconciler();
			reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getDefaultScanner());
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
			return reconciler;
		}
		return super.getPresentationReconciler(sourceViewer);
	}

	private RepositoryTextScanner getDefaultScanner() {
		if (scanner == null) {
			scanner = new RepositoryTextScanner(getMode());
		}
		return scanner;
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (mode == Mode.URL || mode == Mode.TASK_RELATION) {
			return getDefaultHyperlinkDetectors(sourceViewer, mode);
		}
		return super.getHyperlinkDetectors(sourceViewer);
	}

	public IHyperlinkDetector[] getDefaultHyperlinkDetectors(ISourceViewer sourceViewer, Mode mode) {
		IHyperlinkDetector[] detectors;
		if (mode == Mode.URL) {
			detectors = new IHyperlinkDetector[] { new TaskUrlHyperlinkDetector() };
		} else if (mode == Mode.TASK) {
			detectors = new IHyperlinkDetector[] { new TaskHyperlinkDetector() };
		} else if (mode == Mode.TASK_RELATION) {
			detectors = new IHyperlinkDetector[] { new TaskRelationHyperlinkDetector() };
		} else {
			detectors = super.getHyperlinkDetectors(sourceViewer);
		}
		if (detectors != null) {
			IAdaptable target = getDefaultHyperlinkTarget();
			for (IHyperlinkDetector hyperlinkDetector : detectors) {
				if (hyperlinkDetector instanceof AbstractHyperlinkDetector) {
					((AbstractHyperlinkDetector) hyperlinkDetector).setContext(target);
				}
			}
		}
		return detectors;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Map getHyperlinkDetectorTargets(final ISourceViewer sourceViewer) {
		IAdaptable context = getDefaultHyperlinkTarget();

		Map targets = new HashMap();
		targets.put(ID_CONTEXT_EDITOR_TEXT, context);
		targets.put(ID_CONTEXT_EDITOR_TASK, context);
		return targets;
	}

	@SuppressWarnings("unchecked")
	private IAdaptable getDefaultHyperlinkTarget() {
		IAdaptable context = new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if (adapter == TaskRepository.class) {
					return getTaskRepository();
				}
				return null;
			}
		};
		return context;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	@Override
	public IHyperlinkPresenter getHyperlinkPresenter(final ISourceViewer sourceViewer) {
		if (fPreferenceStore == null) {
			return new TaskHyperlinkPresenter(new RGB(0, 0, 255));
		}
		return new TaskHyperlinkPresenter(fPreferenceStore);
	}

	@Override
	public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
		return SWT.NONE;
	}

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (spellCheck) {
			return super.getReconciler(sourceViewer);
		} else {
			return null;
		}
	}

	private static class RepositoryTextScanner extends RuleBasedScanner {

		public RepositoryTextScanner(Mode mode) {
			IToken bugToken = new Token(new TextAttribute(JFaceResources.getColorRegistry().get(
					JFacePreferences.ACTIVE_HYPERLINK_COLOR)));
			IToken quoteToken = new Token(new TextAttribute(CommonColors.TEXT_QUOTED));
			IRule[] rules = new IRule[16];
			rules[0] = (new SingleLineRule("http://", " ", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[1] = (new SingleLineRule("https://", " ", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[2] = (new SingleLineRule("bug#", " ", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[3] = (new SingleLineRule("bug#", "", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[4] = (new SingleLineRule("bug #", "", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[5] = (new SingleLineRule("http://", "\n", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[6] = (new SingleLineRule("https://", "\n", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[7] = (new SingleLineRule("task#", " ", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[8] = (new MultiLineRule("task#", "\n", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[9] = (new MultiLineRule("task# ", " ", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[10] = (new SingleLineRule("task #", "\n", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[11] = (new SingleLineRule("*** This bug has been ", "***", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[12] = (new SingleLineRule("http://", "", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[13] = (new SingleLineRule("https://", "", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			rules[14] = (new MultiLineRule("task #", " ", bugToken)); //$NON-NLS-1$ //$NON-NLS-2$
			SingleLineRule quoteRule = new SingleLineRule(">", null, quoteToken, (char) 0, true); //$NON-NLS-1$
			quoteRule.setColumnConstraint(0);
			rules[15] = quoteRule;
			setRules(rules);
		}

	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (mode == Mode.URL) {
			return null;
		}
		ContentAssistant assistant = new ContentAssistant();
		RepositoryCompletionProcessor processor = new RepositoryCompletionProcessor(taskRepository);
		if (mode == Mode.TASK_RELATION) {
			processor.setNeverIncludePrefix(true);
		}
		assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		return assistant;
	}

}
