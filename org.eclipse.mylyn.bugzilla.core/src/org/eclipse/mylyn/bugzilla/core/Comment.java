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
import java.util.Date;

/**
 * A comment posted on a bug.
 */
public class Comment implements Serializable
{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3978422529214199344L;

	/** Comment's bug */
	private final BugReport bug;
	
	/** Comment's number */
	private final int number;
	
	/** Comment's text */
	private String text;
	
	/** Comment's author */
	private final String author;
	
	/** Author's realname, if known */
	private final String authorName;
	
	/** Comment's creation timestamp */
	private final Date created;
	
	/** Preceding comment */
	private Comment previous;
	
	/** Following comment */
	private Comment next;
	
	/**
	 * Constructor
	 * @param bug The bug that this comment is associated with
	 * @param date The date taht this comment was entered on 
	 * @param author The author of the bug
	 * @param authorName The authors real name
	 */
	public Comment(BugReport bug, int number, Date date, String author, String authorName) 
	{
		this.bug = bug;
		this.number = number;
		this.created = date;
		this.author = author;
		this.authorName = authorName;
	}
	
	/**
	 * Get the bug that this comment is associated with
	 * @return The bug that this comment is associated with
	 */
	public BugReport getBug() 
	{
		return bug;
	}
	
	/**
	 * Get this comment's number
	 * @return This comment's number
	 */
	public int getNumber()
	{
		return number;
	}

	/**
	 * Get the time that this comment was created
	 * @return The comments creation timestamp
	 */
	public Date getCreated() 
	{
		return created;
	}

	/**
	 * Get the author of the comment
	 * @return The comments author
	 */
	public String getAuthor() 
	{
		return author;
	}
	
	/**
	 * Get the authors real name
	 * @return Returns author's name, or <code>null</code> if not known
	 */
	public String getAuthorName() 
	{
		return authorName;
	}

	/**
	 * Get the text contained in the comment
	 * @return The comments text
	 */
	public String getText() 
	{
		return text;
	}

	/**
	 * Set the comments text
	 * @param text The text to set the comment to have
	 */
	public void setText(String text) 
	{
		this.text = text;
	}
	
	/**
	 * Get the next comment for the bug
	 * @return Returns the following comment, or <code>null</code> if the last one.
	 */
	public Comment getNext() 
	{
		return next;
	}

	/**
	 * Set the next comment for the bug
	 * @param next The comment that is after this one
	 */
	protected void setNext(Comment next) 
	{
		this.next = next;
	}

	/**
	 * Get the previous comment
	 * @return Returns preceding comment, or <code>null</code> if the first one
	 */
	public Comment getPrevious() 
	{
		return previous;
	}

	/**
	 * Seth the previous comment for the bug
	 * @param previous The comment that is before this one
	 */
	protected void setPrevious(Comment previous) 
	{
		this.previous = previous;
	}
}

