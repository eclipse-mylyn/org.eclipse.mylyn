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

package org.eclipse.mylar.context.core;

import java.util.List;

import org.eclipse.mylar.internal.context.core.CompositeContextElement;
import org.eclipse.mylar.internal.context.core.IMylarSearchOperation;
import org.eclipse.mylar.internal.context.core.MylarContextElement;
import org.eclipse.mylar.internal.context.core.MylarContextRelation;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRelationProvider implements IMylarContextListener {

	protected final String DOS_0_LABEL = "disabled";

	protected final String DOS_1_LABEL = "landmark resources";

	protected final String DOS_2_LABEL = "interesting resources";

	protected final String DOS_3_LABEL = "interesting projects";

	protected final String DOS_4_LABEL = "project dependencies";

	protected final String DOS_5_LABEL = "entire workspace (slow)";
	
	private boolean enabled = false;

	private String id;

	private String structureKind;

	private int degreeOfSeparation;

	public String getId() {
		return id;
	}

	public AbstractRelationProvider(String structureKind, String id) {
		this.id = id;
		this.structureKind = structureKind;
		degreeOfSeparation = getDefaultDegreeOfSeparation();
	}

	public abstract List<IDegreeOfSeparation> getDegreesOfSeparation();
	
	protected abstract int getDefaultDegreeOfSeparation();

	protected abstract void findRelated(final IMylarElement node, int degreeOfSeparation);

	/**
	 * @param limitTo
	 *            Only used in thye AbstractJavaRelationshipProvider for the
	 *            search type
	 */
	public abstract IMylarSearchOperation getSearchOperation(IMylarElement node, int limitTo, int degreeOfSeparation);

	public abstract String getName();

	public boolean acceptResultElement(Object element) {
		return true;
	}

	public void contextActivated(IMylarContext taskscape) {

	}

	public void contextCleared(IMylarContext context) {
		// ignore
	}
	
	public void landmarkAdded(IMylarElement node) {
		if (enabled) {
			findRelated(node, degreeOfSeparation);
		}
	}

	public void landmarkRemoved(IMylarElement node) {
		// ContextCorePlugin.getTaskscapeManager().removeEdge(element, id);
	}

	protected void searchCompleted(IMylarElement landmark) {
		if (landmark.getRelations().size() > 0) {
			ContextCorePlugin.getContextManager().notifyRelationshipsChanged(landmark);
		}
	}

	protected void incrementInterest(IMylarElement node, String elementKind, String elementHandle,
			int degreeOfSeparation) {
		int predictedInterest = 1;// (7-degreeOfSeparation) *
		// TaskscapeManager.getScalingFactors().getDegreeOfSeparationScale();
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PREDICTION, elementKind, elementHandle,
				getSourceId(), getId(), null, predictedInterest);
		ContextCorePlugin.getContextManager().handleInteractionEvent(event, false, false);
		createEdge(node, elementKind, elementHandle);
	}

	/**
	 * Public for testing
	 */
	public void createEdge(IMylarElement toNode, String elementKind, String targetHandle) {
		CompositeContextElement targetNode = (CompositeContextElement) ContextCorePlugin.getContextManager().getElement(
				targetHandle);
		if (targetNode == null)
			return;
		MylarContextElement concreteTargetNode = null;
		if (targetNode.getNodes().size() != 1) {
			return;
		} else {
			concreteTargetNode = targetNode.getNodes().iterator().next();
		}
		if (concreteTargetNode != null) {
			for (MylarContextElement sourceNode : ((CompositeContextElement) toNode).getNodes()) {
				MylarContextRelation edge = new MylarContextRelation(elementKind, getId(), sourceNode,
						concreteTargetNode, sourceNode.getContext());
				sourceNode.addEdge(edge);
			}
		}
	}

	protected abstract String getSourceId();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getCurrentDegreeOfSeparation() {
		return degreeOfSeparation;
	}

	public void elementDeleted(IMylarElement node) {
		// we don't care when this happens
	}

	public void contextDeactivated(IMylarContext taskscape) {
		// we don't care about this event
	}

	public void interestChanged(List<IMylarElement> nodes) {
		// we don't care about this event
	}

	public void relationsChanged(IMylarElement node) {
		// we don't care about this event
	}

	@Override
	public String toString() {
		return "(provider for: " + id + ")";
	}

	public String getStructureKind() {
		return structureKind;
	}

	public void setDegreeOfSeparation(int degreeOfSeparation) {
		this.degreeOfSeparation = degreeOfSeparation;
	}

	public abstract String getGenericId();

	public abstract void stopAllRunningJobs();

}
