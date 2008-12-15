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
public enum ResolutionType {

	FIXED, DUPLICATE, WONTFIX, INVALID, WORKSFORME, REOPENED, LATER, REMIND, MOVED, UNKNOWN, NOTABUG, NOTGNOME, INCOMPLETE, OBSOLETE, EXPIRED, NOTXIMIAN, NEXTRELEASE, ERRATA, RAWHIDE, UPSTREAM, CANTFIX, CURRENTRELEASE, INSUFFICIENT_DATA, DEFERRED;

	public static ResolutionType convert(String change) {
		if (change.equals("FIXED")) { //$NON-NLS-1$
			return ResolutionType.FIXED;
		}
		if (change.contains("DUPLICATE")) { //$NON-NLS-1$
			return ResolutionType.DUPLICATE;
		}
		if (change.equals("INVALID")) { //$NON-NLS-1$
			return ResolutionType.INVALID;
		}
		if (change.equals("LATER")) { //$NON-NLS-1$
			return ResolutionType.LATER;
		}
		if (change.equals("WORKSFORME")) { //$NON-NLS-1$
			return ResolutionType.WORKSFORME;
		}
		if (change.equals("REOPENED")) { //$NON-NLS-1$
			return ResolutionType.REOPENED;
		}
		if (change.equals("WONTFIX")) { //$NON-NLS-1$
			return ResolutionType.WONTFIX;
		}
		if (change.equals("REMIND")) { //$NON-NLS-1$
			return ResolutionType.REMIND;
		}
		if (change.equals("MOVED")) { //$NON-NLS-1$
			return ResolutionType.MOVED;
		}
		if (change.equals("EXPIRED")) { //$NON-NLS-1$
			return ResolutionType.EXPIRED;
		}
		if (change.equals("NOTABUG")) { // Gnome //$NON-NLS-1$
			return ResolutionType.NOTABUG;
		}
		if (change.equals("NOTGNOME")) { // Gnome //$NON-NLS-1$
			return ResolutionType.NOTGNOME;
		}
		if (change.equals("INCOMPLETE")) { // Gnome //$NON-NLS-1$
			return ResolutionType.INCOMPLETE;
		}
		if (change.equals("OBSOLETE")) { // Gnome //$NON-NLS-1$
			return ResolutionType.OBSOLETE;
		}
		if (change.equals("NOTXIMIAN")) { // Gnome //$NON-NLS-1$
			return ResolutionType.NOTXIMIAN;
		}
		if (change.equals("NEXTRELEASE")) { // Redhat //$NON-NLS-1$
			return ResolutionType.NEXTRELEASE;
		}
		if (change.equals("ERRATA")) {// Redhat //$NON-NLS-1$
			return ResolutionType.ERRATA;
		}
		if (change.equals("RAWHIDE")) {// Redhat //$NON-NLS-1$
			return ResolutionType.RAWHIDE;
		}
		if (change.equals("UPSTREAM")) {// Redhat //$NON-NLS-1$
			return ResolutionType.UPSTREAM;
		}
		if (change.equals("CANTFIX")) {// Redhat //$NON-NLS-1$
			return ResolutionType.CANTFIX;
		}
		if (change.equals("CURRENTRELEASE")) {// Redhat //$NON-NLS-1$
			return ResolutionType.CURRENTRELEASE;
		}
		if (change.equals("INSUFFICIENT_DATA")) {// Redhat //$NON-NLS-1$
			return ResolutionType.INSUFFICIENT_DATA;
		}
		if (change.equals("DEFERRED")) {// Redhat //$NON-NLS-1$
			return ResolutionType.DEFERRED;
		}
		if (change.equals("") == false) { //$NON-NLS-1$
			System.err.println(MessageFormat.format(Messages.ResolutionType_Unknown_resolution_type_X, change));
		}
		return ResolutionType.UNKNOWN;
	}
}
