package nl.isaac.dotcms.languagevariables.util;

import com.dotcms.contenttype.model.type.ContentType;
import com.dotmarketing.beans.Permission;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.business.ContentletAPIPostHookAbstractImp;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.model.ContentletRelationships;
import com.dotmarketing.portlets.structure.model.Relationship;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.User;

import java.util.List;
import java.util.Map;

import nl.isaac.dotcms.languagevariables.cache.LanguageVariablesCacheCleaner;

public class ContentletPostHook extends ContentletAPIPostHookAbstractImp{

	public void checkin(Contentlet contentlet, Map<Relationship, List<Contentlet>> contentRelationships, List<Category> cats ,List<Permission> permissions, User user,boolean respectFrontendRoles,Contentlet returnValue) {
		handleContentlet(contentlet);
	}
	
	public void checkin(Contentlet currentContentlet, ContentletRelationships relationshipsData, List<Category> cats, List<Permission> selectedPermissions, User user,	boolean respectFrontendRoles,Contentlet returnValue) {
		handleContentlet(currentContentlet);
	}
	
	public void checkin(Contentlet contentlet, List<Category> cats ,List<Permission> permissions, User user,boolean respectFrontendRoles,Contentlet returnValue) {
		handleContentlet(contentlet);
	}
	
	public void checkin(Contentlet contentlet, List<Permission> permissions, User user,boolean respectFrontendRoles,Contentlet returnValue) {
		handleContentlet(contentlet);
	}
	
	public void checkin(Contentlet contentlet ,User user,boolean respectFrontendRoles,List<Category> cats,Contentlet returnValue) {
		handleContentlet(contentlet);
	}
	
	public void checkin(Contentlet contentlet, Map<Relationship, List<Contentlet>> contentRelationships, List<Category> cats, User user,boolean respectFrontendRoles,Contentlet returnValue) {
		handleContentlet(contentlet);
	}
	
	public void checkin(Contentlet contentlet, User user,boolean respectFrontendRoles, Contentlet returnValue) {
		handleContentlet(contentlet);
	}
	
	public void checkin(Contentlet contentlet, Map<Relationship, List<Contentlet>> contentRelationships, User user,boolean respectFrontendRoles,Contentlet returnValue) {
		handleContentlet(contentlet);
	}
	
	public void checkinWithoutVersioning(Contentlet contentlet, Map<Relationship, List<Contentlet>> contentRelationships, List<Category> cats ,List<Permission> permissions, User user,boolean respectFrontendRoles,Contentlet returnValue) {
		handleContentlet(contentlet);
	}
	
	public void publish(Contentlet contentlet, User user, boolean respectFrontendRoles) {
		handleContentlet(contentlet);
	}
	
	public void publish(List<Contentlet> contentlets, User user, boolean respectFrontendRoles) {
		for(Contentlet contentlet : contentlets) {
			handleContentlet(contentlet);			
		}
	}

	public void unpublish(Contentlet contentlet, User user, boolean respectFrontendRoles) {
		handleContentlet(contentlet);
	}
	
	public void unpublish(List<Contentlet> contentlets, User user, boolean respectFrontendRoles) {
		for(Contentlet contentlet : contentlets) {
			handleContentlet(contentlet);			
		}
	}
	
	public void restoreVersion(Contentlet contentlet, User user, boolean respectFrontendRoles) {
		handleContentlet(contentlet);
	}
	
	private void handleContentlet(Contentlet newContentlet) {

	  ContentType conType = null;
	  try {
	    conType = APILocator.getContentTypeAPI(APILocator.getUserAPI().getSystemUser()).find(newContentlet.getContentTypeId());
	  } catch (Exception e) {
	    Logger.error(this, "Unable to get the content type from: " + newContentlet.getContentTypeId(), e);
	    return;
	  }
		
		if(conType.variable().equals(Configuration.getStructureVelocityVarName())) {
			String propertyKey = newContentlet.getStringProperty("key");
			String hostIdentifier = newContentlet.getHost();
			String languageId = String.valueOf(newContentlet.getLanguageId());
			
			Logger.debug(this, "Language key '" + propertyKey +  "' changed. Flushing cache...");
			LanguageVariablesCacheCleaner.flush(propertyKey, hostIdentifier, languageId);

			 /**
			  * Volgens Xander werkt het koppelen van Language Variabele niet bij het importeren d.m.v. een CSV bestand
			  * Echter bleek na een test met Koen Peters dat dit wel correct lijkt te werken (misschien bij grote bestanden niet).
			  * De code hieronder zorgt ervoor dat er keys worden toegevoegd voor alle talen mocht er een nieuwe key worden toegevoegd.
			  * Echter wordt dit nu niet gebruikt, omdat het niet nodig lijkt @Danny.Gloudemans 
			  * Verwijderen wanneer de echte portlet wordt gebouwd.
			  */
//			//Option to add also the key in all the other languages with a value from the configuration
//			for(Language language : languageAPI.getLanguages()) {
//				String newContentletIdentifier = newContentlet.getIdentifier();
//				//If it is not the same language
//				if(!newContentletIdentifier.isEmpty() && newContentlet.getLanguageId() != language.getId()) {
//					
//					long languageIdAsLong = language.getId();
//					Logger.info(this, "newContentlet Identifier: " + newContentletIdentifier + " , Language ID: " + languageIdAsLong);
//					Contentlet contentlet;
//					
//					try {
//						contentlet = APILocator.getContentletAPI().findContentletByIdentifier(newContentletIdentifier, true, languageIdAsLong, systemUser, false);
//					} catch (DotContentletStateException e1) {
//						//Contentlet with this identifier doesn't exist for this language
//						contentlet = null;
//					} catch (DotDataException e1) {
//						throw new RuntimeException(e1.toString(), e1);
//					} catch (DotSecurityException e1) {
//						throw new RuntimeException(e1.toString(), e1);
//					}
//										
//					//If contentlet is null, the key doesn't exist in the language, so add this key
//					if(contentlet == null) {
//						contentlet = new Contentlet();
//						contentlet.setHost(newContentlet.getHost());	
//						contentlet.setLanguageId(languageIdAsLong);
//						contentlet.setInode("");
//						contentlet.setIdentifier(newContentletIdentifier);
//						contentlet.setStructureInode(newContentlet.getStructureInode());
//						contentlet.setStringProperty("key", propertyKey);
//						contentlet.setStringProperty("value", "TEST VALUE");
//						
//						try {
//							contentlet = contentletAPI.checkin(contentlet, systemUser, false);
//							contentletAPI.publish(contentlet, systemUser, false);
//						} catch (DotContentletValidationException e) {
//							throw new RuntimeException(e.toString(), e);
//						} catch (DotContentletStateException e) {
//							throw new RuntimeException(e.toString(), e);
//						} catch (IllegalArgumentException e) {
//							throw new RuntimeException(e.toString(), e);
//						} catch (DotDataException e) {
//							throw new RuntimeException(e.toString(), e);
//						} catch (DotSecurityException e) {
//							throw new RuntimeException(e.toString(), e);
//						}
//					}
//				}
//			}	
		}		
	}

	/* Extra functions we need to override because dotCMS forgot to 
	 * put them in the ContentletAPIPostHookAbstractImp.. */
	
	public void isInodeIndexed(String arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}
}
