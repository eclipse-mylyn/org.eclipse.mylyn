/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * A WikiText source editor. Users must {@link #setDocumentProvider(IDocumentProvider) set the document provider}.
 * 
 * @author David Green
 * @see AbstractWikiTextDocumentProvider
 * @since 1.3
 */
public class WikiTextSourceEditor extends TextEditor implements IShowInSource, IShowInTarget {
	private static final String EDITOR_SOURCE_VIEWER = "org.eclipse.mylyn.wikitext.ui.editor.sourceViewer"; //$NON-NLS-1$

	/**
	 * The source editing context. This context id is activated via the {@link IContextService} when the editor is
	 * {@link #init(IEditorSite, IEditorInput) initialized}. This context is also used as the editor help context id.
	 */
	public static final String CONTEXT = "org.eclipse.mylyn.wikitext.ui.editor.markupSourceContext"; //$NON-NLS-1$

	/**
	 * The property id for editor outline change events. Clients wishing to react to changes in the outline should
	 * listen for this event.
	 */
	public static final int PROP_OUTLINE = 0x10000001;

	/**
	 * The property id for outline location change events. Outline location events are fired when navigation within the
	 * source document causes the nearest computed outline item to change. Clients wishing to react to changes in the
	 * location of the caret with respect to the current outline should listen for this event.
	 */
	public static final int PROP_OUTLINE_LOCATION = 0x10000002;

	private MarkupLanguage markupLanguage;

	private MarkupSourceViewer viewer;

	private MarkupSourceViewerConfiguration sourceViewerConfiguration;

	private boolean outlineDirty = true;

	private int documentGeneration = 0;

	private IDocument document;

	private IDocumentListener documentListener;

	private IDocumentPartitioningListener documentPartitioningListener;

	private boolean updateJobScheduled = false;

	private UIJob updateOutlineJob;

	private OutlineItem outlineModel;

	private OutlineItem outlineLocation;

	private OutlineParser outlineParser;

	private AbstractWikiTextSourceEditorOutline outlinePage;

	public WikiTextSourceEditor() {
	}

	/**
	 * Users must set the document provider.
	 * 
	 * @see AbstractWikiTextDocumentProvider
	 * @see WikiTextDocumentProvider
	 */
	@Override
	public void setDocumentProvider(IDocumentProvider provider) {
		if (provider instanceof WikiTextDocumentProvider) {
			((WikiTextDocumentProvider) provider).setMarkupLanguage(getMarkupLanguage());
		}
		super.setDocumentProvider(provider);
	}

	/**
	 * Set the source viewer configuration. This method should be called in the constructor of subclasses.
	 * 
	 * @throws ClassCastException
	 *             if the configuration does not subclass {@link MarkupSourceViewerConfiguration}
	 */
	@Override
	protected void setSourceViewerConfiguration(SourceViewerConfiguration configuration) {
		sourceViewerConfiguration = (MarkupSourceViewerConfiguration) configuration;
		super.setSourceViewerConfiguration(sourceViewerConfiguration);
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		if (getMarkupLanguage() == null) {
			setMarkupLanguage(ServiceLocator.getInstance().getMarkupLanguage("Textile")); //$NON-NLS-1$
		}
		viewer = createMarkupSourceViewer(parent, ruler, styles);

		IFocusService focusService = (IFocusService) PlatformUI.getWorkbench().getService(IFocusService.class);
		if (focusService != null) {
			focusService.addFocusTracker(viewer.getTextWidget(), EDITOR_SOURCE_VIEWER);
		}

		viewer.getTextWidget().setData(MarkupLanguage.class.getName(), getMarkupLanguage());
		viewer.getTextWidget().setData(ISourceViewer.class.getName(), viewer);

		viewer.getTextWidget().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				detectOutlineLocationChanged();
			}
		});
		viewer.getTextWidget().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (isRelevantKeyCode(e.keyCode)) {
					detectOutlineLocationChanged();
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
				detectOutlineLocationChanged();
			}
		});

		getSourceViewerDecorationSupport(viewer);

		updateDocument();

		return viewer;
	}

	/**
	 * Create a markup source viewer. Subclasses may override.
	 * 
	 * @param parent
	 *            the parent of the source viewer
	 * @param ruler
	 *            the vertical ruler
	 * @param styles
	 *            the styles to pass to the viewer
	 * @return a new markup source viewer
	 * @see #createSourceViewer(Composite, IVerticalRuler, int)
	 */
	protected MarkupSourceViewer createMarkupSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		return new MarkupSourceViewer(parent, ruler, styles | SWT.WRAP, getMarkupLanguage());
	}

	/**
	 * The markup language. If unspecified, it's assumed to be Textile.
	 * 
	 * @return the current markup language, or null if it's unspecified.
	 */
	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	/**
	 * set the markup language. If unspecified, it's assumed to be Textile.
	 */
	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
		sourceViewerConfiguration.setMarkupLanguage(markupLanguage);

		IDocumentProvider documentProvider = getDocumentProvider();
		if (documentProvider instanceof WikiTextDocumentProvider) {
			((WikiTextDocumentProvider) documentProvider).setMarkupLanguage(markupLanguage);
		}
		if (getEditorInput() != null) {
			IDocument document = documentProvider.getDocument(getEditorInput());
			IDocumentPartitioner partitioner = document.getDocumentPartitioner();
			if (partitioner instanceof FastMarkupPartitioner) {
				final FastMarkupPartitioner fastMarkupPartitioner = (FastMarkupPartitioner) partitioner;
				fastMarkupPartitioner.setMarkupLanguage(markupLanguage);
			}
		}
		if (viewer != null) {
			viewer.getTextWidget().setData(MarkupLanguage.class.getName(), getMarkupLanguage());
		}
		if (getSourceViewer() != null) {
			getSourceViewer().invalidateTextPresentation();
		}
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setHelpContextId(CONTEXT); // ORDER DEPENDENCY
		setSourceViewerConfiguration(new MarkupSourceViewerConfiguration(getPreferenceStore()));
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		IContextService contextService = (IContextService) site.getService(IContextService.class);
		contextService.activateContext(CONTEXT);
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IContentOutlinePage.class) {
			if (outlinePage == null || outlinePage.getControl() == null || outlinePage.getControl().isDisposed()) {
				outlinePage = createContentOutline();
				outlinePage.setEditor(this);
				return outlinePage;
			}
			return outlinePage;
		}
		if (adapter == OutlineItem.class) {
			return getOutlineModel();
		} else if (adapter == IShowInSource.class) {
			return this;
		} else if (adapter == IShowInTarget.class) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * subclasses may override to provide a non-default content outline.
	 */
	protected AbstractWikiTextSourceEditorOutline createContentOutline() {
		return new DefaultWikiTextSourceEditorOutline();
	}

	@Override
	public void updatePartControl(IEditorInput input) {
		super.updatePartControl(input);
		updateDocument();
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
							outlineDirty = true;
							synchronized (WikiTextSourceEditor.this) {
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

			synchronized (WikiTextSourceEditor.this) {
				outlineDirty = true;
			}
			updateOutline();
		}
	}

	/**
	 * Get the outline model for the document being edited. The returned outline model is guaranteed to be up to date
	 * with respect to the current document. Note that the model will change if the document changes, however all
	 * changes occur on the UI thread.
	 */
	public final OutlineItem getOutlineModel() {
		synchronized (WikiTextSourceEditor.this) {
			// ensure that outline model is caught up with current version of document
			if (outlineDirty || outlineModel == null) {
				updateOutlineNow();
			}
			return outlineModel;
		}
	}

	private void scheduleOutlineUpdate() {
		synchronized (WikiTextSourceEditor.this) {
			if (updateJobScheduled || outlineModel == null) {
				return;
			}
		}
		updateOutlineJob = new UIJob("WikiText - Outline Job") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				synchronized (WikiTextSourceEditor.this) {
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
				synchronized (WikiTextSourceEditor.this) {
					updateJobScheduled = true;
				}
			}

			@Override
			public void done(IJobChangeEvent event) {
				synchronized (WikiTextSourceEditor.this) {
					updateJobScheduled = false;
					updateOutlineJob = null;
				}
			}
		});
		updateOutlineJob.setUser(false);
		updateOutlineJob.setSystem(true);
		updateOutlineJob.setPriority(Job.INTERACTIVE);
		updateOutlineJob.schedule(600); // NAGLE algorithm: capture more changes with a delay
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
		synchronized (WikiTextSourceEditor.this) {
			contentGeneration = documentGeneration;
			initializeOutlineParser();
		}
		// we parse the outline in another thread so that the UI remains responsive
		Job parseOutlineJob = new Job(WikiTextSourceEditor.class.getSimpleName() + "#updateOutline") { //$NON-NLS-1$
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
				synchronized (WikiTextSourceEditor.this) {
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
		synchronized (WikiTextSourceEditor.this) {
			contentGeneration = documentGeneration;
			initializeOutlineParser();
		}
		outlineParser.setMarkupLanguage(language);
		OutlineItem rootItem = outlineParser.parse(content);
		updateOutline(contentGeneration, rootItem);
	}

	private void initializeOutlineParser() {
		synchronized (WikiTextSourceEditor.this) {
			if (outlineParser == null) {
				outlineParser = new OutlineParser();
				outlineParser.setLabelMaxLength(48);
				outlineModel = outlineParser.createRootItem();
			}
		}
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

		IFile file = (IFile) getAdapter(IFile.class);
		outlineModel.setResourcePath(file == null ? null : file.getFullPath().toString());

		firePropertyChange(PROP_OUTLINE);
	}

	private void detectOutlineLocationChanged() {
		OutlineItem nearestItem = getNearestMatchingOutlineItem();
		if (nearestItem != outlineLocation && (nearestItem == null || !nearestItem.equals(outlineLocation))) {
			outlineLocation = nearestItem;
			firePropertyChange(PROP_OUTLINE_LOCATION);
		}
	}

	public ShowInContext getShowInContext() {
		OutlineItem item = getNearestMatchingOutlineItem();
		return new ShowInContext(getEditorInput(), item == null ? new StructuredSelection() : new StructuredSelection(
				item));
	}

	/**
	 * get the outline item nearest matching the selection in the source viewer
	 */
	private OutlineItem getNearestMatchingOutlineItem() {
		Point selectedRange = getSourceViewer().getSelectedRange();
		if (selectedRange != null) {
			return getOutlineModel().findNearestMatchingOffset(selectedRange.x);
		}
		return null;
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
					selectAndReveal(item);
					if (outlinePage != null && outlinePage.getControl() != null
							&& !outlinePage.getControl().isDisposed()) {
						outlinePage.setSelection(selection);
					}
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

	/**
	 * Select and reveal the given outline item, based on its offset and length.
	 * 
	 * @param item
	 *            the item, must not be null
	 */
	public void selectAndReveal(OutlineItem item) {
		selectAndReveal(item.getOffset(), item.getLength());
	}
}
