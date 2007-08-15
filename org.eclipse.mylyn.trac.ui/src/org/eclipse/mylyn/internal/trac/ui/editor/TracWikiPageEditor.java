/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Xiaoyang Guan
 */
public class TracWikiPageEditor extends FormEditor {
	
	public static final String ID_EDITOR = "org.eclipse.mylyn.trac.ui.editor.wikipage";

	private TaskRepository repository;

	private TracWikiPage page;

	private WikiSourceEditor wikiSourceEditor;

	private BrowserFormPage browserPage;

	private class WikiSourceEditor extends FormPage {
		
		private static final String ID = "org.eclipse.mylyn.trac.ui.editor.wikisource";

		private static final String TITLE = "Wiki Page Source";

		private static final String LABEL_PREVIEW = "Page Preview";

		private static final String LABEL_SOURCE = "Page Source";

		private static final String LABEL_BUTTON_PREVIEW = "Preview";

		private static final String LABEL_BUTTON_SUBMIT = "Submit";

		private static final int PREVIEW_BROWSER_HEIGHT = 10 * 14;

		private static final int DEFAULT_WIDTH = 79 * 7; // 500;
		
		private ScrolledForm form;

		private FormToolkit toolkit;

		private Composite editorComposite;

		private Section previewSection;

		private Browser previewBrowser;

		private TextViewer sourceEditor;

		private Button previewButton;

		private Button submitButton;

		protected boolean isDirty;

		public WikiSourceEditor(FormEditor editor) {
			super(editor, ID, TITLE);

		}

		@Override
		protected void createFormContent(IManagedForm managedForm) {
			super.createFormContent(managedForm);
			form = managedForm.getForm();
			toolkit = managedForm.getToolkit();

			editorComposite = form.getBody();
			GridLayout editorLayout = new GridLayout();
			editorComposite.setLayout(editorLayout);
			editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			createPreviewSection(editorComposite);
			createSourceSection(editorComposite);
		}

		private void createPreviewSection(Composite parent) {
			previewSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
			previewSection.setText(LABEL_PREVIEW);
			previewSection.setExpanded(false);
			previewSection.setLayout(new GridLayout());
			previewSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			previewSection.addExpansionListener(new IExpansionListener() {
				public void expansionStateChanging(ExpansionEvent e) {
					form.reflow(true);
				}

				public void expansionStateChanged(ExpansionEvent e) {
					form.reflow(true);
				}
			});

			Composite container = toolkit.createComposite(previewSection);
			previewSection.setClient(container);
			container.setLayout(new GridLayout());
			container.setLayoutData(new GridData(GridData.FILL_BOTH));

			previewBrowser = addBrowser(container, SWT.NONE);
			previewBrowser.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			GridData previewBrowserData = new GridData(GridData.FILL_BOTH);
			previewBrowserData.heightHint = PREVIEW_BROWSER_HEIGHT;
			previewBrowser.setLayoutData(previewBrowserData);

			toolkit.paintBordersFor(container);
		}

		private void createSourceSection(Composite parent) {
			Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
			section.setText(LABEL_SOURCE);
			section.setExpanded(true);
			section.setLayout(new GridLayout());
			section.setLayoutData(new GridData(GridData.FILL_BOTH));
			section.addExpansionListener(new IExpansionListener() {
				public void expansionStateChanging(ExpansionEvent e) {
					form.reflow(true);
				}

				public void expansionStateChanged(ExpansionEvent e) {
					form.reflow(true);
				}
			});

			Composite container = toolkit.createComposite(section);
			section.setClient(container);
			container.setLayout(new GridLayout());
			container.setLayoutData(new GridData(GridData.FILL_BOTH));

			sourceEditor = new SourceViewer(container, null, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			sourceEditor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
			sourceEditor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			sourceEditor.setEditable(true);

			sourceEditor.getTextWidget().addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					markDirty(true);
				}
			});

			StyledText styledText = sourceEditor.getTextWidget();
			GridDataFactory.fillDefaults().hint(DEFAULT_WIDTH, SWT.DEFAULT).grab(true, true).applyTo(styledText);
			
			Document document = new Document(page.getContent());
			sourceEditor.setDocument(document);

			createActionsLayout(container);
			toolkit.paintBordersFor(container);
		}

		private void createActionsLayout(Composite parent) {
			Composite buttonComposite = toolkit.createComposite(parent);
			GridLayout buttonLayout = new GridLayout();
			buttonLayout.numColumns = 2;
			buttonComposite.setLayout(buttonLayout);
			buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addActionButtons(buttonComposite);

		}

		private void addActionButtons(Composite buttonComposite) {
			previewButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_PREVIEW, SWT.NONE);
			GridData previewButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			previewButtonData.widthHint = 100;
			previewButton.setLayoutData(previewButtonData);
			previewButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					previewSection.setExpanded(true);
					setText(previewBrowser, "loading preview...");
					previewWiki(previewBrowser, sourceEditor.getTextWidget().getText());
				}
			});

			submitButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_SUBMIT, SWT.NONE);
			GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			submitButtonData.widthHint = 100;
			submitButton.setImage(TasksUiImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
			submitButton.setLayoutData(submitButtonData);
			submitButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					InputDialog commentDialog = new InputDialog(null, "Page Comment",
							"Enter a Comment for the page (or cancel the submit):", null, null);
					// cancel the comment dialog will cancel the submit
					if (commentDialog.open() == Window.OK) {
						page.getPageInfo().setComment(commentDialog.getValue());
						page.getPageInfo().setAuthor(repository.isAnonymous() ? "anonymous" : repository.getUserName());
						page.setContent(sourceEditor.getTextWidget().getText());

						submitToRepository();
					}
				}
			});

			setSubmitEnabled(true);
		}

		/**
		 * Copied from AbstractRepositoryTaskEditor
		 * 
		 * @param enabled
		 */
		private void setSubmitEnabled(boolean enabled) {
			if (submitButton != null && !submitButton.isDisposed()) {
				submitButton.setEnabled(enabled);
				if (enabled) {
					submitButton.setToolTipText("Submit to " + repository.getUrl());
				}
			}
		}

		public void submitToRepository() {
			class SubmitPageJob extends Job {
				private IStatus jobStatus;

				public SubmitPageJob() {
					super("upload wiki page");
				}

				public IStatus getStatus() {
					return jobStatus;
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						monitor.beginTask("Uploading wiki page", IProgressMonitor.UNKNOWN);
						TracCorePlugin.getDefault().getConnector().getWikiHandler().postWikiPage(repository, page,
								monitor);
						jobStatus = Status.OK_STATUS;
					} catch (CoreException e) {
						StatusHandler.displayStatus("Submit failed", e.getStatus());
						jobStatus = e.getStatus();
					} finally {
						monitor.done();
					}
					return Status.OK_STATUS;
				}

			}

			final SubmitPageJob submitJob = new SubmitPageJob();
			submitJob.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {

					// refresh editor only if uploading the wiki page succeeded
					if (submitJob.getStatus().isOK()) {
						updateWikiPage();
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								browserPage.refresh();
								setActivePage(BrowserFormPage.ID_EDITOR);
								markDirty(false);
							}
						});
					}
				}
			});
			submitJob.schedule();
		}

		@Override
		public boolean isDirty() {
			return isDirty;
		}

		protected void markDirty(boolean dirty) {
			isDirty = dirty;
			getManagedForm().dirtyStateChanged();
		}

		/*====== Copied/modified from AbstractRepositoryTaskEditor ======*/

		private boolean ignoreLocationEvents = false;

		private Browser addBrowser(Composite parent, int style) {
			Browser browser = new Browser(parent, style);
			// intercept links to open tasks in rich editor and urls in separate browser
			browser.addLocationListener(new LocationAdapter() {
				@Override
				public void changing(LocationEvent event) {
					// ignore events that are caused by manually setting the contents of the browser
					if (ignoreLocationEvents) {
						return;
					}

					if (event.location != null) {
						event.doit = false;
						// TODO: add open link support when this editor is moved into ..mylyn.tasks.ui.editors
						//IHyperlink link = new TaskUrlHyperlink(
						//		new Region(0, 0)/* a fake region just to make constructor happy */, event.location);
						//link.open();
					}
				}

			});

			return browser;
		}

		private void setText(Browser browser, String html) {
			try {
				ignoreLocationEvents = true;
				browser.setText((html != null) ? html : "");
			} finally {
				ignoreLocationEvents = false;
			}

		}

		private void previewWiki(final Browser browser, String sourceText) {
			final class PreviewWikiJob extends Job {
				private String sourceText;

				private String htmlText;

				private IStatus jobStatus;

				public PreviewWikiJob(String sourceText) {
					super("Formatting Wiki Text");

					if (sourceText == null) {
						throw new IllegalArgumentException("source text must not be null");
					}

					this.sourceText = sourceText;
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					AbstractRenderingEngine htmlRenderingEngine = new TracRenderingEngine();

					jobStatus = Status.OK_STATUS;
					try {
						htmlText = htmlRenderingEngine.renderAsHtml(repository, sourceText, monitor);
					} catch (CoreException e) {
						jobStatus = e.getStatus();
					}
					return Status.OK_STATUS;
				}

				public String getHtmlText() {
					return htmlText;
				}

				public IStatus getStatus() {
					return jobStatus;
				}

			}

			final PreviewWikiJob job = new PreviewWikiJob(sourceText);

			job.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void done(final IJobChangeEvent event) {
					if (!form.isDisposed()) {
						if (job.getStatus().isOK()) {
							getPartControl().getDisplay().asyncExec(new Runnable() {
								public void run() {
									WikiSourceEditor.this.setText(browser, job.getHtmlText());
									//parentEditor.setMessage(null, IMessageProvider.NONE);
								}
							});
						} else {
							getPartControl().getDisplay().asyncExec(new Runnable() {
								public void run() {
									//parentEditor.setMessage(job.getStatus().getMessage(), IMessageProvider.ERROR);
								}
							});
						}
					}
					super.done(event);
				}
			});

			job.setUser(true);
			job.schedule();
		}

	}

	/**
	 * Modified from org.eclipse.mylyn.internal.web.tasks.BrowserFormPage
	 * 
	 */
	private class BrowserFormPage extends FormPage {

		public static final String ID_EDITOR = "org.eclipse.mylyn.trac.ui.editor.wikibrowser";

		private Browser browser;

		public BrowserFormPage(FormEditor editor, String title) {
			super(editor, ID_EDITOR, title);
		}

		@Override
		protected void createFormContent(IManagedForm managedForm) {
			super.createFormContent(managedForm);
			try {
				TracWikiPageEditorInput editorInput = (TracWikiPageEditorInput) getEditorInput();

				ScrolledForm form = managedForm.getForm();
				form.getBody().setLayout(new FillLayout());
				browser = new Browser(form.getBody(), SWT.NONE);
				managedForm.getForm().setContent(browser);
				browser.setUrl(editorInput.getPageUrl());
			} catch (SWTError e) {
				StatusHandler.fail(e, "Could not create Browser page: " + e.getMessage(), true);
			} catch (RuntimeException e) {
				StatusHandler.fail(e, "Could not create wiki page", false);
			}
		}

		public void refresh() {
			browser.refresh();
		}

		@Override
		public void dispose() {
			if (browser != null && !browser.isDisposed()) {
				browser.dispose();
			}
			super.dispose();
		}
	}

	public TracWikiPageEditor() {
		super();
		wikiSourceEditor = new WikiSourceEditor(this);
		browserPage = new BrowserFormPage(this, "Browser");
	}

	protected void initializeEditor() {
		TracWikiPageEditorInput editorInput = (TracWikiPageEditorInput) getEditorInput();
		page = editorInput.getPage();
		repository = editorInput.getRepository();
	}

	@Override
	protected void addPages() {
		initializeEditor();
		try {
			addPage(wikiSourceEditor);
			addPage(browserPage);
			setPartName(getEditorInput().getName());
			setActivePage(BrowserFormPage.ID_EDITOR);
		} catch (PartInitException e) {
			StatusHandler.fail(e, "Cannot create Trac Wiki page editor pages", true);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		MessageDialog.openInformation(getSite().getShell(), "Changes cannot be saved",
				"Offline editting on wiki pages not supported yet.");
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected void updateWikiPage() {
		Job updatePageJob = new Job("Update wiki page") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("Downloading wiki page", IProgressMonitor.UNKNOWN);
					TracWikiPage newPage = TracCorePlugin.getDefault().getConnector().getWikiHandler().getWikiPage(
							repository, page.getPageInfo().getPageName(), monitor);
					if (newPage != null) {
						((TracWikiPageEditorInput) getEditorInput()).setPage(newPage);
					} else {
						StatusHandler.fail(null, "Unable to retrieve wiki page " + page.getPageInfo().getPageName(),
								true, IStatus.ERROR);
					}
				} catch (CoreException e) {
					StatusHandler.displayStatus("Download failed", e.getStatus());
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}

		};
		updatePageJob.schedule();
	}

}
