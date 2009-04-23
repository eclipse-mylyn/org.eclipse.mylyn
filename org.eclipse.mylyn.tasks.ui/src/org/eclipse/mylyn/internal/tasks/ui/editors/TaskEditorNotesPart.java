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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonTextSupport;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.EditorAreaHelper;
import org.eclipse.ui.internal.WorkbenchPage;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class TaskEditorNotesPart extends AbstractTaskEditorPart {

	private String value;

	private AbstractTask task;

	private SourceViewer noteEditor;

	public TaskEditorNotesPart() {
		setPartName(Messages.TaskPlanningEditor_Notes);
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		task = (AbstractTask) taskEditorPage.getTask();
	}

	private boolean notesEqual() {
		if (task.getNotes() == null && value == null) {
			return true;
		}

		if (task.getNotes() != null && value != null) {
			return task.getNotes().equals(value);
		}
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		Assert.isNotNull(task);

		if (!notesEqual()) {
			task.setNotes(value);
			// XXX REFRESH THE TASLKIST
		}

		super.commit(onSave);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		this.value = task.getNotes();
		if (this.value == null) {
			this.value = ""; //$NON-NLS-1$
		}

		Section section = createSection(parent, toolkit, this.value != null && this.value.length() > 0);

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		noteEditor = new SourceViewer(composite, null, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		noteEditor.configure(new RepositoryTextViewerConfiguration(getModel().getTaskRepository(), true));
		CommonTextSupport textSupport = (CommonTextSupport) getTaskEditorPage().getAdapter(CommonTextSupport.class);
		if (textSupport != null) {
			textSupport.configure(noteEditor, new Document(this.value), true);
		}
		noteEditor.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				TaskEditorNotesPart.this.value = noteEditor.getTextWidget().getText();
				markDirty();
			}
		});

		final GridData gd = new GridData(GridData.FILL_BOTH);
		int widthHint = 0;

		if (getManagedForm() != null && getManagedForm().getForm() != null) {
			widthHint = getManagedForm().getForm().getClientArea().width - 90;
		}
		if (widthHint <= 0 && getTaskEditorPage().getEditor().getEditorSite() != null
				&& getTaskEditorPage().getEditor().getEditorSite().getPage() != null) {
			EditorAreaHelper editorManager = ((WorkbenchPage) getTaskEditorPage().getEditor().getEditorSite().getPage()).getEditorPresentation();
			if (editorManager != null && editorManager.getLayoutPart() != null) {
				widthHint = editorManager.getLayoutPart().getControl().getBounds().width - 90;
			}
		}

		if (widthHint <= 0) {
			widthHint = 100;
		}

		gd.widthHint = widthHint;
		gd.minimumHeight = 100;
		gd.grabExcessHorizontalSpace = true;

		noteEditor.getControl().setLayoutData(gd);
		noteEditor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		noteEditor.setEditable(true);

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
	}

}
