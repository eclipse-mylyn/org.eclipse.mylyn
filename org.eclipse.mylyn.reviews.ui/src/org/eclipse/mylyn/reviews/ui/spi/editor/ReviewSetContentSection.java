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
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.providers.ReviewsLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.IRemoteEmfObserver;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;
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
public class ReviewSetContentSection implements IRemoteEmfObserver<IReviewItemSet, List<IFileItem>> {

	private static final int MAXIMUM_ITEMS_SHOWN = 30;

	protected ReviewsLabelProvider labelProvider;

	private final ReviewSetSection parentSection;

	private final IReviewItemSet set;

	private final Section section;

	private TableViewer viewer;

	private final RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, ?, ?, String> consumer;

	private boolean createdContentSection;

	private boolean requestedModelContents;

	private boolean modelContentsCurrent;

	private Composite tableContainer;

	public ReviewSetContentSection(ReviewSetSection parentSection, final IReviewItemSet set) {
		this.parentSection = parentSection;
		this.set = set;
		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT
				| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;
		//We assume that the last item is also the "current" item
		List<IReviewItemSet> items = set.getReview().getSets();
		if (items.get(items.size() - 1) == set) {
			style |= ExpandableComposite.EXPANDED;
		}
		consumer = getParentSection().getReviewEditorPage()
				.getFactoryProvider()
				.getReviewItemSetContentFactory()
				.getConsumerForRemoteKey(set, set.getId());
		consumer.addObserver(this);
		section = parentSection.getToolkit().createSection(parentSection.getComposite(), style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
		section.setText(set.getName());
		section.setTitleBarForeground(parentSection.getToolkit().getColors().getColor(IFormColors.TITLE));

		parentSection.addTextClient(parentSection.getToolkit(), section, "", false); //$NON-NLS-1$
		updateMessage();

		if (section.isExpanded()) {
			onExpanded();
		}
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				onExpanded();
			}
		});
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

		if (consumer.isRetrieving()) {
			message += " " + org.eclipse.mylyn.internal.reviews.ui.Messages.Reviews_RetrievingContents;
		}

		AbstractReviewSection.appendMessage(getSection(), message);
	}

	protected void onExpanded() {
		updateMessage();
		if (!requestedModelContents) {
			consumer.retrieve(false);
			requestedModelContents = true;
		}
		checkCreateModelControls();
	}

	void createContents() {
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

		Composite actionComposite = getParentSection().getUiFactoryProvider().createButtons(getParentSection(),
				composite, parentSection.getToolkit(), set);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(actionComposite);

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
			private EContentAdapter modelAdapter;

			private int addedDrafts;

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
				if (modelAdapter != null) {
					for (IReviewItem item : getReviewItems(oldInput)) {
						((EObject) item).eAdapters().remove(modelAdapter);
					}
					addedDrafts = 0;
				}

				if (newInput instanceof IReviewItemSet) {
					// monitors any new topics that are added
					modelAdapter = new EContentAdapter() {
						@Override
						public void notifyChanged(Notification notification) {
							super.notifyChanged(notification);
							if (notification.getFeatureID(IReviewItem.class) == ReviewsPackage.REVIEW_ITEM__TOPICS
									&& notification.getEventType() == Notification.ADD) {
								viewer.refresh();
								addedDrafts++;
							}
						}
					};
					for (Object item : getReviewItems(newInput)) {
						((EObject) item).eAdapters().add(modelAdapter);
					}
				}
			}
		});
		labelProvider = new ReviewsLabelProvider.Simple();
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));
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

	/**
	 * We don't know whether the model or the controls will be available first, so we handle both cases here.
	 */
	private void checkCreateModelControls() {
		if (section.isExpanded()) {
			if (!createdContentSection) {
				createdContentSection = true;
				createContents();
			}
			if (requestedModelContents && !set.getItems().isEmpty() && ((viewer == null || !modelContentsCurrent))) {
				modelContentsCurrent = true;
				if (viewer == null) {
					createItemSetTable(tableContainer);
				}
				viewer.setInput(set);
				getParentSection().getTaskEditorPage().reflow();
			}
		}
		updateMessage();
	}

	public Section getSection() {
		return section;
	}

	public ReviewSetSection getParentSection() {
		return parentSection;
	}

	public void created(IReviewItemSet parent, List<IFileItem> object) {
	}

	public void updating(IReviewItemSet parent, List<IFileItem> object) {
		updateMessage();
	}

	public void updated(IReviewItemSet parent, List<IFileItem> object, boolean modified) {
		modelContentsCurrent &= !modified;
		checkCreateModelControls();
	}

	public void failed(IReviewItemSet parent, List<IFileItem> object, IStatus status) {
		StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
				"Error loading patch set", status.getException())); //$NON-NLS-1$
		AbstractReviewSection.appendMessage(getParentSection().getSection(), "Couldn't load patch set.");
	}

	public void dispose() {
		consumer.removeObserver(this);
		section.dispose();
	}
}
