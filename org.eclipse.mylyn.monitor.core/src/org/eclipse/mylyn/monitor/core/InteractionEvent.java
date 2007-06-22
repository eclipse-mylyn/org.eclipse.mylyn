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

package org.eclipse.mylyn.monitor.core;

import java.util.Calendar;
import java.util.Date;

/**
 * Immutable.  Encapsulates interaction between the user and structured elements.
 * 
 * @author Mik Kersten
 * @since	2.0
 */
public class InteractionEvent {

	public enum Kind {
		SELECTION, EDIT, COMMAND, PREFERENCE, PREDICTION, PROPAGATION, MANIPULATION;

		/**
		 * TODO: add PREFERENCE?
		 */
		public boolean isUserEvent() {
			return this == SELECTION || this == EDIT || this == COMMAND || this == PREFERENCE;
		}

		@Override
		public String toString() {
			switch (this) {
			case SELECTION:
				return "selection";
			case EDIT:
				return "edit";
			case COMMAND:
				return "command";
			case PREFERENCE:
				return "preference";
			case PREDICTION:
				return "prediction";
			case PROPAGATION:
				return "propagation";
			case MANIPULATION:
				return "manipulation";
			default:
				return "null";
			}
		}

		public static Kind fromString(String string) {
			if (string == null)
				return null;
			if (string.equals("selection"))
				return SELECTION;
			if (string.equals("edit"))
				return EDIT;
			if (string.equals("command"))
				return COMMAND;
			if (string.equals("preference"))
				return PREFERENCE;
			if (string.equals("prediction"))
				return PREDICTION;
			if (string.equals("propagation"))
				return PROPAGATION;
			if (string.equals("manipulation"))
				return MANIPULATION;
			return null;
		}
	}

	private final Kind kind;

	private final Date date;

	private final Date endDate;

	private final String originId;

	private final String structureKind;

	private final String structureHandle;

	private final String navigation;

	private final String delta;

	private float interestContribution;

	public static final String ID_UNKNOWN = "?";

	public InteractionEvent(Kind kind, String structureKind, String handle, String originId) {
		this(kind, structureKind, handle, originId, 1f); // default
		// contribution
	}

	public InteractionEvent(Kind kind, String structureKind, String handle, String originId, String navigatedRelation) {
		this(kind, structureKind, handle, originId, navigatedRelation, "null", 1f); // default
		// contribution
	}

	public InteractionEvent(Kind kind, String structureKind, String handle, String originId, String navigatedRelation,
			float interestContribution) {
		this(kind, structureKind, handle, originId, navigatedRelation, "null", interestContribution); // default
		// contribution
	}

	/**
	 * Factory method.
	 */
	public static InteractionEvent makeCommand(String originId, String delta) {
		return new InteractionEvent(InteractionEvent.Kind.COMMAND, "null", "null", originId, "null", delta, 1);
	}

	/**
	 * Factory method.
	 */
	public static InteractionEvent makeCopy(InteractionEvent originalEvent, float newInterestContribution) {
		return new InteractionEvent(originalEvent.getKind(), originalEvent.getStructureKind(), originalEvent
				.getStructureHandle(), originalEvent.getOriginId(), originalEvent.getNavigation(), originalEvent
				.getDelta(), newInterestContribution, originalEvent.getDate(), originalEvent.getEndDate());
	}

	/**
	 * Factory method.
	 */
	public static InteractionEvent makePreference(String originId, String delta) {
		return new InteractionEvent(InteractionEvent.Kind.PREFERENCE, "null", "null", originId, "null", delta, 1); // default
		// contribution
	}

	public InteractionEvent(Kind kind, String structureKind, String handle, String originId, float interestContribution) {
		this(kind, structureKind, handle, originId, "null", "null", interestContribution); // default
		// contribution
	}

	public InteractionEvent(Kind kind, String structureKind, String handle, String originId, String navigatedRelation,
			String delta, float interestContribution) {
		this.date = Calendar.getInstance().getTime();
		this.endDate = Calendar.getInstance().getTime();
		this.kind = kind;
		this.structureKind = structureKind;
		this.structureHandle = handle;
		this.originId = originId;
		this.navigation = navigatedRelation;
		this.delta = delta;
		this.interestContribution = interestContribution;
	}

	public InteractionEvent(Kind kind, String structureKind, String handle, String originId, String navigatedRelation,
			String delta, float interestContribution, Date startDate, Date endDate) {
		this.date = startDate;
		this.endDate = endDate;
		this.kind = kind;
		this.structureKind = structureKind;
		this.structureHandle = handle;
		this.originId = originId;
		this.navigation = navigatedRelation;
		this.delta = delta;
		this.interestContribution = interestContribution;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof InteractionEvent))
			return false;
		InteractionEvent event = (InteractionEvent) object;
		return (date == null ? event.date == null : date.equals(event.date))
				&& (endDate == null ? event.endDate == null : endDate.equals(event.endDate))
				&& (kind == null ? event.kind == null : kind.equals(event.kind))
				&& (structureKind == null ? event.structureKind == null : structureKind.equals(event.structureKind))
				&& (structureHandle == null ? event.structureHandle == null : structureHandle
						.equals(event.structureHandle))
				&& (originId == null ? event.originId == null : originId.equals(event.originId))
				&& (navigation == null ? event.navigation == null : navigation.equals(event.navigation))
				&& (delta == null ? event.delta == null : delta.equals(event.delta))
				&& interestContribution == event.interestContribution;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		if (date != null)
			hashCode += date.hashCode();
		if (endDate != null)
			hashCode += endDate.hashCode();
		if (kind != null)
			hashCode += kind.hashCode();
		if (structureKind != null)
			hashCode += structureKind.hashCode();
		if (structureHandle != null)
			hashCode += structureHandle.hashCode();
		if (originId != null)
			hashCode += originId.hashCode();
		if (navigation != null)
			hashCode += navigation.hashCode();
		if (delta != null)
			hashCode += delta.hashCode();
		// TODO: could this lose precision?
		hashCode += new Float(interestContribution).hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		return "(date: " + date + ", kind: " + kind + ", sourceHandle: " + structureHandle + ", origin: " + originId
				+ ", delta: " + delta + ")";
	}

	public boolean isValidStructureHandle() {
		return structureHandle != null && !structureHandle.equals("null") && !structureHandle.trim().equals(ID_UNKNOWN);
	}

	public String getStructureHandle() {
		return structureHandle;
	}

	public String getStructureKind() {
		return structureKind;
	}

	public Date getDate() {
		return date;
	}

	public String getDelta() {
		return delta;
	}

	public Kind getKind() {
		return kind;
	}

	public String getOriginId() {
		return originId;
	}

	public float getInterestContribution() {
		return interestContribution;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getNavigation() {
		return navigation;
	}
}
