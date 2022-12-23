/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.internal.tasks.ui.util.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskContainerComparator;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class AttributePart extends AbstractLocalEditorPart {

	private ImageHyperlink fetchUrlLink;

	private RichTextEditor urlEditor;

	private CCombo categoryChooser;

	protected AbstractTaskCategory category;

	private Label categoryLabel;

	private List<AbstractTaskCategory> categories;

	public AttributePart() {
		super(Messages.TaskPlanningEditor_Attributes);
	}

	@Override
	public void commit(boolean onSave) {
		if (category != null) {
			TasksUiPlugin.getTaskList().addTask(getTask(), category);
			category = null;
			clearState(categoryChooser);
		}
		getTask().setUrl(urlEditor.getText());
		clearState(urlEditor.getControl());
		super.commit(onSave);
	}

	@Override
	public Control createControl(Composite parent, FormToolkit toolkit) {
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE;
//				| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;
		if (getTask().getUrl() != null && getTask().getUrl().length() > 0) {
			style |= ExpandableComposite.EXPANDED;
		}

		Section section = createSection(parent, toolkit, style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
		section.setText(Messages.TaskPlanningEditor_Attributes);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
		createSectionClient(section, toolkit);
		setSection(toolkit, section);

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = EditorUtil.createSectionClientLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		Label label = toolkit.createLabel(composite, Messages.AttributePart_Category_);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		createCategoryChooser(composite, toolkit);

		// url
		label = toolkit.createLabel(composite, Messages.TaskPlanningEditor_URL);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		GridDataFactory.defaultsFor(label).indent(20, 0).applyTo(label);

		Composite urlComposite = toolkit.createComposite(composite);
		GridLayout urlLayout = new GridLayout(2, false);
		urlLayout.verticalSpacing = 0;
		urlLayout.marginWidth = 1;
		urlComposite.setLayout(urlLayout);
		GridDataFactory.fillDefaults()
				.grab(true, false)
				.hint(EditorUtil.MAXIMUM_WIDTH, SWT.DEFAULT)
				.applyTo(urlComposite);

		urlEditor = new RichTextEditor(getRepository(), SWT.FLAT | SWT.SINGLE, null, null, getTask()) {
			@Override
			protected void valueChanged(String value) {
				updateButtons();
				markDirty(urlEditor.getControl());
			}
		};
		urlEditor.setMode(Mode.URL);
		urlEditor.createControl(urlComposite, toolkit);
		urlEditor.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		urlEditor.getViewer().getControl().setMenu(parent.getMenu());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(urlEditor.getControl());

		fetchUrlLink = toolkit.createImageHyperlink(urlComposite, SWT.NONE);
		fetchUrlLink.setImage(CommonImages.getImage(TasksUiImages.TASK_RETRIEVE));
		fetchUrlLink.setToolTipText(Messages.TaskPlanningEditor_Retrieve_task_description_from_URL);
		fetchUrlLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				featchUrl(urlEditor.getText());
			}
		});
		toolkit.paintBordersFor(urlComposite);

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		return section;
	}

	private void createSectionClient(final Section section, FormToolkit toolkit) {
		if (section.getTextClient() == null) {
			final Composite textClient = toolkit.createComposite(section);
			textClient.setBackground(null);
			RowLayout rowLayout = new RowLayout();
			rowLayout.center = true;
			rowLayout.marginLeft = 20;
			rowLayout.marginTop = 1;
			rowLayout.marginBottom = 1;
			textClient.setLayout(rowLayout);

			Label label = toolkit.createLabel(textClient, Messages.AttributePart_Category_);
			label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			label.setBackground(null);

			categoryLabel = toolkit.createLabel(textClient, ""); //$NON-NLS-1$
			categoryLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			categoryLabel.setBackground(null);

			toolkit.paintBordersFor(textClient);

			section.setTextClient(textClient);
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanging(ExpansionEvent e) {
					textClient.setVisible(!e.getState());
				}
			});
			textClient.setVisible(!section.isExpanded());
		}
	}

	/**
	 * Set the task summary to the page title from the specified url.
	 */
	private void featchUrl(final String url) {
		AbstractRetrieveTitleFromUrlJob job = new AbstractRetrieveTitleFromUrlJob(urlEditor.getText()) {
			@Override
			protected void titleRetrieved(String pageTitle) {
				IFormPart[] parts = getManagedForm().getParts();
				for (IFormPart part : parts) {
					if (part instanceof SummaryPart) {
						((SummaryPart) part).setSummary(pageTitle);
					}
				}
			}
		};
		job.schedule();
	}

	private void updateButtons() {
		String value = urlEditor.getText();
		fetchUrlLink.setEnabled(value.startsWith("http://") || value.startsWith("https://")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void refresh(boolean discardChanges) {
		if (shouldRefresh(categoryChooser, discardChanges)) {
			ITaskList taskList = TasksUiInternal.getTaskList();
			categories = new ArrayList<AbstractTaskCategory>(taskList.getCategories());
			Collections.sort(categories, new TaskContainerComparator());

			AbstractTaskCategory selectedCategory = category;
			if (selectedCategory == null) {
				selectedCategory = TaskCategory.getParentTaskCategory(getTask());
			}
			categoryChooser.removeAll();
			int selectedIndex = 0;
			for (int i = 0; i < categories.size(); i++) {
				AbstractTaskCategory category = categories.get(i);
				categoryChooser.add(category.getSummary());
				if (category.equals(selectedCategory)) {
					selectedIndex = i;
				}
			}
			categoryChooser.select(selectedIndex);
			updateCategoryLabel();
		}

		if (shouldRefresh(urlEditor.getControl(), discardChanges)) {
			String url = getTask().getUrl();
			urlEditor.setText(url != null ? url : ""); //$NON-NLS-1$
		}

		updateButtons();
	}

	private void updateCategoryLabel() {
		if (category == null) {
			AbstractTaskCategory parentTaskCategory = TaskCategory.getParentTaskCategory(getTask());
			categoryLabel.setText((parentTaskCategory != null) ? parentTaskCategory.getSummary() : ""); //$NON-NLS-1$
		} else {
			categoryLabel.setText(category.getSummary());
		}
		if (!getSection().isExpanded()) {
			getSection().layout(true, true);
		}
	}

	private void createCategoryChooser(Composite buttonComposite, FormToolkit toolkit) {
		categoryChooser = new CCombo(buttonComposite, SWT.FLAT | SWT.READ_ONLY);
		categoryChooser.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		toolkit.adapt(categoryChooser, false, false);
		categoryChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (categoryChooser.getSelectionIndex() != -1) {
					category = categories.get(categoryChooser.getSelectionIndex());
					updateCategoryLabel();
					markDirty(categoryChooser);
				}
			}
		});
	}

}
