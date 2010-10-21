/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.core;

/**
 * Constants for the query type.  
 * Currently two supported query types: all open changes and all my open changes
 *  @author Mikael Kober, Sony Ericsson
 *  @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 *
 */
public class GerritQuery {
	
	/**
	 * Key for the query attribute. 
	 */
	public static final String TYPE = "gerrit query type";
	
	/**
	 * query type: my open changes
	 */
	public static final String MY_OPEN_CHANGES = "my open changes";
	
	/**
	 * query type: all open changes
	 */
	public static final String ALL_OPEN_CHANGES = "all open changes";
	

}
