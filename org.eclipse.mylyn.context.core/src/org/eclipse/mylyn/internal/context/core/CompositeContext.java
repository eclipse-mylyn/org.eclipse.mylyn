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

package org.eclipse.mylar.internal.context.core;

import java.util.*;

import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * Delegates to contained contexts.
 * 
 * TODO: should info be propagated proportionally to number of taskscapes?
 * 
 * @author Mik Kersten
 */
public class CompositeContext implements IMylarContext {

	protected Map<String, MylarContext> contexts = new HashMap<String, MylarContext>();
	
	protected IMylarElement activeNode = null;

	public IMylarElement addEvent(InteractionEvent event) {
		Set<MylarContextElement> nodes = new HashSet<MylarContextElement>();
		for (MylarContext context : contexts.values()) {
			MylarContextElement info = (MylarContextElement) context.parseEvent(event);
			nodes.add(info);
		}
		CompositeContextElement compositeNode = new CompositeContextElement(event.getStructureHandle(), nodes);
		return compositeNode;
	}

	public IMylarElement get(String handle) {
		if (contexts.values().size() == 0)
			return null;
		Set<MylarContextElement> nodes = new HashSet<MylarContextElement>();
		for (MylarContext taskscape : contexts.values()) {
			MylarContextElement node = (MylarContextElement) taskscape.get(handle);
			if (node != null) {
				nodes.add(node);
			}
		}
		CompositeContextElement composite = new CompositeContextElement(handle, nodes);
		return composite;
	}

	public List<IMylarElement> getLandmarks() {
		Set<IMylarElement> landmarks = new HashSet<IMylarElement>();
		for (MylarContext taskscape : contexts.values()) {
			for (IMylarElement concreteNode : taskscape.getLandmarkMap()) {
				if (concreteNode != null)
					landmarks.add(get(concreteNode.getHandleIdentifier()));
			}
		}
		return new ArrayList<IMylarElement>(landmarks);
	}

	public List<IMylarElement> getInteresting() {
		Set<IMylarElement> landmarks = new HashSet<IMylarElement>();
		for (MylarContext context : contexts.values()) {
			for (IMylarElement concreteNode : context.getInteresting()) {
				if (concreteNode != null)
					landmarks.add(get(concreteNode.getHandleIdentifier()));
			}
		}
		return new ArrayList<IMylarElement>(landmarks);
	}

	public void setActiveElement(IMylarElement activeElement) {
		this.activeNode = activeElement;
	}

	public IMylarElement getActiveNode() {
		return activeNode;
	}

	public void delete(IMylarElement node) {
		for (MylarContext taskscape : contexts.values()) {
			taskscape.delete(node);
		}
	}

	public void clear() {
		for (MylarContext taskscape : contexts.values()) {
			taskscape.reset();
		}
	}

	Map<String, MylarContext> getContextMap() {
		return contexts;
	}

	public List<IMylarElement> getAllElements() {
		Set<IMylarElement> nodes = new HashSet<IMylarElement>();
		for (MylarContext context : contexts.values()) {
			for (IMylarElement concreteNode : context.getAllElements()) {
				nodes.add(get(concreteNode.getHandleIdentifier()));
			}
		}
		return new ArrayList<IMylarElement>(nodes);
	}

	/**
	 * TODO: sort by date?
	 */
	public List<InteractionEvent> getInteractionHistory() {
		Set<InteractionEvent> events = new HashSet<InteractionEvent>();
		for (MylarContext taskscape : contexts.values())
			events.addAll(taskscape.getInteractionHistory());
		return new ArrayList<InteractionEvent>(events);
	}

	public void updateElementHandle(IMylarElement element, String newHandle) {
		for (MylarContext context : contexts.values()) {
			context.updateElementHandle(element, newHandle);
		}
		element.setHandleIdentifier(newHandle);
	}

	/**
	 * Composite contexts do not have a unique handle identifier.
	 * 
	 * @return	null if no unique handle
	 */
	public String getHandleIdentifier() {
		if (contexts.values().size() == 1) {
			return contexts.keySet().iterator().next();
		} else {
			return null;
		}
	}
}
