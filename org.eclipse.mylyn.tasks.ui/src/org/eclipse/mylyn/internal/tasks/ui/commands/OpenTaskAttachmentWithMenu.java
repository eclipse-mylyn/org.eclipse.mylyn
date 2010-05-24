/*******************************************************************************
 * Copyright (c) 2010 Peter Stibrany and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Peter Stibrany - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.mylyn.internal.tasks.ui.ITaskAttachmentViewer;
import org.eclipse.mylyn.internal.tasks.ui.TaskAttachmentViewerManager;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Peter Stibrany
 */
public class OpenTaskAttachmentWithMenu extends ContributionItem {

	private final TaskAttachmentViewerManager manager = new TaskAttachmentViewerManager();

	public OpenTaskAttachmentWithMenu() {
	}

	public OpenTaskAttachmentWithMenu(String id) {
		super(id);
	}

	@Override
	public void fill(Menu menu, int index) {
		List<ITaskAttachment> attachments = AttachmentUtil.getSelectedAttachments(null);
		if (attachments.isEmpty() || attachments.size() > 1) {
			return;
		}

		// find all interesting editors, and add them into menu
		ITaskAttachment attachment = attachments.get(0);
		String viewerId = manager.getPreferredViewerID(attachment);

		int itemsAdded = 0;
		ITaskAttachmentViewer viewer = manager.getBrowserViewer(attachment);
		if (viewer != null) {
			itemsAdded = addItems(menu, index, Collections.singletonList(viewer), attachments, viewerId);
			index += itemsAdded;
		}

		List<ITaskAttachmentViewer> viewers = manager.getWorkbenchViewers(attachment);
		if (viewers.size() > 0) {
			itemsAdded = addSeparator(menu, index, itemsAdded);
			index += itemsAdded;

			itemsAdded = addItems(menu, index, viewers, attachments, viewerId);
			index += itemsAdded;
		}

		viewers = manager.getSystemViewers(attachment);
		if (viewers.size() > 0) {
			itemsAdded = addSeparator(menu, index, itemsAdded);
			index += itemsAdded;

			itemsAdded = addItems(menu, index, viewers, attachments, viewerId);
			index += itemsAdded;
		}
	}

	protected int addSeparator(Menu menu, int index, int itemsAdded) {
		if (itemsAdded > 0) {
			new MenuItem(menu, SWT.SEPARATOR, index);
			return 1;
		}
		return 0;
	}

	protected int addItems(Menu menu, int index, List<ITaskAttachmentViewer> viewers,
			List<ITaskAttachment> attachments, String viewerId) {
		int i = 0;
		for (ITaskAttachmentViewer viewer : viewers) {
			MenuItem item = new MenuItem(menu, SWT.RADIO, index + i);
			item.setText(viewer.getLabel());
			item.addSelectionListener(new RunAssociatedViewer(viewer, attachments));
			if (viewerId != null && viewerId.equals(viewer.getId())) {
				item.setSelection(true);
			}
			i++;
		}
		return i;
	}

	private class RunAssociatedViewer extends SelectionAdapter {

		private final ITaskAttachmentViewer viewer;

		private final List<ITaskAttachment> attachments;

		RunAssociatedViewer(ITaskAttachmentViewer handler, List<ITaskAttachment> attachments) {
			this.attachments = attachments;
			this.viewer = handler;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (!((MenuItem) e.widget).getSelection()) {
				// if the default viewer changes ignore the event for the unselected menu item
				return;
			}

			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					// this event is fired also for item which gets unselected (i.e. previous 'preferred' viewer)
					for (ITaskAttachment attachment : attachments) {
						manager.savePreferredViewerID(attachment, viewer.getId());
						try {
							viewer.openAttachment(page, attachment);
						} catch (CoreException ex) {
							TasksUiInternal.logAndDisplayStatus(Messages.OpenTaskAttachmentHandler_failedToOpenViewer,
									ex.getStatus());
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isDynamic() {
		// menu depends on selected attachment(s)
		return true;
	}

}
