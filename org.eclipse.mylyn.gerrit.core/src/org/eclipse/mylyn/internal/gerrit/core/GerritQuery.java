/*********************************************************************
 * Copyright (c) 2010, 2013 Sony Ericsson/ST Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Sascha Scholz (SAP) - improvements
 *      Francois Chouinard - Bug 414253 Add some definitions
 *      Jacques Bouthillier - Bug 414253 Add support for Gerrit Dashboard
 
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

/**
 * Constants for the query type.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Francois Chouinard
 * @author Jacques Bouthillier
 */
public class GerritQuery {

	/**
	 * Key for the query attribute.
	 */
	public static final String TYPE = "gerrit query type"; //$NON-NLS-1$

	/**
	 * Query type: my changes
	 */
	public static final String MY_CHANGES = "my changes"; //$NON-NLS-1$

	/**
	 * Query type: my watched changes
	 */
	public static final String MY_WATCHED_CHANGES = "my watched changes"; //$NON-NLS-1$

	/**
	 * Query : my starred changes
	 */
	public static final String QUERY_MY_STARRED_CHANGES = "is:starred status:open"; //$NON-NLS-1$

	/**
	 * Query : my drafts changes
	 */
	public static final String QUERY_MY_DRAFTS_CHANGES = "is:draft"; //$NON-NLS-1$

	/**
	 * Query : my drafts comment changes
	 */
	public static final String QUERY_MY_DRAFTS_COMMENTS_CHANGES = "has:draft"; //$NON-NLS-1$

	/**
	 * Query type: all open changes
	 */
	public static final String ALL_OPEN_CHANGES = "all open changes"; //$NON-NLS-1$

	/**
	 * Query : all merged changes
	 */
	public static final String QUERY_ALL_MERGED_CHANGES = "status:merged"; //$NON-NLS-1$

	/**
	 * Query : all abandoned changes
	 */
	public static final String QUERY_ALL_ABANDONED_CHANGES = "status:abandoned"; //$NON-NLS-1$

	/**
	 * Query type: open changes by project
	 */
	public static final String OPEN_CHANGES_BY_PROJECT = "open changes by project"; //$NON-NLS-1$

	/**
	 * Key for the project attribute
	 */
	public static final String PROJECT = "gerrit query project"; //$NON-NLS-1$

	/**
	 * Query type: custom
	 */
	public static final String CUSTOM = "custom"; //$NON-NLS-1$

	/**
	 * Key for the query string attribute
	 */
	public static final String QUERY_STRING = "gerrit query string"; //$NON-NLS-1$

}
