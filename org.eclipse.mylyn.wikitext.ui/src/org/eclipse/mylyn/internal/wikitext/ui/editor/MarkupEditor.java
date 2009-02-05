/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
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
import org.eclipse.jface.text.source.IOverviewRuler;
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
import org.eclipse.mylyn.internal.wikitext.ui.editor.actions.SetMarkupLanguageAction;
import org.eclipse.mylyn.internal.wikitext.ui.editor.operations.AbstractDocumentCommand;
import org.eclipse.mylyn.internal.wikitext.ui.editor.operations.CommandManager;
import org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.Preferences;
import org.eclipse.mylyn.internal.wikitext.ui.editor.reconciler.MarkupMonoReconciler;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.MarkupDocumentProvider;
import org.eclipse.mylyn.wikitext.core.WikiText;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
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
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.IHandlerService;
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
 * 
 * 
 * @author David Green
 */
public class MarkupEditor extends TextEditor implements IShowInTarget, IShowInSource, CommandManager {
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

	private static final String[] SHOW_IN_TARGETS = { IPageLayout.ID_RES_NAV,
			"org.eclipse.jdt.ui.PackageExplorer", IPageLayout.ID_OUTLINE //$NON-NLS-1$
	};

	private static IShowInTargetList SHOW_IN_TARGET_LIST = new IShowInTargetList() {
		public String[] getShowInTargetIds() {
			return SHOW_IN_TARGETS;
		}
	};

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

	public MarkupEditor() {
		setDocumentProvider(new MarkupDocumentProvider());
		sourceViewerConfiguration = new MarkupSourceViewerConfiguration(getPreferenceStore());
		sourceViewerConfiguration.setOutline(outlineModel);
		setSourceViewerConfiguration(sourceViewerConfiguration);
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {

		CTabFolder folder = new CTabFolder(parent, SWT.BOTTOM);

		{
			sourceTab = new CTabItem(folder, SWT.NONE);
			updateSourceTabLabel();

			viewer = new MarkupSourceViewer(folder, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles
					| SWT.WRAP);

			sourceTab.setControl(viewer instanceof Viewer ? ((Viewer) viewer).getControl() : viewer.getTextWidget());
			folder.setSelection(sourceTab);
		}

		{
			CTabItem previewTab = new CTabItem(folder, SWT.NONE);
			previewTab.setText(Messages.getString("MarkupEditor.PreviewView_label")); //$NON-NLS-1$
			previewTab.setToolTipText(Messages.getString("MarkupEditor.PreviewView_tooltip")); //$NON-NLS-1$

			browser = new Browser(folder, SWT.NONE);
			// bug 260479: open hyperlinks in a browser
			browser.addLocationListener(new LocationListener() {
				public void changed(LocationEvent event) {
					event.doit = false;
				}

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
						try {
							PlatformUI.getWorkbench().getBrowserSupport().createBrowser("org.eclipse.ui.browser") //$NON-NLS-1$
									.openURL(new URL(event.location));
						} catch (Exception e) {
							new URLHyperlink(new Region(0, 1), event.location).open();
						}
					}
				}
			});
			previewTab.setControl(browser);
		}

		folder.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				updatePreview();
			}

		});
		viewer.getTextWidget().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				updateOutlineSelection();
			}

		});
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

		IFocusService focusService = (IFocusService) PlatformUI.getWorkbench().getService(IFocusService.class);
		if (focusService != null) {
			focusService.addFocusTracker(viewer.getTextWidget(), MarkupEditor.EDITOR_SOURCE_VIEWER);
		}

		viewer.getTextWidget().setData(MarkupLanguage.class.getName(), getMarkupLanguage());
		viewer.getTextWidget().setData(ISourceViewer.class.getName(), viewer);

		getSourceViewerDecorationSupport(viewer);

		updateDocument();

		if (preferencesListener == null) {
			preferencesListener = new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					if (viewer.getTextWidget().isDisposed()) {
						return;
					}
					viewer.getTextWidget().getDisplay().asyncExec(new Runnable() {
						public void run() {
							reloadPreferences();
						}
					});
				}
			};
			WikiTextUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(preferencesListener);
		}

		return viewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();

		projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
		projectionSupport.install();

		syncProjectionModeWithPreferences();

		viewer.addProjectionListener(new IProjectionListener() {
			public void projectionDisabled() {
				projectionAnnotationById = null;
				saveProjectionPreferences();
			}

			public void projectionEnabled() {
				saveProjectionPreferences();
				updateProjectionAnnotations();
			}
		});

		if (!outlineDirty && isFoldingEnabled()) {
			updateProjectionAnnotations();
		}
	}

	private void reloadPreferences() {
		syncProjectionModeWithPreferences();
		viewer.invalidateTextPresentation();
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
						public void documentAboutToBeChanged(DocumentEvent event) {
						}

						public void documentChanged(DocumentEvent event) {
							previewDirty = true;
							outlineDirty = true;
							synchronized (MarkupEditor.this) {
								++documentGeneration;
							}
							scheduleOutlineUpdate();
						}

					};
				}
				document.addDocumentListener(documentListener);
				if (documentPartitioningListener == null) {
					documentPartitioningListener = new IDocumentPartitioningListener() {
						public void documentPartitioningChanged(IDocument document) {
							// async update
							scheduleOutlineUpdate();
						}
					};
				}
				document.addDocumentPartitioningListener(documentPartitioningListener);
			}

			previewDirty = true;
			outlineDirty = true;
			updateOutline();
		}
	}

	private void updatePreview() {
		// FIXME apply stylesheet preferences to the preview
		if (previewDirty && browser != null) {
			String xhtml = null;
			if (document == null) {
				xhtml = "<?xml version=\"1.0\" ?><html xmlns=\"http://www.w3.org/1999/xhtml\"><body></body></html>"; //$NON-NLS-1$
			} else {
				try {
					MarkupParser markupParser = new MarkupParser();

					IFile file = getFile();
					String title = file.getName();
					if (title.lastIndexOf('.') != -1) {
						title = title.substring(0, title.lastIndexOf('.'));
					}
					StringWriter writer = new StringWriter();
					HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer) {
						@Override
						protected String makeUrlAbsolute(String url) {
							if (url.startsWith("#")) { //$NON-NLS-1$
								return String.format("javascript: window.location.hash = '%s'; return false;", url); //$NON-NLS-1$
							}
							return super.makeUrlAbsolute(url);
						}
					};
					builder.setTitle(title);

					IPath location = file.getLocation();
					if (location != null) {
						builder.setBaseInHead(true);
						builder.setBase(location.removeLastSegments(1).toFile().toURI());
					}

					String css = WikiTextUiPlugin.getDefault().getPreferences().getMarkupViewerCss();
					if (css != null && css.length() > 0) {
						builder.addCssStylesheet(new HtmlDocumentBuilder.Stylesheet(new StringReader(css)));
					}

					markupParser.setBuilder(builder);
					markupParser.setMarkupLanaguage(getMarkupLanguage());
					if (markupParser.getMarkupLanguage() == null) {
						builder.beginDocument();
						builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
						builder.characters(document.get());
						builder.endBlock();
						builder.endDocument();
					} else {
						markupParser.getMarkupLanguage().setBlocksOnly(false);
						markupParser.getMarkupLanguage().setFilterGenerativeContents(false);
						markupParser.parse(document.get());
					}
					xhtml = writer.toString();
				} catch (Exception e) {
					StringWriter stackTrace = new StringWriter();
					PrintWriter writer = new PrintWriter(stackTrace);
					e.printStackTrace(writer);
					writer.close();

					StringWriter documentWriter = new StringWriter();
					HtmlDocumentBuilder builder = new HtmlDocumentBuilder(documentWriter);
					builder.beginDocument();
					builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
					builder.characters(stackTrace.toString());
					builder.endBlock();
					builder.endDocument();

					xhtml = writer.toString();
				}
			}
			browser.setText(xhtml);
			previewDirty = false;
		}
	}

	public IFile getFile() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			return fileEditorInput.getFile();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class == adapter) {
			if (outlinePage == null || outlinePage.getControl() == null || outlinePage.getControl().isDisposed()) {
				outlinePage = new MarkupEditorOutline(this);
			}
			return outlinePage;
		}
		if (adapter == OutlineItem.class) {
			return getOutlineModel();
		}
		if (adapter == IFoldingStructure.class) {
			if (!isFoldingEnabled()) {
				return null;
			}
			if (foldingStructure == null) {
				foldingStructure = new FoldingStructure(this);
			}
			return foldingStructure;
		}
		if (adapter == IShowInTargetList.class) {
			return SHOW_IN_TARGET_LIST;
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
		updateOutlineJob = new UIJob(Messages.getString("MarkupEditor.2")) { //$NON-NLS-1$
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
		if (!outlineDirty) {
			return;
		}
		if (getSourceViewer().getTextWidget().isDisposed()) {
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
		if (!outlineDirty) {
			return;
		}
		if (getSourceViewer().getTextWidget().isDisposed()) {
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

				display.asyncExec(new Runnable() {
					public void run() {
						updateOutline(contentGeneration, rootItem);
					}
				});
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
		if (getSourceViewer().getTextWidget().isDisposed()) {
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

		if (outlinePage != null && outlinePage.getControl() != null && !outlinePage.getControl().isDisposed()) {
			outlinePage.refresh();

			outlinePage.getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (outlinePage != null && outlinePage.getControl() != null
							&& !outlinePage.getControl().isDisposed()) {
						updateOutlineSelection();
					}
				}
			});
		}
		updateProjectionAnnotations();
	}

	@SuppressWarnings("unchecked")
	private void updateProjectionAnnotations() {
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		ProjectionAnnotationModel projectionAnnotationModel = viewer.getProjectionAnnotationModel();
		if (projectionAnnotationModel != null) {
			List<Annotation> newProjectionAnnotations = new ArrayList<Annotation>(projectionAnnotationById == null ? 10
					: projectionAnnotationById.size() + 2);
			Map<HeadingProjectionAnnotation, Position> annotationToPosition = new HashMap<HeadingProjectionAnnotation, Position>();

			List<OutlineItem> children = outlineModel.getChildren();
			if (!children.isEmpty()) {
				createProjectionAnnotations(newProjectionAnnotations, annotationToPosition, children,
						document.getLength());
			}
			if (newProjectionAnnotations.isEmpty()
					&& (projectionAnnotationById == null || projectionAnnotationById.isEmpty())) {
				return;
			}

			Map<String, HeadingProjectionAnnotation> newProjectionAnnotationById = new HashMap<String, HeadingProjectionAnnotation>();

			if (projectionAnnotationById != null) {
				Set<HeadingProjectionAnnotation> toDelete = new HashSet<HeadingProjectionAnnotation>(
						projectionAnnotationById.size());
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
					if (annotation instanceof HeadingProjectionAnnotation) {
						HeadingProjectionAnnotation projectionAnnotation = (HeadingProjectionAnnotation) annotation;
						if (!projectionAnnotationById.containsKey(projectionAnnotation.getHeadingId())
								&& !toDelete.contains(projectionAnnotation)) {
							toDelete.add(projectionAnnotation);
						}
					}
				}
				projectionAnnotationModel.modifyAnnotations(toDelete.isEmpty() ? null
						: toDelete.toArray(new Annotation[toDelete.size()]), annotationToPosition, null);
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
			Map<HeadingProjectionAnnotation, Position> annotationToPosition, List<OutlineItem> children, int endOffset) {
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

		IContextService contextService = (IContextService) site.getService(IContextService.class);
		contextService.activateContext(CONTEXT);

	}

	private void initializeMarkupLanguage(IEditorInput input) {
		MarkupLanguage markupLanguage = loadMarkupLanguagePreference();
		if (markupLanguage == null) {
			String name = input.getName();
			if (input instanceof IFileEditorInput) {
				name = ((IFileEditorInput) input).getFile().getName();
			} else if (input instanceof IPathEditorInput) {
				name = ((IPathEditorInput) input).getPath().lastSegment();
			}
			markupLanguage = WikiText.getMarkupLanguageForFilename(name);
			if (markupLanguage == null) {
				markupLanguage = WikiText.getMarkupLanguage("Textile"); //$NON-NLS-1$
			}
		}
		setMarkupLanguage(markupLanguage, false);
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage, boolean persistSetting) {
		((MarkupDocumentProvider) getDocumentProvider()).setMarkupLanguage(markupLanguage);

		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		IDocumentPartitioner partitioner = document.getDocumentPartitioner();
		if (partitioner instanceof FastMarkupPartitioner) {
			final FastMarkupPartitioner fastMarkupPartitioner = (FastMarkupPartitioner) partitioner;
			fastMarkupPartitioner.setMarkupLanguage(markupLanguage);
		}
		sourceViewerConfiguration.setMarkupLanguage(markupLanguage);
		if (getSourceViewer() != null) {
			getSourceViewer().invalidateTextPresentation();
		}
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
			if (sourceViewer instanceof MarkupSourceViewer) {
				IReconciler reconciler = ((MarkupSourceViewer) sourceViewer).getReconciler();
				if (reconciler instanceof MarkupMonoReconciler) {
					((MarkupMonoReconciler) reconciler).forceReconciling();
				}
			}
		}
	}

	private void updateSourceTabLabel() {
		if (sourceTab != null) {
			MarkupLanguage markupLanguage = getMarkupLanguage();
			if (markupLanguage == null) {
				sourceTab.setText(Messages.getString("MarkupEditor.SourceView_label")); //$NON-NLS-1$
				sourceTab.setToolTipText(Messages.getString("MarkupEditor.SourceView_tooltip")); //$NON-NLS-1$
			} else {
				sourceTab.setText(Messages.getMessage("MarkupEditor.SourceView_label2", markupLanguage.getName())); //$NON-NLS-1$
				sourceTab.setToolTipText(Messages.getMessage(
						"MarkupEditor.SourceView_tooltip2", markupLanguage.getName())); //$NON-NLS-1$
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
	 * 
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
	 * 
	 * @return the markup language name, or null if no preference exists
	 */
	public static String getMarkupLanguagePreference(IFile file) {
		String languageName;
		try {
			languageName = file.getPersistentProperty(new QualifiedName(WikiTextUiPlugin.getDefault().getPluginId(),
					MarkupEditor.MARKUP_LANGUAGE));
		} catch (CoreException e) {
			WikiTextUiPlugin.getDefault().log(IStatus.ERROR, Messages.getString("MarkupEditor.0"), e); //$NON-NLS-1$
			return null;
		}
		return languageName;
	}

	private void storeMarkupLanguagePreference(MarkupLanguage markupLanguage) {
		if (markupLanguage == null) {
			throw new IllegalArgumentException();
		}
		IFile file = getFile();
		if (file != null) {
			MarkupLanguage defaultMarkupLanguage = WikiText.getMarkupLanguageForFilename(file.getName());
			String preference = markupLanguage == null ? null : markupLanguage.getName();
			if (defaultMarkupLanguage != null && defaultMarkupLanguage.getName().equals(preference)) {
				preference = null;
			}
			try {
				file.setPersistentProperty(new QualifiedName(WikiTextUiPlugin.getDefault().getPluginId(),
						MARKUP_LANGUAGE), preference);
			} catch (CoreException e) {
				WikiTextUiPlugin.getDefault().log(IStatus.ERROR,
						MessageFormat.format(Messages.getString("MarkupEditor.1"), preference), e); //$NON-NLS-1$
			}
		}
	}

	public MarkupLanguage getMarkupLanguage() {
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		IDocumentPartitioner partitioner = document.getDocumentPartitioner();
		MarkupLanguage markupLanguage = null;
		if (partitioner instanceof FastMarkupPartitioner) {
			markupLanguage = ((FastMarkupPartitioner) partitioner).getMarkupLanguage();
		}
		return markupLanguage;
	}

	@Override
	protected void createActions() {
		super.createActions();

		IAction action;

//		action = new ShowCheatSheetAction(this);
//		setAction(action.getId(),action);

		action = new ContentAssistAction(Messages.getBundle(), "ContentAssistProposal.", this); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$
	}

	@Override
	public void setAction(String actionID, IAction action) {
		if (action.getActionDefinitionId() != null) {
			IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
			handlerService.activateHandler(action.getActionDefinitionId(), new ActionHandler(action));
		}
		super.setAction(actionID, action);
	}

	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		final MarkupLanguage markupLanguage = getMarkupLanguage();
		MenuManager markupLanguageMenu = new MenuManager(Messages.getString("MarkupEditor.MarkupLanguage")); //$NON-NLS-1$
		for (String markupLanguageName : new TreeSet<String>(WikiText.getMarkupLanguageNames())) {
			markupLanguageMenu.add(new SetMarkupLanguageAction(this, markupLanguageName, markupLanguage != null
					&& markupLanguageName.equals(markupLanguage.getName())));
		}

		menu.prependToGroup(ITextEditorActionConstants.GROUP_SETTINGS, markupLanguageMenu);
	}

	public boolean isFoldingEnabled() {
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		return viewer.getProjectionAnnotationModel() != null;
	}

	/* prevent line number ruler from appearing since it doesn't work with line wrapping 
	 */
	@Override
	protected boolean isLineNumberRulerVisible() {
		return false;
	}

	public boolean show(ShowInContext context) {
		ISelection selection = context.getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Object element : ((IStructuredSelection) selection).toArray()) {
				if (element instanceof OutlineItem) {
					OutlineItem item = (OutlineItem) element;
					selectAndReveal(item.getOffset(), item.getLength());
					return true;
				}
			}
		} else if (selection instanceof ITextSelection) {
			ITextSelection textSel = (ITextSelection) selection;
			selectAndReveal(textSel.getOffset(), textSel.getLength());
			return true;
		}
		return false;
	}

	public ShowInContext getShowInContext() {
		OutlineItem item = getNearestMatchingOutlineItem();
		return new ShowInContext(getEditorInput(), item == null ? new StructuredSelection() : new StructuredSelection(
				item));
	}

	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		// prevent line number toggle action from appearing
		menu.remove(ITextEditorActionConstants.LINENUMBERS_TOGGLE);
	}

	/**
	 * extend the viewer to provide access to the reconciler
	 */
	private static class MarkupSourceViewer extends ProjectionViewer {

		public MarkupSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
				boolean showAnnotationsOverview, int styles) {
			super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		}

		public IReconciler getReconciler() {
			return fReconciler;
		}
	}

	public void perform(AbstractDocumentCommand command) throws CoreException {
		disableReveal = true;
		try {
			command.execute(((ITextViewerExtension6) getViewer()).getUndoManager(), getViewer().getDocument());
		} finally {
			disableReveal = false;
		}
		updateOutlineSelection();
	}
}
