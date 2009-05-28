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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.internal.tasks.ui.util.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IFormPart;
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

	public AttributePart() {
		super(Messages.TaskPlanningEditor_Attributes);
	}

	@Override
	public void commit(boolean onSave) {
		getTask().setUrl(urlEditor.getText());
		super.commit(onSave);
	}

	@Override
	public Control createControl(Composite parent, FormToolkit toolkit) {
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE;
		if (getTask().getUrl() != null && getTask().getUrl().length() > 0) {
			style |= ExpandableComposite.EXPANDED;
		}

		Section section = toolkit.createSection(parent, style);
		section.setText(Messages.TaskPlanningEditor_Attributes);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(section);

		// URL
		Composite urlComposite = toolkit.createComposite(section);
		GridLayout urlLayout = new GridLayout(4, false);
		urlLayout.verticalSpacing = 0;
		urlLayout.marginWidth = 0;
		urlComposite.setLayout(urlLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(urlComposite);

		Label label = toolkit.createLabel(urlComposite, Messages.TaskPlanningEditor_URL);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		urlEditor = new RichTextEditor(getRepository(), SWT.FLAT | SWT.SINGLE) {
			@Override
			protected void valueChanged(String value) {
				updateButtons();
				markDirty();
			}
		};
		urlEditor.setMode(Mode.URL);
		urlEditor.createControl(urlComposite, toolkit);
		urlEditor.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		urlEditor.getViewer().getControl().setMenu(parent.getMenu());

		fetchUrlLink = toolkit.createImageHyperlink(urlComposite, SWT.NONE);
		fetchUrlLink.setImage(CommonImages.getImage(TasksUiImages.TASK_RETRIEVE));
		fetchUrlLink.setToolTipText(Messages.TaskPlanningEditor_Retrieve_task_description_from_URL);
		fetchUrlLink.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		fetchUrlLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				featchUrl(urlEditor.getText());
			}
		});

		refresh();

		toolkit.paintBordersFor(urlComposite);
		section.setClient(urlComposite);
		return section;
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
	public void refresh() {
		String url = getTask().getUrl();
		urlEditor.setText(url != null ? url : ""); //$NON-NLS-1$
		updateButtons();
		super.refresh();
	}

}
