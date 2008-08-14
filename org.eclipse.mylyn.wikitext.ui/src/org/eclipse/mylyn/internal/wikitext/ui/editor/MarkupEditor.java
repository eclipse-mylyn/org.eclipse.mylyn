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
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.actions.SetMarkupLanguageAction;
import org.eclipse.mylyn.internal.wikitext.ui.editor.reconciler.MarkupMonoReconciler;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.MarkupDocumentProvider;
import org.eclipse.mylyn.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.IHandlerService;
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
public class MarkupEditor extends TextEditor {
	/**
	 * the name of the property that stores the markup language name for per-file preference
	 * 
	 * @see IFile#setPersistentProperty(QualifiedName, String) property
	 */
	private static final String MARKUP_LANGUAGE = "markupLanguage";

	public static final String CONTEXT = "org.eclipse.mylyn.wikitext.ui.editor.markupSourceContext"; //$NON-NLS-1$

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

	public static final String EDITOR_SOURCE_VIEWER = "org.eclipse.mylyn.wikitext.ui.editor.sourceViewer";

	public MarkupEditor() {
		setDocumentProvider(new MarkupDocumentProvider());
		sourceViewerConfiguration = new MarkupSourceViewerConfiguration(getPreferenceStore());
		setSourceViewerConfiguration(sourceViewerConfiguration);
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {

		CTabFolder folder = new CTabFolder(parent, SWT.BOTTOM);

		{
			sourceTab = new CTabItem(folder, SWT.NONE);
			updateSourceTabLabel();

			viewer = new MarkupSourceViewer(folder, ruler, styles | SWT.WRAP);

			sourceTab.setControl(viewer instanceof Viewer ? ((Viewer) viewer).getControl() : viewer.getTextWidget());
			folder.setSelection(sourceTab);
		}

		{
			CTabItem previewTab = new CTabItem(folder, SWT.NONE);
			previewTab.setText(Messages.getString("MarkupEditor.PreviewView_label")); //$NON-NLS-1$
			previewTab.setToolTipText(Messages.getString("MarkupEditor.PreviewView_tooltip")); //$NON-NLS-1$

			browser = new Browser(folder, SWT.NONE);
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
				updateOutlineSelection();
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

	private void reloadPreferences() {
		viewer.invalidateTextPresentation();
	}

	@Override
	public void updatePartControl(IEditorInput input) {
		super.updatePartControl(input);
		updateDocument();
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
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		updateDocument();
		if (sourceViewerConfiguration != null) {
			sourceViewerConfiguration.setFile(getFile());
		}
		initializeMarkupLanguage(input);
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
							scheduleOutlineUpdate();
						}

					};
				}
				document.addDocumentListener(documentListener);
				if (documentPartitioningListener == null) {
					documentPartitioningListener = new IDocumentPartitioningListener() {
						public void documentPartitioningChanged(IDocument document) {
							updateOutline();
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
					HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
					builder.setTitle(title);

					IPath location = file.getLocation();
					if (location != null) {
						builder.setBaseInHead(true);
						builder.setBase(location.removeLastSegments(1).toFile().toURI());
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
			if (outlinePage == null || outlinePage.getControl().isDisposed()) {
				outlinePage = new MarkupEditorOutline(this);
				scheduleOutlineUpdate();
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	public OutlineItem getOutlineModel() {
		return outlineModel;
	}

	private void scheduleOutlineUpdate() {
		UIJob updateOutlineJob = new UIJob("Update outline") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (!outlineDirty) {
					return Status.CANCEL_STATUS;
				}
				updateOutline();
				return Status.OK_STATUS;
			}
		};
		updateOutlineJob.setUser(false);
		updateOutlineJob.setSystem(true);
		updateOutlineJob.setPriority(Job.INTERACTIVE);
		updateOutlineJob.schedule(600);
	}

	private void updateOutline() {
		if (!outlineDirty) {
			return;
		}
		if (outlinePage != null && outlinePage.getControl() != null && !outlinePage.getControl().isDisposed()) {
			outlineDirty = false;
			outlineParser.setMarkupLanguage(getMarkupLanguage());
			outlineModel.clear();
			outlineParser.parse(outlineModel, document.get());
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
	}

	private void updateOutlineSelection() {
		if (disableReveal) {
			return;
		}
		if (outlineModel != null && outlinePage != null) {

			disableReveal = true;
			try {
				Point selection = getSourceViewer().getTextWidget().getSelection();
				if (selection != null) {
					OutlineItem item = outlineModel.findNearestMatchingOffset(selection.x);
					if (item != null) {
						outlinePage.setSelection(new StructuredSelection(item));
					}
				}
			} finally {
				disableReveal = false;
			}
		}
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
			markupLanguage = WikiTextPlugin.getDefault().getMarkupLanguageForFilename(name);
			if (markupLanguage == null) {
				// FIXME: this could be a project or workspace setting
				markupLanguage = WikiTextPlugin.getDefault().getMarkupLanguage("Textile");
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
		try {
			String languageName = file.getPersistentProperty(new QualifiedName(WikiTextUiPlugin.getDefault()
					.getPluginId(), MarkupEditor.MARKUP_LANGUAGE));
			if (languageName != null) {
				return WikiTextPlugin.getDefault().getMarkupLanguage(languageName);
			}
		} catch (CoreException e) {
			WikiTextUiPlugin.getDefault().log(IStatus.ERROR, "Cannot load markup language preference", e);
		}
		return null;
	}

	private void storeMarkupLanguagePreference(MarkupLanguage markupLanguage) {
		if (markupLanguage == null) {
			throw new IllegalArgumentException();
		}
		IFile file = getFile();
		if (file != null) {
			MarkupLanguage defaultMarkupLanguage = WikiTextPlugin.getDefault().getMarkupLanguageForFilename(
					file.getName());
			String preference = markupLanguage == null ? null : markupLanguage.getName();
			if (defaultMarkupLanguage != null && defaultMarkupLanguage.getName().equals(preference)) {
				preference = null;
			}
			try {
				file.setPersistentProperty(new QualifiedName(WikiTextUiPlugin.getDefault().getPluginId(),
						MARKUP_LANGUAGE), preference);
			} catch (CoreException e) {
				WikiTextUiPlugin.getDefault().log(IStatus.ERROR,
						String.format("Cannot store markup language preference '%s'", preference), e);
			}
		}
	}

	protected MarkupLanguage getMarkupLanguage() {
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
		for (String markupLanguageName : new TreeSet<String>(WikiTextPlugin.getDefault().getMarkupLanguageNames())) {
			markupLanguageMenu.add(new SetMarkupLanguageAction(this, markupLanguageName, markupLanguage != null
					&& markupLanguageName.equals(markupLanguage.getName())));
		}

		menu.appendToGroup(ITextEditorActionConstants.GROUP_SETTINGS, markupLanguageMenu);
	}

	private static class MarkupSourceViewer extends SourceViewer {

		public MarkupSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
			super(parent, ruler, styles);
		}

		public MarkupSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
				boolean showAnnotationsOverview, int styles) {
			super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		}

		public IReconciler getReconciler() {
			return fReconciler;
		}
	}
}
