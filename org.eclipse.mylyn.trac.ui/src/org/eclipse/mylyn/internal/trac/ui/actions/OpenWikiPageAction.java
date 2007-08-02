/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.actions;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.trac.core.AbstractWikiHandler;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

/**
 * @author Xiaoyang Guan
 */
public class OpenWikiPageAction extends Action implements IViewActionDelegate {

	private TaskRepository repository;

	private AbstractWikiHandler wikiHandler;

	private class OpenWikiPageSelectionDialog extends FilteredItemsSelectionDialog {

		private static final String OPEN_WIKI_PAGE_DIALOG_DIALOG_SETTINGS = "org.eclipse.mylyn.trac.ui.open.wikipage";

		private String[] pageNames;

		private DownloadAllPageNamesJob downloadAllPageNamesJob;

		private class DownloadAllPageNamesJob extends Job {

			public DownloadAllPageNamesJob() {
				super("Downloading All Wiki Page Names");
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					pageNames = wikiHandler.downloadAllPageNames(repository, monitor);
					// refresh the items list with the fetched page names
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							OpenWikiPageSelectionDialog.this.applyFilter();
						}
					});
				} catch (CoreException e) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							OpenWikiPageSelectionDialog.this.close();
						}
					});
					StatusHandler.displayStatus("Unable to download Wiki page names", e.getStatus());
				}
				return Status.OK_STATUS;
			}
		}

		public OpenWikiPageSelectionDialog(Shell shell, boolean multi) {
			super(shell, multi);
			setTitle("Open Wiki Page");
			setSelectionHistory(new PageSelectionHistory());
			setInitialPattern("**");
			setPageNames();
		}

		private void setPageNames() {
			downloadAllPageNamesJob = new DownloadAllPageNamesJob();
			downloadAllPageNamesJob.setUser(true);
			downloadAllPageNamesJob.schedule();
		}

		private void stopDownloading() {
			if (downloadAllPageNamesJob != null) {
				downloadAllPageNamesJob.cancel();
			}
		}

		private class PageSelectionHistory extends SelectionHistory {

			@Override
			protected Object restoreItemFromMemento(IMemento memento) {
				return null;
			}

			@Override
			protected void storeItemToMemento(Object item, IMemento memento) {
			}

		}

		@Override
		protected Control createExtendedContentArea(Composite parent) {
			return null;
		}

		@Override
		protected ItemsFilter createFilter() {
			// return null if the download job hasn't finished so the real filter
			// can be applied only after the pageNames has been populated
			if (pageNames == null) {
				return null;
			}
			return new ItemsFilter() {

				@Override
				public boolean isConsistentItem(Object item) {
					return true;
				}

				@Override
				public boolean matchItem(Object item) {
					if (item != null) {
						return matches(item.toString());
					}
					return false;
				}

			};
		}

		@Override
		protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
				IProgressMonitor progressMonitor) throws CoreException {
			if (pageNames != null) {
				progressMonitor.beginTask("Searching", pageNames.length);
				for (int i = 0; i < pageNames.length; i++) {
					contentProvider.add(pageNames[i], itemsFilter);
					progressMonitor.worked(1);
				}
			}
			progressMonitor.done();
		}

		@Override
		protected IDialogSettings getDialogSettings() {
			IDialogSettings settings = TracUiPlugin.getDefault().getDialogSettings().getSection(
					OPEN_WIKI_PAGE_DIALOG_DIALOG_SETTINGS);
			if (settings == null) {
				settings = TracUiPlugin.getDefault().getDialogSettings().addNewSection(
						OPEN_WIKI_PAGE_DIALOG_DIALOG_SETTINGS);
			}
			return settings;
		}

		@Override
		public String getElementName(Object item) {
			return item.toString();
		}

		@Override
		protected Comparator<Object> getItemsComparator() {
			return new Comparator<Object>() {

				public int compare(Object o1, Object o2) {
					return o1.toString().compareTo(o2.toString());
				}

			};
		}

		@Override
		protected IStatus validateItem(Object item) {
			// ignore
			return Status.OK_STATUS;
		}

		@Override
		protected void cancelPressed() {
			stopDownloading();
			super.cancelPressed();
		}

		@Override
		protected void handleShellCloseEvent() {
			stopDownloading();
			super.handleShellCloseEvent();
		}
	}

	public void init(IViewPart view) {
	}

	public void run(IAction action) {
		OpenWikiPageSelectionDialog openDialog = new OpenWikiPageSelectionDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), true);

		if (openDialog.open() == Window.OK) {
			Object[] selections = openDialog.getResult();
			if (selections != null) {
				for (int i = 0; i < selections.length; i++) {
					openWikiPage(wikiHandler.getWikiUrl(repository) + (String) selections[i]);
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		repository = getSelectedRepository(selection);
		if (repository != null) {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			// Note: the following type checking on the connector won't be needed once the Wiki
			//       functionality is generalized into AbstractRepositoryConnector
			if (connector instanceof TracRepositoryConnector) {
				TracRepositoryConnector tracConnector = (TracRepositoryConnector) connector;
				wikiHandler = tracConnector.getWikiManager();
				action.setEnabled(tracConnector.hasWiki(repository));
			} else {
				action.setEnabled(false);
			}
		} else {
			action.setEnabled(false);
		}
	}

	private TaskRepository getSelectedRepository(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof TaskRepository) {
				return (TaskRepository) selectedObject;
			}
		}
		return null;
	}

	private void openWikiPage(String pageUrl) {
		TasksUiUtil.openUrl(pageUrl, false);
	}
}
