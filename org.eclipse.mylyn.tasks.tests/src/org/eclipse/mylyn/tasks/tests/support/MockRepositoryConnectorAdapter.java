/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.support;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorBranding;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;

public class MockRepositoryConnectorAdapter implements IAdapterFactory {

	public final static class DynamicMockRepositoryConnectorUi extends MockRepositoryConnectorUi {

		private final AbstractRepositoryConnector connector;

		DynamicMockRepositoryConnectorUi(AbstractRepositoryConnector connector) {
			this.connector = connector;
		}

		@Override
		public String getConnectorKind() {
			return connector.getConnectorKind();
		}

	}

	private static final Class<?>[] ADAPTER_LIST = new Class<?>[] { AbstractRepositoryConnector.class };

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adaptableObject instanceof MockRepositoryConnector) {
			final AbstractRepositoryConnector connector = (AbstractRepositoryConnector) adaptableObject;
			if (adapterType == AbstractRepositoryConnectorUi.class) {
				return new DynamicMockRepositoryConnectorUi(connector);
			} else if (adapterType == RepositoryConnectorBranding.class) {
				return new RepositoryConnectorBranding() {
					@Override
					public InputStream getOverlayImageData() throws IOException {
						return CommonTestUtil.getResource(this, "testdata/icons/mock-overlay.gif");
					}

					@Override
					public InputStream getBrandingImageData() throws IOException {
						return CommonTestUtil.getResource(this, "testdata/icons/mock-repository.gif");
					}
				};
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
