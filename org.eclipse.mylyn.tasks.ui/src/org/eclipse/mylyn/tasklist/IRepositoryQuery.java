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

import org.eclipse.mylar.internal.tasklist.ui.ITaskListElement;

/**
 * @author Mik Kersten
 */
public interface IRepositoryQuery extends ITaskListElement{

	public String getQueryUrl();
	
	public void setQueryUrl(String query);
	
	public String getRepositoryUrl();
	
	public void setRepositoryUrl(String url);
	
	public List<IQueryHit> getHits();
	
	public int getMaxHits();
	
	public void setMaxHits(int maxHits);
	
	public void addHit(IQueryHit hit);
	
}
