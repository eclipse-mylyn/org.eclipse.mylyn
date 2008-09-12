/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.editors;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.actions.ContextAttachAction;
import org.eclipse.mylyn.internal.context.ui.actions.ContextClearAction;
import org.eclipse.mylyn.internal.context.ui.actions.ContextCopyAction;
import org.eclipse.mylyn.internal.context.ui.actions.ContextRetrieveAction;
import org.eclipse.mylyn.internal.context.ui.views.ContextNodeOpenListener;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.DelayedRefreshJob;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
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
 * @author Mik Kersten
 */
public class ContextEditorFormPage extends FormPage {

	private static final int SCALE_STEPS = 14;

	public static final String ID_VIEWER = "org.eclipse.mylyn.context.ui.navigator.context";

	private ScrolledForm form;

	private FormToolkit toolkit;

	private CommonViewer commonViewer;

	private final ScalableInterestFilter interestFilter = new ScalableInterestFilter();

	private Scale doiScale;

	private ITask task;

	private class ContextEditorDelayedRefreshJob extends DelayedRefreshJob {

		public ContextEditorDelayedRefreshJob(StructuredViewer treeViewer, String name) {
			super(treeViewer, name);
		}

		@Override
		protected void doRefresh(Object[] items) {
			if (commonViewer != null && !commonViewer.getTree().isDisposed()) {
				commonViewer.refresh();
				if (items != null) {
					for (Object item : items) {
						updateExpansionState(item);
					}
				} else {
					updateExpansionState(null);
				}
			}
		}

		protected void updateExpansionState(Object item) {
			if (commonViewer != null && !commonViewer.getTree().isDisposed()) {
				try {
					commonViewer.getTree().setRedraw(false);
					if (/*!mouseDown && */item == null) {
						commonViewer.expandAll();
					} else if (item != null && item instanceof IInteractionElement) {
						IInteractionElement node = (IInteractionElement) item;
						AbstractContextStructureBridge structureBridge = ContextCorePlugin.getDefault()
								.getStructureBridge(node.getContentType());
						Object objectToRefresh = structureBridge.getObjectForHandle(node.getHandleIdentifier());
						if (objectToRefresh != null) {
							commonViewer.expandToLevel(objectToRefresh, AbstractTreeViewer.ALL_LEVELS);
						}
					}
				} finally {
					commonViewer.getTree().setRedraw(true);
				}
			}
		}

	}

	private final AbstractContextListener CONTEXT_LISTENER = new AbstractContextListener() {

		@Override
		public void contextActivated(IInteractionContext context) {
			refresh();
		}

		@Override
		public void contextDeactivated(IInteractionContext context) {
			refresh();
		}

		@Override
		public void contextCleared(IInteractionContext context) {
			refresh();
		}

		@Override
		public void elementsDeleted(List<IInteractionElement> element) {
			refresh(element);
		}

		@Override
		public void interestChanged(List<IInteractionElement> elements) {
			refresh(elements);
		}

		@Override
		public void landmarkAdded(IInteractionElement element) {
			refresh(Arrays.asList(new IInteractionElement[] { element }));
		}

		@Override
		public void landmarkRemoved(IInteractionElement element) {
			refresh(Arrays.asList(new IInteractionElement[] { element }));
		}
	};

	public ContextEditorFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ContextCore.getContextManager().addListener(CONTEXT_LISTENER);
		task = ((TaskEditorInput) getEditorInput()).getTask();

		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();

		//form.setImage(TaskListImages.getImage(TaskListImages.TASK_ACTIVE_CENTERED));
		//form.setText(LABEL);
		//toolkit.decorateFormHeading(form.getForm());

		form.getBody().setLayout(new GridLayout(2, false));

		createActionsSection(form.getBody());
		createDisplaySection(form.getBody());

		form.reflow(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		// ContextUiPlugin.getViewerManager().removeManagedViewer(commonViewer,
		// this);
		ContextCore.getContextManager().removeListener(CONTEXT_LISTENER);
	}

	private void createActionsSection(Composite composite) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
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
		label.setImage(CommonImages.getImage(CommonImages.FILTER));

		doiScale = new Scale(sectionClient, SWT.FLAT);
		GridData scaleGridData = new GridData(GridData.FILL_HORIZONTAL);
		scaleGridData.heightHint = 36;
		scaleGridData.widthHint = 80;
		doiScale.setLayoutData(scaleGridData);
		doiScale.setPageIncrement(1);
		doiScale.setMinimum(0);
		doiScale.setSelection(SCALE_STEPS / 2);
		doiScale.setMaximum(SCALE_STEPS);
		doiScale.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setFilterThreshold();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about default selection
			}
		});

		if (!task.equals(TasksUi.getTaskActivityManager().getActiveTask())) {
			doiScale.setEnabled(false);
		}

		Label attachImage = toolkit.createLabel(sectionClient, "");
		attachImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_ATTACH));
		attachImage.setEnabled(task != null);
		Hyperlink attachHyperlink = toolkit.createHyperlink(sectionClient, "Attach context...", SWT.NONE);
		attachHyperlink.setEnabled(task != null);
		attachHyperlink.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
				new ContextAttachAction().run(task);
			}

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}

			public void mouseDown(MouseEvent e) {
				// ignore
			}
		});

		Label retrieveImage = toolkit.createLabel(sectionClient, "");
		retrieveImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_RETRIEVE));
		retrieveImage.setEnabled(task != null);
		Hyperlink retrieveHyperlink = toolkit.createHyperlink(sectionClient, "Retrieve Context...", SWT.NONE);
		retrieveHyperlink.setEnabled(task != null);
		retrieveHyperlink.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
				new ContextRetrieveAction().run(task);
			}

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}

			public void mouseDown(MouseEvent e) {
				// ignore
			}
		});

		Label copyImage = toolkit.createLabel(sectionClient, "");
		copyImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_COPY));
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

		Label clearImage = toolkit.createLabel(sectionClient, "");
		clearImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_CLEAR));
		Hyperlink clearHyperlink = toolkit.createHyperlink(sectionClient, "Clear Context", SWT.NONE);
		clearHyperlink.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
				new ContextClearAction().run(task);
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

	private ContextEditorDelayedRefreshJob refreshJob;

	/**
	 * Scales logarithmically to a reasonable interest threshold range (e.g. -10000..10000).
	 */
	protected void setFilterThreshold() {
		double setting = doiScale.getSelection() - (SCALE_STEPS / 2);
		double threshold = Math.signum(setting) * Math.pow(Math.exp(Math.abs(setting)), 1.5);

		interestFilter.setThreshold(threshold);

		refresh();
	}

	private void refresh() {
		createRefreshJob();
		if (refreshJob != null) {
			refreshJob.refresh();
		}
	}

	private synchronized void createRefreshJob() {
		if (commonViewer == null) {
			return;
		}
		if (refreshJob == null) {
			refreshJob = new ContextEditorDelayedRefreshJob(commonViewer, "refresh viewer");
		}
	}

	private void refresh(List<IInteractionElement> elements) {
		createRefreshJob();
		if (refreshJob != null) {
			refreshJob.doRefresh(elements.toArray());
		}
	}

	private void createDisplaySection(Composite composite) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setText("Elements");
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);

		if (task.equals(TasksUi.getTaskActivityManager().getActiveTask())) {
			sectionClient.setLayout(new Layout() {

				@Override
				protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
					return new Point(0, 0);
				}

				@Override
				protected void layout(Composite composite, boolean flushCache) {
					Rectangle clientArea = composite.getClientArea();
					commonViewer.getControl()
							.setBounds(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
				}

			});
			createViewer(sectionClient);
		} else {
			sectionClient.setLayout(new GridLayout());
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
			// TODO: find a sane way of doing this, perhaps via AbstractContextUiBridge, should be:
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
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Could not set flat layout on Java content provider", e));
			}
		}
	}

	protected CommonViewer createCommonViewer(Composite parent) {
		CommonViewer viewer = new CommonViewer(ID_VIEWER, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		return viewer;
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
		//manager.add(removeFromContextAction);
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
