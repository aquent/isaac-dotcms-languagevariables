package nl.isaac.dotcms.languagevariables.viewtool;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.isaac.dotcms.languagevariables.cache.LanguageListCacheGroupHandler;
import nl.isaac.dotcms.languagevariables.languageservice.ContentGlossaryAPI;
import nl.isaac.dotcms.languagevariables.util.Configuration;
import nl.isaac.dotcms.languagevariables.util.RequestUtil;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

/**
 *
 * @author jorith.vandenheuvel
 *
 */
public class LanguageVariablesWebAPI implements ViewTool {
  
  private HttpServletRequest request;

  @Override
  public void init(Object obj) {
    ViewContext context = (ViewContext) obj;
    this.request = context.getRequest();
  }

  /**
   * 
   * @param key
   * @return  The value of the key in the current language. If there is no current language 
   *      defined, the default language will be used. Note that when a certain request 
   *      variable is set, only the key will be returned for debugging purposes.
   * 
   */
  public String get(String key) {
    try {
      if(shouldReturnKey()) {
        return key;
      } else {
        String language = (String) request.getSession().getAttribute(com.dotmarketing.util.WebKeys.HTMLPAGE_LANGUAGE);
        
        if (language == null) {
          language = String.valueOf(APILocator.getLanguageAPI().getDefaultLanguage().getId());
        }
        
        return get(key, language);
      }
    } catch (Throwable t) {
      Logger.error(this, "De oorzaak", t);
      throw new RuntimeException(t);
    }
  }

  /**
   * 
   * @param key
   * @param languageId
   * @return The value of the key in the given language. Note that when a certain request 
   *      variable is set, only the key will be returned for debugging purposes.
   */
  public String get(String key, String languageId) {
    if(shouldReturnKey()) {
      return key;
    } else {
      
      String reqHost = new RequestUtil(request).getCurrentHost().getIdentifier();
      boolean live = new RequestUtil(request).isLiveMode();
      
      String defLang = String.valueOf(APILocator.getLanguageAPI().getDefaultLanguage().getId());
      String sysHost = "";
      try {
        sysHost = APILocator.getHostAPI().findSystemHost().getIdentifier();
      } catch (Exception e) {
        Logger.error(this, "No System Host", e);
        return addKeyToCacheAndReturnKey(key, languageId);
      }
      
      // Span styling for good keys
      String defKeyStyle = "background-color:rgb(225, 224, 255);border:1px dashed #ababab;display:inline-block;padding:0 3px;";

      // First try with the request's language and host
      ContentGlossaryAPI contentGlossaryAPI = new ContentGlossaryAPI(request);
      String value = contentGlossaryAPI.getValue(key);
      if(UtilMethods.isSet(value)) {
        // For live page mode, just return the naked value, otherwise, <span> it so we can see it in use
        if(!isEditOrPreviewMode()) {
          return value;
        } else {
          return "<span style=\"" + defKeyStyle + "\" title=\"Language Key: " + key + "\">" + value + "</span>";
        }
      }
      
      // Let's try same host, default language now
      if(! languageId.equals(defLang)) {
        contentGlossaryAPI = new ContentGlossaryAPI(defLang, reqHost, live);
        value = contentGlossaryAPI.getValue(key);
        if(UtilMethods.isSet(value)) {
          if(!isEditOrPreviewMode()) {
            return value;
          } else {
            return "<span style=\"" + defKeyStyle + "\" title=\"Language Key: " + key + "\">" + value + "</span>";
          }
        }
      }
      
      // Now let's try system host, request language
      if(! reqHost.equals(sysHost)) {
        contentGlossaryAPI = new ContentGlossaryAPI(languageId, sysHost, live);
        value = contentGlossaryAPI.getValue(key);
        if(UtilMethods.isSet(value)) {
          if(!isEditOrPreviewMode()) {
            return value;
          } else {
            return "<span style=\"" + defKeyStyle + "\" title=\"Language Key: " + key + "\">" + value + "</span>";
          }
        }
      }
      
      // Now let's try system host, default language
      if((! reqHost.equals(sysHost)) && (! languageId.equals(defLang))) {
        contentGlossaryAPI = new ContentGlossaryAPI(defLang, sysHost, live);
        value = contentGlossaryAPI.getValue(key);
        if(UtilMethods.isSet(value)) {
          if(!isEditOrPreviewMode()) {
            return value;
          } else {
            return "<span style=\"" + defKeyStyle + "\" title=\"Language Key: " + key + "\">" + value + "</span>";
          }
        }
      }
      
      // None of them matched so let's return the key
      return addKeyToCacheAndReturnKey(key, languageId);
    }
  }
  
	public boolean isEditMode() {
		Object EDIT_MODE_SESSION = request.getSession().getAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION);
		if(EDIT_MODE_SESSION != null) {
			return Boolean.valueOf(EDIT_MODE_SESSION.toString());
		}
		return false; 
	}
	
	public boolean isPreviewMode() {
		Object PREVIEW_MODE_SESSION = request.getSession().getAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION);
		if(PREVIEW_MODE_SESSION != null) {
			return Boolean.valueOf(PREVIEW_MODE_SESSION.toString());
		}
		return false; 
	}
	
	public boolean isEditOrPreviewMode() {
		return isEditMode() || isPreviewMode();
	}
  
  /**
   * Add key without value to a cache list per language, so it can be displayed on the portlet
   * @param key
   * @param languageId
   * @return Key or a replacement from the configuration file
   */
  @SuppressWarnings("unchecked")
  private String addKeyToCacheAndReturnKey(String key, String languageId) {
    List<String> keyList = (List<String>) LanguageListCacheGroupHandler.getInstance().get(Configuration.CacheListKeysWithoutValue + languageId);
    
    if(!keyList.contains(key)) {
      keyList.add(key);
    }
    
    //If true show key otherwise get the replacement value from the configuration file
    if(!Configuration.isValueOfKeyEmptyShowKey()) {
      if(Configuration.isReplacementValueAnEmptyString()) {
        key = "";
      } else {
        key = Configuration.getReplacementValueIfValueIsEmpty();        
      }
    }
    
    if(!isEditOrPreviewMode()) {
      return key;
    } else {
      return "<span style=\"background-color:rgb(255, 239, 242);border:1px dashed #cfcfcf;display:inline-block;padding:0 3px;\" title=\"Language Key Missing!\">" + key + "</span>";
    }
  }
  
  /**
   * Get cache list per language for the portlet
   * @param languageId
   * @return List with keys
   */
  @SuppressWarnings("unchecked")
  public List<String> getKeysWithoutValue(String languageId) {
    List<String> keyList = (List<String>) LanguageListCacheGroupHandler.getInstance().get(Configuration.CacheListKeysWithoutValue + languageId);
    return keyList;
  }
  
  private boolean shouldReturnKey() {
    return UtilMethods.isSet(request.getParameter(Configuration.getDisplayKeysParameterName()));
  }
}
