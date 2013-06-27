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

package org.eclipse.mylyn.tasks.core.spi;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;

/**
 * Specifies branding for a connector that is registered as runtime using {@link RepositoryConnectorContributor}. The
 * branding extension is obtained by adapting an {@link AbstractRepositoryConnector} instance to
 * {@link RepositoryConnectorBranding}.
 * <p>
 * To contribute branding clients need to register an {@link IAdapterFactory} for the type
 * {@link AbstractRepositoryConnector}.
 * </p>
 * Example <code>plugin.xml</code>:
 * <p>
 * 
 * <pre>
 *  &lt;extension
 *        point="org.eclipse.core.runtime.adapters"&gt;
 *     &lt;factory
 *           adaptableType="org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector"
 *           class="MyRepositoryConnectorAdapter"&gt;
 *        &lt;adapter
 *              type="org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorBranding"&gt;
 *        &lt;/adapter&gt;
 *     &lt;/factory&gt;
 *  &lt;/extension&gt;
 * </pre>
 * 
 * </p>
 * <p>
 * <code>MyRepositoryConnector</code> needs to return an instance of {@link RepositoryConnectorBranding} for the
 * appropriate connector instance:
 * 
 * <pre>
 * public class MyRepositoryConnectorAdapter implements IAdapterFactory {
 * 
 * 	&#064;Override
 * 	public Object getAdapter(Object adaptableObject, Class adapterType) {
 * 		if (adaptableObject instanceof MyRepositoryConnector) {
 * 			if (adapterType == RepositoryConnectorBranding.class) {
 * 				return new RepositoryConnectorBranding() {
 * 					&#064;Override
 * 					public InputStream getOverlayImageData() throws IOException {
 * 						return getResource(this, &quot;repository-overlay.gif&quot;);
 * 					}
 * 
 * 					&#064;Override
 * 					public InputStream getBrandingImageData() throws IOException {
 * 						return CommonTestUtil.getResource(this, &quot;repository.gif&quot;);
 * 					}
 * 				};
 * 			}
 * 		}
 * 		return null;
 * 	}
 * }
 * </pre>
 * 
 * </p>
 * 
 * @since 3.10
 * @see RepositoryConnectorContributor
 * @see RepositoryConnectorDescriptor
 */
public abstract class RepositoryConnectorBranding {

	/**
	 * Returns an input stream with the image data for a 16x16 branding icon. This is typically shown in the UI when
	 * selecting connectors. Supported file formats are GIF and PNG.
	 * 
	 * @return input stream for image data
	 * @throws IOException
	 *             thrown if opening of the stream fails
	 */
	@NonNull
	public abstract InputStream getBrandingImageData() throws IOException;

	/**
	 * Returns an input stream with the image data for a 7x8 overlay branding icon. This is typically a very small
	 * version of the branding icon that is used for overlays over repository icons. Supported file formats are GIF and
	 * PNG.
	 * 
	 * @return input stream for image data
	 * @throws IOException
	 *             thrown if opening of the stream fails
	 */
	@NonNull
	public abstract InputStream getOverlayImageData() throws IOException;

}
