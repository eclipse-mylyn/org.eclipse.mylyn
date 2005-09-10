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

package org.eclipse.mylar.core.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylar.core.AbstractRelationshipProvider;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;


/**
 * @author Mik Kersten
 */
public class MylarContextManager {
    
    public static final String SOURCE_ID_MODEL_PROPAGATION = "org.eclipse.mylar.core.model.interest.propagation";
    public static final String SOURCE_ID_DECAY = "org.eclipse.mylar.core.model.interest.decay";
    public static final String SOURCE_ID_DECAY_CORRECTION = "org.eclipse.mylar.core.model.interest.decay.correction";
    
    public static final String SOURCE_ID_MODEL_ERROR = "org.eclipse.mylar.core.model.interest.propagation";
    public static final String CONTAINMENT_PROPAGATION_ID = "org.eclipse.mylar.core.model.edges.containment";
    
	public static final String FILE_EXTENSION = ".xml";
    
    private static final int MAX_PROPAGATION = 17; // TODO: parametrize this
    
    private int numInterestingErrors = 0;
    
    private CompositeContext activeContext = new CompositeContext();
	
    private String contextStoreDirPath;
	private boolean editorAutoCloseEnabled = false;
	private List<IMylarContextListener> listeners = new ArrayList<IMylarContextListener>();
	private List<IMylarContextListener> waitingListeners = new ArrayList<IMylarContextListener>();

	// TODO: move
	private List<IActionExecutionListener> actionExecutionListeners = new ArrayList<IActionExecutionListener>();
	
    private boolean suppressListenerNotification = false;
    
    private MylarContextExternalizer externalizer = new MylarContextExternalizer();
    
    private static ScalingFactors scalingFactors = new ScalingFactors();
    
    public MylarContextManager() {
        File storeDir = new File(MylarPlugin.getDefault().getUserDataDirectory());
        storeDir.mkdirs();
    }

    public IMylarContextNode getActiveNode() {
        if (activeContext != null) {
            return activeContext.getActiveNode();
        } else {
            return null;
        }
    }
    
	public void addErrorPredictedInterest(String handle, String kind, boolean notify) { 
        if (numInterestingErrors > scalingFactors.getMaxNumInterestingErrors() 
            || activeContext.getContextMap().isEmpty()) return;
        InteractionEvent errorEvent = new InteractionEvent(
                InteractionEvent.Kind.PROPAGATION, 
                kind, handle, 
                SOURCE_ID_MODEL_ERROR,
                scalingFactors.getErrorInterest());
        handleInteractionEvent(errorEvent, true);
        numInterestingErrors++; 
    }

    /**
     * TODO: worry about decay-related change if predicted interest dacays
     */
    public void removeErrorPredictedInterest(String handle, String kind, boolean notify) { 
        if (activeContext.getContextMap().isEmpty()) return;
        if (handle == null) return;
        IMylarContextNode node = activeContext.get(handle);
        if (node == null || !node.getDegreeOfInterest().isInteresting()) return;
//        if (node.getDegreeOfInterest().getValue() >= scalingFactors.getErrorInterest()) { // TODO: hack?
            InteractionEvent errorEvent = new InteractionEvent(
                    InteractionEvent.Kind.MANIPULATION, 
                    kind, handle, 
                    SOURCE_ID_MODEL_ERROR,
                    -scalingFactors.getErrorInterest());
            handleInteractionEvent(errorEvent, true);
            numInterestingErrors--;
            // TODO: this will results in double-notification
            if (notify) for (IMylarContextListener listener : listeners) listener.interestChanged(node);
//        }
    } 

    public IMylarContextNode getNode(String elementHandle) {
        if (activeContext != null) {
            return activeContext.get(elementHandle);
        } else {
            return null;
        }
    }

    public IMylarContextNode handleInteractionEvent(InteractionEvent event) {
    	return handleInteractionEvent(event, true);
    }
    
    /**
     * TODO: consider moving this into the context?
     */
    public IMylarContextNode handleInteractionEvent(InteractionEvent event, boolean propagateToParents) {
        if (event.getKind() == InteractionEvent.Kind.COMMAND) return null;
        if (activeContext.getContextMap().values().size() == 0) return null;
        if (suppressListenerNotification) return null;
        
        IMylarContextNode previous = activeContext.get(event.getStructureHandle());
        float previousInterest = 0;
        float decayOffset = 0;
        if (previous != null) previousInterest = previous.getDegreeOfInterest().getValue();
        if (event.getKind().isUserEvent()) {
        	if (previousInterest < 0) {  // reset interest if not interesting
            	decayOffset = (-1)*(previous.getDegreeOfInterest().getValue());
        		activeContext.addEvent(new InteractionEvent(
                        InteractionEvent.Kind.MANIPULATION, 
                        event.getStructureKind(),
                        event.getStructureHandle(), 
                        SOURCE_ID_DECAY_CORRECTION,
                        decayOffset));
            }
        }
        IMylarContextNode node = activeContext.addEvent(event);
        List<IMylarContextNode> interestDelta = new ArrayList<IMylarContextNode>();
        if (propagateToParents && !event.getKind().equals(InteractionEvent.Kind.MANIPULATION)) {
        	propegateDoiToParents(node, previousInterest, decayOffset, 1, interestDelta); 
        }
        if (event.getKind().isUserEvent()) activeContext.setActiveElement(node);

        interestDelta.add(node); // TODO: check that the order of these is sensible
        for (IMylarContextListener listener : listeners) listener.interestChanged(interestDelta);
         
        checkForLandmarkDelta(previousInterest, node);
        
//        if (nextEventIsRaiseChildren && event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
//        	tempRaiseChildrenForSelected();
//    		nextEventIsRaiseChildren = false;
//    	} 
        
        return node;
    }

	private void checkForLandmarkDelta(float previousInterest, IMylarContextNode node) {
		// TODO: don't call interestChanged if it's a landmark?
    	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentKind());
    	if (bridge.canBeLandmark(node.getElementHandle())) {
    		if (previousInterest >= scalingFactors.getLandmark() && !node.getDegreeOfInterest().isLandmark()) {
    			for (IMylarContextListener listener : listeners) listener.landmarkRemoved(node);
            } else if (previousInterest < scalingFactors.getLandmark() && node.getDegreeOfInterest().isLandmark()) {
            	for (IMylarContextListener listener : listeners) listener.landmarkAdded(node);
            }        	
        } 
	}
    
    private void propegateDoiToParents(IMylarContextNode node, float previousInterest, float decayOffset, int level, List<IMylarContextNode> elementDelta) {
        if (level > MAX_PROPAGATION || node == null || node.getDegreeOfInterest().getValue() <= 0) return;// || "/".equals(node.getElementHandle())) return;         
        
        checkForLandmarkDelta(previousInterest, node);
        
        level++; // original is 1st level
        float propagatedIncrement = node.getDegreeOfInterest().getValue() - previousInterest + decayOffset;
//        float propagatedIncrement = scalingFactors.getParentPropagationIncrement(level);
      
        IMylarStructureBridge adapter = MylarPlugin.getDefault().getStructureBridge(node.getContentKind());
                
        String parentHandle = adapter.getParentHandle(node.getElementHandle());
        if (parentHandle != null) {
            InteractionEvent propagationEvent = new InteractionEvent(
                    InteractionEvent.Kind.PROPAGATION, 
                    adapter.getResourceExtension(node.getElementHandle()),
                    adapter.getParentHandle(node.getElementHandle()), 
                    SOURCE_ID_MODEL_PROPAGATION,
                    CONTAINMENT_PROPAGATION_ID,
                    propagatedIncrement);
            IMylarContextNode previous = activeContext.get(propagationEvent.getStructureHandle());
            if (previous != null && previous.getDegreeOfInterest() != null) previousInterest = previous.getDegreeOfInterest().getValue();
            CompositeContextNode parentNode = (CompositeContextNode)activeContext.addEvent(propagationEvent);
            elementDelta.add(0, parentNode);
            propegateDoiToParents(parentNode, previousInterest, decayOffset, level, elementDelta);//adapter.getResourceExtension(), adapter.getParentHandle(parentHandle), level, doi, parentChain);    
        }
    }

    public List<IMylarContextNode> findCompositesForNodes(List<MylarContextNode> nodes) {
        List<IMylarContextNode> composites = new ArrayList<IMylarContextNode>();
        for (MylarContextNode node : nodes) {
            composites.add(activeContext.get(node.getElementHandle()));
        }
        return composites;
    }
	
	public void addListener(IMylarContextListener listener) {
        if (listener != null) {
        	if (suppressListenerNotification) {
        		waitingListeners.add(listener);
        	} else {
        		listeners.add(listener);   
        	}
        } else {
            MylarPlugin.log("attempted to add null lisetener", this);
        }
	}
	
	public void removeListener(IMylarContextListener listener) {
		listeners.remove(listener);
	}
    
    public void removeAllListeners() {
        listeners.clear();
    }
    
    public void notifyPostPresentationSettingsChange(IMylarContextListener.UpdateKind kind) { 
    	for (IMylarContextListener listener : listeners) listener.presentationSettingsChanged(kind);
    }
    
    public void notifyActivePresentationSettingsChange(IMylarContextListener.UpdateKind kind) {
        for (IMylarContextListener listener : listeners) listener.presentationSettingsChanging(kind);
    }

    public boolean isEditorAutoCloseEnabled() {
        return editorAutoCloseEnabled;
    }
    
    public void setEditorAutoCloseEnabled(boolean editorAutoCloseEnabled) {
        this.editorAutoCloseEnabled = editorAutoCloseEnabled;
    }

    /**
     * For testing
     */
    public void contextActivated(MylarContext context) {
        activeContext.getContextMap().put(context.getId(), context);
    } 
    
    public void contextActivated(String id, String path) {
    	try {
		    suppressListenerNotification = true;
		    MylarContext context = activeContext.getContextMap().get(id);
		    if (context == null) context = loadContext(id, path);
		    if (context != null) {
		        activeContext.getContextMap().put(id, context);
		        for (IMylarContextListener listener : listeners) listener.contextActivated(context);
		    } else {
		        MylarPlugin.log("Could not load context", this);
		    }
		    suppressListenerNotification = false;
		    listeners.addAll(waitingListeners);
    	} catch (Throwable t) {
    		MylarPlugin.log(t, "Could not activate context");
    	}
    }

    /**
     * @param id
     */
    public void contextDeactivated(String id, String path) {
    	try {
	        IMylarContext context = activeContext.getContextMap().get(id);        
	        if (context != null) {
	            saveContext(id, path); 
	            activeContext.getContextMap().remove(id);
	            for (IMylarContextListener listener : listeners) listener.contextDeactivated(context);
	        }
    	} catch (Throwable t) {
    		MylarPlugin.log(t, "Could not deactivate context");
    	}
    }

    public void contextDeleted(String id, String path) {
        IMylarContext context = activeContext.getContextMap().get(id);
        eraseContext(id, false);
        if (context != null) { // TODO: this notification is redundant with eraseContext's
            for (IMylarContextListener listener : listeners) listener.contextDeactivated(context);
        }
        try {
	        File f = getFileForContext(path);
	        if (f.exists()) {
	        	f.delete();
	        }
		} catch (SecurityException e) {
			MylarPlugin.fail(e, "Could not delete context file", false);
		}
    } 
     
    private void eraseContext(String id, boolean notify) {
        MylarContext context = activeContext.getContextMap().get(id);
        if (context == null) return;
        activeContext.getContextMap().remove(context);
        context.reset();
        if (notify) {
        	for (IMylarContextListener listener : listeners) listener.presentationSettingsChanging(IMylarContextListener.UpdateKind.UPDATE);
        }
    }
    
     /**
       * @return false if the map could not be read for any reason
       */
    public MylarContext loadContext(String id, String path) {
        MylarContext loadedContext = externalizer.readContextFromXML(getFileForContext(path));
        if (loadedContext == null) {
            return new MylarContext(id, MylarContextManager.getScalingFactors());
        } else {
            return loadedContext;
        }
    }
 
    public void saveContext(String id, String path) {
        MylarContext context = activeContext.getContextMap().get(id);
        if (context == null) {
            return;
        } else {
        	context.collapse();
            externalizer.writeContextToXML(context, getFileForContext(path));
        }
    }

    public File getFileForContext(String path) {
        return new File(contextStoreDirPath + File.separator + path + FILE_EXTENSION);
    }
    
    public String getMylarDir() {
        return "" + contextStoreDirPath;
    }
    
    public IMylarContext getActiveContext() {
        return activeContext;
    }
   
    /**
     * @param kind
     */
    public void resetLandmarkRelationshipsOfKind(String reltationKind) {
        for (IMylarContextNode landmark : activeContext.getLandmarks()) {       	
        	for (IMylarContextEdge edge : landmark.getEdges()) {
        		if (edge.getRelationshipHandle().equals(reltationKind)) {
        			landmark.clearEdges();         		}
			}
        }
        for (IMylarContextListener listener : listeners) listener.edgesChanged(null);
    }

	public void notifyRelationshipsChanged(IMylarContextNode node) {
		for (IMylarContextListener listener : listeners) listener.edgesChanged(node);
	}
    
    public void refreshRelatedElements() {
    	for(IMylarStructureBridge bridge: MylarPlugin.getDefault().getStructureBridges().values()){
    		if(bridge.getProviders() != null){
		        for (AbstractRelationshipProvider provider : bridge.getProviders()) {
		        	List<AbstractRelationshipProvider> providerList = new ArrayList<AbstractRelationshipProvider>();
		        	providerList.add(provider);
		        	updateSearchKindEnabled(providerList, provider.getCurrentDegreeOfSeparation());
		//            if (provider.isEnabled()) {
		//                MylarPlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
		//            }
		//            for (IMylarContextNode node : activeContext.getLandmarks()) provider.landmarkAdded(node);
		        }
    		}
    	}
    }

    public void updateSearchKindEnabled(AbstractRelationshipProvider provider, int degreeOfSeparation) {
        MylarPlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
        if (degreeOfSeparation <= 0) {
        	provider.setEnabled(false);
        	provider.setDegreeOfSeparation(degreeOfSeparation);
        } else {
        	provider.setEnabled(true);
        	provider.setDegreeOfSeparation(degreeOfSeparation);
            for (IMylarContextNode node : activeContext.getLandmarks()) provider.landmarkAdded(node);
        }
    }
    
    public void updateSearchKindEnabled(List<AbstractRelationshipProvider> providers, int degreeOfSeparation) {
    	for(AbstractRelationshipProvider provider: providers) {
    		updateSearchKindEnabled(provider, degreeOfSeparation);
    	}
    }

    public static ScalingFactors getScalingFactors() {
        return MylarContextManager.scalingFactors;
    }

    public void dumpInteractionHistoryForSelected() {
    	MylarPlugin.log("> interaction history: " + activeContext.getActiveNode().getDegreeOfInterest().getEvents(), this);
    }
    
    public void updateMylarDirContents(String prevDir) {    	    	    	
    	this.contextStoreDirPath = MylarPlugin.getDefault().getUserDataDirectory();
		File prev = new File(prevDir);
		if (!prev.isDirectory()) {
			return;
		}
		File[] contents = prev.listFiles();
		File curr = new File(contextStoreDirPath);
		for (File f : contents) {
			// XXX: remove hack below
			if ( (f.getName().endsWith(".xml") && f.getName().startsWith("task")) || f.getName().startsWith("mylar")) {
				String name = curr.getAbsolutePath() + "/" + f.getName();				
				f.renameTo(new File(name));
			}
		}		
	}

	public boolean hasActiveContext() {
		return activeContext.getContextMap().values().size() > 0;
	}

	public List<IMylarContextNode> getActiveLandmarks() {
		List<IMylarContextNode> allLandmarks = activeContext.getLandmarks();
		List<IMylarContextNode> acceptedLandmarks = new ArrayList<IMylarContextNode>();
		for (IMylarContextNode node : allLandmarks) {
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentKind());

            if (bridge.canBeLandmark(node.getElementHandle())) {
            	acceptedLandmarks.add(node);
        	}
        } 
		return acceptedLandmarks;
	}
	
    public Set<IMylarContextNode> getInterestingResources(IMylarContext context) {
        Set<IMylarContextNode> interestingFiles = new HashSet<IMylarContextNode>();
        List<IMylarContextNode> allIntersting = context.getInteresting();
        for (IMylarContextNode node : allIntersting) {
            if (MylarPlugin.getDefault().getStructureBridge(node.getContentKind()).isDocument(node.getElementHandle())) {       
                interestingFiles.add(node);
            }
        }
        return interestingFiles;
    }
	
	public Set<IMylarContextNode> getActiveContextResources() {
		return getInterestingResources(activeContext);
    }

	public void actionObserved(IAction action, String info) {
		for (IActionExecutionListener listener : actionExecutionListeners) {
			listener.actionObserved(action);
		}
	}

	public List<IActionExecutionListener> getActionExecutionListeners() {
		return actionExecutionListeners;
	}
}
