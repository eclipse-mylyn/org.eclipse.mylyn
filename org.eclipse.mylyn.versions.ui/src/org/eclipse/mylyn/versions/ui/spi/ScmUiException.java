/*******************************************************************************
 * Copyright (c) 2010 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * Contributors:
 *   Alvaro Sanchez-Leon - Initial API and Implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.mylyn.versions.ui.spi;

import java.util.Collection;

/**
 * @author alvaro
 */
public class ScmUiException extends Exception {
	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1697975821054492793L;

	// ------------------------------------------------------------------------
	// Instance variables
	// ------------------------------------------------------------------------
	
	private Collection<String> fDetails = null;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	/**
	 * @param aMessage
	 */
	public ScmUiException(String aMessage) {
		super(aMessage);
	}

	/**
	 * @param aMessage
	 * @param aChainedExc
	 *            - Original chained Exception
	 */
	public ScmUiException(String aMessage, Throwable aChainedExc) {
		super(aMessage, aChainedExc);
	}

	/**
	 * @param aChainedExc
	 *            Original chained Exception
	 */
	public ScmUiException(Throwable aChainedExc) {
		super(aChainedExc);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	/**
	 * @param aDetails
	 */
	public void setDetails(Collection<String> aDetails) {
		fDetails = aDetails;
	}

	/**
	 * @return
	 */
	public Collection<String> getDetails() {
		return fDetails;
	}

}
