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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFormUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonTextSupport;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Shawn Minto
 */
public class TaskEditorPlanningPart extends AbstractTaskEditorPart {

	private class NotesAction extends Action {
		public NotesAction() {
			setImageDescriptor(CommonImages.NOTES_SMALL);
			setToolTipText("Add Private Notes");
		}

		@Override
		public void run() {
			CommonFormUtil.setExpanded(part.getSection(), true);
			part.getControl().setFocus();
		}
	};

	private final PlanningPart part;

	public TaskEditorPlanningPart() {
		part = new PlanningPart(ExpandableComposite.TWISTIE) {
			@Override
			protected void fillToolBar(ToolBarManager toolBarManager) {
				toolBarManager.add(new NotesAction());
			}
		};
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		boolean needsDueDate = !taskEditorPage.getConnector().hasRepositoryDueDate(taskEditorPage.getTaskRepository(),
				taskEditorPage.getTask(), getTaskData());
		CommonTextSupport textSupport = (CommonTextSupport) getTaskEditorPage().getAdapter(CommonTextSupport.class);
		// disable notes for new tasks to avoid confusion due to showing multiple input fields
		part.initialize(taskEditorPage.getManagedForm(), taskEditorPage.getTaskRepository(),
				(AbstractTask) taskEditorPage.getTask(), needsDueDate, taskEditorPage, textSupport);
		part.setNeedsNotes(!getModel().getTaskData().isNew());
		part.setAlwaysExpand(getModel().getTaskData().isNew());
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		part.createControl(parent, toolkit);
		part.getSection().setToolTipText(Messages.TaskEditorPlanningPart_TaskEditorPlanningPart_tooltip);
		setSection(toolkit, part.getSection());
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		part.commit(onSave);
	}

	@Override
	public boolean isDirty() {
		return super.isDirty() || part.isDirty();
	}

	@Override
	public void dispose() {
		part.dispose();
	}

	public PlanningPart getPlanningPart() {
		return part;
	}

}
