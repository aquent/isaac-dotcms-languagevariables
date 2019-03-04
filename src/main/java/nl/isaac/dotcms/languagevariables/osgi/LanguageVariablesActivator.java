package nl.isaac.dotcms.languagevariables.osgi;

import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.loggers.Log4jUtil;
import nl.isaac.dotcms.languagevariables.cache.servlet.FlushVariablesCache;
import nl.isaac.dotcms.languagevariables.languageservice.LanguagePrefixesServlet;
import nl.isaac.dotcms.languagevariables.util.ContentletPostHook;
import nl.isaac.dotcms.languagevariables.viewtool.LanguageVariablesWebApiInfo;
import nl.isaac.dotcms.util.osgi.ExtendedGenericBundleActivator;
import org.apache.felix.http.api.ExtHttpService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Language Variables Activator.
 */
public class LanguageVariablesActivator extends ExtendedGenericBundleActivator {

  private LanguagePrefixesServlet languagePrefixesServlet;
  private FlushVariablesCache languageFlushServlet;
  private ServiceTracker tracker;
  private ExtHttpService httpService;
  private LoggerContext pluginLoggerContext;

  @Override
  public void start(BundleContext context) throws Exception {
    // Setup Logger
    LoggerContext dotcmsLoggerContext = Log4jUtil.getLoggerContext();
    pluginLoggerContext = (LoggerContext) LogManager.getContext(this.getClass().getClassLoader(),
                                                                false, dotcmsLoggerContext, dotcmsLoggerContext.getConfigLocation());

    // Default DotCMS call
    initializeServices(context);

    // Add the viewtools
    registerViewToolService(context, new LanguageVariablesWebApiInfo());

    // Register the portlets
    registerPortlets(context, new String[] { "conf/portlet.xml", "conf/liferay-portlet.xml"});

    // Register the servlet
    registerServlets(context);

    // Register language variables (portlet name)
    registerLanguageVariables(context);

    // Register hook
    addPostHook(new ContentletPostHook());

    // Flush the cache
    DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    cache.flushAll();

    // TODO XS/MD: Add code to check if the structure is already created, otherwise WARN
  }


  /**
   * Mostly boilerplate to consistently get the httpservice where we can register the {@link #searcherServlet}, we also create the servlet here.
   * @param context
   */
  private void registerServlets(BundleContext context) {
    tracker = new ServiceTracker(context, ExtHttpService.class.getName(), null);
    ServiceReference sRef = context.getServiceReference(ExtHttpService.class.getName());
    if (sRef != null) {
      tracker.addingService(sRef);
      httpService = (ExtHttpService) context.getService(sRef);
      try {
        languagePrefixesServlet = new LanguagePrefixesServlet();
        languageFlushServlet = new FlushVariablesCache();

        httpService.registerServlet("/servlets/glossary/prefixes", languagePrefixesServlet, null, null);
        httpService.registerServlet("/servlets/languagevariables/portlet/flush", languageFlushServlet, null, null);
      } catch (Exception e) {
        throw new RuntimeException("Failed to register servlets", e);
      }
    }
    tracker.open();
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    // Unregister the servlets
    if (httpService != null && languagePrefixesServlet != null) {
      httpService.unregisterServlet(languagePrefixesServlet);
    }
    if (httpService != null && languageFlushServlet != null) {
      httpService.unregisterServlet(languageFlushServlet);
    }
    tracker.close();

    // Unregister the viewtool
    unregisterViewToolServices();

    //Unregister all the bundle services
    unregisterServices(context);

    // Shutdown the logger
    Log4jUtil.shutdown(pluginLoggerContext);
  }

}
