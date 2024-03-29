/**
 * This class is copied from the ISAAC package
 */
package nl.isaac.dotcms.languagevariables.util;

import javax.servlet.http.HttpServletRequest;

import com.dotmarketing.util.PageMode;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.Role;
import com.dotmarketing.business.web.UserWebAPIImpl;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

public class RequestUtil implements ViewTool {
	private HttpServletRequest request;
	
	/**
	 * Only dotCMS should call this constructor, followed by an init()
	 */
	public RequestUtil() {};
	
	public RequestUtil(HttpServletRequest request) {
		this.request = request;
	}
	
	public void init(Object initData) {
		ViewContext context = (ViewContext) initData;
	    this.request = context.getRequest();		
	}
	
	private boolean isBackendLogin() {
		try {
			User backendUser = WebAPILocator.getUserWebAPI().getLoggedInUser(request);
			return backendUser != null && backendUser.isActive();
		} catch (Exception e) {
			Logger.warn(this, "Exception while checking for Admin", e);
			return false;
		}
	}
	
	
	public boolean isAdministratorLoggedIn() {
		try {
			User backendUser = WebAPILocator.getUserWebAPI().getLoggedInUser(request);
			Role adminRole = APILocator.getRoleAPI().loadCMSAdminRole();
			return APILocator.getRoleAPI().doesUserHaveRole(backendUser, adminRole);
		} catch (Exception e) {
			Logger.warn(this, "Exception while checking for Admin", e);
			return false;
		}
	}
	
	public boolean isLiveMode() {
		return !(isEditMode() || isPreviewMode()); 
	}
	
	public boolean isEditMode() {
		return PageMode.get(request) == PageMode.EDIT_MODE;
	}
	
	public boolean isPreviewMode() {
		return PageMode.get(request) == PageMode.PREVIEW_MODE;
	}
	
	public boolean isBackendViewOfPage() {
		return isBackendLogin();
	}
	
	public boolean isEditOrPreviewMode() {
		return isEditMode() || isPreviewMode();
	}
	
	public boolean isDebugMode() {
		UserWebAPIImpl uwai = new UserWebAPIImpl();
		User frontend = null;
		User backend = null;
		try {
			frontend = uwai.getLoggedInFrontendUser(request);
			backend  = uwai.getLoggedInUser(request);
		} catch (Exception e) {
			throw new RuntimeException(e.toString(), e);
		}
		
		if(null == frontend && backend != null) {
			return true;
		}

		return isEditMode()	|| isPreviewMode();
	}
	
	public Host getCurrentHost() {
		try {
			return WebAPILocator.getHostWebAPI().getCurrentHost(request);
		} catch (PortalException e) {
			throw new RuntimeException(e);
		} catch (SystemException e) {
			throw new RuntimeException(e);
		} catch (DotDataException e) {
			throw new RuntimeException(e);
		} catch (DotSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public String getLanguage() {
		String languageId = (String) request.getSession().getAttribute(WebKeys.HTMLPAGE_LANGUAGE);
		if(languageId == null) {
			Logger.warn(this, "Can't detect language, returning default language");
			languageId = Long.valueOf(APILocator.getLanguageAPI().getDefaultLanguage().getId()).toString();
		}
		
		return languageId;
	}
	
	public static Integer getSelectedLanguage(HttpServletRequest request) {
		return (Integer)request.getSession().getAttribute(WebKeys.LANGUAGE);
	}
}
