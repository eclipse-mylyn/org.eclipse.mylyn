/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class TaskEditorNewCommentPart extends AbstractTaskEditorPart {

	private static final int DESCRIPTION_HEIGHT = 10 * 14;

	private static final int DESCRIPTION_WIDTH = 79 * 7; // 500;

//	/**
//	 * A listener for selection of the textbox where a new comment is entered in.
//	 */
//	private class NewCommentListener implements Listener {
//		public void handleEvent(Event event) {
//			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
//					new RepositoryTaskSelection(getTaskData().getId(), getTaskData().getRepositoryUrl(),
//							getTaskData().getRepositoryKind(), getSectionLabel(SECTION_NAME.NEWCOMMENT_SECTION), false,
//							getTaskData().getSummary()))));
//		}
//	}

	private StyledText addCommentsTextBox = null;

	private boolean ignoreLocationEvents = false;

	private TextViewer newCommentTextViewer;

	private AbstractRenderingEngine renderingEngine;

	public TaskEditorNewCommentPart(AbstractTaskEditorPage taskEditorPage) {
		super(taskEditorPage);
		// ignore
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
		// ignore
		Composite newCommentsComposite = toolkit.createComposite(parent);
		newCommentsComposite.setLayout(new GridLayout());

		// HACK: new new comment attribute not created by connector, create one.
		if (getTaskData().getAttribute(RepositoryTaskAttribute.COMMENT_NEW) == null) {
			getTaskData().setAttributeValue(RepositoryTaskAttribute.COMMENT_NEW, "");
		}
		final RepositoryTaskAttribute attribute = getTaskData().getAttribute(RepositoryTaskAttribute.COMMENT_NEW);

		renderingEngine = getTaskEditorPage().getAttributeEditorToolkit().getRenderingEngine(attribute);
		if (renderingEngine != null) {
			// composite with StackLayout to hold text editor and preview widget
			Composite editPreviewComposite = toolkit.createComposite(newCommentsComposite);
			GridData editPreviewData = new GridData(GridData.FILL_BOTH);
			editPreviewData.widthHint = DESCRIPTION_WIDTH;
			editPreviewData.minimumHeight = DESCRIPTION_HEIGHT;
			editPreviewData.grabExcessHorizontalSpace = true;
			editPreviewComposite.setLayoutData(editPreviewData);
			editPreviewComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

			final StackLayout editPreviewLayout = new StackLayout();
			editPreviewComposite.setLayout(editPreviewLayout);

			newCommentTextViewer = getTaskEditorPage().addTextEditor(getTaskRepository(), editPreviewComposite,
					attribute.getValue(), true, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

			editPreviewLayout.topControl = newCommentTextViewer.getControl();
			editPreviewComposite.layout();

			// composite for edit/preview button
			Composite buttonComposite = toolkit.createComposite(newCommentsComposite);
			buttonComposite.setLayout(new GridLayout());
			createPreviewButton(buttonComposite, newCommentTextViewer, editPreviewComposite, editPreviewLayout, toolkit);
		} else {
			newCommentTextViewer = getTaskEditorPage().addTextEditor(getTaskRepository(), newCommentsComposite,
					attribute.getValue(), true, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData addCommentsTextData = new GridData(GridData.FILL_BOTH);
			addCommentsTextData.widthHint = DESCRIPTION_WIDTH;
			addCommentsTextData.minimumHeight = DESCRIPTION_HEIGHT;
			addCommentsTextData.grabExcessHorizontalSpace = true;
			newCommentTextViewer.getControl().setLayoutData(addCommentsTextData);
			newCommentTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		}
		newCommentTextViewer.setEditable(true);
		newCommentTextViewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				String newValue = addCommentsTextBox.getText();
				if (!newValue.equals(attribute.getValue())) {
					attribute.setValue(newValue);
					getTaskEditorPage().getAttributeEditorManager().attributeChanged(attribute);
				}
			}
		});
// FIXME EDITOR implement
//		newCommentTextViewer.getTextWidget().addListener(SWT.FocusIn, new NewCommentListener());
		addCommentsTextBox = newCommentTextViewer.getTextWidget();

		toolkit.paintBordersFor(newCommentsComposite);

		setControl(newCommentsComposite);
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
					jobStatus = new RepositoryStatus(getTaskRepository(), IStatus.INFO, TasksUiPlugin.ID_PLUGIN,
							RepositoryStatus.ERROR_INTERNAL, "The repository does not support HTML preview.");
					return Status.OK_STATUS;
				}

				jobStatus = Status.OK_STATUS;
				try {
					htmlText = renderingEngine.renderAsHtml(getTaskRepository(), sourceText, monitor);
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
								getTaskEditor().setMessage(null, IMessageProvider.NONE);
							}
						});
					} else {
						getControl().getDisplay().asyncExec(new Runnable() {
							public void run() {
								getTaskEditor().setMessage(job.getStatus().getMessage(), IMessageProvider.ERROR);
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

	@Override
	public void setFocus() {
		newCommentTextViewer.getControl().setFocus();
	}

	public void appendText(String text) {
		StringBuilder strBuilder = new StringBuilder();
		String oldText = newCommentTextViewer.getDocument().get();
		if (strBuilder.length() != 0) {
			strBuilder.append("\n");
		}
		strBuilder.append(oldText);
		newCommentTextViewer.getDocument().set(strBuilder.toString());
		RepositoryTaskAttribute attribute = getTaskData().getAttribute(RepositoryTaskAttribute.COMMENT_NEW);
		if (attribute != null) {
			attribute.setValue(strBuilder.toString());
			getTaskEditorPage().getAttributeEditorManager().attributeChanged(attribute);
		}
		newCommentTextViewer.getTextWidget().setCaretOffset(strBuilder.length());
	}
	
}
