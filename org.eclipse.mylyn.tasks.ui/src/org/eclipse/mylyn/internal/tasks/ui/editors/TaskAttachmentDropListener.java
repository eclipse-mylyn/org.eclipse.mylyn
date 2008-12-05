/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Maarten Meijer - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.core.data.TextTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Mik Kersten
 * @author Maarten Meijer
 * @author Steffen Pingel
 */
public class TaskAttachmentDropListener implements DropTargetListener {

	private final AbstractTaskEditorPage page;

	public TaskAttachmentDropListener(AbstractTaskEditorPage page) {
		this.page = page;
	}

	public void dragEnter(DropTargetEvent event) {
		if (event.detail == DND.DROP_DEFAULT) {
			if ((event.operations & DND.DROP_COPY) != 0) {
				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
		// will accept text but prefer to have files dropped
		for (TransferData dataType : event.dataTypes) {
			if (FileTransfer.getInstance().isSupportedType(dataType)) {
				event.currentDataType = dataType;
				// files should only be copied
				if (event.detail != DND.DROP_COPY) {
					event.detail = DND.DROP_NONE;
				}
				break;
			}
		}
	}

	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
	}

	public void dragOperationChanged(DropTargetEvent event) {
		if ((event.detail == DND.DROP_DEFAULT) || (event.operations & DND.DROP_COPY) != 0) {
			event.detail = DND.DROP_COPY;
		} else {
			event.detail = DND.DROP_NONE;
		}
		// allow text to be moved but files should only be copied
		if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
			if (event.detail != DND.DROP_COPY) {
				event.detail = DND.DROP_NONE;
			}
		}
	}

	public void dragLeave(DropTargetEvent event) {
	}

	public void dropAccept(DropTargetEvent event) {
	}

	public void drop(DropTargetEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			String text = (String) event.data;
			EditorUtil.openNewAttachmentWizard(page, null, new TextTaskAttachmentSource(text));
		}
		if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
			String[] files = (String[]) event.data;
			if (files.length > 0) {
				File file = new File(files[0]);
				NewAttachmentWizardDialog dialog = EditorUtil.openNewAttachmentWizard(page, null,
						new FileTaskAttachmentSource(file));
				if (files.length > 1) {
					dialog.setMessage(Messages.TaskAttachmentDropListener_Note_that_only_the_first_file_dragged_will_be_attached,
							IMessageProvider.WARNING);
				}
			}
		}
	}

}
