package nl.isaac.dotcms.languagevariables.osgi;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.util.Logger;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import nl.isaac.dotcms.languagevariables.cache.LanguageListCacheGroupHandler;
import nl.isaac.dotcms.languagevariables.util.Configuration;

/**
 * Clears the language variable cache on restart of the plugin.
 * @author cfalzone
 */
public class CacheRegionsClearingJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

    Logger.info(this, "------------------------------------------");
    Logger.info(this, "Start CacheRegionsClearing job");
    Logger.info(this, "");

    List<Language> languages = APILocator.getLanguageAPI().getLanguages();
    for (Language language : languages) {
      LanguageListCacheGroupHandler.getInstance().remove(Configuration.CacheListKeysWithoutValue + language.getId());
    }

    Logger.info(this, "");
    Logger.info(this, "Finish CacheRegionsClearing Job");
    Logger.info(this, "------------------------------------------");

  }

}
