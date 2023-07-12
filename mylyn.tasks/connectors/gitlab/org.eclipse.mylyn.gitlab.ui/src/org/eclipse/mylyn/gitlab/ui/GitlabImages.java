package org.eclipse.mylyn.gitlab.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.commons.ui.CommonsUiConstants;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class GitlabImages {

    private static final URL baseUrl;
    static {
	Bundle bundle = Platform.getBundle(GitlabUiActivator.PLUGIN_ID);
	if (bundle != null) {
	    baseUrl = bundle.getEntry("/icons/"); //$NON-NLS-1$
	} else {
	    URL iconsUrl = null;
	    try {
		// lookup location of CommonImages class on disk
		iconsUrl = new URL(GitlabImages.class.getResource("GitlabImages.class"), "../../../../../../icons/"); //$NON-NLS-1$ //$NON-NLS-2$
	    } catch (MalformedURLException e) {
		// ignore
	    }
	    baseUrl = iconsUrl;
	}
    }

    private static ImageRegistry imageRegistry;

    private static final String T_EVIEW = "eview16"; //$NON-NLS-1$
    private static final String T_OBJ = "obj16"; //$NON-NLS-1$

    public static final ImageDescriptor GITLAB_ICON = create(T_EVIEW, "gitlab-icon.png"); //$NON-NLS-1$

    public static final ImageDescriptor GITLAB_OVERLAY = create(T_EVIEW, "gitlab-overlay.png"); //$NON-NLS-1$
    public static final ImageDescriptor GITLAB = create(T_OBJ, "gitlab.png"); //$NON-NLS-1$

    private static ImageDescriptor create(String prefix, String name) {
	try {
	    return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
	} catch (MalformedURLException e) {
	    return ImageDescriptor.getMissingImageDescriptor();
	}
    }

    private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
	if (baseUrl == null) {
	    throw new MalformedURLException();
	}

	StringBuffer buffer = new StringBuffer(prefix);
	buffer.append('/');
	buffer.append(name);
	return new URL(baseUrl, buffer.toString());
    }

    private static ImageRegistry getImageRegistry() {
	if (imageRegistry == null) {
	    imageRegistry = new ImageRegistry();
	}

	return imageRegistry;
    }

    /**
     * Lazily initializes image map.
     *
     * @param imageDescriptor
     * @return Image
     */
    public static Image getImage(ImageDescriptor imageDescriptor) {
	ImageRegistry imageRegistry = getImageRegistry();
	Image image = imageRegistry.get("" + imageDescriptor.hashCode()); //$NON-NLS-1$
	if (image == null) {
	    image = imageDescriptor.createImage(true);
	    imageRegistry.put("" + imageDescriptor.hashCode(), image); //$NON-NLS-1$
	}
	return image;
    }
}
