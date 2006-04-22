/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylar.internal.core.util.ITimerThreadListener;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.TimerThread;
import org.eclipse.mylar.provisional.core.AbstractRelationProvider;
import org.eclipse.mylar.provisional.core.IInteractionEventListener;
import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarRelation;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.InterestComparator;
import org.eclipse.mylar.provisional.core.MylarPlugin;

/**
 * This is the core class resposible for context management.
 * 
 * @author Mik Kersten
 */
public class MylarContextManager {

	// TODO: move constants
	
	private static final String CONTEXT_FILENAME_ENCODING = "UTF-8";

	public static final String ACTIVITY_DELTA_DEACTIVATED = "deactivated";

	public static final String ACTIVITY_DELTA_ACTIVATED = "activated";

	public static final String ACTIVITY_ORIGIN_ID = "org.eclipse.mylar.core";

	public static final String ACTIVITY_HANDLE_ATTENTION = "attention";

	public static final String ACTIVITY_HANDLE_LIFECYCLE = "lifecycle";
	
	public static final String ACTIVITY_DELTA_STARTED = "started";
	
	public static final String ACTIVITY_DELTA_STOPPED = "stopped";
	
	public static final String ACTIVITY_STRUCTURE_KIND = "context";

	private static final int TIMEOUT_INACTIVITY_MILLIS = 2 * 60 * 1000;

	public static final String CONTEXT_HISTORY_FILE_NAME = "context-history";

	public static final String SOURCE_ID_MODEL_PROPAGATION = "org.eclipse.mylar.core.model.interest.propagation";

	public static final String SOURCE_ID_DECAY = "org.eclipse.mylar.core.model.interest.decay";

	public static final String SOURCE_ID_DECAY_CORRECTION = "org.eclipse.mylar.core.model.interest.decay.correction";

	public static final String SOURCE_ID_MODEL_ERROR = "org.eclipse.mylar.core.model.interest.propagation";

	public static final String CONTAINMENT_PROPAGATION_ID = "org.eclipse.mylar.core.model.edges.containment";

	public static final String CONTEXT_FILE_EXTENSION = ".xml";

	private static final int MAX_PROPAGATION = 17; // TODO: parametrize this

	private int numInterestingErrors = 0;

	private List<String> errorElementHandles = new ArrayList<String>();

	private boolean contextCapturePaused = false;

	private CompositeContext currentContext = new CompositeContext();

	private MylarContext activityMetaContext = null;
	
	private ActivityListener activityListener;

	private int inactivityTimeout = TIMEOUT_INACTIVITY_MILLIS;

	private List<IMylarContextListener> activityMetaContextListeners = new ArrayList<IMylarContextListener>();

	private List<IMylarContextListener> listeners = new ArrayList<IMylarContextListener>();
	
	private List<IMylarContextListener> waitingListeners = new ArrayList<IMylarContextListener>();

	// TODO: move
	private List<IActionExecutionListener> actionExecutionListeners = new ArrayList<IActionExecutionListener>();

	private boolean suppressListenerNotification = false;

	private MylarContextExternalizer externalizer = new MylarContextExternalizer();

	private boolean activationHistorySuppressed = false;

	public static final String CONTEXT_HANDLE_DELIM = "-";

	private static ScalingFactors scalingFactors = new ScalingFactors();

	private final ShellLifecycleListener shellLifecycleListener;
		
	private class ActivityListener implements ITimerThreadListener, IInteractionEventListener, IMylarContextListener {

		private TimerThread timer;
		private int sleepPeriod = 60000;
		private boolean isStalled;

		public ActivityListener(int millis) {
			timer = new TimerThread(millis);
			timer.addListener(this);
			timer.start();
			sleepPeriod = millis;
			MylarPlugin.getDefault().addInteractionListener(this);
		}

		public void fireTimedOut() {
			if (!isStalled) {
				handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND, ACTIVITY_STRUCTURE_KIND,
						ACTIVITY_HANDLE_ATTENTION, ACTIVITY_ORIGIN_ID, null, ACTIVITY_DELTA_DEACTIVATED, 1f));
			}
			isStalled = true;
		}

		public void intervalElapsed() {
			// ignore

		}

		public void interactionObserved(InteractionEvent event) {
			timer.resetTimer();
			if (isStalled) {
				handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND, ACTIVITY_STRUCTURE_KIND,
						ACTIVITY_HANDLE_ATTENTION, ACTIVITY_ORIGIN_ID, null, ACTIVITY_DELTA_ACTIVATED, 1f));
			}
			isStalled = false;
		}

		public void startObserving() {
		}

		public void stopTimer() {
			timer.kill();
			MylarPlugin.getDefault().removeInteractionListener(this);
		}

		public void stopObserving() {
		}

		public void setTimeout(int millis) {
			timer.kill();
			sleepPeriod = millis;
			timer = new TimerThread(millis);
			timer.addListener(this);
			timer.start();
		}

		public void contextActivated(IMylarContext context) {
			interactionObserved(null);
			timer.kill();
			timer = new TimerThread(sleepPeriod);
			timer.addListener(this);
			timer.start();
		}

		public void contextDeactivated(IMylarContext context) {
			interactionObserved(null);
			timer.kill();
		}

		public void presentationSettingsChanging(UpdateKind kind) {
			// ignore
			
		}

		public void presentationSettingsChanged(UpdateKind kind) {
			// ignore
			
		}

		public void interestChanged(IMylarElement element) {
			// ignore
			
		}

		public void interestChanged(List<IMylarElement> elements) {
			// ignore
			
		}

		public void nodeDeleted(IMylarElement element) {
			// ignore
			
		}

		public void landmarkAdded(IMylarElement element) {
			// ignore
			
		}

		public void landmarkRemoved(IMylarElement element) {
			// ignore
			
		}

		public void edgesChanged(IMylarElement element) {
			// ignore
			
		}
	}

	public MylarContextManager() {
		File storeDir = new File(MylarPlugin.getDefault().getDataDirectory());
		storeDir.mkdirs();

		activityMetaContext = externalizer.readContextFromXML(CONTEXT_HISTORY_FILE_NAME,
				getFileForContext(CONTEXT_HISTORY_FILE_NAME));
		if (activityMetaContext == null) {
			resetActivityHistory();
		}
		for (IMylarContextListener listener : activityMetaContextListeners) {
			listener.contextActivated(activityMetaContext);
		}

		shellLifecycleListener = new ShellLifecycleListener(this);
		
		activityListener = new ActivityListener(TIMEOUT_INACTIVITY_MILLIS);// INACTIVITY_TIMEOUT_MILLIS);
		this.addListener(activityListener);
		activityListener.startObserving();
	}

	public void handleActivityMetaContextEvent(InteractionEvent event) {
		IMylarElement element = activityMetaContext.parseEvent(event);
		for (IMylarContextListener listener : activityMetaContextListeners) {
			try {
				listener.interestChanged(element);
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "context listener failed", false);
			}
		}
	} 

 	public void setInactivityTimeout(int millis) {
		inactivityTimeout = millis;
		activityListener.setTimeout(millis);
	}

	/**
	 * @return timeout in mililiseconds
	 */
	public int getInactivityTimeout() {
		return inactivityTimeout;
	}

	public IMylarElement getActiveElement() {
		if (currentContext != null) {
			return currentContext.getActiveNode();
		} else {
			return null;
		}
	}

	public void addErrorPredictedInterest(String handle, String kind, boolean notify) {
		if (numInterestingErrors > scalingFactors.getMaxNumInterestingErrors()
				|| currentContext.getContextMap().isEmpty())
			return;
		InteractionEvent errorEvent = new InteractionEvent(InteractionEvent.Kind.PROPAGATION, kind, handle,
				SOURCE_ID_MODEL_ERROR, scalingFactors.getErrorInterest());
		handleInteractionEvent(errorEvent, true);
		errorElementHandles.add(handle);
		numInterestingErrors++;
	}

	/**
	 * TODO: worry about decay-related change if predicted interest dacays
	 */
	public void removeErrorPredictedInterest(String handle, String kind, boolean notify) {
		if (currentContext.getContextMap().isEmpty())
			return;
		if (handle == null)
			return;
		IMylarElement node = currentContext.get(handle);
		if (node != null && node.getInterest().isInteresting() && errorElementHandles.contains(handle)) {
			InteractionEvent errorEvent = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle,
					SOURCE_ID_MODEL_ERROR, -scalingFactors.getErrorInterest());
			handleInteractionEvent(errorEvent, true);
			numInterestingErrors--;
			errorElementHandles.remove(handle);
			// TODO: this results in double-notification
			if (notify)
				for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners))
					listener.interestChanged(node);
		}
	}

	public IMylarElement getElement(String elementHandle) {
		if (currentContext != null && elementHandle != null) {
			return currentContext.get(elementHandle);
		} else {
			return null;
		}
	}

	public IMylarElement handleInteractionEvent(InteractionEvent event) {
		return handleInteractionEvent(event, true);
	}

	public IMylarElement handleInteractionEvent(InteractionEvent event, boolean propagateToParents) {
		return handleInteractionEvent(event, propagateToParents, true);
	}

	public void handleInteractionEvents(List<InteractionEvent> events, boolean propagateToParents) {
		Set<IMylarElement> compositeDelta = new HashSet<IMylarElement>();
		for (InteractionEvent event : events) {
			compositeDelta.addAll(internalHandleInteractionEvent(event, propagateToParents));			
		}
		notifyInterestDelta(new ArrayList<IMylarElement>(compositeDelta));
	}

	public IMylarElement handleInteractionEvent(InteractionEvent event, boolean propagateToParents,
			boolean notifyListeners) {
		List<IMylarElement> interestDelta = internalHandleInteractionEvent(event, propagateToParents);
		if (notifyListeners) {
			notifyInterestDelta(interestDelta);
		}
		return currentContext.get(event.getStructureHandle());
	}
	
	private List<IMylarElement> internalHandleInteractionEvent(InteractionEvent event, boolean propagateToParents) {
		if (contextCapturePaused || event.getKind() == InteractionEvent.Kind.COMMAND || !isContextActive()
				|| suppressListenerNotification)
			return Collections.emptyList();
  
		IMylarElement previous = currentContext.get(event.getStructureHandle());
		float previousInterest = 0;
		boolean previouslyPredicted = false;
		boolean previouslyPropagated = false;
		float decayOffset = 0;
		if (previous != null) {
			previousInterest = previous.getInterest().getValue();
			previouslyPredicted = previous.getInterest().isPredicted();
			previouslyPropagated = previous.getInterest().isPropagated();
		}
		if (event.getKind().isUserEvent()) {
			decayOffset = ensureIsInteresting(event.getContentType(), event.getStructureHandle(), previous, previousInterest);
		}
		IMylarElement element = currentContext.addEvent(event);
		List<IMylarElement> interestDelta = new ArrayList<IMylarElement>();
		if (propagateToParents && !event.getKind().equals(InteractionEvent.Kind.MANIPULATION)) {
			propegateInterestToParents(event.getKind(), element, previousInterest, decayOffset, 1, interestDelta);
		}
		if (event.getKind().isUserEvent())
			currentContext.setActiveElement(element);

		if (isInterestDelta(previousInterest, previouslyPredicted, previouslyPropagated, element)) {
			interestDelta.add(element); 
		}

		checkForLandmarkDeltaAndNotify(previousInterest, element);
		return interestDelta;
	}

	private float ensureIsInteresting(String contentType, String handle, IMylarElement previous, float previousInterest) {
		float decayOffset = 0;
		if (previousInterest < 0) { // reset interest if not interesting
			decayOffset = (-1) * (previous.getInterest().getValue());
			currentContext.addEvent(new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
					contentType, handle, SOURCE_ID_DECAY_CORRECTION, decayOffset));
		}
		return decayOffset;
	}
	
	private void notifyInterestDelta(List<IMylarElement> interestDelta) {
		if (!interestDelta.isEmpty()) {
			for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
				listener.interestChanged(interestDelta);
			}
		}
	}

	protected boolean isInterestDelta(float previousInterest, boolean previouslyPredicted, boolean previouslyPropagated,
			IMylarElement node) {
		float currentInterest = node.getInterest().getValue();
		if (previousInterest <= 0 && currentInterest > 0) {
			return true;
		} else if (previousInterest > 0 && currentInterest <= 0) {
			return true;
		} else if (currentInterest > 0 && previouslyPredicted && !node.getInterest().isPredicted()) {
			return true;
		} else if (currentInterest > 0 && previouslyPropagated && !node.getInterest().isPropagated()) {
			return true;
		} else {
			return false;
		}
	}

	protected void checkForLandmarkDeltaAndNotify(float previousInterest, IMylarElement node) {
		// TODO: don't call interestChanged if it's a landmark?
		IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
		if (bridge.canBeLandmark(node.getHandleIdentifier())) {
			if (previousInterest >= scalingFactors.getLandmark() && !node.getInterest().isLandmark()) {
				for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners))
					listener.landmarkRemoved(node);
			} else if (previousInterest < scalingFactors.getLandmark() && node.getInterest().isLandmark()) {
				for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners))
					listener.landmarkAdded(node);
			}
		}
	}

	private void propegateInterestToParents(InteractionEvent.Kind kind, IMylarElement node, float previousInterest, float decayOffset, int level,
			List<IMylarElement> interestDelta) {
		if (level > MAX_PROPAGATION || node == null || node.getInterest().getValue() <= 0) {
			return;
		}

		checkForLandmarkDeltaAndNotify(previousInterest, node);

		level++; // original is 1st level
		float propagatedIncrement = node.getInterest().getValue() - previousInterest + decayOffset;

		IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
		
		String parentHandle = bridge.getParentHandle(node.getHandleIdentifier());
		if (parentHandle != null) {
			InteractionEvent propagationEvent = new InteractionEvent(InteractionEvent.Kind.PROPAGATION, bridge
					.getContentType(node.getHandleIdentifier()), bridge.getParentHandle(node.getHandleIdentifier()),
					SOURCE_ID_MODEL_PROPAGATION, CONTAINMENT_PROPAGATION_ID, propagatedIncrement);
			IMylarElement previous = currentContext.get(propagationEvent.getStructureHandle());
			if (previous != null && previous.getInterest() != null) {
				previousInterest = previous.getInterest().getValue();
			}
			CompositeContextElement parentNode = (CompositeContextElement) currentContext.addEvent(propagationEvent);
			if (kind.isUserEvent() && parentNode.getInterest().getEncodedValue() < scalingFactors.getInteresting()) {
				float parentOffset = ((-1) * parentNode.getInterest().getEncodedValue()) + 1;
				currentContext.addEvent(new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
						parentNode.getContentType(), parentNode.getHandleIdentifier(), SOURCE_ID_DECAY_CORRECTION, parentOffset));
//				ensureIsInteresting(parentNode.getContentType(), parentNode.getHandleIdentifier(), parentNode, parentNode.getInterest().getEncodedValue());
			}
			if (isInterestDelta(previousInterest, previous.getInterest().isPredicted(), previous.getInterest()
					.isPropagated(), parentNode)) {
				interestDelta.add(0, parentNode);
			}
			propegateInterestToParents(kind, parentNode, previousInterest, decayOffset, level, interestDelta);// adapter.getResourceExtension(),
		}
	}

	public List<IMylarElement> findCompositesForNodes(List<MylarContextElement> nodes) {
		List<IMylarElement> composites = new ArrayList<IMylarElement>();
		for (MylarContextElement node : nodes) {
			composites.add(currentContext.get(node.getHandleIdentifier()));
		}
		return composites;
	}

	public void addListener(IMylarContextListener listener) {
		if (listener != null) {
			if (suppressListenerNotification && !waitingListeners.contains(listener)) {
				waitingListeners.add(listener);
			} else {
				if (!listeners.contains(listener))
					listeners.add(listener);
			}
		} else {
			MylarStatusHandler.log("attempted to add null lisetener", this);
		}
	}

	public void removeListener(IMylarContextListener listener) {
		listeners.remove(listener);
	}
	
	public void addActivityMetaContextListener(IMylarContextListener listener) {
		activityMetaContextListeners.add(listener);
	}
	
	public void removeActivityMetaContextListener(IMylarContextListener listener) {
		activityMetaContextListeners.remove(listener);
	}
	 
	public void removeAllListeners() {
		listeners.clear();
	}
	public void notifyPostPresentationSettingsChange(IMylarContextListener.UpdateKind kind) {
		for (IMylarContextListener listener : listeners)
			listener.presentationSettingsChanged(kind);
	}

	public void notifyActivePresentationSettingsChange(IMylarContextListener.UpdateKind kind) {
		for (IMylarContextListener listener : listeners)
			listener.presentationSettingsChanging(kind);
	}

	/**
	 * Public for testing, activiate via handle
	 */
	public void activateContext(MylarContext context) {
		currentContext.getContextMap().put(context.getHandleIdentifier(), context);
		if (!activationHistorySuppressed) {
			handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND, ACTIVITY_STRUCTURE_KIND, context
					.getHandleIdentifier(), ACTIVITY_ORIGIN_ID, null, ACTIVITY_DELTA_ACTIVATED, 1f));
		}
	}

	public List<MylarContext> getActiveContexts() {
		return new ArrayList<MylarContext>(currentContext.getContextMap().values());
	}

	public void activateContext(String handleIdentifier) {
		try {
			suppressListenerNotification = true;
			MylarContext context = currentContext.getContextMap().get(handleIdentifier);
			if (context == null)
				context = loadContext(handleIdentifier);
			if (context != null) {
				activateContext(context);
				for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
					try {
						listener.contextActivated(context);
					} catch (Exception e) {
						MylarStatusHandler.fail(e, "context listener failed", false);
					}
				}
				refreshRelatedElements();
			} else {
				MylarStatusHandler.log("Could not load context", this);
			}
			suppressListenerNotification = false;
			listeners.addAll(waitingListeners);
			waitingListeners.clear();
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "Could not activate context");
		}
	}

	/**
	 * Could load in the context and inspect it, but this is cheaper.
	 */
	public boolean hasContext(String path) {
		File contextFile = getFileForContext(path);
		return contextFile.exists() && contextFile.length() > 0;
	}

	void deactivateAllContexts() {
		for (String handleIdentifier : currentContext.getContextMap().keySet()) {
			deactivateContext(handleIdentifier);
		}
	}
	
	public void deactivateContext(String handleIdentifier) {
		try {
			IMylarContext context = currentContext.getContextMap().get(handleIdentifier);
			if (context != null) {			
				saveContext(handleIdentifier);
				currentContext.getContextMap().remove(handleIdentifier);

				setContextCapturePaused(true);
				for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
					try {
						listener.contextDeactivated(context);
					} catch (Exception e) {
						MylarStatusHandler.fail(e, "context listener failed", false);
					}
				}
				setContextCapturePaused(false);
			}
			if (!activationHistorySuppressed) {
				handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND, ACTIVITY_STRUCTURE_KIND,
						handleIdentifier, ACTIVITY_ORIGIN_ID, null, ACTIVITY_DELTA_DEACTIVATED, 1f));
			}
			saveActivityHistoryContext();
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "Could not deactivate context");
		}
	}

	public void deleteContext(String handleIdentifier) {
		IMylarContext context = currentContext.getContextMap().get(handleIdentifier);
		eraseContext(handleIdentifier, false);
		if (context != null) {
			// TODO: this notification is redundant with eraseContext's
			setContextCapturePaused(true);
			for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
				listener.contextDeactivated(context);
			}
			setContextCapturePaused(false);
		}
		try {
			File file = getFileForContext(handleIdentifier);
			if (file.exists()) {
				file.delete();
			}
		} catch (SecurityException e) {
			MylarStatusHandler.fail(e, "Could not delete context file", false);
		}
	}

	private void eraseContext(String id, boolean notify) {
		MylarContext context = currentContext.getContextMap().get(id);
		if (context == null)
			return;
		currentContext.getContextMap().remove(context);
		context.reset();
		if (notify) {
			for (IMylarContextListener listener : listeners)
				listener.presentationSettingsChanging(IMylarContextListener.UpdateKind.UPDATE);
		}
	}

	/**
	 * @return false if the map could not be read for any reason
	 */
	public MylarContext loadContext(String handleIdentifier) {
		MylarContext loadedContext = externalizer.readContextFromXML(handleIdentifier,
				getFileForContext(handleIdentifier));
		if (loadedContext == null) {
			return new MylarContext(handleIdentifier, MylarContextManager.getScalingFactors());
		} else {
			return loadedContext;
		}
	}

	public void saveContext(String handleIdentifier) {
		try {
			setContextCapturePaused(true);
			MylarContext context = currentContext.getContextMap().get(handleIdentifier);
			if (context == null)
				return;
			context.collapse();
			externalizer.writeContextToXML(context, getFileForContext(handleIdentifier));
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "could not save context", false);
		} finally {
			setContextCapturePaused(false);
		}
	}

	public void saveActivityHistoryContext() {
		try {
			setContextCapturePaused(true);
			externalizer.writeContextToXML(activityMetaContext, getFileForContext(CONTEXT_HISTORY_FILE_NAME));
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "could not save activity history", false);
		} finally {
			setContextCapturePaused(false);
		}
	}

	public File getFileForContext(String handleIdentifier) {
		String encoded;
		try {
			encoded = URLEncoder.encode(handleIdentifier, CONTEXT_FILENAME_ENCODING);
			String dataDirectory = MylarPlugin.getDefault().getDataDirectory();
			File contextFile = new File(dataDirectory + File.separator + encoded + CONTEXT_FILE_EXTENSION);
			return contextFile;
		} catch (UnsupportedEncodingException e) {
			MylarStatusHandler.fail(e, "Could not determine path for context", false);
		}
		return null;
	}

	public IMylarContext getActiveContext() {
		return currentContext;
	}

	/**
	 * @param kind
	 */
	public void resetLandmarkRelationshipsOfKind(String reltationKind) {
		for (IMylarElement landmark : currentContext.getLandmarks()) {
			for (IMylarRelation edge : landmark.getRelations()) {
				if (edge.getRelationshipHandle().equals(reltationKind)) {
					landmark.clearRelations();
				}
			}
		}
		for (IMylarContextListener listener : listeners)
			listener.edgesChanged(null);
	}

	/**
	 * Copy the listener list in case it is modified during the notificiation.
	 * 
	 * @param node
	 */
	public void notifyRelationshipsChanged(IMylarElement node) {
		if (suppressListenerNotification)
			return;
		for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
			listener.edgesChanged(node);
		}
	}

	public void refreshRelatedElements() {
		try {
			for (IMylarStructureBridge bridge : MylarPlugin.getDefault().getStructureBridges().values()) {
				if (bridge.getRelationshipProviders() != null) {
					for (AbstractRelationProvider provider : bridge.getRelationshipProviders()) {
						List<AbstractRelationProvider> providerList = new ArrayList<AbstractRelationProvider>();
						providerList.add(provider);
						updateDegreesOfSeparation(providerList, provider.getCurrentDegreeOfSeparation());
					}
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not refresn related elements", false);
		}
	}

	public List<AbstractRelationProvider> getActiveRelationProviders() {
		List<AbstractRelationProvider> providers = new ArrayList<AbstractRelationProvider>();
		Map<String, IMylarStructureBridge> bridges = MylarPlugin.getDefault().getStructureBridges();
		for (Entry<String, IMylarStructureBridge> entry : bridges.entrySet()) {
			IMylarStructureBridge bridge = entry.getValue();// bridges.get(extension);
			if (bridge.getRelationshipProviders() != null) {
				providers.addAll(bridge.getRelationshipProviders());
			}
		}
		return providers;
	}

	public void updateDegreeOfSeparation(AbstractRelationProvider provider, int degreeOfSeparation) {
		MylarPlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
		if (degreeOfSeparation <= 0) {
			// provider.setEnabled(false);
			provider.setDegreeOfSeparation(degreeOfSeparation);
		} else {
			// provider.setEnabled(true);
			provider.setDegreeOfSeparation(degreeOfSeparation);
			for (IMylarElement node : currentContext.getLandmarks())
				provider.landmarkAdded(node);
		}
	}

	public void updateDegreesOfSeparation(List<AbstractRelationProvider> providers, int degreeOfSeparation) {
		for (AbstractRelationProvider provider : providers) {
			updateDegreeOfSeparation(provider, degreeOfSeparation);
		}
	}

	public static ScalingFactors getScalingFactors() {
		return MylarContextManager.scalingFactors;
	}

	// Copying of mylar dir contents disabled for now (WC)
	// public void updateMylarDirContents(String prevDir) {
	// File prev = new File(prevDir);
	// if (!prev.isDirectory()) {
	// return;
	// }
	// File[] contents = prev.listFiles();
	// File curr = new File(MylarPlugin.getDefault().getMylarDataDirectory());
	// for (File f : contents) {
	// // XXX: remove hack below
	// if ( (f.getName().endsWith(".xml") && f.getName().startsWith("task")) ||
	// f.getName().startsWith("mylar")) {
	// String name = curr.getAbsolutePath() + "/" + f.getName();
	// f.renameTo(new File(name));
	// }
	// }
	// }

	public boolean isContextActive() {
		return currentContext.getContextMap().values().size() > 0;
	}

	public List<IMylarElement> getActiveLandmarks() {
		List<IMylarElement> allLandmarks = currentContext.getLandmarks();
		List<IMylarElement> acceptedLandmarks = new ArrayList<IMylarElement>();
		for (IMylarElement node : allLandmarks) {
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());

			if (bridge.canBeLandmark(node.getHandleIdentifier())) {
				acceptedLandmarks.add(node);
			}
		}
		return acceptedLandmarks;
	}

	/**
	 * Sorted in descending interest order.
	 */
	public List<IMylarElement> getInterestingDocuments(IMylarContext context) {
		Set<IMylarElement> set = new HashSet<IMylarElement>();
		List<IMylarElement> allIntersting = context.getInteresting();
		for (IMylarElement node : allIntersting) {
			if (MylarPlugin.getDefault().getStructureBridge(node.getContentType()).isDocument(
					node.getHandleIdentifier())) {
				set.add(node);
			}
		}
		List<IMylarElement> list = new ArrayList<IMylarElement>(set);
		Collections.sort(list, new InterestComparator<IMylarElement>());
		return list;
	}

	/**
	 * Get the interesting resources for the active context.
	 * 
	 * Sorted in descending interest order.
	 */
	public List<IMylarElement> getInterestingDocuments() {
		return getInterestingDocuments(currentContext);
	}

	public void actionObserved(IAction action, String info) {
		for (IActionExecutionListener listener : actionExecutionListeners) {
			listener.actionObserved(action);
		}
	}

	public List<IActionExecutionListener> getActionExecutionListeners() {
		return actionExecutionListeners;
	}

	public MylarContext getActivityHistoryMetaContext() {
		return activityMetaContext;
	}

	public void resetActivityHistory() {
		activityMetaContext = new MylarContext(CONTEXT_HISTORY_FILE_NAME, MylarContextManager.getScalingFactors());
		saveActivityHistoryContext();
	}

	public boolean isActivationHistorySuppressed() {
		return activationHistorySuppressed;
	}

	public void setActivationHistorySuppressed(boolean activationHistorySuppressed) {
		this.activationHistorySuppressed = activationHistorySuppressed;
	}

	/**
	 * @return	true if interest was manipulated successfully
	 */
	public boolean manipulateInterestForElement(IMylarElement element, boolean increment, boolean forceLandmark,
			String sourceId) {
		if (element == null) {
			return false;
		}
		float originalValue = element.getInterest().getValue();
		float changeValue = 0;
		IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element.getContentType());
		if (!increment) {
			if (element.getInterest().isLandmark() && bridge.canBeLandmark(element.getHandleIdentifier())) {
				// keep it interesting
				changeValue = (-1 * originalValue) + 1;
			} else {
				// make uninteresting
				if (originalValue >= 0)
					changeValue = (-1 * originalValue) - 1;

				// reduce interest of children
				for (String childHandle : bridge.getChildHandles(element.getHandleIdentifier())) {
					IMylarElement childElement = getElement(childHandle);
					if (childElement.getInterest().isInteresting() && !childElement.equals(element)) {
						manipulateInterestForElement(childElement, increment, forceLandmark, sourceId);
					}
				}
			}
		} else {
			if (!forceLandmark && (originalValue > MylarContextManager.getScalingFactors().getLandmark())) {
				changeValue = 0;
			} else { // make it a landmark by setting interest to 2 x landmark interest
				if (element != null && bridge.canBeLandmark(element.getHandleIdentifier())) {
//						&& !bridge.getContentType(element.getHandleIdentifier()).equals(MylarPlugin.CONTENT_TYPE_ANY)) {
					changeValue = (2*MylarContextManager.getScalingFactors().getLandmark()) - originalValue + 1;
				} else { 
					return false;
				}
			}
		}
		if (changeValue != 0) {
			InteractionEvent interactionEvent = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, element
					.getContentType(), element.getHandleIdentifier(), sourceId, changeValue);
			MylarPlugin.getContextManager().handleInteractionEvent(interactionEvent);
		}
		return true;
	}

	public void setActiveSearchEnabled(boolean enabled) {
		for (AbstractRelationProvider provider : getActiveRelationProviders()) {
			provider.setEnabled(enabled);
		}
	}

	/**
	 * Retruns the highest interet context.
	 * 
	 * TODO: refactor this into better multiple context support
	 */
	public String getDominantContextHandleForElement(IMylarElement node) {
		IMylarElement dominantNode = null;
		if (node instanceof CompositeContextElement) {
			CompositeContextElement compositeNode = (CompositeContextElement) node;
			if (compositeNode.getNodes().isEmpty())
				return null;
			dominantNode = (IMylarElement) compositeNode.getNodes().toArray()[0];

			for (IMylarElement concreteNode : compositeNode.getNodes()) {
				if (dominantNode != null
						&& dominantNode.getInterest().getValue() < concreteNode.getInterest().getValue()) {
					dominantNode = concreteNode;
				}
			}
		} else if (node instanceof MylarContextElement) {
			dominantNode = node;
		}
		if (node != null) {
			return ((MylarContextElement) dominantNode).getContext().getHandleIdentifier();
		} else {
			return null;
		}
	}

	public void updateHandle(IMylarElement element, String newHandle) {
		if (element == null)
			return;
		getActiveContext().updateElementHandle(element, newHandle);
		for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
			listener.interestChanged(element);
		}
		if (element.getInterest().isLandmark()) {
			for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
				listener.landmarkAdded(element);
			}
		}
	}

	public void delete(IMylarElement element) {
		if (element == null)
			return;
		getActiveContext().delete(element);
		for (IMylarContextListener listener : new ArrayList<IMylarContextListener>(listeners)) {
			listener.nodeDeleted(element);
		}
	}

	public boolean isContextCapturePaused() {
		return contextCapturePaused;
	}

	public void setContextCapturePaused(boolean paused) {
		this.contextCapturePaused = paused;
	}

	/**
	 * For testing.
	 */
	public List<IMylarContextListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	ShellLifecycleListener getShellLifecycleListener() {
		return shellLifecycleListener;
	}
}
