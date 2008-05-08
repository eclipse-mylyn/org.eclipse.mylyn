/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * A node for the tree used to compare bugs in the compare viewer.
 */
public class BugzillaCompareNode implements IStreamContentAccessor, IStructureComparator, ITypedElement {

	/** The label for this piece of data. */
	private String key;

	/** The data for this node. */
	private String value;

	/** The children of this node. */
	private ArrayList<BugzillaCompareNode> nodeChildren;

	/** This node's image. */
	private Image image;

	/**
	 * Constructor. The image for this node is set to <code>null</code>.
	 * 
	 * @param key
	 * 		The label for this node.
	 * @param value
	 * 		The data for this node.
	 */
	public BugzillaCompareNode(String key, String value) {
		this(key, value, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param key
	 * 		The label for this node.
	 * @param value
	 * 		The data for this node.
	 * @param image
	 * 		The image for this node.
	 */
	public BugzillaCompareNode(String key, String value, Image image) {
		super();
		this.key = key;
		this.value = checkText(value);
		this.nodeChildren = null;
		this.image = image;
	}

	/**
	 * This function checks to make sure the given string is not <code>null</code>. If it is, the empty string is
	 * returned instead.
	 * 
	 * @param newValue
	 * 		The string to be checked.
	 * @return If the text is <code>null</code>, then return the null string (<code>""</code>). Otherwise, return the
	 * 	text.
	 */
	private String checkText(String newValue) {
		return ((newValue == null) ? "" : newValue);
	}

	public Object[] getChildren() {
		return (nodeChildren == null) ? new Object[0] : nodeChildren.toArray();
	}

	/**
	 * Adds a node to this node's list of children.
	 * 
	 * @param bugNode
	 * 		The new child.
	 */
	public void addChild(BugzillaCompareNode bugNode) {
		if (nodeChildren == null) {
			nodeChildren = new ArrayList<BugzillaCompareNode>();
		}
		nodeChildren.add(bugNode);
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(getValue().getBytes());
	}

	/**
	 * @return The label for this node.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set the label for this node.
	 * 
	 * @param key
	 * 		The new label.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return The data for this node.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the data for this node.
	 * 
	 * @param value
	 * 		The new data.
	 */
	public void setValue(String value) {
		this.value = checkText(value);
	}

	public Image getImage() {
		return image;
	}

	/**
	 * Sets the image for this object. This image is used when displaying this object in the UI.
	 * 
	 * @param newImage
	 * 		The new image.
	 */
	public void setImage(Image newImage) {
		this.image = newImage;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof BugzillaCompareNode) {
			BugzillaCompareNode bugNode = (BugzillaCompareNode) arg0;
			return getKey().equals(bugNode.getKey());
		}
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	public String getName() {
		return getKey();
	}

	public String getType() {
		return "bug report";
	}

	/**
	 * Parses the given <code>BugReport</code> into a tree of <code>BugzillaCompareNode</code>'s suitable for use in a
	 * compare viewer.
	 * 
	 * @param bug
	 * 		The <code>BugReport</code> that needs parsing.
	 * @return The tree of <code>BugzillaCompareNode</code>'s.
	 */
	public static BugzillaCompareNode parseBugReport(RepositoryTaskData bug) {
		Image defaultImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW);
		BugzillaCompareNode topNode = new BugzillaCompareNode("Bug #" + bug.getTaskId(), null, defaultImage);

		Image attributeImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		BugzillaCompareNode attributes = new BugzillaCompareNode("Attributes", null, attributeImage);
		for (RepositoryTaskAttribute attribute : bug.getAttributes()) {
			BugzillaCompareNode child = new BugzillaCompareNode(attribute.toString(), attribute.getValue(),
					defaultImage);
			attributes.addChild(child);
		}

		BugzillaCompareNode comments = new BugzillaCompareNode("Comments", null, defaultImage);
		for (TaskComment taskComment : bug.getComments()) {
			String bodyString = "Comment from " + taskComment.getAuthorName() + ":\n\n" + taskComment.getText();
			comments.addChild(new BugzillaCompareNode(
					taskComment.getAttributeValue(BugzillaReportElement.BUG_WHEN.getKeyString()), bodyString,
					defaultImage));
		}
		topNode.addChild(comments);

		topNode.addChild(new BugzillaCompareNode("New Comment", bug.getNewComment(), defaultImage));

		BugzillaCompareNode ccList = new BugzillaCompareNode("CC List", null, defaultImage);
		for (String cc : bug.getCc()) {
			ccList.addChild(new BugzillaCompareNode("CC", cc, defaultImage));
		}
		topNode.addChild(ccList);

		BugzillaCompareNode titleNode = new BugzillaCompareNode("BugReport Object", null, defaultImage);
		titleNode.addChild(topNode);

		return titleNode;
	}

//	public static BugzillaCompareNode parseBugReport(BugzillaReport bug) {
//		Image defaultImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW);
//		BugzillaCompareNode topNode = new BugzillaCompareNode("Bug #" + bug.getId(), null, defaultImage);
//		Date creationDate = bug.getCreated();
//		if (creationDate == null) {
//			// XXX: this could be backwards
//			creationDate = Calendar.getInstance().getTime();
//		}
//		BugzillaCompareNode child = new BugzillaCompareNode("Creation Date", creationDate.toString(), defaultImage);
//		topNode.addChild(child);
//
//		String keywords = "";
//		if (bug.getKeywords() != null) {
//			for (Iterator<String> iter = bug.getKeywords().iterator(); iter.hasNext();) {
//				keywords += iter.next() + " ";
//			}
//		}
//		topNode.addChild(new BugzillaCompareNode("Keywords", keywords, defaultImage));
//
//		Image attributeImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
//		BugzillaCompareNode attributes = new BugzillaCompareNode("Attributes", null, attributeImage);
//		for (Iterator<RepositoryTaskAttribute> iter = bug.getAttributes().iterator(); iter.hasNext();) {
//			RepositoryTaskAttribute attribute = iter.next();
//			if (attribute.getName().compareTo("delta_ts") == 0 || attribute.getName().compareTo("Last Modified") == 0
//					|| attribute.getName().compareTo("longdesclength") == 0)
//				continue;
//			// Since the bug report may not be saved offline, get the
//			// attribute's new
//			// value, which is what is in the submit viewer.
//
//			attributes.addChild(new BugzillaCompareNode(attribute.getName(), attribute.getValue(), attributeImage));
//		}
//		topNode.addChild(attributes);
//
//		topNode.addChild(new BugzillaCompareNode("Description", bug.getDescription(), defaultImage));
//
//		BugzillaCompareNode comments = new BugzillaCompareNode("Comments", null, defaultImage);
//		for (Iterator<Comment> iter = bug.getComments().iterator(); iter.hasNext();) {
//			Comment comment = iter.next();
//			String bodyString = "Comment from " + comment.getAuthorName() + ":\n\n" + comment.getText();
//			comments.addChild(new BugzillaCompareNode(comment.getAttributeValue(BugzillaReportElement.CREATION_TS), bodyString, defaultImage));
//		}
//		topNode.addChild(comments);
//
//		topNode.addChild(new BugzillaCompareNode("New Comment", bug.getNewComment(), defaultImage));
//
//		BugzillaCompareNode ccList = new BugzillaCompareNode("CC List", null, defaultImage);
//		for (Iterator<String> iter = bug.getCC().iterator(); iter.hasNext();) {
//			String cc = iter.next();
//			ccList.addChild(new BugzillaCompareNode("CC", cc, defaultImage));
//		}
//		topNode.addChild(ccList);
//
//		BugzillaCompareNode titleNode = new BugzillaCompareNode("BugReport Object", null, defaultImage);
//		titleNode.addChild(topNode);
//
//		return titleNode;
//	}

}
