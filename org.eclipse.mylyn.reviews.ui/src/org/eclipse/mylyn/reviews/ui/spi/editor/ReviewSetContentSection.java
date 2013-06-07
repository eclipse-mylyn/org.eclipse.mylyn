/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies, Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sam Davis - improvements for bug 383592
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.providers.ReviewsLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 * @author Sam Davis
 */
public class ReviewSetContentSection {

	private static final int MAXIMUM_ITEMS_SHOWN = 30;

	private final ReviewSetSection parentSection;

	private final IReviewItemSet set;

	private final Section section;

	private TableViewer viewer;

	private final RemoteEmfObserver<IReviewItemSet, List<IFileItem>, String, Long> itemListClient = new RemoteEmfObserver<IReviewItemSet, List<IFileItem>, String, Long>() {

		@Override
		public void created(IReviewItemSet parentObject, java.util.List<IFileItem> modelObject) {
			createItemSetTable();
			updateMessage();
		}

		@Override
		public void updated(IReviewItemSet parentObject, java.util.List<IFileItem> modelObject, boolean modified) {
			if (modified) {
				updateItemSetTable();
			}
			updateMessage();
		}

		@Override
		public void updating(IReviewItemSet parent, java.util.List<IFileItem> object) {
			updateMessage();
		}

		@Override
		public void failed(IReviewItemSet parent, List<IFileItem> object, IStatus status) {
			Status errorStatus = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Error loading patch set",
					status.getException());
			StatusHandler.log(errorStatus);
			if (getParentSection().getSection().getTextClient() != null) {
				AbstractReviewSection.appendMessage(getParentSection().getSection(), "Couldn't load patch set.");
			}
		}
	};

	private Composite tableContainer;

	private Composite actionContainer;

	public ReviewSetContentSection(ReviewSetSection parentSection, final IReviewItemSet set) {
		this.parentSection = parentSection;
		this.set = set;
		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT
				| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;
		section = parentSection.getToolkit().createSection(parentSection.getComposite(), style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
		section.setText(set.getName());
		section.setTitleBarForeground(parentSection.getToolkit().getColors().getColor(IFormColors.TITLE));

		parentSection.addTextClient(parentSection.getToolkit(), section, "", false); //$NON-NLS-1$
		final RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, ?, ?, Long> itemSetConsumer = getParentSection().getReviewEditorPage()
				.getFactoryProvider()
				.getReviewItemSetContentFactory()
				.getConsumerForLocalKey(set, set.getId());
		itemListClient.setConsumer(itemSetConsumer);
		final RemoteEmfConsumer<IRepository, IReview, String, ?, ?, Date> reviewConsumer = getParentSection().getReviewEditorPage()
				.getFactoryProvider()
				.getReviewFactory()
				.getConsumerForModel(set.getReview().getRepository(), set.getReview());
		reviewConsumer.addObserver(new RemoteEmfObserver<IRepository, IReview, String, Date>() {
			@Override
			public void updated(IRepository parentObject, IReview modelObject, boolean modified) {
				if (reviewConsumer.getRemoteObject() != null && section.isExpanded() && modified) {
					itemSetConsumer.retrieve(false);
					updateMessage();
					craeteButtons();
				}
			}

			@Override
			public void failed(IRepository parentObject, IReview modelObject, IStatus status) {
				AbstractReviewSection.appendMessage(getSection(), "Couldn't load patch set: " + status.getMessage());
			}
		});

		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				if (e.getState()) {
					if (set.getItems().isEmpty()) {
						itemSetConsumer.retrieve(false);
					}
					updateMessage();
					craeteButtons();
				}
			}
		});
		createMainSection();
		createItemSetTable();
		updateMessage();
	}

	public void updateMessage() {
		if (section.isDisposed()) {
			return;
		}
		String message;

		String time = DateFormat.getDateTimeInstance().format(set.getCreationDate());
		int numComments = set.getAllComments().size();
		if (numComments > 0) {
			message = NLS.bind("{0}, {1} Comments", time, numComments);
		} else {
			message = NLS.bind("{0}", time);
		}

		if (itemListClient != null && itemListClient.getConsumer().isRetrieving()) {
			message += " " + org.eclipse.mylyn.internal.reviews.ui.Messages.Reviews_RetrievingContents;
		}

		AbstractReviewSection.appendMessage(getSection(), message);
	}

	void createMainSection() {
		Composite composite = parentSection.getToolkit().createComposite(section);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
		section.setClient(composite);

		Label authorLabel = new Label(composite, SWT.NONE);
		FormColors colors = parentSection.getToolkit().getColors();
		authorLabel.setForeground(colors.getColor(IFormColors.TITLE));
		authorLabel.setText("Author");

		Text authorText = new Text(composite, SWT.READ_ONLY);
		if (set.getAddedBy() != null) {
			authorText.setText(set.getAddedBy().getDisplayName());
		} else {
			authorText.setText("Unspecified");
		}

		Label committerLabel = new Label(composite, SWT.NONE);
		committerLabel.setForeground(colors.getColor(IFormColors.TITLE));
		committerLabel.setText("Committer");

		Text committerText = new Text(composite, SWT.READ_ONLY);
		if (set.getCommittedBy() != null) {
			committerText.setText(set.getCommittedBy().getDisplayName());
		} else {
			committerText.setText("Unspecified");
		}

		Label commitLabel = new Label(composite, SWT.NONE);
		commitLabel.setForeground(colors.getColor(IFormColors.TITLE));
		commitLabel.setText("Commit");

		Hyperlink commitLink = new Hyperlink(composite, SWT.READ_ONLY);
		commitLink.setText(set.getRevision());
		commitLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				getParentSection().getUiFactoryProvider()
						.getOpenCommitFactory(ReviewSetContentSection.this.getParentSection(), set)
						.execute();
			}
		});

		Label refLabel = new Label(composite, SWT.NONE);
		refLabel.setForeground(colors.getColor(IFormColors.TITLE));
		refLabel.setText("Ref");

		Text refText = new Text(composite, SWT.READ_ONLY);
		refText.setText(set.getReference());

		tableContainer = new Composite(composite, SWT.NONE);
		tableContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(tableContainer);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(tableContainer);

		actionContainer = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(actionContainer);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(actionContainer);
		craeteButtons();

		parentSection.getTaskEditorPage().reflow();
	}

	public void createItemSetTable() {
		if (viewer == null && !set.getItems().isEmpty()) {

			boolean fixedViewerSize = set.getItems().size() > MAXIMUM_ITEMS_SHOWN;
			int heightHint = fixedViewerSize ? 300 : SWT.DEFAULT;
			int style = SWT.SINGLE | SWT.BORDER | SWT.VIRTUAL;
			if (fixedViewerSize) {
				style |= SWT.V_SCROLL;
			} else {
				style |= SWT.NO_SCROLL;
			}
			viewer = new TableViewer(tableContainer, style);
			GridDataFactory.fillDefaults()
					.span(2, 1)
					.grab(true, true)
					.hint(500, heightHint)
					.applyTo(viewer.getControl());
			viewer.setContentProvider(new IStructuredContentProvider() {

				public void dispose() {
					// ignore
				}

				public Object[] getElements(Object inputElement) {
					return getReviewItems(inputElement).toArray();
				}

				private List<IFileItem> getReviewItems(Object inputElement) {
					if (inputElement instanceof IReviewItemSet) {
						return ((IReviewItemSet) inputElement).getItems();
					}
					return Collections.emptyList();
				}

				public void inputChanged(final Viewer viewer, Object oldInput, Object newInput) {
				}
			});
			ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

			final DelegatingStyledCellLabelProvider styledLabelProvider = new DelegatingStyledCellLabelProvider(
					new ReviewsLabelProvider.Simple()) {
				@Override
				public String getToolTipText(Object element) {
					//For some reason tooltips are not delegated..
					return ReviewsLabelProvider.ITEMS_COLUMN.getToolTipText(element);
				};
			};
			viewer.setLabelProvider(styledLabelProvider);
			viewer.addOpenListener(new IOpenListener() {
				public void open(OpenEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					IFileItem item = (IFileItem) selection.getFirstElement();
					if (item != null) {
						getParentSection().getUiFactoryProvider()
								.getOpenFileFactory(ReviewSetContentSection.this.getParentSection(), set, item)
								.execute();
					}
				}
			});
			EditorUtil.addScrollListener(viewer.getTable());
			viewer.setInput(set);
			getParentSection().getTaskEditorPage().reflow();
		}
	}

	public void updateItemSetTable() {
		if (set.getItems().size() > 0 && viewer != null) {
			viewer.setInput(set);
		}
	}

	public void craeteButtons() {
		if (!actionContainer.isDisposed()) {
			for (Control oldActionControl : actionContainer.getChildren()) {
				oldActionControl.dispose();
			}
			getParentSection().getUiFactoryProvider().createControls(getParentSection(), actionContainer,
					getParentSection().getToolkit(), set);
			actionContainer.layout();
			getParentSection().getTaskEditorPage().reflow();
		}
	}

	public Section getSection() {
		return section;
	}

	public ReviewSetSection getParentSection() {
		return parentSection;
	}

	public void dispose() {
		itemListClient.dispose();
		section.dispose();
	}
}
