/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;

/**
 * This is the core class resposible for context management.
 * 
 * @author Mik Kersten
 * @author Jevgeni Holodkov
 */
public class InteractionContextManager {

	// TODO: move constants

	private static final String PROPERTY_CONTEXT_ACTIVE = "org.eclipse.mylyn.context.core.context.active";

	private static final String PREFERENCE_ATTENTION_MIGRATED = "mylyn.attention.migrated";

	public static final String CONTEXT_FILENAME_ENCODING = "UTF-8";

	public static final String ACTIVITY_DELTA_DEACTIVATED = "deactivated";

	public static final String ACTIVITY_DELTA_ACTIVATED = "activated";

	public static final String ACTIVITY_DELTA_ADDED = "added";

	public static final String ACTIVITY_DELTA_STARTED = "started";

	public static final String ACTIVITY_DELTA_STOPPED = "stopped";

	public static final String ACTIVITY_ORIGINID_WORKBENCH = "org.eclipse.ui.workbench";

	public static final String ACTIVITY_ORIGINID_OS = "os";

	public static final String ACTIVITY_STRUCTUREKIND_LIFECYCLE = "lifecycle";

	public static final String ACTIVITY_STRUCTUREKIND_TIMING = "timing";

	public static final String ACTIVITY_STRUCTUREKIND_ACTIVATION = "activation";

	public static final String CONTEXT_HISTORY_FILE_NAME = "activity";

	public static final String OLD_CONTEXT_HISTORY_FILE_NAME = "context-history";

	public static final String SOURCE_ID_MODEL_PROPAGATION = "org.eclipse.mylyn.core.model.interest.propagation";

	public static final String SOURCE_ID_DECAY = "org.eclipse.mylyn.core.model.interest.decay";

	public static final String SOURCE_ID_DECAY_CORRECTION = "org.eclipse.mylyn.core.model.interest.decay.correction";

	public static final String SOURCE_ID_MODEL_ERROR = "org.eclipse.mylyn.core.model.interest.propagation";

	public static final String CONTAINMENT_PROPAGATION_ID = "org.eclipse.mylyn.core.model.edges.containment";

	public static final String CONTEXT_FILE_EXTENSION = ".xml.zip";

	public static final String CONTEXT_FILE_EXTENSION_OLD = ".xml";

	private static final int MAX_PROPAGATION = 17; // TODO: parametrize this

	private int numInterestingErrors = 0;

	private List<String> errorElementHandles = new ArrayList<String>();

	private Set<File> contextFiles = null;

	private boolean contextCapturePaused = false;

	private CompositeInteractionContext activeContext = new CompositeInteractionContext(getCommonContextScaling());

	/**
	 * Global contexts do not participate in the regular activation lifecycle but are instead activated and deactivated
	 * by clients.
	 */
	private Map<String, InteractionContext> globalContexts = new HashMap<String, InteractionContext>();

	private InteractionContext activityMetaContext = null;

	private List<IInteractionContextListener> activityMetaContextListeners = new ArrayList<IInteractionContextListener>();

	private List<IInteractionContextListener> listeners = new CopyOnWriteArrayList<IInteractionContextListener>();

	private List<IInteractionContextListener> waitingListeners = new ArrayList<IInteractionContextListener>();

	private boolean suppressListenerNotification = false;

	private InteractionContextExternalizer externalizer = new InteractionContextExternalizer();

	private boolean activationHistorySuppressed = false;

	private static InteractionContextScaling commonContextScaling = new InteractionContextScaling();

	public InteractionContext getActivityMetaContext() {
		if (activityMetaContext == null) {
			loadActivityMetaContext();
		}
		return activityMetaContext;
	}

	public void loadActivityMetaContext() {
		if (ContextCorePlugin.getDefault().getContextStore() != null) {
			File contextActivityFile = getFileForContext(CONTEXT_HISTORY_FILE_NAME);
			activityMetaContext = externalizer.readContextFromXML(CONTEXT_HISTORY_FILE_NAME, contextActivityFile,
					commonContextScaling);
			if (activityMetaContext == null) {
				resetActivityHistory();
			} else if (!ContextCorePlugin.getDefault().getPluginPreferences().getBoolean(PREFERENCE_ATTENTION_MIGRATED)) {
				activityMetaContext = migrateLegacyActivity(activityMetaContext);
				saveActivityContext();
				ContextCorePlugin.getDefault().getPluginPreferences().setValue(PREFERENCE_ATTENTION_MIGRATED, true);
				ContextCorePlugin.getDefault().savePluginPreferences();
			}
			for (IInteractionContextListener listener : activityMetaContextListeners) {
				listener.contextActivated(activityMetaContext);
			}
		} else {
			resetActivityHistory();
			StatusHandler.log("No context store installed, not restoring activity context.", this);
		}
	}

	/**
	 * Used to migrate old activity to new activity events
	 * 
	 * @since 2.1
	 */
	private InteractionContext migrateLegacyActivity(InteractionContext context) {
		LegacyActivityAdaptor adaptor = new LegacyActivityAdaptor();
		InteractionContext newMetaContext = new InteractionContext(context.getHandleIdentifier(),
				InteractionContextManager.getCommonContextScaling());
		for (InteractionEvent event : context.getInteractionHistory()) {
			InteractionEvent temp = adaptor.parseInteractionEvent(event);
			if (temp != null) {
				newMetaContext.parseEvent(temp);
			}
		}
		return newMetaContext;
	}

	public void processActivityMetaContextEvent(InteractionEvent event) {
		IInteractionElement element = getActivityMetaContext().parseEvent(event);
		for (IInteractionContextListener listener : activityMetaContextListeners) {
			try {
				List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
				changed.add(element);
				listener.interestChanged(changed);
			} catch (Throwable t) {
				StatusHandler.fail(t, "context listener failed", false);
			}
		}
	}

	public void resetActivityHistory() {
		activityMetaContext = new InteractionContext(CONTEXT_HISTORY_FILE_NAME,
				InteractionContextManager.getCommonContextScaling());
		saveActivityContext();
	}

	public IInteractionElement getActiveElement() {
		if (activeContext != null) {
			return activeContext.getActiveNode();
		} else {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public void addErrorPredictedInterest(String handle, String kind, boolean notify) {
		if (numInterestingErrors > commonContextScaling.getMaxNumInterestingErrors()
				|| activeContext.getContextMap().isEmpty())
			return;
		InteractionEvent errorEvent = new InteractionEvent(InteractionEvent.Kind.PROPAGATION, kind, handle,
				SOURCE_ID_MODEL_ERROR, commonContextScaling.getErrorInterest());
		processInteractionEvent(errorEvent, true);
		errorElementHandles.add(handle);
		numInterestingErrors++;
	}

	/**
	 * TODO: worry about decay-related change if predicted interest dacays
	 */
	@SuppressWarnings("deprecation")
	public void removeErrorPredictedInterest(String handle, String kind, boolean notify) {
		if (activeContext.getContextMap().isEmpty())
			return;
		if (handle == null)
			return;
		IInteractionElement element = activeContext.get(handle);
		if (element != null && element.getInterest().isInteresting() && errorElementHandles.contains(handle)) {
			InteractionEvent errorEvent = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle,
					SOURCE_ID_MODEL_ERROR, -commonContextScaling.getErrorInterest());
			processInteractionEvent(errorEvent, true);
			numInterestingErrors--;
			errorElementHandles.remove(handle);
			// TODO: this results in double-notification
			if (notify)
				for (IInteractionContextListener listener : listeners) {
					List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
					changed.add(element);
					listener.interestChanged(changed);
				}
		}
	}

	public IInteractionElement getElement(String elementHandle) {
		if (activeContext != null && elementHandle != null) {
			return activeContext.get(elementHandle);
		} else {
			return null;
		}
	}

	/**
	 * TODO: consider using IInteractionElement instead, or making other methods consistent
	 */
	public IInteractionElement processInteractionEvent(Object object, Kind eventKind, String origin,
			IInteractionContext context) {
		AbstractContextStructureBridge structureBridge = ContextCorePlugin.getDefault().getStructureBridge(object);
		if (structureBridge != null) {
			String structureKind = structureBridge.getContentType();
			String handle = structureBridge.getHandleIdentifier(object);
			if (structureKind != null && handle != null) {
				InteractionEvent event = new InteractionEvent(eventKind, structureKind, handle, origin);
				List<IInteractionElement> interestDelta = internalProcessInteractionEvent(event, context, true);

				notifyInterestDelta(interestDelta);

				return context.get(event.getStructureHandle());
			}
		}
		return null;
	}

	public IInteractionElement processInteractionEvent(InteractionEvent event) {
		return processInteractionEvent(event, true);
	}

	public IInteractionElement processInteractionEvent(InteractionEvent event, boolean propagateToParents) {
		return processInteractionEvent(event, propagateToParents, true);
	}

	public void processInteractionEvents(List<InteractionEvent> events, boolean propagateToParents) {
		Set<IInteractionElement> compositeDelta = new HashSet<IInteractionElement>();
		for (InteractionEvent event : events) {
			if (isContextActive()) {
				compositeDelta.addAll(internalProcessInteractionEvent(event, activeContext, propagateToParents));
			}
			for (InteractionContext globalContext : globalContexts.values()) {
				if (globalContext.getContentLimitedTo().equals(event.getStructureKind())) {
					internalProcessInteractionEvent(event, globalContext, propagateToParents);
				}
			}
		}
		notifyInterestDelta(new ArrayList<IInteractionElement>(compositeDelta));
	}

	public IInteractionElement processInteractionEvent(InteractionEvent event, boolean propagateToParents,
			boolean notifyListeners) {
		boolean alreadyNotified = false;
		if (isContextActive()) {
			List<IInteractionElement> interestDelta = internalProcessInteractionEvent(event, activeContext,
					propagateToParents);
			if (notifyListeners) {
				notifyInterestDelta(interestDelta);
			}
		}
		for (InteractionContext globalContext : globalContexts.values()) {
			if (globalContext.getContentLimitedTo().equals(event.getStructureKind())) {
				List<IInteractionElement> interestDelta = internalProcessInteractionEvent(event, globalContext,
						propagateToParents);
				if (notifyListeners && !alreadyNotified) {
					notifyInterestDelta(interestDelta);
				}
			}
		}

		return activeContext.get(event.getStructureHandle());
	}

	private List<IInteractionElement> internalProcessInteractionEvent(InteractionEvent event,
			IInteractionContext interactionContext, boolean propagateToParents) {
		if (contextCapturePaused || InteractionEvent.Kind.COMMAND.equals(event.getKind())
				|| suppressListenerNotification) {
			return Collections.emptyList();
		}

		IInteractionElement previous = interactionContext.get(event.getStructureHandle());
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
			decayOffset = ensureIsInteresting(interactionContext, event.getStructureKind(), event.getStructureHandle(),
					previous, previousInterest);
		}
		IInteractionElement element = addInteractionEvent(interactionContext, event);
		List<IInteractionElement> interestDelta = new ArrayList<IInteractionElement>();
		if (propagateToParents && !event.getKind().equals(InteractionEvent.Kind.MANIPULATION)) {
			propegateInterestToParents(interactionContext, event.getKind(), element, previousInterest, decayOffset, 1,
					interestDelta);
		}
		if (event.getKind().isUserEvent() && interactionContext instanceof CompositeInteractionContext) {
			((CompositeInteractionContext) interactionContext).setActiveElement(element);
		}

		if (isInterestDelta(previousInterest, previouslyPredicted, previouslyPropagated, element)) {
			interestDelta.add(element);
		}

		checkForLandmarkDeltaAndNotify(previousInterest, element);
		return interestDelta;
	}

	private IInteractionElement addInteractionEvent(IInteractionContext interactionContext, InteractionEvent event) {
		if (interactionContext instanceof CompositeInteractionContext) {
			return ((CompositeInteractionContext) interactionContext).addEvent(event);
		} else if (interactionContext instanceof InteractionContext) {
			return ((InteractionContext) interactionContext).parseEvent(event);
		} else {
			return null;
		}
	}

	private float ensureIsInteresting(IInteractionContext interactionContext, String contentType, String handle,
			IInteractionElement previous, float previousInterest) {
		float decayOffset = 0;
		if (previousInterest < 0) { // reset interest if not interesting
			decayOffset = (-1) * (previous.getInterest().getValue());
			addInteractionEvent(interactionContext, new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
					contentType, handle, SOURCE_ID_DECAY_CORRECTION, decayOffset));
		}
		return decayOffset;
	}

	private void notifyInterestDelta(List<IInteractionElement> interestDelta) {
		if (!interestDelta.isEmpty()) {
			for (IInteractionContextListener listener : listeners) {
				listener.interestChanged(interestDelta);
			}
		}
	}

	protected boolean isInterestDelta(float previousInterest, boolean previouslyPredicted,
			boolean previouslyPropagated, IInteractionElement node) {
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

	protected void checkForLandmarkDeltaAndNotify(float previousInterest, IInteractionElement node) {
		// TODO: don't call interestChanged if it's a landmark?
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
				.getStructureBridge(node.getContentType());
		if (bridge.canBeLandmark(node.getHandleIdentifier())) {
			if (previousInterest >= commonContextScaling.getLandmark() && !node.getInterest().isLandmark()) {
				for (IInteractionContextListener listener : listeners)
					listener.landmarkRemoved(node);
			} else if (previousInterest < commonContextScaling.getLandmark() && node.getInterest().isLandmark()) {
				for (IInteractionContextListener listener : listeners)
					listener.landmarkAdded(node);
			}
		}
	}

	private void propegateInterestToParents(IInteractionContext interactionContext, InteractionEvent.Kind kind,
			IInteractionElement node, float previousInterest, float decayOffset, int level,
			List<IInteractionElement> interestDelta) {

		if (level > MAX_PROPAGATION || node == null || node.getHandleIdentifier() == null
				|| node.getInterest().getValue() <= 0) {
			return;
		}

		checkForLandmarkDeltaAndNotify(previousInterest, node);

		level++; // original is 1st level
		float propagatedIncrement = node.getInterest().getValue() - previousInterest + decayOffset;

		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
				.getStructureBridge(node.getContentType());
		String parentHandle = bridge.getParentHandle(node.getHandleIdentifier());

		// check if should use child bridge
		for (String contentType : ContextCorePlugin.getDefault().getChildContentTypes(bridge.getContentType())) {
			AbstractContextStructureBridge childBridge = ContextCorePlugin.getDefault().getStructureBridge(contentType);
			Object resolved = childBridge.getObjectForHandle(parentHandle);
			if (resolved != null) {
				AbstractContextStructureBridge canonicalBridge = ContextCorePlugin.getDefault().getStructureBridge(
						resolved);
				// HACK: hard-coded resource content type
				if (!canonicalBridge.getContentType().equals(ContextCorePlugin.CONTENT_TYPE_RESOURCE)) {
					// NOTE: resetting bridge
					bridge = canonicalBridge;
				}
			}
		}

		if (parentHandle != null) {
			InteractionEvent propagationEvent = new InteractionEvent(InteractionEvent.Kind.PROPAGATION,
					bridge.getContentType(node.getHandleIdentifier()), parentHandle, SOURCE_ID_MODEL_PROPAGATION,
					CONTAINMENT_PROPAGATION_ID, propagatedIncrement);
			IInteractionElement previous = interactionContext.get(propagationEvent.getStructureHandle());
			if (previous != null && previous.getInterest() != null) {
				previousInterest = previous.getInterest().getValue();
			}
			IInteractionElement parentNode = addInteractionEvent(interactionContext, propagationEvent);
			if (kind.isUserEvent()
					&& parentNode.getInterest().getEncodedValue() < commonContextScaling.getInteresting()) {
				float parentOffset = ((-1) * parentNode.getInterest().getEncodedValue()) + 1;
				addInteractionEvent(interactionContext, new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
						parentNode.getContentType(), parentNode.getHandleIdentifier(), SOURCE_ID_DECAY_CORRECTION,
						parentOffset));
			}
			if (previous != null
					&& isInterestDelta(previousInterest, previous.getInterest().isPredicted(), previous.getInterest()
							.isPropagated(), parentNode)) {
				interestDelta.add(0, parentNode);
			}
			propegateInterestToParents(interactionContext, kind, parentNode, previousInterest, decayOffset, level,
					interestDelta);// adapter.getResourceExtension(),
		}
	}

// public List<IInteractionElement>
// findCompositesForNodes(List<InteractionContextElement> nodes) {
// List<IInteractionElement> composites = new ArrayList<IInteractionElement>();
// for (InteractionContextElement node : nodes) {
// composites.add(aaactiveContext.get(node.getHandleIdentifier()));
// }
// return composites;
// }

	public void addListener(IInteractionContextListener listener) {
		if (listener != null) {
			if (suppressListenerNotification && !waitingListeners.contains(listener)) {
				waitingListeners.add(listener);
			} else {
				if (!listeners.contains(listener))
					listeners.add(listener);
			}
		} else {
			StatusHandler.log("attempted to add null lisetener", this);
		}
	}

	public void removeListener(IInteractionContextListener listener) {
		listeners.remove(listener);
	}

	public void addActivityMetaContextListener(IInteractionContextListener listener) {
		activityMetaContextListeners.add(listener);
	}

	public void removeActivityMetaContextListener(IInteractionContextListener listener) {
		activityMetaContextListeners.remove(listener);
	}

	public void removeAllListeners() {
		listeners.clear();
	}

	/**
	 * Public for testing, activate via handle
	 */
	public void internalActivateContext(InteractionContext context) {
		System.setProperty(PROPERTY_CONTEXT_ACTIVE, Boolean.TRUE.toString());

		activeContext.getContextMap().put(context.getHandleIdentifier(), context);
		if (contextFiles != null) {
			contextFiles.add(getFileForContext(context.getHandleIdentifier()));
		}
		if (!activationHistorySuppressed) {
			processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
					ACTIVITY_STRUCTUREKIND_ACTIVATION, context.getHandleIdentifier(), ACTIVITY_ORIGINID_WORKBENCH,
					null, ACTIVITY_DELTA_ACTIVATED, 1f));
		}
		
		for (IInteractionContextListener listener : listeners) {
			try {
				listener.contextActivated(context);
			} catch (Exception e) {
				StatusHandler.fail(e, "context listener failed", false);
			}
		}
	}

	public Collection<InteractionContext> getActiveContexts() {
		return Collections.unmodifiableCollection(activeContext.getContextMap().values());
	}

	public void activateContext(String handleIdentifier) {
		try {
			suppressListenerNotification = true;
			InteractionContext context = activeContext.getContextMap().get(handleIdentifier);
			if (context == null) {
				context = loadContext(handleIdentifier);
			}
			if (context != null) {
				internalActivateContext(context);
			} else {
				StatusHandler.log("Could not load context", this);
			}
			suppressListenerNotification = false;
			listeners.addAll(waitingListeners);
			waitingListeners.clear();
		} catch (Throwable t) {
			StatusHandler.log(t, "Could not activate context");
		}
	}

	/**
	 * Lazily loads set of handles with corresponding contexts.
	 */
	public boolean hasContext(String handleIdentifier) {
		if (handleIdentifier == null) {
			return false;
		}
		if (contextFiles == null) {
			contextFiles = new HashSet<File>();
			File contextDirectory = ContextCorePlugin.getDefault().getContextStore().getContextDirectory();
			File[] files = contextDirectory.listFiles();
			for (File file : files) {
				contextFiles.add(file);
			}
		}
		if (getActiveContext() != null && handleIdentifier.equals(getActiveContext().getHandleIdentifier())) {
			return !getActiveContext().getAllElements().isEmpty();
		} else {
			File file = getFileForContext(handleIdentifier);
			return contextFiles.contains(file);
		}
// File contextFile = getFileForContext(path);
// return contextFile.exists() && contextFile.length() > 0;
	}

	public void deactivateAllContexts() {
		Set<String> handles = new HashSet<String>(activeContext.getContextMap().keySet());
		for (String handleIdentifier : handles) {
			deactivateContext(handleIdentifier);
		}
	}

	public void deactivateContext(String handleIdentifier) {
		try {
			System.setProperty(PROPERTY_CONTEXT_ACTIVE, Boolean.FALSE.toString());

			IInteractionContext context = activeContext.getContextMap().get(handleIdentifier);
			if (context != null) {
				saveContext(handleIdentifier);
				activeContext.getContextMap().remove(handleIdentifier);

				setContextCapturePaused(true);
				for (IInteractionContextListener listener : listeners) {
					try {
						listener.contextDeactivated(context);
					} catch (Exception e) {
						StatusHandler.fail(e, "context listener failed", false);
					}
				}
				if (context.getAllElements().size() == 0) {
					contextFiles.remove(getFileForContext(context.getHandleIdentifier()));
				}
				setContextCapturePaused(false);
			}
			if (!activationHistorySuppressed) {
				processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
						ACTIVITY_STRUCTUREKIND_ACTIVATION, handleIdentifier, ACTIVITY_ORIGINID_WORKBENCH, null,
						ACTIVITY_DELTA_DEACTIVATED, 1f));
			}
			saveActivityContext();
		} catch (Throwable t) {
			StatusHandler.log(t, "Could not deactivate context");
		}
	}

	public void deleteContext(String handleIdentifier) {
		IInteractionContext context = activeContext.getContextMap().get(handleIdentifier);
		eraseContext(handleIdentifier, false);
		try {
			File file = getFileForContext(handleIdentifier);
			if (file.exists()) {
				file.delete();
			}
			setContextCapturePaused(true);
			for (IInteractionContextListener listener : listeners) {
				listener.contextCleared(context);
			}
			setContextCapturePaused(false);
			if (contextFiles != null) {
				contextFiles.remove(getFileForContext(handleIdentifier));
			}
		} catch (SecurityException e) {
			StatusHandler.fail(e, "Could not delete context file", false);
		}
	}

	private void eraseContext(String handleIdentifier, boolean notify) {
		if (contextFiles != null) {
			contextFiles.remove(getFileForContext(handleIdentifier));
		}
		InteractionContext context = activeContext.getContextMap().get(handleIdentifier);
		if (context == null)
			return;
		activeContext.getContextMap().remove(context);
		context.reset();
	}

	/**
	 * @return false if the map could not be read for any reason
	 */
	public InteractionContext loadContext(String handleIdentifier) {
		return loadContext(handleIdentifier, getFileForContext(handleIdentifier));
	}

	public InteractionContext loadContext(String handleIdentifier, InteractionContextScaling contextScaling) {
		return loadContext(handleIdentifier, getFileForContext(handleIdentifier), contextScaling);
	}

	public InteractionContext loadContext(String handleIdentifier, File file) {
		return loadContext(handleIdentifier, file, InteractionContextManager.getCommonContextScaling());
	}

	private InteractionContext loadContext(String handleIdentifier, File file, InteractionContextScaling contextScaling) {
		InteractionContext loadedContext = externalizer.readContextFromXML(handleIdentifier, file, contextScaling);
		if (loadedContext == null) {
			return new InteractionContext(handleIdentifier, contextScaling);
		} else {
			return loadedContext;
		}
	}

	public void saveContext(String handleIdentifier) {
		InteractionContext context = activeContext.getContextMap().get(handleIdentifier);
		if (context == null) {
			return;
		} else {
			saveContext(context);
		}
	}

	public void saveContext(InteractionContext context) {
		boolean wasPaused = contextCapturePaused;
		try {
			if (!wasPaused) {
				setContextCapturePaused(true);
			}

			context.collapse();
			externalizer.writeContextToXml(context, getFileForContext(context.getHandleIdentifier()));
			if (contextFiles == null) {
				contextFiles = new HashSet<File>();
			}
			contextFiles.add(getFileForContext(context.getHandleIdentifier()));
		} catch (Throwable t) {
			StatusHandler.fail(t, "could not save context", false);
		} finally {
			if (!wasPaused) {
				setContextCapturePaused(false);
			}
		}
	}

	/**
	 * Creates a file for specified context and activates it
	 */
	public void importContext(InteractionContext context) {
		externalizer.writeContextToXml(context, getFileForContext(context.getHandleIdentifier()));
		if (contextFiles == null) {
			contextFiles = new HashSet<File>();
		}
		contextFiles.add(getFileForContext(context.getHandleIdentifier()));
		activeContext.getContextMap().put(context.getHandleIdentifier(), context);

		if (!activationHistorySuppressed) {
			processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
					ACTIVITY_STRUCTUREKIND_ACTIVATION, context.getHandleIdentifier(), ACTIVITY_ORIGINID_WORKBENCH,
					null, ACTIVITY_DELTA_ACTIVATED, 1f));
		}
	}

	public void saveActivityContext() {
		if (ContextCorePlugin.getDefault().getContextStore() == null) {
			return;
		}
		boolean wasPaused = contextCapturePaused;
		try {
			if (!wasPaused) {
				setContextCapturePaused(true);
			}

			InteractionContext context = getActivityMetaContext();
			externalizer.writeContextToXml(collapseActivityMetaContext(context),
					getFileForContext(CONTEXT_HISTORY_FILE_NAME));
		} catch (Throwable t) {
			StatusHandler.fail(t, "could not save activity history", false);
		} finally {
			if (!wasPaused) {
				setContextCapturePaused(false);
			}
		}
	}

	public InteractionContext collapseActivityMetaContext(InteractionContext context) {
		Map<String, List<InteractionEvent>> attention = new HashMap<String, List<InteractionEvent>>();
		InteractionContext tempContext = new InteractionContext(CONTEXT_HISTORY_FILE_NAME,
				InteractionContextManager.getCommonContextScaling());
		for (InteractionEvent event : context.getInteractionHistory()) {

			if (event.getKind().equals(InteractionEvent.Kind.ATTENTION)
					&& event.getDelta().equals(ACTIVITY_DELTA_ADDED)) {
				if (event.getStructureHandle() == null || event.getStructureHandle().equals("")) {
					continue;
				}
				List<InteractionEvent> interactionEvents = attention.get(event.getStructureHandle());
				if (interactionEvents == null) {
					interactionEvents = new ArrayList<InteractionEvent>();
					attention.put(event.getStructureHandle(), interactionEvents);
				}
				interactionEvents.add(event);
			} else {
				if (!attention.isEmpty()) {
					addAttentionEvents(attention, tempContext);
					attention.clear();
				}
				tempContext.parseEvent(event);
			}
		}

		if (!attention.isEmpty()) {
			addAttentionEvents(attention, tempContext);
		}

		return tempContext;
	}

	/**
	 * Collapse activity events of like handle into one event Grouped by hour.
	 */
	private void addAttentionEvents(Map<String, List<InteractionEvent>> attention, InteractionContext temp) {
		try {
			for (String handle : attention.keySet()) {
				List<InteractionEvent> activityEvents = attention.get(handle);
				List<InteractionEvent> collapsedEvents = new ArrayList<InteractionEvent>();
				if (activityEvents.size() > 1) {
					collapsedEvents = collapseEventsByHour(activityEvents);
				} else if (activityEvents.size() == 1) {
					if (activityEvents.get(0).getEndDate().getTime() - activityEvents.get(0).getDate().getTime() > 0) {
						collapsedEvents.add(activityEvents.get(0));
					}
				}
				if (!collapsedEvents.isEmpty()) {
					for (InteractionEvent collapsedEvent : collapsedEvents) {
						temp.parseEvent(collapsedEvent);
					}
				}
				activityEvents.clear();
			}
		} catch (Exception e) {
			StatusHandler.fail(e, "Error during meta activity collapse", false);
		}
	}

	/** public for testing * */
	// TODO: simplify
	public List<InteractionEvent> collapseEventsByHour(List<InteractionEvent> eventsToCollapse) {
		List<InteractionEvent> collapsedEvents = new ArrayList<InteractionEvent>();
		Iterator<InteractionEvent> itr = eventsToCollapse.iterator();
		InteractionEvent firstEvent = itr.next();
		long total = 0;
		Calendar t0 = Calendar.getInstance();
		Calendar t1 = Calendar.getInstance();
		while (itr.hasNext()) {

			t0.setTime(firstEvent.getDate());
			t0.set(Calendar.MINUTE, 0);
			t0.set(Calendar.MILLISECOND, 0);

			t1.setTime(firstEvent.getDate());
			t1.set(Calendar.MINUTE, t1.getMaximum(Calendar.MINUTE));
			t1.set(Calendar.MILLISECOND, t1.getMaximum(Calendar.MILLISECOND));

			InteractionEvent nextEvent = itr.next();
			if (t0.getTime().compareTo(nextEvent.getDate()) <= 0 && t1.getTime().compareTo(nextEvent.getDate()) >= 0) {
				// Collapsible event
				if (total == 0) {
					total += firstEvent.getEndDate().getTime() - firstEvent.getDate().getTime();
				}
				total += nextEvent.getEndDate().getTime() - nextEvent.getDate().getTime();

				if (!itr.hasNext()) {
					if (total != 0) {
						Date newEndDate = new Date(firstEvent.getDate().getTime() + total);
						InteractionEvent aggregateEvent = new InteractionEvent(firstEvent.getKind(),
								firstEvent.getStructureKind(), firstEvent.getStructureHandle(),
								firstEvent.getOriginId(), firstEvent.getNavigation(), firstEvent.getDelta(), 1f,
								firstEvent.getDate(), newEndDate);
						collapsedEvents.add(aggregateEvent);
						total = 0;
					}
				}

			} else {
				// Next event isn't collapsible, add collapsed if exists
				if (total != 0) {
					Date newEndDate = new Date(firstEvent.getDate().getTime() + total);
					InteractionEvent aggregateEvent = new InteractionEvent(firstEvent.getKind(),
							firstEvent.getStructureKind(), firstEvent.getStructureHandle(), firstEvent.getOriginId(),
							firstEvent.getNavigation(), firstEvent.getDelta(), 1f, firstEvent.getDate(), newEndDate);
					collapsedEvents.add(aggregateEvent);
					total = 0;
				} else {
					collapsedEvents.add(firstEvent);
					if (!itr.hasNext()) {
						collapsedEvents.add(nextEvent);
					}
				}

				firstEvent = nextEvent;
			}

		}

		return collapsedEvents;
	}

	public File getFileForContext(String handleIdentifier) {
		String encoded;
		try {
			encoded = URLEncoder.encode(handleIdentifier, CONTEXT_FILENAME_ENCODING);
			File contextDirectory = ContextCorePlugin.getDefault().getContextStore().getContextDirectory();
			File contextFile = new File(contextDirectory, encoded + CONTEXT_FILE_EXTENSION);
			return contextFile;
		} catch (UnsupportedEncodingException e) {
			StatusHandler.fail(e, "Could not determine path for context", false);
		}
		return null;
	}

	public IInteractionContext getActiveContext() {
		return activeContext;
	}

	public void resetLandmarkRelationshipsOfKind(String reltationKind) {
		for (IInteractionElement landmark : activeContext.getLandmarks()) {
			for (IInteractionRelation edge : landmark.getRelations()) {
				if (edge.getRelationshipHandle().equals(reltationKind)) {
					landmark.clearRelations();
				}
			}
		}
		for (IInteractionContextListener listener : listeners)
			listener.relationsChanged(null);
	}

	/**
	 * Copy the listener list in case it is modified during the notificiation.
	 * 
	 * @param node
	 */
	public void notifyRelationshipsChanged(IInteractionElement node) {
		if (suppressListenerNotification)
			return;
		for (IInteractionContextListener listener : listeners) {
			listener.relationsChanged(node);
		}
	}

	public static InteractionContextScaling getCommonContextScaling() {
		return commonContextScaling;
	}

	public boolean isContextActive() {
		return !contextCapturePaused && activeContext.getContextMap().values().size() > 0;
	}

	public List<IInteractionElement> getActiveLandmarks() {
		List<IInteractionElement> allLandmarks = activeContext.getLandmarks();
		List<IInteractionElement> acceptedLandmarks = new ArrayList<IInteractionElement>();
		for (IInteractionElement node : allLandmarks) {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
					node.getContentType());

			if (bridge.canBeLandmark(node.getHandleIdentifier())) {
				acceptedLandmarks.add(node);
			}
		}
		return acceptedLandmarks;
	}

	public Collection<IInteractionElement> getInterestingDocuments(IInteractionContext context) {
		Set<IInteractionElement> set = new HashSet<IInteractionElement>();
		if (context == null) {
			return set;
		} else {
			List<IInteractionElement> allIntersting = context.getInteresting();
			for (IInteractionElement node : allIntersting) {
				if (ContextCorePlugin.getDefault().getStructureBridge(node.getContentType()).isDocument(
						node.getHandleIdentifier())) {
					set.add(node);
				}
			}
			return set;
		}
	}

	public Collection<IInteractionElement> getInterestingDocuments() {
		return getInterestingDocuments(activeContext);
	}

	public boolean isActivationHistorySuppressed() {
		return activationHistorySuppressed;
	}

	public void setActivationHistorySuppressed(boolean activationHistorySuppressed) {
		this.activationHistorySuppressed = activationHistorySuppressed;
	}

	/**
	 * Manipulates interest for the active context.
	 */
	public boolean manipulateInterestForElement(IInteractionElement element, boolean increment, boolean forceLandmark,
			boolean preserveUninteresting, String sourceId) {
		if (!isContextActive()) {
			return false;
		} else {
			return manipulateInterestForElement(element, increment, forceLandmark, preserveUninteresting, sourceId,
					activeContext);
		}
	}

	/**
	 * @return true if interest was manipulated successfully
	 */
	public boolean manipulateInterestForElement(IInteractionElement element, boolean increment, boolean forceLandmark,
			boolean preserveUninteresting, String sourceId, IInteractionContext context) {
		if (element == null || context == null) {
			return false;
		}
		float originalValue = element.getInterest().getValue();
		float changeValue = 0;
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
				element.getContentType());
		if (!increment) {
			if (element.getInterest().isLandmark() && bridge.canBeLandmark(element.getHandleIdentifier())) {
				// keep it interesting
				changeValue = (-1 * originalValue) + 1;
			} else {
				// make uninteresting
				if (originalValue >= 0) {
					changeValue = (-1 * originalValue) - 1;
				}

				// reduce interest of children
				for (String childHandle : bridge.getChildHandles(element.getHandleIdentifier())) {
					IInteractionElement childElement = getElement(childHandle);
					if (childElement != null && childElement.getInterest().isInteresting()
							&& !childElement.equals(element)) {
						manipulateInterestForElement(childElement, increment, forceLandmark, preserveUninteresting,
								sourceId, context);
					}
				}
			}
		} else {
			if (!forceLandmark && (originalValue > context.getScaling().getLandmark())) {
				changeValue = 0;
			} else {
				if (bridge.canBeLandmark(element.getHandleIdentifier())) {
					changeValue = (context.getScaling().getForcedLandmark()) - originalValue + 1;
				} else {
					return false;
				}
			}
		}
		if (changeValue > context.getScaling().getInteresting() || preserveUninteresting) {
			InteractionEvent interactionEvent = new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
					element.getContentType(), element.getHandleIdentifier(), sourceId, changeValue);
			List<IInteractionElement> interestDelta = internalProcessInteractionEvent(interactionEvent, context, true);
			notifyInterestDelta(interestDelta);
		} else if (changeValue < context.getScaling().getInteresting()) {
			delete(element, context);
			// TODO: batch this into a delta
			for (IInteractionContextListener listener : listeners) {
				listener.elementDeleted(element);
			}
		}
		return true;
	}

	public void setActiveSearchEnabled(boolean enabled) {
		for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
			provider.setEnabled(enabled);
		}
	}

	/**
	 * Retruns the highest interet context.
	 * 
	 * TODO: refactor this into better multiple context support
	 */
	public String getDominantContextHandleForElement(IInteractionElement node) {
		IInteractionElement dominantNode = null;
		if (node instanceof CompositeContextElement) {
			CompositeContextElement compositeNode = (CompositeContextElement) node;
			if (compositeNode.getNodes().isEmpty())
				return null;
			dominantNode = (IInteractionElement) compositeNode.getNodes().toArray()[0];

			for (IInteractionElement concreteNode : compositeNode.getNodes()) {
				if (dominantNode != null
						&& dominantNode.getInterest().getValue() < concreteNode.getInterest().getValue()) {
					dominantNode = concreteNode;
				}
			}
		} else if (node instanceof InteractionContextElement) {
			dominantNode = node;
		}
		if (dominantNode != null) {
			return ((InteractionContextElement) dominantNode).getContext().getHandleIdentifier();
		} else {
			return null;
		}
	}

	public void updateHandle(IInteractionElement element, String newHandle) {
		if (element == null)
			return;
		getActiveContext().updateElementHandle(element, newHandle);
		for (IInteractionContextListener listener : listeners) {
			List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
			changed.add(element);
			listener.interestChanged(changed);
		}
		if (element.getInterest().isLandmark()) {
			for (IInteractionContextListener listener : listeners) {
				listener.landmarkAdded(element);
			}
		}
	}

	public void delete(IInteractionElement element) {
		delete(element, getActiveContext());
	}

	private void delete(IInteractionElement element, IInteractionContext context) {
		if (element == null || context == null) {
			return;
		}
		context.delete(element);
		for (IInteractionContextListener listener : listeners) {
			listener.elementDeleted(element);
		}
	}

	/**
	 * NOTE: If pausing ensure to restore to original state.
	 */
	public void setContextCapturePaused(boolean paused) {
		this.contextCapturePaused = paused;
	}

	public boolean isContextCapturePaused() {
		return contextCapturePaused;
	}

	/**
	 * For testing.
	 */
	public List<IInteractionContextListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	public boolean isValidContextFile(File file) {
		if (file.exists() && file.getName().endsWith(InteractionContextManager.CONTEXT_FILE_EXTENSION)) {
			InteractionContext context = externalizer.readContextFromXML("temp", file, commonContextScaling);
			return context != null;
		}
		return false;
	}

	public void copyContext(String targetcontextHandle, File sourceContextFile) {
		File targetContextFile = getFileForContext(targetcontextHandle);
		targetContextFile.delete();
		try {
			copy(sourceContextFile, targetContextFile);
			contextFiles.add(targetContextFile);
		} catch (IOException e) {
			StatusHandler.fail(e, "Cold not transfer context: " + targetcontextHandle, false);
		}
	}

	/**
	 * clones context from source to destination
	 * 
	 * @since 2.1
	 */
	public void cloneContext(String sourceContextHandle, String destinationContextHandle) {
		InteractionContext source = loadContext(sourceContextHandle);
		if (source != null) {
			source.setHandleIdentifier(destinationContextHandle);
			saveContext(source);
		}
	}

	private void copy(File src, File dest) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public void addGlobalContext(InteractionContext context) {
		globalContexts.put(context.getHandleIdentifier(), context);
	}

	public void removeGlobalContext(InteractionContext context) {
		globalContexts.remove(context.getHandleIdentifier());
	}

	public Collection<InteractionContext> getGlobalContexts() {
		return globalContexts.values();
	}
}
