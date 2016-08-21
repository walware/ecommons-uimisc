package de.walware.ecommons.waltable.internal;

import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;


/**
 * The activator class for the plug-in life cycle
 */
public class WaLTablePlugin extends Plugin {
	
	
	public static final String PLUGIN_ID= "de.walware.ecommons.waltable.core"; //$NON-NLS-1$
	
	
	/** The shared instance. */
	private static WaLTablePlugin gPlugin;
	
	/**
	 * Returns the shared plug-in instance
	 * 
	 * @return the shared instance
	 */
	public static WaLTablePlugin getDefault() {
		return gPlugin;
	}
	
	public static void log(final IStatus status) {
		final WaLTablePlugin plugin= getDefault();
		if (plugin != null) {
			final ILog log= plugin.getLog();
			if (log != null) {
				log.log(status);
			}
		}
	}
	
	
	public WaLTablePlugin() {
		gPlugin= this;
	}
	
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		
		gPlugin= null;
	}
	
}
