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
import java.util.List;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.search.RelationshipProvider;


/**
 * @author Mik Kersten
 */
public class ContextManager {
    
    public static final String SOURCE_ID_MODEL_PROPAGATION = "org.eclipse.mylar.core.model.interest.propagation";
    public static final String SOURCE_ID_DECAY = "org.eclipse.mylar.core.model.interest.decay";
    public static final String SOURCE_ID_DECAY_CORRECTION = "org.eclipse.mylar.core.model.interest.decay.correction";
    
    public static final String SOURCE_ID_MODEL_ERROR = "org.eclipse.mylar.core.model.interest.propagation";
        
    public static final String CONTAINMENT_PROPAGATION_ID = "org.eclipse.mylar.core.model.edges.containment";
    
	public static final String FILE_EXTENSION = ".xml";
    
    private static final int MAX_PROPAGATION = 17; // TODO: parametrize this
    
    private int numInterestingErrors = 0;
    
    private CompositeContext activeTaskscape = new CompositeContext();
	
    private String taskscapeStoreDirPath;
	private boolean editorAutoCloseEnabled = false;
	private List<IMylarContextListener> listeners = new ArrayList<IMylarContextListener>();
	private List<IMylarContextListener> waitingListeners = new ArrayList<IMylarContextListener>();

	private String tempRaisedHandle = null;
    private boolean suppressListenerNotification = false;
    
    private ContextExternalizer externalizer = new ContextExternalizer();
	private boolean nextEventIsRaiseChildren;
    
    private static ScalingFactors scalingFactors = new ScalingFactors();
    
    public ContextManager() {
        taskscapeStoreDirPath = MylarPlugin.getDefault().getUserDataDirectory();
        
        File storeDir = new File(taskscapeStoreDirPath);
        storeDir.mkdirs();
    }

    public IMylarContextNode getActiveNode() {
        if (activeTaskscape != null) {
            return activeTaskscape.getActiveNode();
        } else {
            return null;
        }
    }
    
	public void addErrorPredictedInterest(String handle, String kind, boolean notify) { 
        if (numInterestingErrors > scalingFactors.getMaxNumInterestingErrors() 
            || activeTaskscape.getTaskscapeMap().isEmpty()) return;
        InteractionEvent errorEvent = new InteractionEvent(
                InteractionEvent.Kind.PREDICTION, 
                kind, handle, 
                SOURCE_ID_MODEL_ERROR,
                scalingFactors.getErrorInterest());
        handleInteractionEvent(errorEvent, false);
        numInterestingErrors++;
    }

    /**
     * TODO: worry about decay-related change if predicted interest dacays
     */
    public void removeErrorPredictedInterest(String handle, String kind, boolean notify) { 
        if (activeTaskscape.getTaskscapeMap().isEmpty()) return;
        if (handle == null) return;
        IMylarContextNode node = activeTaskscape.get(handle);
        if (node == null) return;
        if (node.getDegreeOfInterest().getValue() >= scalingFactors.getErrorInterest()) { // TODO: hack?
            InteractionEvent errorEvent = new InteractionEvent(
                    InteractionEvent.Kind.MANIPULATION, 
                    kind, handle, 
                    SOURCE_ID_MODEL_ERROR,
                    -scalingFactors.getErrorInterest());
            handleInteractionEvent(errorEvent, false);
            numInterestingErrors--;
            // TODO: this will results in double-notification
            if (notify) for (IMylarContextListener listener : listeners) listener.interestChanged(node);
        }
    } 

    public IMylarContextNode getNode(String elementHandle) {
        if (activeTaskscape != null) {
            return activeTaskscape.get(elementHandle);
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
        if (activeTaskscape.getTaskscapeMap().values().size() == 0) return null;
        if (suppressListenerNotification) return null;
        
        IMylarContextNode previous = activeTaskscape.get(event.getStructureHandle());
        float previousInterest = 0;
        float decayOffset = 0;
        if (previous != null) previousInterest = previous.getDegreeOfInterest().getValue();
        if (event.getKind().isUserEvent()) {
        	if (previousInterest < 0) {  // reset interest if not interesting
            	decayOffset = (-1)*(previous.getDegreeOfInterest().getValue());
        		activeTaskscape.addEvent(new InteractionEvent(
                        InteractionEvent.Kind.MANIPULATION, 
                        event.getStructureKind(),
                        event.getStructureHandle(), 
                        SOURCE_ID_DECAY_CORRECTION,
                        decayOffset));
            }
        }
        IMylarContextNode node = activeTaskscape.addEvent(event);
        List<IMylarContextNode> interestDelta = new ArrayList<IMylarContextNode>();
        if (propagateToParents && !event.getKind().equals(InteractionEvent.Kind.MANIPULATION)) {
        	propegateDoiToParents(node, previousInterest, decayOffset, 1, interestDelta); 
        }
        if (event.getKind().isUserEvent()) activeTaskscape.setActiveElement(node);

        interestDelta.add(node); // TODO: check that the order of these is sensible
        for (IMylarContextListener listener : listeners) listener.interestChanged(interestDelta);
        tempRaisedHandle = null;
         
        // TODO: don't call interestChanged if it's a landmark?
        if (previousInterest >= scalingFactors.getLandmark() && !node.getDegreeOfInterest().isLandmark()) {
            for (IMylarContextListener listener : listeners) listener.landmarkRemoved(node);
        } else if (previousInterest < scalingFactors.getLandmark() && node.getDegreeOfInterest().isLandmark()) {
            for (IMylarContextListener listener : listeners) listener.landmarkAdded(node);
        }
        
        if (nextEventIsRaiseChildren && event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
        	tempRaiseChildrenForSelected();
    		nextEventIsRaiseChildren = false;
    	} 
        
        return node;
    }
    
    private void propegateDoiToParents(IMylarContextNode node, float previousInterest, float decayOffset, int level, List<IMylarContextNode> elementDelta) {
        if (level > MAX_PROPAGATION || node == null || node.getDegreeOfInterest().getValue() <= 0) return;// || "/".equals(node.getElementHandle())) return;         
        
        // TODO: move this above?
        if (previousInterest >= scalingFactors.getLandmark() && !node.getDegreeOfInterest().isLandmark()) {
            for (IMylarContextListener listener : listeners) listener.landmarkRemoved(node);
        } else if (previousInterest < scalingFactors.getLandmark() && node.getDegreeOfInterest().isLandmark()) {
            for (IMylarContextListener listener : listeners) listener.landmarkAdded(node);
        }
        
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
            IMylarContextNode previous = activeTaskscape.get(propagationEvent.getStructureHandle());
            if (previous != null && previous.getDegreeOfInterest() != null) previousInterest = previous.getDegreeOfInterest().getValue();
            CompositeContextNode parentNode = (CompositeContextNode)activeTaskscape.addEvent(propagationEvent);
            elementDelta.add(0, parentNode);
            propegateDoiToParents(parentNode, previousInterest, decayOffset, level, elementDelta);//adapter.getResourceExtension(), adapter.getParentHandle(parentHandle), level, doi, parentChain);    
        }
    }

    public List<IMylarContextNode> findCompositesForNodes(List<ContextNode> nodes) {
        List<IMylarContextNode> composites = new ArrayList<IMylarContextNode>();
        for (ContextNode node : nodes) {
            composites.add(activeTaskscape.get(node.getElementHandle()));
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
    public void taskActivated(Context taskscape) {
        activeTaskscape.getTaskscapeMap().put(taskscape.getId(), taskscape);
    } 
    
    public void taskActivated(String id, String path) {
	    suppressListenerNotification = true;
	    Context taskscape = activeTaskscape.getTaskscapeMap().get(id);
	    if (taskscape == null) taskscape = loadTaskscape(id, path);
	    if (taskscape != null) {
	        activeTaskscape.getTaskscapeMap().put(id, taskscape);
	        for (IMylarContextListener listener : listeners) listener.contextActivated(taskscape);
	    } else {
	        MylarPlugin.log("Could not load taskscape", this);
	    }
	    suppressListenerNotification = false;
	    listeners.addAll(waitingListeners);
    }

    /**
     * @param id
     */
    public void taskDeactivated(String id, String path) {
        IMylarContext taskscape = activeTaskscape.getTaskscapeMap().get(id);        
        if (taskscape != null) {
            saveTaskscape(id, path); 
            activeTaskscape.getTaskscapeMap().remove(id);
            for (IMylarContextListener listener : listeners) listener.contextDeactivated(taskscape);
        }
    }

    public void taskDeleted(String id, String path) {
        IMylarContext taskscape = activeTaskscape.getTaskscapeMap().get(id);
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
        Context taskscape = activeTaskscape.getTaskscapeMap().get(id);
        if (taskscape == null) return;
        activeTaskscape.getTaskscapeMap().remove(taskscape);
        // TODO: write out the taskscape 
//        saveTaskscape(id);
//        taskscape.reset();
        taskscape.reset();
        for (IMylarContextListener listener : listeners) listener.presentationSettingsChanging(IMylarContextListener.UpdateKind.UPDATE);
    }
    
     /**
       * @return false if the map could not be read for any reason
       */
    public Context loadTaskscape(String taskId, String path) {
        Context loadedTaskscape = externalizer.readXMLTaskscapeFromFile(getFileForTaskscape(path));
        if (loadedTaskscape == null) {
            return new Context(taskId, ContextManager.getScalingFactors());
        } else {
            return loadedTaskscape;
        }
    }
 
    public void saveTaskscape(String taskId, String path) {
        Context taskscape = activeTaskscape.getTaskscapeMap().get(taskId);
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
    
    public CompositeContext getActiveContext() {
        return activeTaskscape;
    }
  
    public List<RelationshipProvider> getRelationshipProviders() {
        List<RelationshipProvider> providers = new ArrayList<RelationshipProvider>();
        for (IMylarContextListener listener : listeners) {
            if (listener instanceof RelationshipProvider) providers.add((RelationshipProvider)listener);
        }
        return providers;
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

    public boolean isTempRaised(String handleIdentifier) {
        if (handleIdentifier == null) return false;
        if (handleIdentifier.equals(tempRaisedHandle)) {
            return true;
        } else {
            return false;
        }
    }
    
    public void tempRaiseChildrenForSelected() {
        if (activeTaskscape.getActiveNode() == null) return;
            
        tempRaisedHandle = activeTaskscape.getActiveNode().getElementHandle();
        for (IMylarContextListener listnener : listeners) listnener.presentationSettingsChanged(IMylarContextListener.UpdateKind.FILTER);
//        IMylarStructureBridge adapter = MylarPlugin.getDefault().getStructureBridge(composite.getActiveElement().getKind());
//        String parentHandle = adapter.getParentHandle(tempRaisedHandle);
//        ITaskscapeNode parentNode = getNode(parentHandle);
//        if (parentNode != null)  {
//            for (ITaskscapeListener listnener : listeners) listnener.interestChanged(parentNode);
//        }
    }

    public void refreshRelatedElements() {
//        throw new RuntimeException("unimplemented");
        for (RelationshipProvider provider : getRelationshipProviders()) {
            if (provider.isEnabled()) {
                MylarPlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
            }
            for (IMylarContextNode node : activeTaskscape.getLandmarks()) provider.landmarkAdded(node);
        }
    }

    public void updateSearchKindEnabled(RelationshipProvider provider, boolean on) {
        if (!on) {
            MylarPlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
        } else {
            for (IMylarContextNode node : activeTaskscape.getLandmarks()) provider.landmarkAdded(node);
        }
    }

    public String getTempRaisedHandle() {
        return tempRaisedHandle;
    }

    public static ScalingFactors getScalingFactors() {
        return ContextManager.scalingFactors;
    }

    public void dumpInteractionHistoryForSelected() {
    	MylarPlugin.log("> interaction history: " + activeTaskscape.getActiveNode().getDegreeOfInterest().getEvents(), this);
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
}
