/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryTaskEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractTaskEditorFactory;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.NewTaskEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.RepositoryTaskEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorBusyIndicator;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.IBusyEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionContributor;
import org.eclipse.mylyn.internal.tasks.ui.util.SelectionProviderAdapter;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDragSourceListener;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @author Mik Kersten
 * @author Eric Booth (initial prototype)
 * @author Rob Elves
 */
@SuppressWarnings( { "deprecation", "restriction" })
public class TaskEditor extends SharedHeaderFormEditor {

	public static final String ID_EDITOR = "org.eclipse.mylyn.tasks.ui.editors.task";

	private ToggleTaskActivationAction activateAction;

	@Deprecated
	private IEditorPart contentOutlineProvider = null;

	private EditorBusyIndicator editorBusyIndicator;

	private MenuManager menuManager;

	private IHyperlinkListener messageHyperLinkListener;

	private ITask task;

	private TaskEditorInput taskEditorInput;

	private TaskDragSourceListener titleDragSourceListener;

	public TaskEditor() {
	}

	@Deprecated
	private void addPage(AbstractTaskEditorFactory factory) {
		IEditorInput editorInput;
		if (taskEditorInput != null && taskEditorInput.getTask() == null) {
			editorInput = new RepositoryTaskEditorInput(taskEditorInput.getTaskRepository(), taskEditorInput.getTask()
					.getTaskId(), "");
		} else {
			editorInput = getEditorInput();
		}
		if (factory.canCreateEditorFor(task) || factory.canCreateEditorFor(editorInput)) {
			try {
				IEditorPart editor = factory.createEditor(this, editorInput);
				IEditorInput input = task != null ? factory.createEditorInput(task) : editorInput;
				if (editor != null && input != null) {
					FormPage taskEditor = (FormPage) editor;
					editor.init(getEditorSite(), input);
					int index = addPage(taskEditor);
					if (input.getImageDescriptor() != null) {
						setPageImage(index, CommonImages.getImage(input.getImageDescriptor()));
					}
					if (editor instanceof AbstractRepositoryTaskEditor) {
						((AbstractRepositoryTaskEditor) editor).setParentEditor(this);

						if (editorInput instanceof RepositoryTaskEditorInput) {
							RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) editorInput;
							setPartName(existingInput.getName());
						} else if (editorInput instanceof NewTaskEditorInput) {
							String label = ((NewTaskEditorInput) editorInput).getName();
							setPartName(label);
						}
						setPageText(index, factory.getTitle());

						// TODO review
						setActivePage(index);
					}
				}

				// HACK: overwrites if multiple present
				if (factory.providesOutline()) {
					contentOutlineProvider = editor;
				}
			} catch (Exception e) {
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not create editor via factory: " + factory, e));
			}
		}

	}

	@Override
	protected void addPages() {
		initialize();

		// API REVIEW remove check
		if (taskEditorInput != null) {
			// determine factories
			Set<String> conflictingIds = new HashSet<String>();
			ArrayList<AbstractTaskEditorPageFactory> pageFactories = new ArrayList<AbstractTaskEditorPageFactory>();
			for (AbstractTaskEditorPageFactory pageFactory : TasksUiPlugin.getDefault().getTaskEditorPageFactories()) {
				if (pageFactory.canCreatePageFor(getTaskEditorInput())) {
					pageFactories.add(pageFactory);
					String[] ids = pageFactory.getConflictingIds(getTaskEditorInput());
					if (ids != null) {
						conflictingIds.addAll(Arrays.asList(ids));
					}
				}
			}
			for (Iterator<AbstractTaskEditorPageFactory> it = pageFactories.iterator(); it.hasNext();) {
				if (conflictingIds.contains(it.next().getId())) {
					it.remove();
				}
			}

			// sort by priority
			Collections.sort(pageFactories, new Comparator<AbstractTaskEditorPageFactory>() {
				public int compare(AbstractTaskEditorPageFactory o1, AbstractTaskEditorPageFactory o2) {
					return o1.getPriority() - o2.getPriority();
				}
			});

			// create pages
			for (AbstractTaskEditorPageFactory factory : pageFactories) {
				try {
					FormPage page = factory.createPage(this);
					int index = addPage(page);
					setPageImage(index, factory.getPageImage());
					setPageText(index, factory.getPageText());
					if (factory.getPriority() == AbstractTaskEditorPageFactory.PRIORITY_TASK) {
						setActivePage(index);
					}
					if (page instanceof ISelectionProvider) {
						((ISelectionProvider) page).addSelectionChangedListener(getActionBarContributor());
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Could not create editor via factory: " + factory, e));
				}
			}
		}

		// API REVIEW remove code
		List<AbstractTaskEditorFactory> factories = new ArrayList<AbstractTaskEditorFactory>(TasksUiPlugin.getDefault()
				.getTaskEditorFactories());
		Collections.sort(factories, new Comparator<AbstractTaskEditorFactory>() {
			public int compare(AbstractTaskEditorFactory o1, AbstractTaskEditorFactory o2) {
				return o1.getTabOrderPriority() - o2.getTabOrderPriority();
			}
		});
		for (AbstractTaskEditorFactory factory : factories) {
			addPage(factory);
		}

		updateTitleImage();
		updateHeaderToolBar();
	}

	private void initialize() {
		editorBusyIndicator = new EditorBusyIndicator(new IBusyEditor() {
			public Image getTitleImage() {
				return TaskEditor.this.getTitleImage();
			}

			public void setTitleImage(Image image) {
				TaskEditor.this.setTitleImage(image);
			}
		});

		menuManager = new MenuManager();
		configureContextMenuManager(menuManager);
		Menu menu = menuManager.createContextMenu(getContainer());
		getContainer().setMenu(menu);
		getEditorSite().registerContextMenu(menuManager, getEditorSite().getSelectionProvider(), false);
	}

	/**
	 * @since 3.0
	 */
	@Deprecated
	public void configureContextMenuManager(MenuManager manager) {
		if (manager == null) {
			return;
		}
		IMenuListener listener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		};
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(listener);
	}

	@Deprecated
	protected void contextMenuAboutToShow(IMenuManager manager) {
		TaskEditorActionContributor contributor = getActionBarContributor();
		if (contributor != null) {
			contributor.contextMenuAboutToShow(manager);
		}
	}

	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		getToolkit().decorateFormHeading(headerForm.getForm().getForm());
		updateHeader();
		installTitleDrag(getHeaderForm().getForm().getForm());
	}

	@Override
	public void dispose() {
		if (editorBusyIndicator != null) {
			editorBusyIndicator.stop();
		}
		if (activateAction != null) {
			activateAction.dispose();
		}
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		for (IFormPage page : getPages()) {
			if (page.isDirty()) {
				page.doSave(monitor);
			}
		}

		editorDirtyStateChanged();
	}

	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
	}

	private TaskEditorActionContributor getActionBarContributor() {
		return (TaskEditorActionContributor) getEditorSite().getActionBarContributor();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return getAdapterDelgate(adapter);
	}

	private Object getAdapterDelgate(Class<?> adapter) {
		// TODO: consider adding: IContentOutlinePage.class.equals(adapter) &&
		if (contentOutlineProvider != null) {
			return contentOutlineProvider.getAdapter(adapter);
		} else {
			return super.getAdapter(adapter);
		}
	}

	/**
	 * @since 3.0
	 */
	public Menu getMenu() {
		return getContainer().getMenu();
	}

	@SuppressWarnings("unchecked")
	IFormPage[] getPages() {
		ArrayList formPages = new ArrayList();
		if (pages != null) {
			for (int i = 0; i < pages.size(); i++) {
				Object page = pages.get(i);
				if (page instanceof IFormPage) {
					formPages.add(page);
				}
			}
		}
		return (IFormPage[]) formPages.toArray(new IFormPage[formPages.size()]);
	}

	@Deprecated
	protected IWorkbenchSiteProgressService getProgressService() {
		Object siteService = getEditorSite().getAdapter(IWorkbenchSiteProgressService.class);
		if (siteService != null) {
			return (IWorkbenchSiteProgressService) siteService;
		}
		return null;
	}

	@Deprecated
	public ISelection getSelection() {
		if (getSite() != null && getSite().getSelectionProvider() != null) {
			return getSite().getSelectionProvider().getSelection();
		} else {
			return StructuredSelection.EMPTY;
		}
	}

	public TaskEditorInput getTaskEditorInput() {
		return taskEditorInput;
	}

	@Deprecated
	public Form getTopForm() {
		return this.getHeaderForm().getForm().getForm();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// API REVIEW remove the commented parts
		//		if (!(input instanceof TaskEditorInput)) {
//			throw new PartInitException("Invalid editor input \"" + input.getClass() + "\"");
//		}

		super.init(site, input);

		// API REVIEW remove the instanceof check
		if (input instanceof TaskEditorInput) {
			this.taskEditorInput = (TaskEditorInput) input;
			this.task = taskEditorInput.getTask();
		}

		setPartName(input.getName());
	}

	private void installTitleDrag(Form form) {
		// API 3.0 remove
		if (task == null) {
			return;
		}

		if (titleDragSourceListener == null) {
			Transfer[] transferTypes;
			if (null == task) {
				transferTypes = new Transfer[] { TextTransfer.getInstance() };
			} else {
				transferTypes = new Transfer[] { TaskTransfer.getInstance(), TextTransfer.getInstance(),
						FileTransfer.getInstance() };
			}
			titleDragSourceListener = new TaskDragSourceListener(new SelectionProviderAdapter() {
				@Override
				public ISelection getSelection() {
					return new StructuredSelection(task);
				}
			});
			form.addTitleDragSupport(DND.DROP_MOVE | DND.DROP_LINK, transferTypes, titleDragSourceListener);
		}
	}

	@Override
	public boolean isDirty() {
		for (IFormPage page : getPages()) {
			if (page.isDirty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Deprecated
	public void markDirty() {
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * Refresh editor with new contents (if any)
	 */
	@Deprecated
	public void refreshEditorContents() {
		for (IFormPage page : getPages()) {
			if (page instanceof AbstractRepositoryTaskEditor) {
				AbstractRepositoryTaskEditor editor = (AbstractRepositoryTaskEditor) page;
				editor.refreshEditor();
			}
		}
	}

	@Override
	public void setFocus() {
		IFormPage page = getActivePageInstance();
		if (page != null) {
			page.setFocus();
		} else {
			super.setFocus();
		}
	}

	@Deprecated
	public void setFocusOfActivePage() {
		if (this.getActivePage() > -1) {
			IFormPage page = this.getPages()[this.getActivePage()];
			if (page != null) {
				page.setFocus();
			}
		}
	}

	public void setMessage(String message, int type) {
		setMessage(message, type, null);
	}

	/**
	 * @since 2.3
	 */
	public void setMessage(String message, int type, IHyperlinkListener listener) {
		if (getHeaderForm() != null && getHeaderForm().getForm() != null) {
			if (!getHeaderForm().getForm().isDisposed()) {
				Form form = getHeaderForm().getForm().getForm();
				form.setMessage(message, type);
				if (messageHyperLinkListener != null) {
					form.removeMessageHyperlinkListener(messageHyperLinkListener);
				}
				if (listener != null) {
					form.addMessageHyperlinkListener(listener);
				}
				messageHyperLinkListener = listener;
			}
		}
	}

	/**
	 * @since 3.0
	 */
	public void setStatus(String message, final String title, final IStatus status) {
		setMessage(message, IMessageProvider.ERROR, new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				TasksUiInternal.displayStatus(title, status);
			}
		});
	}

	@Override
	public void showBusy(boolean busy) {
		if (busy) {
			if (TasksUiInternal.isAnimationsEnabled()) {
				editorBusyIndicator.start();
			}
		} else {
			editorBusyIndicator.stop();
		}
		Form form = getHeaderForm().getForm().getForm();
		EditorUtil.setEnabledState(form.getBody(), !busy);
		for (IFormPage page : getPages()) {
			if (page instanceof WorkbenchPart) {
				WorkbenchPart part = (WorkbenchPart) page;
				part.showBusy(busy);
			}
		}
	}

	private void updateHeader() {
		IEditorInput input = getEditorInput();
		if (input instanceof TaskEditorInput) {
			updateHeaderImage(task.getConnectorKind());
			updateHeaderLabel(task);
		} else if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskData taskData = ((RepositoryTaskEditorInput) input).getTaskData();
			if (task != null) {
				updateHeaderImage(task.getConnectorKind());
				updateHeaderLabel(task);
			} else if (taskData != null) {
				updateHeaderImage(taskData.getConnectorKind());
				updateHeaderLabel(taskData);
			}
		}
		installTitleDrag(getHeaderForm().getForm().getForm());
	}

	/**
	 * @since 3.0
	 */
	public void updateHeaderToolBar() {
		Form form = getHeaderForm().getForm().getForm();
		FormToolkit toolkit = getHeaderForm().getToolkit();
		IToolBarManager toolBarManager = form.getToolBarManager();

		toolBarManager.removeAll();
		toolBarManager.update(true);

		for (IFormPage page : getPages()) {
			if (page instanceof AbstractTaskEditorPage) {
				AbstractTaskEditorPage taskEditorPage = (AbstractTaskEditorPage) page;
				taskEditorPage.fillToolBar(toolBarManager);
			} else if (page instanceof AbstractRepositoryTaskEditor) {
				AbstractRepositoryTaskEditor taskEditorPage = (AbstractRepositoryTaskEditor) page;
				taskEditorPage.fillToolBar(toolBarManager);
			}
		}

		// TODO EDITOR remove check
		if (task != null) {
			if (activateAction == null) {
				activateAction = new ToggleTaskActivationAction(task, toolBarManager);
			}
			toolBarManager.add(new Separator("activation"));
			toolBarManager.add(activateAction);
		}

		toolBarManager.update(true);
	}

	private void updateHeaderImage(String connectorKind) {
		if (LocalRepositoryConnector.CONNECTOR_KIND.equals(connectorKind)) {
			getHeaderForm().getForm().setImage(CommonImages.getImage(TasksUiImages.TASK));
		} else {
			ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(connectorKind);
			Image image = CommonImages.getImageWithOverlay(TasksUiImages.REPOSITORY, overlay, false, false);
			getHeaderForm().getForm().setImage(image);
		}
	}

	private void updateHeaderLabel(ITask task) {
		if (task instanceof LocalTask) {
			getHeaderForm().getForm().setText("Task: " + task.getSummary());
		} else {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(task.getConnectorKind());
			String kindLabel = "";
			if (connectorUi != null) {
				kindLabel = connectorUi.getTaskKindLabel(task);
			}

			String idLabel = task.getTaskKey();
			if (idLabel != null) {
				getHeaderForm().getForm().setText(kindLabel + " " + idLabel);
			} else {
				getHeaderForm().getForm().setText(kindLabel);
			}
		}
	}

	@Deprecated
	private void updateHeaderLabel(RepositoryTaskData taskData) {
		String kindLabel = taskData.getTaskKind();
		String idLabel = taskData.getTaskKey();

		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskData.getConnectorKind());
		if (connectorUi != null && task != null) {
			kindLabel = connectorUi.getTaskKindLabel(task);
		}
		if (taskData.isNew()) {
			kindLabel = "New " + kindLabel;
			idLabel = "";
		}

		if (getHeaderForm().getForm() != null) {
			if (idLabel != null) {
				getHeaderForm().getForm().setText(kindLabel + " " + idLabel);
			} else {
				getHeaderForm().getForm().setText(kindLabel);
			}
		}
	}

	/**
	 * Update the title of the editor
	 */
	@Deprecated
	public void updateTitle(String name) {
		// setContentDescription(name);
		setPartName(name);
		setTitleToolTip(name);
		updateHeader();
	}

	private void updateTitleImage() {
		if (task != null) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(task.getConnectorKind());
			if (connectorUi != null) {
				ImageDescriptor overlayDescriptor = connectorUi.getTaskKindOverlay(task);
				setTitleImage(CommonImages.getCompositeTaskImage(TasksUiImages.TASK, overlayDescriptor, false));
			} else {
				setTitleImage(CommonImages.getImage(TasksUiImages.TASK));
			}
		} else if (getEditorInput() instanceof AbstractRepositoryTaskEditorInput) {
			setTitleImage(CommonImages.getImage(TasksUiImages.TASK_REMOTE));
		} else {
			setTitleImage(CommonImages.getImage(TasksUiImages.TASK));
		}
	}

}
