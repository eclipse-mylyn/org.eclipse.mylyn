/*******************************************************************************
 * Copyright (c) 2011 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * Contributors:
 *   Alvaro Sanchez-Leon - Initial API
 *******************************************************************************/

package org.eclipse.mylyn.versions.core.spi;

import java.util.Map;

/**
 * @author lmcalvs
 */
public interface ScmInfoAttributes {

	/**
	 * Returns specific informational attributes from extending connectors
	 * 
	 * @return
	 */
	public Map<String, String> getInfoAtrributes();

}
