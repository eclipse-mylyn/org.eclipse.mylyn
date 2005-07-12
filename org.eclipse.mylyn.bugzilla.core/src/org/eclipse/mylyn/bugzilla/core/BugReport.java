/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * A bug report entered in Bugzilla.
 */
public class BugReport implements Serializable, IBugzillaBug {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3258693199936631348L;
	
	/** Bug id */
	protected final int id;

	/** The bug's server */
	protected final String server;

	/** Description of the bug */
	protected String description;

	/** Creation timestamp */
	protected Date created;

	/** The bugs valid keywords */
	protected List<String> validKeywords;

	/** The operations that can be done on the bug*/
	protected List<Operation> operations = new ArrayList<Operation>();

	/** Bug attributes (status, resolution, etc.) */
	protected HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();

	/** The keys for the bug attributes */
	protected ArrayList<String> attributeKeys = new ArrayList<String>();

	/** A list of comments */
	protected ArrayList<Comment> comments = new ArrayList<Comment>();

	/** The value for the new comment to add (text that is saved)*/
	protected String newComment = "";
	
	/** The new value for the new comment to add (text from submit editor)*/
	protected String newNewComment = "";

	/** CC list */
	protected HashSet<String> cc = new HashSet<String>();

	/** The operation that was selected to do to the bug */
	protected Operation selectedOperation = null;

	/** Whether or not this bug report is saved offline. */
	protected boolean savedOffline = false;

	/**
	 * Constructor
	 * @param id The id of the bug
	 * @param server The server that this bug is being created for
	 */
	public BugReport(int id, String server) {
		this.id = id;
		this.server = server;
	}

	/**
	 * Get the bugs id
	 * @return The bugs id
	 */
	public int getId() {
		return id;
	}

	public String getServer() {
		return server;
	}
	
	public String getLabel() {
		return "Bug #" + id;
	}

	/**
	 * Get the bugs description
	 * @return The description of the bug
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the bug 
	 * @param description The description to set the bug to have
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the summary for the bug
	 * @return The bugs summary
	 */
	public String getSummary() {
        if(getAttribute("Summary") == null){
        	BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, "WE SHOULD NEVER GET HERE " + id, null));
        	return "";
        }
		return getAttribute("Summary").getValue();
	}

	/**
	 * Set the summary of the bug 
	 * @param summary The summary to set the bug to have
	 */
	public void setSummary(String summary) {
		if( this.getAttribute("Summary") == null ){
			Attribute a = new Attribute("Summary");
			a.setValue(summary);
			addAttribute(a);	
		}	
		else
			getAttribute("Summary").setValue(summary);
	}

	/**
	 * Get the date that the bug was created
	 * @return The bugs creation date
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * Set the bugs creation date
	 * @param created The date the the bug was created
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	public Attribute getAttribute(String key) {
		return attributes.get(key);
	}

	/**
	 * Get the list of attributes for this bug
	 * @return An <code>ArrayList</code> of the bugs attributes
	 */
	public List<Attribute> getAttributes() {
		// create an array list to store the attributes in
		ArrayList<Attribute> attributeEntries = new ArrayList<Attribute>(attributeKeys.size());

		// go through each of the attribute keys
		for (Iterator<String> it = attributeKeys.iterator(); it.hasNext();) {
			// get the key for the attribute
			String key = it.next();

			// get the attribute and add it to the list
			Attribute attribute = attributes.get(key);
			attributeEntries.add(attribute);
		}

		// return the list of attributes for the bug
		return attributeEntries;
	}

	/**
	 * Add an attribute to the bug
	 * @param attribute The attribute to add to the bug
	 */
	public void addAttribute(Attribute attribute) {
		if (!attributes.containsKey(attribute.getName())) {
			// add the attributes key to the list if it doesn't exist
			attributeKeys.add(attribute.getName());
		}

		// put the value of the attribute into the map, using its name as the key
		attributes.put(attribute.getName(), attribute);
	}

	/**
	 * Get the comments posted on the bug
	 * @return A list of comments for the bug
	 */
	public ArrayList<Comment> getComments() {
		return comments;
	}

	/**
	 * Add a comment to the bug
	 * @param comment The comment to add to the bug
	 */
	public void addComment(Comment comment) {
		Comment preceding = null;
		if (comments.size() > 0) {
			// if there are some comments, get the last comment and set the next
			// value to be the new comment
			preceding = comments.get(comments.size() - 1);
			preceding.setNext(comment);
		}
		// set the comments previous value to the preceeding one
		comment.setPrevious(preceding);

		// add the comment to the comment list
		comments.add(comment);
	}

	/**
	 * Get the person who reported the bug
	 * @return The person who reported the bug
	 */
	public String getReporter() {
		return getAttribute("Reporter").getValue();
	}

	/**
	 * Get the person to whom this bug is assigned
	 * @return The person who is assigned to this bug
	 */
	public String getAssignedTo() {
		return getAttribute("Assigned To").getValue();
	}

	/**
	 * Get the resolution of the bug
	 * @return The resolution of the bug
	 */
	public String getResolution() {
		return getAttribute("Resolution").getValue();
	}

	/**
	 * Get the status of the bug
	 * @return The bugs status
	 */
	public String getStatus() {
		return getAttribute("Status").getValue();
	}

	/**
	 * Get the keywords for the bug
	 * @return The keywords for the bug
	 */
	public List<String> getKeywords() {
		return validKeywords;
	}

	/**
	 * Set the keywords for the bug
	 * @param keywords The keywords to set the bug to have
	 */
	public void setKeywords(List<String> keywords) {
		this.validKeywords = keywords;
	}

	/**
	 * Get the set of addresses in the CC list
	 * @return A <code>Set</code> of addresses in the CC list
	 */
	public Set<String> getCC() {
		return cc;
	}

	/**
	 * Add an email to the bugs CC list
	 * @param email The email address to add to the CC list
	 */
	public void addCC(String email) {
		cc.add(email);
	}

	/**
	 * Remove an address from the bugs CC list
	 * @param email the address to be removed from the CC list
	 * @return <code>true</code> if the email is in the set and it was removed
	 */
	public boolean removeCC(String email) {
		return cc.remove(email);
	}
	/**
	 * Get the new comment that is to be added to the bug
	 * @return The new comment
	 */
	public String getNewComment() {
		return newComment;
	}

	/**
	 * Set the new comment that will be added to the bug
	 * @param newComment The new comment to add to the bug
	 */
	public void setNewComment(String newComment) {
		this.newComment = newComment;
		newNewComment = newComment;
	}

	/**
	 * Get the new value of the new NewComment
	 * @return the new value of the new NewComment.
	 */
	public String getNewNewComment() {
		return newNewComment;
	}
	

	/**
	 * Set the new value of the new NewComment
	 * @param newNewComment The new value of the new NewComment.
	 */
	public void setNewNewComment(String newNewComment) {
		this.newNewComment = newNewComment;
	}
	

	/**
	 * Get all of the operations that can be done to the bug
	 * @return The operations that can be done to the bug
	 */
	public List<Operation> getOperations() {
		return operations;
	}

	/**
	 * Add an operation to the bug
	 * @param o The operation to add
	 */
	public void addOperation(Operation o) {
		operations.add(o);
	}

	/**
	 * Get an operation from the bug based on its display name
	 * @param displayText The display text for the operation
	 * @return The operation that has the display text
	 */
	public Operation getOperation(String displayText) {
		Iterator<Operation> itr = operations.iterator();
		while (itr.hasNext()) {
			Operation o = itr.next();
			String opName = o.getOperationName();
			opName = opName.replaceAll("</.*>", "");
			opName = opName.replaceAll("<.*>", "");
			if (opName.equals(displayText))
				return o;
		}
		return null;
	}

	/**
	 * Set the selected operation
	 * @param o The selected operation
	 */
	public void setSelectedOperation(Operation o) {
		selectedOperation = o;
	}

	/**
	 * Get the selected operation
	 * @return The selected operation
	 */
	public Operation getSelectedOperation() {
		return selectedOperation;
	}

	public boolean isSavedOffline() {
		return savedOffline;
	}
	
	public boolean isLocallyCreated() {
		return false;
	}
	
	public void setOfflineState(boolean newOfflineState) {
		savedOffline = newOfflineState;
	}
}
