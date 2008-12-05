/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
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

/**
 * @author Steffen Pingel
 */
public class TaskEditorDescriptionPart extends TaskEditorRichTextPart {

	public TaskEditorDescriptionPart() {
		setPartName(Messages.TaskEditorDescriptionPart_Description);
	}

	private void addDuplicateDetection(Composite composite, FormToolkit toolkit) {
		List<AbstractDuplicateDetector> allCollectors = new ArrayList<AbstractDuplicateDetector>();
		if (getDuplicateSearchCollectorsList() != null) {
			allCollectors.addAll(getDuplicateSearchCollectorsList());
		}
		if (!allCollectors.isEmpty()) {
			int style = ExpandableComposite.TWISTIE | ExpandableComposite.SHORT_TITLE_BAR;
			if (getTaskData().isNew()) {
				style |= ExpandableComposite.EXPANDED;
			}
			Section duplicatesSection = toolkit.createSection(composite, style);
			duplicatesSection.setText(Messages.TaskEditorDescriptionPart_Duplicate_Detection);
			duplicatesSection.setLayout(new GridLayout());
			GridDataFactory.fillDefaults().indent(SWT.DEFAULT, 15).applyTo(duplicatesSection);
			Composite relatedBugsComposite = toolkit.createComposite(duplicatesSection);
			relatedBugsComposite.setLayout(new GridLayout(4, false));
			relatedBugsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			duplicatesSection.setClient(relatedBugsComposite);
			Label duplicateDetectorLabel = new Label(relatedBugsComposite, SWT.LEFT);
			duplicateDetectorLabel.setText(Messages.TaskEditorDescriptionPart_Detector);

			final CCombo duplicateDetectorChooser = new CCombo(relatedBugsComposite, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(duplicateDetectorChooser, false, false);
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
				Button searchForDuplicates = toolkit.createButton(relatedBugsComposite, Messages.TaskEditorDescriptionPart_Search, SWT.NONE);
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
		if (getAttribute() == null) {
			return;
		}

		super.createControl(parent, toolkit);
		addDuplicateDetection(getComposite(), toolkit);
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBar) {
		if (!getTaskData().isNew()) {
			AbstractReplyToCommentAction replyAction = new AbstractReplyToCommentAction(getTaskEditorPage(), null) {
				@Override
				protected String getReplyText() {
					return getEditor().getValue();
				}
			};
			toolBar.add(replyAction);
		}
		super.fillToolBar(toolBar);
	}

	protected IRepositoryQuery getDuplicateQuery(String name) throws CoreException {
		String duplicateDetectorName = name.equals("default") ? "Stack Trace" : name; //$NON-NLS-1$ //$NON-NLS-2$
		for (AbstractDuplicateDetector detector : getDuplicateSearchCollectorsList()) {
			if (detector.getName().equals(duplicateDetectorName)) {
				return detector.getDuplicatesQuery(getTaskEditorPage().getTaskRepository(), getTaskData());
			}
		}
		return null;
	}

	protected Set<AbstractDuplicateDetector> getDuplicateSearchCollectorsList() {
		Set<AbstractDuplicateDetector> duplicateDetectors = new HashSet<AbstractDuplicateDetector>();
		for (AbstractDuplicateDetector detector : TasksUiPlugin.getDefault().getDuplicateSearchCollectorsList()) {
			if (isValidDuplicateDetector(detector)) {
				duplicateDetectors.add(detector);
			}
		}
		return duplicateDetectors;
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		setAttribute(getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION));
	}

	private boolean isValidDuplicateDetector(AbstractDuplicateDetector detector) {
		return (detector.getConnectorKind() == null || detector.getConnectorKind().equals(
				getTaskEditorPage().getConnectorKind())) //
				&& detector.canQuery(getTaskData());
	}

	public void searchForDuplicates(String duplicateDetectorName) {
		try {
			IRepositoryQuery duplicatesQuery = getDuplicateQuery(duplicateDetectorName);
			if (duplicatesQuery != null) {
				SearchHitCollector collector = new SearchHitCollector(TasksUiInternal.getTaskList(),
						getTaskEditorPage().getTaskRepository(), duplicatesQuery);
				NewSearchUI.runQueryInBackground(collector);
			} else {
				TasksUiInternal.displayStatus(Messages.TaskEditorDescriptionPart_Duplicate_Detection_Failed, new Status(IStatus.ERROR,
						TasksUiPlugin.ID_PLUGIN, Messages.TaskEditorDescriptionPart_The_duplicate_detector_did_not_return_a_valid_query));
			}
		} catch (CoreException e) {
			TasksUiInternal.displayStatus(Messages.TaskEditorDescriptionPart_Duplicate_Detection_Failed, e.getStatus());
		}
	}

}
