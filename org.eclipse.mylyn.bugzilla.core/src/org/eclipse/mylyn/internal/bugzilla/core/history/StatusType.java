/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Anvik - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core.history;

import java.text.MessageFormat;

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
		if (change.equals("RESOLVED")) { //$NON-NLS-1$
			return RESOLVED;
		}
		if (change.equals("ASSIGNED")) { //$NON-NLS-1$
			return ASSIGNED;
		}
		if (change.equals("NEW")) { //$NON-NLS-1$
			return NEW;
		}
		if (change.equals("REOPENED")) { //$NON-NLS-1$
			return REOPENED;
		}
		if (change.equals("CLOSED")) { //$NON-NLS-1$
			return CLOSED;
		}
		if (change.equals("VERIFIED")) { //$NON-NLS-1$
			return VERIFIED;
		}
		if (change.equals("UNCONFIRMED")) { //$NON-NLS-1$
			return UNCONFIRMED;
		}
		if (change.startsWith("NEEDINFO")) { //$NON-NLS-1$
			return NEEDINFO;
		}
		if (change.equals("WAITING")) { //$NON-NLS-1$
			return WAITING;
		}
		if (change.equals("SUSPENDED")) { //$NON-NLS-1$
			return SUSPENDED;
		}
		if (change.equals("MODIFIED")) { //$NON-NLS-1$
			return MODIFIED;
		}
		if (change.equals("POST")) { //$NON-NLS-1$
			return POST;
		}
		if (change.equals("INVESTIGATE")) { //$NON-NLS-1$
			return INVESTIGATE;
		}
		if (change.equals("PASSES_QA")) { //$NON-NLS-1$
			return PASSES_QA;
		}
		if (change.equals("PROD_READY")) { //$NON-NLS-1$
			return PROD_READY;
		}
		if (change.equals("RELEASE_PENDING")) { //$NON-NLS-1$
			return RELEASE_PENDING;
		}
		if (change.equals("ON_QA")) { //$NON-NLS-1$
			return ON_QA;
		}
		if (change.equals("QA_READY")) { //$NON-NLS-1$
			return QA_READY;
		}
		if (change.equals("FAILS_QA")) { //$NON-NLS-1$
			return FAILS_QA;
		}
		if (change.equals("SPEC")) { //$NON-NLS-1$
			return SPEC;
		}
		if (change.equals("") == false) { //$NON-NLS-1$
			System.err.println(MessageFormat.format(Messages.StatusType_Unknown_status_type_X, change));
		}
		return UNKNOWN;
	}
}
