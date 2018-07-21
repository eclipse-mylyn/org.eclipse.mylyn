/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Raphael Ackermann - spell checking support on bug 195514
 *     Jingwen Ou - extensibility improvements
 *     David Green - fix for bug 256702 
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.commons.ui.FillWidthLayout;
import org.eclipse.mylyn.commons.ui.compatibility.CommonThemes;
import org.eclipse.mylyn.commons.workbench.editors.CommonTextSupport;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.tasks.ui.commands.ViewSourceHandler;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
public class RichTextEditor {

	public enum State {
		DEFAULT, BROWSER, EDITOR, PREVIEW;
	};

	public static class StateChangedEvent {

		public State state;

	}

	public interface StateChangedListener {

		public void stateChanged(StateChangedEvent event);

	}

	public class ViewSourceAction extends Action {

		public ViewSourceAction() {
			super(Messages.RichTextAttributeEditor_Viewer_Source, SWT.TOGGLE);
			setChecked(false);
			setEnabled(false);
		}

		@Override
		public void run() {
			if (isChecked()) {
				showDefault();
			} else {
				showEditor();
			}
			if (editorLayout != null) {
				EditorUtil.reflow(editorLayout.topControl);
			}
			ViewSourceHandler.setChecked(isChecked());
		}

	}

	private static final String KEY_TEXT_VERSION = "org.eclipse.mylyn.tasks.ui.textVersion"; //$NON-NLS-1$

	private BrowserPreviewViewer browserViewer;

	private IContextActivation contextActivation;

	private final IContextService contextService;

	private Control control;

	private SourceViewer defaultViewer;

	private Composite editorComposite;

	private StackLayout editorLayout;

	private SourceViewer editorViewer;

	private final AbstractTaskEditorExtension extension;

	private Mode mode;

	private SourceViewer previewViewer;

	boolean readOnly;

	private AbstractRenderingEngine renderingEngine;

	private final TaskRepository repository;

	private boolean spellCheckingEnabled;

	private final int style;

	private FormToolkit toolkit;

	private final IAction viewSourceAction;

	private String text;

	/**
	 * Changed each time text is updated.
	 */
	private int textVersion;

	private final ListenerList stateChangedListeners = new ListenerList(ListenerList.IDENTITY);

	private final ITask task;

	@Deprecated
	public RichTextEditor(TaskRepository repository, int style) {
		this(repository, style, null, null, null);
	}

	@Deprecated
	public RichTextEditor(TaskRepository repository, int style, IContextService contextService,
			AbstractTaskEditorExtension extension) {
		this(repository, style, contextService, extension, null);
	}

	public RichTextEditor(TaskRepository repository, int style, IContextService contextService,
			AbstractTaskEditorExtension extension, ITask task) {
		this.repository = repository;
		this.style = style;
		this.contextService = contextService;
		this.extension = extension;
		this.text = ""; //$NON-NLS-1$
		this.viewSourceAction = new ViewSourceAction();
		setMode(Mode.DEFAULT);
		this.task = task;
	}

	private SourceViewer configure(final SourceViewer viewer, Document document, boolean readOnly) {
		// do this before setting the document to not require invalidating the presentation
		installHyperlinkPresenter(viewer, repository, task, getMode());

		updateDocument(viewer, document, readOnly);
		if (readOnly) {
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
			installListeners(viewer);
			viewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		}

		// enable cut/copy/paste
		CommonTextSupport.setTextViewer(viewer.getTextWidget(), viewer);
		viewer.setEditable(!readOnly);
		viewer.getTextWidget().setFont(getFont());
		if (toolkit != null) {
			toolkit.adapt(viewer.getControl(), false, false);
		}
		EditorUtil.addScrollListener(viewer.getTextWidget());
		return viewer;
	}

	/** Configures annotation model for spell checking. */
	private void updateDocument(SourceViewer viewer, Document document, boolean readOnly) {
		if (new Integer(this.textVersion).equals(viewer.getData(KEY_TEXT_VERSION))) {
			// already up-to-date, skip re-loading of the document
			return;
		}

		if (readOnly) {
			viewer.setDocument(document);
		} else {
			AnnotationModel annotationModel = new AnnotationModel();
			viewer.showAnnotations(false);
			viewer.showAnnotationsOverview(false);
			IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
			final SourceViewerDecorationSupport support = new SourceViewerDecorationSupport(viewer, null,
					annotationAccess, EditorsUI.getSharedTextColors());
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
		viewer.setData(KEY_TEXT_VERSION, this.textVersion);
	}

	public void createControl(Composite parent, FormToolkit toolkit) {
		this.toolkit = toolkit;

		int style = this.style;
		if (!isReadOnly() && (style & SWT.NO_SCROLL) == 0) {
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
					editorViewer = extension.createViewer(repository, editorComposite, style,
							createHyperlinkDetectorContext());
				} else {
					editorViewer = extension.createEditor(repository, editorComposite, style,
							createHyperlinkDetectorContext());
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
				configure(editorViewer, new Document(getText()), isReadOnly());
				show(editorViewer.getControl());
			} else {
				defaultViewer = createDefaultEditor(editorComposite, style);
				configure(defaultViewer, new Document(getText()), isReadOnly());
				show(defaultViewer.getControl());
			}

			if (!isReadOnly() && (style & SWT.NO_SCROLL) == 0) {
				editorComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
			}

			viewSourceAction.setEnabled(true);
		} else {
			defaultViewer = createDefaultEditor(parent, style);
			configure(defaultViewer, new Document(getText()), isReadOnly());
			setControl(defaultViewer.getControl());

			viewSourceAction.setEnabled(false);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private IAdaptable createHyperlinkDetectorContext() {
		return new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if (adapter == TaskRepository.class) {
					return repository;
				}
				if (adapter == ITask.class) {
					return task;
				}
				return null;
			}
		};
	}

	private SourceViewer createDefaultEditor(Composite parent, int styles) {
		SourceViewer defaultEditor = new SourceViewer(parent, null, styles | SWT.WRAP);

		RepositoryTextViewerConfiguration viewerConfig = new RepositoryTextViewerConfiguration(repository, task,
				isSpellCheckingEnabled() && !isReadOnly());
		viewerConfig.setMode(getMode());
		defaultEditor.configure(viewerConfig);

		return defaultEditor;
	}

	private BrowserPreviewViewer getBrowserViewer() {
		if (editorComposite == null || renderingEngine == null) {
			return null;
		}

		if (browserViewer == null) {
			browserViewer = new BrowserPreviewViewer(getRepository(), renderingEngine);
			browserViewer.createControl(editorComposite, toolkit);
		}
		return browserViewer;
	}

	public Control getControl() {
		return control;
	}

	public SourceViewer getDefaultViewer() {
		if (defaultViewer == null) {
			defaultViewer = createDefaultEditor(editorComposite, style);
			configure(defaultViewer, new Document(getText()), isReadOnly());

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

	public SourceViewer getEditorViewer() {
		return editorViewer;
	}

	private Font getFont() {
		if (mode == Mode.DEFAULT) {
			IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
			Font font = themeManager.getCurrentTheme().getFontRegistry().get(CommonThemes.FONT_EDITOR_COMMENT);
			return font;
		} else {
			return EditorUtil.TEXT_FONT;
		}
	}

	public Mode getMode() {
		return mode;
	}

	/**
	 * @return The preview source viewer or null if there is no extension available or the attribute is read only
	 */
	private SourceViewer getPreviewViewer() {
		if (extension == null) {
			return null;
		}

		// construct as needed
		if (previewViewer == null) {
			// previewer should always have a vertical scroll bar if it's editable
			int previewViewerStyle = style;
			if (getEditorViewer() != null) {
				previewViewerStyle |= SWT.V_SCROLL;
			}
			previewViewer = extension.createViewer(repository, editorComposite, previewViewerStyle,
					createHyperlinkDetectorContext());
			configure(previewViewer, new Document(getText()), true);
			// adapt maximize action
			previewViewer.getControl().setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION,
					editorViewer.getControl().getData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION));
			installMenu(previewViewer.getControl(), editorViewer.getControl().getMenu());
			//set the background color in case there is an incoming to show
			previewViewer.getTextWidget().setBackground(editorComposite.getBackground());
		}
		return previewViewer;
	}

	public AbstractRenderingEngine getRenderingEngine() {
		return renderingEngine;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public String getText() {
		return this.text;
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

	public IAction getViewSourceAction() {
		return viewSourceAction;
	}

	public boolean hasBrowser() {
		return renderingEngine != null;
	}

	public boolean hasPreview() {
		return extension != null && !isReadOnly();
	}

	public static RepositoryTextViewerConfiguration installHyperlinkPresenter(ISourceViewer viewer,
			TaskRepository repository, ITask task, Mode mode) {
		RepositoryTextViewerConfiguration configuration = new RepositoryTextViewerConfiguration(repository, task, false);
		configuration.setMode(mode);

		// do not configure viewer, this has already been done in extension

		AbstractHyperlinkTextPresentationManager manager;
		if (mode == Mode.DEFAULT) {
			manager = new HighlightingHyperlinkTextPresentationManager();
			manager.setHyperlinkDetectors(configuration.getDefaultHyperlinkDetectors(viewer, null));
			manager.install(viewer);

			manager = new TaskHyperlinkTextPresentationManager();
			manager.setHyperlinkDetectors(configuration.getDefaultHyperlinkDetectors(viewer, Mode.TASK));
			manager.install(viewer);
		} else if (mode == Mode.TASK_RELATION) {
			manager = new TaskHyperlinkTextPresentationManager();
			manager.setHyperlinkDetectors(configuration.getDefaultHyperlinkDetectors(viewer, Mode.TASK_RELATION));
			manager.install(viewer);
		}

		return configuration;
	}

	private void installListeners(final SourceViewer viewer) {
		viewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				// filter out events caused by text presentation changes, e.g. annotation drawing
				String value = viewer.getTextWidget().getText();
				if (!RichTextEditor.this.text.equals(value)) {
					RichTextEditor.this.text = value;
					RichTextEditor.this.textVersion++;
					viewer.setData(KEY_TEXT_VERSION, RichTextEditor.this.textVersion);
					valueChanged(value);
					CommonFormUtil.ensureVisible(viewer.getTextWidget());
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

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isSpellCheckingEnabled() {
		return spellCheckingEnabled;
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

	private void setControl(Control control) {
		this.control = control;
	}

	public void setMode(Mode mode) {
		Assert.isNotNull(mode);
		this.mode = mode;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setRenderingEngine(AbstractRenderingEngine renderingEngine) {
		this.renderingEngine = renderingEngine;
	}

	public void setSpellCheckingEnabled(boolean spellCheckingEnabled) {
		this.spellCheckingEnabled = spellCheckingEnabled;
	}

	public void setText(String value) {
		this.text = value;
		this.textVersion++;
		SourceViewer viewer = getViewer();
		if (viewer != null) {
			viewer.getDocument().set(value);
		}
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
		fireStateChangedEvent();
	}

	protected void fireStateChangedEvent() {
		if (stateChangedListeners.isEmpty()) {
			return;
		}
		StateChangedEvent event = new StateChangedEvent();
		if (defaultViewer != null && defaultViewer.getControl() == editorLayout.topControl) {
			event.state = State.DEFAULT;
		} else if (editorViewer != null && editorViewer.getControl() == editorLayout.topControl) {
			event.state = State.EDITOR;
		} else if (previewViewer != null && previewViewer.getControl() == editorLayout.topControl) {
			event.state = State.PREVIEW;
		} else if (browserViewer != null && browserViewer.getControl() == editorLayout.topControl) {
			event.state = State.BROWSER;
		}
		Object[] listeners = stateChangedListeners.getListeners();
		for (Object listener : listeners) {
			((StateChangedListener) listener).stateChanged(event);
		}
	}

	/**
	 * Brings <code>viewer</code> to top.
	 */
	private void show(SourceViewer viewer) {
		// WikiText modifies the document therefore, set a new document every time a viewer is changed to synchronize content between viewers 
		// ensure that editor has an annotation model
		updateDocument(viewer, new Document(getText()), !viewer.isEditable());
		show(viewer.getControl());
	}

	public void showBrowser() {
		BrowserPreviewViewer viewer = getBrowserViewer();
		viewer.update(getText());
		if (viewer != null) {
			show(viewer.getControl());
		}
	}

	public void showDefault() {
		show(getDefaultViewer());
	}

	public void showEditor() {
		if (getEditorViewer() != null) {
			show(getEditorViewer());
		} else {
			show(getDefaultViewer());
		}
	}

	private void showPreview(boolean sticky) {
		if (!isReadOnly() && getPreviewViewer() != null) {
			show(getPreviewViewer());
		}
	}

	public void showPreview() {
		showPreview(true);
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

	protected void valueChanged(String value) {
	}

	public void enableAutoTogglePreview() {
		if (!isReadOnly() && getPreviewViewer() != null) {
			final MouseAdapter listener = new MouseAdapter() {
				private boolean toggled;

				@Override
				public void mouseUp(MouseEvent e) {
					if (!toggled && e.count == 1) {
						// delay switching in case user intended to select text
						Display.getDefault().timerExec(Display.getDefault().getDoubleClickTime(), new Runnable() {

							public void run() {
								if (previewViewer.getTextWidget() == null || previewViewer.getTextWidget().isDisposed()) {
									return;
								}

								if (previewViewer.getTextWidget().getSelectionCount() == 0) {
									int offset = previewViewer.getTextWidget().getCaretOffset();
									showEditor();
									editorViewer.getTextWidget().setCaretOffset(offset);

									// only do this once, let the user manage toggling from then on
									toggled = true;
								}
							}
						});
					}
				}
			};
			previewViewer.getTextWidget().addMouseListener(listener);
//			editorViewer.getTextWidget().addFocusListener(new FocusAdapter() {
//				@Override
//				public void focusLost(FocusEvent e) {
//					if (!previewSticky) {
//						showPreview(false);
//					}
//				}
//			});
		}
	}

	/**
	 * Sets the background color for all instantiated viewers
	 * 
	 * @param color
	 */
	public void setBackground(Color color) {
		if (editorComposite != null && !editorComposite.isDisposed()) {
			editorComposite.setBackground(color);
			for (Control child : editorComposite.getChildren()) {
				child.setBackground(color);
			}
		}
	}

	public void addStateChangedListener(StateChangedListener listener) {
		stateChangedListeners.add(listener);
	}

	public void removeStateChangedListener(StateChangedListener listener) {
		stateChangedListeners.remove(listener);
	}

}
