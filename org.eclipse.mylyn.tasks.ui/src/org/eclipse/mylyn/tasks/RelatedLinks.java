/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Related links stored for each task used as input to the Table
 * used in the TaskSummaryEditor
 * 
 * @author Ken Sueda
 */
public class RelatedLinks {
	private List<String> links;
	
	public RelatedLinks() {
		links = new ArrayList<String>();			
	}		
	public void add(String link) {
		links.add(link);
	}			
	public List<String> getLinks() {
		return links;
	}
	public void remove(String link) {
		links.remove(link);
	}
}
