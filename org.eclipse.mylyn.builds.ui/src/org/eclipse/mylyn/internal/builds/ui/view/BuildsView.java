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

package org.eclipse.mylyn.internal.builds.ui.view;

import java.net.URISyntaxException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ViewPluginAction;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Steffen Pingel
 */
public class BuildsView extends ViewPart {

	private TreeViewer buildTree;

	private BuildContentProvider contentProvider;

	public BuildsView() {
		// ignore
	}

	@Override
	public void createPartControl(Composite parent) {
		buildTree = new TreeViewer(parent, SWT.FULL_SELECTION);
		Tree tree = buildTree.getTree();
		tree.setHeaderVisible(true);

		TreeViewerColumn buildViewerColumn = new TreeViewerColumn(buildTree, SWT.LEFT);
		buildViewerColumn.setLabelProvider(new DecoratingStyledCellLabelProvider(new BuildLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), null));
		TreeColumn buildColumn = buildViewerColumn.getColumn();
		buildColumn.setText("Builds");
		buildColumn.setWidth(220);

		TreeViewerColumn summaryViewerColumn = new TreeViewerColumn(buildTree, SWT.LEFT);
		summaryViewerColumn.setLabelProvider(new BuildSummaryLabelProvider());
		TreeColumn summaryColumn = summaryViewerColumn.getColumn();
		summaryColumn.setText("Summary");
		summaryColumn.setWidth(220);

		TreeViewerColumn statusViewerColumn = new TreeViewerColumn(buildTree, SWT.LEFT);
		statusViewerColumn.setLabelProvider(new BuildStatusLabelProvider());
		TreeColumn statusColumn = statusViewerColumn.getColumn();
		statusColumn.setText("Status");
		statusColumn.setWidth(50);

		contentProvider = new BuildContentProvider();
		buildTree.setContentProvider(contentProvider);

		MenuManager menuManager = new MenuManager();

		GroupMarker marker = new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS);
		menuManager.add(marker);
		Menu contextMenu = menuManager.createContextMenu(parent);

		buildTree.getTree().setMenu(contextMenu);
		getSite().registerContextMenu(menuManager, buildTree);

		fillToolbar();

		buildTree.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				OpenInBrowserAction action = new OpenInBrowserAction();
				action.selectionChanged((IStructuredSelection) event.getSelection());
				action.run();
			}
		});

		buildTree.setInput(getInitialInput());
		buildTree.expandAll();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	private void fillToolbar() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(new NewBuildServerMenuAction());

		RefreshAction refresh = new RefreshAction() {
			@Override
			public void run() {
				getViewer().setInput(getInitialInput());
				getViewer().expandAll();
			};
		};
		toolBarManager.add(refresh);

		toolBarManager.add(new Separator());

		OpenInBrowserAction openInBrowserAction = new OpenInBrowserAction();
		buildTree.addSelectionChangedListener(openInBrowserAction);
		toolBarManager.add(openInBrowserAction);
	}

	protected Object getInitialInput() {
		//URI.createPlatformResourceURI("/Project/builds.xml", false);
		try {
			URI uri = URI.createURI(BuildsUiPlugin.getDefault()
					.getBundle()
					.getResource("sample-data.xml")
					.toURI()
					.toString());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource ecoreResource = resourceSet.getResource(uri, true);
			return ecoreResource.getContents().get(0);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	TreeViewer getViewer() {
		return buildTree;
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		//Ensures that the RefreshBuildsAutomatically Action has the correct checked state
		getViewSite().getActionBars().getMenuManager().addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IContributionItem[] items = manager.getItems();
				for (IContributionItem item : items) {
					if (item instanceof ActionContributionItem) {
						ActionContributionItem actionItem = (ActionContributionItem) item;
						if (actionItem.getAction() instanceof ViewPluginAction) {
							((ViewPluginAction) actionItem.getAction()).selectionChanged(new StructuredSelection());
						}
					}
				}
			}
		});
	}

	@Override
	public void setFocus() {
		getViewer().getControl().setFocus();
	}

}
