package org.eclipse.nebula.widgets.nattable.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class for the plug-in life cycle
 */
public class NatTablePlugin extends Plugin {
	
	
	public static final String PLUGIN_ID = "de.walware.thirdparty-org.eclipse.nebula.widgets.nattable.core"; //$NON-NLS-1$
	
	
	/** The shared instance. */
	private static NatTablePlugin gPlugin;
	
	/**
	 * Returns the shared plug-in instance
	 * 
	 * @return the shared instance
	 */
	public static NatTablePlugin getDefault() {
		return gPlugin;
	}
	
	public static void log(final IStatus status) {
		final NatTablePlugin plugin = getDefault();
		if (plugin != null) {
			final ILog log = plugin.getLog();
			if (log != null) {
				log.log(status);
			}
		}
	}
	
	
	public NatTablePlugin() {
		gPlugin = this;
	}
	
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		
		gPlugin = null;
	}
	
}
