/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Francois Chouinard - Initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritPerson;

import com.google.gson.annotations.SerializedName;

/**
 * The optional labels part of the ChangeInfo structure (filled by GSON).
 * 
 * @author Francois Chouinard
 */
public class GerritReviewLabel {

	/*
	 * When specifying the "LABELS" option on the gerrit changes query, the result
	 * has the following form:
	 * 
	 * {
	 *    "project": "demo",
	 *    "branch": "master",
	 *    ...
	 *    "owner": {
	 *      "name": "John Doe"
	 *    },
	 *    "labels": {
	 *      "Verified": { ... },
	 *      "Code-Review": { ... }
	 *    }
	 * }
	 * 
	 * The "labels" section has the following semi-formal structure:
	 * 
	 * LABELS      = "labels"      + ":" + "{" + VERIFIED + "," + CODE_REVIEW + "}".
	 * VERIFIED    = "Verified"    + ":" + "{" + [ STATUS ] + "}".
	 * CODE_REVIEW = "Code-Review" + ":" + "{" + [ STATUS ] + "}".
	 * STATUS      = APPROVED | RECOMMENDED | DISLIKED | REJECTED.
	 * 
	 * APPROVED    = "approved"    + ":" + "{" + REVIEWER + "}".
	 * RECOMMENDED = "recommended" + ":" + "{" + REVIEWER + "}".
	 * DISLIKED    = "dislikes"    + ":" + "{" + REVIEWER + "}".
	 * REJECTED    = "rejected"    + ":" + "{" + REVIEWER + "}".
	 * 
	 * REVIEWER    = "name" + ":" + STRING.
	 */

	@SerializedName("Verified")
	private GerritReviewStatus verified;

	@SerializedName("Code-Review")
	private GerritReviewStatus code_review;

	public GerritReviewStatus getVerifyStatus() {
		return verified;
	}

	public GerritReviewStatus getCodeReviewStatus() {
		return code_review;
	}

	public class GerritReviewStatus {

		private GerritPerson approved; // +2

		private GerritPerson recommended; // +1

		private GerritPerson disliked; // -1

		private GerritPerson rejected; // -2

		public GerritPerson getReviewer() {
			return approved;
		}

		public boolean isApproved() {
			return approved != null;
		}

		public boolean isRecommended() {
			return recommended != null;
		}

		public boolean isRejected() {
			return rejected != null;
		}

		public boolean isDisliked() {
			return disliked != null;
		}

		public String getStatus() {
			if (isApproved()) {
				return "2"; //$NON-NLS-1$
			}
			if (isRecommended()) {
				return "1"; //$NON-NLS-1$
			}
			if (isDisliked()) {
				return "-1"; //$NON-NLS-1$
			}
			if (isRejected()) {
				return "-2"; //$NON-NLS-1$
			}
			return "0"; //$NON-NLS-1$
		}
	}

}
