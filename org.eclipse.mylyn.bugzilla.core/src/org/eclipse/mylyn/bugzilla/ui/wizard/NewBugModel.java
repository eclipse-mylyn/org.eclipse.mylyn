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
package org.eclipse.mylar.bugzilla.ui.wizard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.bugzilla.ui.OfflineView;
import org.eclipse.mylar.bugzilla.ui.editor.NewBugEditorInput;
import org.eclipse.mylar.bugzilla.ui.outline.BugzillaTools;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;


/**
 * This class is used to store data about the new bug that is being created while the wizard is being used
 * 
 * @author Eric Booth
 */
public class NewBugModel implements Serializable, IBugzillaBug {

	/** Automatically generated serialVersionUID */
	private static final long serialVersionUID = 3977859587934335283L;
	
	/** Whether the attributes have been parsed yet or not */
	protected boolean hasParsedAttributes = false;
	
	/** Whether the products have been parsed yet or not */
	protected boolean hasParsedProducts = false;
	
	/** The bug's id */
	protected final int id;
	
	/** The product that the bug is for */
	protected String product;

	/** A list of the attributes that can be changed for the new bug */
	public HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();
	
	/** The summary for the bug */
	protected String summary = "";
	
	/** The description for the bug */
	protected String description = "";

	/**
	 * Flag to indicate status of connection to Bugzilla server to identify
	 * whether ProductConfiguration should be used instead
	 */
	protected boolean connected = true;

	/** Whether or not this bug report is saved offline. */
	protected boolean savedOffline = false;
	
	/**
	 * Creates a new <code>NewBugModel</code>. The id chosen for this bug is
	 * based on the id of the last <code>NewBugModel</code> that was created.
	 */
	public NewBugModel() {
		super();
		id = OfflineView.getNextOfflineBugId();
	}

	public Attribute getAttribute(String key) {
		return attributes.get(key);
	}
	
	/**
	 * Get the list of attributes for this model
	 * 
	 * @return An <code>ArrayList</code> of the models attributes
	 */
	public List<Attribute> getAttributes() {
		// create an array list to store the attributes in
		ArrayList<Attribute> attributeEntries = new ArrayList<Attribute>(attributes.keySet().size());
	
		// go through each of the attribute keys
		for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext(); ) {
			// get the key for the attribute
			String key = it.next();
		
			// get the attribute and add it to the list
			Attribute attribute = attributes.get(key);
			attributeEntries.add(attribute);
		}
	
		// return the list of attributes for the bug
		return attributeEntries;
	}

	public int getId() {
		return id;
	}

	public String getServer() {
		return BugzillaTools.OFFLINE_SERVER_DEFAULT;
	}
	
	public String getLabel() {
		return "New Bug #" + id;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String newDescription) {
		description = newDescription;
	}

	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String newSummary) {
		summary = newSummary;
	}
	
	/**
	 * @return The product that the bug is for.
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * Sets the product that the bug is for.
	 * @param product The product.
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @return Flag to indicate status of connection to Bugzilla server (to
	 *         identify whether ProductConfiguration should be used instead)
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Sets the value of the flag to indicate status of connection to Bugzilla server (to identify whether ProductConfiguration should be used instead) 
	 * @param newConnectionStatus <code>true</code> if the bug is connected.
	 */
	public void setConnected(boolean newConnectionStatus) {
		connected = newConnectionStatus;
	}
	
	/**
	 * @return Returns whether the attributes have been parsed yet or not.
	 */
	public boolean hasParsedAttributes() {
		return hasParsedAttributes;
	}

	/**
	 * Sets whether the attributes have been parsed yet or not.
	 * @param hasParsedAttributes <code>true</code> if the attributes have been parsed.
	 */
	public void setParsedAttributesStatus(boolean hasParsedAttributes) {
		this.hasParsedAttributes = hasParsedAttributes;
	}

	/**
	 * @return Returns whether the products have been parsed yet or not.
	 */
	public boolean hasParsedProducts() {
		return hasParsedProducts;
	}

	/**
	 * Sets whether the products have been parsed yet or not.
	 * @param hasParsedProducts <code>true</code> if the products have been parsed.
	 */
	public void setParsedProductsStatus(boolean hasParsedProducts) {
		this.hasParsedProducts = hasParsedProducts;
	}

	public boolean isSavedOffline() {
		return savedOffline;
	}
	
	public boolean isLocallyCreated() {
		return true;
	}
	
	public void setOfflineState(boolean newOfflineState) {
		savedOffline = newOfflineState;
	}

	public void closeEditor(IWorkbenchPage page) {
		IEditorInput input = new NewBugEditorInput(this);
		IEditorPart bugEditor = page.findEditor(input);
		if (bugEditor != null) {
			page.closeEditor(bugEditor, false);
		}
	}

}
