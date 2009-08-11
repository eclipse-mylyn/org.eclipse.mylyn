/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Abstract part that can be used on the local task editor
 * 
 * @author Shawn Minto
 */
public abstract class AbstractLocalEditorPart extends AbstractFormPart {

	private static final String FLAG_DIRTY = "LocalEditor.dirty"; //$NON-NLS-1$

	private Control control;

	private TaskRepository repository;

	private Section section;

	private final String sectionName;

	private final int sectionStyle;

	private AbstractTask task;

	public AbstractLocalEditorPart(int sectionStyle, String sectionName) {
		this.sectionStyle = sectionStyle;
		this.sectionName = sectionName;
	}

	public AbstractLocalEditorPart(String sectionName) {
		this.sectionStyle = ExpandableComposite.TWISTIE;
		this.sectionName = sectionName;
	}

	public abstract Control createControl(Composite parent, FormToolkit toolkit);

	protected Section createSection(Composite parent, FormToolkit toolkit, boolean expandedState) {
		int style = ExpandableComposite.TITLE_BAR | getSectionStyle();
		if (expandedState && isTwistie(style)) {
			style |= ExpandableComposite.EXPANDED;
		}
		return createSection(parent, toolkit, style);
	}

	protected Section createSection(Composite parent, FormToolkit toolkit, int style) {
		Section section = toolkit.createSection(parent, style);
		section.setText(getSectionName());
		section.clientVerticalSpacing = 0;
		section.descriptionVerticalSpacing = 0;
		section.marginHeight = 0;
		return section;
	}

	public Control getControl() {
		return control;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public Section getSection() {
		return section;
	}

	public String getSectionName() {
		return sectionName;
	}

	private int getSectionStyle() {
		return sectionStyle;
	}

	public AbstractTask getTask() {
		return task;
	}

	public void initialize(IManagedForm managedForm, TaskRepository repository, AbstractTask task) {
		super.initialize(managedForm);
		this.task = task;
		this.repository = repository;
	}

	private boolean isTwistie(int style) {
		return (style & ExpandableComposite.TWISTIE) != 0;
	}

	public void setControl(Control control) {
		this.control = control;
	}

	protected void setSection(FormToolkit toolkit, Section section) {
		this.section = section;
		setControl(section);
	}

	@Override
	public void refresh() {
		refresh(true);
		super.refresh();
	}

	protected abstract void refresh(boolean discardChanges);

	protected void markDirty(Control control) {
		control.setData(FLAG_DIRTY, Boolean.TRUE);
		markDirty();
	}

	protected boolean shouldRefresh(Control control, boolean discardChanges) {
		if (discardChanges) {
			clearState(control);
			return true;
		}
		return control.getData(FLAG_DIRTY) == null;
	}

	protected void clearState(Control control) {
		control.setData(FLAG_DIRTY, null);
	}

}
