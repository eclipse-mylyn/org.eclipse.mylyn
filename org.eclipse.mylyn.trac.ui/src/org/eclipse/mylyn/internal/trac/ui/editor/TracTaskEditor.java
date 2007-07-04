/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.TracXmlRpcClient;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 * @author Xiaoyang Guan
 */
public class TracTaskEditor extends AbstractRepositoryTaskEditor {

	private static final String LABEL_BUTTON_PREVIEW = "Preview";

	private static final String LABEL_BUTTON_EDIT = "Edit";
	
	private int buttonState = 0;

	private static final int DESCRIPTION_WIDTH = 79 * 7; // 500;

	private Browser descriptionWikiBrowser;

	private Button descriptionEditorButton;

	public TracTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	protected void validateInput() {
	}

	@Override
	protected void createDescriptionLayout(Composite composite) {
		Section descriptionSection = createSection(composite, getSectionLabel(SECTION_NAME.DESCRIPTION_SECTION));
		final Composite sectionComposite = getManagedForm().getToolkit().createComposite(descriptionSection);
		descriptionSection.setClient(sectionComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		sectionComposite.setLayout(addCommentsLayout);
		GridData sectionCompositeData = new GridData(GridData.FILL_HORIZONTAL);
		sectionComposite.setLayoutData(sectionCompositeData);

		RepositoryTaskAttribute attribute = taskData.getDescriptionAttribute();
		if (attribute != null && !attribute.isReadOnly()) {
			final Composite descriptionComposite = getManagedForm().getToolkit().createComposite(sectionComposite);
			descriptionComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			GridData descriptionGridData = new GridData(GridData.FILL_BOTH);
			descriptionGridData.heightHint = SWT.DEFAULT;
			descriptionComposite.setLayoutData(descriptionGridData);
			final StackLayout descriptionLayout = new StackLayout();
			descriptionComposite.setLayout(descriptionLayout);
			
			descriptionWikiBrowser = new Browser(descriptionComposite, SWT.NONE);

			descriptionTextViewer = addTextEditor(repository, descriptionComposite, taskData.getDescription(), true,
					SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			descriptionTextViewer.setEditable(true);
			StyledText styledText = descriptionTextViewer.getTextWidget();
//			styledText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
//			descriptionTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			descriptionTextViewer.getTextWidget().addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					String newValue = descriptionTextViewer.getTextWidget().getText();
					RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.DESCRIPTION);
					attribute.setValue(newValue);
					attributeChanged(attribute);
					taskData.setDescription(newValue);
				}
			});
			addSelectableControl(taskData.getDescription(), styledText);
			descriptionLayout.topControl = descriptionTextViewer.getControl();
			getManagedForm().getToolkit().paintBordersFor(descriptionComposite);

			Composite buttonComposite = getManagedForm().getToolkit().createComposite(sectionComposite);
			GridLayout buttonLayout = new GridLayout();
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(buttonComposite);
			buttonLayout.numColumns = 1;
			buttonComposite.setLayout(buttonLayout);
			descriptionEditorButton = addPreviewButton(buttonComposite);
			descriptionEditorButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					buttonState = ++buttonState % 2;
					if (buttonState == 1) {
						previewWiki(taskData.getDescription());
					}
					descriptionEditorButton.setText(buttonState == 0 ? LABEL_BUTTON_PREVIEW : LABEL_BUTTON_EDIT);
					descriptionLayout.topControl = (buttonState == 0 ? descriptionTextViewer.getControl()
							: descriptionWikiBrowser);
					descriptionComposite.layout();
				}
			});
			
		} else {
			String text = taskData.getDescription();
			descriptionTextViewer = addTextViewer(repository, sectionComposite, text, SWT.MULTI | SWT.WRAP);
			StyledText styledText = descriptionTextViewer.getTextWidget();
			GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(
					descriptionTextViewer.getControl());

			addSelectableControl(text, styledText);
		}

		if (hasChanged(taskData.getAttribute(RepositoryTaskAttribute.DESCRIPTION))) {
			descriptionTextViewer.getTextWidget().setBackground(getColorIncoming());
		}
		descriptionTextViewer.getTextWidget().addListener(SWT.FocusIn, new DescriptionListener());

		Composite replyComp = getManagedForm().getToolkit().createComposite(descriptionSection);
		replyComp.setLayout(new RowLayout());
		replyComp.setBackground(null);

		createReplyHyperlink(0, replyComp, taskData.getDescription());
		descriptionSection.setTextClient(replyComp);
		addDuplicateDetection(sectionComposite);
		getManagedForm().getToolkit().paintBordersFor(sectionComposite);
	}
	
	private Button addPreviewButton(Composite buttonComposite) {
		Button previewButton = getManagedForm().getToolkit().createButton(buttonComposite, LABEL_BUTTON_PREVIEW, SWT.PUSH);
		GridData previewButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		previewButtonData.widthHint = 100;
		//previewButton.setImage(TasksUiImages.getImage(TasksUiImages.PREVIEW));
		previewButton.setLayoutData(previewButtonData);

		return previewButton;

	}

	private void previewWiki(String sourceText) {
		final class PreviewWikiJob extends Job {
			private String sourceText;

			private String htmlText;

			public PreviewWikiJob(String sourceText) {
				super("Formatting Wiki Text");

				if (sourceText == null) {
					throw new IllegalArgumentException("source text must not be null");
				}

				this.sourceText = sourceText;
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ITracClient client = TracCorePlugin.getDefault().getConnector().getClientManager().getRepository(
							repository);
					if (client instanceof TracXmlRpcClient) {
						TracXmlRpcClient tracXmlRpcClient = (TracXmlRpcClient) client;
						htmlText = tracXmlRpcClient.wikiToHtml(sourceText);
					} else {
						htmlText = "Preview is not available because it needs XML-RPC access mode.";
					}
				} catch (MalformedURLException e) {
					htmlText = "Preview is not available because the repository server url is incorrect: "
							+ e.toString();
				} catch (TracException e) {
					htmlText = "Preview is not available offline: " + e.toString();
				}
				return Status.OK_STATUS;
			}

			public String getHtmlText() {
				return htmlText;
			}

		}

		final PreviewWikiJob job = new PreviewWikiJob(sourceText);
		job.setUser(true);
		job.schedule();

		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				super.done(event);
				if (event.getResult().isOK()) {
					getPartControl().getDisplay().asyncExec(new Runnable() {
						public void run() {
							IAction action = new Action("Display Wiki Preview") {
								public void run() {
									Object editor = getManagedForm().getContainer();
									if (editor instanceof TracTaskEditor) {
										TracTaskEditor tracTaskEditor = (TracTaskEditor) editor;
										tracTaskEditor.displayWikiPreview(job.getHtmlText());
									}
								}
							};
							action.run();
						}
					});
				}
			}
		});
	}

	private void displayWikiPreview(String htmlText) {
		String htmlHeader = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">"
				+ "<head>"
				+ "<link rel=\"stylesheet\" href=\"REPOSITORY_URL/chrome/common/css/trac.css\" type=\"text/css\" />"
				+ "<link rel=\"stylesheet\" href=\"REPOSITORY_URL/chrome/common/css/wiki.css\" type=\"text/css\" />"
				+ "<link rel=\"icon\" href=\"REPOSITORY_URL/chrome/common/trac.ico\" type=\"image/x-icon\" />"
				+ "<link rel=\"shortcut icon\" href=\"EPOSITORY_URL/chrome/common/trac.ico\" type=\"image/x-icon\" />"
				+ "<style type=\"text/css\"></style>" + "</head>";
		String htmlBody = "<body> " + "<div class=\"field\">" + "<fieldset id=\"preview\">"
				+ "<legend>Preview</legend>" + htmlText + "</fieldset>" + "</div>" + "</body>";
		String htmlFooter = "</html>";

		String html = htmlHeader.replace("REPOSITORY_URL", repository.getUrl()) + htmlBody + htmlFooter;

		descriptionWikiBrowser.setText(html);
	}

}
