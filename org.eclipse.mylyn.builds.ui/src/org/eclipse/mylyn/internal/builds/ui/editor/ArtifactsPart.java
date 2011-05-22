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

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class ArtifactsPart extends AbstractBuildEditorPart {

	static class ArtifactFolder {

		List<IArtifact> artifacts = new ArrayList<IArtifact>();

		List<ArtifactFolder> folders = new ArrayList<ArtifactsPart.ArtifactFolder>();

		String name;

		public ArtifactFolder(String name) {
			this.name = name;
		}

		protected Object[] getChildren() {
			List<Object> all = new ArrayList<Object>(artifacts.size() + folders.size());
			all.addAll(artifacts);
			all.addAll(folders);
			return all.toArray();
		}

		public boolean hasChildren() {
			return artifacts.size() + folders.size() > 0;
		}

		public String getName() {
			// ignore
			return null;
		}

	}

	static class ArtifactsContentProvider implements ITreeContentProvider {

		private static final Object[] NO_ELEMENTS = new Object[0];

		private ArtifactFolder input;

		public void dispose() {
			input = null;
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ArtifactFolder) {
				return ((ArtifactFolder) parentElement).getChildren();
			}
			return NO_ELEMENTS;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement == input) {
				return input.getChildren();
			}
			if (inputElement instanceof String) {
				return new Object[] { inputElement };
			}
			return NO_ELEMENTS;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof ArtifactFolder) {
				return ((ArtifactFolder) element).hasChildren();
			}
			return false;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof ArtifactFolder) {
				input = (ArtifactFolder) newInput;
			} else {
				input = null;
			}
		}

	}

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.builds.ui.editor.menu.Artifacts"; //$NON-NLS-1$

	private MenuManager menuManager;

	private TreeViewer viewer;

	public ArtifactsPart() {
		super(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setPartName("Artifacts");
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(1, false));

		viewer = new TreeViewer(toolkit.createTree(composite, SWT.NONE));
		GridDataFactory.fillDefaults().hint(300, 100).grab(true, true).applyTo(viewer.getControl());
		viewer.setContentProvider(new ArtifactsContentProvider());
		viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new ArtifactsLabelProvider(), null, null));
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getPage().getSite().getSelectionProvider().setSelection(event.getSelection());
			}
		});
		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (item instanceof IArtifact) {
					IArtifact artifact = (IArtifact) item;
					if (artifact.getUrl() != null) {
						WorkbenchUtil.openUrl(artifact.getUrl());
					}
				}
			}
		});

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		getPage().getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, viewer, true);
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		ArtifactFolder root = getRootFolder();
		if (root.hasChildren()) {
			viewer.setInput(root);
		} else {
			viewer.setInput("No artifacts.");
		}

		toolkit.paintBordersFor(composite);
		return composite;
	}

	private ArtifactFolder getRootFolder() {
		ArtifactFolder root = new ArtifactFolder("Root");
		root.artifacts.addAll(getInput(IBuild.class).getArtifacts());
		return root;
	}

}
