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

package org.eclipse.mylyn.context.core;

import java.util.List;

import org.eclipse.mylyn.internal.context.core.CompositeContextElement;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;
import org.eclipse.mylyn.internal.context.core.InteractionContextElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextRelation;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Used for Active Search.
 * 
 * NOTE: this facility is not exposed by default in Mylyn 2.0 and likely to change for 3.0.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractRelationProvider implements IInteractionContextListener {

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

	protected abstract void findRelated(final IInteractionElement node, int degreeOfSeparation);

	/**
	 * @param limitTo
	 *            Only used in thye AbstractJavaRelationshipProvider for the search type
	 */
	public abstract IActiveSearchOperation getSearchOperation(IInteractionElement node, int limitTo,
			int degreeOfSeparation);

	public abstract String getName();

	public boolean acceptResultElement(Object element) {
		return true;
	}

	public void contextActivated(IInteractionContext taskscape) {

	}

	public void contextCleared(IInteractionContext context) {
		// ignore
	}

	public void landmarkAdded(IInteractionElement node) {
		if (enabled) {
			findRelated(node, degreeOfSeparation);
		}
	}

	public void landmarkRemoved(IInteractionElement node) {
		// ContextCorePlugin.getTaskscapeManager().removeEdge(element, id);
	}

	protected void searchCompleted(IInteractionElement landmark) {
		if (landmark.getRelations().size() > 0) {
			ContextCorePlugin.getContextManager().notifyRelationshipsChanged(landmark);
		}
	}

	protected void incrementInterest(IInteractionElement node, String elementKind, String elementHandle,
			int degreeOfSeparation) {
		int predictedInterest = 1;// (7-degreeOfSeparation) *
		// TaskscapeManager.getScalingFactors().getDegreeOfSeparationScale();
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PREDICTION, elementKind, elementHandle,
				getSourceId(), getId(), null, predictedInterest);
		ContextCorePlugin.getContextManager().processInteractionEvent(event, false, false);
		createEdge(node, elementKind, elementHandle);
	}

	/**
	 * Public for testing
	 */
	public void createEdge(IInteractionElement toNode, String elementKind, String targetHandle) {
		CompositeContextElement targetNode = (CompositeContextElement) ContextCorePlugin.getContextManager()
				.getElement(targetHandle);
		if (targetNode == null)
			return;
		InteractionContextElement concreteTargetNode = null;
		if (targetNode.getNodes().size() != 1) {
			return;
		} else {
			concreteTargetNode = targetNode.getNodes().iterator().next();
		}
		if (concreteTargetNode != null) {
			for (InteractionContextElement sourceNode : ((CompositeContextElement) toNode).getNodes()) {
				InteractionContextRelation edge = new InteractionContextRelation(elementKind, getId(), sourceNode,
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

	public void elementDeleted(IInteractionElement node) {
		// we don't care when this happens
	}

	public void contextDeactivated(IInteractionContext taskscape) {
		// we don't care about this event
	}

	public void interestChanged(List<IInteractionElement> nodes) {
		// we don't care about this event
	}

	public void relationsChanged(IInteractionElement node) {
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
