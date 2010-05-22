/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Frank Becker - improvements for bug 304910
 *     Tasktop Technologies - improvements
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.tasks.ui.editor;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.internal.wikitext.tasks.ui.WikiTextTasksUiPlugin;
import org.eclipse.mylyn.internal.wikitext.tasks.ui.util.PlatformUrlHyperlink;
import org.eclipse.mylyn.internal.wikitext.tasks.ui.util.Util;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.internal.wikitext.ui.util.PreferenceStoreFacade;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.AnnotationHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlinkPresenter;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.ui.editor.MarkupSourceViewer;
import org.eclipse.mylyn.wikitext.ui.editor.MarkupSourceViewerConfiguration;
import org.eclipse.mylyn.wikitext.ui.editor.ShowInTargetBridge;
import org.eclipse.mylyn.wikitext.ui.viewer.DefaultHyperlinkDetectorDescriptorFilter;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewer;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
 * source viewer that displays markup in its intended formatted form.
 * 
 * @author David Green
 * @since 1.0
 */
@SuppressWarnings("restriction")
public class MarkupTaskEditorExtension<MarkupLanguageType extends MarkupLanguage> extends AbstractTaskEditorExtension {

	/**
	 * Provide a means to disable WikiWord linking. This feature is experimental and may be removed in a future release.
	 * To enable this feature, set the system property <tt>MarkupTaskEditorExtension.wikiWordDisabled</tt> to
	 * <tt>true</tt>. eg, <tt>-DMarkupTaskEditorExtension.wikiWordDisabled=true</tt>
	 */
	private static final boolean DISABLE_WIKI_WORD = Boolean.getBoolean(MarkupTaskEditorExtension.class.getSimpleName()
			+ ".wikiWordDisabled"); //$NON-NLS-1$

	private static final String ID_CONTEXT_EDITOR_TASK = "org.eclipse.mylyn.tasks.ui.TaskEditor"; //$NON-NLS-1$

	private static final String ID_CONTEXT_EDITOR_TEXT = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$

	private MarkupLanguageType markupLanguage;

	public MarkupLanguageType getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguageType markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	@Override
	public String getEditorContextId() {
		return MarkupEditor.CONTEXT;
	}

	@Deprecated
	@Override
	public SourceViewer createViewer(TaskRepository taskRepository, Composite parent, int style) {
		return createViewer(taskRepository, parent, style, null);
	}

	@Override
	public SourceViewer createViewer(TaskRepository taskRepository, Composite parent, int style, IAdaptable context) {
		if (markupLanguage == null) {
			throw new IllegalStateException();
		}
		MarkupViewer markupViewer = new MarkupViewer(parent, null, style | SWT.FLAT | SWT.WRAP);
		MarkupLanguageType markupLanguageCopy = createRepositoryMarkupLanguage(taskRepository);
		configureMarkupLanguage(taskRepository, markupLanguageCopy);

		markupViewer.setMarkupLanguage(markupLanguageCopy);
		MarkupViewerConfiguration configuration = createViewerConfiguration(taskRepository, markupViewer);
		configuration.setDisableHyperlinkModifiers(true);
		if (markupLanguageCopy.isDetectingRawHyperlinks()) {
			// bug 264612 don't detect hyperlinks twice
			configuration.addHyperlinkDetectorDescriptorFilter(new DefaultHyperlinkDetectorDescriptorFilter(
					"org.eclipse.mylyn.tasks.ui.hyperlinks.detectors.url")); //$NON-NLS-1$
		}
		markupViewer.configure(configuration);

		markupViewer.setEditable(false);
		markupViewer.getTextWidget().setCaret(null);

		if (JFaceResources.getFontRegistry().hasValueFor(WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_DEFAULT_FONT)) {
			markupViewer.getTextWidget().setFont(
					JFaceResources.getFontRegistry().get(WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_DEFAULT_FONT));
		}
		if (JFaceResources.getFontRegistry().hasValueFor(WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_MONOSPACE_FONT)) {
			markupViewer.setDefaultMonospaceFont(JFaceResources.getFontRegistry().get(
					WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_MONOSPACE_FONT));
		}

		markupViewer.setStylesheet(WikiTextUiPlugin.getDefault().getPreferences().getStylesheet());

		return markupViewer;
	}

	@SuppressWarnings("unchecked")
	private MarkupLanguageType createRepositoryMarkupLanguage(TaskRepository taskRepository) {
		MarkupLanguageType copy = (MarkupLanguageType) markupLanguage.clone();
		MarkupLanguageConfiguration configuration = createMarkupLanguageConfiguration(taskRepository);
		copy.configure(configuration);
		return copy;
	}

	/**
	 * @since 1.3
	 */
	protected MarkupLanguageConfiguration createMarkupLanguageConfiguration(TaskRepository taskRepository) {
		MarkupLanguageConfiguration configuration = Util.create(taskRepository.getConnectorKind());
		if (DISABLE_WIKI_WORD) {
			configuration.setWikiWordLinking(false);
		}
		return configuration;
	}

	protected TaskMarkupViewerConfiguration createViewerConfiguration(TaskRepository taskRepository,
			MarkupViewer markupViewer) {
		return createViewerConfiguration(taskRepository, markupViewer, null);
	}

	/**
	 * @since 1.3
	 */
	protected TaskMarkupViewerConfiguration createViewerConfiguration(TaskRepository taskRepository,
			MarkupViewer markupViewer, IAdaptable context) {
		return new TaskMarkupViewerConfiguration(markupViewer, taskRepository, context);
	}

	protected TaskMarkupSourceViewerConfiguration createSourceViewerConfiguration(TaskRepository taskRepository,
			SourceViewer viewer) {
		return createSourceViewerConfiguration(taskRepository, viewer, null);
	}

	/**
	 * @since 1.3
	 */
	protected TaskMarkupSourceViewerConfiguration createSourceViewerConfiguration(TaskRepository taskRepository,
			SourceViewer viewer, IAdaptable context) {
		IPreferenceStore preferenceStore = EditorsUI.getPreferenceStore();
		return new TaskMarkupSourceViewerConfiguration(preferenceStore, taskRepository, context);
	}

	@Deprecated
	@Override
	public SourceViewer createEditor(TaskRepository taskRepository, Composite parent, int style) {
		return createEditor(taskRepository, parent, style, null);
	}

	@Override
	public SourceViewer createEditor(TaskRepository taskRepository, Composite parent, int style, IAdaptable context) {
		final MarkupLanguageType markupLanguageCopy = createRepositoryMarkupLanguage(taskRepository);
		configureMarkupLanguage(taskRepository, markupLanguageCopy);

		SourceViewer viewer = new MarkupSourceViewer(parent, null, style | SWT.WRAP, markupLanguageCopy);
		// configure the viewer
		MarkupSourceViewerConfiguration configuration = createSourceViewerConfiguration(taskRepository, viewer, context);

		configuration.setMarkupLanguage(markupLanguageCopy);
		configuration.setShowInTarget(new ShowInTargetBridge(viewer));
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

		support.install(new EditorExtensionPreferenceStore(EditorsUI.getPreferenceStore(), viewer.getControl()));
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
	 * @see #configureDefaultInternalLinkPattern(TaskRepository, MarkupLanguage)
	 */
	protected void configureMarkupLanguage(TaskRepository taskRepository, MarkupLanguageType markupLanguage) {
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
	protected void configureDefaultInternalLinkPattern(TaskRepository taskRepository, MarkupLanguageType markupLanguage) {
		// nothing to do
	}

	protected static class TaskMarkupSourceViewerConfiguration extends MarkupSourceViewerConfiguration {

		private final TaskRepository taskRepository;

		private final IAdaptable context;

		public TaskMarkupSourceViewerConfiguration(IPreferenceStore preferenceStore, TaskRepository taskRepository) {
			this(preferenceStore, taskRepository, null);
		}

		/**
		 * @since 1.3
		 */
		public TaskMarkupSourceViewerConfiguration(IPreferenceStore preferenceStore, TaskRepository taskRepository,
				IAdaptable context) {
			super(preferenceStore, WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_DEFAULT_FONT,
					WikiTextTasksUiPlugin.FONT_REGISTRY_KEY_MONOSPACE_FONT);
			this.taskRepository = taskRepository;
			if (context == null) {
				this.context = createDefaultHyperlinkDetectorContext(taskRepository);
			} else {
				this.context = context;
			}
			// filter out the platform URL hyperlink detector since Mylyn contributes one as well.
			addHyperlinkDetectorDescriptorFilter(new DefaultHyperlinkDetectorDescriptorFilter(
					"org.eclipse.ui.internal.editors.text.URLHyperlinkDetector")); //$NON-NLS-1$
		}

		@Override
		protected IContentAssistProcessor[] createContentAssistProcessors() {
			IContentAssistProcessor processor = TasksUi.getUiFactory().createTaskContentAssistProcessor(taskRepository);
			return processor == null ? null : new IContentAssistProcessor[] { processor };
		}

		@Override
		public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
			return new DefaultAnnotationHover() {
				@Override
				protected boolean isIncluded(Annotation annotation) {
					return annotation.getType().startsWith("org.eclipse.wikitext") || super.isIncluded(annotation); //$NON-NLS-1$
				}
			};
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
			Map hyperlinkDetectorTargets = super.getHyperlinkDetectorTargets(sourceViewer);
			addRepositoryHyperlinkDetectorTargets(context, hyperlinkDetectorTargets);
			return hyperlinkDetectorTargets;
		}

		@Override
		public IReconciler getReconciler(ISourceViewer sourceViewer) {
			if (sourceViewer.isEditable()) {
				return super.getReconciler(sourceViewer);
			} else {
				return null;
			}
		}

		@Override
		public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
			return SWT.NONE;
		}

		@Override
		public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
			if (fPreferenceStore == null) {
				return new TaskHyperlinkPresenter(new RGB(0, 0, 255));
			}
			return new TaskHyperlinkPresenter(fPreferenceStore);
		}
	}

	protected static class TaskMarkupViewerConfiguration extends MarkupViewerConfiguration {

		private final TaskRepository taskRepository;

		private final IAdaptable context;

		public TaskMarkupViewerConfiguration(MarkupViewer viewer, TaskRepository taskRepository) {
			this(viewer, taskRepository, null);
		}

		/**
		 * @since 1.3
		 */
		public TaskMarkupViewerConfiguration(MarkupViewer viewer, TaskRepository taskRepository, IAdaptable context) {
			super(viewer);
			this.taskRepository = taskRepository;
			if (context == null) {
				this.context = createDefaultHyperlinkDetectorContext(taskRepository);
			} else {
				this.context = context;
			}
			markupHyperlinksFirst = false;
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
			Map hyperlinkDetectorTargets = super.getHyperlinkDetectorTargets(sourceViewer);
			addRepositoryHyperlinkDetectorTargets(context, hyperlinkDetectorTargets);
			return hyperlinkDetectorTargets;
		}

		/**
		 * @noreference This method is not intended to be referenced by clients.
		 */
		@Override
		protected AnnotationHyperlinkDetector createAnnotationHyperlinkDetector() {
			return new PlatformUrlAnnotationHyperlinkDetector();
		}

		@Override
		public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
			if (fPreferenceStore == null) {
				return new TaskHyperlinkPresenter(new RGB(0, 0, 255));
			}
			return new TaskHyperlinkPresenter(fPreferenceStore);
		}
	}

	private static class PlatformUrlAnnotationHyperlinkDetector extends AnnotationHyperlinkDetector {
		@Override
		protected IHyperlink createUrlHyperlink(IRegion region, String href) {
			return new PlatformUrlHyperlink(region, href);
		}
	}

	@SuppressWarnings( { "rawtypes" })
	private static IAdaptable createDefaultHyperlinkDetectorContext(final TaskRepository repository) {
		return new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if (adapter == TaskRepository.class) {
					return repository;
				}
				return null;
			}
		};
	}

	@SuppressWarnings( { "unchecked" })
	private static void addRepositoryHyperlinkDetectorTargets(IAdaptable context, Map hyperlinkDetectorTargets) {
		hyperlinkDetectorTargets.put(ID_CONTEXT_EDITOR_TEXT, context);
		hyperlinkDetectorTargets.put(ID_CONTEXT_EDITOR_TASK, context);
	}

	/**
	 * bug 251657: wrap preferences so that we can alter the current line highlight based on the focus state of the
	 * provided control. bug 273528: override workspace preferences to eliminate print margin in the task editor
	 * 
	 * @author David Green
	 */
	private static class EditorExtensionPreferenceStore extends PreferenceStoreFacade {

		// track separately from isFocusControl() since isFocusControl() is not accurate while processing a focus event
		boolean controlFocused;

		public EditorExtensionPreferenceStore(IPreferenceStore preferenceStore, Control control) {
			super(preferenceStore);
			controlFocused = control.isFocusControl();
			control.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					controlFocused = true;
					focusChanged();
				}

				public void focusLost(FocusEvent e) {
					controlFocused = false;
					focusChanged();
				}
			});
		}

		protected void focusChanged() {
			if (!getCurrentLineHighlightPreference()) {
				return;
			}
			boolean newValue = getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE);
			firePropertyChangeEvent(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, !newValue,
					newValue);
		}

		private boolean getCurrentLineHighlightPreference() {
			return TasksUiUtil.getHighlightCurrentLine();
		}

		@Override
		public boolean getBoolean(String name) {
			if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE.equals(name)) {
				if (!controlFocused) {
					return false;
				}
				return getCurrentLineHighlightPreference();
			}
			if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN.equals(name)) {
				return false;
			}
			return super.getBoolean(name);
		}
	}
}
