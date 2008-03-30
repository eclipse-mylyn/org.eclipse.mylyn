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
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorBusyIndicator;
import org.eclipse.mylyn.internal.tasks.ui.editors.IBusyEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionContributor;
import org.eclipse.mylyn.internal.tasks.ui.util.SelectionProviderAdapter;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDragSourceListener;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @author Mik Kersten
 * @author Eric Booth (initial prototype)
 * @author Rob Elves
 */
public class TaskEditor extends SharedHeaderFormEditor implements IBusyEditor {

	public static final String ID_EDITOR = "org.eclipse.mylyn.tasks.ui.editors.task";

	protected AbstractTask task;

	private TaskEditorInput taskEditorInput;

	private final List<IEditorPart> editors = new ArrayList<IEditorPart>();

	private IEditorPart contentOutlineProvider = null;

	public final Object FAMILY_SUBMIT = new Object();

	private MenuManager menuManager = new MenuManager();

	private EditorBusyIndicator editorBusyIndicator;

	private IHyperlinkListener messageHyperLinkListener;

	private TaskDragSourceListener titleDragSourceListener;

	public TaskEditor() {
	}

	protected void contextMenuAboutToShow(IMenuManager manager) {
		TaskEditorActionContributor contributor = getContributor();
		// IFormPage page = getActivePageInstance();
		if (contributor != null) {
			contributor.contextMenuAboutToShow(manager);
		}
	}

	public TaskEditorActionContributor getContributor() {
		return (TaskEditorActionContributor) getEditorSite().getActionBarContributor();
	}

	/**
	 * Configures the standard task editor context menu
	 * 
	 * @Since 2.3
	 */
	public void configureContextMenuManager(MenuManager manager, TextViewer textViewer) {
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

		if (textViewer != null) {
			TaskEditorActionContributor contributor = getContributor();
			if (contributor != null) {
				contributor.addTextViewer(textViewer);
			}
		}
	}

	protected void configureContextMenuManager(MenuManager manager) {
		configureContextMenuManager(manager, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return getAdapterDelgate(adapter);
	}

	public Object getAdapterDelgate(Class<?> adapter) {
		// TODO: consider adding: IContentOutlinePage.class.equals(adapter) &&
		if (contentOutlineProvider != null) {
			return contentOutlineProvider.getAdapter(adapter);
		} else {
			return super.getAdapter(adapter);
		}
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

	/**
	 * Refresh editor with new contents (if any)
	 */
	public void refreshEditorContents() {
		for (IFormPage page : getPages()) {
			if (page instanceof AbstractRepositoryTaskEditor) {
				AbstractRepositoryTaskEditor editor = (AbstractRepositoryTaskEditor) page;
				editor.refreshEditor();
			}
		}
	}

	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
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

	@Override
	public boolean isSaveAsAllowed() {
		return false;
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

	@Deprecated
	public void markDirty() {
		firePropertyChange(PROP_DIRTY);
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

	@Override
	public void dispose() {
		if (editorBusyIndicator != null) {
			editorBusyIndicator.stop();
		}

		for (IEditorPart part : editors) {
			part.dispose();
		}

		super.dispose();
	}

	public TaskEditorInput getTaskEditorInput() {
		return taskEditorInput;
	}

	@Override
	protected void addPages() {
		editorBusyIndicator = new EditorBusyIndicator(this);

		menuManager = new MenuManager();
		configureContextMenuManager(menuManager);
		getContainer().setMenu(menuManager.createContextMenu(getContainer()));

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

			// createPages
			for (AbstractTaskEditorPageFactory factory : pageFactories) {
				try {
					FormPage page = factory.createPage(this);
					int index = addPage(page);
					setPageImage(index, factory.getPageImage());
					setPageText(index, factory.getPageText());
				} catch (Exception e) {
					StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
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
	}

	private void addPage(AbstractTaskEditorFactory factory) {
		IEditorInput editorInput;
		if (taskEditorInput != null && taskEditorInput.getTask() == null) {
			editorInput = new RepositoryTaskEditorInput(taskEditorInput.getTaskRepository(),
					taskEditorInput.getTaskData().getId(), "");
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
						setPageImage(index, TasksUiImages.getImage(input.getImageDescriptor()));
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

	private void updateTitleImage() {
		if (task != null) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(task.getConnectorKind());
			if (connectorUi != null) {
				ImageDescriptor overlayDescriptor = connectorUi.getTaskKindOverlay(task);
				setTitleImage(TasksUiImages.getCompositeTaskImage(TasksUiImages.TASK, overlayDescriptor, false));
			} else {
				setTitleImage(TasksUiImages.getImage(TasksUiImages.TASK));
			}
		} else if (getEditorInput() instanceof AbstractRepositoryTaskEditorInput) {
			this.setTitleImage(TasksUiImages.getImage(TasksUiImages.TASK_REMOTE));
		} else {
			setTitleImage(TasksUiImages.getImage(TasksUiImages.TASK));
		}
	}

	@Override
	public void setFocus() {
		if (getActivePageInstance() instanceof AbstractNewRepositoryTaskEditor) {
			getActivePageInstance().setFocus();
		} else {
			super.setFocus();
		}
	}

	/**
	 * Update the title of the editor
	 */
	// API 3.0 rename to setTitle()
	public void updateTitle(String name) {
		// setContentDescription(name);
		setPartName(name);
		setTitleToolTip(name);
		updateFormTitle();
	}

	@Override
	public void showBusy(boolean busy) {
		if (busy) {
			if (TasksUiUtil.isAnimationsEnabled()) {
				editorBusyIndicator.start();
			}
		} else {
			editorBusyIndicator.stop();
		}

		for (IFormPage page : getPages()) {
			if (page instanceof AbstractRepositoryTaskEditor) {
				AbstractRepositoryTaskEditor editor = (AbstractRepositoryTaskEditor) page;
				editor.showBusy(busy);
			}
		}
	}

	public ISelection getSelection() {
		if (getSite() != null && getSite().getSelectionProvider() != null) {
			return getSite().getSelectionProvider().getSelection();
		} else {
			return StructuredSelection.EMPTY;
		}
	}

	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		getToolkit().decorateFormHeading(headerForm.getForm().getForm());
		headerForm.getForm().setImage(TasksUiImages.getImage(TasksUiImages.TASK));
		updateFormTitle();
	}

	private void installTitleDrag(Form form, final RepositoryTaskData taskData) {
		if (taskData != null && taskData.isNew()) {
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
					if (task != null) {
						return new StructuredSelection(task);
					} else if (taskData != null && !taskData.isNew()) {
						return new StructuredSelection(taskData);
					}
					return null;
				}
			});
			form.addTitleDragSupport(DND.DROP_MOVE | DND.DROP_LINK, transferTypes, titleDragSourceListener);
		}
	}

	protected void updateFormTitle() {
		RepositoryTaskData taskData = null;
		IEditorInput input = getEditorInput();
		if (input instanceof TaskEditorInput) {
			if (task instanceof LocalTask) {
				getHeaderForm().getForm().setText("Task: " + task.getSummary());
			} else if (task != null) {
				setFormHeaderImage(task.getConnectorKind());
				setFormHeaderLabel(task);
			}
		} else if (input instanceof RepositoryTaskEditorInput) {
			taskData = ((RepositoryTaskEditorInput) input).getTaskData();
			if (task != null && taskData != null && !taskData.isNew()) {
				setFormHeaderImage(task.getConnectorKind());
				setFormHeaderLabel(task);
			} else {
				if (taskData != null) {
					setFormHeaderImage(taskData.getRepositoryKind());
					setFormHeaderLabel(taskData);
				}
			}
		}
		installTitleDrag(getHeaderForm().getForm().getForm(), taskData);
	}

	private void setFormHeaderImage(String repositoryKind) {
		ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(repositoryKind);
		Image image = TasksUiImages.getImageWithOverlay(TasksUiImages.REPOSITORY, overlay, false, false);
		if (getHeaderForm() != null) {
			getHeaderForm().getForm().setImage(image);
		}
	}

	public Form getTopForm() {
		return this.getHeaderForm().getForm().getForm();
	}

	/**
	 * @since 2.3
	 */
	public void setMessage(String message, int type, IHyperlinkListener listener) {
		if (this.getHeaderForm() != null && this.getHeaderForm().getForm() != null) {
			if (!this.getHeaderForm().getForm().isDisposed()) {
				getTopForm().setMessage(message, type);

				if (messageHyperLinkListener != null) {
					getTopForm().removeMessageHyperlinkListener(messageHyperLinkListener);
				}
				if (listener != null) {
					getTopForm().addMessageHyperlinkListener(listener);
				}
				messageHyperLinkListener = listener;
			}
		}
	}

	public void setMessage(String message, int type) {
		setMessage(message, type, null);
	}

	protected IWorkbenchSiteProgressService getProgressService() {
		Object siteService = getEditorSite().getAdapter(IWorkbenchSiteProgressService.class);
		if (siteService != null) {
			return (IWorkbenchSiteProgressService) siteService;
		}
		return null;
	}

	private void setFormHeaderLabel(RepositoryTaskData taskData) {
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskData.getRepositoryKind());

		String kindLabel = taskData.getTaskKind();

		if (connectorUi != null) {
			kindLabel = connectorUi.getTaskKindLabel(taskData);
		}

		String idLabel = taskData.getTaskKey();

		if (taskData.isNew()) {
			if (connectorUi != null) {
				kindLabel = "New " + connectorUi.getTaskKindLabel(taskData);
			} else {
				kindLabel = "New " + taskData.getTaskKind();
			}
			idLabel = "";
		}

		if (idLabel != null) {
			if (getHeaderForm().getForm() != null) {
				getHeaderForm().getForm().setText(kindLabel + " " + idLabel);
			}
		} else if (getHeaderForm().getForm() != null) {
			getHeaderForm().getForm().setText(kindLabel);
		}
	}

	private void setFormHeaderLabel(AbstractTask repositoryTask) {
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repositoryTask.getConnectorKind());
		String kindLabel = "";
		if (connectorUi != null) {
			kindLabel = connectorUi.getTaskKindLabel(repositoryTask);
		}

		String idLabel = repositoryTask.getTaskKey();

		if (idLabel != null) {
			if (getHeaderForm().getForm() != null) {
				getHeaderForm().getForm().setText(kindLabel + " " + idLabel);
			}
		} else if (getHeaderForm() != null && getHeaderForm().getForm() != null) {
			getHeaderForm().getForm().setText(kindLabel);
		}
	}

	@Override
	public void setTitleImage(Image titleImage) {
		super.setTitleImage(titleImage);
	}

}
