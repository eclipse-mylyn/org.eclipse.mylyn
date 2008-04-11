/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener2;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.internal.context.ui.actions.ContextRetrieveAction;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.PlanningPerspectiveFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Main entry point for the Context UI.
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 2.0
 */
public class ContextUiPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.context.ui";

	private class ContextActivationListener implements IInteractionContextListener2 {

		public void contextPreActivated(IInteractionContext context) {
			initLazyStart();
		}

		public void elementsDeleted(List<IInteractionElement> elements) {
			// ignore

		}

		public void contextActivated(IInteractionContext context) {
			// ignore

		}

		public void contextCleared(IInteractionContext context) {
			// ignore

		}

		public void contextDeactivated(IInteractionContext context) {
			// ignore

		}

		public void elementDeleted(IInteractionElement element) {
			// ignore

		}

		public void interestChanged(List<IInteractionElement> elements) {
			// ignore

		}

		public void landmarkAdded(IInteractionElement element) {
			// ignore

		}

		public void landmarkRemoved(IInteractionElement element) {
			// ignore

		}

		public void relationsChanged(IInteractionElement element) {
			// ignore

		}

	}

	private final ContextActivationListener contextActivationListener = new ContextActivationListener();

	private final Map<String, AbstractContextUiBridge> bridges = new HashMap<String, AbstractContextUiBridge>();

	private final Map<String, ILabelProvider> contextLabelProviders = new HashMap<String, ILabelProvider>();

	private static ContextUiPlugin INSTANCE;

	private HighlighterList highlighters = null;

	private FocusedViewerManager viewerManager;

	private final ContextPerspectiveManager perspectiveManager = new ContextPerspectiveManager();

	private final ContentOutlineManager contentOutlineManager = new ContentOutlineManager();

	private final Map<AbstractContextUiBridge, ImageDescriptor> activeSearchIcons = new HashMap<AbstractContextUiBridge, ImageDescriptor>();

	private final Map<AbstractContextUiBridge, String> activeSearchLabels = new HashMap<AbstractContextUiBridge, String>();

	private final Map<String, Set<Class<?>>> preservedFilterClasses = new HashMap<String, Set<Class<?>>>();

	private final Map<String, Set<String>> preservedFilterIds = new HashMap<String, Set<String>>();

	private static final AbstractContextLabelProvider DEFAULT_LABEL_PROVIDER = new AbstractContextLabelProvider() {

		@Override
		protected Image getImage(IInteractionElement node) {
			return null;
		}

		@Override
		protected Image getImage(IInteractionRelation edge) {
			return null;
		}

		@Override
		protected String getText(IInteractionElement node) {
			return "? " + node;
		}

		@Override
		protected String getText(IInteractionRelation edge) {
			return "? " + edge;
		}

		@Override
		protected Image getImageForObject(Object object) {
			return null;
		}

		@Override
		protected String getTextForObject(Object node) {
			return "? " + node;
		}

	};

	private static final AbstractContextUiBridge DEFAULT_UI_BRIDGE = new AbstractContextUiBridge() {

		@Override
		public void open(IInteractionElement node) {
			// ignore
		}

		@Override
		public void close(IInteractionElement node) {
			// ignore
		}

		@Override
		public boolean acceptsEditor(IEditorPart editorPart) {
			return false;
		}

		@Override
		public List<TreeViewer> getContentOutlineViewers(IEditorPart editor) {
			return Collections.emptyList();
		}

		@Override
		public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
			return null;
		}

		@Override
		public IInteractionElement getElement(IEditorInput input) {
			return null;
		}

		@Override
		public String getContentType() {
			return null;
		}
	};

	private static final ITaskActivityListener TASK_ACTIVATION_LISTENER = new TaskActivityAdapter() {

		public void activityChanged() {
			// ignore
		}

		public void taskActivated(AbstractTask task) {
			boolean hasLocalContext = ContextCore.getContextManager().hasContext(task.getHandleIdentifier());
			if (!hasLocalContext) {
				AbstractTask repositoryTask = task;
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
						repositoryTask);
				TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
						repositoryTask.getRepositoryUrl());

				if (connector != null && connector.getAttachmentHandler() != null
						&& AttachmentUtil.hasContext(repository, repositoryTask)) {
					boolean getRemote = MessageDialog.openQuestion(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getShell(), ITasksUiConstants.TITLE_DIALOG,
							"No local task context exists.  Retrieve from repository?");
					if (getRemote) {
						new ContextRetrieveAction().run(repositoryTask);
					}
				}
			}
		}

		public void taskDeactivated(AbstractTask task) {
			// ignore

		}

		public void taskListRead() {
			// ignore

		}
	};

	private final AtomicBoolean lazyStarted = new AtomicBoolean(false);

	public ContextUiPlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		ContextCore.getContextManager().addListener(contextActivationListener);
		if (ContextCore.getContextManager().isContextActive()) {
			initLazyStart();
		}

		initializeDefaultPreferences(getPreferenceStore());
		initializeHighlighters();

		viewerManager = new FocusedViewerManager();
		perspectiveManager.addManagedPerspective(PlanningPerspectiveFactory.ID_PERSPECTIVE);
	}

	private void initLazyStart() {
		if (!lazyStarted.getAndSet(true)) {
			IWorkbench workbench = PlatformUI.getWorkbench();
			try {
				lazyStart(workbench);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, super.getBundle().getSymbolicName(), IStatus.ERROR,
						"Could not lazy start context plug-in", t));
			}
			if (TasksUi.getTaskListManager() != null) {
				ContextCore.getContextManager().removeListener(contextActivationListener);
			}
		}
	}

	private void lazyStart(IWorkbench workbench) {
		try {
			ContextCore.getContextManager().addListener(viewerManager);
			MonitorUiPlugin.getDefault().addWindowPartListener(contentOutlineManager);

			// NOTE: can't init within this class because ..mylyn.tasks.ui activation will be triggered on activation
			ContextHighlighterInitializer.init();

			TasksUi.getTaskListManager().addActivityListener(perspectiveManager);
			MonitorUiPlugin.getDefault().addWindowPerspectiveListener(perspectiveManager);
			TasksUi.getTaskListManager().addActivityListener(TASK_ACTIVATION_LISTENER);
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Context UI initialization failed",
					e));
		}

		// activate all UI bridges and load all focused view actions before setting the selections (see below)
		UiStartupExtensionPointReader.runStartupExtensions();

		try {
			// NOTE: this needs to be done because some views (e.g. Project Explorer) are not
			// correctly initialized on startup and do not have the dummy selection event
			// sent to them.  See PartPluginAction and bug 213545.
			// API-3.0: consider a mechanism to identify only views that provide focus
			for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				if (window.getActivePage() != null) {
					IViewReference[] views = window.getActivePage().getViewReferences();
					for (IViewReference viewReference : views) {
						IViewPart viewPart = viewReference.getView(false);
						if (viewPart != null) {
							UiUtil.initializeViewerSelection(viewPart);
						}
					}
				}
			}
			viewerManager.forceReferesh();
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"Could not initialize focused viewers", e));
		}
	}

	private void lazyStop() {
		ContextCore.getContextManager().removeListener(viewerManager);
		MonitorUiPlugin.getDefault().removeWindowPartListener(contentOutlineManager);

		TasksUi.getTaskListManager().removeActivityListener(perspectiveManager);
		MonitorUiPlugin.getDefault().removeWindowPerspectiveListener(perspectiveManager);
		TasksUi.getTaskListManager().removeActivityListener(TASK_ACTIVATION_LISTENER);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		if (lazyStarted.get()) {
			lazyStop();
		}
		if (TasksUi.getTaskListManager() != null) {
			ContextCore.getContextManager().removeListener(contextActivationListener);
		}

		super.stop(context);
		perspectiveManager.removeManagedPerspective(PlanningPerspectiveFactory.ID_PERSPECTIVE);
		viewerManager.dispose();
		highlighters.dispose();
	}

	private void initializeHighlighters() {
		String hlist = getPreferenceStore().getString(ContextUiPrefContstants.HIGHLIGHTER_PREFIX);
		if (hlist != null && hlist.length() != 0) {
			highlighters = new HighlighterList(hlist);
		} else {
			highlighters = new HighlighterList();
			highlighters.setToDefaultList();
			getPreferenceStore().setValue(ContextUiPrefContstants.HIGHLIGHTER_PREFIX,
					this.highlighters.externalizeToString());
		}
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE, true);

		store.setDefault(ContextUiPrefContstants.AUTO_MANAGE_PERSPECTIVES, false);
		store.setDefault(ContextUiPrefContstants.AUTO_MANAGE_EDITORS, true);
		store.setDefault(ContextUiPrefContstants.AUTO_MANAGE_EXPANSION, true);
		store.setDefault(ContextUiPrefContstants.AUTO_MANAGE_EDITOR_CLOSE_ACTION, true);
		store.setDefault(ContextUiPrefContstants.AUTO_MANAGE_EDITOR_CLOSE_WARNING, true);

		store.setDefault(ContextUiPrefContstants.GAMMA_SETTING_LIGHTENED, false);
		store.setDefault(ContextUiPrefContstants.GAMMA_SETTING_STANDARD, true);
		store.setDefault(ContextUiPrefContstants.GAMMA_SETTING_DARKENED, false);
	}

	public void setHighlighterMapping(String id, String name) {
		String prefId = ContextUiPrefContstants.TASK_HIGHLIGHTER_PREFIX + id;
		getPreferenceStore().putValue(prefId, name);
	}

	/**
	 * Returns the shared instance.
	 */
	public static ContextUiPlugin getDefault() {
		return INSTANCE;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	@Deprecated
	public static String getResourceString(String key) {
		ResourceBundle bundle = ContextUiPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	@Deprecated
	public static String getMessage(String key) {
		ResourceBundle bundle = getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	@Deprecated
	public ResourceBundle getResourceBundle() {
		return null;
	}

	public List<AbstractContextUiBridge> getUiBridges() {
		UiExtensionPointReader.initExtensions();
		return new ArrayList<AbstractContextUiBridge>(bridges.values());
	}

	/**
	 * @return the corresponding adapter if found, or an adapter with no behavior otherwise (so null is never returned)
	 */
	public AbstractContextUiBridge getUiBridge(String contentType) {
		UiExtensionPointReader.initExtensions();
		AbstractContextUiBridge bridge = bridges.get(contentType);
		if (bridge != null) {
			return bridge;
		} else {
			return DEFAULT_UI_BRIDGE;
		}
	}

	/**
	 * TODO: cache this to improve performance?
	 */
	public AbstractContextUiBridge getUiBridgeForEditor(IEditorPart editorPart) {
		UiExtensionPointReader.initExtensions();
		AbstractContextUiBridge foundBridge = null;
		for (AbstractContextUiBridge bridge : bridges.values()) {
			if (bridge.acceptsEditor(editorPart)) {
				foundBridge = bridge;
				break;
			}
		}
		if (foundBridge != null) {
			return foundBridge;
		} else {
			return DEFAULT_UI_BRIDGE;
		}
	}

	private void internalAddBridge(String extension, AbstractContextUiBridge bridge) {
		this.bridges.put(extension, bridge);
	}

	public ILabelProvider getContextLabelProvider(String extension) {
		// if (!UiExtensionPointReader.extensionsRead)
		// UiExtensionPointReader.initExtensions();
		ILabelProvider provider = contextLabelProviders.get(extension);
		if (provider != null) {
			return provider;
		} else {
			return DEFAULT_LABEL_PROVIDER;
		}
	}

	private void internalAddContextLabelProvider(String extension, ILabelProvider provider) {
		this.contextLabelProviders.put(extension, provider);
	}

	/**
	 * @return null if not found
	 */
	public Highlighter getHighlighter(String name) {
		if (highlighters == null) {
			this.initializeHighlighters();
		}
		return highlighters.getHighlighter(name);
	}

	/**
	 * API-3.0: remove
	 */
	@Deprecated
	public Highlighter getHighlighterForContextId(String id) {
		String prefId = ContextUiPrefContstants.TASK_HIGHLIGHTER_PREFIX + id;
		String highlighterName = getPreferenceStore().getString(prefId);
		return getHighlighter(highlighterName);
	}

	public HighlighterList getHighlighterList() {
		if (this.highlighters == null) {
			this.initializeHighlighters();
		}
		return this.highlighters;
	}

	public List<Highlighter> getHighlighters() {
		if (highlighters == null) {
			this.initializeHighlighters();
		}
		return highlighters.getHighlighters();
	}

	public static FocusedViewerManager getViewerManager() {
		return INSTANCE.viewerManager;
	}

	static class UiExtensionPointReader {

		private static boolean extensionsRead = false;

		private static UiExtensionPointReader thisReader = new UiExtensionPointReader();

		public static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylyn.context.ui.bridges";

		public static final String ELEMENT_UI_BRIDGE = "uiBridge";

		public static final String ELEMENT_PRESERVED_FILTERS = "preservedFilters";

		public static final String ELEMENT_VIEW_ID = "viewId";

		public static final String ELEMENT_ID = "id";

		public static final String ELEMENT_FILTER = "filter";

		public static final String ELEMENT_CLASS = "class";

		public static final String ELEMENT_UI_CONTEXT_LABEL_PROVIDER = "labelProvider";

		public static final String ELEMENT_UI_BRIDGE_CONTENT_TYPE = "contentType";

		public static final String ELEMENT_STRUCTURE_BRIDGE_SEARCH_ICON = "activeSearchIcon";

		public static final String ELEMENT_STRUCTURE_BRIDGE_SEARCH_LABEL = "activeSearchLabel";

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry.getExtensionPoint(UiExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (IExtension extension : extensions) {
					IConfigurationElement[] elements = extension.getConfigurationElements();
					for (IConfigurationElement element : elements) {
						if (element.getName().equals(UiExtensionPointReader.ELEMENT_UI_BRIDGE)) {
							readBridge(element);
						} else if (element.getName().equals(UiExtensionPointReader.ELEMENT_UI_CONTEXT_LABEL_PROVIDER)) {
							readLabelProvider(element);
						} else if (element.getName().equals(UiExtensionPointReader.ELEMENT_PRESERVED_FILTERS)) {
							readPreservedFilters(element);
						}
					}
				}
				extensionsRead = true;
			}
		}

		private static void readLabelProvider(IConfigurationElement element) {
			try {
				Object provider = element.createExecutableExtension(UiExtensionPointReader.ELEMENT_CLASS);
				Object contentType = element.getAttribute(UiExtensionPointReader.ELEMENT_UI_BRIDGE_CONTENT_TYPE);
				if (provider instanceof ILabelProvider && contentType != null) {
					ContextUiPlugin.getDefault().internalAddContextLabelProvider((String) contentType,
							(ILabelProvider) provider);
				} else {
					StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
							"Could not load label provider: " + provider.getClass().getCanonicalName()
									+ " must implement " + ILabelProvider.class.getCanonicalName()));
				}
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Could not load label provider extension", e));
			}
		}

		private static void readPreservedFilters(IConfigurationElement element) {
			String viewId = element.getAttribute(UiExtensionPointReader.ELEMENT_VIEW_ID);
			IConfigurationElement[] children = element.getChildren();
			for (IConfigurationElement child : children) {
				if (child.getName().equals(UiExtensionPointReader.ELEMENT_FILTER)) {
					try {
						Object filterClass = child.createExecutableExtension(UiExtensionPointReader.ELEMENT_CLASS);
						ContextUiPlugin.getDefault().addPreservedFilterClass(viewId, (ViewerFilter) filterClass);
					} catch (Exception e) {
						String filterId = child.getAttribute(ELEMENT_ID);
						ContextUiPlugin.getDefault().addPreservedFilterId(viewId, filterId);
					}
				}
			}
		}

		@SuppressWarnings("deprecation")
		private static void readBridge(IConfigurationElement element) {
			try {
				Object bridge = element.createExecutableExtension(UiExtensionPointReader.ELEMENT_CLASS);
				Object contentType = element.getAttribute(UiExtensionPointReader.ELEMENT_UI_BRIDGE_CONTENT_TYPE);
				if (bridge instanceof AbstractContextUiBridge && contentType != null) {
					ContextUiPlugin.getDefault().internalAddBridge((String) contentType,
							(AbstractContextUiBridge) bridge);

					String iconPath = element.getAttribute(ELEMENT_STRUCTURE_BRIDGE_SEARCH_ICON);
					if (iconPath != null) {
						ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
								element.getDeclaringExtension().getNamespace(), iconPath);
						if (descriptor != null) {
							ContextUiPlugin.getDefault().setActiveSearchIcon((AbstractContextUiBridge) bridge,
									descriptor);
						}
					}
					String label = element.getAttribute(ELEMENT_STRUCTURE_BRIDGE_SEARCH_LABEL);
					if (label != null) {
						ContextUiPlugin.getDefault().setActiveSearchLabel((AbstractContextUiBridge) bridge, label);
					}

				} else {
					StatusHandler.log("Could not load bridge: " + bridge.getClass().getCanonicalName()
							+ " must implement " + AbstractContextUiBridge.class.getCanonicalName(), thisReader);
				}
			} catch (CoreException e) {
				StatusHandler.log(e, "Could not load bridge extension");
			}
		}
	}

	static class UiStartupExtensionPointReader {

		private static final String EXTENSION_ID_STARTUP = "org.eclipse.mylyn.context.ui.startup";

		private static final String ELEMENT_STARTUP = "startup";

		private static final String ELEMENT_CLASS = "class";

		public static void runStartupExtensions() {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_STARTUP);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (IExtension extension : extensions) {
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().compareTo(ELEMENT_STARTUP) == 0) {
						runStartupExtension(element);
					}
				}
			}
		}

		private static void runStartupExtension(IConfigurationElement configurationElement) {
			try {
				Object object = WorkbenchPlugin.createExtension(configurationElement, ELEMENT_CLASS);
				if (!(object instanceof IContextUiStartup)) {
					StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not : "
							+ object.getClass().getCanonicalName() + " must implement "
							+ AbstractContextStructureBridge.class.getCanonicalName()));
					return;
				}

				IContextUiStartup startup = (IContextUiStartup) object;
				startup.lazyStartup();
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Could not load startup extension", e));
			}
		}

	}

	/**
	 * @param task
	 *            can be null to indicate no task
	 */
	public String getPerspectiveIdFor(AbstractTask task) {
		if (task != null) {
			return getPreferenceStore().getString(
					ContextUiPrefContstants.PREFIX_TASK_TO_PERSPECTIVE + task.getHandleIdentifier());
		} else {
			return getPreferenceStore().getString(ContextUiPrefContstants.PERSPECTIVE_NO_ACTIVE_TASK);
		}
	}

	/**
	 * @param task
	 *            can be null to indicate no task
	 */
	public void setPerspectiveIdFor(AbstractTask task, String perspectiveId) {
		if (task != null) {
			getPreferenceStore().setValue(
					ContextUiPrefContstants.PREFIX_TASK_TO_PERSPECTIVE + task.getHandleIdentifier(), perspectiveId);
		} else {
			getPreferenceStore().setValue(ContextUiPrefContstants.PERSPECTIVE_NO_ACTIVE_TASK, perspectiveId);
		}
	}

	private void setActiveSearchIcon(AbstractContextUiBridge bridge, ImageDescriptor descriptor) {
		activeSearchIcons.put(bridge, descriptor);
	}

	public ImageDescriptor getActiveSearchIcon(AbstractContextUiBridge bridge) {
		UiExtensionPointReader.initExtensions();
		return activeSearchIcons.get(bridge);
	}

	private void setActiveSearchLabel(AbstractContextUiBridge bridge, String label) {
		activeSearchLabels.put(bridge, label);
	}

	public String getActiveSearchLabel(AbstractContextUiBridge bridge) {
		UiExtensionPointReader.initExtensions();
		return activeSearchLabels.get(bridge);
	}

	public void addPreservedFilterClass(String viewId, ViewerFilter filter) {
		Set<Class<?>> preservedList = preservedFilterClasses.get(viewId);
		if (preservedList == null) {
			preservedList = new HashSet<Class<?>>();
			preservedFilterClasses.put(viewId, preservedList);
		}
		preservedList.add(filter.getClass());
	}

	public Set<Class<?>> getPreservedFilterClasses(String viewId) {
		UiExtensionPointReader.initExtensions();
		if (preservedFilterClasses.containsKey(viewId)) {
			return preservedFilterClasses.get(viewId);
		} else {
			return Collections.emptySet();
		}
	}

	public void addPreservedFilterId(String viewId, String filterId) {
		Set<String> preservedList = preservedFilterIds.get(viewId);
		if (preservedList == null) {
			preservedList = new HashSet<String>();
			preservedFilterIds.put(viewId, preservedList);
		}
		preservedList.add(filterId);
	}

	public Set<String> getPreservedFilterIds(String viewId) {
		UiExtensionPointReader.initExtensions();
		if (preservedFilterIds.containsKey(viewId)) {
			return preservedFilterIds.get(viewId);
		} else {
			return Collections.emptySet();
		}
	}

	public static ContextPerspectiveManager getPerspectiveManager() {
		return INSTANCE.perspectiveManager;
	}
}
