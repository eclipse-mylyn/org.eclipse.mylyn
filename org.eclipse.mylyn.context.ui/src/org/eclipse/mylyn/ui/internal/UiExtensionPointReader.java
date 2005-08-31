/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;

/**
 * @author Mik Kersten
 */
public class UiExtensionPointReader {
	
	private static boolean extensionsRead = false;
	private static UiExtensionPointReader thisReader = new UiExtensionPointReader();
	
	// read the extensions and load the required plugins
	public static void initExtensions() {
		// code from "contributing to eclipse" with modifications for deprecated code
		if(!extensionsRead){
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint(MylarUiPlugin.EXTENSION_ID_CONTEXT);
			IExtension[] extensions = extensionPoint.getExtensions();
			for(int i = 0; i < extensions.length; i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for(int j = 0; j < elements.length; j++){
					if(elements[j].getName().compareTo(MylarUiPlugin.ELEMENT_UI_BRIDGE) == 0){
						readBridge(elements[j]);
					} else if(elements[j].getName().compareTo(MylarUiPlugin.ELEMENT_UI_CONTEXT_LABEL_PROVIDER) == 0){
						readLabelProvider(elements[j]);
					} 
				}
			}
			extensionsRead = true;
		}
	}

	private static void readLabelProvider(IConfigurationElement element) {
		try {
			Object provider = element.createExecutableExtension(MylarUiPlugin.ELEMENT_UI_CLASS);
			Object contentType = element.getAttribute(MylarUiPlugin.ELEMENT_UI_BRIDGE_CONTENT_TYPE);
			if (provider instanceof ILabelProvider && contentType != null) {
				MylarUiPlugin.getDefault().internalAddContextLabelProvider((String)contentType, (ILabelProvider)provider);
			} else {
				MylarPlugin.log("Could not load label provider: " + provider.getClass().getCanonicalName() + " must implement " + ILabelProvider.class.getCanonicalName(), thisReader);	
			}
		} catch (CoreException e){
			MylarPlugin.log(e, "Could not load label provider extension");
		}
	}
	
	private static void readBridge(IConfigurationElement element) {
		try{
			Object bridge = element.createExecutableExtension(MylarUiPlugin.ELEMENT_UI_CLASS);
			Object contentType = element.getAttribute(MylarUiPlugin.ELEMENT_UI_BRIDGE_CONTENT_TYPE);
			if (bridge instanceof IMylarUiBridge && contentType != null) {
				MylarUiPlugin.getDefault().internalAddBridge((String)contentType, (IMylarUiBridge) bridge);
			} else {
				MylarPlugin.log("Could not load bridge: " + bridge.getClass().getCanonicalName() + " must implement " + IMylarUiBridge.class.getCanonicalName(), thisReader);	
			}
		} catch (CoreException e){
			MylarPlugin.log(e, "Could not load bridge extension");
		}
	}
}
