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
package org.eclipse.mylar.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.internal.CompositeTaskscape;
import org.eclipse.mylar.core.model.internal.CompositeTaskscapeNode;
import org.eclipse.mylar.core.model.internal.ScalingFactors;
import org.eclipse.mylar.core.model.internal.Taskscape;
import org.eclipse.mylar.core.model.internal.TaskscapeExternalizer;
import org.eclipse.mylar.core.model.internal.TaskscapeNode;
import org.eclipse.mylar.core.search.RelationshipProvider;


/**
 * @author Mik Kersten
 */
public class TaskscapeManager {
    
    public static final String SOURCE_ID_MODEL_PROPAGATION = "org.eclipse.mylar.core.model.interest.propagation";
    public static final String SOURCE_ID_DECAY_CORRECTION = "org.eclipse.mylar.core.model.interest.decay.correction";
    
    public static final String SOURCE_ID_MODEL_ERROR = "org.eclipse.mylar.core.model.interest.propagation";
        
    public static final String CONTAINMENT_PROPAGATION_ID = "org.eclipse.mylar.core.model.edges.containment";
    
	public static final String FILE_EXTENSION = ".xml";
    
    private static final int MAX_PROPAGATION = 17; // TODO: parametrize this
    
    private int numInterestingErrors = 0;
    
    private CompositeTaskscape activeTaskscape = new CompositeTaskscape();
	
    private String taskscapeStoreDirPath;
	private boolean editorAutoCloseEnabled = false;
	private List<ITaskscapeListener> listeners = new ArrayList<ITaskscapeListener>();
//    private List<IInteractionListener> interactionListeners = new ArrayList<IInteractionListener>();
    private String tempRaisedHandle = null;
    private boolean suppressSelections = false;
    
    private TaskscapeExternalizer externalizer = new TaskscapeExternalizer();
    
    private static ScalingFactors scalingFactors = new ScalingFactors();
    
    public TaskscapeManager() {
        taskscapeStoreDirPath = MylarPlugin.getDefault().getUserDataDirectory();
        
        File storeDir = new File(taskscapeStoreDirPath);
        storeDir.mkdirs();
    }

    public ITaskscapeNode getActiveNode() {
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
//        ITaskscapeNode node = activeTaskscape.addEvent(errorEvent);
        handleInteractionEvent(errorEvent);
        numInterestingErrors++;
//        if (notify) for (ITaskscapeListener listener : listeners) listener.interestChanged(node); 
    }

    /**
     * TODO: worry about decay-related change if predicted interest dacays
     */
    public void removeErrorPredictedInterest(String handle, String kind, boolean notify) { 
        if (activeTaskscape.getTaskscapeMap().isEmpty()) return;
        if (handle == null) return;
        ITaskscapeNode node = activeTaskscape.get(handle);
        if (node == null) return;
        if (node.getDegreeOfInterest().getValue() >= scalingFactors.getErrorInterest()) { // TODO: hack?
            InteractionEvent errorEvent = new InteractionEvent(
                    InteractionEvent.Kind.PREDICTION, 
                    kind, handle, 
                    SOURCE_ID_MODEL_ERROR,
                    -scalingFactors.getErrorInterest());
            handleInteractionEvent(errorEvent);
//            activeTaskscape.addEvent(errorEvent);
            numInterestingErrors--;
            // TODO: this will results in double-notification
            if (notify) for (ITaskscapeListener listener : listeners) listener.interestChanged(node);
        }
    } 

    public ITaskscapeNode getNode(String elementHandle) {
        if (activeTaskscape != null) {
            return activeTaskscape.get(elementHandle);
        } else {
            return null;
        }
    }

    /**
     * TODO: consider moving this into the taskscape?
     */
    public ITaskscapeNode handleInteractionEvent(InteractionEvent event) {
        if (event.getKind() == InteractionEvent.Kind.COMMAND) return null;
        if (activeTaskscape.getTaskscapeMap().values().size() == 0) return null;
        if (suppressSelections) return null;
        
        ITaskscapeNode previous = activeTaskscape.get(event.getStructureHandle());
        float previousInterest = 0;
        if (previous != null && previous.getDegreeOfInterest() != null) {
        	previousInterest = previous.getDegreeOfInterest().getValue();
        	// deal with decay
        	float decay = previous.getDegreeOfInterest().getDecayValue();
        	// reset interest if not interesting
        	if (previousInterest - decay < 0) {
        		activeTaskscape.addEvent(new InteractionEvent(
                        InteractionEvent.Kind.MANIPULATION, 
                        event.getStructureKind(),
                        event.getStructureHandle(), 
                        SOURCE_ID_MODEL_PROPAGATION,
                        -1*(previousInterest-decay)));
        	}
        }
        ITaskscapeNode node = activeTaskscape.addEvent(event);
        List<ITaskscapeNode> interestDelta = new ArrayList<ITaskscapeNode>();
        propegateDoiToParents(node, previousInterest, 1, interestDelta); 
        activeTaskscape.setActiveElement(node);

        interestDelta.add(node); // TODO: check that the order of these is sensible
        for (ITaskscapeListener listener : listeners) listener.interestChanged(interestDelta);
        tempRaisedHandle = null;
        return node;
    }
    
    private void propegateDoiToParents(ITaskscapeNode node, float previousInterest, int level, List<ITaskscapeNode> elementDelta) {
        if (level > MAX_PROPAGATION || node == null || node.getDegreeOfInterest().getValue() <= 0) return;// || "/".equals(node.getElementHandle())) return;         
        
        // TODO: move this above?
        if (previousInterest >= scalingFactors.getLandmark() && !node.getDegreeOfInterest().isLandmark()) {
            for (ITaskscapeListener listener : listeners) listener.landmarkRemoved(node);
        } else if (previousInterest < scalingFactors.getLandmark() && node.getDegreeOfInterest().isLandmark()) {
            for (ITaskscapeListener listener : listeners) listener.landmarkAdded(node);
        }
        
        level++; // original is 1st level
        float propagatedIncrement = scalingFactors.getParentPropagationIncrement(level);
      
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
            ITaskscapeNode previous = activeTaskscape.get(propagationEvent.getStructureHandle());
            if (previous != null && previous.getDegreeOfInterest() != null) previousInterest = previous.getDegreeOfInterest().getValue();
            CompositeTaskscapeNode parentNode = (CompositeTaskscapeNode)activeTaskscape.addEvent(propagationEvent);
            elementDelta.add(0, parentNode);
            propegateDoiToParents(parentNode, previousInterest, level, elementDelta);//adapter.getResourceExtension(), adapter.getParentHandle(parentHandle), level, doi, parentChain);    
        }
    }

    public List<ITaskscapeNode> findCompositesForNodes(List<TaskscapeNode> nodes) {
        List<ITaskscapeNode> composites = new ArrayList<ITaskscapeNode>();
        for (TaskscapeNode node : nodes) {
            composites.add(activeTaskscape.get(node.getElementHandle()));
        }
        return composites;
    }
	
	public void addListener(ITaskscapeListener listener) {
        if (listener != null) {
            listeners.add(listener);            
        } else {
            MylarPlugin.log(this, "attempted to add null lisetener");
        }
	}
	
	public void removeListener(ITaskscapeListener listener) {
		listeners.remove(listener);
	}

    
    public void removeAllListeners() {
        listeners.clear();
    }
    
    public void notifyPostPresentationSettingsChange(ITaskscapeListener.UpdateKind kind) { 
        for (ITaskscapeListener listener : listeners) listener.presentationSettingsChanged(kind);
    }
    
    public void notifyActivePresentationSettingsChange(ITaskscapeListener.UpdateKind kind) {
        for (ITaskscapeListener listener : listeners) listener.presentationSettingsChanging(kind);
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
    public void taskActivated(Taskscape taskscape) {
        activeTaskscape.getTaskscapeMap().put(taskscape.getId(), taskscape);
    } 
    
    public void taskActivated(String id, String path) {
        suppressSelections = true;
        Taskscape taskscape = activeTaskscape.getTaskscapeMap().get(id);
        if (taskscape == null) taskscape = loadTaskscape(id, path);
        if (taskscape != null) {
            activeTaskscape.getTaskscapeMap().put(id, taskscape);
            for (ITaskscapeListener listener : listeners) listener.taskscapeActivated(taskscape);
        } else {
            MylarPlugin.log(this, "Could not load taskscape");
        }
        suppressSelections = false;
    }

    /**
     * @param id
     */
    public void taskDeactivated(String id, String path) {
        ITaskscape taskscape = activeTaskscape.getTaskscapeMap().get(id);        
        if (taskscape != null) {
            saveTaskscape(id, path); 
            activeTaskscape.getTaskscapeMap().remove(id);
            for (ITaskscapeListener listener : listeners) listener.taskscapeDeactivated(taskscape);
        }
    }

    public void taskDeleted(String id, String path) {
        ITaskscape taskscape = activeTaskscape.getTaskscapeMap().get(id);
        if (taskscape != null) {
            for (ITaskscapeListener listener : listeners) listener.taskscapeDeactivated(taskscape);
        }
        eraseTaskscape(id);
    }
     
    private void eraseTaskscape(String id) {
        Taskscape taskscape = activeTaskscape.getTaskscapeMap().get(id);
        if (taskscape == null) return;
        activeTaskscape.getTaskscapeMap().remove(taskscape);
        // TODO: write out the taskscape 
//        saveTaskscape(id);
//        taskscape.reset();
        taskscape.reset();
        for (ITaskscapeListener listener : listeners) listener.presentationSettingsChanging(ITaskscapeListener.UpdateKind.UPDATE);
    }
    
     /**
       * @return false if the map could not be read for any reason
       */
    public Taskscape loadTaskscape(String taskId, String path) {
        Taskscape loadedTaskscape = externalizer.readXMLTaskscapeFromFile(getFileForTaskscape(path));
        if (loadedTaskscape == null) {
            return new Taskscape(taskId, TaskscapeManager.getScalingFactors());
        } else {
            return loadedTaskscape;
        }
    }
 
    public void saveTaskscape(String taskId, String path) {
        Taskscape taskscape = activeTaskscape.getTaskscapeMap().get(taskId);
        if (taskscape == null) {
            return;
        } else {
            externalizer.writeXMLTaskscapeToFile(taskscape, getFileForTaskscape(path));
        }
    }

    public File getFileForTaskscape(String path) {
        return new File(taskscapeStoreDirPath + File.separator + path + FILE_EXTENSION);
    }
    
    public String getMylarDir() {
        return "" + taskscapeStoreDirPath;
    }
    
    public CompositeTaskscape getActiveTaskscape() {
        return activeTaskscape;
    }
  
    public List<RelationshipProvider> getRelationshipProviders() {
        List<RelationshipProvider> providers = new ArrayList<RelationshipProvider>();
        for (ITaskscapeListener listener : listeners) {
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
        for (ITaskscapeListener listnener : listeners) listnener.presentationSettingsChanged(ITaskscapeListener.UpdateKind.FILTER);
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
                MylarPlugin.getTaskscapeManager().resetLandmarkRelationshipsOfKind(provider.getId());
            }
            for (ITaskscapeNode node : activeTaskscape.getLandmarks()) provider.landmarkAdded(node);
        }
    }

    public void updateSearchKindEnabled(RelationshipProvider provider, boolean on) {
        if (!on) {
            MylarPlugin.getTaskscapeManager().resetLandmarkRelationshipsOfKind(provider.getId());
        } else {
            for (ITaskscapeNode node : activeTaskscape.getLandmarks()) provider.landmarkAdded(node);
        }
    }

    public String getTempRaisedHandle() {
        return tempRaisedHandle;
    }

    public static ScalingFactors getScalingFactors() {
        return TaskscapeManager.scalingFactors;
    }

    public void dumpInteractionHistoryForSelected() {
    	MylarPlugin.log("> interaction history: " + activeTaskscape.getActiveNode().getDegreeOfInterest().getEvents());
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
}
