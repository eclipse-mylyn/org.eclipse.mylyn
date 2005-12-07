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
package org.eclipse.mylar.tasklist;

import java.util.List;

import org.eclipse.mylar.tasklist.ui.ITaskListElement;

public interface IQuery extends ITaskListElement{

	public String getQueryString();
	
	public void setQueryString(String query);
	
	public List<IQueryHit> getChildren();
	
	public int getMaxHits();
	
	public void setMaxHits(int maxHits);
	
	public void addHit(IQueryHit hit);
}
