/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class PreviewAttributeEditor extends AbstractAttributeEditor {

	private final RichTextAttributeEditor editor;

	private boolean ignoreLocationEvents;

	private final AbstractRenderingEngine renderingEngine;

	public PreviewAttributeEditor(AttributeManager manager, RepositoryTaskAttribute taskAttribute,
			AbstractRenderingEngine renderingEngine, RichTextAttributeEditor editor) {
		super(manager, taskAttribute);

		Assert.isNotNull(editor);
		Assert.isNotNull(renderingEngine);

		this.editor = editor;
		this.renderingEngine = renderingEngine;
	}

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

				if (event.location != null && !event.location.startsWith("about")) {
					event.doit = false;
					IHyperlink link = new TaskUrlHyperlink(
							new Region(0, 0)/* a fake region just to make constructor happy */, event.location);
					link.open();
				}
			}

		});

		return browser;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		final Composite sectionComposite = toolkit.createComposite(parent);
		sectionComposite.setLayout(new GridLayout(1, false));

		// composite with StackLayout to hold text editor and preview widget
		Composite editorComposite = toolkit.createComposite(sectionComposite);
		editorComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = MAXIMUM_WIDTH;
		gd.minimumHeight = MAXIMUM_HEIGHT;
		gd.grabExcessHorizontalSpace = true;
		editorComposite.setLayoutData(gd);
		final StackLayout descriptionLayout = new StackLayout();
		editorComposite.setLayout(descriptionLayout);

		editor.createControl(editorComposite, toolkit);

		// composite for edit/preview button
		Composite buttonComposite = toolkit.createComposite(sectionComposite);
		buttonComposite.setLayout(new GridLayout());
		createPreviewButton(buttonComposite, editor.getViewer(), editorComposite, descriptionLayout, toolkit);
	}

	/**
	 * Creates and sets up the button for switching between text editor and HTML preview. Subclasses that support HTML
	 * preview of new comments must override this method.
	 * 
	 * @param buttonComposite
	 *            the composite that holds the button
	 * @param editor
	 *            the TextViewer for editing text
	 * @param previewBrowser
	 *            the Browser for displaying the preview
	 * @param editorLayout
	 *            the StackLayout of the <code>editorComposite</code>
	 * @param editorComposite
	 *            the composite that holds <code>editor</code> and <code>previewBrowser</code>
	 * @since 2.1
	 */
	private void createPreviewButton(final Composite buttonComposite, final TextViewer editor,
			final Composite editorComposite, final StackLayout editorLayout, final FormToolkit toolkit) {
		// create an anonymous object that encapsulates the edit/preview button together with
		// its state and String constants for button text;
		// this implementation keeps all information needed to set up the button 
		// in this object and the method parameters, and this method is reused by both the
		// description section and new comments section.
		new Object() {
			private static final String LABEL_BUTTON_EDIT = "Edit";

			private static final String LABEL_BUTTON_PREVIEW = "Preview";

			private int buttonState = 0;

			private Browser previewBrowser;

			private Button previewButton;

			{
				previewButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_PREVIEW, SWT.PUSH);
				GridData previewButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				previewButtonData.widthHint = 100;
				//previewButton.setImage(TasksUiImages.getImage(TasksUiImages.PREVIEW));
				previewButton.setLayoutData(previewButtonData);
				previewButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						if (previewBrowser == null) {
							previewBrowser = addBrowser(editorComposite, SWT.NONE);
						}

						buttonState = ++buttonState % 2;
						if (buttonState == 1) {

							setText(previewBrowser, "Loading preview...");
							previewWiki(previewBrowser, editor.getTextWidget().getText());
						}
						previewButton.setText(buttonState == 0 ? LABEL_BUTTON_PREVIEW : LABEL_BUTTON_EDIT);
						editorLayout.topControl = (buttonState == 0 ? editor.getControl() : previewBrowser);
						editorComposite.layout();
					}
				});
			}

		};
	}

	private void previewWiki(final Browser browser, String sourceText) {
		final class PreviewWikiJob extends Job {
			private String htmlText;

			private IStatus jobStatus;

			private final String sourceText;

			public PreviewWikiJob(String sourceText) {
				super("Formatting Wiki Text");

				if (sourceText == null) {
					throw new IllegalArgumentException("source text must not be null");
				}

				this.sourceText = sourceText;
			}

			public String getHtmlText() {
				return htmlText;
			}

			public IStatus getStatus() {
				return jobStatus;
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (renderingEngine == null) {
					jobStatus = new RepositoryStatus(getAttributeEditorManager().getTaskRepository(), IStatus.INFO,
							TasksUiPlugin.ID_PLUGIN, RepositoryStatus.ERROR_INTERNAL,
							"The repository does not support HTML preview.");
					return Status.OK_STATUS;
				}

				jobStatus = Status.OK_STATUS;
				try {
					htmlText = renderingEngine.renderAsHtml(getAttributeEditorManager().getTaskRepository(),
							sourceText, monitor);
				} catch (CoreException e) {
					jobStatus = e.getStatus();
				}
				return Status.OK_STATUS;
			}

		}

		final PreviewWikiJob job = new PreviewWikiJob(sourceText);

		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(final IJobChangeEvent event) {
				if (!getControl().isDisposed()) {
					if (job.getStatus().isOK()) {
						getControl().getDisplay().asyncExec(new Runnable() {
							public void run() {
								setText(browser, job.getHtmlText());
								// TODO EDITOR error handling
								//getAttributeEditorManager().setMessage(null, IMessageProvider.NONE);
							}
						});
					} else {
						getControl().getDisplay().asyncExec(new Runnable() {
							public void run() {
								StatusHandler.displayStatus("Error", job.getStatus());
								// TODO EDITOR error handling
								//getAttributeEditorManager().setMessage(job.getStatus().getMessage(), IMessageProvider.ERROR);
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

	private void setText(Browser browser, String html) {
		try {
			ignoreLocationEvents = true;
			browser.setText((html != null) ? html : "");
		} finally {
			ignoreLocationEvents = false;
		}

	}

}
