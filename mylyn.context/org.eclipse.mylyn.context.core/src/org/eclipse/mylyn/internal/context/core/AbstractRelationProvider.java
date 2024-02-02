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

import java.util.List;

import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Used for Active Search. NOTE: this facility is not exposed by default in Mylyn 2.0 and likely to change for 3.0.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractRelationProvider extends AbstractContextListener {

	protected final String DOS_0_LABEL = "disabled"; //$NON-NLS-1$

	protected final String DOS_1_LABEL = "landmark resources"; //$NON-NLS-1$

	protected final String DOS_2_LABEL = "interesting resources"; //$NON-NLS-1$

	protected final String DOS_3_LABEL = "interesting projects"; //$NON-NLS-1$

	protected final String DOS_4_LABEL = "project dependencies"; //$NON-NLS-1$

	protected final String DOS_5_LABEL = "entire workspace (slow)"; //$NON-NLS-1$

	private boolean enabled = false;

	private final String id;

	private final String structureKind;

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

	@Override
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
			case LANDMARKS_ADDED:
				if (enabled) {
					for (IInteractionElement node : event.getElements()) {
						findRelated(node, degreeOfSeparation);
					}
				}
				break;
		}
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
		CompositeContextElement targetNode = (CompositeContextElement) ContextCore.getContextManager()
				.getElement(targetHandle);
		if (targetNode == null) {
			return;
		}
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

	@Override
	public String toString() {
		return "(provider for: " + id + ")"; //$NON-NLS-1$ //$NON-NLS-2$
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
