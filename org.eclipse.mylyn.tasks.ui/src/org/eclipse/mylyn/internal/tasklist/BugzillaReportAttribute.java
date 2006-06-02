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

package org.eclipse.mylar.internal.tasklist;


public class BugzillaReportAttribute extends AbstractRepositoryReportAttribute {

	private static final long serialVersionUID = 6959987055086133307L;

	public BugzillaReportAttribute(BugzillaReportElement element) {
		super(element.toString(), element.isHidden());
		super.setID(element.getKeyString());
		super.setReadOnly(element.isReadOnly());
	}
	
}
