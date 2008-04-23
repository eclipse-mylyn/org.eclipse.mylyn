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

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.IdentityAttributeFactory;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class TaskEditorDescriptionPart extends TaskEditorRichTextPart {

	private static final String LABEL_SEARCH_DUPS = "Search";

	private static final String LABEL_SELECT_DETECTOR = "Duplicate Detection";

	public TaskEditorDescriptionPart(TaskAttribute attribute) {
		super(attribute);
		setPartName("Description");
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
			Label duplicateDetectorLabel = new Label(relatedBugsComposite, SWT.LEFT);
			duplicateDetectorLabel.setText("Detector:");

			final CCombo duplicateDetectorChooser = new CCombo(relatedBugsComposite, SWT.FLAT | SWT.READ_ONLY);
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
				Button searchForDuplicates = toolkit.createButton(relatedBugsComposite, LABEL_SEARCH_DUPS, SWT.NONE);
				GridData searchDuplicatesButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				searchForDuplicates.setLayoutData(searchDuplicatesButtonData);
				searchForDuplicates.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						String selectedDetector = duplicateDetectorChooser.getItem(duplicateDetectorChooser.getSelectionIndex());
						searchForDuplicates(selectedDetector);
					}
				});
			}

			toolkit.paintBordersFor(relatedBugsComposite);
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		super.createControl(parent, toolkit);

		addDuplicateDetection(getComposite(), toolkit);
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBar) {
		AbstractReplyToCommentAction replyAction = new AbstractReplyToCommentAction(getTaskEditorPage(), 0) {
			@Override
			protected String getReplyText() {
				return getEditor().getValue();
			}
		};
		toolBar.add(replyAction);
	}

	protected AbstractRepositoryQuery getDuplicateSearchCollector(String name) {
		String duplicateDetectorName = name.equals("default") ? "Stack Trace" : name;
		Set<AbstractDuplicateDetector> allDetectors = getDuplicateSearchCollectorsList();

		for (AbstractDuplicateDetector detector : allDetectors) {
			if (detector.getName().equals(duplicateDetectorName)) {
				return detector.getDuplicatesQuery(getTaskEditorPage().getTaskRepository(),
						TaskDataUtil.toLegacyData(getTaskData(), IdentityAttributeFactory.getInstance()));
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
					|| abstractDuplicateDetector.getKind().equals(getTaskEditorPage().getConnectorKind())) {
				duplicateDetectors.add(abstractDuplicateDetector);
			}
		}
		return duplicateDetectors;
	}

	public boolean searchForDuplicates(String duplicateDetectorName) {
		AbstractRepositoryQuery duplicatesQuery = getDuplicateSearchCollector(duplicateDetectorName);
		if (duplicatesQuery != null) {
			SearchHitCollector collector = new SearchHitCollector(TasksUi.getTaskListManager().getTaskList(),
					getTaskEditorPage().getTaskRepository(), duplicatesQuery);
			if (collector != null) {
				NewSearchUI.runQueryInBackground(collector);
				return true;
			}
		}

		return false;
	}

}
