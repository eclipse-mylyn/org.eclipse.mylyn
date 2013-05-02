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
import org.eclipse.mylyn.reviews.core.spi.remote.emf.IRemoteEmfObserver;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;
import org.eclipse.mylyn.reviews.core.spi.remote.review.ReviewItemSetContentRemoteFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.review.ReviewItemSetRemoteFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
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

	private boolean buttonsUpdated;

	private final IRemoteEmfObserver<IRepository, IReview, String, Date> reviewObserver = new RemoteEmfObserver<IRepository, IReview, String, Date>() {
		@Override
		public void updated(IRepository parentObject, IReview modelObject, boolean modified) {
			updateButtons();
		}
	};

	private final ReviewItemSetRemoteFactory.Client setClient = new ReviewItemSetRemoteFactory.Client() {

		@Override
		protected boolean isClientReady() {
			return section != null && !section.isDisposed();
		}

		@Override
		protected void update() {
			updateButtons();
		}
	};

	private final ReviewItemSetContentRemoteFactory.Client itemsClient = new ReviewItemSetContentRemoteFactory.Client() {

		@Override
		protected boolean isClientReady() {
			return section != null && !section.isDisposed();
		}

		@Override
		protected void create() {
			updateMessage();
			createItemSetTable(tableContainer);
			viewer.setInput(set);
			getParentSection().getTaskEditorPage().reflow();
		}

		@Override
		protected void update() {
			super.update();
			updateMessage();
			viewer.setInput(set);
			updateButtons();
		}

		@Override
		protected void updating() {
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

	private Composite actionComposite;

	private RemoteEmfConsumer<IRepository, IReview, String, ?, ?, Date> reviewConsumer;

	public ReviewSetContentSection(ReviewSetSection parentSection, final IReviewItemSet set) {
		this.parentSection = parentSection;
		this.set = set;
		//We assume that the last item is also the "current" item
//		List<IReviewItemSet> items = set.getReview().getSets();
//		if (items.get(items.size() - 1) == set) {
//			style |= ExpandableComposite.EXPANDED;
//		}
		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT
				| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;
		section = parentSection.getToolkit().createSection(parentSection.getComposite(), style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
		section.setText(set.getName());
		section.setTitleBarForeground(parentSection.getToolkit().getColors().getColor(IFormColors.TITLE));

		parentSection.addTextClient(parentSection.getToolkit(), section, "", false); //$NON-NLS-1$

		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				if (e.getState()) {
					itemsClient.populate();
					updateButtons();
				}
			}
		});
		createPatchSetControls();
		updateMessage();
	}

	private void createPatchSetControls() {
		RemoteEmfConsumer<IReview, IReviewItemSet, String, ?, ?, String> setConsumer = getParentSection().getReviewEditorPage()
				.getFactoryProvider()
				.getReviewItemSetFactory()
				.getConsumerForModel(set.getParentReview(), set);
		setClient.setConsumer(setConsumer);
		RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, ?, ?, Long> contentConsumer = getParentSection().getReviewEditorPage()
				.getFactoryProvider()
				.getReviewItemSetContentFactory()
				.getConsumerForLocalKey(set, set.getId());
		itemsClient.setConsumer(contentConsumer);
		createMainSection();
	}

	public void updateMessage() {
		String message;

		String time = DateFormat.getDateTimeInstance().format(set.getCreationDate());
		int numComments = set.getAllComments().size();
		if (numComments > 0) {
			message = NLS.bind("{0}, {1} Comments", time, numComments);
		} else {
			message = NLS.bind("{0}", time);
		}

		if (itemsClient.getConsumer().isRetrieving()) {
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
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(actionContainer);

		parentSection.getTaskEditorPage().reflow();
	}

	public void createItemSetTable(Composite composite) {
		boolean fixedViewerSize = set.getItems().size() > MAXIMUM_ITEMS_SHOWN;
		int heightHint = fixedViewerSize ? 300 : SWT.DEFAULT;
		int style = SWT.SINGLE | SWT.BORDER | SWT.VIRTUAL;
		if (fixedViewerSize) {
			style |= SWT.V_SCROLL;
		} else {
			style |= SWT.NO_SCROLL;
		}
		viewer = new TableViewer(composite, style);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).hint(500, heightHint).applyTo(viewer.getControl());
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
	}

	public void updateButtons() {
		if (!buttonsUpdated && setClient.getConsumer().getRemoteObject() != null) {
			if (actionComposite != null) {
				actionComposite.dispose();
			}
			actionComposite = getParentSection().getUiFactoryProvider().createButtons(getParentSection(),
					actionContainer, parentSection.getToolkit(), set);
			getParentSection().getTaskEditorPage().reflow();
			buttonsUpdated = true;
		}
	}

	public Section getSection() {
		return section;
	}

	public ReviewSetSection getParentSection() {
		return parentSection;
	}

	public void dispose() {
		itemsClient.dispose();
		section.dispose();
	}

	public void updateReview() {
		updateButtons();
	}
}
