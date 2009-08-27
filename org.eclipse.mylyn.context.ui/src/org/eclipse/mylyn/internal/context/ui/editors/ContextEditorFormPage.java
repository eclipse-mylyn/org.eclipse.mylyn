/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
import java.util.List;

import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiConstants;
import org.eclipse.mylyn.internal.context.ui.views.ContextNodeOpenListener;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.DelayedRefreshJob;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.INavigatorContentExtension;

/**
 * @author Mik Kersten
 */
public class ContextEditorFormPage extends FormPage {

	private static final int SCALE_STEPS = 14;

	public static final String ID_VIEWER = "org.eclipse.mylyn.context.ui.navigator.context"; //$NON-NLS-1$

	private ScrolledForm form;

	private Composite sectionClient;

	private FormToolkit toolkit;

	private CommonViewer commonViewer;

	private Hyperlink activateTaskHyperlink;

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
			if (invisiblePart != null) {
				invisiblePart.updateInvisibleElementsSection();
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
		public void contextChanged(ContextChangeEvent event) {
			Control partControl = getPartControl();
			switch (event.getEventKind()) {
			case ACTIVATED:
				if (partControl != null && !partControl.isDisposed()) {
					updateContentArea();
					refresh();
				}
				break;
			case DEACTIVATED:
				if (partControl != null && !partControl.isDisposed()) {
					updateContentArea();
					refresh();
				}
				break;
			case CLEARED:
				if (event.isActiveContext()) {
					refresh();
				}
				break;
			case ELEMENTS_DELETED:
			case INTEREST_CHANGED:
			case LANDMARKS_ADDED:
			case LANDMARKS_REMOVED:
				refresh(event.getElements());
				break;
			}
		}

	};

	public ContextEditorFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ContextCore.getContextManager().addListener(CONTEXT_LISTENER);
		task = ((TaskEditorInput) getEditorInput()).getTask();

		form = managedForm.getForm();

		toolkit = managedForm.getToolkit();

		//form.setImage(TaskListImages.getImage(TaskListImages.TASK_ACTIVE_CENTERED));
		//form.setText(LABEL);
		//toolkit.decorateFormHeading(form.getForm());

		form.getBody().setLayout(new FillLayout());
		Composite composite = new Composite(form.getBody(), SWT.NONE) {
			@Override
			public Point computeSize(int widhtHint, int heigtHint, boolean changed) {
				Rectangle clientArea = getClientArea();
				return super.computeSize(widhtHint, clientArea.height, changed);
			}
		};
		toolkit.adapt(composite);
		composite.setLayout(new GridLayout(2, false));

		createActionsSection(composite);
		createContentSection(composite);

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
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		section.setText(Messages.ContextEditorFormPage_Actions);

		section.setLayout(new GridLayout());
		GridData sectionGridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		sectionGridData.widthHint = 80;
		section.setLayoutData(sectionGridData);

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		sectionClient.setLayoutData(new GridData());

		ImageHyperlink filterImage = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
		filterImage.setImage(CommonImages.getImage(CommonImages.FILTER));
		filterImage.setToolTipText(Messages.ContextEditorFormPage_Show_All_Elements);
		filterImage.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				doiScale.setSelection(0);
				interestFilter.setThreshold(Integer.MIN_VALUE);
				refresh();
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore	
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});

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
				updateFilterThreshold();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about default selection
			}
		});

		if (!isActiveTask()) {
			doiScale.setEnabled(false);
		}

		if (AttachmentUtil.canUploadAttachment(task)) {
			Label attachImage = toolkit.createLabel(sectionClient, ""); //$NON-NLS-1$
			attachImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_ATTACH));
			attachImage.setEnabled(task != null);
			Hyperlink attachHyperlink = toolkit.createHyperlink(sectionClient,
					Messages.ContextEditorFormPage_Attach_context_, SWT.NONE);
			bindCommand(attachHyperlink, IContextUiConstants.ID_COMMAND_ATTACH_CONTEXT, null);
		}

		if (AttachmentUtil.canDownloadAttachment(task)) {
			Label retrieveImage = toolkit.createLabel(sectionClient, ""); //$NON-NLS-1$
			retrieveImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_RETRIEVE));
			retrieveImage.setEnabled(task != null);
			Hyperlink retrieveHyperlink = toolkit.createHyperlink(sectionClient,
					Messages.ContextEditorFormPage_Retrieve_Context_, SWT.NONE);
			bindCommand(retrieveHyperlink, IContextUiConstants.ID_COMMAND_RETRIEVE_CONTEXT,
					"The task does not have any context attachments.");
		}

		Label copyImage = toolkit.createLabel(sectionClient, ""); //$NON-NLS-1$
		copyImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_COPY));
		Hyperlink copyHyperlink = toolkit.createHyperlink(sectionClient,
				Messages.ContextEditorFormPage_Copy_Context_to_, SWT.NONE);
		bindCommand(copyHyperlink, IContextUiConstants.ID_COMMAND_COPY_CONTEXT,
				"The task context is empty. Activate the task to build a context.");

		Label clearImage = toolkit.createLabel(sectionClient, ""); //$NON-NLS-1$
		clearImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_CLEAR));
		Hyperlink clearHyperlink = toolkit.createHyperlink(sectionClient, Messages.ContextEditorFormPage_RemoveAll,
				SWT.NONE);
		bindCommand(clearHyperlink, IContextUiConstants.ID_COMMAND_CLEAR_CONTEXT,
				"The task context is empty. Activate the task to build a context.");
		section.setExpanded(true);
	}

	private void bindCommand(final Hyperlink hyperlink, final String commandId, final String notEnabledMessage) {
		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				try {
					TasksUiInternal.executeCommand(getSite(), commandId, hyperlink.getText(), task, null);
				} catch (NotEnabledException e) {
					TasksUiInternal.displayStatus(hyperlink.getText(), new Status(IStatus.ERROR,
							ContextUiPlugin.ID_PLUGIN, notEnabledMessage));
				}
			}
		});
//		command.addCommandListener(new ICommandListener() {
//		public void commandChanged(CommandEvent commandEvent) {
//				if (commandEvent.isEnabledChanged()) {
//					hyperlink.setEnabled(commandEvent.getCommand().isEnabled());
//				}
//			}
//		});
	}

	private ContextEditorDelayedRefreshJob refreshJob;

	private InvisibleContextElementsPart invisiblePart;

	/**
	 * Scales logarithmically to a reasonable interest threshold range (e.g. -10000..10000).
	 */
	protected void updateFilterThreshold() {
		if (doiScale.getSelection() == 0) {
			interestFilter.setThreshold(Integer.MIN_VALUE);
		} else if (doiScale.getSelection() == SCALE_STEPS) {
			interestFilter.setThreshold(Integer.MAX_VALUE);
		} else {
			double setting = doiScale.getSelection() - (SCALE_STEPS / 2);
			double threshold = Math.signum(setting) * Math.pow(Math.exp(Math.abs(setting)), 1.5);
			interestFilter.setThreshold(threshold);
		}
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
			refreshJob = new ContextEditorDelayedRefreshJob(commonViewer, "refresh viewer"); //$NON-NLS-1$
		}
	}

	private void refresh(List<IInteractionElement> elements) {
		createRefreshJob();
		if (refreshJob != null) {
			refreshJob.doRefresh(elements.toArray());
		}
	}

	private void createContentSection(Composite composite) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		section.setText(Messages.ContextEditorFormPage_Elements);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new FillLayout());
		section.setClient(sectionClient);

		updateContentArea();

		toolkit.createLabel(composite, "  "); //$NON-NLS-1$

		invisiblePart = new InvisibleContextElementsPart(commonViewer);
		Control invisibleControl = invisiblePart.createControl(toolkit, composite);
		GridDataFactory.fillDefaults().applyTo(invisibleControl);
	}

	private void createActivateTaskHyperlink(Composite parent) {
		activateTaskHyperlink = toolkit.createHyperlink(parent,
				Messages.ContextEditorFormPage_Activate_task_to_edit_context, SWT.NONE);
		activateTaskHyperlink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				TasksUiInternal.activateTaskThroughCommand(task);
			}
		});
	}

	/**
	 * Disposes the viewer when the current task is not active or creates it if task is activated.
	 */
	private void updateContentArea() {
		if (isActiveTask()) {
			doiScale.setEnabled(true);
			doiScale.setSelection(SCALE_STEPS / 2);
			if (activateTaskHyperlink != null) {
				activateTaskHyperlink.dispose();
				activateTaskHyperlink = null;
			}
			if (commonViewer == null) {
				createViewer(sectionClient);
			}

			if (invisiblePart != null) {
				invisiblePart.setCommonViewer(commonViewer);
			}
			updateFilterThreshold();
		} else {
			doiScale.setEnabled(false);
			doiScale.setSelection(SCALE_STEPS / 2);
			if (commonViewer != null) {
				commonViewer.getControl().dispose();
				commonViewer = null;
				if (invisiblePart != null) {
					invisiblePart.setCommonViewer(commonViewer);
				}
				disposeRefreshJob();
			}
			if (activateTaskHyperlink == null) {
				createActivateTaskHyperlink(sectionClient);
			}
		}
		sectionClient.layout();
	}

	private synchronized void disposeRefreshJob() {
		if (refreshJob != null) {
			refreshJob.cancel();
			refreshJob = null;
		}
	}

	private boolean isActiveTask() {
		return task.equals(TasksUi.getTaskActivityManager().getActiveTask());
	}

	private void createViewer(Composite parent) {
		commonViewer = new CommonViewer(ID_VIEWER, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		commonViewer.setUseHashlookup(true);
		commonViewer.addFilter(interestFilter);
		commonViewer.addOpenListener(new ContextNodeOpenListener(commonViewer));
		try {
			commonViewer.getControl().setRedraw(false);

			forceFlatLayoutOfJavaContent(commonViewer);

			commonViewer.setInput(getSite().getPage().getInput());
			hookContextMenu();
			commonViewer.expandAll();
		} finally {
			commonViewer.getControl().setRedraw(true);
		}
	}

	public static void forceFlatLayoutOfJavaContent(CommonViewer commonViewer) {
		INavigatorContentExtension javaContent = commonViewer.getNavigatorContentService().getContentExtensionById(
				"org.eclipse.jdt.java.ui.javaContent"); //$NON-NLS-1$
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
				Method method = clazz.getDeclaredMethod("setIsFlatLayout", new Class[] { boolean.class }); //$NON-NLS-1$
				method.invoke(treeContentProvider, new Object[] { true });
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Could not set flat layout on Java content provider", e)); //$NON-NLS-1$
			}
		}
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
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
