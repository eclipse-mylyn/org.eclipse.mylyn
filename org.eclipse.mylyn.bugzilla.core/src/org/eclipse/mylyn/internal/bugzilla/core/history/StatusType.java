/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core.history;

/**
 * @author John Anvik
 */
public enum StatusType {
	VERIFIED, RESOLVED, CLOSED, REOPENED, ASSIGNED, NEW, UNCONFIRMED, NEEDINFO, /* ANT? */
	WAITING, /* GCC */
	SUSPENDED, /* GCC */
	MODIFIED, /* Redhat */
	POST, /* Redhat */
	INVESTIGATE, /* Redhat */
	PASSES_QA, /* Redhat */
	PROD_READY, /* Redhat */
	RELEASE_PENDING, /* Redhat */
	ON_QA, /* Redhat */
	QA_READY, /* Redhat */
	FAILS_QA, /* Redhat */
	SPEC, /* Redhat */
	UNKNOWN;

	public static StatusType convert(String change) {
		if (change.equals("RESOLVED")) {
			return RESOLVED;
		}
		if (change.equals("ASSIGNED")) {
			return ASSIGNED;
		}
		if (change.equals("NEW")) {
			return NEW;
		}
		if (change.equals("REOPENED")) {
			return REOPENED;
		}
		if (change.equals("CLOSED")) {
			return CLOSED;
		}
		if (change.equals("VERIFIED")) {
			return VERIFIED;
		}
		if (change.equals("UNCONFIRMED")) {
			return UNCONFIRMED;
		}
		if (change.startsWith("NEEDINFO")) {
			return NEEDINFO;
		}
		if (change.equals("WAITING")) {
			return WAITING;
		}
		if (change.equals("SUSPENDED")) {
			return SUSPENDED;
		}
		if (change.equals("MODIFIED")) {
			return MODIFIED;
		}
		if (change.equals("POST")) {
			return POST;
		}
		if (change.equals("INVESTIGATE")) {
			return INVESTIGATE;
		}
		if (change.equals("PASSES_QA")) {
			return PASSES_QA;
		}
		if (change.equals("PROD_READY")) {
			return PROD_READY;
		}
		if (change.equals("RELEASE_PENDING")) {
			return RELEASE_PENDING;
		}
		if (change.equals("ON_QA")) {
			return ON_QA;
		}
		if (change.equals("QA_READY")) {
			return QA_READY;
		}
		if (change.equals("FAILS_QA")) {
			return FAILS_QA;
		}
		if (change.equals("SPEC")) {
			return SPEC;
		}
		if (change.equals("") == false) {
			System.err.println("Unknown status type: " + change);
		}
		return UNKNOWN;
	}
}