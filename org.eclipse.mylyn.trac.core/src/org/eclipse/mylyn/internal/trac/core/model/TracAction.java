/*******************************************************************************
 * Copyright (c) 2009 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Steffen Pingel
 */
public class TracAction {

	private List<TracTicketField> fields;

	private String hint;

	private String id;

	private String label;

	public TracAction(String id) {
		this.id = id;
	}

	public void addField(TracTicketField field) {
		if (fields == null) {
			fields = new ArrayList<TracTicketField>();
		}
		fields.add(field);
	}

	public List<TracTicketField> getFields() {
		if (fields == null) {
			return Collections.emptyList();
		}
		return new ArrayList<TracTicketField>(fields);
	}

	public String getHint() {
		return hint;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void removeField(TracTicketField field) {
		if (fields != null) {
			fields.remove(field);
		}
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
