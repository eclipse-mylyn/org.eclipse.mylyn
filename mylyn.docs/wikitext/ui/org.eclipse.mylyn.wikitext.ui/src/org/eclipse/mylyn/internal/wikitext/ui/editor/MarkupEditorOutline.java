/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Marc-Andre Laperle (Ericsson) - Add collapse all button (Bug 424558)
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.dnd.DndConfigurationStrategy;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * @author David Green
 */
public class MarkupEditorOutline extends ContentOutlinePage implements IShowInSource, IShowInTarget {

	private final MarkupEditor editor;

	private boolean disableReveal;

	private DndConfigurationStrategy dndConfigurationStrategy;

	public MarkupEditorOutline(MarkupEditor editor) {
		this.editor = editor;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setUseHashlookup(true);
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		viewer.setContentProvider(new BaseWorkbenchContentProvider());
		viewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		viewer.setInput(editor.getOutlineModel());

		viewer.addOpenListener(event -> revealInEditor(event.getSelection(), true));
		viewer.addPostSelectionChangedListener(event -> revealInEditor(event.getSelection(), false));
		viewer.expandAll();

		new ToolTip(viewer.getControl(), ToolTip.RECREATE, false) {
			@Override
			protected Composite createToolTipContentArea(Event event, Composite parent) {

				Composite comp = new Composite(parent, SWT.NONE);
				comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

				GridLayout gl = new GridLayout(1, false);
				gl.marginBottom = 2;
				gl.marginTop = 2;
				gl.marginHeight = 0;
				gl.marginWidth = 0;
				gl.marginLeft = 2;
				gl.marginRight = 2;
				gl.verticalSpacing = 1;
				comp.setLayout(gl);

				Object tipItem = getToolTipItem(new Point(event.x, event.y));
				if (tipItem instanceof OutlineItem outlineItem) {
					Label label = new Label(comp, SWT.WRAP);
					label.setBackground(comp.getBackground());
					label.setText(outlineItem.getTooltip());
				}

				return comp;
			}

			@Override
			protected boolean shouldCreateToolTip(Event event) {
				final Object eventItem = getToolTipItem(new Point(event.x, event.y));
				boolean shouldCreate = eventItem instanceof OutlineItem && super.shouldCreateToolTip(event);
				if (!shouldCreate) {
					hide();
				}
				return shouldCreate;
			}

			protected Object getToolTipItem(Point point) {
				TreeItem item = ((Tree) getTreeViewer().getControl()).getItem(point);
				if (item != null) {
					return item.getData();
				}
				return null;
			}
		};

		IPageSite site = getSite();
		site.setSelectionProvider(viewer);
		configureActionBars(site);

		MenuManager manager = new MenuManager("#PopUp"); //$NON-NLS-1$
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(this::contextMenuAboutToShow);
		viewer.getTree().setMenu(manager.createContextMenu(viewer.getTree()));

		site.registerContextMenu(MarkupEditor.ID + ".outlineContextMenu", manager, viewer); //$NON-NLS-1$

		configureDnd();
		configureActions();
	}

	/**
	 * Collapse all nodes.
	 */
	private static class CollapseAllAction extends Action {

		private final TreeViewer viewer;

		public CollapseAllAction(TreeViewer viewer) {
			super(Messages.MarkupEditor_collapseAllAction_label);
			setDescription(Messages.MarkupEditor_collapseAllAction_description);
			setToolTipText(Messages.MarkupEditor_collapseAllAction_tooltip);
			setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(WikiTextUiPlugin.getDefault().getPluginId(),
					"icons/collapseall.svg")); //$NON-NLS-1$
			this.viewer = viewer;
		}

		@Override
		public void run() {
			try {
				viewer.getControl().setRedraw(false);
				viewer.collapseAll();
			} finally {
				viewer.getControl().setRedraw(true);
			}
		}
	}

	private void configureActionBars(IPageSite site) {
		IActionBars actionBars = site.getActionBars();
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		IHandlerService handlerService = site.getService(IHandlerService.class);

		CollapseAllAction collapseAllAction = new CollapseAllAction(getTreeViewer());
		toolBarManager.add(collapseAllAction);
		handlerService.activateHandler(CollapseAllHandler.COMMAND_ID, new ActionHandler(collapseAllAction));

		actionBars.updateActionBars();
	}

	@Override
	public void dispose() {
		if (dndConfigurationStrategy != null) {
			dndConfigurationStrategy.dispose();
			dndConfigurationStrategy = null;
		}
		super.dispose();
	}

	private void configureDnd() {
		dndConfigurationStrategy = new DndConfigurationStrategy();
		dndConfigurationStrategy.configure(editor, getControl(), getTreeViewer());
	}

	private void configureActions() {
		registerAction(ITextEditorActionConstants.UNDO);
		registerAction(ITextEditorActionConstants.REDO);
		registerAction(ITextEditorActionConstants.REVERT);
		registerAction(ITextEditorActionConstants.SAVE);
		registerAction(ITextEditorActionConstants.FIND);
		registerAction(ITextEditorActionConstants.PRINT);
	}

	private void registerAction(String actionId) {
		IAction action = editor.getAction(actionId);
		if (action != null) {
			getSite().getActionBars().setGlobalActionHandler(actionId, action);
		}
	}

	protected void contextMenuAboutToShow(IMenuManager menuManager) {

		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_UNDO));
		menuManager.add(new GroupMarker(ITextEditorActionConstants.GROUP_SAVE));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_COPY));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_PRINT));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_EDIT));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_FIND));
		menuManager.add(new Separator(IWorkbenchActionConstants.GROUP_ADD));
		menuManager.add(new Separator(IWorkbenchActionConstants.GROUP_SHOW_IN));
		menuManager.add(new Separator(IWorkbenchActionConstants.GROUP_REORGANIZE));
		menuManager.add(new Separator(ITextEditorActionConstants.GROUP_REST));
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		addAction(menuManager, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.UNDO);
		addAction(menuManager, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.REDO);
		addAction(menuManager, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.REVERT);
		addAction(menuManager, ITextEditorActionConstants.GROUP_SAVE, ITextEditorActionConstants.SAVE);
		addAction(menuManager, ITextEditorActionConstants.GROUP_FIND, ITextEditorActionConstants.FIND);
		addAction(menuManager, ITextEditorActionConstants.GROUP_PRINT, ITextEditorActionConstants.PRINT);

	}

	protected final void addAction(IMenuManager menu, String group, String actionId) {
		IAction action = editor.getAction(actionId);
		if (action != null) {
			if (action instanceof IUpdate updateAction) {
				updateAction.update();
			}

			IMenuManager subMenu = menu.findMenuUsingPath(group);
			if (subMenu != null) {
				subMenu.add(action);
			} else {
				menu.appendToGroup(group, action);
			}
		}
	}

	private void revealInEditor(ISelection selection, boolean open) {
		if (disableReveal) {
			return;
		}
		if (selection instanceof IStructuredSelection structuredSelection) {
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof OutlineItem item) {
				editor.selectAndReveal(item);
			}
		}
	}

	public void refresh() {
		getTreeViewer().getTree().setRedraw(false);
		try {
			getTreeViewer().refresh();
			getTreeViewer().expandAll();
		} finally {
			getTreeViewer().getTree().setRedraw(true);
		}
	}

	@Override
	public void setSelection(ISelection selection) {
		disableReveal = true;
		try {
			super.setSelection(selection);
		} finally {
			disableReveal = false;
		}
	}

	@Override
	public ShowInContext getShowInContext() {
		return editor.getShowInContext();
	}

	@Override
	public boolean show(ShowInContext context) {
		return editor.show(context);
	}
}
