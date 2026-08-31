/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.mylyn.internal.github.egit.github.core.RepositoryCommit;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * Pull request commit adapter
 */
public class PullRequestCommitAdapter extends WorkbenchAdapter {

	private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

	private final RepositoryCommit commit;

	/**
	 * @param commit
	 */
	public PullRequestCommitAdapter(RepositoryCommit commit) {
		this.commit = commit;
	}

	/**
	 * Get commit
	 *
	 * @return commit
	 */
	public RepositoryCommit getCommit() {
		return commit;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return UIIcons.CHANGESET;
	}

	@Override
	public String getLabel(Object object) {
		return commit.getSha().substring(0, 8);
	}

	@Override
	public StyledString getStyledText(Object object) {
		StyledString styled = new StyledString(getLabel(object));
		try {
			String desc = null;
			if (commit.getCommit() != null) {
				desc = commit.getCommit().getCommitShortInfo().getMessage();
			}
			if (desc != null) {
				int delim = desc.indexOf('\n');
				if (delim == -1) {
					delim = 80;
				}
				if (delim < desc.length()) {
					desc = desc.substring(0, delim);
				}
				styled.append(": ").append(desc); //$NON-NLS-1$
			}
			styled.append(' ');
			String name = commit.getAuthor().getName();
			String authorWithDate;
			authorWithDate = MessageFormat.format(
					Messages.PullRequestCommitAdapter_AuthorWithDate, name,
					DATE_FORMAT.format(Date.from(commit.getAuthor().getDate())));
			styled.append(authorWithDate, StyledString.QUALIFIER_STYLER);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return styled;
	}

}
