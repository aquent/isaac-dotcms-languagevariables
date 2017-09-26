package nl.isaac.dotcms.languagevariables.osgi;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.util.Logger;

import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.http.api.ExtHttpService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import nl.isaac.dotcms.languagevariables.cache.LanguageListCacheGroupHandler;
import nl.isaac.dotcms.languagevariables.cache.servlet.FlushVariablesCache;
import nl.isaac.dotcms.languagevariables.languageservice.LanguagePrefixesServlet;
import nl.isaac.dotcms.languagevariables.util.Configuration;
import nl.isaac.dotcms.languagevariables.util.ContentletPostHook;
import nl.isaac.dotcms.languagevariables.viewtool.LanguageVariablesWebAPI;
import nl.isaac.dotcms.util.osgi.ExtendedGenericBundleActivator;
import nl.isaac.dotcms.util.osgi.ViewToolScope;

/**
 * Language Variables Activator.
 */
public class LanguageVariablesActivator extends ExtendedGenericBundleActivator {

  private LanguagePrefixesServlet languagePrefixesServlet;
  private FlushVariablesCache languageFlushServlet;
  private ServiceTracker<ExtHttpService, ExtHttpService> tracker;

  @Override
  public void start(BundleContext context) throws Exception {

    // Default DotCMS call
    initializeServices(context);
    publishBundleServices(context);

    // Add the viewtools
    addViewTool(context, LanguageVariablesWebAPI.class, "languageVariables", ViewToolScope.REQUEST);

    // Register the portlets
    registerPortlets(context, new String[] { "conf/portlet.xml", "conf/liferay-portlet.xml"});

    // Register the servlet
    registerServlets(context);

    // Register language variables (portlet name)
    registerLanguageVariables(context);

    // Register hook
    addPostHook(new ContentletPostHook());

    // Clear Cache
    List<Language> languages = APILocator.getLanguageAPI().getLanguages();
    for (Language language : languages) {
      LanguageListCacheGroupHandler.getInstance().remove(Configuration.CacheListKeysWithoutValue + language.getId());
    }

    // TODO XS/MD: Add code to check if the structure is already created, otherwise WARN
  }


  /**
   * Mostly boilerplate to consistently get the httpservice where we can register the {@link #searcherServlet}, we also create the servlet here.
   * @param context
   */
  private void registerServlets(BundleContext context) {
    tracker = new ServiceTracker<ExtHttpService, ExtHttpService>(context, ExtHttpService.class, null) {
      @Override public ExtHttpService addingService(ServiceReference<ExtHttpService> reference) {
        ExtHttpService extHttpService = super.addingService(reference);

        languagePrefixesServlet = new LanguagePrefixesServlet();
        languageFlushServlet = new FlushVariablesCache();

        try {

          extHttpService.registerServlet("/servlets/glossary/prefixes", languagePrefixesServlet, null, null);
          extHttpService.registerServlet("/servlets/languagevariables/portlet/flush", languageFlushServlet, null, null);

        } catch (ServletException e) {
          throw new RuntimeException("Failed to register servlets", e);
        } catch (NamespaceException e) {
          throw new RuntimeException("Failed to register servlets", e);
        }

        Logger.info(this, "Registered prefixes and languageFlush servlet");

        return extHttpService;
      }
      @Override public void removedService(ServiceReference<ExtHttpService> reference, ExtHttpService extHttpService) {
        extHttpService.unregisterServlet(languagePrefixesServlet);
        extHttpService.unregisterServlet(languageFlushServlet);
        super.removedService(reference, extHttpService);
      }
    };
    tracker.open();
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    unpublishBundleServices();
    unregisterViewToolServices();
  }

}
