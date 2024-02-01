/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Delegates to contained contexts. TODO: should info be propagated proportionally to number of taskscapes?
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 * @author David Green bug 257977 isInteresting
 */
public class CompositeInteractionContext implements IInteractionContext {

	protected Map<String, InteractionContext> contexts = new HashMap<>();

	protected IInteractionElement activeNode = null;

	private final IInteractionContextScaling contextScaling;

	public String contentLimitedTo = null;

	public CompositeInteractionContext(IInteractionContextScaling contextScaling) {
		this.contextScaling = contextScaling;
	}

	public IInteractionElement addEvent(InteractionEvent event) {
		List<InteractionContextElement> nodes = new ArrayList<>();
		for (InteractionContext context : contexts.values()) {
			InteractionContextElement info = (InteractionContextElement) context.parseEvent(event);
			nodes.add(info);
		}
		CompositeContextElement compositeNode = new CompositeContextElement(event.getStructureHandle(), nodes,
				contextScaling);
		return compositeNode;
	}

	@Override
	public IInteractionElement get(String handle) {
		if (handle == null || contexts.size() == 0) {
			return null;
		}
		List<InteractionContextElement> nodes = new ArrayList<>();
		for (InteractionContext taskscape : contexts.values()) {
			InteractionContextElement node = (InteractionContextElement) taskscape.get(handle);
			if (node != null) {
				nodes.add(node);
			}
		}
		CompositeContextElement composite = new CompositeContextElement(handle, nodes, contextScaling);
		return composite;
	}

	@Override
	public List<IInteractionElement> getLandmarks() {
		Set<IInteractionElement> landmarks = new HashSet<>();
		for (InteractionContext taskscape : contexts.values()) {
			for (IInteractionElement concreteNode : taskscape.getLandmarks()) {
				if (concreteNode != null) {
					landmarks.add(get(concreteNode.getHandleIdentifier()));
				}
			}
		}
		return new ArrayList<>(landmarks);
	}

	@Override
	public List<IInteractionElement> getInteresting() {
		Set<IInteractionElement> landmarks = new HashSet<>();
		for (InteractionContext context : contexts.values()) {
			for (IInteractionElement concreteNode : context.getInteresting()) {
				if (concreteNode != null) {
					landmarks.add(get(concreteNode.getHandleIdentifier()));
				}
			}
		}
		return new ArrayList<>(landmarks);
	}

	@Override
	public boolean isInteresting(String elementHandle) {
		for (InteractionContext context : contexts.values()) {
			if (context.isInteresting(elementHandle)) {
				return true;
			}
		}
		return false;
	}

	public void setActiveElement(IInteractionElement activeElement) {
		activeNode = activeElement;
	}

	@Override
	public IInteractionElement getActiveNode() {
		return activeNode;
	}

	@Override
	public void delete(IInteractionElement node) {
		for (InteractionContext taskscape : contexts.values()) {
			taskscape.delete(node);
		}
	}

	@Override
	public void delete(Collection<IInteractionElement> nodes) {
		for (InteractionContext context : contexts.values()) {
			context.delete(nodes);
		}
	}

	public void clear() {
		for (InteractionContext taskscape : contexts.values()) {
			taskscape.reset();
		}
	}

	public Map<String, InteractionContext> getContextMap() {
		return contexts;
	}

	@Override
	public List<IInteractionElement> getAllElements() {
		Set<IInteractionElement> nodes = new HashSet<>();
		for (InteractionContext context : contexts.values()) {
			for (IInteractionElement concreteNode : context.getAllElements()) {
				nodes.add(get(concreteNode.getHandleIdentifier()));
			}
		}
		return new ArrayList<>(nodes);
	}

	/**
	 * TODO: sort by date?
	 */
	@Override
	public List<InteractionEvent> getInteractionHistory() {
		Set<InteractionEvent> events = new HashSet<>();
		for (InteractionContext taskscape : contexts.values()) {
			events.addAll(taskscape.getInteractionHistory());
		}
		return new ArrayList<>(events);
	}

	@Override
	public void updateElementHandle(IInteractionElement element, String newHandle) {
		for (InteractionContext context : contexts.values()) {
			context.updateElementHandle(element, newHandle);
		}
		element.setHandleIdentifier(newHandle);
	}

	/**
	 * Composite contexts do not have a unique handle identifier.
	 * 
	 * @return null if no unique handle
	 */
	@Override
	public String getHandleIdentifier() {
		if (contexts.size() == 1) {
			return contexts.keySet().iterator().next();
		} else {
			return null;
		}
	}

	@Override
	public IInteractionContextScaling getScaling() {
		return contextScaling;
	}

	@Override
	public String getContentLimitedTo() {
		return contentLimitedTo;
	}

	@Override
	public void setContentLimitedTo(String contentLimitedTo) {
		this.contentLimitedTo = contentLimitedTo;
	}

	public void addEvents(IInteractionContext otherContext) {
		for (InteractionContext context : contexts.values()) {
			context.addEvents(otherContext);
		}
	}
}
