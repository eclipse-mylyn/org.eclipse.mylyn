/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.spi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;

import com.google.common.collect.ImmutableList;

/**
 * Specifies branding for a connector. The branding extension is obtained by adapting an
 * {@link AbstractRepositoryConnector} instance to {@link RepositoryConnectorBranding}.
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
	 * Returns an input stream with the image data for a 16x16 branding icon, for a connector that is registered at
	 * runtime using {@link RepositoryConnectorContributor}. This is typically shown in the UI when selecting
	 * connectors. Supported file formats are GIF and PNG.
	 * <p>
	 * Note: for connectors contributed through the <code>org.eclipse.mylyn.tasks.ui.repositories</code> extension
	 * point, the branding image specified in the extension takes precedence; this method is never called.
	 * 
	 * @return input stream for image data
	 * @throws IOException
	 *             thrown if opening of the stream fails
	 */
	@NonNull
	public abstract InputStream getBrandingImageData() throws IOException;

	/**
	 * Returns an input stream with the image data for a 7x8 overlay branding icon, for a connector that is registered
	 * at runtime using {@link RepositoryConnectorContributor}. This is typically a very small version of the branding
	 * icon that is used for overlays over repository icons. Supported file formats are GIF and PNG.
	 * <p>
	 * Note: for connectors contributed through the <code>org.eclipse.mylyn.tasks.ui.repositories</code> extension
	 * point, the overlay image specified in the extension takes precedence; this method is never called.
	 * 
	 * @return input stream for image data
	 * @throws IOException
	 *             thrown if opening of the stream fails
	 */
	@NonNull
	public abstract InputStream getOverlayImageData() throws IOException;

	/**
	 * Returns a list of identifiers of the brands supported by this connector. This is useful for connectors that can
	 * connect to different brands of the same system. Typically, different brands are functionally identical but use
	 * different labels and icons.
	 * <p>
	 * Each brand may be presented in the UI as though it were a separate connector.
	 * 
	 * @since 3.16
	 */
	@NonNull
	public List<String> getBrands() {
		return ImmutableList.of();
	}

	/**
	 * Returns branding image data for a specific brand of the target system. Returns <code>null</code> if the given
	 * brand is unknown to the connector or uses the {@link #getBrandingImageData() default branding image}. The default
	 * implementation always returns <code>null</code>.
	 * 
	 * @return input stream for image data, or <code>null</code>
	 * @throws IOException
	 *             thrown if opening of the stream fails
	 * @see #getBrandingImageData()
	 * @see #getBrands()
	 * @since 3.16
	 */
	@Nullable
	public InputStream getBrandingImageData(@NonNull String brand) throws IOException {
		return null;
	}

	/**
	 * Returns overlay image data for a specific brand of the target system. Returns <code>null</code> if the given
	 * brand is unknown to the connector or uses the {@link #getOverlayImageData() default overlay image}. The default
	 * implementation always returns <code>null</code>.
	 * 
	 * @param brand
	 * @throws IOException
	 *             thrown if opening of the stream fails
	 * @return
	 * @see #getOverlayImageData()
	 * @see #getBrands()
	 * @since 3.16
	 */
	@Nullable
	public InputStream getOverlayImageData(@NonNull String brand) throws IOException {
		return null;
	}

	/**
	 * Returns the connector label to use for a specific brand of the target system. Returns <code>null</code> if the
	 * given brand is unknown to the connector or uses the {@link AbstractRepositoryConnector#getLabel() default
	 * connector label}. The default implementation always returns <code>null</code>.
	 * 
	 * @param brand
	 * @return
	 * @see AbstractRepositoryConnector#getLabel()
	 * @see #getBrands()
	 * @since 3.16
	 */
	@Nullable
	public String getConnectorLabel(@NonNull String brand) {
		return null;
	}

}
