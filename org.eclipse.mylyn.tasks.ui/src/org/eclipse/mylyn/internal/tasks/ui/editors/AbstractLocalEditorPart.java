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

	private final int sectionStyle;

	private final String sectionName;

	protected TaskRepository taskRepository;

	protected AbstractTask task;

	public AbstractLocalEditorPart(String sectionName) {
		this.sectionStyle = ExpandableComposite.TWISTIE;
		this.sectionName = sectionName;
	}

	public AbstractLocalEditorPart(int sectionStyle, String sectionName) {
		this.sectionStyle = sectionStyle;
		this.sectionName = sectionName;
	}

	public void initialize(IManagedForm managedForm, TaskRepository taskRepository, AbstractTask task) {
		super.initialize(managedForm);
		this.task = task;
		this.taskRepository = taskRepository;
	}

	public abstract Control createControl(Composite parent, FormToolkit toolkit);

	public String getSectionName() {
		return sectionName;
	}

	// adapted from AbstractTaskEditorPart
	private Control control;

	private Section section;

	public void setControl(Control control) {
		this.control = control;
	}

	public Control getControl() {
		return control;
	}

	protected void setSection(FormToolkit toolkit, Section section) {
		this.section = section;
		setControl(section);
	}

	protected Section createSection(Composite parent, FormToolkit toolkit, boolean expandedState) {
		int style = ExpandableComposite.TITLE_BAR | getSectionStyle();
		if (expandedState && isTwistie(style)) {
			style |= ExpandableComposite.EXPANDED;
		}
		return createSection(parent, toolkit, style);
	}

	private boolean isTwistie(int style) {
		return (style & ExpandableComposite.TWISTIE) == 1;
	}

	protected Section createSection(Composite parent, FormToolkit toolkit, int style) {
		Section section = toolkit.createSection(parent, style);
		section.setText(getSectionName());
		section.clientVerticalSpacing = 0;
		section.descriptionVerticalSpacing = 0;
		section.marginHeight = 0;
		return section;
	}

	public Section getSection() {
		return section;
	}

	private int getSectionStyle() {
		return sectionStyle;
	}

}
