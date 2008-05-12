/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;

/**
 * @since 3.0
 */
public interface IInteractionContextManager {

	// API-3.0: move constants

	public static final String PROPERTY_CONTEXT_ACTIVE = "org.eclipse.mylyn.context.core.context.active";

	public static final String CONTEXT_FILENAME_ENCODING = "UTF-8";

	public static final String ACTIVITY_DELTA_DEACTIVATED = "deactivated";

	public static final String ACTIVITY_DELTA_ACTIVATED = "activated";

	public static final String ACTIVITY_DELTA_ADDED = "added";

	public static final String ACTIVITY_DELTA_REMOVED = "removed";

	public static final String ACTIVITY_DELTA_STARTED = "started";

	public static final String ACTIVITY_DELTA_STOPPED = "stopped";

	public static final String ACTIVITY_HANDLE_NONE = "none";

	public static final String ACTIVITY_ORIGINID_WORKBENCH = "org.eclipse.ui.workbench";

	public static final String ACTIVITY_ORIGINID_OS = "os";

	public static final String ACTIVITY_ORIGINID_USER = "user";

	public static final String ACTIVITY_STRUCTUREKIND_LIFECYCLE = "lifecycle";

	public static final String ACTIVITY_STRUCTUREKIND_TIMING = "timing";

	public static final String ACTIVITY_STRUCTUREKIND_ACTIVATION = "activation";

	public static final String CONTEXT_HISTORY_FILE_NAME = "activity";

	public static final String OLD_CONTEXT_HISTORY_FILE_NAME = "context-history";

	public static final String CONTAINMENT_PROPAGATION_ID = "org.eclipse.mylyn.core.model.edges.containment";

	public static final String CONTEXT_FILE_EXTENSION = ".xml.zip";

	public static final String CONTEXT_FILE_EXTENSION_OLD = ".xml";

	public static final String SOURCE_ID_DECAY = "org.eclipse.mylyn.core.model.interest.decay";

	/**
	 * @return null if the element handle is null or if the element is not found in the active task context.
	 */
	public abstract IInteractionElement getElement(String elementHandle);

	public abstract void addListener(IInteractionContextListener listener);

	public abstract void removeListener(IInteractionContextListener listener);

	public abstract void removeAllListeners();

	public abstract Collection<InteractionContext> getActiveContexts();

	public abstract void activateContext(String handleIdentifier);

	public abstract void deactivateAllContexts();

	public abstract void deactivateContext(String handleIdentifier);

	public abstract void deleteContext(String handleIdentifier);

	/**
	 * @return false if the map could not be read for any reason
	 */
	public abstract IInteractionContext loadContext(String handleIdentifier);

	public abstract void saveContext(String handleIdentifier);

	public abstract IInteractionContext getActiveContext();

	public abstract List<IInteractionElement> getActiveLandmarks();

	public abstract Collection<IInteractionElement> getInterestingDocuments(IInteractionContext context);

	public abstract Collection<IInteractionElement> getInterestingDocuments();

	/**
	 * Manipulates interest for the active context.
	 * 
	 * API-3.0: revise or remove this and it's helper
	 */
	public abstract boolean manipulateInterestForElement(IInteractionElement element, boolean increment,
			boolean forceLandmark, boolean preserveUninteresting, String sourceId);

	/**
	 * @return true if interest was manipulated successfully
	 */
	public abstract boolean manipulateInterestForElement(IInteractionElement element, boolean increment,
			boolean forceLandmark, boolean preserveUninteresting, String sourceId, IInteractionContext context);

	public abstract void updateHandle(IInteractionElement element, String newHandle);

	public abstract void delete(IInteractionElement element);

	public abstract void copyContext(String targetcontextHandle, File sourceContextFile);

	/**
	 * clones context from source to destination
	 * 
	 * @since 2.1
	 */
	public abstract void cloneContext(String sourceContextHandle, String destinationContextHandle);

	public abstract Collection<IInteractionContext> getGlobalContexts();

	public IInteractionContext getActivityMetaContext();

	public void loadActivityMetaContext();

	public void processActivityMetaContextEvent(InteractionEvent event);

	public void resetActivityHistory();

	public IInteractionElement getActiveElement();

	/**
	 * TODO: consider using IInteractionElement instead, or making other methods consistent
	 */
	public IInteractionElement processInteractionEvent(Object object, Kind eventKind, String origin,
			IInteractionContext context);

	public IInteractionElement processInteractionEvent(InteractionEvent event);

	public IInteractionElement processInteractionEvent(InteractionEvent event, boolean propagateToParents);

	public void processInteractionEvents(List<InteractionEvent> events, boolean propagateToParents);

	public IInteractionElement processInteractionEvent(InteractionEvent event, boolean propagateToParents,
			boolean notifyListeners);

	public void saveActivityContext();

	/** public for testing * */
	public List<InteractionEvent> collapseEventsByHour(List<InteractionEvent> eventsToCollapse);

	public File getFileForContext(String handleIdentifier);

	public boolean isContextActive();

	/**
	 * NOTE: If pausing ensure to restore to original state.
	 */
	public void setContextCapturePaused(boolean paused);

	public void addActivityMetaContextListener(IInteractionContextListener listener);

	/**
	 * Lazily loads set of handles with corresponding contexts.
	 */
	public boolean hasContext(String handleIdentifier);

	public void addGlobalContext(IInteractionContext context);

	public void removeGlobalContext(IInteractionContext context);

	public abstract boolean isContextActivePropertySet();

	public abstract boolean isContextCapturePaused();

	public abstract boolean isValidContextFile(File file);

	public abstract void removeActivityMetaContextListener(IInteractionContextListener context_listener);

}