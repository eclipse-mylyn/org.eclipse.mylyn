/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractQueryHitCollector;
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * Collector for the Bugzilla query results
 * 
 * TODO: unify BugzillaSearchResultCollector and BugzillaResultCollector
 * 
 * @author Shawn Minto
 * @author Rob Elves
 */
public class BugzillaResultCollector extends AbstractQueryHitCollector {

	public BugzillaResultCollector(TaskList tasklist) {
		super(tasklist);		
	}

	private List<AbstractQueryHit> results = new ArrayList<AbstractQueryHit>();

//	private IBugzillaSearchOperation operation;
//
//	public void setOperation(IBugzillaSearchOperation operation) {
//		this.operation = operation;
//	}
//
//	public IBugzillaSearchOperation getOperation() {
//		return operation;
//	}

	@Override
	public void addMatch(AbstractQueryHit hit) {
		String description = hit.getId() + ": " + hit.getDescription();
		hit.setDescription(description);
		results.add(hit);
	}
	
	public List<AbstractQueryHit> getResults() {
		return results;
	}

}

///**
//* Get the string specifying the number of matches found
//* 
//* @param count
//*            The number of matches found
//* @return The <code>String</code> specifying the number of matches found
//*/
//private String getFormattedMatchesString(int count) {
//	// if only 1 match, return the singular match string
//	String name = "";
//	if (operation != null && operation.getName() != null)
//		name = " - " + operation.getName();
//	if (count == 1)
//		return MATCH + name;
//
//	// format the matches string and return it
//	Object[] messageFormatArgs = { new Integer(count) };
//	return MessageFormat.format(MATCHES + name, messageFormatArgs);
//}
