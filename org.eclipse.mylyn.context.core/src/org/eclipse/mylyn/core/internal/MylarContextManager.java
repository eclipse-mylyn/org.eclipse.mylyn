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
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.core.AbstractRelationProvider;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarRelation;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.IActiveTimerListener;
import org.eclipse.mylar.core.util.IInteractionEventListener;
import org.eclipse.ui.internal.Workbench;

/**
 * This is the core class resposible for context management. 
 * 
 * @author Mik Kersten
 */
public class MylarContextManager {
    
    private static final String ACTIVITY_DEACTIVATED = "deactivated";
	private static final String ACTIVITY_ACTIVATED = "activated";
	private static final String ACTIVITY_ID = "org.eclipse.mylar.core";
	private static final String ACTIVITY_HANDLE = "attention";
	private static final String ACTIVITY_KIND = "context";
	public static final int ACTIVITY_TIMEOUT_MINUTES = 5; // in minutes 
		
	private static final String CONTEXT_HISTORY_FILE_NAME = "context-history";
	public static final String SOURCE_ID_MODEL_PROPAGATION = "org.eclipse.mylar.core.model.interest.propagation";
    public static final String SOURCE_ID_DECAY = "org.eclipse.mylar.core.model.interest.decay";
    public static final String SOURCE_ID_DECAY_CORRECTION = "org.eclipse.mylar.core.model.interest.decay.correction";
    
    public static final String SOURCE_ID_MODEL_ERROR = "org.eclipse.mylar.core.model.interest.propagation";
    public static final String CONTAINMENT_PROPAGATION_ID = "org.eclipse.mylar.core.model.edges.containment";
    
	public static final String FILE_EXTENSION = ".xml";
    
    private static final int MAX_PROPAGATION = 17; // TODO: parametrize this
    
    private int numInterestingErrors = 0;
    private List<String> errorElementHandles = new ArrayList<String>();
    
    private CompositeContext activeContext = new CompositeContext();
    private MylarContext activityHistory = null;
    private ActivityListener activityListener;
    
	private List<IMylarContextListener> listeners = new ArrayList<IMylarContextListener>();
	private List<IMylarContextListener> waitingListeners = new ArrayList<IMylarContextListener>();
 
	// TODO: move
	private List<IActionExecutionListener> actionExecutionListeners = new ArrayList<IActionExecutionListener>();
    private boolean suppressListenerNotification = false;
    private MylarContextExternalizer externalizer = new MylarContextExternalizer();
	private boolean activationHistorySuppressed = false;
    private static ScalingFactors scalingFactors = new ScalingFactors();
    
    private class ActivityListener implements IActiveTimerListener, IInteractionEventListener {
    	
    	private ActivityTimerThread timer;
    	private boolean isStalled;
    	
    	public ActivityListener(){
    		timer = new ActivityTimerThread(ACTIVITY_TIMEOUT_MINUTES);
    		timer.addListener(this);
    		timer.start();
    		MylarPlugin.getDefault().addInteractionListener(this);
    	}
    	
    	public void fireTimedOut() {
    		if (!isStalled) {
    	        activityHistory.parseEvent(
    	            	new InteractionEvent(InteractionEvent.Kind.COMMAND,
    	            			ACTIVITY_KIND,
    	            			ACTIVITY_HANDLE,
    	            			ACTIVITY_ID,
    	            			null,
    	            			ACTIVITY_DEACTIVATED,
    	            			1f));
    		}
    		isStalled = true; 
    		timer.resetTimer();
    	}

    	public void interactionObserved(InteractionEvent event) {
    		timer.resetTimer();		
    		if(isStalled) {
      	        activityHistory.parseEvent(
    	            	new InteractionEvent(InteractionEvent.Kind.COMMAND,
    	            			ACTIVITY_KIND,
    	            			ACTIVITY_HANDLE,
    	            			ACTIVITY_ID,
    	            			null,
    	            			ACTIVITY_ACTIVATED,
    	            			1f));
    		}
    		isStalled = false;
    	} 

    	public void start() {} 

    	public void stopTimer() {
    		timer.killThread();
    		MylarPlugin.getDefault().removeInteractionListener(this);
    	}

    	public void stop() {}
    }
    
    public MylarContextManager() {
        File storeDir = new File(MylarPlugin.getDefault().getMylarDataDirectory());
        storeDir.mkdirs();
        
        activityHistory = externalizer.readContextFromXML(getFileForContext(CONTEXT_HISTORY_FILE_NAME));
        if (activityHistory == null) {
        	resetActivityHistory();
        } 
        
        activityListener = new ActivityListener();//ACTIVITY_TIMEOUT_MINUTES);
        activityListener.start();
//        activityTimer.addListener(new IActiveTimerListener() {
//			public void fireTimedOut() {
//				
//			}
//        });
//        activityTimer.start();
    }

    public IMylarElement getActiveNode() {
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
        errorElementHandles.add(handle);
        numInterestingErrors++; 
    }

    /**
     * TODO: worry about decay-related change if predicted interest dacays
     */
    public void removeErrorPredictedInterest(String handle, String kind, boolean notify) { 
        if (activeContext.getContextMap().isEmpty()) return;
        if (handle == null) return;
        IMylarElement node = activeContext.get(handle);
        if (node != null 
            && node.getDegreeOfInterest().isInteresting()
        	&& errorElementHandles.contains(handle)) {
        	InteractionEvent errorEvent = new InteractionEvent(
                    InteractionEvent.Kind.MANIPULATION, 
                    kind, handle, 
                    SOURCE_ID_MODEL_ERROR,
                    -scalingFactors.getErrorInterest());
            handleInteractionEvent(errorEvent, true);
            numInterestingErrors--;
            errorElementHandles.remove(handle);
            // TODO: this results in double-notification
            if (notify) for (IMylarContextListener listener : listeners) listener.interestChanged(node);
        }
    } 

	public IMylarElement getNode(String elementHandle) {
        if (activeContext != null) {
            return activeContext.get(elementHandle);
        } else {
            return null;
        }
    }

    public IMylarElement handleInteractionEvent(InteractionEvent event) {
    	return handleInteractionEvent(event, true);
    }

    public IMylarElement handleInteractionEvent(InteractionEvent event, boolean propagateToParents) {
    	return handleInteractionEvent(event, true, true);
    }
    
    /**
     * TODO: consider moving this into the context?
     */
    public IMylarElement handleInteractionEvent(InteractionEvent event, boolean propagateToParents, boolean notifyListeners) {
        if (event.getKind() == InteractionEvent.Kind.COMMAND) return null;
        if (activeContext.getContextMap().values().size() == 0) return null;
        if (suppressListenerNotification) return null;
        
        IMylarElement previous = activeContext.get(event.getStructureHandle());
        float previousInterest = 0;
        boolean previouslyPredicted = false;
        boolean previouslyPropagated = false;
        float decayOffset = 0;
        if (previous != null) {
        	previousInterest = previous.getDegreeOfInterest().getValue();
        	previouslyPredicted = previous.getDegreeOfInterest().isPropagated();
        	previouslyPropagated = previous.getDegreeOfInterest().isPropagated();
        }
        if (event.getKind().isUserEvent()) {
        	if (previousInterest < 0) {  // reset interest if not interesting
            	decayOffset = (-1)*(previous.getDegreeOfInterest().getValue());
        		activeContext.addEvent(new InteractionEvent(
                        InteractionEvent.Kind.MANIPULATION, 
                        event.getContentType(),
                        event.getStructureHandle(), 
                        SOURCE_ID_DECAY_CORRECTION,
                        decayOffset));
            }
        }
        IMylarElement node = activeContext.addEvent(event);
        List<IMylarElement> interestDelta = new ArrayList<IMylarElement>();
        if (propagateToParents && !event.getKind().equals(InteractionEvent.Kind.MANIPULATION)) {
        	propegateDoiToParents(node, previousInterest, decayOffset, 1, interestDelta); 
        }
        if (event.getKind().isUserEvent()) activeContext.setActiveElement(node);

        if (isInterestDelta(previousInterest, previouslyPredicted, previouslyPropagated, node)) {
        	interestDelta.add(node); // TODO: check that the order of these is sensible
        }
        if (notifyListeners && !interestDelta.isEmpty()) {
	        for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
	        	listener.interestChanged(interestDelta);
	        }
        } 
         
        checkForLandmarkDeltaAndNotify(previousInterest, node);
        return node;
    }

	private boolean isInterestDelta(float previousInterest, boolean previouslyPredicted, boolean previouslyPropagated, IMylarElement node) {
		float currentInterest = node.getDegreeOfInterest().getValue();
		if (previousInterest <= 0 && currentInterest > 0) {
			return true;
		} else if (previousInterest > 0 && currentInterest <=0){
			return true;
		} else if (currentInterest > 0 && previouslyPredicted && !node.getDegreeOfInterest().isPredicted()) {
			return true;
		} else if (currentInterest > 0 && previouslyPropagated && !node.getDegreeOfInterest().isPropagated()) {
			return true;
		} else {
			return false;
		}
	}

	private void checkForLandmarkDeltaAndNotify(float previousInterest, IMylarElement node) {
		// TODO: don't call interestChanged if it's a landmark?
    	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
    	if (bridge.canBeLandmark(node.getHandleIdentifier())) {
    		if (previousInterest >= scalingFactors.getLandmark() && !node.getDegreeOfInterest().isLandmark()) {
    			for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) listener.landmarkRemoved(node);
            } else if (previousInterest < scalingFactors.getLandmark() && node.getDegreeOfInterest().isLandmark()) {
            	for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) listener.landmarkAdded(node);
            }        	
        } 
	}
    
    private void propegateDoiToParents(IMylarElement node, float previousInterest, float decayOffset, int level, List<IMylarElement> elementDelta) {
        if (level > MAX_PROPAGATION || node == null || node.getDegreeOfInterest().getValue() <= 0) return;// || "/".equals(node.getElementHandle())) return;         
        
        checkForLandmarkDeltaAndNotify(previousInterest, node);
        
        level++; // original is 1st level
        float propagatedIncrement = node.getDegreeOfInterest().getValue() - previousInterest + decayOffset;
//        float propagatedIncrement = scalingFactors.getParentPropagationIncrement(level);
      
        IMylarStructureBridge adapter = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
                
        String parentHandle = adapter.getParentHandle(node.getHandleIdentifier());
        if (parentHandle != null) {
            InteractionEvent propagationEvent = new InteractionEvent(
                    InteractionEvent.Kind.PROPAGATION, 
                    adapter.getContentType(node.getHandleIdentifier()),
                    adapter.getParentHandle(node.getHandleIdentifier()), 
                    SOURCE_ID_MODEL_PROPAGATION,
                    CONTAINMENT_PROPAGATION_ID,
                    propagatedIncrement);
            IMylarElement previous = activeContext.get(propagationEvent.getStructureHandle());
            if (previous != null && previous.getDegreeOfInterest() != null) previousInterest = previous.getDegreeOfInterest().getValue();
            CompositeContextNode parentNode = (CompositeContextNode)activeContext.addEvent(propagationEvent);
            if (isInterestDelta(
            		previousInterest, 
            		previous.getDegreeOfInterest().isPredicted(), 
            		previous.getDegreeOfInterest().isPropagated(),
            		parentNode)) {
            	elementDelta.add(0, parentNode);
            }
            propegateDoiToParents(parentNode, previousInterest, decayOffset, level, elementDelta);//adapter.getResourceExtension(), adapter.getParentHandle(parentHandle), level, doi, parentChain);    
        }
    }

    public List<IMylarElement> findCompositesForNodes(List<MylarContextNode> nodes) {
        List<IMylarElement> composites = new ArrayList<IMylarElement>();
        for (MylarContextNode node : nodes) {
            composites.add(activeContext.get(node.getHandleIdentifier()));
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
    
    /**
     * For testing
     */
    public void contextActivated(MylarContext context) {
        activeContext.getContextMap().put(context.getId(), context);
        if (!activationHistorySuppressed) {
	        activityHistory.parseEvent(
	        	new InteractionEvent(InteractionEvent.Kind.COMMAND,
	        			ACTIVITY_KIND,
	        			context.getId(),
	        			ACTIVITY_ID,
	        			null,
	        			ACTIVITY_ACTIVATED,
	        			1f));
        }
    } 
    
    public void contextActivated(String id, String path) {
    	try {
		    suppressListenerNotification = true;
		    MylarContext context = activeContext.getContextMap().get(id);
		    if (context == null) context = loadContext(id, path);
		    if (context != null) {
		    	contextActivated(context);
		        for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
		        	listener.contextActivated(context);
		        }
		        refreshRelatedElements();
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
     * Could load in the context and inspect it, but this is cheaper.
     */
	public boolean hasContext(String path) {
		File contextFile = getFileForContext(path);
		return contextFile.exists() && contextFile.length() > 0;
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
	            for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
	            	listener.contextDeactivated(context);
	            }
	        }
	        if (!activationHistorySuppressed) {
		        activityHistory.parseEvent(
		            	new InteractionEvent(InteractionEvent.Kind.COMMAND,
		            			ACTIVITY_KIND,
		            			id,
		            			ACTIVITY_ID,
		            			null,
		            			ACTIVITY_DEACTIVATED,
		            			1f));
	        }
	        saveActivityHistoryContext();
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
        if (context == null) return;
    	context.collapse();
        externalizer.writeContextToXML(context, getFileForContext(path));
    }
    
    private void saveActivityHistoryContext() {
    	externalizer.writeContextToXML(activityHistory, getFileForContext(CONTEXT_HISTORY_FILE_NAME));
	}

    public File getFileForContext(String path) {
        return new File(MylarPlugin.getDefault().getMylarDataDirectory() + File.separator + path + FILE_EXTENSION);
    }
    
    public IMylarContext getActiveContext() {
        return activeContext;
    }
   
    /**
     * @param kind
     */
    public void resetLandmarkRelationshipsOfKind(String reltationKind) {
        for (IMylarElement landmark : activeContext.getLandmarks()) {       	
        	for (IMylarRelation edge : landmark.getRelations()) {
        		if (edge.getRelationshipHandle().equals(reltationKind)) {
        			landmark.clearEdges();         		}
			}
        }
        for (IMylarContextListener listener : listeners) listener.edgesChanged(null);
    }

    /**
     * Copy the listener list in case it is modified during the notificiation.
     * @param node
     */
	public void notifyRelationshipsChanged(IMylarElement node) {
		if (suppressListenerNotification) return;
		for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
			listener.edgesChanged(node);
		}
	}
    
    public void refreshRelatedElements() {
    	for(IMylarStructureBridge bridge: MylarPlugin.getDefault().getStructureBridges().values()){
    		if(bridge.getRelationshipProviders() != null){
		        for (AbstractRelationProvider provider : bridge.getRelationshipProviders()) {
		        	List<AbstractRelationProvider> providerList = new ArrayList<AbstractRelationProvider>();
		        	providerList.add(provider);
		        	updateDegreesOfSeparation(providerList, provider.getCurrentDegreeOfSeparation());
		//            if (provider.isEnabled()) {
		//                MylarPlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
		//            }
		//            for (IMylarElement node : activeContext.getLandmarks()) provider.landmarkAdded(node);
		        }
    		}
    	}
    }

    public List<AbstractRelationProvider> getActiveRelationProviders() {
    	List<AbstractRelationProvider> providers = new ArrayList<AbstractRelationProvider>();
		Map<String, IMylarStructureBridge> bridges = MylarPlugin.getDefault().getStructureBridges();
        for (String extension : bridges.keySet()) {
            IMylarStructureBridge bridge = bridges.get(extension);
            providers.addAll(bridge.getRelationshipProviders());
        }
        return providers;
    }
    
    public void updateDegreeOfSeparation(AbstractRelationProvider provider, int degreeOfSeparation) {
        MylarPlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
        if (degreeOfSeparation <= 0) {
//        	provider.setEnabled(false);
        	provider.setDegreeOfSeparation(degreeOfSeparation);
        } else {
//        	provider.setEnabled(true);
        	provider.setDegreeOfSeparation(degreeOfSeparation);
            for (IMylarElement node : activeContext.getLandmarks()) provider.landmarkAdded(node);
        }
    }
    
    public void updateDegreesOfSeparation(List<AbstractRelationProvider> providers, int degreeOfSeparation) {
    	for(AbstractRelationProvider provider: providers) {
    		updateDegreeOfSeparation(provider, degreeOfSeparation);
    	}
    }

    public static ScalingFactors getScalingFactors() {
        return MylarContextManager.scalingFactors;
    }

    public void dumpInteractionHistoryForSelected() {
    	MylarPlugin.log("> interaction history: " + activeContext.getActiveNode().getDegreeOfInterest().getEvents(), this);
    }
    
    public void updateMylarDirContents(String prevDir) { 
		File prev = new File(prevDir);
		if (!prev.isDirectory()) {
			return;
		}
		File[] contents = prev.listFiles();
		File curr = new File(MylarPlugin.getDefault().getMylarDataDirectory());
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

	public List<IMylarElement> getActiveLandmarks() {
		List<IMylarElement> allLandmarks = activeContext.getLandmarks();
		List<IMylarElement> acceptedLandmarks = new ArrayList<IMylarElement>();
		for (IMylarElement node : allLandmarks) {
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());

            if (bridge.canBeLandmark(node.getHandleIdentifier())) {
            	acceptedLandmarks.add(node);
        	}
        } 
		return acceptedLandmarks;
	}
	
    public Set<IMylarElement> getInterestingResources(IMylarContext context) {
        Set<IMylarElement> interestingFiles = new HashSet<IMylarElement>();
        List<IMylarElement> allIntersting = context.getInteresting();
        for (IMylarElement node : allIntersting) {
            if (MylarPlugin.getDefault().getStructureBridge(node.getContentType()).isDocument(node.getHandleIdentifier())) {       
                interestingFiles.add(node);
            }
        }
        return interestingFiles;
    }
	
	/**
	 * Get the interesting resources for the active context.
	 */
	public Set<IMylarElement> getActiveContextResources() {
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

	public MylarContext getActivityHistory() {
		return activityHistory;
	}
	
	public void resetActivityHistory() {
		activityHistory = new MylarContext(CONTEXT_HISTORY_FILE_NAME, MylarContextManager.getScalingFactors());
		saveActivityHistoryContext();
	}

	public boolean isActivationHistorySuppressed() {
		return activationHistorySuppressed;
	}

	public void setActivationHistorySuppressed(boolean activationHistorySuppressed) {
		this.activationHistorySuppressed = activationHistorySuppressed;
	}
	
    public void manipulateInterestForNode(IMylarElement node, boolean increment, boolean forceLandmark, String sourceId) {
        float originalValue = node.getDegreeOfInterest().getValue();
        float changeValue = 0;
        if (!increment) {
            if (node.getDegreeOfInterest().isLandmark()) { // keep it interesting
                changeValue = (-1 * originalValue) + 1; 
            } else { 
            	if (originalValue >=0) changeValue = (-1 * originalValue)-1;
            }
        } else {
        	if (!forceLandmark && (originalValue >  MylarContextManager.getScalingFactors().getLandmark())) {
                changeValue = 0;
            } else { // make it a landmark
            	
    			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
    			if (node != null
    				&& bridge.canBeLandmark(node.getHandleIdentifier())
    				&& !bridge.getContentType(node.getHandleIdentifier()).equals(MylarPlugin.CONTENT_TYPE_ANY)) {
            		changeValue = MylarContextManager.getScalingFactors().getLandmark() - originalValue + 1;
            	} else {
            		// TODO: move this to UI?
    				MessageDialog.openInformation(
    						Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
    						"Mylar Interest Manipulation", 
    						"This element is not a valid landmark because it is not a structured element.  Note that files and other resources can not be landmarks.");
            	}
            }
        }
        if (changeValue != 0) {
            InteractionEvent interactionEvent = new InteractionEvent(
                    InteractionEvent.Kind.MANIPULATION,  
                    node.getContentType(), 
                    node.getHandleIdentifier(), 
                    sourceId,
                    changeValue);
            MylarPlugin.getContextManager().handleInteractionEvent(interactionEvent);
        }		
    }

	public void setActiveSearchEnabled(boolean enabled) {
		for(AbstractRelationProvider provider: getActiveRelationProviders()){
            provider.setEnabled(enabled);
        }
	}
}
