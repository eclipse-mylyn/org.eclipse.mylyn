/*******************************************************************************
 * Copyright (c) 2015 Vaughan Hilts and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Vaughan Hilts - Initial implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.reviews.internal.core.BuildResult;
import org.eclipse.mylyn.reviews.internal.core.BuildResult.BuildStatus;

public class HudsonCommentParser {
	private static final Pattern URL_PATTERN = Pattern
			.compile("([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]*[a-zA-Z0-9%_~!$&?#'(*+;:@/=-])"); //$NON-NLS-1$

	private static final Pattern PatcherSetNumber_Pattern = Pattern.compile("(\\d+)"); //$NON-NLS-1$

	public List<BuildResult> getBuildResult(String commentDescription) {
		int patchSetNumber = extractPatchSetNumberFromText(commentDescription);
		if (patchSetNumber == -1) {
			return List.of();
		}
		String[] lines = commentDescription.split("[\\r\\n]+"); //$NON-NLS-1$
		List<BuildResult> results = new ArrayList<>();

		for (String line : lines) {
			BuildResult result = createBuildResultFromLine(patchSetNumber, line);
			if (result != null) {
				results.add(result);
			}
		}
		return List.copyOf(results);
	}

	private BuildResult createBuildResultFromLine(int patchSetNumber, String line) {
		if (line.trim().startsWith(">")) { //$NON-NLS-1$
			return null;
		}
		String url = extractFirstUrlFromText(line);
		if (url == null) {
			return null;
		}
		int buildNumber = extractBuildNumberFromUrl(url);
		if (buildNumber == -1) {
			return null;
		}
		// Strips out unique data, thus making the job name discrete
		String jobName = url.substring(0, url.lastIndexOf(Integer.toString(buildNumber)) - 1);
		BuildStatus buildStatus = extractBuildStatusFromText(line);
		if (buildStatus == null) {
			return null;
		}
		return new BuildResult(buildNumber, url, buildStatus, patchSetNumber, jobName);
	}

	private BuildStatus extractBuildStatusFromText(String descriptionLastLine) {
		// This is a special case, so in this case we return the value directly
		if (descriptionLastLine.startsWith("Build Started")) { //$NON-NLS-1$
			return BuildStatus.STARTED;
		}
		String[] urlFragments = descriptionLastLine.split(" :"); //$NON-NLS-1$
		if (urlFragments.length > 1) {
			try {
				return BuildStatus.valueOf(urlFragments[1].trim());
			} catch (IllegalArgumentException exception) {
				return null;
			}
		} else {
			return null;
		}
	}

	private int extractBuildNumberFromUrl(String url) {
		String[] segments = url.split("/"); //$NON-NLS-1$
		String lastSegment = segments[segments.length - 1];
		try {
			return Integer.parseInt(lastSegment);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private String extractFirstUrlFromText(String text) {
		Matcher matcher = URL_PATTERN.matcher(text);
		if (matcher.find()) {
			return matcher.group(0);
		} else {
			return null;
		}
	}

	private int extractPatchSetNumberFromText(String text) {
		String firstLineToSearch = text.split("[\\r\\n]+")[0]; //$NON-NLS-1$

		Matcher matcher = PatcherSetNumber_Pattern.matcher(firstLineToSearch);
		if (matcher.find()) {
			try {

				return Integer.parseInt(matcher.group(0));
			} catch (NumberFormatException e) {
				return -1;
			}
		} else {
			return -1;
		}
	}
}
