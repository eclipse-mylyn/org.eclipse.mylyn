/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.text.SimpleDateFormat;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;

/**
 * This is the core class resposible for context management.
 * 
 * @author Mik Kersten
 * @author Jevgeni Holodkov
 * @author Shawn Minto
 */
// FIXME 3.2 review all FIXME comments
public class InteractionContextManager implements IInteractionContextManager {

	public static final String SOURCE_ID_DECAY = "org.eclipse.mylyn.core.model.interest.decay"; //$NON-NLS-1$

	public static final String CONTEXT_FILE_EXTENSION_OLD = ".xml"; //$NON-NLS-1$

	public static final String CONTEXT_FILE_EXTENSION = ".xml.zip"; //$NON-NLS-1$

	public static final String CONTAINMENT_PROPAGATION_ID = "org.eclipse.mylyn.core.model.edges.containment"; //$NON-NLS-1$

	/**
	 * @deprecated not used anymore (used in version < 1.0.1)
	 */
	@Deprecated
	public static final String OLD_CONTEXT_HISTORY_FILE_NAME = "context-history"; //$NON-NLS-1$

	public static final String CONTEXT_HISTORY_FILE_NAME = "activity"; //$NON-NLS-1$

	public static final String CONTEXT_FILENAME_ENCODING = "UTF-8"; //$NON-NLS-1$

	public static final String PROPERTY_CONTEXT_ACTIVE = "org.eclipse.mylyn.context.core.context.active"; //$NON-NLS-1$

	public static final String ACTIVITY_STRUCTUREKIND_ACTIVATION = "activation"; //$NON-NLS-1$

	public static final String ACTIVITY_STRUCTUREKIND_TIMING = "timing"; //$NON-NLS-1$

	public static final String ACTIVITY_STRUCTUREKIND_WORKINGSET = "workingset"; //$NON-NLS-1$

	public static final String ACTIVITY_STRUCTUREKIND_LIFECYCLE = "lifecycle"; //$NON-NLS-1$

	public static final String ACTIVITY_ORIGINID_USER = "user"; //$NON-NLS-1$

	public static final String ACTIVITY_ORIGINID_OS = "os"; //$NON-NLS-1$

	public static final String ACTIVITY_ORIGINID_WORKBENCH = "org.eclipse.ui.workbench"; //$NON-NLS-1$

	public static final String ACTIVITY_HANDLE_NONE = "none"; //$NON-NLS-1$

	public static final String ACTIVITY_DELTA_STOPPED = "stopped"; //$NON-NLS-1$

	public static final String ACTIVITY_DELTA_STARTED = "started"; //$NON-NLS-1$

	public static final String ACTIVITY_DELTA_REMOVED = "removed"; //$NON-NLS-1$

	public static final String ACTIVITY_DELTA_ADDED = "added"; //$NON-NLS-1$

	public static final String ACTIVITY_DELTA_ACTIVATED = "activated"; //$NON-NLS-1$

	public static final String ACTIVITY_DELTA_DEACTIVATED = "deactivated"; //$NON-NLS-1$

	// TODO: move constants
	private static final int MAX_PROPAGATION = 17; // TODO: parametrize this

	private static final ILock metaContextLock = Job.getJobManager().newLock();

	private static final String PREFERENCE_ATTENTION_MIGRATED = "mylyn.attention.migrated"; //$NON-NLS-1$

	private static final String SOURCE_ID_DECAY_CORRECTION = "org.eclipse.mylyn.core.model.interest.decay.correction"; //$NON-NLS-1$

	private static final String SOURCE_ID_MODEL_ERROR = "org.eclipse.mylyn.core.model.interest.propagation"; //$NON-NLS-1$

	private static final String SOURCE_ID_MODEL_PROPAGATION = "org.eclipse.mylyn.core.model.interest.propagation"; //$NON-NLS-1$

	private boolean activationHistorySuppressed = false;

	private final CompositeInteractionContext activeContext = new CompositeInteractionContext(
			ContextCore.getCommonContextScaling());

	private InteractionContext activityMetaContext = null;

	private final List<AbstractContextListener> activityMetaContextListeners = new CopyOnWriteArrayList<AbstractContextListener>();

	private boolean contextCapturePaused = false;

	private final List<AbstractContextListener> contextListeners = new CopyOnWriteArrayList<AbstractContextListener>();

	private final List<String> errorElementHandles = new ArrayList<String>();

	/**
	 * Global contexts do not participate in the regular activation lifecycle but are instead activated and deactivated
	 * by clients.
	 */
	private final Collection<IInteractionContext> globalContexts = new HashSet<IInteractionContext>();

	private int numInterestingErrors = 0;

	private boolean suppressListenerNotification = false;

	private final List<AbstractContextListener> waitingContextListeners = new ArrayList<AbstractContextListener>();

	private final LocalContextStore contextStore;

	public InteractionContextManager(LocalContextStore contextStore) {
		this.contextStore = contextStore;
	}

	public void activateContext(String handleIdentifier) {
		try {
			IInteractionContext loadedContext = activeContext.getContextMap().get(handleIdentifier);
			final IInteractionContext context;
			if (loadedContext == null) {
				context = contextStore.loadContext(handleIdentifier);
			} else {
				context = loadedContext;
			}
			// FIXME should not fire event when context == null
			for (final AbstractContextListener listener : contextListeners) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
								+ listener.getClass(), e));
					}

					public void run() throws Exception {
						ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.PRE_ACTIVATED,
								context.getHandleIdentifier(), context, null);
						listener.contextChanged(event);
					}
				});
			}

			if (context != null) {
				suppressListenerNotification = true;
				internalActivateContext(context);
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Could not load context")); //$NON-NLS-1$
			}
			suppressListenerNotification = false;
			contextListeners.addAll(waitingContextListeners);
			waitingContextListeners.clear();
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Could not activate context", t)); //$NON-NLS-1$
		}
	}

	public void addActivityMetaContextListener(AbstractContextListener listener) {
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
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN,
					"Error during meta activity collapse", e)); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("deprecation")
	public void addErrorPredictedInterest(String handle, String kind, boolean notify) {
		if (numInterestingErrors > ((InteractionContextScaling) ContextCore.getCommonContextScaling()).getMaxNumInterestingErrors()
				|| activeContext.getContextMap().isEmpty()) {
			return;
		}
		InteractionEvent errorEvent = new InteractionEvent(InteractionEvent.Kind.PROPAGATION, kind, handle,
				SOURCE_ID_MODEL_ERROR,
				((InteractionContextScaling) ContextCore.getCommonContextScaling()).getErrorInterest());
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

	public void addListener(AbstractContextListener listener) {
		Assert.isNotNull(listener);
		if (suppressListenerNotification && !waitingContextListeners.contains(listener)) {
			waitingContextListeners.add(listener);
		} else {
			if (!contextListeners.contains(listener)) {
				contextListeners.add(listener);
			}
		}
	}

	protected void checkForLandmarkDeltaAndNotify(float previousInterest, final IInteractionElement node,
			final IInteractionContext context) {
		// TODO: don't call interestChanged if it's a landmark?
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
				.getStructureBridge(node.getContentType());
		if (bridge.canBeLandmark(node.getHandleIdentifier())) {
			if (previousInterest >= ContextCore.getCommonContextScaling().getLandmark()
					&& !node.getInterest().isLandmark()) {
				for (final AbstractContextListener listener : contextListeners) {
					SafeRunner.run(new ISafeRunnable() {
						public void handleException(Throwable e) {
							StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
									"Listener failed: " //$NON-NLS-1$
											+ listener.getClass(), e));
						}

						public void run() throws Exception {
							List<IInteractionElement> changed = new ArrayList<IInteractionElement>(1);
							changed.add(node);
							ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.LANDMARKS_REMOVED,
									context.getHandleIdentifier(), context, changed);
							listener.contextChanged(event);
						}
					});
				}
			} else if (previousInterest < ContextCore.getCommonContextScaling().getLandmark()
					&& node.getInterest().isLandmark()) {
				for (final AbstractContextListener listener : contextListeners) {
					SafeRunner.run(new ISafeRunnable() {
						public void handleException(Throwable e) {
							StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
									"Listener failed: " //$NON-NLS-1$
											+ listener.getClass(), e));
						}

						public void run() throws Exception {
							List<IInteractionElement> changed = new ArrayList<IInteractionElement>(1);
							changed.add(node);
							ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.LANDMARKS_ADDED,
									context.getHandleIdentifier(), context, changed);
							listener.contextChanged(event);
						}
					});
				}
			}
		}
	}

	public InteractionContext collapseActivityMetaContext(InteractionContext context) {
		Map<String, List<InteractionEvent>> attention = new HashMap<String, List<InteractionEvent>>();
		InteractionContext tempContext = new InteractionContext(InteractionContextManager.CONTEXT_HISTORY_FILE_NAME,
				ContextCore.getCommonContextScaling());
		for (InteractionEvent event : context.getInteractionHistory()) {

			if (event.getKind().equals(InteractionEvent.Kind.ATTENTION)
					&& event.getDelta().equals(InteractionContextManager.ACTIVITY_DELTA_ADDED)) {
				if (event.getStructureHandle() == null || event.getStructureHandle().equals("")) { //$NON-NLS-1$
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

	public void deactivateAllContexts() {
		Set<String> handles = new HashSet<String>(activeContext.getContextMap().keySet());
		for (String handleIdentifier : handles) {
			deactivateContext(handleIdentifier);
		}
	}

	public void deactivateContext(String handleIdentifier) {
		try {
			System.setProperty(InteractionContextManager.PROPERTY_CONTEXT_ACTIVE, Boolean.FALSE.toString());

			final IInteractionContext context = activeContext.getContextMap().get(handleIdentifier);
			if (context != null) {
				contextStore.saveContext(context);
				activeContext.getContextMap().remove(handleIdentifier);

				setContextCapturePaused(true);
				for (final AbstractContextListener listener : contextListeners) {
					SafeRunner.run(new ISafeRunnable() {
						public void handleException(Throwable e) {
							StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
									"Listener failed: " //$NON-NLS-1$
											+ listener.getClass(), e));
						}

						public void run() throws Exception {
							ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.DEACTIVATED,
									context.getHandleIdentifier(), context, null);
							listener.contextChanged(event);
						}
					});
				}
				setContextCapturePaused(false);
			}
			if (!activationHistorySuppressed) {
				processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
						InteractionContextManager.ACTIVITY_STRUCTUREKIND_ACTIVATION, handleIdentifier,
						InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
						InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED, 1f));
			}
//			saveActivityMetaContext();
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Could not deactivate context", t)); //$NON-NLS-1$
		}
	}

	public void deleteElement(IInteractionElement element) {
		delete(element, getActiveContext());
		notifyElementsDeleted(getActiveContext(), Arrays.asList(new IInteractionElement[] { element }));
	}

	public void deleteElements(Collection<IInteractionElement> elements) {
		Assert.isNotNull(elements);
		IInteractionContext context = getActiveContext();
		if (elements.size() == 0 || context == null) {
			return;
		}

		context.delete(elements);

		notifyElementsDeleted(getActiveContext(), new ArrayList<IInteractionElement>(elements));
	}

	private void delete(IInteractionElement element, IInteractionContext context) {
		if (element == null || context == null) {
			return;
		}
		context.delete(element);
	}

	public void deleteContext(final String handleIdentifier) {
		final IInteractionContext context = activeContext.getContextMap().get(handleIdentifier);

		setContextCapturePaused(true);
		eraseContext(handleIdentifier);

		contextStore.deleteContext(handleIdentifier);
		for (final AbstractContextListener listener : contextListeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.CLEARED, handleIdentifier,
							context, null);
					listener.contextChanged(event);
				}
			});
		}
		setContextCapturePaused(false);
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

	private void eraseContext(String handleIdentifier) {
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

	public Set<IInteractionElement> getActiveLandmarks() {
		List<IInteractionElement> allLandmarks = activeContext.getLandmarks();
		Set<IInteractionElement> acceptedLandmarks = new HashSet<IInteractionElement>();
		for (IInteractionElement node : allLandmarks) {
			AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(node.getContentType());

			if (bridge.canBeLandmark(node.getHandleIdentifier())) {
				acceptedLandmarks.add(node);
			}
		}
		return acceptedLandmarks;
	}

	public InteractionContext getActivityMetaContext() {
		try {
			metaContextLock.acquire();
			if (activityMetaContext == null) {
				loadActivityMetaContext();
			}
		} finally {
			metaContextLock.release();
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

	public Collection<IInteractionContext> getGlobalContexts() {
		return globalContexts;
	}

	public Collection<IInteractionElement> getActiveDocuments() {
		return getActiveDocuments(activeContext);
	}

	public Set<IInteractionElement> getActiveDocuments(IInteractionContext context) {
		Set<IInteractionElement> set = new HashSet<IInteractionElement>();
		if (context == null) {
			return set;
		} else {
			List<IInteractionElement> allIntersting = context.getInteresting();
			for (IInteractionElement node : allIntersting) {
				if (ContextCore.getStructureBridge(node.getContentType()).isDocument(node.getHandleIdentifier())) {
					set.add(node);
				}
			}
			return set;
		}
	}

	/**
	 * For testing.
	 */
	public List<AbstractContextListener> getListeners() {
		return Collections.unmodifiableList(contextListeners);
	}

	/**
	 * Lazily loads set of handles with corresponding contexts.
	 */
	public boolean hasContext(String handleIdentifier) {
		if (handleIdentifier == null) {
			return false;
		}

		if (getActiveContext() != null && handleIdentifier.equals(getActiveContext().getHandleIdentifier())) {
			return !getActiveContext().getAllElements().isEmpty();
		} else {
			return contextStore.hasContext(handleIdentifier);
		}
	}

	/**
	 * Public for testing, activate via handle
	 */
	public void internalActivateContext(final IInteractionContext context) {
		Assert.isTrue(context instanceof InteractionContext, "Must provide a concrete InteractionContext"); //$NON-NLS-1$

		System.setProperty(InteractionContextManager.PROPERTY_CONTEXT_ACTIVE, Boolean.TRUE.toString());
		activeContext.getContextMap().put(context.getHandleIdentifier(), (InteractionContext) context);
//		if (contextFiles != null) {
//			contextFiles.add(getFileForContext(context.getHandleIdentifier()));
//		}
		if (!activationHistorySuppressed) {
			processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
					InteractionContextManager.ACTIVITY_STRUCTUREKIND_ACTIVATION, context.getHandleIdentifier(),
					InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
					InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f));
		}

		for (final AbstractContextListener listener : contextListeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.ACTIVATED,
							context.getHandleIdentifier(), context, null);
					listener.contextChanged(event);
				}
			});
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

		checkForLandmarkDeltaAndNotify(previousInterest, element, interactionContext);
		return interestDelta;
	}

	public boolean isActivationHistorySuppressed() {
		return activationHistorySuppressed;
	}

	// TODO consider removing check for pause and making clients explicitly determine this, 
	// or provide a separate method
	public boolean isContextActive() {
		return !contextCapturePaused && activeContext.getContextMap().values().size() > 0;
	}

	@Deprecated
	public boolean isContextActivePropertySet() {
		return Boolean.parseBoolean(System.getProperty(InteractionContextManager.PROPERTY_CONTEXT_ACTIVE));
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

	public void loadActivityMetaContext() {
		if (contextStore != null) {
			for (final AbstractContextListener listener : activityMetaContextListeners) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
								+ listener.getClass(), e));
					}

					public void run() throws Exception {
						ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.PRE_ACTIVATED,
								InteractionContextManager.CONTEXT_HISTORY_FILE_NAME, null, null);
						listener.contextChanged(event);
					}
				});
			}

			try {
				metaContextLock.acquire();

				activityMetaContext = (InteractionContext) contextStore.loadContext(InteractionContextManager.CONTEXT_HISTORY_FILE_NAME);
				if (activityMetaContext == null || activityMetaContext.getInteractionHistory().isEmpty()) {
					File contextHistory = contextStore.getFileForContext(InteractionContextManager.CONTEXT_HISTORY_FILE_NAME);
					if (restoreSnapshot(contextHistory)) {
						activityMetaContext = (InteractionContext) contextStore.loadContext(InteractionContextManager.CONTEXT_HISTORY_FILE_NAME);
					}
				}

				if (activityMetaContext == null) {
					resetActivityMetaContext();
				} else if (!ContextCorePlugin.getDefault().getPluginPreferences().getBoolean(
						PREFERENCE_ATTENTION_MIGRATED)) {
					activityMetaContext = migrateLegacyActivity(activityMetaContext);
					saveActivityMetaContext();
					ContextCorePlugin.getDefault().getPluginPreferences().setValue(PREFERENCE_ATTENTION_MIGRATED, true);
					ContextCorePlugin.getDefault().savePluginPreferences();
				}
			} finally {
				metaContextLock.release();
			}

			for (final AbstractContextListener listener : activityMetaContextListeners) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
								+ listener.getClass(), e));
					}

					public void run() throws Exception {
						ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.ACTIVATED,
								activityMetaContext.getHandleIdentifier(), activityMetaContext, null);
						listener.contextChanged(event);
					}
				});
			}
		} else {
			resetActivityMetaContext();
			StatusHandler.log(new Status(IStatus.INFO, ContextCorePlugin.ID_PLUGIN,
					"No context store installed, not restoring activity context.")); //$NON-NLS-1$
		}
	}

	public void saveActivityMetaContext() {
		if (contextStore == null) {
			return;
		}
		boolean wasPaused = contextCapturePaused;
		try {
			metaContextLock.acquire();
			if (!wasPaused) {
				setContextCapturePaused(true);
			}

			InteractionContext context = getActivityMetaContext();
			takeSnapshot(contextStore.getFileForContext(InteractionContextManager.CONTEXT_HISTORY_FILE_NAME));
			contextStore.saveContext(collapseActivityMetaContext(context),
					InteractionContextManager.CONTEXT_HISTORY_FILE_NAME);
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Could not save activity history", //$NON-NLS-1$
					t));
		} finally {
			metaContextLock.release();
			if (!wasPaused) {
				setContextCapturePaused(false);
			}
		}
	}

	/**
	 * COPY: from AbstractExternalizationParticipant
	 */
	protected boolean takeSnapshot(File file) {
		if (file.length() > 0) {
			File originalFile = file.getAbsoluteFile();
			File backup = new File(file.getParentFile(), "." + file.getName()); //$NON-NLS-1$
			backup.delete();
			return originalFile.renameTo(backup);
		}
		return false;
	}

	protected boolean restoreSnapshot(File file) {
		File backup = new File(file.getParentFile(), "." + file.getName()); //$NON-NLS-1$
		File originalFile = file.getAbsoluteFile();
		if (originalFile.exists()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.ENGLISH); //$NON-NLS-1$
			File failed = new File(file.getParentFile(), "failed-" + format.format(new Date()) + "-" //$NON-NLS-1$ //$NON-NLS-2$
					+ originalFile.getName());
			originalFile.renameTo(failed);
		}
		if (backup.exists()) {
			return backup.renameTo(originalFile);
		}
		return false;
	}

	/**
	 * Manipulates interest for the active context.
	 */
	// TODO 3.2 revise or remove this and it's helper
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
				notifyElementsDeleted(context, new ArrayList<IInteractionElement>(changedElements));
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
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(element.getContentType());
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

	private void notifyElementsDeleted(final IInteractionContext context, final List<IInteractionElement> interestDelta) {
		if (!interestDelta.isEmpty()) {
			for (final AbstractContextListener listener : contextListeners) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
								+ listener.getClass(), e));
					}

					public void run() throws Exception {
						ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.ELEMENTS_DELETED,
								context.getHandleIdentifier(), context, interestDelta);
						listener.contextChanged(event);
					}
				});
			}
		}
	}

	@Deprecated
	public void notifyInterestDelta(final List<IInteractionElement> interestDelta) {
		notifyInterestDelta(getActiveContext(), interestDelta);
	}

	public void notifyInterestDelta(final IInteractionContext context, final List<IInteractionElement> interestDelta) {
		if (!interestDelta.isEmpty()) {
			for (final AbstractContextListener listener : contextListeners) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
								+ listener.getClass(), e));
					}

					public void run() throws Exception {
						ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.INTEREST_CHANGED,
								context.getHandleIdentifier(), context, interestDelta);
						listener.contextChanged(event);
					}
				});
			}
		}
	}

	/**
	 * Copy the listener list in case it is modified during the notificiation.
	 * 
	 * @param element
	 */
	public void notifyRelationshipsChanged(final IInteractionElement element) {
		if (suppressListenerNotification) {
			return;
		}
		for (final AbstractContextListener listener : contextListeners) {
			if (listener instanceof IRelationsListener) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
								+ listener.getClass(), e));
					}

					public void run() throws Exception {
						((IRelationsListener) listener).relationsChanged(element);
					}
				});

			}
		}
	}

	public void processActivityMetaContextEvent(InteractionEvent event) {
		IInteractionElement element = getActivityMetaContext().parseEvent(event);

		final List<IInteractionElement> changed = Collections.singletonList(element);
		for (final AbstractContextListener listener : activityMetaContextListeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.INTEREST_CHANGED,
							getActivityMetaContext().getHandleIdentifier(), getActivityMetaContext(), changed);
					listener.contextChanged(event);
				}
			});
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
		AbstractContextStructureBridge structureBridge = ContextCore.getStructureBridge(object);
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

		checkForLandmarkDeltaAndNotify(previousInterest, node, interactionContext);
		level++; // original is 1st level

		// NOTE: original code summed parent interest
//		float propagatedIncrement = node.getInterest().getValue() - previousInterest + decayOffset;

		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
				.getStructureBridge(node.getContentType());
		String parentHandle = bridge.getParentHandle(node.getHandleIdentifier());

		// check if should use child bridge
		for (String contentType : ContextCore.getChildContentTypes(bridge.getContentType())) {
			AbstractContextStructureBridge childBridge = ContextCore.getStructureBridge(contentType);
			Object resolved = childBridge.getObjectForHandle(parentHandle);
			if (resolved != null) {
				AbstractContextStructureBridge canonicalBridge = ContextCore.getStructureBridge(resolved);
				// HACK: hard-coded resource content type
				if (!canonicalBridge.getContentType().equals(ContextCore.CONTENT_TYPE_RESOURCE)) {
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
						InteractionContextManager.CONTAINMENT_PROPAGATION_ID, increment);
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

	public void removeActivityMetaContextListener(AbstractContextListener listener) {
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
		final IInteractionElement element = activeContext.get(handle);
		if (element != null && element.getInterest().isInteresting() && errorElementHandles.contains(handle)) {
			InteractionEvent errorEvent = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle,
					SOURCE_ID_MODEL_ERROR,
					((InteractionContextScaling) ContextCore.getCommonContextScaling()).getErrorInterest());
			processInteractionEvent(errorEvent, true);
			numInterestingErrors--;
			errorElementHandles.remove(handle);
			// TODO: this results in double-notification
			if (notify) {
				for (final AbstractContextListener listener : contextListeners) {
					SafeRunner.run(new ISafeRunnable() {
						public void handleException(Throwable e) {
							StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
									"Listener failed: " //$NON-NLS-1$
											+ listener.getClass(), e));
						}

						public void run() throws Exception {
							// FIXME use singleton list instead that is constructed outside of loop
							List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
							changed.add(element);
							listener.interestChanged(changed);
						}
					});
				}
			}
		}
	}

	public void removeGlobalContext(IInteractionContext context) {
		globalContexts.remove(context);
	}

	public void removeListener(AbstractContextListener listener) {
		waitingContextListeners.remove(listener);
		contextListeners.remove(listener);
	}

	public void resetActivityMetaContext() {
		try {
			metaContextLock.acquire();
			activityMetaContext = new InteractionContext(InteractionContextManager.CONTEXT_HISTORY_FILE_NAME,
					ContextCore.getCommonContextScaling());
			saveActivityMetaContext();
		} finally {
			metaContextLock.release();
		}
	}

	public void resetLandmarkRelationshipsOfKind(String reltationKind) {
		for (IInteractionElement landmark : activeContext.getLandmarks()) {
			for (IInteractionRelation edge : landmark.getRelations()) {
				if (edge.getRelationshipHandle().equals(reltationKind)) {
					landmark.clearRelations();
				}
			}
		}
		for (final AbstractContextListener listener : contextListeners) {
			if (listener instanceof IRelationsListener) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
								+ listener.getClass(), e));
					}

					public void run() throws Exception {
						((IRelationsListener) listener).relationsChanged(null);
					}
				});
			}
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
		synchronized (InteractionContextManager.this) {
			this.contextCapturePaused = paused;
		}
	}

	public void updateHandle(final IInteractionElement element, String newHandle) {
		if (element == null) {
			return;
		}
		final IInteractionContext context = getActiveContext();
		context.updateElementHandle(element, newHandle);
		for (final AbstractContextListener listener : contextListeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					// FIXME use singleton list instead that is constructed outside of loop
					List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
					changed.add(element);
					ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.INTEREST_CHANGED,
							context.getHandleIdentifier(), context, changed);
					listener.contextChanged(event);
				}
			});
		}
		if (element.getInterest().isLandmark()) {
			for (final AbstractContextListener listener : contextListeners) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
								+ listener.getClass(), e));
					}

					public void run() throws Exception {
						List<IInteractionElement> changed = new ArrayList<IInteractionElement>();
						changed.add(element);
						ContextChangeEvent event = new ContextChangeEvent(ContextChangeKind.LANDMARKS_ADDED,
								context.getHandleIdentifier(), context, changed);
						listener.contextChanged(event);
					}
				});
			}
		}
	}
}
