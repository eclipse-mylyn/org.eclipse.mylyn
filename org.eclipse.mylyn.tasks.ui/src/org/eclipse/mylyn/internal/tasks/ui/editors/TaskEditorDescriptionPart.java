/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class TaskEditorDescriptionPart extends AbstractTaskEditorPart {

	/**
	 * A listener for selection of the summary field.
	 * 
	 * @since 2.1
	 */
//	protected class DescriptionListener implements Listener {
//		public DescriptionListener() {
//		}
//
//		public void handleEvent(Event event) {
//			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
//					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(),
//							taskData.getRepositoryKind(), getSectionLabel(SECTION_NAME.DESCRIPTION_SECTION), true,
//							taskData.getSummary()))));
//		}
//	}
	private static final int DESCRIPTION_HEIGHT = 10 * 14;

	private static final int DESCRIPTION_WIDTH = 79 * 7; // 500;

	private static final String LABEL_SEARCH_DUPS = "Search";

	private static final String LABEL_SELECT_DETECTOR = "Duplicate Detection";

	private final Section descriptionSection;

	protected TextViewer descriptionTextViewer = null;

	protected CCombo duplicateDetectorChooser;

	protected Label duplicateDetectorLabel;

	private boolean ignoreLocationEvents = false;

	private AbstractRenderingEngine renderingEngine;

	private Button searchForDuplicates;

	public TaskEditorDescriptionPart(AbstractTaskEditorPage taskEditorPage, Section descriptionSection) {
		super(taskEditorPage);
		this.descriptionSection = descriptionSection;
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

	private void addDuplicateDetection(Composite composite, FormToolkit toolkit) {
		List<AbstractDuplicateDetector> allCollectors = new ArrayList<AbstractDuplicateDetector>();
		if (getDuplicateSearchCollectorsList() != null) {
			allCollectors.addAll(getDuplicateSearchCollectorsList());
		}
		if (!allCollectors.isEmpty()) {
			Section duplicatesSection = toolkit.createSection(composite, ExpandableComposite.TWISTIE
					| ExpandableComposite.SHORT_TITLE_BAR);
			duplicatesSection.setText(LABEL_SELECT_DETECTOR);
			duplicatesSection.setLayout(new GridLayout());
			GridDataFactory.fillDefaults().indent(SWT.DEFAULT, 15).applyTo(duplicatesSection);
			Composite relatedBugsComposite = toolkit.createComposite(duplicatesSection);
			relatedBugsComposite.setLayout(new GridLayout(4, false));
			relatedBugsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			duplicatesSection.setClient(relatedBugsComposite);
			duplicateDetectorLabel = new Label(relatedBugsComposite, SWT.LEFT);
			duplicateDetectorLabel.setText("Detector:");

			duplicateDetectorChooser = new CCombo(relatedBugsComposite, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(duplicateDetectorChooser, true, true);
			duplicateDetectorChooser.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			duplicateDetectorChooser.setFont(TEXT_FONT);
			duplicateDetectorChooser.setLayoutData(GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).create());

			Collections.sort(allCollectors, new Comparator<AbstractDuplicateDetector>() {

				public int compare(AbstractDuplicateDetector c1, AbstractDuplicateDetector c2) {
					return c1.getName().compareToIgnoreCase(c2.getName());
				}

			});

			for (AbstractDuplicateDetector detector : allCollectors) {
				duplicateDetectorChooser.add(detector.getName());
			}

			duplicateDetectorChooser.select(0);
			duplicateDetectorChooser.setEnabled(true);
			duplicateDetectorChooser.setData(allCollectors);

			if (allCollectors.size() > 0) {

				searchForDuplicates = toolkit.createButton(relatedBugsComposite, LABEL_SEARCH_DUPS, SWT.NONE);
				GridData searchDuplicatesButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				searchForDuplicates.setLayoutData(searchDuplicatesButtonData);
				searchForDuplicates.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						searchForDuplicates();
					}
				});
			}
//		} else {
//			Label label = new Label(composite, SWT.LEFT);
//			label.setText(LABEL_NO_DETECTOR);

			toolkit.paintBordersFor(relatedBugsComposite);

		}

	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		final Composite sectionComposite = toolkit.createComposite(parent);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		sectionComposite.setLayout(addCommentsLayout);

		final RepositoryTaskAttribute attribute = getTaskData().getDescriptionAttribute();
		if (attribute != null && !attribute.isReadOnly()) {
			renderingEngine = getTaskEditorPage().getAttributeEditorToolkit().getRenderingEngine(attribute);
			if (renderingEngine != null) {
				// composite with StackLayout to hold text editor and preview widget
				Composite descriptionComposite = toolkit.createComposite(sectionComposite);
				descriptionComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				GridData descriptionGridData = new GridData(GridData.FILL_BOTH);
				descriptionGridData.widthHint = DESCRIPTION_WIDTH;
				descriptionGridData.minimumHeight = DESCRIPTION_HEIGHT;
				descriptionGridData.grabExcessHorizontalSpace = true;
				descriptionComposite.setLayoutData(descriptionGridData);
				final StackLayout descriptionLayout = new StackLayout();
				descriptionComposite.setLayout(descriptionLayout);

				descriptionTextViewer = getTaskEditorPage().addTextEditor(getTaskRepository(), descriptionComposite,
						getTaskData().getDescription(), true, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
				descriptionLayout.topControl = descriptionTextViewer.getControl();
				descriptionComposite.layout();

				// composite for edit/preview button
				Composite buttonComposite = toolkit.createComposite(sectionComposite);
				buttonComposite.setLayout(new GridLayout());
				createPreviewButton(buttonComposite, descriptionTextViewer, descriptionComposite, descriptionLayout,
						toolkit);
			} else {
				descriptionTextViewer = getTaskEditorPage().addTextEditor(getTaskRepository(), sectionComposite,
						getTaskData().getDescription(), true, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
				final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				// wrap text at this margin, see comment below
				gd.widthHint = DESCRIPTION_WIDTH;
				gd.minimumHeight = DESCRIPTION_HEIGHT;
				gd.grabExcessHorizontalSpace = true;
				descriptionTextViewer.getControl().setLayoutData(gd);
				descriptionTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				// the goal is to make the text viewer as big as the text so it does not require scrolling when first drawn 
				// on screen: when the descriptionTextViewer calculates its height it wraps the text according to the widthHint 
				// which does not reflect the actual size of the widget causing the widget to be taller 
				// (actual width > gd.widhtHint) or shorter (actual width < gd.widthHint) therefore the widthHint is tweaked 
				// once in the listener  
				sectionComposite.addControlListener(new ControlAdapter() {
					private boolean first;

					@Override
					public void controlResized(ControlEvent e) {
						if (!first) {
							first = true;
							int width = sectionComposite.getSize().x;
							Point size = descriptionTextViewer.getTextWidget().computeSize(width, SWT.DEFAULT, true);
							// limit width to parent widget
							gd.widthHint = width;
							// limit height to avoid dynamic resizing of the text widget
							gd.heightHint = Math.min(Math.max(DESCRIPTION_HEIGHT, size.y), DESCRIPTION_HEIGHT * 4);
							sectionComposite.layout();
						}
					}
				});
			}
			descriptionTextViewer.setEditable(true);
			descriptionTextViewer.addTextListener(new ITextListener() {
				public void textChanged(TextEvent event) {
					String newValue = descriptionTextViewer.getTextWidget().getText();
					if (!newValue.equals(attribute.getValue())) {
						attribute.setValue(newValue);
						getTaskEditorPage().getAttributeEditorManager().attributeChanged(attribute);
						getTaskData().setDescription(newValue);
					}
				}
			});
			StyledText styledText = descriptionTextViewer.getTextWidget();
			getTaskEditorPage().addSelectableControl(getTaskData().getDescription(), styledText);
		} else {
			String text = getTaskData().getDescription();
			descriptionTextViewer = getTaskEditorPage().addTextViewer(getTaskRepository(), sectionComposite, text,
					SWT.MULTI | SWT.WRAP);
			StyledText styledText = descriptionTextViewer.getTextWidget();
			GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(
					descriptionTextViewer.getControl());

			getTaskEditorPage().addSelectableControl(text, styledText);
		}

		getTaskEditorPage().getAttributeEditorManager().decorate(attribute, descriptionTextViewer.getTextWidget());
		// FIXME EDITOR
		//		descriptionTextViewer.getTextWidget().addListener(SWT.FocusIn, new DescriptionListener());

		Composite replyComp = toolkit.createComposite(parent);
		replyComp.setLayout(new RowLayout());
		replyComp.setBackground(null);

		getTaskEditorPage().createReplyHyperlink(0, replyComp, getTaskData().getDescription());
		descriptionSection.setTextClient(replyComp);
		addDuplicateDetection(sectionComposite, toolkit);
		toolkit.paintBordersFor(sectionComposite);

		setControl(sectionComposite);
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

	protected SearchHitCollector getDuplicateSearchCollector(String name) {
		String duplicateDetectorName = name.equals("default") ? "Stack Trace" : name;
		Set<AbstractDuplicateDetector> allDetectors = getDuplicateSearchCollectorsList();

		for (AbstractDuplicateDetector detector : allDetectors) {
			if (detector.getName().equals(duplicateDetectorName)) {
				return detector.getSearchHitCollector(getTaskRepository(), getTaskData());
			}
		}
		// didn't find it
		return null;
	}

	protected Set<AbstractDuplicateDetector> getDuplicateSearchCollectorsList() {
		Set<AbstractDuplicateDetector> duplicateDetectors = new HashSet<AbstractDuplicateDetector>();
		for (AbstractDuplicateDetector abstractDuplicateDetector : TasksUiPlugin.getDefault()
				.getDuplicateSearchCollectorsList()) {
			if (abstractDuplicateDetector.getKind() == null
					|| abstractDuplicateDetector.getKind().equals(getConnector().getConnectorKind())) {
				duplicateDetectors.add(abstractDuplicateDetector);
			}
		}
		return duplicateDetectors;
	}

	public boolean searchForDuplicates() {
		String duplicateDetectorName = duplicateDetectorChooser.getItem(duplicateDetectorChooser.getSelectionIndex());

		SearchHitCollector collector = getDuplicateSearchCollector(duplicateDetectorName);
		if (collector != null) {
			NewSearchUI.runQueryInBackground(collector);
			return true;
		}

		return false;
	}

}
