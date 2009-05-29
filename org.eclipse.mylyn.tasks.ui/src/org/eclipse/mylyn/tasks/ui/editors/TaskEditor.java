/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eric Booth - initial prototype
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.SelectionProviderAdapter;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.editor.EditorBusyIndicator;
import org.eclipse.mylyn.internal.provisional.commons.ui.editor.IBusyEditor;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskEditorScheduleAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.Messages;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionContributor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskPlanningEditor;
import org.eclipse.mylyn.internal.tasks.ui.util.PlatformUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDragSourceListener;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.internal.forms.widgets.BusyIndicator;
import org.eclipse.ui.internal.forms.widgets.FormHeading;
import org.eclipse.ui.internal.forms.widgets.TitleRegion;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 * @since 2.0
 */
public class TaskEditor extends SharedHeaderFormEditor {

	/**
	 * @since 2.0
	 */
	public static final String ID_EDITOR = "org.eclipse.mylyn.tasks.ui.editors.task"; //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final String ID_TOOLBAR_HEADER = "org.eclipse.mylyn.tasks.ui.editors.task.toolbar.header"; //$NON-NLS-1$

	private static final String ID_LEFT_TOOLBAR_HEADER = "org.eclipse.mylyn.tasks.ui.editors.task.toolbar.header.left"; //$NON-NLS-1$

	private ToggleTaskActivationAction activateAction;

	@Deprecated
	private final IEditorPart contentOutlineProvider = null;

	private EditorBusyIndicator editorBusyIndicator;

	private MenuManager menuManager;

	private IHyperlinkListener messageHyperLinkListener;

	private ITask task;

	private TaskEditorInput taskEditorInput;

	private TaskDragSourceListener titleDragSourceListener;

	private Composite editorParent;

	private IMenuService menuService;

	private IToolBarManager toolBarManager;

	private ToolBarManager leftToolBarManager;

	private ToolBar leftToolBar;

	private Image headerImage;

	private BusyIndicator busyLabel;

	private int initialLeftToolbarSize;

	private boolean noExtraPadding;

	public TaskEditor() {
	}

	@Override
	protected Composite createPageContainer(Composite parent) {
		this.editorParent = parent;
		Composite composite = super.createPageContainer(parent);

		// create left tool bar that replaces form heading label
		try {
			FormHeading heading = (FormHeading) getHeaderForm().getForm().getForm().getHead();
			// ensure that busy label exists and no standard label is set
			heading.setBusy(true);
			heading.setBusy(false);

			Field field = FormHeading.class.getDeclaredField("titleRegion"); //$NON-NLS-1$
			field.setAccessible(true);

			TitleRegion titleRegion = (TitleRegion) field.get(heading);

			busyLabel = (BusyIndicator) titleRegion.getChildren()[1];

			leftToolBarManager = new ToolBarManager(SWT.FLAT);
			leftToolBar = leftToolBarManager.createControl(titleRegion);
			leftToolBar.moveAbove(busyLabel);
			leftToolBar.setLocation(busyLabel.getLocation().x, 1);
//			if (headerImage != null) {
//				busyLabel.setImage(headerImage);
//			}
			leftToolBar.addControlListener(new ControlAdapter() {
				private boolean ignoreResizeEvents;

				@Override
				public void controlResized(ControlEvent e) {
					if (ignoreResizeEvents) {
						return;
					}
					ignoreResizeEvents = true;
					try {
						// the tool bar contents has changed, update state
						updateHeaderImage();
						updateHeaderLabel();
					} finally {
						ignoreResizeEvents = false;
					}
				}
			});
			busyLabel.addControlListener(new ControlAdapter() {
				@Override
				public void controlMoved(ControlEvent e) {
					if (leftToolBar != null) {
						leftToolBar.setLocation(busyLabel.getLocation().x, leftToolBar.getLocation().y);
					}
				}
			});
			titleRegion.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					if (leftToolBar != null) {
						// bottom align tool bar in title region
						Point size = leftToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
						int y = leftToolBar.getParent().getSize().y - size.y - 2;
						if (!hasLeftToolBar()) {
							// hide tool bar to avoid overlaying busyLabel on windows
							size.x = 0;
						}
						leftToolBar.setBounds(busyLabel.getLocation().x, y, size.x, size.y);
					}
				}
			});
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Failed to create left header toolbar", e)); //$NON-NLS-1$
			if (leftToolBar != null) {
				leftToolBar.dispose();
				leftToolBar = null;
			}
			if (leftToolBarManager != null) {
				leftToolBarManager.dispose();
				leftToolBarManager = null;
			}
		}
		return composite;
	}

	Composite getEditorParent() {
		return editorParent;
	}

	@Override
	protected void addPages() {
		initialize();

		// determine factories
		Set<String> conflictingIds = new HashSet<String>();
		ArrayList<AbstractTaskEditorPageFactory> pageFactories = new ArrayList<AbstractTaskEditorPageFactory>();
		for (AbstractTaskEditorPageFactory pageFactory : TasksUiPlugin.getDefault().getTaskEditorPageFactories()) {
			if (pageFactory.canCreatePageFor(getTaskEditorInput()) && WorkbenchUtil.allowUseOf(pageFactory)) {
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
				IFormPage page = factory.createPage(this);
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
						"Could not create editor via factory: " + factory, e)); //$NON-NLS-1$
			}
		}

		updateTitleImage();
		updateHeaderToolBar();
		installTitleDrag(getHeaderForm().getForm().getForm());
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

		// install context menu on form heading and title
		getHeaderForm().getForm().setMenu(menu);
		Composite head = getHeaderForm().getForm().getForm().getHead();
		if (head != null) {
			CommonUiUtil.setMenu(head, menu);
		}
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
	protected FormToolkit createToolkit(Display display) {
		// create a toolkit that shares colors between editors.
		return new FormToolkit(TasksUiPlugin.getDefault().getFormColors(display));
	}

	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		getToolkit().decorateFormHeading(headerForm.getForm().getForm());
		updateHeader();
	}

	@Override
	public void dispose() {
		if (headerImage != null) {
			headerImage.dispose();
		}
		if (editorBusyIndicator != null) {
			editorBusyIndicator.stop();
		}
		if (activateAction != null) {
			activateAction.dispose();
		}
		if (menuService != null && toolBarManager instanceof ContributionManager) {
			menuService.releaseContributions((ContributionManager) toolBarManager);
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
		if (contentOutlineProvider != null) {
			return contentOutlineProvider.getAdapter(adapter);
		} else if (IContentOutlinePage.class.equals(adapter)) {
			IFormPage[] pages = getPages();
			for (IFormPage page : pages) {
				Object outlinePage = page.getAdapter(adapter);
				if (outlinePage != null) {
					return outlinePage;
				}
			}
		}
		return super.getAdapter(adapter);
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
		if (!(input instanceof TaskEditorInput)) {
			throw new PartInitException("Invalid editor input \"" + input.getClass() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		super.init(site, input);

		this.taskEditorInput = (TaskEditorInput) input;
		this.task = taskEditorInput.getTask();

		setPartName(input.getName());

		// activate context
		IContextService contextSupport = (IContextService) site.getService(IContextService.class);
		if (contextSupport != null) {
			contextSupport.activateContext(ID_EDITOR);
		}
	}

	private void installTitleDrag(Form form) {
		if (titleDragSourceListener == null && !hasLeftToolBar()) {
			Transfer[] transferTypes;
			if (null == task) {
				transferTypes = new Transfer[] { TextTransfer.getInstance() };
			} else {
				transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer(), TextTransfer.getInstance(),
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
	 * 
	 * @since 3.0
	 */
	public void refreshPages() {
		for (IFormPage page : getPages()) {
			if (page instanceof AbstractTaskEditorPage) {
				((AbstractTaskEditorPage) page).refreshFormContent();
			} else if (page instanceof BrowserFormPage) {
				// XXX 3.2 replace by invocation of refreshFromContent();
				((BrowserFormPage) page).init(getEditorSite(), getEditorInput());
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
				try {
					// avoid flicker of the left header toolbar
					getHeaderForm().getForm().setRedraw(false);

					Form form = getHeaderForm().getForm().getForm();
					form.setMessage(message, type);
					if (messageHyperLinkListener != null) {
						form.removeMessageHyperlinkListener(messageHyperLinkListener);
					}
					if (listener != null) {
						form.addMessageHyperlinkListener(listener);
					}
					messageHyperLinkListener = listener;

					// make sure the busyLabel image is large enough to accommodate the tool bar
					if (hasLeftToolBar()) {
						if (message != null && busyLabel != null && hasLeftToolBar()) {
							setHeaderImage(busyLabel.getImage());
						} else {
							updateHeaderImage();
						}
					}
				} finally {
					getHeaderForm().getForm().setRedraw(true);
				}
			}
		}
	}

	private void setHeaderImage(final Image image) {
		if (busyLabel == null) {
			return;
		}

		final Point size = leftToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);

		// padding between toolbar and image, ensure image is at least one pixel wide to avoid SWT error 
		final int padding = (size.x > 0 && !noExtraPadding) ? 10 : 1;
		final Rectangle imageBounds = (image != null) ? image.getBounds() : new Rectangle(0, 0, 0, 0);
		int tempHeight = (image != null) ? Math.max(size.y + 1, imageBounds.height) : size.y + 1;
		// avoid extra padding due to margin added by TitleRegion.VMARGIN
		final int height = (tempHeight > imageBounds.height + 5) ? tempHeight - 5 : tempHeight;

		CompositeImageDescriptor descriptor = new CompositeImageDescriptor() {
			@Override
			protected void drawCompositeImage(int width, int height) {
				if (image != null) {
					drawImage(image.getImageData(), size.x + padding, (height - image.getBounds().height) / 2);
				}
			}

			@Override
			protected Point getSize() {
				return new Point(size.x + padding + imageBounds.width, height);
			}

		};
		Image newHeaderImage = descriptor.createImage();

		// directly set on busyLabel since getHeaderForm().getForm().setImage() does not update 
		// the image if a message is currently displayed
		busyLabel.setImage(newHeaderImage);

		if (headerImage != null) {
			headerImage.dispose();
		}
		headerImage = newHeaderImage;

		// avoid extra padding due to large title font
		// TODO reset font in case tool bar is empty
		leftToolBar.getParent().setFont(JFaceResources.getDefaultFont());
		getHeaderForm().getForm().reflow(true);
	}

	/**
	 * @since 3.1
	 */
	public String getMessage() {
		if (getHeaderForm() != null && getHeaderForm().getForm() != null) {
			if (!getHeaderForm().getForm().isDisposed()) {
				Form form = getHeaderForm().getForm().getForm();
				return form.getMessage();
			}
		}
		return null;
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
		if (editorBusyIndicator != null) {
			if (busy) {
				if (TasksUiInternal.isAnimationsEnabled()) {
					editorBusyIndicator.start();
				}
			} else {
				editorBusyIndicator.stop();
			}
		}

		if (getHeaderForm() != null && getHeaderForm().getForm() != null && !getHeaderForm().getForm().isDisposed()) {
			Form form = getHeaderForm().getForm().getForm();
			if (form != null && !form.isDisposed()) {
				// TODO consider only disabling certain actions 
				IToolBarManager toolBarManager = form.getToolBarManager();
				if (toolBarManager instanceof ToolBarManager) {
					ToolBar control = ((ToolBarManager) toolBarManager).getControl();
					if (control != null) {
						control.setEnabled(!busy);
					}
				}

				if (leftToolBar != null) {
					leftToolBar.setEnabled(!busy);
				}

				CommonUiUtil.setEnabled(form.getBody(), !busy);
				for (IFormPage page : getPages()) {
					if (page instanceof WorkbenchPart) {
						WorkbenchPart part = (WorkbenchPart) page;
						part.showBusy(busy);
					}
				}
			}
		}
	}

	private void updateHeader() {
		IEditorInput input = getEditorInput();
		updateHeaderImage();
		updateHeaderLabel();
		setTitleToolTip(input.getToolTipText());
		setPartName(input.getName());
	}

	/**
	 * @since 3.0
	 */
	public void updateHeaderToolBar() {
		final Form form = getHeaderForm().getForm().getForm();
		toolBarManager = form.getToolBarManager();

		toolBarManager.removeAll();
		toolBarManager.update(true);

		TaskRepository outgoingNewRepository = TasksUiUtil.getOutgoingNewTaskRepository(task);
		final TaskRepository taskRepository = (outgoingNewRepository != null) ? outgoingNewRepository
				: taskEditorInput.getTaskRepository();
		ControlContribution repositoryLabelControl = new ControlContribution(Messages.AbstractTaskEditorPage_Title) {
			@Override
			protected Control createControl(Composite parent) {
				FormToolkit toolkit = getHeaderForm().getToolkit();
				Composite composite = toolkit.createComposite(parent);
				composite.setLayout(new RowLayout());
				composite.setBackground(null);
				String label = taskRepository.getRepositoryLabel();
				if (label.indexOf("//") != -1) { //$NON-NLS-1$
					label = label.substring((taskRepository.getRepositoryUrl().indexOf("//") + 2)); //$NON-NLS-1$
				}

				Hyperlink link = new Hyperlink(composite, SWT.NONE);
				link.setText(label);
				link.setFont(JFaceResources.getBannerFont());
				link.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				link.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						TasksUiUtil.openEditRepositoryWizard(taskRepository);
					}
				});

				return composite;
			}
		};
		toolBarManager.add(repositoryLabelControl);

		toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		toolBarManager.add(new Separator("page")); //$NON-NLS-1$
		for (IFormPage page : getPages()) {
			if (page instanceof TaskFormPage) {
				TaskFormPage taskEditorPage = (TaskFormPage) page;
				taskEditorPage.fillToolBar(toolBarManager);
			}
		}

		final String taskUrl = task.getUrl();
		if (taskUrl != null && taskUrl.length() > 0) {
			Action openWithBrowserAction = new Action() {
				@Override
				public void run() {
					TasksUiUtil.openUrl(taskUrl);
				}
			};
			openWithBrowserAction.setImageDescriptor(CommonImages.BROWSER_OPEN_TASK);
			openWithBrowserAction.setToolTipText(Messages.AbstractTaskEditorPage_Open_with_Web_Browser);
			toolBarManager.add(openWithBrowserAction);
		}

		if (activateAction == null) {
			activateAction = new ToggleTaskActivationAction(task) {
				@Override
				public void run() {
					TaskList taskList = TasksUiPlugin.getTaskList();
					if (taskList.getTask(task.getRepositoryUrl(), task.getTaskId()) == null) {
						setMessage(Messages.TaskEditor_Task_added_to_the_Uncategorized_container,
								IMessageProvider.INFORMATION);
					}
					super.run();
				}
			};
		}
		toolBarManager.add(new Separator("planning")); //$NON-NLS-1$
		toolBarManager.add(new TaskEditorScheduleAction(task));

		toolBarManager.add(new Separator("activation")); //$NON-NLS-1$
		toolBarManager.add(activateAction);

		// add external contributions
		menuService = (IMenuService) getSite().getService(IMenuService.class);
		if (menuService != null && toolBarManager instanceof ContributionManager) {
			menuService.populateContributionManager((ContributionManager) toolBarManager, "toolbar:" //$NON-NLS-1$
					+ ID_TOOLBAR_HEADER + "." + taskRepository.getConnectorKind()); //$NON-NLS-1$
		}

		toolBarManager.update(true);

		// XXX move this call
		updateLeftHeaderToolBar();
		updateHeader();
	}

	private void updateLeftHeaderToolBar() {
		leftToolBarManager.removeAll();
		leftToolBarManager.update(true);

		leftToolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		leftToolBarManager.add(new Separator("page")); //$NON-NLS-1$

		initialLeftToolbarSize = leftToolBarManager.getSize();

		for (IFormPage page : getPages()) {
			if (page instanceof AbstractTaskEditorPage) {
				AbstractTaskEditorPage taskEditorPage = (AbstractTaskEditorPage) page;
				taskEditorPage.fillLeftHeaderToolBar(leftToolBarManager);
			} else if (page instanceof TaskPlanningEditor) {
				TaskPlanningEditor taskEditorPage = (TaskPlanningEditor) page;
				taskEditorPage.fillLeftHeaderToolBar(leftToolBarManager);
			}
		}

		// add external contributions
		menuService = (IMenuService) getSite().getService(IMenuService.class);
		if (menuService != null && leftToolBarManager instanceof ContributionManager) {
			TaskRepository outgoingNewRepository = TasksUiUtil.getOutgoingNewTaskRepository(task);
			TaskRepository taskRepository = (outgoingNewRepository != null) ? outgoingNewRepository
					: taskEditorInput.getTaskRepository();
			menuService.populateContributionManager(leftToolBarManager, "toolbar:" + ID_LEFT_TOOLBAR_HEADER + "." //$NON-NLS-1$ //$NON-NLS-2$
					+ taskRepository.getConnectorKind());
		}

		leftToolBarManager.update(true);

		if (hasLeftToolBar()) {
			// XXX work around a bug in Gtk that causes the toolbar size to be incorrect if no
			// tool bar buttons are contributed
			if (leftToolBar != null) {
				Point size = leftToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
				boolean changed = false;
				for (Control control : leftToolBar.getChildren()) {
					final Point childSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
					if (childSize.y > size.y) {
						size.y = childSize.y;
						changed = true;
					}
				}
				if (changed) {
					leftToolBar.setSize(size);
				}
			}

			if (PlatformUtil.isToolBarHeightBroken(leftToolBar)) {
				ToolItem item = new ToolItem(leftToolBar, SWT.NONE);
				item.setEnabled(false);
				item.setImage(CommonImages.getImage(CommonImages.BLANK));
				item.setWidth(1);
				noExtraPadding = true;
			} else if (PlatformUtil.needsToolItemToForceToolBarHeight()) {
				ToolItem item = new ToolItem(leftToolBar, SWT.NONE);
				item.setEnabled(false);
				final Image image = new Image(item.getDisplay(), CommonImages.getImage(CommonImages.BLANK)
						.getImageData()
						.scaledTo(1, 22));
				item.setImage(image);
				item.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						image.dispose();
					}
				});
				item.setWidth(1);
				noExtraPadding = true;
			}
		}
	}

	private void updateHeaderImage() {
		if (hasLeftToolBar()) {
			setHeaderImage(null);
		} else {
			String connectorKind;
			TaskRepository outgoingNewRepository = TasksUiUtil.getOutgoingNewTaskRepository(task);
			if (outgoingNewRepository != null) {
				connectorKind = outgoingNewRepository.getConnectorKind();
			} else {
				connectorKind = task.getConnectorKind();
			}

			if (LocalRepositoryConnector.CONNECTOR_KIND.equals(connectorKind)) {
				getHeaderForm().getForm().setImage(CommonImages.getImage(TasksUiImages.TASK));
			} else {
				ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(connectorKind);
				Image image = CommonImages.getImageWithOverlay(TasksUiImages.REPOSITORY, overlay, false, false);
				getHeaderForm().getForm().setImage(image);
			}
		}
	}

	private boolean hasLeftToolBar() {
		return leftToolBar != null && leftToolBarManager != null
				&& leftToolBarManager.getSize() > initialLeftToolbarSize;
	}

	private void updateHeaderLabel() {
		if (hasLeftToolBar()) {
			getHeaderForm().getForm().setText(null);
		} else {
			String connectorKind;
			TaskRepository outgoingNewRepository = TasksUiUtil.getOutgoingNewTaskRepository(task);
			if (outgoingNewRepository != null) {
				connectorKind = outgoingNewRepository.getConnectorKind();
			} else {
				connectorKind = task.getConnectorKind();
			}

			if (connectorKind.equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
				getHeaderForm().getForm().setText(Messages.TaskEditor_Task_ + task.getSummary());
			} else {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(connectorKind);
				String kindLabel = ""; //$NON-NLS-1$
				if (connectorUi != null) {
					kindLabel = connectorUi.getTaskKindLabel(task);
				}

				String idLabel = task.getTaskKey();
				if (idLabel != null) {
					getHeaderForm().getForm().setText(kindLabel + " " + idLabel); //$NON-NLS-1$
				} else {
					getHeaderForm().getForm().setText(kindLabel);
				}
			}
		}
	}

	/**
	 * Update the title of the editor.
	 * 
	 * @deprecated use {@link #updateHeaderToolBar()} instead
	 */
	@Deprecated
	public void updateTitle(String name) {
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
//		} else if (getEditorInput() instanceof AbstractRepositoryTaskEditorInput) {
//			setTitleImage(CommonImages.getImage(TasksUiImages.TASK_REMOTE));
		} else {
			setTitleImage(CommonImages.getImage(TasksUiImages.TASK));
		}
	}

}
