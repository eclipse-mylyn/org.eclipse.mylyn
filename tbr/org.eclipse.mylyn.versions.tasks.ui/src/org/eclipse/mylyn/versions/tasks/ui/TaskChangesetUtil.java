package org.eclipse.mylyn.versions.tasks.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.osgi.util.NLS;

public class TaskChangesetUtil {
	private static final String PLUGIN_ID = "org.eclipse.mylyn.versions.tasks.ui";

	private static List<AbstractChangesetMappingProvider> providers;

	public static List<AbstractChangesetMappingProvider> getMappingProviders() {
		if (providers != null)
			return providers;

		return providers = loadMappingProviders();
	}

	private synchronized static List<AbstractChangesetMappingProvider> loadMappingProviders() {
		List<AbstractChangesetMappingProvider> providers = new ArrayList<AbstractChangesetMappingProvider>();

		MultiStatus result = new MultiStatus(PLUGIN_ID, 0,
				"Task Changeset Mapping Provider failed to load", null); //$NON-NLS-1$

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint connectorsExtensionPoint = registry
				.getExtensionPoint("org.eclipse.mylyn.versions.tasks.changesetmapping"); //$NON-NLS-1$
		IExtension[] extensions = connectorsExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension
					.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				try {
					Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
					if (object instanceof AbstractChangesetMappingProvider) {
						providers
								.add((AbstractChangesetMappingProvider) object);
					} else {
						result.add(new Status(
								IStatus.ERROR,
								PLUGIN_ID,
								// FIXME error message
								NLS.bind(
										"Extension ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
										object.getClass().getCanonicalName(),
										element.getContributor().getName())));
					}
				} catch (Throwable e) {
					result.add(new Status(
							IStatus.ERROR,
							PLUGIN_ID,
							// FIXME error message
							NLS.bind(
									"Connector core failed to load for extension contributed by {0}", element.getContributor().getName()), e)); //$NON-NLS-1$
				}
			}
		}

		if (!result.isOK()) {
			StatusHandler.log(result);
		}

		return providers;
	}

}
