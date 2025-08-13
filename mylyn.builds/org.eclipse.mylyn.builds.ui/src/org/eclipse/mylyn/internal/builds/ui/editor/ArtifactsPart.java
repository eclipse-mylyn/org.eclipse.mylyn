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
 *     GitHub - fixes for bug 352916
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 * @author Kevin Sawicki
 */
public class ArtifactsPart extends AbstractBuildEditorPart {

	static class ArtifactFolder {

		final Map<String, ArtifactFolder> folders = new HashMap<>();

		final List<IArtifact> artifacts = new ArrayList<>();

		final String name;

		public ArtifactFolder(String name) {
			this.name = name;
		}

		protected Object[] getChildren() {
			List<Object> all = new ArrayList<>(artifacts.size() + folders.size());
			all.addAll(artifacts);
			all.addAll(folders.values());
			return all.toArray();
		}

		public boolean hasChildren() {
			return artifacts.size() + folders.size() > 0;
		}

		public String getName() {
			return name;
		}

		ArtifactFolder add(String path, IArtifact artifact) {
			int slash = path.indexOf('/');
			if (slash > 0 && slash + 1 < path.length()) {
				String name = path.substring(0, slash);
				ArtifactFolder folder = folders.get(name);
				if (folder == null) {
					folder = new ArtifactFolder(name);
					folders.put(name, folder);
				}
				path = path.substring(slash + 1);
				folder.add(path, artifact);
			} else {
				artifacts.add(artifact);
			}
			return this;
		}

		public ArtifactFolder add(IArtifact artifact) {
			return add(artifact.getRelativePath(), artifact);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	static class ArtifactsContentProvider implements ITreeContentProvider {

		private static final Object[] NO_ELEMENTS = {};

		private ArtifactFolder input;

		@Override
		public void dispose() {
			input = null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ArtifactFolder) {
				return ((ArtifactFolder) parentElement).getChildren();
			}
			return NO_ELEMENTS;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement == input) {
				return input.getChildren();
			}
			if (inputElement instanceof String) {
				return new Object[] { inputElement };
			}
			return NO_ELEMENTS;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof ArtifactFolder) {
				return ((ArtifactFolder) element).hasChildren();
			}
			return false;
		}

		@Override
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
		setPartName(Messages.ArtifactsPart_artifactsLabel);
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(1, false));

		viewer = new TreeViewer(toolkit.createTree(composite, SWT.NONE));
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		GridDataFactory.fillDefaults().hint(300, 100).grab(true, true).applyTo(viewer.getControl());
		viewer.setContentProvider(new ArtifactsContentProvider());
		viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new ArtifactsLabelProvider(), null, null));
		viewer.addSelectionChangedListener(event -> getPage().getSite().getSelectionProvider().setSelection(event.getSelection()));
		viewer.addOpenListener(event -> {
			Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (item instanceof IArtifact artifact) {
				if (artifact.getUrl() != null) {
					BrowserUtil.openUrl(artifact.getUrl(), BrowserUtil.NO_RICH_EDITOR);
				}
			}
		});
		viewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				String name1 = null;
				String name2 = null;
				if (e1 instanceof IArtifact) {
					name1 = ((IArtifact) e1).getName();
				} else if (e1 instanceof ArtifactFolder) {
					name1 = ((ArtifactFolder) e1).getName();
				}
				if (e2 instanceof IArtifact) {
					name2 = ((IArtifact) e2).getName();
				} else if (e1 instanceof ArtifactFolder) {
					name2 = ((ArtifactFolder) e2).getName();
				}
				if (name1 != null && name2 != null) {
					return name1.compareToIgnoreCase(name2);
				}
				return super.compare(viewer, e1, e2);
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
			viewer.setInput(Messages.ArtifactsPart_noArtifacts);
		}

		final int numArtifacts = getInput(IBuild.class).getArtifacts().size();
		getSection().setText(MessageFormat.format(Messages.ArtifactsPart_artifacts, numArtifacts));

		toolkit.paintBordersFor(composite);
		return composite;
	}

	private ArtifactFolder getRootFolder() {
		ArtifactFolder root = new ArtifactFolder(Messages.ArtifactsPart_rootFolder);
		for (IArtifact artifact : getInput(IBuild.class).getArtifacts()) {
			root.add(artifact);
		}
		return root;
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBarManager) {
		super.fillToolBar(toolBarManager);

		toolBarManager.add(new Action(Messages.ArtifactsPart_collapseAll, CommonImages.COLLAPSE_ALL) {

			@Override
			public void run() {
				viewer.collapseAll();
			}
		});
	}
}
