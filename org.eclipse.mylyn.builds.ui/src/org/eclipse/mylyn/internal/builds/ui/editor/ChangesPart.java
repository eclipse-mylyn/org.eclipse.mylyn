/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.internal.core.Change;
import org.eclipse.mylyn.builds.internal.core.ChangeArtifact;
import org.eclipse.mylyn.internal.team.ui.actions.TaskFinder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class ChangesPart extends AbstractBuildEditorPart {

	static class ChangesContentProvider implements ITreeContentProvider {

		private static final Object[] NO_ELEMENTS = new Object[0];

		private IChangeSet input;

		public void dispose() {
			input = null;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof IChangeSet) {
				input = (IChangeSet) newInput;
			} else {
				input = null;
			}
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement == input) {
				return input.getChanges().toArray();
			} else if (inputElement instanceof String) {
				return new Object[] { inputElement };
			}
			return NO_ELEMENTS;
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IChange) {
				return ((IChange) parentElement).getArtifacts().toArray();
			}
			return NO_ELEMENTS;
		}

		public Object getParent(Object element) {
			if (element instanceof EObject) {
				return ((EObject) element).eContainer();
			}
			return null;
		}

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

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.builds.ui.editor.menu.changes"; //$NON-NLS-1$

	public ChangesPart() {
		super(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setPartName("Changes");
		this.span = 2;
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
		GridDataFactory.fillDefaults().hint(500, 100).grab(true, false).applyTo(viewer.getControl());
		viewer.setContentProvider(new ChangesContentProvider());
		viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new ChangesLabelProvider(), PlatformUI
				.getWorkbench().getDecoratorManager().getLabelDecorator(), null));
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getPage().getSite().getSelectionProvider().setSelection(event.getSelection());
			}
		});

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				Object selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (selection instanceof Change) {
					ChangesPart.this.open((Change) selection);
				}
				if (selection instanceof ChangeArtifact) {
					ChangesPart.this.open((ChangeArtifact) selection);
				}
			}

		});

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		getPage().getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, viewer, true);
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		if (changeSet == null || changeSet.getChanges().isEmpty()) {
			viewer.setInput("No changes.");
		} else {
			viewer.setInput(changeSet);
		}

		toolkit.paintBordersFor(composite);
		return composite;
	}

	private void open(ChangeArtifact selection) {
		// ignore

	}

	private void open(Change selection) {
		TaskReference reference = new TaskReference();
		reference.setText(selection.getMessage());
		TaskFinder finder = new TaskFinder(reference);
		finder.open();
	}

}
