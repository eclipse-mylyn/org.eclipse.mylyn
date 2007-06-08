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
package org.eclipse.mylyn.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.mylyn.internal.tasks.ui.editors.IRepositoryTaskSelection;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;

/**
 * A node for the tree in the <code>RepositoryTaskOutlinePage</code>.
 * 
 * @author Mik Kersten (hardening of prototype)
 */
public class RepositoryTaskOutlineNode implements IRepositoryTaskSelection {

	public static final String LABEL_DESCRIPTION = "Description";

	public static final String LABEL_COMMENTS = "Comments";

	public static final String LABEL_NEW_COMMENT = "New Comment";

	/** The taskId of the Bugzilla object that the selection was on. */
	protected String id;

	/** The server of the Bugzilla object that the selection was on. */
	protected String server;

	/** The label for this piece of data. */
	private String key;

	/** The children of this node. */
	private ArrayList<RepositoryTaskOutlineNode> nodeChildren;

	/** The parent of this node or null if it is the bug report */
	private RepositoryTaskOutlineNode parent;

	private Object data = null;

	private String bugSummary;

	private boolean fromEditor = false;

	private boolean isCommentHeader = false;

	private boolean isDescription = false;

	/**
	 * Creates a new <code>RepositoryTaskOutlineNode</code>.
	 * 
	 * @param taskId
	 *            The taskId of the bug this outline is for.
	 * @param server
	 *            The server of the bug this outline is for.
	 * @param key
	 *            The label for this node.
	 * @param image
	 *            The image that will be displayed by this node in the tree.
	 * @param data
	 *            The data, if necessary, this node represents.
	 * @param parent
	 *            The parent of this node
	 */
	public RepositoryTaskOutlineNode(String id, String server, String key, Object data, String summary) {
		this.id = id;
		this.server = server;
		this.key = key;
		this.nodeChildren = null;
		this.data = data;
		this.parent = null;
		this.bugSummary = summary;
	}

	public boolean isFromEditor() {
		return fromEditor;
	}

	/**
	 * @return The children of this node, represented as an <code>Object</code>
	 *         array.
	 */
	public RepositoryTaskOutlineNode[] getChildren() {
		return (nodeChildren == null) ? new RepositoryTaskOutlineNode[0] : nodeChildren
				.toArray(new RepositoryTaskOutlineNode[nodeChildren.size()]);
	}

	/**
	 * Adds a node to this node's list of children.
	 * 
	 * @param bugNode
	 *            The new child.
	 */
	public void addChild(RepositoryTaskOutlineNode bugNode) {
		if (nodeChildren == null) {
			nodeChildren = new ArrayList<RepositoryTaskOutlineNode>();
		}
		bugNode.setParent(this);
		nodeChildren.add(bugNode);
	}

	/**
	 * @return The label of this node.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return <code>true</code> if the given object is another node
	 *         representing the same piece of data in the editor.
	 */
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof RepositoryTaskOutlineNode) {
			RepositoryTaskOutlineNode bugNode = (RepositoryTaskOutlineNode) arg0;
			return getKey().equals(bugNode.getKey());
		}
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	/**
	 * @return The name of this node.
	 */
	public String getName() {
		return getKey();
	}

	/**
	 * @return The data (where applicable) this node represents.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Sets the data that this node represents.
	 * 
	 * @param data
	 *            The new piece of data.
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * Parses the given <code>IBugzillaBug</code> into a tree of
	 * <code>RepositoryTaskOutlineNode</code>'s suitable for use in the
	 * <code>RepositoryTaskOutlinePage</code> view.
	 * 
	 * @param bug
	 *            The bug that needs parsing.
	 * @return The tree of <code>RepositoryTaskOutlineNode</code>'s.
	 */
	public static RepositoryTaskOutlineNode parseBugReport(RepositoryTaskData bug, boolean hasNewComment) {
		// Choose the appropriate parsing function based on
		// the type of IBugzillaBug.
		// if (bug instanceof NewBugzillaReport) {
		// return parseNewBugReport((NewBugzillaReport) bug);
		// } else
		if (bug instanceof RepositoryTaskData) {
			return parseExistingBugReport(bug, hasNewComment);
		} else {
			return null;
		}
	}

	public static RepositoryTaskOutlineNode parseBugReport(RepositoryTaskData bug) {
		return parseBugReport(bug, true);
	}

//	/**
//	 * Parses the given <code>NewBugModel</code> into a tree of
//	 * <code>RepositoryTaskOutlineNode</code>'s suitable for use in the
//	 * <code>RepositoryTaskOutlinePage</code> view.
//	 * 
//	 * @param bug
//	 *            The <code>NewBugModel</code> that needs parsing.
//	 * @return The tree of <code>RepositoryTaskOutlineNode</code>'s.
//	 */
//	protected static RepositoryTaskOutlineNode parseNewBugReport(NewBugzillaReport bug) {
//		int bugId = bug.getId();
//		String bugServer = bug.getRepositoryUrl();
//		Image bugImage = BugzillaImages.getImage(BugzillaImages.BUG);
//		Image defaultImage = BugzillaImages.getImage(BugzillaImages.BUG_COMMENT);
//		RepositoryTaskOutlineNode topNode = new RepositoryTaskOutlineNode(bugId, bugServer, bug.getLabel(), bugImage, bug, bug
//				.getSummary());
//
//		topNode.addChild(new RepositoryTaskOutlineNode(bugId, bugServer, "New Description", defaultImage, null, bug
//				.getSummary()));
//
//		RepositoryTaskOutlineNode titleNode = new RepositoryTaskOutlineNode(bugId, bugServer, "NewBugModel Object", defaultImage,
//				null, bug.getSummary());
//		titleNode.addChild(topNode);
//
//		return titleNode;
//	}

	/**
	 * Parses the given <code>BugReport</code> into a tree of
	 * <code>RepositoryTaskOutlineNode</code>'s suitable for use in the
	 * <code>RepositoryTaskOutlinePage</code> view.
	 * 
	 * @param bug
	 *            The <code>BugReport</code> that needs parsing.
	 * @return The tree of <code>RepositoryTaskOutlineNode</code>'s.
	 */
	protected static RepositoryTaskOutlineNode parseExistingBugReport(RepositoryTaskData bug, boolean hasNewComment) {

		String bugId = bug.getId();
		String bugServer = bug.getRepositoryUrl();
		RepositoryTaskOutlineNode topNode = new RepositoryTaskOutlineNode(bugId, bugServer, bug.getLabel(), bug, bug
				.getSummary());

		RepositoryTaskOutlineNode desc = new RepositoryTaskOutlineNode(bugId, bugServer, LABEL_DESCRIPTION, bug
				.getDescription(), bug.getSummary());
		desc.setIsDescription(true);

		topNode.addChild(desc);

		RepositoryTaskOutlineNode comments = null;
		for (Iterator<TaskComment> iter = bug.getComments().iterator(); iter.hasNext();) {
			TaskComment taskComment = iter.next();
			// first comment is the bug summary
			if(taskComment.getNumber() == 0) continue;
			if (comments == null) {
				comments = new RepositoryTaskOutlineNode(bugId, bugServer, LABEL_COMMENTS, taskComment, bug
						.getSummary());
				comments.setIsCommentHeader(true);
			}
			comments.addChild(new RepositoryTaskOutlineNode(bugId, bugServer, taskComment.getCreated(),
					taskComment, bug.getSummary()));
		}
		if (comments != null) {
			topNode.addChild(comments);
		}

		if (hasNewComment) {
			topNode
					.addChild(new RepositoryTaskOutlineNode(bugId, bugServer, LABEL_NEW_COMMENT, null, bug.getSummary()));
		}
		
		RepositoryTaskOutlineNode titleNode = new RepositoryTaskOutlineNode(bugId, bugServer, "BugReport Object",
				null, bug.getSummary());
		titleNode.addChild(topNode);

		return titleNode;
	}

	public boolean hasComment() {
		// If the comment category was selected, then the comment object is
		// not the intended selection (it is just used to help find the correct
		// location in the editor).
		return (data instanceof TaskComment) && !(key.toLowerCase(Locale.ENGLISH).equals("comments"));
	}

	public TaskComment getComment() {
		return (hasComment()) ? (TaskComment) data : null;
	}

	public void setComment(TaskComment taskComment) {
		data = taskComment;
	}

	public String getContents() {
		return key;
	}

	public void setContents(String contents) {
		key = contents;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRepositoryUrl() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public boolean isEmpty() {
		return (server == null) || ((getContents() == null) && (getComment() == null));
	}

	public RepositoryTaskOutlineNode getParent() {
		return parent;
	}

	public void setParent(RepositoryTaskOutlineNode parent) {
		this.parent = parent;
	}

	public boolean isCommentHeader() {
		return isCommentHeader;
	}

	public boolean isDescription() {
		return isDescription;
	}

	public void setIsCommentHeader(boolean isCommentHeader) {
		this.isCommentHeader = isCommentHeader;
	}

	public void setIsDescription(boolean isDescription) {
		this.isDescription = isDescription;
	}

	public String getBugSummary() {
		return bugSummary;
	}
}
