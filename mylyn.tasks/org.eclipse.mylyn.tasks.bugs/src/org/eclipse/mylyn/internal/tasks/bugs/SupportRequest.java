/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.bugs.IProduct;
import org.eclipse.mylyn.tasks.bugs.ISupportRequest;
import org.eclipse.mylyn.tasks.bugs.ITaskContribution;

/**
 * @author Steffen Pingel
 */
public class SupportRequest implements ISupportRequest {

	private final Map<String, ITaskContribution> contributionByProductId;

	private final IStatus status;

	private final SupportProviderManager providerManager;

	private AttributeTaskMapper defaultContribution;

	private final IProduct product;

	public SupportRequest(SupportProviderManager providerManager, IStatus status, IProduct product) {
		this.providerManager = providerManager;
		this.status = status;
		contributionByProductId = new HashMap<>();
		if (product != null) {
			this.product = product;
			defaultContribution = process(getNamespace(), (SupportProduct) product);
		} else {
			this.product = null;
			process();
		}
	}

	public SupportRequest(SupportProviderManager providerManager, IStatus status) {
		this(providerManager, status, null);
	}

	@Override
	public ITaskContribution getOrCreateContribution(IProduct product) {
		ITaskContribution contribution = contributionByProductId.get(product.getId());
		if (contribution == null) {
			contribution = new AttributeTaskMapper(status, product);
			contributionByProductId.put(product.getId(), contribution);
		}
		return contribution;
//		ITaskContribution contribution = taskContributionByProductId.get(productId);
//		if (contribution == null) {
//			SupportProduct product = providerManager.getProduct(productId);
//			if (product == null) {
//				throw new IllegalArgumentException(NLS.bind("Invalid product id ''{0}''", productId)); //$NON-NLS-1$
//			}
//			contribution = new AttributeTaskMapper(status, product);
//			taskContributionByProductId.put(productId, contribution);
//		}
//		return contribution;
	}

	@Override
	public ITaskContribution getDefaultContribution() {
		if (defaultContribution == null) {
			String namespace = getNamespace();
			Map<String, String> attributes = providerManager.getDefaultProduct().getAllAttributes(namespace);

			defaultContribution = new AttributeTaskMapper(status, providerManager.getDefaultProduct());
			defaultContribution.getAttributes().putAll(attributes);
		}
		return defaultContribution;
	}

	public IProduct getProduct() {
		return product;
	}

	@Override
	public IStatus getStatus() {
		return status;
	}

	private void process() {
		String namespace = getNamespace();
		Collection<SupportProduct> products = providerManager.getProducts();
		for (SupportProduct product : products) {
			process(namespace, product);
		}
	}

	private AttributeTaskMapper process(String namespace, SupportProduct product) {
		Map<String, String> productAttributes = product.getAllAttributes(namespace);
		if (!productAttributes.isEmpty()) {
			// merge global and more specific product attributes
			Map<String, String> attributes = providerManager.getDefaultProduct().getAllAttributes(namespace);
			attributes.putAll(productAttributes);

			AttributeTaskMapper contribution = (AttributeTaskMapper) getOrCreateContribution(product);
			contribution.getAttributes().putAll(attributes);
			return contribution;
		}
		return null;
	}

	private String getNamespace() {
		return status.getPlugin();
	}

	public List<ITaskContribution> getContributions() {
		return new ArrayList<>(contributionByProductId.values());
	}

}
