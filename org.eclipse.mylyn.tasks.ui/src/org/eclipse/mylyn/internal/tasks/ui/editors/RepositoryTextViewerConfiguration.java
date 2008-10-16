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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlink;
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
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
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

	private static final String ID_CONTEXT_EDITOR_TASK = "org.eclipse.mylyn.tasks.ui.TaskEditor";

	private static final String ID_CONTEXT_EDITOR_TEXT = "org.eclipse.ui.DefaultTextEditor";

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
		return new RepositoryTextViewerHyperlinkPresenter(JFaceResources.getColorRegistry().get(
				JFacePreferences.ACTIVE_HYPERLINK_COLOR), sourceViewer);
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

	private final class RepositoryTextViewerHyperlinkPresenter extends DefaultHyperlinkPresenter {

		private final ISourceViewer sourceViewer;

		private IRegion activeRegion;

		/**
		 * Stores which task a tooltip is being displayed for. It is used to avoid having the same tooltip being set
		 * multiple times while you move the mouse over a task hyperlink (bug#209409)
		 */
		private ITask currentTaskHyperlink;

		private RepositoryTextViewerHyperlinkPresenter(Color color, ISourceViewer sourceViewer) {
			super(color);
			this.sourceViewer = sourceViewer;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void applyTextPresentation(TextPresentation textPresentation) {
			super.applyTextPresentation(textPresentation);
			if (activeRegion != null && currentTaskHyperlink != null && currentTaskHyperlink.isCompleted()) {
				Iterator<StyleRange> styleRangeIterator = textPresentation.getAllStyleRangeIterator();
				while (styleRangeIterator.hasNext()) {
					StyleRange styleRange = styleRangeIterator.next();
					if (activeRegion.getOffset() == styleRange.start && activeRegion.getLength() == styleRange.length) {
						styleRange.strikeout = true;
						break;
					}
				}
			}
		}

		@Override
		public void showHyperlinks(IHyperlink[] hyperlinks) {
			activeRegion = null;
			if (hyperlinks != null && hyperlinks.length > 0 && hyperlinks[0] instanceof TaskHyperlink) {
				TaskHyperlink hyperlink = (TaskHyperlink) hyperlinks[0];

				TaskList taskList = TasksUiPlugin.getTaskList();
				String repositoryUrl = hyperlink.getRepository().getRepositoryUrl();

				ITask task = taskList.getTask(repositoryUrl, hyperlink.getTaskId());
				if (task == null) {
					task = taskList.getTaskByKey(repositoryUrl, hyperlink.getTaskId());
				}

				if (task != null && task != currentTaskHyperlink) {
					currentTaskHyperlink = task;
					activeRegion = hyperlink.getHyperlinkRegion();
					if (sourceViewer.getTextWidget() != null && !sourceViewer.getTextWidget().isDisposed()) {
						if (task.getTaskKey() == null) {
							sourceViewer.getTextWidget().setToolTipText(task.getSummary());
						} else {
							sourceViewer.getTextWidget().setToolTipText(task.getTaskKey() + ": " + task.getSummary());
						}
					}
				}
			}
			super.showHyperlinks(hyperlinks);
		}

		@Override
		public void hideHyperlinks() {
			if (currentTaskHyperlink != null) {
				if (sourceViewer.getTextWidget() != null && !sourceViewer.getTextWidget().isDisposed()) {
					sourceViewer.getTextWidget().setToolTipText(null);
				}
				currentTaskHyperlink = null;
			}
			super.hideHyperlinks();
		}

	}

	private static class RepositoryTextScanner extends RuleBasedScanner {

		public RepositoryTextScanner(Mode mode) {
			IToken bugToken = new Token(new TextAttribute(JFaceResources.getColorRegistry().get(
					JFacePreferences.ACTIVE_HYPERLINK_COLOR)));
			IToken quoteToken = new Token(new TextAttribute(CommonColors.TEXT_QUOTED));
			IRule[] rules = new IRule[16];
			rules[0] = (new SingleLineRule("http://", " ", bugToken));
			rules[1] = (new SingleLineRule("https://", " ", bugToken));
			rules[2] = (new SingleLineRule("bug#", " ", bugToken));
			rules[3] = (new SingleLineRule("bug#", "", bugToken));
			rules[4] = (new SingleLineRule("bug #", "", bugToken));
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
			SingleLineRule quoteRule = new SingleLineRule(">", null, quoteToken, (char) 0, true);
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
