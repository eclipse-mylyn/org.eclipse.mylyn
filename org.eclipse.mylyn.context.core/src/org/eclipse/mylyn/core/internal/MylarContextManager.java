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
/*
 * Created on Jul 12, 2004
  */
package org.eclipse.mylar.core.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylar.core.AbstractRelationshipProvider;
import org.eclipse.mylar.core.IMylarContext;
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
	
    private String taskscapeStoreDirPath;
	private boolean editorAutoCloseEnabled = false;
	private List<IMylarContextListener> listeners = new ArrayList<IMylarContextListener>();
	private List<IMylarContextListener> waitingListeners = new ArrayList<IMylarContextListener>();

    private boolean suppressListenerNotification = false;
    
    private MylarContextExternalizer externalizer = new MylarContextExternalizer();
	private boolean nextEventIsRaiseChildren;
    
    private static ScalingFactors scalingFactors = new ScalingFactors();
    
    public MylarContextManager() {
        taskscapeStoreDirPath = MylarPlugin.getDefault().getUserDataDirectory();
        
        File storeDir = new File(taskscapeStoreDirPath);
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
                InteractionEvent.Kind.PREDICTION, 
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
     * TODO: consider moving this into the taskscape?
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
    	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
    	if (bridge.canBeLandmark(bridge.getObjectForHandle(node.getElementHandle()))) {
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
      
        IMylarStructureBridge adapter = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
                
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
    public void taskActivated(MylarContext taskscape) {
        activeContext.getContextMap().put(taskscape.getId(), taskscape);
    } 
    
    public void taskActivated(String id, String path) {
    	try {
		    suppressListenerNotification = true;
		    MylarContext taskscape = activeContext.getContextMap().get(id);
		    if (taskscape == null) taskscape = loadTaskscape(id, path);
		    if (taskscape != null) {
		        activeContext.getContextMap().put(id, taskscape);
		        for (IMylarContextListener listener : listeners) listener.contextActivated(taskscape);
		    } else {
		        MylarPlugin.log("Could not load taskscape", this);
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
    public void taskDeactivated(String id, String path) {
    	try {
	        IMylarContext taskscape = activeContext.getContextMap().get(id);        
	        if (taskscape != null) {
	            saveTaskscape(id, path); 
	            activeContext.getContextMap().remove(id);
	            for (IMylarContextListener listener : listeners) listener.contextDeactivated(taskscape);
	        }
    	} catch (Throwable t) {
    		MylarPlugin.log(t, "Could not deactivate context");
    	}
    }

    public void taskDeleted(String id, String path) {
        IMylarContext taskscape = activeContext.getContextMap().get(id);
        if (taskscape != null) {
            for (IMylarContextListener listener : listeners) listener.contextDeactivated(taskscape);
        }
        File f = getFileForTaskscape(path);
        if (f.exists()) {
        	f.delete();
        }
        eraseTaskscape(id);
    }
     
    private void eraseTaskscape(String id) {
        MylarContext taskscape = activeContext.getContextMap().get(id);
        if (taskscape == null) return;
        activeContext.getContextMap().remove(taskscape);
        // TODO: write out the taskscape 
//        saveTaskscape(id);
//        taskscape.reset();
        taskscape.reset();
        for (IMylarContextListener listener : listeners) listener.presentationSettingsChanging(IMylarContextListener.UpdateKind.UPDATE);
    }
    
     /**
       * @return false if the map could not be read for any reason
       */
    public MylarContext loadTaskscape(String taskId, String path) {
        MylarContext loadedTaskscape = externalizer.readXMLTaskscapeFromFile(getFileForTaskscape(path));
        if (loadedTaskscape == null) {
            return new MylarContext(taskId, MylarContextManager.getScalingFactors());
        } else {
            return loadedTaskscape;
        }
    }
 
    public void saveTaskscape(String taskId, String path) {
        MylarContext taskscape = activeContext.getContextMap().get(taskId);
        if (taskscape == null) {
            return;
        } else {
        	taskscape.collapse();
            externalizer.writeXMLTaskscapeToFile(taskscape, getFileForTaskscape(path));
        }
    }

    public File getFileForTaskscape(String path) {
        return new File(taskscapeStoreDirPath + File.separator + path + FILE_EXTENSION);
    }
    
    public String getMylarDir() {
        return "" + taskscapeStoreDirPath;
    }
    
    public IMylarContext getActiveContext() {
        return activeContext;
    }
   
    /**
     * @param kind
     */
    public void resetLandmarkRelationshipsOfKind(String reltationKind) {
//        throw new RuntimeException("unimplemented");
//        for (ITaskscapeNode landmark : composite.getLandmarks()) {
//            landmark.removeEdge(kind);
//        }
//        for (ITaskscapeListener listener : listeners) listener.relationshipsChanged();
    }

    public void refreshRelatedElements() {
//        throw new RuntimeException("unimplemented");
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

    public void updateSearchKindEnabled(List<AbstractRelationshipProvider> providers, int degreeOfSeparation) {
    	for(AbstractRelationshipProvider provider: providers){
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
    }

    public static ScalingFactors getScalingFactors() {
        return MylarContextManager.scalingFactors;
    }

    public void dumpInteractionHistoryForSelected() {
    	MylarPlugin.log("> interaction history: " + activeContext.getActiveNode().getDegreeOfInterest().getEvents(), this);
    }
    
    public void updateMylarDirContents(String prevDir) {    	    	    	
    	this.taskscapeStoreDirPath = MylarPlugin.getDefault().getUserDataDirectory();
		File prev = new File(prevDir);
		if (!prev.isDirectory()) {
			return;
		}
		File[] contents = prev.listFiles();
		File curr = new File(taskscapeStoreDirPath);

		for (File f : contents) {			
			if ( (f.getName().endsWith(".xml") && f.getName().startsWith("task")) || f.getName().startsWith("mylar")) {
				String name = curr.getAbsolutePath() + "/" + f.getName();				
				f.renameTo(new File(name));
			}
		}		
	}

	public void setNextEventIsRaiseChildren() {
		nextEventIsRaiseChildren = true;
	}

	public boolean hasActiveContext() {
		return activeContext.getContextMap().values().size() > 0;
	}

	public List<IMylarContextNode> getActiveLandmarks() {
		List<IMylarContextNode> allLandmarks = activeContext.getLandmarks();
		List<IMylarContextNode> acceptedLandmarks = new ArrayList<IMylarContextNode>();
		for (IMylarContextNode node : allLandmarks) {
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
            if (bridge.canBeLandmark(bridge.getObjectForHandle(node.getElementHandle()))) {
            	acceptedLandmarks.add(node);
        	}
        } 
		return acceptedLandmarks;
	}
	
    public Set<IMylarContextNode> getInterestingResources(IMylarContext context) {
        Set<IMylarContextNode> interestingFiles = new HashSet<IMylarContextNode>();
        List<IMylarContextNode> allIntersting = context.getInteresting();
        for (IMylarContextNode node : allIntersting) {
            if (MylarPlugin.getDefault().getStructureBridge(node.getStructureKind()).isDocument(node.getElementHandle())) {       
                interestingFiles.add(node);
            }
        }
        return interestingFiles;
    }
	
	public Set<IMylarContextNode> getActiveContextResources() {
		return getInterestingResources(activeContext);
    }
}
