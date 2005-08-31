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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.actions.ApplyMylarToOutlineAction;
import org.eclipse.mylar.ui.internal.ColorMap;
import org.eclipse.mylar.ui.internal.UiExtensionPointReader;
import org.eclipse.mylar.ui.internal.ViewerConfigurator;
import org.eclipse.mylar.ui.internal.views.Highlighter;
import org.eclipse.mylar.ui.internal.views.HighlighterList;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarUiPlugin extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "org.eclipse.mylar.ui";
	public static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylar.ui.context";
	public static final String ELEMENT_UI_BRIDGE = "uiBridge";
	public static final String ELEMENT_UI_CLASS = "class";
	public static final String ELEMENT_UI_CONTEXT_LABEL_PROVIDER = "labelProvider";	
	public static final String ELEMENT_UI_BRIDGE_CONTENT_TYPE = "contentType";

    public static final String MARKER_LANDMARK = "org.eclipse.mylar.ui.interest.landmark";
    public static final String TASK_HIGHLIGHTER_PREFIX = "org.eclipse.mylar.ui.interest.highlighters.task.";
    public static final String INTEREST_FILTER_EXCLUSION = "org.eclipse.mylar.ui.interest.filter.exclusion";
    public static final String HIGHLIGHTER_PREFIX = "org.eclipse.mylar.ui.interest.highlighters";
    public static final String GAMMA_SETTING_DARKENED = "org.eclipse.mylar.ui.gamma.darkened";
    public static final String GAMMA_SETTING_STANDARD = "org.eclipse.mylar.ui.gamma.standard";
    public static final String GAMMA_SETTING_LIGHTENED = "org.eclipse.mylar.ui.gamma.lightened";
    public static final String GLOBAL_FILTERING = "org.eclipse.mylar.ui.interest.filter.global";
    public static final String INTERSECTION_MODE = "org.eclipse.mylar.ui.interest.intersection";    
	
    private Map<String, IMylarUiBridge> bridges = new HashMap<String, IMylarUiBridge>();
    private Map<String, ILabelProvider> contextLabelProviders = new HashMap<String, ILabelProvider>();
    
    private static MylarUiPlugin plugin;
	private ResourceBundle resourceBundle;
    private boolean decorateInterestMode = false;
    
    private HighlighterList highlighters = null;
    private Highlighter intersectionHighlighter;
    private ColorMap colorMap = new ColorMap(); 
    
    protected MylarViewerManager uiUpdateManager = new MylarViewerManager();
    private ViewerConfigurator viewerConfigurator = new ViewerConfigurator();
    public static final Font ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
	public static final Font BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

    private static final AbstractContextLabelProvider DEFAULT_LABEL_PROVIDER = new AbstractContextLabelProvider() {

		@Override
		protected Image getImage(IMylarContextNode node) {
			return null;
		}

		@Override
		protected Image getImage(IMylarContextEdge edge) {
			return null;
		}

		@Override
		protected String getText(IMylarContextNode node) {
			return "" + node;
		}

		@Override
		protected String getText(IMylarContextEdge edge) {
			return "" + edge;
		}
    	
    };
	
    private static final IMylarUiBridge DEFAULT_UI_BRIDGE = new IMylarUiBridge() {

        public void open(IMylarContextNode node) {
        	// ignore
        }

        public void close(IMylarContextNode node) {
        	// ignore
        }

        public boolean acceptsEditor(IEditorPart editorPart) {
            return false; 
        }

        public List<TreeViewer> getTreeViewers(IEditorPart editor) {
            return Collections.emptyList();
        }

        public void refreshOutline(Object element, boolean updateLabels) {
        }

    };
        
    public MylarUiPlugin() {
		super(); 
		plugin = this;
        try {
            resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.MylarUiPluginResources");            
        } catch (MissingResourceException x) {
            resourceBundle = null;
        } catch (Throwable t) {
        	MylarPlugin.log(t, "plug-in intialization failed");      
        }
        initializeHighlighters();
        initializeDefaultPreferences(getPrefs());
        initializeActions();
	} 

    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
            	UiExtensionPointReader.initExtensions();
            	MylarPlugin.getContextManager().addListener(uiUpdateManager);
                
                Workbench.getInstance().getActiveWorkbenchWindow().getPartService().addPartListener(viewerConfigurator);
        		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
        		for (int i= 0; i < windows.length; i++) {
        			windows[i].addPageListener(viewerConfigurator);
//        			IWorkbenchPage[] pages= windows[i].getPages();
//        			for (int j= 0; j < pages.length; j++) {
//        				pages[j].addPartListener(viewerManager);
//        			}
        		}
                if (ApplyMylarToOutlineAction.getDefault() != null) ApplyMylarToOutlineAction.getDefault().update();
            }
        });
    }
    
	@Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }
    
    private void initializeActions() {
        // don't have any actions to initialize
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

    public void internalAddBridge(String extension, IMylarUiBridge bridge) {
        this.bridges.put(extension, bridge);
//        final ActiveSearchView activeSearchView = ActiveSearchView.getFromActivePerspective();
//        if (activeSearchView != null) {
//            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
//                public void run() { 
//                    activeSearchView.resetProviders();
//                }
//            });   
//        }
    }

    public ILabelProvider getContextLabelProvider(String extension) {
    	ILabelProvider provider = contextLabelProviders.get(extension);
        if (provider != null) {
            return provider;
        } else {
            return DEFAULT_LABEL_PROVIDER;
        }
    }
    
    public void internalAddContextLabelProvider(String extension, ILabelProvider provider) {
        this.contextLabelProviders.put(extension, provider);
    }
    
    public void updateGammaSetting(ColorMap.GammaSetting setting) {
    	if (colorMap.getGammaSetting() != setting) {
    		highlighters.updateHighlighterWithGamma(colorMap.getGammaSetting(), setting);
    		colorMap.setGammaSetting(setting);
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

	public MylarViewerManager getUiUpdateManager() {
		return uiUpdateManager;
	}
}
