/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.AbstractAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension6;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.actions.PreviewOutlineItemAction;
import org.eclipse.mylyn.internal.wikitext.ui.editor.actions.SetMarkupLanguageAction;
import org.eclipse.mylyn.internal.wikitext.ui.editor.operations.AbstractDocumentCommand;
import org.eclipse.mylyn.internal.wikitext.ui.editor.operations.CommandManager;
import org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.Preferences;
import org.eclipse.mylyn.internal.wikitext.ui.editor.reconciler.MarkupMonoReconciler;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.MarkupDocumentProvider;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.MarkupTokenScanner;
import org.eclipse.mylyn.internal.wikitext.ui.util.NlsResourceBundle;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.mylyn.wikitext.ui.editor.MarkupSourceViewerConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * A text editor for editing lightweight markup. Can be configured to accept any {@link MarkupLanguage}, with pluggable content assist,
 * validation, and cheat-sheet help content.
 *
 * @author David Green
 * @author Nicolas Bros
 */
public class MarkupEditor extends TextEditor implements IShowInTarget, IShowInSource, CommandManager {
	private static final String CSS_CLASS_EDITOR_PREVIEW = "editorPreview"; //$NON-NLS-1$

	private static final String RULER_CONTEXT_MENU_ID = "org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor.ruler"; //$NON-NLS-1$

	/**
	 * the name of the property that stores the markup language name for per-file preference
	 *
	 * @see IFile#setPersistentProperty(QualifiedName, String) property
	 */
	private static final String MARKUP_LANGUAGE = "markupLanguage"; //$NON-NLS-1$

	/**
	 * the source editing context
	 */
	public static final String CONTEXT = "org.eclipse.mylyn.wikitext.ui.editor.markupSourceContext"; //$NON-NLS-1$

	/**
	 * the ID of the editor
	 */
	public static final String ID = "org.eclipse.mylyn.wikitext.ui.editor.markupEditor"; //$NON-NLS-1$

	private static final String[] SHOW_IN_TARGETS = { //
			"org.eclipse.ui.views.ResourceNavigator", //$NON-NLS-1$
			"org.eclipse.jdt.ui.PackageExplorer", //$NON-NLS-1$
			"org.eclipse.ui.navigator.ProjectExplorer", // 3.5 //$NON-NLS-1$
			IPageLayout.ID_OUTLINE };

	private static IShowInTargetList SHOW_IN_TARGET_LIST = () -> SHOW_IN_TARGETS;

	private IDocument document;

	private IDocumentListener documentListener;

	private boolean previewDirty = true;

	private boolean outlineDirty = true;

	private Browser browser;

	private MarkupEditorOutline outlinePage;

	private OutlineItem outlineModel;

	private final OutlineParser outlineParser = new OutlineParser();

	{
		outlineParser.setLabelMaxLength(48);
		outlineModel = outlineParser.createRootItem();
	}

	private boolean disableReveal = false;

	private ISourceViewer viewer;

	private IPropertyChangeListener preferencesListener;

	private IDocumentPartitioningListener documentPartitioningListener;

	private final MarkupSourceViewerConfiguration sourceViewerConfiguration;

	private CTabItem sourceTab;

	private ProjectionSupport projectionSupport;

	private Map<String, HeadingProjectionAnnotation> projectionAnnotationById;

	private boolean updateJobScheduled = false;

	protected int documentGeneration = 0;

	public static final String EDITOR_SOURCE_VIEWER = "org.eclipse.mylyn.wikitext.ui.editor.sourceViewer"; //$NON-NLS-1$

	private UIJob updateOutlineJob;

	private IFoldingStructure foldingStructure;

	private CTabFolder tabFolder;

	private CTabItem previewTab;

	public MarkupEditor() {
		setDocumentProvider(new MarkupDocumentProvider());
		sourceViewerConfiguration = new MarkupSourceViewerConfiguration(getPreferenceStore());
		sourceViewerConfiguration.setOutline(outlineModel);
		sourceViewerConfiguration.setShowInTarget(this);
		setSourceViewerConfiguration(sourceViewerConfiguration);
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		sourceViewerConfiguration.initializeDefaultFonts();
		tabFolder = new CTabFolder(parent, SWT.BOTTOM);

		// Disable next/previous page traversal in tab folder. Instead the
		// workbench commands nextSubTab and previousSubTab are used. These
		// events swallow the key combo Ctrl+PgDn/PgUp which brakes normal
		// editor tab switching in the default key binding scheme. This
		// solution is similar to how MultiPageEditorPart works.
		tabFolder.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_PAGE_NEXT || e.detail == SWT.TRAVERSE_PAGE_PREVIOUS) {
				e.doit = false;
			}
		});

		{
			sourceTab = new CTabItem(tabFolder, SWT.NONE);
			updateSourceTabLabel();

			viewer = new MarkupProjectionViewer(tabFolder, ruler, getOverviewRuler(), isOverviewRulerVisible(),
					styles | SWT.WRAP);

			sourceTab.setControl(((Viewer) viewer).getControl());
		}

		try {
			previewTab = new CTabItem(tabFolder, SWT.NONE);
			previewTab.setText(Messages.MarkupEditor_preview);
			previewTab.setToolTipText(Messages.MarkupEditor_preview_tooltip);

			browser = new Browser(tabFolder, SWT.NONE);
			// bug 260479: open hyperlinks in a browser
			browser.addLocationListener(new LocationListener() {
				@Override
				public void changed(LocationEvent event) {
					event.doit = false;
				}

				private boolean tryToOpenAsWorkspaceFile(String location) {
					if (getEditorInput() instanceof IURIEditorInput uriInput) {
						try {
							URI locationURI = uriInput.getURI().resolve(location);
							if (locationURI != null && "file".equals(locationURI.getScheme())) { //$NON-NLS-1$
								IFile[] files = ResourcesPlugin.getWorkspace()
										.getRoot()
										.findFilesForLocationURI(locationURI);
								if (files.length > 0) {
									// it is a workspace resource -> open using an editor
									IEditorPart editor = IDE.openEditor(getEditorSite().getPage(), files[0]);
									if (editor instanceof MarkupEditor markupEditor) {
										markupEditor.showPreview(null);
									}
									return true;
								}
							}
						} catch (Exception e) {
							logErrorOpeningAsWorkspaceFile(e);
						}
					}
					return false;
				}

				@Override
				public void changing(LocationEvent event) {
					// if it looks like an absolute URL
					if (event.location.matches("([a-zA-Z]{3,8})://?.*")) { //$NON-NLS-1$
						// workaround for browser problem (bug 262043)
						int idxOfSlashHash = event.location.indexOf("/#"); //$NON-NLS-1$
						if (idxOfSlashHash != -1) {
							// allow javascript-based scrolling to work
							if (!event.location.startsWith("file:///#")) { //$NON-NLS-1$
								event.doit = false;
							}
							return;
						}
						// workaround end

						event.doit = false;
						if (tryToOpenAsWorkspaceFile(event.location)) {
							return;
						}
						try {
							PlatformUI.getWorkbench()
									.getBrowserSupport()
									.createBrowser("org.eclipse.ui.browser") //$NON-NLS-1$
									.openURL(new URL(event.location));
						} catch (Exception e) {
							new URLHyperlink(new Region(0, 1), event.location).open();
						}
					} else {
						tryToOpenAsWorkspaceFile(event.location);
					}
				}
			});
			previewTab.setControl(browser);
		} catch (SWTError e) {
			// disable preview, the exception is probably due to the internal browser not being available
			if (previewTab != null) {
				previewTab.dispose();
				previewTab = null;
			}
			logPreviewTabUnavailable(e);
		}

		// start special files in "read mode", i.e. preview tab
		IEditorInput ei = getEditorInput();
		if (previewTab != null && ei instanceof IURIEditorInput editorInput) {
			String previewFileNamePattern = WikiTextUiPlugin.getDefault().getPreferences().getPreviewFileNamePattern();
			File file = new File(editorInput.getURI());
			if (previewFileNamePattern != null && file.getName().matches(previewFileNamePattern)) {
				tabFolder.setSelection(previewTab);
			} else {
				tabFolder.setSelection(sourceTab);
			}
		} else {
			tabFolder.setSelection(sourceTab);
		}

		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			@Override
			public void widgetSelected(SelectionEvent selectionevent) {
				if (isShowingPreview()) {
					updatePreview();
				}
			}
		});
		viewer.getTextWidget()
				.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> updateOutlineSelection()));
		viewer.getTextWidget().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (isRelevantKeyCode(e.keyCode)) {
					updateOutlineSelection();
				}
			}

			private boolean isRelevantKeyCode(int keyCode) {
				// for some reason not all key presses result in a selection change
				switch (keyCode) {
					case SWT.ARROW_DOWN:
					case SWT.ARROW_LEFT:
					case SWT.ARROW_RIGHT:
					case SWT.ARROW_UP:
					case SWT.PAGE_DOWN:
					case SWT.PAGE_UP:
						return true;
				}
				return false;
			}
		});
		viewer.getTextWidget().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				updateOutlineSelection();
			}
		});

		IFocusService focusService = PlatformUI.getWorkbench().getService(IFocusService.class);
		if (focusService != null) {
			focusService.addFocusTracker(viewer.getTextWidget(), MarkupEditor.EDITOR_SOURCE_VIEWER);
		}

		viewer.getTextWidget().setData(MarkupLanguage.class.getName(), getMarkupLanguage());
		viewer.getTextWidget().setData(ISourceViewer.class.getName(), viewer);

		getSourceViewerDecorationSupport(viewer);

		updateDocument();

		if (preferencesListener == null) {
			preferencesListener = event -> {
				if (viewer.getTextWidget() == null || viewer.getTextWidget().isDisposed()) {
					return;
				}
				if (isFontPreferenceChange(event)) {
					viewer.getTextWidget().getDisplay().asyncExec(this::reloadPreferences);
				}
			};
			WikiTextUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(preferencesListener);
		}

		if (isShowingPreview()) {
			updatePreview();
		}

		return viewer;
	}

	private void logPreviewScrollingFailure(SWTException e) {
		WikiTextUiPlugin.getDefault()
				.getLog()
				.log(WikiTextUiPlugin.getDefault()
						.createStatus(format(Messages.MarkupEditor_previewScrollingFailed, e.getMessage()),
								IStatus.WARNING, e));

	}

	private void logPreviewTabUnavailable(SWTError e) {
		WikiTextUiPlugin.getDefault()
				.getLog()
				.log(WikiTextUiPlugin.getDefault()
						.createStatus(format(Messages.MarkupEditor_previewUnavailable, e.getMessage()), IStatus.ERROR,
								e));
	}

	private void logErrorOpeningAsWorkspaceFile(Exception e) {
		WikiTextUiPlugin.getDefault()
				.getLog()
				.log(WikiTextUiPlugin.getDefault()
						.createStatus(format(Messages.MarkupEditor_openWorkspaceFileFailed, e.getMessage()),
								IStatus.ERROR, e));
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		// fix bug 267553: font problems can occur if the default font of the text widget doesn't match the
		//                 default font returned by the token scanner
		if (sourceViewerConfiguration.getDefaultFont() != null) {
			viewer.getTextWidget().setFont(sourceViewerConfiguration.getDefaultFont());
		}

		projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
		projectionSupport.install();

		syncProjectionModeWithPreferences();

		viewer.addProjectionListener(new IProjectionListener() {
			@Override
			public void projectionDisabled() {
				projectionAnnotationById = null;
				saveProjectionPreferences();
			}

			@Override
			public void projectionEnabled() {
				saveProjectionPreferences();
				updateProjectionAnnotations();
			}
		});

		if (!outlineDirty && isFoldingEnabled()) {
			updateProjectionAnnotations();
		}
		JFaceResources.getFontRegistry().addListener(preferencesListener);
	}

	private void reloadPreferences() {
		previewDirty = true;
		syncProjectionModeWithPreferences();
		((MarkupTokenScanner) sourceViewerConfiguration.getMarkupScanner()).reloadPreferences();
		sourceViewerConfiguration.initializeDefaultFonts();
		viewer.invalidateTextPresentation();
	}

	private boolean isFontPreferenceChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(sourceViewerConfiguration.getFontPreference())
				|| event.getProperty().equals(sourceViewerConfiguration.getMonospaceFontPreference())) {
			return true;
		}
		return false;
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		super.handlePreferenceStoreChanged(event);
		reloadPreferences();
	}

	private void syncProjectionModeWithPreferences() {
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		if (viewer.isProjectionMode() != WikiTextUiPlugin.getDefault().getPreferences().isEditorFolding()) {
			viewer.doOperation(ProjectionViewer.TOGGLE);
		}
	}

	@Override
	public void updatePartControl(IEditorInput input) {
		super.updatePartControl(input);
		updateDocument();
	}

	public void saveProjectionPreferences() {
		if (isFoldingEnabled() != WikiTextUiPlugin.getDefault().getPreferences().isEditorFolding()) {
			Preferences preferences = WikiTextUiPlugin.getDefault().getPreferences().clone();
			preferences.setEditorFolding(isFoldingEnabled());
			preferences.save(WikiTextUiPlugin.getDefault().getPreferenceStore(), false);
		}
	}

	@Override
	public void dispose() {
		if (document != null) {
			if (documentListener != null) {
				document.removeDocumentListener(documentListener);
			}
			if (documentPartitioningListener != null) {
				document.removeDocumentPartitioningListener(documentPartitioningListener);
			}
			document = null;
		}
		if (preferencesListener != null) {
			WikiTextUiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(preferencesListener);
			JFaceResources.getFontRegistry().addListener(preferencesListener);
			preferencesListener = null;
		}
		super.dispose();
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor(); // ORDER DEPENDENCY
		setHelpContextId(CONTEXT); // ORDER DEPENDENCY
		setRulerContextMenuId(RULER_CONTEXT_MENU_ID);

	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		updateDocument();
		IFile file = getFile();
		if (sourceViewerConfiguration != null) {
			sourceViewerConfiguration.setFile(file);
		}
		initializeMarkupLanguage(input);
		outlineModel.setResourcePath(file == null ? null : file.getFullPath().toString());
	}

	private void updateDocument() {
		if (getSourceViewer() != null) {
			IDocument previousDocument = document;
			document = getSourceViewer().getDocument();
			if (previousDocument == document) {
				return;
			}
			if (previousDocument != null && documentListener != null) {
				previousDocument.removeDocumentListener(documentListener);
			}
			if (previousDocument != null && documentPartitioningListener != null) {
				previousDocument.removeDocumentPartitioningListener(documentPartitioningListener);
			}
			if (document != null) {
				if (documentListener == null) {
					documentListener = new IDocumentListener() {
						@Override
						public void documentAboutToBeChanged(DocumentEvent event) {
						}

						@Override
						public void documentChanged(DocumentEvent event) {
							previewDirty = true;
							outlineDirty = true;
							synchronized (MarkupEditor.this) {
								++documentGeneration;
							}
							scheduleOutlineUpdate();
							if (isShowingPreview()) {
								updatePreview();
							}
						}

					};
				}
				document.addDocumentListener(documentListener);
				if (documentPartitioningListener == null) {
					documentPartitioningListener = document -> scheduleOutlineUpdate();
				}
				document.addDocumentPartitioningListener(documentPartitioningListener);
			}

			previewDirty = true;
			outlineDirty = true;
			updateOutline();
			if (isShowingPreview()) {
				updatePreview();
			}
		}
	}

	/**
	 * JavaScript that returns the current top scroll position of the browser widget
	 */
	private static final String JAVASCRIPT_GETSCROLLTOP = """
			function getScrollTop() {\s\
			  if(typeof pageYOffset!='undefined') return pageYOffset;\
			  else{\
			var B=document.body;\
			var D=document.documentElement;\
			D=(D.clientHeight)?D:B;return D.scrollTop;}\
			}; return getScrollTop();"""; //$NON-NLS-1$

	/**
	 * updates the preview
	 */
	private void updatePreview() {
		updatePreview(null);
	}

	/**
	 * updates the preview and optionally reveal the section that corresponds to the given outline item.
	 *
	 * @param outlineItem
	 *            the outline item, or null
	 */
	private void updatePreview(final OutlineItem outlineItem) {
		if (previewDirty && browser != null) {
			Object result = null;
			try {
				result = browser.evaluate(JAVASCRIPT_GETSCROLLTOP);
			} catch (SWTException e) {
				// bug 517281 javascript fails for some Linux configurations
				logPreviewScrollingFailure(e);
			}
			final int verticalScrollbarPos = result != null ? ((Number) result).intValue() : 0;
			String xhtml = null;
			if (document == null) {
				xhtml = "<?xml version=\"1.0\" ?><html xmlns=\"http://www.w3.org/1999/xhtml\"><body></body></html>"; //$NON-NLS-1$
			} else {
				try {
					IFile file = getFile();
					String title = file == null ? "" : file.getName(); //$NON-NLS-1$
					if (title.lastIndexOf('.') != -1) {
						title = title.substring(0, title.lastIndexOf('.'));
					}
					StringWriter writer = new StringWriter();
					HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer) {
						@Override
						protected void emitAnchorHref(String href) {
							if (href != null && href.startsWith("#")) { //$NON-NLS-1$
								writer.writeAttribute("onclick", //$NON-NLS-1$
										String.format("javascript: window.location.hash = '%s'; return false;", href)); //$NON-NLS-1$
								writer.writeAttribute("href", "#"); //$NON-NLS-1$//$NON-NLS-2$
							} else {
								super.emitAnchorHref(href);
							}
						}

						@Override
						public void beginHeading(int level, Attributes attributes) {
							attributes.appendCssClass(CSS_CLASS_EDITOR_PREVIEW);
							super.beginHeading(level, attributes);
						}

						@Override
						public void beginBlock(BlockType type, Attributes attributes) {
							attributes.appendCssClass(CSS_CLASS_EDITOR_PREVIEW);
							super.beginBlock(type, attributes);
						}
					};
					builder.setTitle(title);

					IPath location = file == null ? null : file.getLocation();
					if (location != null) {
						builder.setBaseInHead(true);
						builder.setBase(location.removeLastSegments(1).toFile().toURI());
					}

					String css = WikiTextUiPlugin.getDefault().getPreferences().getMarkupViewerCss();
					if (css != null && css.length() > 0) {
						builder.addCssStylesheet(new HtmlDocumentBuilder.Stylesheet(new StringReader(css)));
					}

					MarkupLanguage markupLanguage = getMarkupLanguage();
					if (markupLanguage != null) {
						markupLanguage = markupLanguage.clone();
						if (markupLanguage instanceof AbstractMarkupLanguage language) {
							language.setEnableMacros(true);
							language.setFilterGenerativeContents(false);
							language.setBlocksOnly(false);
						}

						MarkupParser markupParser = new MarkupParser();
						markupParser.setBuilder(builder);
						markupParser.setMarkupLanguage(markupLanguage);

						markupParser.parse(document.get());
					} else {
						builder.beginDocument();
						builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
						builder.characters(document.get());
						builder.endBlock();
						builder.endDocument();
					}
					xhtml = writer.toString();
				} catch (Exception e) {
					StringWriter stackTrace = new StringWriter();
					try (PrintWriter writer = new PrintWriter(stackTrace)) {
						e.printStackTrace(writer);
					}

					StringWriter documentWriter = new StringWriter();
					HtmlDocumentBuilder builder = new HtmlDocumentBuilder(documentWriter);
					builder.beginDocument();
					builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
					builder.characters(stackTrace.toString());
					builder.endBlock();
					builder.endDocument();

					xhtml = documentWriter.toString();
				}
			}
			browser.addProgressListener(new ProgressAdapter() {

				@Override
				public void completed(ProgressEvent event) {
					browser.removeProgressListener(this);
					if (outlineItem != null) {
						revealInBrowser(outlineItem);
					} else {
						browser.execute(String.format("window.scrollTo(0,%d);", verticalScrollbarPos)); //$NON-NLS-1$
					}
				}

			});
			browser.setText(xhtml);
			previewDirty = false;
		} else if (outlineItem != null && browser != null) {
			revealInBrowser(outlineItem);
		}
	}

	public IFile getFile() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput fileEditorInput) {
			return fileEditorInput.getFile();
		}
		return null;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if (IContentOutlinePage.class == adapter) {
			if (!isOutlinePageValid()) {
				outlinePage = new MarkupEditorOutline(this);
			}
			return adapter.cast(outlinePage);
		}
		if (adapter == OutlineItem.class) {
			return adapter.cast(getOutlineModel());
		}
		if (adapter == IFoldingStructure.class) {
			if (!isFoldingEnabled()) {
				return null;
			}
			if (foldingStructure == null) {
				foldingStructure = new FoldingStructure(this);
			}
			return adapter.cast(foldingStructure);
		}
		if (adapter == IShowInTargetList.class) {
			return adapter.cast(SHOW_IN_TARGET_LIST);
		}
		return super.getAdapter(adapter);
	}

	public ISourceViewer getViewer() {
		return viewer;
	}

	public OutlineItem getOutlineModel() {
		// ensure that outline model is caught up with current version of document
		if (outlineDirty) {
			updateOutlineNow();
		}
		return outlineModel;
	}

	private void scheduleOutlineUpdate() {
		synchronized (MarkupEditor.this) {
			if (updateJobScheduled) {
				return;
			}
		}
		updateOutlineJob = new UIJob(Messages.MarkupEditor_updateOutline) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				synchronized (MarkupEditor.this) {
					updateJobScheduled = false;
				}
				if (!outlineDirty) {
					return Status.CANCEL_STATUS;
				}
				updateOutline();
				return Status.OK_STATUS;
			}
		};
		updateOutlineJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void scheduled(IJobChangeEvent event) {
				synchronized (MarkupEditor.this) {
					updateJobScheduled = true;
				}
			}

			@Override
			public void done(IJobChangeEvent event) {
				synchronized (MarkupEditor.this) {
					updateJobScheduled = false;
					updateOutlineJob = null;
				}
			}
		});
		updateOutlineJob.setUser(false);
		updateOutlineJob.setSystem(true);
		updateOutlineJob.setPriority(Job.INTERACTIVE);
		updateOutlineJob.schedule(600);
	}

	private void updateOutlineNow() {
		if (!outlineDirty || !isSourceViewerValid()) {
			return;
		}
		// we maintain the outline even if the outline page is not in use, which allows us to use the outline for
		// content assist and other things

		MarkupLanguage markupLanguage = getMarkupLanguage();
		if (markupLanguage == null) {
			return;
		}
		final MarkupLanguage language = markupLanguage.clone();
		final String content = document.get();
		final int contentGeneration;
		synchronized (MarkupEditor.this) {
			contentGeneration = documentGeneration;
		}
		outlineParser.setMarkupLanguage(language);
		OutlineItem rootItem = outlineParser.parse(content);
		updateOutline(contentGeneration, rootItem);
	}

	private void updateOutline() {
		if (!outlineDirty || !isSourceViewerValid()) {
			return;
		}
		// we maintain the outline even if the outline page is not in use, which allows us to use the outline for
		// content assist and other things

		MarkupLanguage markupLanguage = getMarkupLanguage();
		if (markupLanguage == null) {
			return;
		}
		final MarkupLanguage language = markupLanguage.clone();

		final Display display = getSourceViewer().getTextWidget().getDisplay();
		final String content = document.get();
		final int contentGeneration;
		synchronized (MarkupEditor.this) {
			contentGeneration = documentGeneration;
		}
		// we parse the outline in another thread so that the UI remains responsive
		Job parseOutlineJob = new Job(MarkupEditor.class.getSimpleName() + "#updateOutline") { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				outlineParser.setMarkupLanguage(language);
				if (shouldCancel()) {
					return Status.CANCEL_STATUS;
				}
				final OutlineItem rootItem = outlineParser.parse(content);
				if (shouldCancel()) {
					return Status.CANCEL_STATUS;
				}

				display.asyncExec(() -> updateOutline(contentGeneration, rootItem));
				return Status.OK_STATUS;
			}

			private boolean shouldCancel() {
				synchronized (MarkupEditor.this) {
					if (contentGeneration != documentGeneration) {
						return true;
					}
				}
				return false;
			}
		};
		parseOutlineJob.setPriority(Job.INTERACTIVE);
		parseOutlineJob.setSystem(true);
		parseOutlineJob.schedule();
	}

	private void updateOutline(int contentGeneration, OutlineItem rootItem) {
		if (!isSourceViewerValid()) {
			return;
		}
		synchronized (this) {
			if (contentGeneration != documentGeneration) {
				return;
			}
		}
		outlineDirty = false;

		outlineModel.clear();
		outlineModel.moveChildren(rootItem);

		IFile file = getFile();
		outlineModel.setResourcePath(file == null ? null : file.getFullPath().toString());

		if (isOutlinePageValid()) {
			outlinePage.refresh();

			outlinePage.getControl().getDisplay().asyncExec(() -> {
				if (isOutlinePageValid()) {
					updateOutlineSelection();
				}
			});
		}
		updateProjectionAnnotations();
	}

	private boolean isOutlinePageValid() {
		return outlinePage != null && outlinePage.getControl() != null && !outlinePage.getControl().isDisposed();
	}

	private boolean isSourceViewerValid() {
		return getSourceViewer() != null && getSourceViewer().getTextWidget() != null
				&& !getSourceViewer().getTextWidget().isDisposed();
	}

	private void updateProjectionAnnotations() {
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		ProjectionAnnotationModel projectionAnnotationModel = viewer.getProjectionAnnotationModel();
		if (projectionAnnotationModel != null) {
			List<Annotation> newProjectionAnnotations = new ArrayList<>(
					projectionAnnotationById == null ? 10 : projectionAnnotationById.size() + 2);
			Map<HeadingProjectionAnnotation, Position> annotationToPosition = new HashMap<>();

			List<OutlineItem> children = outlineModel.getChildren();
			if (!children.isEmpty()) {
				createProjectionAnnotations(newProjectionAnnotations, annotationToPosition, children,
						document.getLength());
			}
			if (newProjectionAnnotations.isEmpty()
					&& (projectionAnnotationById == null || projectionAnnotationById.isEmpty())) {
				return;
			}

			Map<String, HeadingProjectionAnnotation> newProjectionAnnotationById = new HashMap<>();

			if (projectionAnnotationById != null) {
				Set<HeadingProjectionAnnotation> toDelete = new HashSet<>(projectionAnnotationById.size());
				Iterator<Entry<HeadingProjectionAnnotation, Position>> newPositionIt = annotationToPosition.entrySet()
						.iterator();
				while (newPositionIt.hasNext()) {
					Entry<HeadingProjectionAnnotation, Position> newAnnotationEnt = newPositionIt.next();

					HeadingProjectionAnnotation newAnnotation = newAnnotationEnt.getKey();
					Position newPosition = newAnnotationEnt.getValue();
					HeadingProjectionAnnotation annotation = projectionAnnotationById.get(newAnnotation.getHeadingId());
					if (annotation != null) {
						Position position = projectionAnnotationModel.getPosition(annotation);
						if (newPosition.equals(position)) {
							newPositionIt.remove();
							newProjectionAnnotationById.put(annotation.getHeadingId(), annotation);
						} else {
							toDelete.add(annotation);
							if (annotation.isCollapsed()) {
								newAnnotation.markCollapsed();
							} else {
								newAnnotation.markExpanded();
							}
							newProjectionAnnotationById.put(annotation.getHeadingId(), newAnnotation);
						}
					} else {
						newProjectionAnnotationById.put(newAnnotation.getHeadingId(), newAnnotation);
					}
				}
				Iterator<Annotation> annotationIt = projectionAnnotationModel.getAnnotationIterator();
				while (annotationIt.hasNext()) {
					Annotation annotation = annotationIt.next();
					if (annotation instanceof HeadingProjectionAnnotation projectionAnnotation) {
						if (!projectionAnnotationById.containsKey(projectionAnnotation.getHeadingId())
								&& !toDelete.contains(projectionAnnotation)) {
							toDelete.add(projectionAnnotation);
						}
					}
				}
				projectionAnnotationModel.modifyAnnotations(
						toDelete.isEmpty() ? null : toDelete.toArray(new Annotation[toDelete.size()]),
						annotationToPosition, null);
			} else {
				projectionAnnotationModel.modifyAnnotations(null, annotationToPosition, null);
				for (HeadingProjectionAnnotation annotation : annotationToPosition.keySet()) {
					newProjectionAnnotationById.put(annotation.getHeadingId(), annotation);
				}
			}
			projectionAnnotationById = newProjectionAnnotationById;
		} else {
			projectionAnnotationById = null;
		}
	}

	private void createProjectionAnnotations(List<Annotation> newProjectionAnnotations,
			Map<HeadingProjectionAnnotation, Position> annotationToPosition, List<OutlineItem> children,
			int endOffset) {
		final int size = children.size();
		final int lastIndex = size - 1;
		for (int x = 0; x < size; ++x) {
			OutlineItem child = children.get(x);
			if (child.getId() == null || child.getId().length() == 0) {
				continue;
			}
			int offset = child.getOffset();
			int end;
			if (x == lastIndex) {
				end = endOffset;
			} else {
				end = children.get(x + 1).getOffset();
			}
			int length = end - offset;

			if (length > 0) {
				HeadingProjectionAnnotation annotation = new HeadingProjectionAnnotation(child.getId());
				Position position = new Position(offset, length);

				newProjectionAnnotations.add(annotation);
				annotationToPosition.put(annotation, position);
			}

			if (!child.getChildren().isEmpty()) {
				createProjectionAnnotations(newProjectionAnnotations, annotationToPosition, child.getChildren(), end);
			}
		}
	}

	private void updateOutlineSelection() {
		if (disableReveal) {
			return;
		}
		if (outlineModel != null && outlinePage != null) {

			disableReveal = true;
			try {
				OutlineItem item = getNearestMatchingOutlineItem();
				if (item != null) {
					outlinePage.setSelection(new StructuredSelection(item));
				}
			} finally {
				disableReveal = false;
			}
		}
	}

	/**
	 * get the outline item nearest matching the selection in the source viewer
	 */
	private OutlineItem getNearestMatchingOutlineItem() {
		Point selectedRange = getSourceViewer().getSelectedRange();
		if (selectedRange != null) {
			return outlineModel.findNearestMatchingOffset(selectedRange.x);
		}
		return null;
	}

	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { CONTEXT });
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		IContextService contextService = site.getService(IContextService.class);
		contextService.activateContext(CONTEXT);

	}

	private void initializeMarkupLanguage(IEditorInput input) {
		MarkupLanguage markupLanguage = loadMarkupLanguagePreference();
		if (markupLanguage == null) {
			String name = input.getName();
			if (input instanceof IFileEditorInput fei) {
				name = fei.getFile().getName();
			} else if (input instanceof IPathEditorInput pei) {
				name = pei.getPath().lastSegment();
			}
			markupLanguage = WikiText.getMarkupLanguageForFilename(name);
			if (markupLanguage == null) {
				markupLanguage = WikiText.getMarkupLanguage("Textile"); //$NON-NLS-1$
			}
		}
		setMarkupLanguage(markupLanguage, false);
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage, boolean persistSetting) {
		if (markupLanguage instanceof AbstractMarkupLanguage language) {
			language.setEnableMacros(false);
		}
		((MarkupDocumentProvider) getDocumentProvider()).setMarkupLanguage(markupLanguage);

		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		IDocumentPartitioner partitioner = document.getDocumentPartitioner();
		if (partitioner instanceof FastMarkupPartitioner fastMarkupPartitioner) {
			fastMarkupPartitioner.setMarkupLanguage(markupLanguage);
		}
		sourceViewerConfiguration.setMarkupLanguage(markupLanguage);
		if (getSourceViewer() != null) {
			getSourceViewer().invalidateTextPresentation();
		}
		outlineDirty = true;
		scheduleOutlineUpdate();
		updateSourceTabLabel();

		if (viewer != null) {
			viewer.getTextWidget().setData(MarkupLanguage.class.getName(), getMarkupLanguage());
		}

		if (persistSetting && markupLanguage != null) {
			storeMarkupLanguagePreference(markupLanguage);
		}
		if (persistSetting) {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer instanceof MarkupProjectionViewer mpv) {
				IReconciler reconciler = mpv.getReconciler();
				if (reconciler instanceof MarkupMonoReconciler monoReconciler) {
					monoReconciler.forceReconciling();
				}
			}
		}
	}

	private void updateSourceTabLabel() {
		if (sourceTab != null) {
			MarkupLanguage markupLanguage = getMarkupLanguage();
			if (markupLanguage == null) {
				sourceTab.setText(Messages.MarkupEditor_markupSource);
				sourceTab.setToolTipText(Messages.MarkupEditor_markupSource_tooltip);
			} else {
				sourceTab.setText(
						NLS.bind(Messages.MarkupEditor_markupSource_named, new Object[] { markupLanguage.getName() }));
				sourceTab.setToolTipText(NLS.bind(Messages.MarkupEditor_markupSource_tooltip_named,
						new Object[] { markupLanguage.getName() }));
			}
		}
	}

	private MarkupLanguage loadMarkupLanguagePreference() {
		IFile file = getFile();
		if (file != null) {
			return loadMarkupLanguagePreference(file);
		}
		return null;
	}

	/**
	 * lookup the markup language preference of a file based on the persisted preference.
	 *
	 * @param file
	 *            the file for which the preference should be looked up
	 * @return the markup language preference, or null if it was not set or could not be loaded.
	 */
	public static MarkupLanguage loadMarkupLanguagePreference(IFile file) {
		String languageName = getMarkupLanguagePreference(file);
		if (languageName != null) {
			return WikiText.getMarkupLanguage(languageName);
		}
		return null;
	}

	/**
	 * lookup the markup language preference of a file based on the persisted preference.
	 *
	 * @param file
	 *            the file for which the preference should be looked up
	 * @return the markup language name, or null if no preference exists
	 */
	public static String getMarkupLanguagePreference(IFile file) {
		if (file.exists()) {
			try {
				return file.getPersistentProperty(
						new QualifiedName(WikiTextUiPlugin.getDefault().getPluginId(), MarkupEditor.MARKUP_LANGUAGE));
			} catch (CoreException e) {
				WikiTextUiPlugin.getDefault().log(IStatus.ERROR, Messages.MarkupEditor_markupPreferenceError, e);
			}
		}
		return null;
	}

	private void storeMarkupLanguagePreference(MarkupLanguage markupLanguage) {
		if (markupLanguage == null) {
			throw new IllegalArgumentException();
		}
		IFile file = getFile();
		if (file != null) {
			MarkupLanguage defaultMarkupLanguage = WikiText.getMarkupLanguageForFilename(file.getName());
			String preference = markupLanguage.getName();
			if (defaultMarkupLanguage != null && defaultMarkupLanguage.getName().equals(preference)) {
				preference = null;
			}
			try {
				file.setPersistentProperty(
						new QualifiedName(WikiTextUiPlugin.getDefault().getPluginId(), MARKUP_LANGUAGE), preference);
			} catch (CoreException e) {
				WikiTextUiPlugin.getDefault()
						.log(IStatus.ERROR,
								NLS.bind(Messages.MarkupEditor_markupPreferenceError2, new Object[] { preference }), e);
			}
		}
	}

	public MarkupLanguage getMarkupLanguage() {
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		IDocumentPartitioner partitioner = document.getDocumentPartitioner();
		MarkupLanguage markupLanguage = null;
		if (partitioner instanceof FastMarkupPartitioner fmp) {
			markupLanguage = fmp.getMarkupLanguage();
		}
		return markupLanguage;
	}

	@Override
	protected void createActions() {
		super.createActions();

		IAction action;

//		action = new ShowCheatSheetAction(this);
//		setAction(action.getId(),action);

		action = new ContentAssistAction(new NlsResourceBundle(Messages.class), "ContentAssistProposal_", this); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$
	}

	@Override
	public void setAction(String actionID, IAction action) {
		if (action != null && action.getActionDefinitionId() != null && !isCommandAction(action)) {
			// bug 336679: don't activate handlers for CommandAction.
			// We do this by class name so that we don't rely on internals
			IHandlerService handlerService = getSite().getService(IHandlerService.class);
			handlerService.activateHandler(action.getActionDefinitionId(), new ActionHandler(action));
		}
		super.setAction(actionID, action);
	}

	private boolean isCommandAction(IAction action) {
		for (Class<?> clazz = action.getClass(); clazz != Object.class
				&& clazz != AbstractAction.class; clazz = clazz.getSuperclass()) {
			if (clazz.getName().equals("org.eclipse.ui.internal.actions.CommandAction")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		final MarkupLanguage markupLanguage = getMarkupLanguage();
		MenuManager markupLanguageMenu = new MenuManager(Messages.MarkupEditor_markupLanguage);
		for (String markupLanguageName : new TreeSet<>(WikiText.getMarkupLanguageNames())) {
			markupLanguageMenu.add(new SetMarkupLanguageAction(this, markupLanguageName,
					markupLanguage != null && markupLanguageName.equals(markupLanguage.getName())));
		}

		menu.prependToGroup(ITextEditorActionConstants.GROUP_SETTINGS, markupLanguageMenu);

		OutlineItem nearestOutlineItem = getNearestMatchingOutlineItem();
		if (nearestOutlineItem != null && !nearestOutlineItem.isRootItem()) {
			menu.appendToGroup(ITextEditorActionConstants.GROUP_OPEN,
					new PreviewOutlineItemAction(this, nearestOutlineItem));
		}
	}

	public boolean isFoldingEnabled() {
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		return viewer.getProjectionAnnotationModel() != null;
	}

	@Override
	public boolean show(ShowInContext context) {
		ISelection selection = context.getSelection();
		if (selection instanceof IStructuredSelection sse) {
			for (Object element : sse.toArray()) {
				if (element instanceof OutlineItem item) {
					selectAndReveal(item);
					if (isOutlinePageValid()) {
						outlinePage.setSelection(selection);
					}
					return true;
				}
			}
		} else if (selection instanceof ITextSelection textSel) {
			selectAndReveal(textSel.getOffset(), textSel.getLength());
			return true;
		}
		return false;
	}

	public void selectAndReveal(OutlineItem item) {
		selectAndReveal(item.getOffset(), item.getLength());
		if (isShowingPreview()) {
			// scroll preview to the selected item.
			revealInBrowser(item);
		}
	}

	private void revealInBrowser(OutlineItem item) {
		browser.execute(String.format("document.getElementById('%s').scrollIntoView(true);window.location.hash = '%s';", //$NON-NLS-1$
				item.getId(), item.getId()));
	}

	@Override
	public ShowInContext getShowInContext() {
		OutlineItem item = getNearestMatchingOutlineItem();
		return new ShowInContext(getEditorInput(),
				item == null ? new StructuredSelection() : new StructuredSelection(item));
	}

	/**
	 * Causes the editor to display the preview at the specified outline item.
	 */
	public void showPreview(OutlineItem outlineItem) {
		if (!isShowingPreview()) {
			tabFolder.setSelection(previewTab);
		}
		updatePreview(outlineItem);
	}

	@Override
	public void perform(AbstractDocumentCommand command) throws CoreException {
		disableReveal = true;
		try {
			command.execute(((ITextViewerExtension6) getViewer()).getUndoManager(), getViewer().getDocument());
		} finally {
			disableReveal = false;
		}
		updateOutlineSelection();
	}

	private boolean isShowingPreview() {
		return previewTab != null && tabFolder.getSelection() == previewTab;
	}

	@Override
	protected boolean getInitialWordWrapStatus() {
		return true;
	}

	/**
	 * Switches between source and preview editor sub-tabs.
	 */
	public void switchSubTab() {
		if (isShowingPreview()) {
			tabFolder.setSelection(sourceTab);
		} else {
			showPreview(getNearestMatchingOutlineItem());
		}
	}

	@Override
	public void setFocus() {
		if (isShowingPreview()) {
			browser.setFocus();
		} else {
			getViewer().getTextWidget().setFocus();
		}
	}
}
