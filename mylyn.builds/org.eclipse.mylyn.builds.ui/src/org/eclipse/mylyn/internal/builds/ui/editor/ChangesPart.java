/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.internal.core.Change;
import org.eclipse.mylyn.builds.internal.core.ChangeArtifact;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.team.ui.actions.TaskFinder;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.ui.ScmUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class ChangesPart extends AbstractBuildEditorPart {

	static class ChangesContentProvider implements ITreeContentProvider {

		private static final Object[] NO_ELEMENTS = {};

		private IChangeSet input;

		@Override
		public void dispose() {
			input = null;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof IChangeSet) {
				input = (IChangeSet) newInput;
			} else {
				input = null;
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement == input) {
				return input.getChanges().toArray();
			} else if (inputElement instanceof String) {
				return new Object[] { inputElement };
			}
			return NO_ELEMENTS;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IChange) {
				return ((IChange) parentElement).getArtifacts().toArray();
			}
			return NO_ELEMENTS;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof EObject) {
				return ((EObject) element).eContainer();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof IChangeSet) {
				return !((IChangeSet) element).getChanges().isEmpty();
			}
			if (element instanceof IChange) {
				return !((IChange) element).getArtifacts().isEmpty();
			}
			return false;
		}

	}

	private TreeViewer viewer;

	private MenuManager menuManager;

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.builds.ui.editor.menu.Changes"; //$NON-NLS-1$

	public ChangesPart() {
		super(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setPartName(Messages.ChangesPart_changesPartName);
		setExpandVertically(true);
		span = 2;
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(1, false));

		IChangeSet changeSet = getInput(IBuild.class).getChangeSet();

//		if (changeSet == null || changeSet.getChanges().isEmpty()) {
//			createLabel(composite, toolkit, "No changes.");
//		}

		viewer = new TreeViewer(toolkit.createTree(composite, SWT.H_SCROLL));
		GridDataFactory.fillDefaults().hint(500, 100).grab(true, true).applyTo(viewer.getControl());
		viewer.setContentProvider(new ChangesContentProvider());
		viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new ChangesLabelProvider(), null, null));
		viewer.addSelectionChangedListener(event -> getPage().getSite().getSelectionProvider().setSelection(event.getSelection()));

		viewer.addOpenListener(event -> {
			Object selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (selection instanceof Change) {
				ChangesPart.this.open((Change) selection);
			}
			if (selection instanceof ChangeArtifact) {
				try {
					ChangesPart.this.open((ChangeArtifact) selection);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		});

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		getPage().getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, viewer, true);
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		if (changeSet == null || changeSet.getChanges().isEmpty()) {
			viewer.setInput(Messages.ChangesPart_noChanges);
		} else {
			viewer.setInput(changeSet);
		}

		toolkit.paintBordersFor(composite);
		return composite;
	}

	private void open(IChangeArtifact changeArtifact) throws CoreException {
		final IResource resource = ScmCore.findResource(changeArtifact.getFile());
		if (resource == null) {
			getMessageManager().addMessage(ChangesPart.class.getName(),
					Messages.ChangesPart_fileNotAvailable, null, IMessageProvider.WARNING);
			return;
		}
		final ScmConnector connector = ScmCore.getConnector(resource);
		if (connector == null) {
			getMessageManager().addMessage(ChangesPart.class.getName(),
					Messages.ChangesPart_noExtensionAvailbleForFile, null, IMessageProvider.WARNING);
			return;
		}

		final String prevRevision = changeArtifact.getPrevRevision();
		final String revision = changeArtifact.getRevision() != null
				? changeArtifact.getRevision()
				: ((IChange) ((ChangeArtifact) changeArtifact).eContainer()).getRevision();
		if (revision == null) {
			getMessageManager().addMessage(ChangesPart.class.getName(),
					Messages.ChangesPart_couldNotDetermineChangeRevisionsForTheSelectedFile, null, IMessageProvider.WARNING);
		}

		try {
			final AtomicReference<IFileRevision> left = new AtomicReference<>();
			final AtomicReference<IFileRevision> right = new AtomicReference<>();
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(monitor -> {
				try {
					ScmArtifact rightArtifact = connector.getArtifact(resource, revision);
					right.set(rightArtifact.getFileRevision(monitor));

					if (prevRevision != null) {
						ScmArtifact leftArtifact = connector.getArtifact(resource, prevRevision);
						left.set(leftArtifact.getFileRevision(monitor));
					}
					if (left.get() == null) {
						try {
							IFileRevision[] contributors = rightArtifact.getContributors(monitor);
							if (contributors != null && contributors.length > 0) {
								left.set(contributors[0]);
							}
						} catch (UnsupportedOperationException e) {
							// ignore
						}
					}
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			});
			if (right.get() != null) {
				getMessageManager().removeMessage(ChangesPart.class.getName());

				ScmUi.openCompareEditor(getPage().getSite().getPage(), left.get(), right.get());
			} else {
				getMessageManager().addMessage(ChangesPart.class.getName(),
						Messages.ChangesPart_couldNotDetermineChangeRevisionsForTheSelectedFile, null, IMessageProvider.WARNING);
			}
		} catch (InvocationTargetException e) {
			StatusManager.getManager()
					.handle(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, Messages.ChangesPart_unexpectedError, e),
							StatusManager.SHOW | StatusManager.LOG);
		} catch (InterruptedException e) {
			// ignore
		}

	}

	private void open(IChange selection) {
		TaskReference reference = new TaskReference();
		reference.setText(selection.getMessage());
		TaskFinder finder = new TaskFinder(reference);
		finder.open();
	}

}
