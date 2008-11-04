/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Raphael Ackermann - spell checking support on bug 195514
 *     Jingwen Ou - extensibility improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.tasks.ui.commands.ViewSourceHandler;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.themes.IThemeManager;

/**
 * A text attribute editor that can switch between a editor, preview and source view.
 * 
 * @author Raphael Ackermann
 * @author Steffen Pingel
 * @author Jingwen Ou
 */
public class RichTextAttributeEditor extends AbstractAttributeEditor {

	private IContextActivation contextActivation;

	private final IContextService contextService;

	private SourceViewer defaultViewer;

	private Composite editorComposite;

	private StackLayout editorLayout;

	private final AbstractTaskEditorExtension extension;

	private SourceViewer editorViewer;

	private SourceViewer previewViewer;

	private final TaskRepository taskRepository;

	private FormToolkit toolkit;

	public class ViewSourceAction extends Action {

		public ViewSourceAction() {
			super("Viewer Source", SWT.TOGGLE);
			setChecked(false);
		}

		@Override
		public void run() {
			if (isChecked()) {
				showDefault();
			} else {
				showEditor();
			}
		}

	}

	private final IAction viewSourceAction = new ViewSourceAction();

	private boolean spellCheckingEnabled;

	private final int style;

	private Mode mode;

	private AbstractRenderingEngine renderingEngine;

	private BrowserPreviewViewer browserViewer;

	public RichTextAttributeEditor(TaskDataModel manager, TaskRepository taskRepository, TaskAttribute taskAttribute) {
		this(manager, taskRepository, taskAttribute, SWT.MULTI);
	}

	public RichTextAttributeEditor(TaskDataModel manager, TaskRepository taskRepository, TaskAttribute taskAttribute,
			int style) {
		this(manager, taskRepository, taskAttribute, style, null, null);
	}

	public RichTextAttributeEditor(TaskDataModel manager, TaskRepository taskRepository, TaskAttribute taskAttribute,
			int style, IContextService contextService, AbstractTaskEditorExtension extension) {
		super(manager, taskAttribute);
		this.taskRepository = taskRepository;
		this.style = style;
		this.contextService = contextService;
		this.extension = extension;
		if ((style & SWT.MULTI) != 0) {
			setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.MULTIPLE));
		} else {
			setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
		}
		setMode(Mode.DEFAULT);
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		Assert.isNotNull(mode);
		this.mode = mode;
	}

	private void installListeners(final SourceViewer viewer) {
		viewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				// filter out events caused by text presentation changes, e.g. annotation drawing
				String value = viewer.getTextWidget().getText();
				if (!getValue().equals(value)) {
					setValue(value);
					EditorUtil.ensureVisible(viewer.getTextWidget());
				}
			}
		});
		// ensure that tab traverses to next control instead of inserting a tab character unless editing multi-line text
		if ((style & SWT.MULTI) != 0 && mode != Mode.DEFAULT) {
			viewer.getTextWidget().addListener(SWT.Traverse, new Listener() {
				public void handleEvent(Event event) {
					switch (event.detail) {
					case SWT.TRAVERSE_TAB_NEXT:
					case SWT.TRAVERSE_TAB_PREVIOUS:
						event.doit = true;
						break;
					}
				}
			});
		}
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public boolean isSpellCheckingEnabled() {
		return spellCheckingEnabled;
	}

	public void setSpellCheckingEnabled(boolean spellCheckingEnabled) {
		this.spellCheckingEnabled = spellCheckingEnabled;
	}

	public void setValue(String value) {
		getAttributeMapper().setValue(getTaskAttribute(), value);
		attributeChanged();
	}

	/** Configures annotation model for spell checking. */
	private void configureAsEditor(SourceViewer viewer, Document document) {
		AnnotationModel annotationModel = new AnnotationModel();
		viewer.showAnnotations(false);
		viewer.showAnnotationsOverview(false);
		IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
		final SourceViewerDecorationSupport support = new SourceViewerDecorationSupport(viewer, null, annotationAccess,
				EditorsUI.getSharedTextColors());
		Iterator<?> e = new MarkerAnnotationPreferences().getAnnotationPreferences().iterator();
		while (e.hasNext()) {
			support.setAnnotationPreference((AnnotationPreference) e.next());
		}
		support.install(EditorsUI.getPreferenceStore());
		viewer.getTextWidget().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				support.uninstall();
			}
		});
		//viewer.getTextWidget().setIndent(2);
		viewer.setDocument(document, annotationModel);
	}

	private RepositoryTextViewerConfiguration installHyperlinkPresenter(SourceViewer viewer) {
		RepositoryTextViewerConfiguration configuration = new RepositoryTextViewerConfiguration(taskRepository, false);
		configuration.setMode(getMode());

		// do not configure viewer, this has already been done in extension

		AbstractHyperlinkTextPresentationManager manager;
		if (getMode() == Mode.DEFAULT) {
			manager = new HighlightingHyperlinkTextPresentationManager();
			manager.setHyperlinkDetectors(configuration.getDefaultHyperlinkDetectors(viewer, null));
			manager.install(viewer);

			manager = new TaskHyperlinkTextPresentationManager();
			manager.setHyperlinkDetectors(configuration.getDefaultHyperlinkDetectors(viewer, Mode.TASK));
			manager.install(viewer);
		} else if (getMode() == Mode.TASK_RELATION) {
			manager = new TaskHyperlinkTextPresentationManager();
			manager.setHyperlinkDetectors(configuration.getDefaultHyperlinkDetectors(viewer, Mode.TASK_RELATION));
			manager.install(viewer);
		}

		return configuration;
	}

	private SourceViewer configure(final SourceViewer viewer, boolean readOnly) {
		// do this before setting the document to not require invalidating the presentation
		installHyperlinkPresenter(viewer);

		Document document = new Document(getValue());
		if (readOnly) {
			viewer.setDocument(document);
			if (extension != null) {
				// setting view source action
				viewer.getControl().setData(ViewSourceHandler.VIEW_SOURCE_ACTION, viewSourceAction);
				viewer.getControl().addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent e) {
						ViewSourceHandler.setChecked(getViewer() == defaultViewer);
					}
				});
			}
		} else {
			configureAsEditor(viewer, document);
			installListeners(viewer);
			viewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		}

		// enable cut/copy/paste
		EditorUtil.setTextViewer(viewer.getTextWidget(), viewer);
		viewer.setEditable(!readOnly);
		viewer.getTextWidget().setFont(getFont());
		toolkit.adapt(viewer.getControl(), false, false);

		return viewer;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		this.toolkit = toolkit;

		int style = this.style;
		if (!isReadOnly() && (style & TasksUiInternal.SWT_NO_SCROLL) == 0) {
			style |= SWT.V_SCROLL;
		}

		if (extension != null || renderingEngine != null) {
			editorComposite = new Composite(parent, SWT.NULL);
			editorLayout = new StackLayout() {
				@Override
				protected Point computeSize(Composite composite, int hint, int hint2, boolean flushCache) {
					return topControl.computeSize(hint, hint2, flushCache);
				}
			};
			editorComposite.setLayout(editorLayout);
			setControl(editorComposite);

			if (extension != null) {
				if (isReadOnly()) {
					editorViewer = extension.createViewer(taskRepository, editorComposite, style);
				} else {
					editorViewer = extension.createEditor(taskRepository, editorComposite, style);
					editorViewer.getTextWidget().addFocusListener(new FocusListener() {
						public void focusGained(FocusEvent e) {
							setContext();
						}

						public void focusLost(FocusEvent e) {
							unsetContext();
						}
					});
					editorViewer.getTextWidget().addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							unsetContext();
						}
					});
				}
				configure(editorViewer, isReadOnly());
				show(editorViewer);
			} else {
				defaultViewer = createDefaultEditor(editorComposite, style);
				configure(defaultViewer, isReadOnly());
				show(defaultViewer);
			}
		} else {
			defaultViewer = createDefaultEditor(parent, style);
			configure(defaultViewer, isReadOnly());
			setControl(defaultViewer.getControl());
		}
	}

	private SourceViewer createDefaultEditor(Composite parent, int styles) {
		SourceViewer defaultEditor = new RepositoryTextViewer(taskRepository, parent, styles | SWT.WRAP);

		RepositoryTextViewerConfiguration viewerConfig = new RepositoryTextViewerConfiguration(taskRepository,
				isSpellCheckingEnabled());
		viewerConfig.setMode(getMode());
		defaultEditor.configure(viewerConfig);

		return defaultEditor;
	}

	public SourceViewer getDefaultViewer() {
		if (defaultViewer == null) {
			defaultViewer = createDefaultEditor(editorComposite, style);
			configure(defaultViewer, isReadOnly());

			// fixed font size
			defaultViewer.getTextWidget().setFont(JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT));
			// adapt maximize action
			defaultViewer.getControl().setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION,
					editorViewer.getControl().getData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION));
			// adapt menu to the new viewer
			installMenu(defaultViewer.getControl(), editorViewer.getControl().getMenu());
		}
		return defaultViewer;
	}

	private void installMenu(final Control control, Menu menu) {
		if (menu != null) {
			control.setMenu(menu);
			control.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					control.setMenu(null);
				}
			});
		}
	}

	private Font getFont() {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		Font font = themeManager.getCurrentTheme().getFontRegistry().get(CommonThemes.FONT_EDITOR_COMMENT);
		return font;
	}

	private SourceViewer getPreviewViewer() {
		if (extension == null) {
			return null;
		}

		// construct as needed
		if (previewViewer == null) {
			previewViewer = extension.createViewer(taskRepository, editorComposite, style);
			configure(previewViewer, true);
			// adapt maximize action
			previewViewer.getControl().setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION,
					editorViewer.getControl().getData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION));
		}
		Document document = new Document(editorViewer.getDocument().get());
		previewViewer.setDocument(document);
		return previewViewer;
	}

	public SourceViewer getEditorViewer() {
		return editorViewer;
	}

	public SourceViewer getViewer() {
		if (editorLayout == null) {
			return defaultViewer;
		}
		if (defaultViewer != null && editorLayout.topControl == defaultViewer.getControl()) {
			return defaultViewer;
		} else if (previewViewer != null && editorLayout.topControl == previewViewer.getControl()) {
			return previewViewer;
		} else {
			return editorViewer;
		}
	}

	private void setContext() {
		if (contextService == null) {
			return;
		}
		if (contextActivation != null) {
			contextService.deactivateContext(contextActivation);
			contextActivation = null;
		}
		if (contextService != null && extension.getEditorContextId() != null) {
			contextActivation = contextService.activateContext(extension.getEditorContextId());
		}
	}

	/**
	 * Brings <code>viewer</code> to top.
	 */
	private void show(SourceViewer viewer) {
		show(viewer.getControl());
	}

	/**
	 * Brings <code>control</code> to top.
	 */
	private void show(Control control) {
		// no extension is available
		if (editorComposite == null) {
			return;
		}

		editorLayout.topControl = control;
		if (editorComposite.getParent().getLayout() instanceof FillWidthLayout) {
			((FillWidthLayout) editorComposite.getParent().getLayout()).flush();
		}
		editorComposite.layout();
		control.setFocus();
	}

	public void showDefault() {
		show(getDefaultViewer());
	}

	public void showPreview() {
		if (!isReadOnly()) {
			show(getPreviewViewer());
		}
	}

	public void showEditor() {
		if (getEditorViewer() != null) {
			show(getEditorViewer());
		} else {
			show(getDefaultViewer());
		}
	}

	private void unsetContext() {
		if (contextService == null) {
			return;
		}
		if (contextActivation != null) {
			contextService.deactivateContext(contextActivation);
			contextActivation = null;
		}
	}

	public boolean hasPreview() {
		return extension != null && !isReadOnly();
	}

	public boolean hasBrowser() {
		return renderingEngine != null;
	}

	private BrowserPreviewViewer getBrowserViewer() {
		if (editorComposite == null || renderingEngine == null) {
			return null;
		}

		if (browserViewer == null) {
			browserViewer = new BrowserPreviewViewer(getModel().getTaskRepository(), renderingEngine);
			browserViewer.createControl(editorComposite, toolkit);
		}
		return browserViewer;
	}

	public AbstractRenderingEngine getRenderingEngine() {
		return renderingEngine;
	}

	public void setRenderingEngine(AbstractRenderingEngine renderingEngine) {
		this.renderingEngine = renderingEngine;
	}

	public void showBrowser() {
		BrowserPreviewViewer viewer = getBrowserViewer();
		viewer.update(getValue());
		if (viewer != null) {
			show(viewer.getControl());
		}
	}

}
