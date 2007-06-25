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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author John Anvik
 */
public class AttachmentEvent extends TaskRevision {

	private static final long serialVersionUID = 3258693199936631348L;

	private static final Pattern id = Pattern.compile("\\d+");

	private final int attachmentId;

	private List<AttachmentFlag> flags;

	public AttachmentEvent(int id, List<AttachmentFlag> flags) {
		this.what = TaskRevision.ATTACHMENT;
		this.attachmentId = id;
		this.flags = flags;
	}

	public String getFlagsString() {
		String flagString = "";
		for (AttachmentFlag flag : this.flags) {
			flagString += flag + " ";
		}
		return flagString;
	}

	public static int parseId(String attachment) {
		Matcher matcher = AttachmentEvent.id.matcher(attachment);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		}

		// Error situation
		System.err.println("WARNING: Cannot find attachment id in " + attachment);
		return -1;
	}

	public static List<AttachmentFlag> parseFlags(String attachmentFlags) {
		List<AttachmentFlag> flags = new ArrayList<AttachmentFlag>();
		AttachmentFlagStatus flagStatus = AttachmentFlagStatus.UNKNOWN;
		AttachmentFlagState flagState = AttachmentFlagState.UNKNOWN;

		String[] flagToken = attachmentFlags.split(", ");
		for (int i = 0; i < flagToken.length; i++) {
			String token = flagToken[i];
			if (token.indexOf("(") != -1) {
				int end = token.indexOf("(");
				String substr = token.substring(0, end);
				token = substr;
			}

			/* Handle the case of the obsolete status 'needs-work' */
			if (token.startsWith("needs-work")) {
				/*
				 * Since we don't know if 'needs-work' applies to 'review' or
				 * 'superreview', deny both
				 */
				flags.add(new AttachmentFlag(AttachmentFlagStatus.REVIEW, AttachmentFlagState.DENIED));
				flags.add(new AttachmentFlag(AttachmentFlagStatus.SUPERREVIEW, AttachmentFlagState.DENIED));
			} else {
				boolean startsWithReview = token.toLowerCase().startsWith(
						AttachmentFlagStatus.REVIEW.name().toLowerCase());
				boolean firstOrSecondReview = token.toLowerCase().contains(
						AttachmentFlagStatus.REVIEW.name().toLowerCase())
						&& (token.toLowerCase().startsWith("first-") || token.toLowerCase().startsWith("second-"));
				/*
				 * if(firstOrSecondReview){ System.err.println("First/second
				 * activated"); }
				 */
				if (startsWithReview || firstOrSecondReview) {
					flagStatus = AttachmentFlagStatus.REVIEW;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.SUPERREVIEW.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.SUPERREVIEW;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.APPROVAL.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.APPROVAL;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.UI.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.UI;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.BRANCH.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.BRANCH;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.COMMITTED.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.COMMITTED;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.ACCEPTED.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.ACCEPTED;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.COMMENTED.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.COMMENTED;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.NONE.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.NONE;
				} else if (token.toLowerCase().startsWith(AttachmentFlagStatus.REJECTED.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.REJECTED;
				} else if (token.equals("1")
						|| token.toLowerCase().startsWith(AttachmentFlagStatus.OBSOLETE.name().toLowerCase())) {
					flagStatus = AttachmentFlagStatus.OBSOLETE;
				}

				// Assure that flag was set to something meaningful
				if (flagStatus.equals(AttachmentFlagStatus.UNKNOWN) && token.equals("") == false) {
					System.err.println("WARNING: Attachment flag status unknown: " + token);
				}

				if (token.length() > 0) {
					if (token.charAt(token.length() - 1) == '?') {
						flagState = AttachmentFlagState.REQUESTED;
					} else if (token.charAt(token.length() - 1) == '+') {
						flagState = AttachmentFlagState.GRANTED;
					} else if (token.charAt(token.length() - 1) == '-') {
						flagState = AttachmentFlagState.DENIED;
					} else if (flagStatus.equals(AttachmentFlagStatus.OBSOLETE)
							|| flagStatus.equals(AttachmentFlagStatus.COMMITTED)
							|| flagStatus.equals(AttachmentFlagStatus.ACCEPTED)
							|| flagStatus.equals(AttachmentFlagStatus.COMMENTED)
							|| flagStatus.equals(AttachmentFlagStatus.NONE)
							|| flagStatus.equals(AttachmentFlagStatus.REJECTED)
							|| flagStatus.equals(AttachmentFlagStatus.REVIEW)) {
						flagState = AttachmentFlagState.OFF;
					}
				}
				// Assure that flag state was set to something meaningful
				if (flagState.equals(AttachmentFlagState.UNKNOWN) && token.equals("") == false) {
					System.err.println("WARNING: Attachment flag state unknown: " + token);
				}

				flags.add(new AttachmentFlag(flagStatus, flagState));
			}
		}
		return flags;
	}

	@Override
	public String toString() {
		return this.getName() + " | " + this.getDate() + " | " + this.getWhat() + " | " + this.attachmentId + " | "
				+ this.getFlagsString();
	}

	public List<AttachmentFlag> getFlags() {
		return this.flags;
	}

	public int getAttachmentId() {
		return this.attachmentId;
	}

}
