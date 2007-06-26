/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class CategoryEditor extends EditorPart {

	public static final String ID_EDITOR = "org.eclipse.mylyn.tasks.ui.editors.category";

	private Text url;

	private CategoryEditorInput input = null;

	private boolean isDirty = false;

	private Text description = null;

	@Override
	public void doSave(IProgressMonitor monitor) {
		input.setCategoryName(description.getText());
		input.setUrl(url.getText());
		isDirty = false;
		// TODO: save the list
//		MylarTaskListPlugin.getTaskListManager().notifyListUpdated();
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		this.input = (CategoryEditorInput) input;
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm sform = toolkit.createScrolledForm(parent);
		sform.getBody().setLayout(new TableWrapLayout());
		Composite editorComposite = sform.getBody();

		createSummarySection(editorComposite, toolkit);
	}

	@Override
	public void setFocus() {
	}

	private void createSummarySection(Composite parent, FormToolkit toolkit) {
		Section summarySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		summarySection.setText("Category Summary");
		summarySection.setLayout(new TableWrapLayout());
		summarySection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		Composite summaryContainer = toolkit.createComposite(summarySection);
		summarySection.setClient(summaryContainer);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		summaryContainer.setLayout(layout);

		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty();
			}
		};

		toolkit.createLabel(summaryContainer, "Description: ", SWT.NULL);
//		lblDescription.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		description = toolkit.createText(summaryContainer, input.getCategoryName(), SWT.FLAT);
		description.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		description.setData(FormToolkit.KEY_DRAW_BORDER);
		description.addModifyListener(modifyListener);

		toolkit.createLabel(summaryContainer, "URL: ", SWT.NONE);
//		lblUrl.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		url = toolkit.createText(summaryContainer, input.getUrl(), SWT.FLAT);
		url.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));
		url.addModifyListener(modifyListener);

		toolkit.paintBordersFor(summaryContainer);
	}

	private void markDirty() {
		isDirty = true;
		firePropertyChange(PROP_DIRTY);
	}
}
