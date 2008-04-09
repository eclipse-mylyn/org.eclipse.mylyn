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
import java.util.Arrays;
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionContextListener2;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
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
 * @author Shawn Minto
 */
public class InteractionContextManager implements IInteractionContextManager {

	// TODO: move constants
	private static final int MAX_PROPAGATION = 17; // TODO: parametrize this

	private static final String PREFERENCE_ATTENTION_MIGRATED = "mylyn.attention.migrated";

	private static final String SOURCE_ID_DECAY_CORRECTION = "org.eclipse.mylyn.core.model.interest.decay.correction";

	private static final String SOURCE_ID_MODEL_ERROR = "org.eclipse.mylyn.core.model.interest.propagation";

	private static final String SOURCE_ID_MODEL_PROPAGATION = "org.eclipse.mylyn.core.model.interest.propagation";

	private boolean activationHistorySuppressed = false;

	private final CompositeInteractionContext activeContext = new CompositeInteractionContext(
			ContextCore.getCommonContextScaling());

	private InteractionContext activityMetaContext = null;

	private final List<IInteractionContextListener> activityMetaContextListeners = new CopyOnWriteArrayList<IInteractionContextListener>();

	private boolean contextCapturePaused = false;

	private Set<File> contextFiles = null;

	private final List<IInteractionContextListener> contextListeners = new CopyOnWriteArrayList<IInteractionContextListener>();

	private final List<String> errorElementHandles = new ArrayList<String>();

	private final InteractionContextExternalizer externalizer = new InteractionContextExternalizer();

	/**
	 * Global contexts do not participate in the regular activation lifecycle but are instead activated and deactivated
	 * by clients.
	 */
	private final Collection<IInteractionContext> globalContexts = new HashSet<IInteractionContext>();

	private int numInterestingErrors = 0;

	private boolean suppressListenerNotification = false;

	private final List<IInteractionContextListener> waitingContextListeners = new ArrayList<IInteractionContextListener>();

	public void activateContext(String handleIdentifier) {
		try {
			InteractionContext context = activeContext.getContextMap().get(handleIdentifier);
			if (context == null) {
				context = loadContext(handleIdentifier);
			}
			for (IInteractionContextListener listener : contextListeners) {
				if (listener instanceof IInteractionContextListener2) {
					((IInteractionContextListener2) listener).contextPreActivated(context);
				}
			}
			if (context != null) {
				suppressListenerNotification = true;
				internalActivateContext(context);
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID, "Could not load context"));
			}
			suppressListenerNotification = false;
			contextListeners.addAll(waitingContextListeners);
			waitingContextListeners.clear();
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Could not activate context", t));
		}
	}

	public void addActivityMetaContextListener(IInteractionContextListener listener) {
		activityMetaContextListeners.add(listener);
	}

	/**
	 * Collapse activity events of like handle into one event Grouped by hour.
	 */
	public void addAttentionEvents(Map<String, List<InteractionEvent>> attention, InteractionContext temp) {
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
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID,
					"Error during meta activity collapse", e));
		}
	}

	@SuppressWarnings("deprecation")
	public void addErrorPredictedInterest(String handle, String kind, boolean notify) {
		if (numInterestingErrors > ContextCore.getCommonContextScaling().getMaxNumInterestingErrors()
				|| activeContext.getContextMap().isEmpty()) {
			return;
		}
		InteractionEvent errorEvent = new InteractionEvent(InteractionEvent.Kind.PROPAGATION, kind, handle,
				SOURCE_ID_MODEL_ERROR, ContextCore.getCommonContextScaling().getErrorInterest());
		processInteractionEvent(errorEvent, true);
		errorElementHandles.add(handle);
		numInterestingErrors++;
	}

	public void addGlobalContext(IInteractionContext context) {
		globalContexts.add(context);
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

	public void addListener(IInteractionContextListener listener) {
		if (listener != null) {
			if (suppressListenerNotification && !waitingContextListeners.contains(listener)) {
				waitingContextListeners.add(listener);
			} else {
				if (!contextListeners.contains(listener)) {
					contextListeners.add(listener);
				}
			}
		} else {
			// API 3.0 FIXME replace by Assert.isNotNull(listener)
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Attempted to add null lisetener",
					new Exception()));
		}
	}

	protected void checkForLandmarkDeltaAndNotify(float previousInterest, IInteractionElement node) {
		// TODO: don't call interestChanged if it's a landmark?
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
				.getStructureBridge(node.getContentType());
		if (bridge.canBeLandmark(node.getHandleIdentifier())) {
			if (previousInterest >= ContextCore.getCommonContextScaling().getLandmark()
					&& !node.getInterest().isLandmark()) {
				for (IInteractionContextListener listener : contextListeners) {
					listener.landmarkRemoved(node);
				}
			} else if (previousInterest < ContextCore.getCommonContextScaling().getLandmark()
					&& node.getInterest().isLandmark()) {
				for (IInteractionContextListener listener : contextListeners) {
					listener.landmarkAdded(node);
				}
			}
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

	public InteractionContext collapseActivityMetaContext(InteractionContext context) {
		Map<String, List<InteractionEvent>> attention = new HashMap<String, List<InteractionEvent>>();
		InteractionContext tempContext = new InteractionContext(IInteractionContextManager.CONTEXT_HISTORY_FILE_NAME,
				ContextCore.getCommonContextScaling());
		for (InteractionEvent event : context.getInteractionHistory()) {

			if (event.getKind().equals(InteractionEvent.Kind.ATTENTION)
					&& event.getDelta().equals(IInteractionContextManager.ACTIVITY_DELTA_ADDED)) {
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

	public void copyContext(String targetcontextHandle, File sourceContextFile) {
		File targetContextFile = getFileForContext(targetcontextHandle);
		targetContextFile.delete();
		try {
			copy(sourceContextFile, targetContextFile);
			contextFiles.add(targetContextFile);
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Cold not transfer context: "
					+ targetcontextHandle, e));
		}
	}

	public void deactivateAllContexts() {
		Set<String> handles = new HashSet<String>(activeContext.getContextMap().keySet());
		for (String handleIdentifier : handles) {
			deactivateContext(handleIdentifier);
		}
	}

	public void deactivateContext(String handleIdentifier) {
		try {
			System.setProperty(IInteractionContextManager.PROPERTY_CONTEXT_ACTIVE, Boolean.FALSE.toString());

			IInteractionContext context = activeContext.getContextMap().get(handleIdentifier);
			if (context != null) {
				saveContext(handleIdentifier);
				activeContext.getContextMap().remove(handleIdentifier);

				setContextCapturePaused(true);
				for (IInteractionContextListener listener : contextListeners) {
					try {
						listener.contextDeactivated(context);
					} catch (Exception e) {
						StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID,
								"Context listener failed: " + listener.getClass().getCanonicalName(), e));
					}
				}
				if (context.getAllElements().size() == 0) {
					contextFiles.remove(getFileForContext(context.getHandleIdentifier()));
				}
				setContextCapturePaused(false);
			}
			if (!activationHistorySuppressed) {
				processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
						IInteractionContextManager.ACTIVITY_STRUCTUREKIND_ACTIVATION, handleIdentifier,
						IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
						IInteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 1f));
			}
			saveActivityContext();
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Could not deactivate context", t));
		}
	}

	public void delete(IInteractionElement element) {
		delete(element, getActiveContext());
		notifyElementsDeleted(Arrays.asList(new IInteractionElement[] { element }));
	}

	private void delete(IInteractionElement element, IInteractionContext context) {
		if (element == null || context == null) {
			return;
		}
		context.delete(element);
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
			for (IInteractionContextListener listener : contextListeners) {
				listener.contextCleared(context);
			}
			setContextCapturePaused(false);
			if (contextFiles != null) {
				contextFiles.remove(getFileForContext(handleIdentifier));
			}
		} catch (SecurityException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Could not delete context file", e));
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

	private void eraseContext(String handleIdentifier, boolean notify) {
		if (contextFiles != null) {
			contextFiles.remove(getFileForContext(handleIdentifier));
		}
		InteractionContext context = activeContext.getContextMap().get(handleIdentifier);
		if (context == null) {
			return;
		}
		activeContext.getContextMap().remove(context);
		context.reset();
	}

	public IInteractionContext getActiveContext() {
		return activeContext;
	}

	public Collection<InteractionContext> getActiveContexts() {
		return Collections.unmodifiableCollection(activeContext.getContextMap().values());
	}

	public IInteractionElement getActiveElement() {
		if (activeContext != null) {
			return activeContext.getActiveNode();
		} else {
			return null;
		}
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

	public InteractionContext getActivityMetaContext() {
		if (activityMetaContext == null) {
			loadActivityMetaContext();
		}
		return activityMetaContext;
	}

	/**
	 * Returns the highest interest context.
	 * 
	 * TODO: refactor this into better multiple context support
	 */
	@Deprecated
	public String getDominantContextHandleForElement(IInteractionElement node) {
		IInteractionElement dominantNode = null;
		if (node instanceof CompositeContextElement) {
			CompositeContextElement compositeNode = (CompositeContextElement) node;
			if (compositeNode.getNodes().isEmpty()) {
				return null;
			}
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

	/**
	 * @return null if the element handle is null or if the element is not found in the active task context.
	 */
	public IInteractionElement getElement(String elementHandle) {
		if (activeContext != null && elementHandle != null) {
			return activeContext.get(elementHandle);
		} else {
			return null;
		}
	}

	public File getFileForContext(String handleIdentifier) {
		String encoded;
		try {
			encoded = URLEncoder.encode(handleIdentifier, IInteractionContextManager.CONTEXT_FILENAME_ENCODING);
			File contextDirectory = ContextCorePlugin.getDefault().getContextStore().getContextDirectory();
			File contextFile = new File(contextDirectory, encoded + IInteractionContextManager.CONTEXT_FILE_EXTENSION);
			return contextFile;
		} catch (UnsupportedEncodingException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID,
					"Could not determine path for context", e));
		}
		return null;
	}

	public Collection<IInteractionContext> getGlobalContexts() {
		return globalContexts;
	}

	public Collection<IInteractionElement> getInterestingDocuments() {
		return getInterestingDocuments(activeContext);
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

	/**
	 * For testing.
	 */
	public List<IInteractionContextListener> getListeners() {
		return Collections.unmodifiableList(contextListeners);
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
					IInteractionContextManager.ACTIVITY_STRUCTUREKIND_ACTIVATION, context.getHandleIdentifier(),
					IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
					IInteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f));
		}
	}

	/**
	 * Public for testing, activate via handle
	 */
	public void internalActivateContext(InteractionContext context) {
		System.setProperty(IInteractionContextManager.PROPERTY_CONTEXT_ACTIVE, Boolean.TRUE.toString());

		activeContext.getContextMap().put(context.getHandleIdentifier(), context);
		if (contextFiles != null) {
			contextFiles.add(getFileForContext(context.getHandleIdentifier()));
		}
		if (!activationHistorySuppressed) {
			processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
					IInteractionContextManager.ACTIVITY_STRUCTUREKIND_ACTIVATION, context.getHandleIdentifier(),
					IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
					IInteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f));
		}

		for (IInteractionContextListener listener : contextListeners) {
			try {
				listener.contextActivated(context);
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Context listener failed: "
						+ listener.getClass().getCanonicalName(), e));
			}
		}
	}

	public List<IInteractionElement> internalProcessInteractionEvent(InteractionEvent event,
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

	public boolean isActivationHistorySuppressed() {
		return activationHistorySuppressed;
	}

	// API-3.0: consider removing check for pause and making clients explicitly determine this, 
	// or provide a separate method
	public boolean isContextActive() {
		return !contextCapturePaused && activeContext.getContextMap().values().size() > 0;
	}

	@Deprecated
	public boolean isContextActivePropertySet() {
		return Boolean.parseBoolean(System.getProperty(IInteractionContextManager.PROPERTY_CONTEXT_ACTIVE));
	}

	public boolean isContextCapturePaused() {
		return contextCapturePaused;
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

	public boolean isValidContextFile(File file) {
		if (file.exists() && file.getName().endsWith(IInteractionContextManager.CONTEXT_FILE_EXTENSION)) {
			InteractionContext context = externalizer.readContextFromXML("temp", file,
					ContextCore.getCommonContextScaling());
			return context != null;
		}
		return false;
	}

	public void loadActivityMetaContext() {
		if (ContextCorePlugin.getDefault().getContextStore() != null) {
			for (IInteractionContextListener listener : activityMetaContextListeners) {
				if (listener instanceof IInteractionContextListener2) {
					((IInteractionContextListener2) listener).contextPreActivated(activityMetaContext);
				}
			}

			File contextActivityFile = getFileForContext(IInteractionContextManager.CONTEXT_HISTORY_FILE_NAME);
			activityMetaContext = externalizer.readContextFromXML(IInteractionContextManager.CONTEXT_HISTORY_FILE_NAME,
					contextActivityFile, ContextCore.getCommonContextScaling());
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
			StatusHandler.log(new Status(IStatus.INFO, ContextCorePlugin.PLUGIN_ID,
					"No context store installed, not restoring activity context."));
		}
	}

	/**
	 * @return false if the map could not be read for any reason
	 */
	public InteractionContext loadContext(String handleIdentifier) {
		return loadContext(handleIdentifier, getFileForContext(handleIdentifier));
	}

	public InteractionContext loadContext(String handleIdentifier, File file) {
		return loadContext(handleIdentifier, file, ContextCore.getCommonContextScaling());
	}

	private InteractionContext loadContext(String handleIdentifier, File file, IInteractionContextScaling contextScaling) {
		InteractionContext loadedContext = externalizer.readContextFromXML(handleIdentifier, file, contextScaling);
		if (loadedContext == null) {
			return new InteractionContext(handleIdentifier, contextScaling);
		} else {
			return loadedContext;
		}
	}

	@Deprecated
	public InteractionContext loadContext(String handleIdentifier, InteractionContextScaling contextScaling) {
		return loadContext(handleIdentifier, getFileForContext(handleIdentifier), contextScaling);
	}

	/**
	 * Manipulates interest for the active context.
	 * 
	 * API-3.0: revise or remove this and it's helper
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
		Set<IInteractionElement> changedElements = new HashSet<IInteractionElement>();
		boolean manipulated = manipulateInterestForElementHelper(element, increment, forceLandmark,
				preserveUninteresting, sourceId, context, changedElements);
		if (manipulated) {
			if (preserveUninteresting || increment) {
				notifyInterestDelta(new ArrayList<IInteractionElement>(changedElements));
			} else {
				notifyElementsDeleted(new ArrayList<IInteractionElement>(changedElements));
			}
		}
		return manipulated;
	}

	private boolean manipulateInterestForElementHelper(IInteractionElement element, boolean increment,
			boolean forceLandmark, boolean preserveUninteresting, String sourceId, IInteractionContext context,
			Set<IInteractionElement> changedElements) {
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
					changeValue = ((-1) * originalValue) - 1;
				}

				// reduce interest of children
				for (String childHandle : bridge.getChildHandles(element.getHandleIdentifier())) {
					IInteractionElement childElement = context.get(childHandle);
					if (childElement != null /*&& childElement.getInterest().isInteresting()*/
							&& !childElement.equals(element)) {
						manipulateInterestForElementHelper(childElement, increment, forceLandmark,
								preserveUninteresting, sourceId, context, changedElements);
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
//		if (changeValue > context.getScaling().getInteresting() || preserveUninteresting) {
		if (increment || preserveUninteresting) {
			InteractionEvent interactionEvent = new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
					element.getContentType(), element.getHandleIdentifier(), sourceId, changeValue);
			List<IInteractionElement> interestDelta = internalProcessInteractionEvent(interactionEvent, context, true);
			changedElements.addAll(interestDelta);
//			notifyInterestDelta(interestDelta);
		} else { //if (changeValue < context.getScaling().getInteresting()) {
			changedElements.add(element);
			delete(element, context);
		}
		return true;
	}

	/**
	 * Used to migrate old activity to new activity events
	 * 
	 * @since 2.1
	 */
	public InteractionContext migrateLegacyActivity(InteractionContext context) {
		LegacyActivityAdaptor adaptor = new LegacyActivityAdaptor();
		InteractionContext newMetaContext = new InteractionContext(context.getHandleIdentifier(),
				ContextCore.getCommonContextScaling());
		for (InteractionEvent event : context.getInteractionHistory()) {
			InteractionEvent temp = adaptor.parseInteractionEvent(event);
			if (temp != null) {
				newMetaContext.parseEvent(temp);
			}
		}
		return newMetaContext;
	}

	@SuppressWarnings("deprecation")
	private void notifyElementsDeleted(List<IInteractionElement> interestDelta) {
		if (!interestDelta.isEmpty()) {
			for (IInteractionContextListener listener : contextListeners) {
				if (listener instanceof IInteractionContextListener2) {
					((IInteractionContextListener2) listener).elementsDeleted(interestDelta);
				} else {
					// need this for legacy support
					for (IInteractionElement element : interestDelta) {
						listener.elementDeleted(element);
					}
				}
			}
		}
	}

	public void notifyInterestDelta(List<IInteractionElement> interestDelta) {
		if (!interestDelta.isEmpty()) {
			for (IInteractionContextListener listener : contextListeners) {
				listener.interestChanged(interestDelta);
			}
		}
	}

	/**
	 * Copy the listener list in case it is modified during the notificiation.
	 * 
	 * @param node
	 */
	public void notifyRelationshipsChanged(IInteractionElement node) {
		if (suppressListenerNotification) {
			return;
		}
		for (IInteractionContextListener listener : contextListeners) {
			listener.relationsChanged(node);
		}
	}

	public void processActivityMetaContextEvent(InteractionEvent event) {
		IInteractionElement element = getActivityMetaContext().parseEvent(event);
		for (IInteractionContextListener listener : activityMetaContextListeners) {
			try {
				List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
				changed.add(element);
				listener.interestChanged(changed);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Context listener failed: "
						+ listener.getClass().getCanonicalName(), t));
			}
		}
	}

	public IInteractionElement processInteractionEvent(InteractionEvent event) {
		return processInteractionEvent(event, true);
	}

	public IInteractionElement processInteractionEvent(InteractionEvent event, boolean propagateToParents) {
		return processInteractionEvent(event, propagateToParents, true);
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
		for (IInteractionContext globalContext : globalContexts) {
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

	public void processInteractionEvents(List<InteractionEvent> events, boolean propagateToParents) {
		Set<IInteractionElement> compositeDelta = new HashSet<IInteractionElement>();
		for (InteractionEvent event : events) {
			if (isContextActive()) {
				compositeDelta.addAll(internalProcessInteractionEvent(event, activeContext, propagateToParents));
			}
			for (IInteractionContext globalContext : globalContexts) {
				if (globalContext.getContentLimitedTo().equals(event.getStructureKind())) {
					internalProcessInteractionEvent(event, globalContext, propagateToParents);
				}
			}
		}
		notifyInterestDelta(new ArrayList<IInteractionElement>(compositeDelta));
	}

	/**
	 * Policy is that a parent should not have an interest lower than that of one of its children. This meets our goal
	 * of having them decay no faster than the children while having their interest be proportional to the interest of
	 * their children.
	 */
	private void propegateInterestToParents(IInteractionContext interactionContext, InteractionEvent.Kind kind,
			IInteractionElement node, float previousInterest, float decayOffset, int level,
			List<IInteractionElement> interestDelta) {

		if (level > MAX_PROPAGATION || node == null || node.getHandleIdentifier() == null
				|| node.getInterest().getValue() <= 0) {
			return;
		}

		checkForLandmarkDeltaAndNotify(previousInterest, node);
		level++; // original is 1st level

		// NOTE: original code summed parent interest
//		float propagatedIncrement = node.getInterest().getValue() - previousInterest + decayOffset;

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
			String parentContentType = bridge.getContentType(parentHandle);

			IInteractionElement parentElement = interactionContext.get(parentHandle);
			float parentPreviousInterest = 0;
			if (parentElement != null && parentElement.getInterest() != null) {
				parentPreviousInterest = parentElement.getInterest().getValue();
			}

			// NOTE: if element marked as landmark, this propagates the landmark value to all parents
			float increment = interactionContext.getScaling().getInteresting();
			if (parentPreviousInterest < node.getInterest().getValue()) {
				increment = node.getInterest().getValue() - parentPreviousInterest;
				InteractionEvent propagationEvent = new InteractionEvent(InteractionEvent.Kind.PROPAGATION,
						parentContentType, parentHandle, SOURCE_ID_MODEL_PROPAGATION,
						IInteractionContextManager.CONTAINMENT_PROPAGATION_ID, increment);
				parentElement = addInteractionEvent(interactionContext, propagationEvent);
			}

			// NOTE: this might be redundant
			if (parentElement != null && kind.isUserEvent()
					&& parentElement.getInterest().getValue() < ContextCore.getCommonContextScaling().getInteresting()) {
				float parentOffset = ContextCore.getCommonContextScaling().getInteresting()
						- parentElement.getInterest().getValue() + increment;
				addInteractionEvent(interactionContext, new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
						parentElement.getContentType(), parentElement.getHandleIdentifier(),
						SOURCE_ID_DECAY_CORRECTION, parentOffset));
			}

			if (parentElement != null
					&& isInterestDelta(parentPreviousInterest, parentElement.getInterest().isPredicted(),
							parentElement.getInterest().isPropagated(), parentElement)) {
				interestDelta.add(0, parentElement);
			}
			propegateInterestToParents(interactionContext, kind, parentElement, parentPreviousInterest, decayOffset,
					level, interestDelta);
		}
	}

	public void removeActivityMetaContextListener(IInteractionContextListener listener) {
		activityMetaContextListeners.remove(listener);
	}

	public void removeAllListeners() {
		waitingContextListeners.clear();
		contextListeners.clear();
	}

	/**
	 * TODO: worry about decay-related change if predicted interest dacays
	 */
	@SuppressWarnings("deprecation")
	public void removeErrorPredictedInterest(String handle, String kind, boolean notify) {
		if (activeContext.getContextMap().isEmpty()) {
			return;
		}
		if (handle == null) {
			return;
		}
		IInteractionElement element = activeContext.get(handle);
		if (element != null && element.getInterest().isInteresting() && errorElementHandles.contains(handle)) {
			InteractionEvent errorEvent = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle,
					SOURCE_ID_MODEL_ERROR, ContextCore.getCommonContextScaling().getErrorInterest());
			processInteractionEvent(errorEvent, true);
			numInterestingErrors--;
			errorElementHandles.remove(handle);
			// TODO: this results in double-notification
			if (notify) {
				for (IInteractionContextListener listener : contextListeners) {
					List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
					changed.add(element);
					listener.interestChanged(changed);
				}
			}
		}
	}

	public void removeGlobalContext(IInteractionContext context) {
		globalContexts.remove(context);
	}

	public void removeListener(IInteractionContextListener listener) {
		waitingContextListeners.remove(listener);
		contextListeners.remove(listener);
	}

	public void resetActivityHistory() {
		activityMetaContext = new InteractionContext(IInteractionContextManager.CONTEXT_HISTORY_FILE_NAME,
				ContextCore.getCommonContextScaling());
		saveActivityContext();
	}

	public void resetLandmarkRelationshipsOfKind(String reltationKind) {
		for (IInteractionElement landmark : activeContext.getLandmarks()) {
			for (IInteractionRelation edge : landmark.getRelations()) {
				if (edge.getRelationshipHandle().equals(reltationKind)) {
					landmark.clearRelations();
				}
			}
		}
		for (IInteractionContextListener listener : contextListeners) {
			listener.relationsChanged(null);
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
					getFileForContext(IInteractionContextManager.CONTEXT_HISTORY_FILE_NAME));
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Could not save activity history",
					t));
		} finally {
			if (!wasPaused) {
				setContextCapturePaused(false);
			}
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
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "could not save context", t));
		} finally {
			if (!wasPaused) {
				setContextCapturePaused(false);
			}
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

	public void setActivationHistorySuppressed(boolean activationHistorySuppressed) {
		this.activationHistorySuppressed = activationHistorySuppressed;
	}

	public void setActiveSearchEnabled(boolean enabled) {
		for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
			provider.setEnabled(enabled);
		}
	}

	/**
	 * NOTE: If pausing ensure to restore to original state.
	 */
	public void setContextCapturePaused(boolean paused) {
		this.contextCapturePaused = paused;
	}

	public void updateHandle(IInteractionElement element, String newHandle) {
		if (element == null) {
			return;
		}
		getActiveContext().updateElementHandle(element, newHandle);
		for (IInteractionContextListener listener : contextListeners) {
			List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
			changed.add(element);
			listener.interestChanged(changed);
		}
		if (element.getInterest().isLandmark()) {
			for (IInteractionContextListener listener : contextListeners) {
				listener.landmarkAdded(element);
			}
		}
	}
}
