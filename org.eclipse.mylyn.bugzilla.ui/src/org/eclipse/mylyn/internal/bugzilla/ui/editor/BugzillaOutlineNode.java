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
package org.eclipse.mylar.internal.bugzilla.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.internal.bugzilla.ui.IBugzillaReportSelection;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.bugzilla.core.Comment;
import org.eclipse.mylar.provisional.bugzilla.core.IBugzillaBug;
import org.eclipse.swt.graphics.Image;

/**
 * A node for the tree in the <code>BugzillaOutlinePage</code>.
 * 
 * @author Mik Kersten (hardening of prototype)
 */
public class BugzillaOutlineNode implements IBugzillaReportSelection {

	/** The id of the Bugzilla object that the selection was on. */
	protected int id;

	/** The server of the Bugzilla object that the selection was on. */
	protected String server;

	/** The label for this piece of data. */
	private String key;

	/** The children of this node. */
	private ArrayList<BugzillaOutlineNode> nodeChildren;

	/** The parent of this node or null if it is the bug report */
	private BugzillaOutlineNode parent;

	/** This node's image. */
	private Image image;

	private Object data = null;

	private String bugSummary;

	private boolean fromEditor = false;

	private boolean isCommentHeader = false;

	private boolean isDescription = false;

	/**
	 * Creates a new <code>BugzillaOutlineNode</code>.
	 * 
	 * @param id
	 *            The id of the bug this outline is for.
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
	public BugzillaOutlineNode(int id, String server, String key, Image image, Object data, String summary) {
		this.id = id;
		this.server = server;
		this.key = key;
		this.nodeChildren = null;
		this.image = image;
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
	public BugzillaOutlineNode[] getChildren() {
		return (nodeChildren == null) ? new BugzillaOutlineNode[0] : nodeChildren
				.toArray(new BugzillaOutlineNode[nodeChildren.size()]);
	}

	/**
	 * Adds a node to this node's list of children.
	 * 
	 * @param bugNode
	 *            The new child.
	 */
	public void addChild(BugzillaOutlineNode bugNode) {
		if (nodeChildren == null) {
			nodeChildren = new ArrayList<BugzillaOutlineNode>();
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

	// /**
	// * Set the label of this node.
	// * @param key The new label.
	// */
	// public void setKey(String key) {
	// this.key = key;
	// }

	/**
	 * TODO: remove, nodes don't need to know about image decorator
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Sets the decorator image for this node.
	 * 
	 * @param newImage
	 *            The new image.
	 */
	public void setImage(Image newImage) {
		this.image = newImage;
	}

	/**
	 * @return <code>true</code> if the given object is another node
	 *         representing the same piece of data in the editor.
	 */
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof BugzillaOutlineNode) {
			BugzillaOutlineNode bugNode = (BugzillaOutlineNode) arg0;
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
	 * <code>BugzillaOutlineNode</code>'s suitable for use in the
	 * <code>BugzillaOutlinePage</code> view.
	 * 
	 * @param bug
	 *            The bug that needs parsing.
	 * @return The tree of <code>BugzillaOutlineNode</code>'s.
	 */
	public static BugzillaOutlineNode parseBugReport(IBugzillaBug bug) {
		// Choose the appropriate parsing function based on
		// the type of IBugzillaBug.
		if (bug instanceof NewBugzillaReport) {
			return parseBugReport((NewBugzillaReport) bug);
		} else if (bug instanceof BugzillaReport) {
			return parseBugReport((BugzillaReport) bug);
		} else {
			return null;
		}
	}

	/**
	 * Parses the given <code>NewBugModel</code> into a tree of
	 * <code>BugzillaOutlineNode</code>'s suitable for use in the
	 * <code>BugzillaOutlinePage</code> view.
	 * 
	 * @param bug
	 *            The <code>NewBugModel</code> that needs parsing.
	 * @return The tree of <code>BugzillaOutlineNode</code>'s.
	 */
	protected static BugzillaOutlineNode parseBugReport(NewBugzillaReport bug) {
		int bugId = bug.getId();
		String bugServer = bug.getRepositoryUrl();
		Image bugImage = BugzillaImages.getImage(BugzillaImages.BUG);
		Image defaultImage = BugzillaImages.getImage(BugzillaImages.BUG_COMMENT);
		BugzillaOutlineNode topNode = new BugzillaOutlineNode(bugId, bugServer, bug.getLabel(), bugImage, bug, bug
				.getSummary());

		topNode.addChild(new BugzillaOutlineNode(bugId, bugServer, "New Description", defaultImage, null, bug
				.getSummary()));

		BugzillaOutlineNode titleNode = new BugzillaOutlineNode(bugId, bugServer, "NewBugModel Object", defaultImage,
				null, bug.getSummary());
		titleNode.addChild(topNode);

		return titleNode;
	}

	/**
	 * Parses the given <code>BugReport</code> into a tree of
	 * <code>BugzillaOutlineNode</code>'s suitable for use in the
	 * <code>BugzillaOutlinePage</code> view.
	 * 
	 * @param bug
	 *            The <code>BugReport</code> that needs parsing.
	 * @return The tree of <code>BugzillaOutlineNode</code>'s.
	 */
	protected static BugzillaOutlineNode parseBugReport(BugzillaReport bug) {

		int bugId = bug.getId();
		String bugServer = bug.getRepositoryUrl();
		Image bugImage = BugzillaImages.getImage(BugzillaImages.BUG);
		Image defaultImage = BugzillaImages.getImage(BugzillaImages.BUG_COMMENT);
		BugzillaOutlineNode topNode = new BugzillaOutlineNode(bugId, bugServer, bug.getLabel(), bugImage, bug, bug
				.getSummary());

		BugzillaOutlineNode desc = new BugzillaOutlineNode(bugId, bugServer, "Description", defaultImage, bug
				.getDescription(), bug.getSummary());
		desc.setIsDescription(true);

		topNode.addChild(desc);

		BugzillaOutlineNode comments = null;
		for (Iterator<Comment> iter = bug.getComments().iterator(); iter.hasNext();) {
			Comment comment = iter.next();
			// first comment is the bug description
			if(comment.getNumber() == 0) continue;
			if (comments == null) {
				comments = new BugzillaOutlineNode(bugId, bugServer, "Comments", defaultImage, comment, bug
						.getSummary());
				comments.setIsCommentHeader(true);
			}
			comments.addChild(new BugzillaOutlineNode(bugId, bugServer, comment.getCreated().toString(), defaultImage,
					comment, bug.getSummary()));
		}
		if (comments != null) {
			topNode.addChild(comments);
		}

		topNode
				.addChild(new BugzillaOutlineNode(bugId, bugServer, "New Comment", defaultImage, null, bug.getSummary()));

		BugzillaOutlineNode titleNode = new BugzillaOutlineNode(bugId, bugServer, "BugReport Object", defaultImage,
				null, bug.getSummary());
		titleNode.addChild(topNode);

		return titleNode;
	}

	public boolean hasComment() {
		// If the comment category was selected, then the comment object is
		// not the intended selection (it is just used to help find the correct
		// location in the editor).
		return (data instanceof Comment) && !(key.toLowerCase().equals("comments"));
	}

	public Comment getComment() {
		return (hasComment()) ? (Comment) data : null;
	}

	public void setComment(Comment comment) {
		data = comment;
	}

	public String getContents() {
		return key;
	}

	public void setContents(String contents) {
		key = contents;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public boolean isEmpty() {
		return (server == null) || ((getContents() == null) && (getComment() == null));
	}

	public BugzillaOutlineNode getParent() {
		return parent;
	}

	public void setParent(BugzillaOutlineNode parent) {
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
