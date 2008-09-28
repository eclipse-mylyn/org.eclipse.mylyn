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
package org.eclipse.mylyn.wikitext.ui.editor;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.internal.sandbox.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryCompletionProcessor;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupSourceViewerConfiguration;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.internal.wikitext.ui.util.PlatformUrlHyperlink;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.AnnotationHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewer;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

/**
 * A task editor extension that uses a markup language to parse content. Provides a markup-aware source editor, and a
 * source viewer that displays markup in its indended formatter form.
 * 
 * @author David Green
 */
@SuppressWarnings("restriction")
public class MarkupTaskEditorExtension extends AbstractTaskEditorExtension {

	private static final String ID_CONTEXT_EDITOR_TASK = "org.eclipse.mylyn.tasks.ui.TaskEditor";

	private static final String ID_CONTEXT_EDITOR_TEXT = "org.eclipse.ui.DefaultTextEditor";

	private MarkupLanguage markupLanguage;

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	@Override
	public String getEditorContextId() {
		return MarkupEditor.CONTEXT;
	}

	@Override
	public SourceViewer createViewer(TaskRepository taskRepository, Composite parent, int style) {
		if (markupLanguage == null) {
			throw new IllegalStateException();
		}
		MarkupViewer markupViewer = new MarkupViewer(parent, null, style | SWT.FLAT | SWT.WRAP);
		MarkupLanguage markupLanguageCopy = markupLanguage.clone();
		configureMarkupLanguage(taskRepository, markupLanguageCopy);

		markupViewer.setMarkupLanguage(markupLanguageCopy);
		MarkupViewerConfiguration configuration = createViewerConfiguration(taskRepository, markupViewer);
		markupViewer.configure(configuration);

		markupViewer.setEditable(false);
		markupViewer.getTextWidget().setCaret(null);

		return markupViewer;
	}

	protected MarkupViewerConfiguration createViewerConfiguration(TaskRepository taskRepository,
			MarkupViewer markupViewer) {
		return new TaskMarkupViewerConfiguration(markupViewer, taskRepository);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SourceViewer createEditor(TaskRepository taskRepository, Composite parent, int style) {
		final MarkupLanguage markupLanguageCopy = markupLanguage.clone();
		configureMarkupLanguage(taskRepository, markupLanguageCopy);

		SourceViewer viewer = new SourceViewer(parent, null, style | SWT.WRAP) {
			@Override
			public void setDocument(IDocument document, IAnnotationModel annotationModel, int modelRangeOffset,
					int modelRangeLength) {
				if (document != null) {
					configurePartitioning(document);
				}
				super.setDocument(document, annotationModel, modelRangeOffset, modelRangeLength);
			}

			private void configurePartitioning(IDocument document) {
				FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
				partitioner.setMarkupLanguage(markupLanguageCopy.clone());
				partitioner.connect(document);
				document.setDocumentPartitioner(partitioner);
			}
		};
		// configure the viewer
		MarkupSourceViewerConfiguration configuration = new TaskMarkupSourceViewerConfiguration(
				EditorsUI.getPreferenceStore(), taskRepository);

		configuration.setMarkupLanguage(markupLanguageCopy);
		viewer.configure(configuration);

		// we want the viewer to show annotations
		viewer.showAnnotations(true);

		DefaultMarkerAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
		MarkerAnnotationPreferences annotationPreferences = new MarkerAnnotationPreferences();

		// configure viewer annotation/decoration support
		final SourceViewerDecorationSupport support = new SourceViewerDecorationSupport(viewer, null, annotationAccess,
				EditorsUI.getSharedTextColors());

		// hook the support up to the preference store
		Iterator<AnnotationPreference> e = annotationPreferences.getAnnotationPreferences().iterator();
		while (e.hasNext()) {
			AnnotationPreference preference = e.next();
			support.setAnnotationPreference(preference);
		}
		support.setCursorLinePainterPreferenceKeys(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE,
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		support.setMarginPainterPreferenceKeys(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN,
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR,
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN);

		support.install(EditorsUI.getPreferenceStore());
		viewer.getControl().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				support.dispose();
			}
		});

		IFocusService focusService = (IFocusService) PlatformUI.getWorkbench().getService(IFocusService.class);
		if (focusService != null) {
			focusService.addFocusTracker(viewer.getTextWidget(), MarkupEditor.EDITOR_SOURCE_VIEWER);
		}

		viewer.getTextWidget().setData(MarkupLanguage.class.getName(), markupLanguageCopy);
		viewer.getTextWidget().setData(ISourceViewer.class.getName(), viewer);

		return viewer;
	}

	/**
	 * Configures the markup language with settings from the task repository. Subclasses may override this method, but
	 * should call <code>super.configureMarkupLanguage(taskRepository,markupLanguage)</code>.
	 * 
	 * @param taskRepository
	 *            the repository from which settings should be used
	 * @param markupLanguage
	 *            the markup language to configure
	 * 
	 * @see #configureDefaultInternalLinkPattern(TaskRepository, MarkupLanguage)
	 */
	protected void configureMarkupLanguage(TaskRepository taskRepository, MarkupLanguage markupLanguage) {
		String internalLinkPattern = taskRepository.getProperty(AbstractTaskEditorExtension.INTERNAL_WIKI_LINK_PATTERN);
		if (internalLinkPattern != null && internalLinkPattern.trim().length() > 0) {
			markupLanguage.setInternalLinkPattern(internalLinkPattern.trim());
		} else {
			configureDefaultInternalLinkPattern(taskRepository, markupLanguage);
		}
	}

	/**
	 * Overriding methods should set the {@link MarkupLanguage#getInternalLinkPattern() internal hyperlink pattern} of
	 * the given markup language based on some default rules applied to the task repository URL. The default
	 * implementation does nothing.
	 * 
	 * @param taskRepository
	 *            the task repository from which settings may be obtained
	 * @param markupLanguage
	 *            the markup language to configure
	 */
	protected void configureDefaultInternalLinkPattern(TaskRepository taskRepository, MarkupLanguage markupLanguage) {
		// nothing to do
	}

	protected static class TaskMarkupSourceViewerConfiguration extends MarkupSourceViewerConfiguration {

		private final TaskRepository taskRepository;

		public TaskMarkupSourceViewerConfiguration(IPreferenceStore preferenceStore, TaskRepository taskRepository) {
			super(preferenceStore);
			this.taskRepository = taskRepository;
		}

		@Override
		protected IContentAssistProcessor[] createContentAssistProcessors() {
			// FIXME: remove usage of internal API
//			IContentAssistProcessor processor = TasksUi.createContentAssistProcessor(taskRepository);
//			return processor==null?null:new IContentAssistProcessor[] { processor };
			return new IContentAssistProcessor[] { new RepositoryCompletionProcessor(taskRepository) };
		}

		@Override
		public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
			return new DefaultAnnotationHover() {
				@Override
				protected boolean isIncluded(Annotation annotation) {
					return annotation.getType().startsWith("org.eclipse.wikitext") || super.isIncluded(annotation);
				}
			};
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
			Map hyperlinkDetectorTargets = super.getHyperlinkDetectorTargets(sourceViewer);
			addRepositoryHyperlinkDetectorTargets(taskRepository, hyperlinkDetectorTargets);
			return hyperlinkDetectorTargets;
		}
	}

	protected static class TaskMarkupViewerConfiguration extends MarkupViewerConfiguration {

		private final TaskRepository taskRepository;

		public TaskMarkupViewerConfiguration(MarkupViewer viewer, TaskRepository taskRepository) {
			super(viewer);
			this.taskRepository = taskRepository;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
			Map hyperlinkDetectorTargets = super.getHyperlinkDetectorTargets(sourceViewer);
			addRepositoryHyperlinkDetectorTargets(taskRepository, hyperlinkDetectorTargets);
			return hyperlinkDetectorTargets;
		}

		@Override
		protected AnnotationHyperlinkDetector createAnnotationHyperlinkDetector() {
			return new AnnotationHyperlinkDetector() {
				@Override
				protected IHyperlink createUrlHyperlink(IRegion region, String href) {
					return new PlatformUrlHyperlink(region, href);
				}
			};
		}

		@Override
		public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
			return SWT.NONE;
		}

		@Override
		public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
			if (fPreferenceStore == null) {
				return new TaskHyperlinkPresenter(sourceViewer, new RGB(0, 0, 255));
			}

			return new TaskHyperlinkPresenter(sourceViewer, fPreferenceStore);
		}

	}

	@SuppressWarnings("unchecked")
	private static void addRepositoryHyperlinkDetectorTargets(final TaskRepository taskRepository,
			Map hyperlinkDetectorTargets) {
		IAdaptable context = new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if (adapter == TaskRepository.class) {
					return taskRepository;
				}
				return null;
			}
		};
		hyperlinkDetectorTargets.put(ID_CONTEXT_EDITOR_TEXT, context);
		hyperlinkDetectorTargets.put(ID_CONTEXT_EDITOR_TASK, context);
	}

	private static class TaskHyperlinkPresenter extends DefaultHyperlinkPresenter {

		private IRegion activeRegion;

		/**
		 * Stores which task a tooltip is being displayed for. It is used to avoid having the same tooltip being set
		 * multiple times while you move the mouse over a task hyperlink (bug#209409)
		 */
		private ITask currentTaskHyperlink;

		public TaskHyperlinkPresenter(ISourceViewer sourceViewer, IPreferenceStore store) {
			super(store);
			this.sourceViewer = sourceViewer;
		}

		public TaskHyperlinkPresenter(ISourceViewer sourceViewer, RGB color) {
			super(color);
			this.sourceViewer = sourceViewer;
		}

		private final ISourceViewer sourceViewer;

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

				ITask task = TasksUi.getRepositoryModel().getTask(hyperlink.getRepository(), hyperlink.getTaskId());

				if (task != null && task != currentTaskHyperlink) {
					currentTaskHyperlink = task;
					activeRegion = hyperlink.getHyperlinkRegion();
					Control cursorControl = sourceViewer.getTextWidget().getDisplay().getCursorControl();
					if (cursorControl != null) {
						if (task.getTaskKey() == null) {
							cursorControl.setToolTipText(task.getSummary());
						} else {
							cursorControl.setToolTipText(task.getTaskKey() + ": " + task.getSummary());
						}
					}
				}
			}
			super.showHyperlinks(hyperlinks);
		}

		@Override
		public void hideHyperlinks() {
			Control cursorControl = sourceViewer.getTextWidget().getDisplay().getCursorControl();
			if (cursorControl != null) {
				cursorControl.setToolTipText(null);
			}
			currentTaskHyperlink = null;

			super.hideHyperlinks();
		}
	}
}
