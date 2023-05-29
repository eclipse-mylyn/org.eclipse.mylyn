/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui.editors;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.DelayedRefreshJob;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.views.ContextNodeOpenListener;
import org.eclipse.mylyn.internal.tasks.ui.context.AttachContextHandler;
import org.eclipse.mylyn.internal.tasks.ui.context.ClearContextHandler;
import org.eclipse.mylyn.internal.tasks.ui.context.CopyContextHandler;
import org.eclipse.mylyn.internal.tasks.ui.context.RetrieveContextHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
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

	private ScalableInterestFilter interestFilter;

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
			switch (event.getEventKind()) {
			case ACTIVATED:
				if (isActiveTask()) {
					context.setWrappedContext(ContextCorePlugin.getContextManager().getActiveContext());
					refresh();// in case activation was caused by a retrieve context
				}
				break;
			case DEACTIVATED:
				if (context.isForSameTaskAs(event.getContext())) {
					context.setWrappedContext(
							ContextCorePlugin.getContextStore().loadContext(task.getHandleIdentifier()));
				}
				break;
			case CLEARED:
				if (context.isForSameTaskAs(event.getContextHandle())) {// context may be null so check handle
					context.setWrappedContext(
							ContextCorePlugin.getContextStore().loadContext(task.getHandleIdentifier()));
					refresh();//in this case the context has actually changed so refresh
				}
				break;
			case ELEMENTS_DELETED:
			case INTEREST_CHANGED:
			case LANDMARKS_ADDED:
			case LANDMARKS_REMOVED:
				if (context.isForSameTaskAs(event.getContext())) {
					context.setWrappedContext(event.getContext());
					refresh(event.getElements());
				}
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
		if (isActiveTask()) {
			context = new ContextWrapper(ContextCorePlugin.getContextManager().getActiveContext(), task);
		} else {
			context = new ContextWrapper(ContextCorePlugin.getContextStore().loadContext(task.getHandleIdentifier()),
					task);
		}

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
		disposeRefreshJob();
		ContextCore.getContextManager().removeListener(CONTEXT_LISTENER);
		if (invisiblePart != null) {
			invisiblePart.dispose();
			invisiblePart = null;
		}
	}

	private int calculateMaxWidth(int existing, Control image, Control link) {
		Point imageSize = image.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		Point hyperlinkSize = link.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		int required = imageSize.x + hyperlinkSize.x;
		return required > existing ? required : existing;
	}

	private void createActionsSection(Composite composite) {
		Section section = toolkit.createSection(composite,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		section.setText(Messages.ContextEditorFormPage_Actions);

		section.setLayout(new GridLayout());
		GridData sectionGridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		section.setLayoutData(sectionGridData);

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		GridData sectionClientGridData = new GridData();
		sectionClient.setLayoutData(sectionClientGridData);

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

		Label attachImage = null;
		Hyperlink attachHyperlink = null;
		if (AttachmentUtil.canUploadAttachment(task)) {
			attachImage = toolkit.createLabel(sectionClient, ""); //$NON-NLS-1$
			attachImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_ATTACH));
			attachImage.setEnabled(task != null);
			attachHyperlink = toolkit.createHyperlink(sectionClient, Messages.ContextEditorFormPage_Attach_context_,
					SWT.NONE);
			//bindCommand(attachHyperlink, IContextUiConstants.ID_COMMAND_ATTACH_CONTEXT, null);
			attachHyperlink.setEnabled(task != null);
			attachHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					AttachContextHandler.run(task);
				}
			});
		}

		Label retrieveImage = null;
		Hyperlink retrieveHyperlink = null;
		if (AttachmentUtil.canDownloadAttachment(task)) {
			retrieveImage = toolkit.createLabel(sectionClient, ""); //$NON-NLS-1$
			retrieveImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_RETRIEVE));
			retrieveImage.setEnabled(task != null);
			retrieveHyperlink = toolkit.createHyperlink(sectionClient, Messages.ContextEditorFormPage_Retrieve_Context_,
					SWT.NONE);
			//bindCommand(retrieveHyperlink, IContextUiConstants.ID_COMMAND_RETRIEVE_CONTEXT,
			//		Messages.ContextEditorFormPage_No_context_attachments_Error);
			retrieveHyperlink.setEnabled(task != null);
			retrieveHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					RetrieveContextHandler.run(task);
				}
			});
		}

		Label copyImage = toolkit.createLabel(sectionClient, ""); //$NON-NLS-1$
		copyImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_COPY));
		Hyperlink copyHyperlink = toolkit.createHyperlink(sectionClient,
				Messages.ContextEditorFormPage_Copy_Context_to_, SWT.NONE);
		//bindCommand(copyHyperlink, IContextUiConstants.ID_COMMAND_COPY_CONTEXT,
		//		Messages.ContextEditorFormPage_Context_is_empty_Error);
		copyHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				CopyContextHandler.run(task);
			}
		});

		Label clearImage = toolkit.createLabel(sectionClient, ""); //$NON-NLS-1$
		clearImage.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_CLEAR));
		Hyperlink clearHyperlink = toolkit.createHyperlink(sectionClient, Messages.ContextEditorFormPage_RemoveAll,
				SWT.NONE);
		//bindCommand(clearHyperlink, IContextUiConstants.ID_COMMAND_CLEAR_CONTEXT,
		//		Messages.ContextEditorFormPage_Context_is_empty_Error);
		clearHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				ClearContextHandler.run(task);
			}
		});

		int with = 0;
		if (AttachmentUtil.canUploadAttachment(task)) {
			with = calculateMaxWidth(with, attachImage, attachHyperlink);
		}
		if (AttachmentUtil.canDownloadAttachment(task)) {
			with = calculateMaxWidth(with, retrieveImage, retrieveHyperlink);
		}
		with = calculateMaxWidth(with, copyImage, copyHyperlink);
		with = calculateMaxWidth(with, clearImage, clearHyperlink);

		sectionClientGridData.widthHint = with;
		section.setExpanded(true);
	}

//	private void bindCommand(final Hyperlink hyperlink, final String commandId, final String notEnabledMessage) {
//		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
//			@Override
//			public void linkActivated(HyperlinkEvent event) {
//				try {
//					TasksUiInternal.executeCommand(getSite(), commandId, hyperlink.getText(), task, null);
//				} catch (NotEnabledException e) {
//					TasksUiInternal.displayStatus(hyperlink.getText(), new Status(IStatus.ERROR,
//							ContextUiPlugin.ID_PLUGIN, notEnabledMessage));
//				}
//			}
//		});
//	}

	private ContextEditorDelayedRefreshJob refreshJob;

	private InvisibleContextElementsPart invisiblePart;

	private ContextWrapper context;

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
			refreshJob.refreshElements(elements.toArray());
		}
	}

	private void createContentSection(Composite composite) {
		Section section = toolkit.createSection(composite,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		section.setText(Messages.ContextEditorFormPage_Elements);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new FillLayout());
		section.setClient(sectionClient);
		createToolBar(section);

		doiScale.setEnabled(true);
		doiScale.setSelection(SCALE_STEPS / 2);
		if (commonViewer == null) {
			createViewer(sectionClient);
		}

		if (invisiblePart != null) {
			invisiblePart.setCommonViewer(commonViewer);
		}
		updateFilterThreshold();
		sectionClient.layout();

		toolkit.createLabel(composite, "  "); //$NON-NLS-1$

		invisiblePart = new InvisibleContextElementsPart(commonViewer, context);
		Control invisibleControl = invisiblePart.createControl(toolkit, composite);
		GridDataFactory.fillDefaults().applyTo(invisibleControl);
	}

	private void createToolBar(Section section) {
		Composite composite = toolkit.createComposite(section);
		composite.setBackground(null);
		section.setTextClient(composite);
		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
		manager.add(new Action(Messages.ContextEditorFormPage_Collapse_All, CommonImages.COLLAPSE_ALL_SMALL) {
			@Override
			public void run() {
				if (commonViewer != null && commonViewer.getTree() != null && !commonViewer.getTree().isDisposed()) {
					commonViewer.collapseAll();
				}
			}
		});
		manager.add(new Action(Messages.ContextEditorFormPage_Expand_All, CommonImages.EXPAND_ALL_SMALL) {
			@Override
			public void run() {
				if (commonViewer != null && commonViewer.getTree() != null && !commonViewer.getTree().isDisposed()) {
					commonViewer.expandAll();
				}
			}
		});
		manager.createControl(composite);
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
		interestFilter = new ScalableInterestFilter(context);
		commonViewer.addFilter(interestFilter);
		commonViewer.addOpenListener(new ContextNodeOpenListener(commonViewer, context));
		try {
			commonViewer.getControl().setRedraw(false);

			ContextUiPlugin.forceFlatLayoutOfJavaContent(commonViewer);

			commonViewer.setInput(getSite().getPage().getInput());
			hookContextMenu();
			commonViewer.expandAll();
		} finally {
			commonViewer.getControl().setRedraw(true);
		}
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu") { //$NON-NLS-1$
			private final IMenuListener listener = new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					fillContextMenu(manager);
				}
			};
			{
				addMenuListener(listener);
			}

			@Override
			public void addMenuListener(IMenuListener listener) {
				// HACK - ensure we are the last listener so we can remove items added by other listeners
				super.removeMenuListener(this.listener);
				super.addMenuListener(listener);
				super.addMenuListener(this.listener);
			}
		};
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
