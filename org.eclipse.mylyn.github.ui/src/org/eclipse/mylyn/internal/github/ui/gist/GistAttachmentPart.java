/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttachmentPart;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Gist editor attachment part. Modeled after {@link TaskEditorAttachmentPart}
 * but with less columns.
 */
public class GistAttachmentPart extends AbstractTaskEditorPart {

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.attachments"; //$NON-NLS-1$

	private final String[] attachmentsColumns = {
			org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Name,
			org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Size,
			org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Creator };

	private final int[] attachmentsColumnWidths = { 150, 70, 100 };

	private List<TaskAttribute> attachments;

	private boolean hasIncoming;

	private MenuManager menuManager;

	private Composite attachmentsComposite;

	private Table attachmentsTable;

	/**
	 * Create gist editor attachment part
	 */
	public GistAttachmentPart() {
		setPartName(Messages.GistAttachmentPart_PartName);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart#createControl(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	public void createControl(Composite parent, FormToolkit toolkit) {

	}

	/**
	 * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
	 */
	public void dispose() {
		if (menuManager != null)
			menuManager.dispose();
		super.dispose();
	}

	private File getStateFile() {
		IPath stateLocation = Platform.getStateLocation(TasksUiPlugin
				.getDefault().getBundle());
		return stateLocation.append("GistAttachmentPart.xml").toFile(); //$NON-NLS-1$
	}

	private void createButtons(Composite attachmentsComposite,
			FormToolkit toolkit) {
		final Composite attachmentControlsComposite = toolkit
				.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(
				GridData.BEGINNING));

		Button attachFileButton = toolkit
				.createButton(
						attachmentControlsComposite,
						org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Attach_,
						SWT.PUSH);
		attachFileButton.setImage(CommonImages
				.getImage(CommonImages.FILE_PLAIN));
		attachFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(),
						Mode.DEFAULT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachFileButton);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart#fillToolBar(org.eclipse.jface.action.ToolBarManager)
	 */
	protected void fillToolBar(ToolBarManager toolBarManager) {

	}

	/**
	 * @see org.eclipse.ui.forms.AbstractFormPart#setFormInput(java.lang.Object)
	 */
	public boolean setFormInput(Object input) {
		return super.setFormInput(input);
	}

}
