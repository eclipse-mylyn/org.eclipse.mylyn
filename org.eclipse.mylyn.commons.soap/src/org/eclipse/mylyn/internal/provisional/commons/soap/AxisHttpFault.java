/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.soap;

import java.io.IOException;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.utils.Messages;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;

/**
 * Indicates that the server returned an unexpected HTTP error in response to a SOAP call.
 * 
 * @author Steffen Pingel
 */
public class AxisHttpFault extends AxisFault {

	private static final long serialVersionUID = -7568493217182396309L;

	public static AxisHttpFault makeFault(HttpMethodBase method) throws IOException {
		int returnCode = method.getStatusCode();
		String statusMessage = method.getStatusText();
		AxisHttpFault fault = new AxisHttpFault("HTTP", "(" + returnCode + ")" + statusMessage, returnCode); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		fault.extractDetails(method);
		fault.setFaultDetailString(Messages.getMessage("return01", "" + returnCode, //$NON-NLS-1$ //$NON-NLS-2$
				method.getResponseBodyAsString()));
		fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE, Integer.toString(returnCode));
		return fault;
	}

	private String location;

	private final int returnCode;

	public AxisHttpFault(String code, String faultString, int returnCode) {
		super(code, faultString, null, null);
		this.returnCode = returnCode;
	}

	private void extractDetails(HttpMethodBase method) {
		Header locationHeader = method.getResponseHeader("location"); //$NON-NLS-1$
		if (locationHeader != null) {
			this.location = locationHeader.getValue();
		}
	}

	public String getLocation() {
		return location;
	}

	public int getReturnCode() {
		return returnCode;
	}

}
