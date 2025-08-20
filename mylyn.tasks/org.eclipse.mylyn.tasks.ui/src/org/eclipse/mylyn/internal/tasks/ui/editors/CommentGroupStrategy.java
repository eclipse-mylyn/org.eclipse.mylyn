/*******************************************************************************
 * Copyright (c) 2004, 2015 Jingwen Ou and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Jingwen Ou
 * @author Steffen Pingel
 */
public class CommentGroupStrategy {

	public static class CommentGroup {

		public static final String CURRENT = Messages.CommentGroupStrategy_Current;

		public static final String OLDER = Messages.CommentGroupStrategy_Older;

		public static final String RECENT = Messages.CommentGroupStrategy_Recent;

		private final List<ITaskComment> comments;

		private final String groupName;

		private final boolean incoming;

		public CommentGroup(String groupName, List<ITaskComment> comments, boolean incoming) {
			this.groupName = groupName;
			this.comments = comments;
			this.incoming = incoming;
		}

		public List<TaskAttribute> getCommentAttributes() {
			List<TaskAttribute> commentAttributes = new ArrayList<>(comments.size());
			for (ITaskComment comment : comments) {
				commentAttributes.add(comment.getTaskAttribute());
			}
			return Collections.unmodifiableList(commentAttributes);
		}

		public List<ITaskComment> getComments() {
			return Collections.unmodifiableList(comments);
		}

		public String getGroupName() {
			return groupName;
		}

		public boolean hasIncoming() {
			return incoming;
		}
	}

	// public for testing
	public static final int MAX_CURRENT = 12;

	// public for testing
	public static final int MAX_RECENT = 20;

	/**
	 * Groups comments according to "Older", "Recent" and "Current".
	 *
	 * @param comments
	 *            a list of comments to be grouped
	 * @param currentPersonId
	 *            current login user id
	 * @return a list of groups
	 */
	public List<CommentGroup> groupComments(List<ITaskComment> comments, String currentPersonId) {
		if (comments.size() == 0) {
			return Collections.emptyList();
		}

		List<CommentGroup> commentGroups = new ArrayList<>(3);

		// current
		List<ITaskComment> current = new ArrayList<>(MAX_CURRENT);
		if (comments.size() > MAX_CURRENT) {
			for (int i = comments.size() - 1; i >= 0; i--) {
				ITaskComment comment = comments.get(i);
				if (isCurrent(current, comment, currentPersonId)) {
					current.add(comment);
				}
			}
			Collections.reverse(current);
		} else {
			current = comments;
		}
		commentGroups.add(new CommentGroup(CommentGroup.CURRENT, current, hasIncomingChanges(current)));

		// recent
		if (comments.size() > current.size()) {
			int recentToIndex = comments.size() - current.size();
			int recentFromIndex = Math.max(recentToIndex - MAX_RECENT, 0);
			List<ITaskComment> recent = new ArrayList<>(comments.subList(recentFromIndex, recentToIndex));
			if (recent.size() > 0) {
				commentGroups.add(new CommentGroup(CommentGroup.RECENT, recent, hasIncomingChanges(recent)));

				// the rest goes to older
				if (comments.size() > current.size() + recent.size()) {
					int olderToIndex = comments.size() - current.size() - recent.size();
					List<ITaskComment> older = new ArrayList<>(comments.subList(0, olderToIndex));
					commentGroups.add(new CommentGroup(CommentGroup.OLDER, older, hasIncomingChanges(older)));
				}
			}
		}

		Collections.reverse(commentGroups);
		return commentGroups;
	}

	protected boolean hasIncomingChanges(ITaskComment taskComment) {
		return false;
	}

	private boolean hasIncomingChanges(List<ITaskComment> comments) {
		for (ITaskComment comment : comments) {
			if (hasIncomingChanges(comment)) {
				return true;
			}
		}
		return false;
	}

	// public for testing
	public boolean isCurrent(List<ITaskComment> current, ITaskComment comment, String currentPersonId) {
		if (current.size() >= MAX_CURRENT) {
			return false;
		}

		// add all incoming changes
		if (hasIncomingChanges(comment)) {
			return true;
		}

		if (!current.isEmpty()) {
			// check if last comment was by current user
			ITaskComment lastComment = current.get(current.size() - 1);
			IRepositoryPerson lastPerson = lastComment.getAuthor();
			if (lastPerson != null && lastPerson.matchesUsername(currentPersonId)) {
				// bug 238038 comment #58, if the latest comment is generated automatically, look back one comment
				IRepositoryPerson person = comment.getAuthor();
				if (person != null && person.getPersonId().equals(currentPersonId) && lastComment.getText() != null
						&& lastComment.getText().contains(AttachmentUtil.CONTEXT_DESCRIPTION)) {
					return true;
				}

				return false;
			}
		}

		return true;
	}
}
