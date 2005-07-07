/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.resources.ResourceSelectionMonitor;
import org.eclipse.mylar.core.resources.ResourceStructureBridge;
import org.eclipse.mylar.ui.actions.FilterNavigatorAction;
import org.eclipse.mylar.ui.actions.FilterOutlineAction;
import org.eclipse.mylar.ui.actions.FilterProblemsListAction;
import org.eclipse.mylar.ui.internal.MylarWorkingSetUpdater;
import org.eclipse.mylar.ui.internal.UiUpdateManager;
import org.eclipse.mylar.ui.internal.UiUtil;
import org.eclipse.mylar.ui.internal.ViewerConfigurationManager;
import org.eclipse.mylar.ui.internal.views.Highlighter;
import org.eclipse.mylar.ui.internal.views.HighlighterList;
import org.eclipse.mylar.ui.internal.views.ProblemsListInterestFilter;
import org.eclipse.mylar.ui.internal.views.ProblemsListLabelProvider;
import org.eclipse.mylar.ui.resources.NavigatorRefreshListener;
import org.eclipse.mylar.ui.resources.ResourceUiBridge;
import org.eclipse.mylar.ui.views.ActiveSearchView;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;


/**
 * @author Mik Kersten
 */
public class MylarUiPlugin extends AbstractUIPlugin implements IStartup {

    private Map<String, IMylarUiBridge> bridges = new HashMap<String, IMylarUiBridge>();
    private static MylarUiPlugin plugin;
	private ResourceBundle resourceBundle;
    private boolean decorateInterestMode = false;
    public static final String MARKER_LANDMARK = "org.eclipse.mylar.ui.interest.landmark";
    
    private static final String TASK_HIGHLIGHTER_PREFIX = "org.eclipse.mylar.ui.interest.highlighters.task.";
    public static final String HIGHLIGHTER_PREFIX = "org.eclipse.mylar.ui.interest.highlighters";
    public static final String GAMMA_SETTING_DARKENED = "org.eclipse.mylar.ui.gamma.darkened";
    public static final String GAMMA_SETTING_STANDARD = "org.eclipse.mylar.ui.gamma.standard";
    public static final String GAMMA_SETTING_LIGHTENED = "org.eclipse.mylar.ui.gamma.lightened";
    public static final String GLOBAL_FILTERING = "org.eclipse.mylar.ui.interest.filter.global";
    public static final String INTERSECTION_MODE = "org.eclipse.mylar.ui.interest.intersection";    
    
    private HighlighterList highlighters = null;
    private Highlighter intersectionHighlighter;
    private ColorMap colorMap = new ColorMap(); 
    
    private List<MylarWorkingSetUpdater> workingSetUpdaters = null;
    
    protected ProblemsListInterestFilter interestFilter = new ProblemsListInterestFilter();

    private ViewerConfigurationManager viewerManager = new ViewerConfigurationManager();
    
    private NavigatorRefreshListener navigatorRefreshListener = new NavigatorRefreshListener();

    private static final IMylarUiBridge DEFAULT_UI_BRIDGE = new IMylarUiBridge() {

        public void open(ITaskscapeNode node) {
//            throw new RuntimeException("null adapter: " + node);
        }

        public void close(ITaskscapeNode node) {
//            throw new RuntimeException("null adapter: " + node);
        }

        public ILabelProvider getLabelProvider() {
            return NULL_LABEL_PROVIDER;
        }

        public boolean acceptsEditor(IEditorPart editorPart) {
            return false;
        }

        public List<TreeViewer> getTreeViewers(IEditorPart editor) {
            return Collections.emptyList();
        }

        public void refreshOutline(Object element, boolean updateLabels) {
        }

        public ImageDescriptor getIconForRelationship(String relationshipHandle) {
            return null;
        }
        
        public String getNameForRelationship(String relationshipHandle) { 
            return null;
        }
    };
    
    private static final ILabelProvider NULL_LABEL_PROVIDER = new ILabelProvider() {

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            return "" + element;
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }
        
    };
    
    protected UiUpdateManager uiUpdateManager = new UiUpdateManager();
    
    public MylarUiPlugin() {
		super();
		plugin = this;
        try {
            resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.MylarUiPluginResources");            
        } catch (MissingResourceException x) {
            resourceBundle = null;
        } catch (Throwable t) {
        	MylarPlugin.log(t, "plug-in init failed");      
        }
        initializeHighlighters();
        initializeDefaultPreferences(getPrefs());
        initializeActions();
	} 

    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
                MylarPlugin.getTaskscapeManager().addListener(uiUpdateManager);
                
                Workbench.getInstance().getActiveWorkbenchWindow().getPartService().addPartListener(viewerManager);
        		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
        		for (int i= 0; i < windows.length; i++) {
        			windows[i].addPageListener(viewerManager);
//        			IWorkbenchPage[] pages= windows[i].getPages();
//        			for (int j= 0; j < pages.length; j++) {
//        				pages[j].addPartListener(viewerManager);
//        			}
        		}
        		
                setupProblemsView();
                
                MylarPlugin.getTaskscapeManager().addListener(navigatorRefreshListener);
                MylarUiPlugin.getDefault().addAdapter(ResourceStructureBridge.EXTENSION, new ResourceUiBridge());
                
                MylarPlugin.getDefault().getSelectionMonitors().add(new ResourceSelectionMonitor());
                
                if (FilterNavigatorAction.getDefault() != null) FilterNavigatorAction.getDefault().update();
                if (FilterOutlineAction.getDefault() != null) FilterOutlineAction.getDefault().update();
                if (FilterProblemsListAction.getDefault() != null) FilterProblemsListAction.getDefault().update();
            }
        });
    }
    
    private void initializeActions() {
        // don't have any actions to initialize
    }
    
    protected void setupProblemsView() {
        TableViewer viewer = UiUtil.getProblemViewFromActivePerspective();
        if (viewer != null) {
            viewer.setLabelProvider(new ProblemsListLabelProvider(
                    (TableViewLabelProvider)viewer.getLabelProvider()));
        }
    }

    private void initializeHighlighters() {
        String hlist = getPrefs().getString(MylarUiPlugin.HIGHLIGHTER_PREFIX);
        if (hlist !=null && hlist.length() != 0) {  
            highlighters = new HighlighterList(hlist);
        } else {            
            // Only get here if it is the first time running
            // mylar. load default colors
            highlighters = new HighlighterList();
            highlighters.setToDefaultList();
            getPrefs().setValue(MylarUiPlugin.HIGHLIGHTER_PREFIX, this.highlighters.externalizeToString());
        }
    } 
    
    @Override
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        store.setDefault(GAMMA_SETTING_LIGHTENED,false);
        store.setDefault(GAMMA_SETTING_STANDARD,true);
        store.setDefault(GAMMA_SETTING_DARKENED,false);
        
        if (!store.contains(GLOBAL_FILTERING)) store.setDefault(GLOBAL_FILTERING, true);
    }
    
    public void setHighlighterMapping(String id, String name) {
        String prefId = TASK_HIGHLIGHTER_PREFIX  + id;
        getPrefs().putValue(prefId, name);
    } 
    
    /**
	 * Returns the shared instance.
	 */
	public static MylarUiPlugin getDefault() {
		return plugin;
	}
    
	public static IPreferenceStore getPrefs() {
		return MylarPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarUiPlugin.getDefault().getResourceBundle(); 
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}
 
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
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
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

    public boolean isDecorateInterestMode() {
        return decorateInterestMode;
    }
    

    public void setDecorateInterestMode(boolean decorateInterestLevel) {
        this.decorateInterestMode = decorateInterestLevel;
    }

    /**
     * @return  the corresponding adapter if found, or an adapter with no behavior otherwise (so
     * null is never returned)
     */
    public IMylarUiBridge getUiBridge(String extension) {
        IMylarUiBridge bridge = bridges.get(extension);
        if (bridge != null) {
            return bridge;
        } else {
            return DEFAULT_UI_BRIDGE;
        }
    }
    
    /**
     * TODO: cache this to improve performance?
     */
    public IMylarUiBridge getUiBridgeForEditor(IEditorPart editorPart) {
        IMylarUiBridge foundBridge = null;
        for (IMylarUiBridge bridge : bridges.values()) {
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

    public void addAdapter(String extension, IMylarUiBridge adapter) {
        this.bridges.put(extension, adapter);
        final ActiveSearchView activeSearchView = ActiveSearchView.getFromActivePerspective();
        if (activeSearchView != null) {
            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                public void run() { 
                    activeSearchView.resetProviders();
                }
            });   
        }
    }

    public ColorMap getColorMap() {
        return colorMap;
    }

    public Highlighter getDefaultHighlighter() {
    	return HighlighterList.DEFAULT_HIGHLIGHTER;
    }

    /**
     * @return  null if not found
     */
    public Highlighter getHighlighter(String name) {
    	if (highlighters == null) {
    		this.initializeHighlighters();
    	}
        return highlighters.getHighlighter(name);
    }

    public Highlighter getHighlighterForTaskId(String id) {
        String prefId = TASK_HIGHLIGHTER_PREFIX + id;
        String highlighterName = getPrefs().getString(prefId);
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

    public Highlighter getIntersectionHighlighter() {
        return intersectionHighlighter;
    }

    public void setColorMap(ColorMap colorMap) {
        this.colorMap = colorMap;
    }

    public void setIntersectionHighlighter(Highlighter intersectionHighlighter) {
        this.intersectionHighlighter = intersectionHighlighter;
    }

//    /**
//     * TODO: refactor
//     */
//    public boolean isGlobalFilteringEnabled() {
//    	return true;
////        return getPrefs().getBoolean(GLOBAL_FILTERING);
//    }

    public boolean isGlobalFoldingEnabled() {
        return getPrefs().getBoolean(GLOBAL_FILTERING);
    }
    
    public void setGlobalFilteringEnabled(boolean globalFilteringEnabled) {
        getPrefs().setValue(GLOBAL_FILTERING, globalFilteringEnabled);
    }
    
    public boolean isIntersectionMode() {
        return getPrefs().getBoolean(INTERSECTION_MODE);
    }

    public void setIntersectionMode(boolean isIntersectionMode) {
        getPrefs().setValue(INTERSECTION_MODE, isIntersectionMode);
    }

	public void addWorkingSetUpdater(MylarWorkingSetUpdater updater) {
		if(workingSetUpdaters == null)
			workingSetUpdaters = new ArrayList<MylarWorkingSetUpdater>();
		workingSetUpdaters.add(updater);
		MylarPlugin.getTaskscapeManager().addListener(updater);
	}

	public MylarWorkingSetUpdater getWorkingSetUpdater() {
		if(workingSetUpdaters == null)
			return null;
		else
			return workingSetUpdaters.get(0);
	}

	public UiUpdateManager getUiUpdateManager() {
		return uiUpdateManager;
	}
}
