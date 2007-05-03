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
import org.eclipse.mylar.internal.context.ui.ContextUiImages;
import org.eclipse.mylar.internal.context.ui.actions.ContextAttachAction;
import org.eclipse.mylar.internal.context.ui.actions.ContextCopyAction;
import org.eclipse.mylar.internal.context.ui.actions.ContextRetrieveAction;
import org.eclipse.mylar.internal.context.ui.actions.RemoveFromContextAction;
import org.eclipse.mylar.internal.context.ui.views.ContextNodeOpenListener;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.INavigatorContentExtension;

/**
 * 
 * @author Mik Kersten
 */
public class ContextEditorFormPage extends FormPage {

	public static final String ID_VIEWER = "org.eclipse.mylar.context.ui.navigator.context";

	private ScrolledForm form;

	private FormToolkit toolkit;

	private CommonViewer commonViewer;

	private RemoveFromContextAction removeFromContextAction;

	private ScalableInterestFilter interestFilter = new ScalableInterestFilter();

	private Scale doiScale;

	private ITask task;

	private IMylarContextListener CONTEXT_LISTENER = new IMylarContextListener() {

		private void refresh() {
			if (commonViewer != null && !commonViewer.getTree().isDisposed()) {
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
		task = ((TaskEditorInput) getEditorInput()).getTask();

		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();

		//form.setImage(TaskListImages.getImage(TaskListImages.TASK_ACTIVE_CENTERED));
		//form.setText(LABEL);
		//toolkit.decorateFormHeading(form.getForm());

		form.getBody().setLayout(new GridLayout(2, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));

		createActionsSection(form.getBody());
		createDisplaySection(form.getBody());

		form.reflow(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		// ContextUiPlugin.getDefault().getViewerManager().removeManagedViewer(commonViewer,
		// this);
		ContextCorePlugin.getContextManager().removeListener(CONTEXT_LISTENER);
	}

	private void createActionsSection(Composite composite) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText("Actions");

		section.setLayout(new GridLayout());
		GridData sectionGridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		sectionGridData.widthHint = 80;
		section.setLayoutData(sectionGridData);

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		sectionClient.setLayoutData(new GridData());

		Label label = toolkit.createLabel(sectionClient, "");
		label.setImage(TasksUiImages.getImage(TasksUiImages.FILTER));

		doiScale = new Scale(sectionClient, SWT.FLAT);
		GridData scaleGridData = new GridData(GridData.FILL_HORIZONTAL);
		scaleGridData.heightHint = 36;
		scaleGridData.widthHint = 80;
		doiScale.setLayoutData(scaleGridData);
		doiScale.setPageIncrement(1);
		doiScale.setSelection(0);
		doiScale.setMinimum(0);
		doiScale.setMaximum(10);
		doiScale.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setFilterThreshold();
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
				setFilterThreshold();
			}
		});

		if (!task.equals(TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask())) {
			doiScale.setEnabled(false);
		}
		
		Label attachImage = toolkit.createLabel(sectionClient, "");
		attachImage.setImage(TasksUiImages.getImage(ContextUiImages.CONTEXT_ATTACH));
		attachImage.setEnabled(task instanceof AbstractRepositoryTask);
		Hyperlink attachHyperlink = toolkit.createHyperlink(sectionClient, "Attach context...", SWT.NONE);
		attachHyperlink.setEnabled(task instanceof AbstractRepositoryTask);
		attachHyperlink.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
				new ContextAttachAction().run((AbstractRepositoryTask) task);
			}

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}

			public void mouseDown(MouseEvent e) {
				// ignore
			}
		});

		Label retrieveImage = toolkit.createLabel(sectionClient, "");
		retrieveImage.setImage(TasksUiImages.getImage(ContextUiImages.CONTEXT_RETRIEVE));
		retrieveImage.setEnabled(task instanceof AbstractRepositoryTask);
		Hyperlink retrieveHyperlink = toolkit.createHyperlink(sectionClient, "Retrieve Context...", SWT.NONE);
		retrieveHyperlink.setEnabled(task instanceof AbstractRepositoryTask);
		retrieveHyperlink.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
				new ContextRetrieveAction().run((AbstractRepositoryTask) task);
			}

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}

			public void mouseDown(MouseEvent e) {
				// ignore
			}
		});

		Label copyImage = toolkit.createLabel(sectionClient, "");
		copyImage.setImage(TasksUiImages.getImage(ContextUiImages.CONTEXT_COPY));
		Hyperlink copyHyperlink = toolkit.createHyperlink(sectionClient, "Copy Context to...", SWT.NONE);
		copyHyperlink.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
				new ContextCopyAction().run(task);
			}

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}

			public void mouseDown(MouseEvent e) {
				// ignore
			}
		});

		section.setExpanded(true);
	}

	protected void setFilterThreshold() {
		int setting = doiScale.getSelection();
		int threshold = setting * setting * setting;

		interestFilter.setThreshold(threshold);
		commonViewer.refresh();
		commonViewer.expandAll();
	}

	private void createDisplaySection(Composite composite) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText("Elements");
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new FillLayout());

		if (task.equals(TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask())) {
			createViewer(sectionClient);
		} else {
			Hyperlink retrieveHyperlink = toolkit.createHyperlink(sectionClient, "Activate task to edit context",
					SWT.NONE);
			retrieveHyperlink.addMouseListener(new MouseListener() {

				public void mouseUp(MouseEvent e) {
					new TaskActivateAction().run(task);
				}

				public void mouseDoubleClick(MouseEvent e) {
					// ignore
				}

				public void mouseDown(MouseEvent e) {
					// ignore
				}
			});
		}

		section.setExpanded(true);
	}

	private void createViewer(Composite aParent) {

		commonViewer = createCommonViewer(aParent);
		commonViewer.addFilter(interestFilter);
		
		commonViewer.addOpenListener(new ContextNodeOpenListener(commonViewer)); 
		
		try {
			commonViewer.getControl().setRedraw(false);

			forceFlatLayoutOfJavaContent(commonViewer);

			commonViewer.setInput(getSite().getPage().getInput());
			getSite().setSelectionProvider(commonViewer);
			// ContextUiPlugin.getDefault().getViewerManager().addManagedViewer(commonViewer,
			// this);
			makeContextMenuActions();
			hookContextMenu();
			commonViewer.expandAll();
		} finally {
			commonViewer.getControl().setRedraw(true);
		}
	}

	public static void forceFlatLayoutOfJavaContent(CommonViewer commonViewer) {
		INavigatorContentExtension javaContent = commonViewer.getNavigatorContentService().getContentExtensionById(
				"org.eclipse.jdt.java.ui.javaContent");
		if (javaContent != null) {
			ITreeContentProvider treeContentProvider = javaContent.getContentProvider();
			// TODO: find a sane way of doing this, should be:
			// if (javaContent.getContentProvider() != null) {
			// JavaNavigatorContentProvider java =
			// (JavaNavigatorContentProvider)javaContent.getContentProvider();
			// java.setIsFlatLayout(true);
			// }
			try {
				Class<?> clazz = treeContentProvider.getClass().getSuperclass();
				Method method = clazz.getDeclaredMethod("setIsFlatLayout", new Class[] { boolean.class });
				method.invoke(treeContentProvider, new Object[] { true });
			} catch (Exception e) {
				MylarStatusHandler.log(e, "couldn't set flat layout on Java content provider");
			}
		}
	}

	protected CommonViewer createCommonViewer(Composite parent) {
		CommonViewer viewer = new CommonViewer(ID_VIEWER, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
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
