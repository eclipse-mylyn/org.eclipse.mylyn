/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.context.ui.editors;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.context.ui.actions.RemoveFromContextAction;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.INavigatorContentExtension;

/**
 * 
 * @author Mik Kersten
 */
public class ContextEditorFormPage extends FormPage {

	private static final String LABEL = "Task Context Editor";

	private static final String ID_VIEWER = "org.eclipse.mylar.context.ui.navigator.context";

	private ScrolledForm form;

	private FormToolkit toolkit;
	
	private CommonViewer commonViewer;
	
	private RemoveFromContextAction removeFromContextAction;

	private ScalableInterestFilter interestFilter = new ScalableInterestFilter();
		
	private Scale doiScale;
	
	private IMylarContextListener CONTEXT_LISTENER = new IMylarContextListener() {

		private void refresh() {
			if (!commonViewer.getTree().isDisposed()) {
				commonViewer.refresh();
				commonViewer.expandAll();
			}
		}
		
		public void contextActivated(IMylarContext context) {
			refresh();
		}

		public void contextDeactivated(IMylarContext context) {
			refresh();
		}

		public void elementDeleted(IMylarElement element) {
			refresh();
		}

		public void interestChanged(List<IMylarElement> elements) {
			refresh();
		}

		public void landmarkAdded(IMylarElement element) {
			refresh();
		}

		public void landmarkRemoved(IMylarElement element) {
			refresh();
		}

		public void presentationSettingsChanged(UpdateKind kind) {
			refresh();
		}

		public void presentationSettingsChanging(UpdateKind kind) {
			refresh();
		}

		public void relationsChanged(IMylarElement element) {
			refresh();
		}
		
	};

	public ContextEditorFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ContextCorePlugin.getContextManager().addListener(CONTEXT_LISTENER);
//		task = ((ContextEditorInput)getEditorInput()).getTask();

		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		
		form.setImage(TaskListImages.getImage(TaskListImages.CONTEXT_ATTACH));
		form.setText(LABEL);
		toolkit.decorateFormHeading(form.getForm());

		form.getBody().setLayout(new GridLayout(2, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createActionsSection(form.getBody());
		createDisplaySection(form.getBody());
		
		form.reflow(true);
	}
	
	@Override
	public void dispose() {
		super.dispose();
//		ContextUiPlugin.getDefault().getViewerManager().removeManagedViewer(commonViewer, this);
		ContextCorePlugin.getContextManager().removeListener(CONTEXT_LISTENER);
	}

	private void createActionsSection(Composite composite) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText("Actions");
		
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		section.setLayout(new FillLayout());
		sectionClient.setLayout(new GridLayout());
		sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

		toolkit.createLabel(sectionClient, "Interest Filter Threshold");
				
		doiScale = new Scale(sectionClient, SWT.NONE);
		GridData scaleGridData = new GridData(GridData.FILL_HORIZONTAL);
		scaleGridData.heightHint = 20;	
		doiScale.setLayoutData(scaleGridData);
		doiScale.setPageIncrement(20);
		doiScale.setSelection(0);
		doiScale.setMinimum(-100);
		doiScale.setMaximum(+100);
		doiScale.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				interestFilter.setThreshold(doiScale.getSelection());
				commonViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about default selection
			}
		});
		doiScale.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				// don't care about double click
			}

			public void mouseDown(MouseEvent e) {
				// don't care about mouse down
			}

			public void mouseUp(MouseEvent e) {
				interestFilter.setThreshold(doiScale.getSelection());
				commonViewer.refresh();
			}
		});
		
		section.setExpanded(true);
		
	}

	private float scaleDoiSelection(int selection) {
		int value = selection;
		int scaledValue = (-1) * (value - 6);
		if (scaledValue < 0)
			scaledValue = 0;
		return scaledValue;
	}
	
	private void createDisplaySection(Composite composite) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText("Elements");
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
				
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new FillLayout());
		
		createViewer(sectionClient);
		
		section.setExpanded(true);
	}
	
	public void createViewer(Composite aParent) {

		commonViewer = createCommonViewer(aParent);	
		commonViewer.addFilter(interestFilter);

		try {
			commonViewer.getControl().setRedraw(false);
			
			INavigatorContentExtension javaContent = commonViewer.getNavigatorContentService().getContentExtensionById("org.eclipse.jdt.java.ui.javaContent");
			ITreeContentProvider treeContentProvider = javaContent.getContentProvider();

			// TODO: find a sane way of doing this, should be:
//			if (javaContent.getContentProvider() != null) {
//				JavaNavigatorContentProvider java = (JavaNavigatorContentProvider)javaContent.getContentProvider();
//				java.setIsFlatLayout(true);
//			}
			try {
				Class<?> clazz = treeContentProvider.getClass().getSuperclass();
				Method method = clazz.getDeclaredMethod("setIsFlatLayout", new Class[] { boolean.class });
				method.invoke(treeContentProvider, new Object[] { true } );
			} catch (Exception e) {
				MylarStatusHandler.log(e, "couldn't set flat layout on Java content provider");
			}
			
			commonViewer.setInput(getSite().getPage().getInput()); 
			getSite().setSelectionProvider(commonViewer);
//			ContextUiPlugin.getDefault().getViewerManager().addManagedViewer(commonViewer, this);
			makeContextMenuActions();
			hookContextMenu();
			commonViewer.expandAll();
		} finally { 
			commonViewer.getControl().setRedraw(true);
		}
	}
	
	protected CommonViewer createCommonViewer(Composite parent) {
		CommonViewer viewer = new CommonViewer(ID_VIEWER, parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		return viewer;
	}
	
	private void makeContextMenuActions() {
		removeFromContextAction = new RemoveFromContextAction(commonViewer, interestFilter);
		commonViewer.addSelectionChangedListener(removeFromContextAction);
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuManager.createContextMenu(commonViewer.getControl());
		commonViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, commonViewer);
	}
	
	protected void fillContextMenu(IMenuManager manager) {
		manager.add(removeFromContextAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public ISelection getSelection() {
		if (getSite() != null && getSite().getSelectionProvider() != null) {
			return getSite().getSelectionProvider().getSelection();
		} else {
			return StructuredSelection.EMPTY;
		}
	}
}
