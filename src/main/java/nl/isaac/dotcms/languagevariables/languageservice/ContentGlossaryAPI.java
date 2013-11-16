package nl.isaac.dotcms.languagevariables.languageservice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.isaac.dotcms.languagevariables.cache.LanguageVariableCacheKey;
import nl.isaac.dotcms.languagevariables.cache.LanguageVariablesCacheGroupHandler;
import nl.isaac.dotcms.languagevariables.util.Configuration;
import nl.isaac.dotcms.languagevariables.util.ContentletQuery;
import nl.isaac.dotcms.languagevariables.util.RequestUtil;

import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.util.UtilMethods;

public class ContentGlossaryAPI {
	
	private final String languageId;
	private final String hostIdentifier;
	private final boolean live;
	
//	private LanguageFactory factory = new LanguageFactory();
		
	public ContentGlossaryAPI(HttpServletRequest request) {
		this(new RequestUtil(request).getLanguage(), new RequestUtil(request).getCurrentHost().getIdentifier(), new RequestUtil(request).isLiveMode());
	}

	public ContentGlossaryAPI(String languageId, String hostIdentifier, boolean live) {
		this.languageId = languageId;
		this.hostIdentifier = hostIdentifier;
		this.live = live;
	}
	
//	/**
//	 * Create key for every language
//	 * @param key
//	 */
//	public void addKey(String key) {
//		for(Language language : APILocator.getLanguageAPI().getLanguages()) {
//			factory.createNewContentlet(language.getId(), key, "");
//		}
//	}
//		
//	/**
//	 * Call get KeyList
//	 * Call Get all Unique Key Objects
//	 * @return List<Key>
//	 */
//	public List<Key> getAllUniqueKeys() {
//		List<Key> keys = factory.getAllKeyObjects(null);
//		Logger.info(this, "Size: " + keys.size());
//		return keys;		
//	}
//	
//	/**
//	 * (Un)archive key based on unique key
//	 * @param key
//	 * @param archive true = archive, false = un-archive
//	 */
//	public void keyPublish(boolean publish, String[] keys) {
//		List<Contentlet> publishContentlets = getAllContentletsByKey(keys);
//		User user = factory.getUser();
//		
//		try {
//			if(publish) {
//				APILocator.getContentletAPI().publish(publishContentlets, user, false);				
//			} else {
//				APILocator.getContentletAPI().unpublish(publishContentlets, user, false);				
//			}
//		} catch (DotContentletStateException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotDataException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotSecurityException e) {
//			throw new RuntimeException(e.toString(), e);
//		}
//	}	
//	
//
//	/**
//	 * Publish/Unpublish value based on unique key. When there isn't a contentlet available for that key, 
//	 * it will create a new contentlet
//	 * @param key
//	 * @param keyArray
//	 * @param publish true = publish, false = unpublish
//	 */
//	public void publishContentlet(String key, boolean publish, String[][] keyArray) {
//		List<Contentlet> allContentlets = factory.getAllContentlets(null);
//		List<Contentlet> publishContentlets = new ArrayList<Contentlet>();
//		
//		for(Contentlet c : allContentlets) {
//			if(c.getStringProperty("key").equalsIgnoreCase(key)) {
//				Logger.info(this, "Toegevoegd: Key: " + key);
//				publishContentlets.add(c);
//			}
//		}
//
//		for(int i = 0; i < keyArray.length; i++) {
//			Long languageId = Long.parseLong(keyArray[i][0]);
//			String value = keyArray[i][1];
//			boolean exists = false;
//			for(Contentlet c : publishContentlets) {
//				if(c.getLanguageId() == languageId) {
//					c.setStringProperty("value", value);
//					exists = true;
//				}
//			}
//			if(!exists) {
//				publishContentlets.add(factory.createNewContentlet(languageId, key, value));
//			}
//		}
//		Logger.info(this, "Publish contentlets: "+publishContentlets.size());
//		User user = factory.getUser();
//		try {
//			if(publish) {
//				APILocator.getContentletAPI().publish(publishContentlets, user, false);				
//			} else {
//				APILocator.getContentletAPI().unpublish(publishContentlets, user, false);				
//			}
//		} catch (DotContentletStateException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotDataException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotSecurityException e) {
//			throw new RuntimeException(e.toString(), e);
//		}
//	}
//	
//	/**
//	 * (Un)archive key based on unique key
//	 * @param key
//	 * @param archive true = archive, false = un-archive
//	 */
//	public void archiveContentlet(boolean archive, String[] keys) {
//		List<Contentlet> archiveContentlets = getAllContentletsByKey(keys);
//		User user = factory.getUser();
//		
//		try {
//			if(archive) {
//				APILocator.getContentletAPI().archive(archiveContentlets, user, false);				
//			} else {
//				APILocator.getContentletAPI().unarchive(archiveContentlets, user, false);				
//			}
//		} catch (DotContentletStateException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotDataException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotSecurityException e) {
//			throw new RuntimeException(e.toString(), e);
//		}
//	}	 
//	
//	/**
//	 * (Un)Lock all contentlets based on the unique key.
//	 * @param key
//	 */
//	public void lockContentlet(boolean lock, String[] keys) {
//		List<Contentlet> archiveContentlets = getAllContentletsByKey(keys);
//		User user = factory.getUser();
//		
//		try {
//			for(Contentlet c : archiveContentlets) {
//				if(lock) {
//					APILocator.getContentletAPI().lock(c, user, false);				
//				} else {
//					APILocator.getContentletAPI().unlock(c, user, false);
//				}
//			}
//			
//		} catch (DotContentletStateException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotDataException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotSecurityException e) {
//			throw new RuntimeException(e.toString(), e);
//		}
//	}
//	
//	/**
//	 * Delete all contentlets based on the unique key.
//	 * @param key
//	 */
//	public void deleteContentlet(String[] keys) {
//		List<Contentlet> deleteContentlets = getAllContentletsByKey(keys);
//		User user = factory.getUser();
//		
//		try {
//			APILocator.getContentletAPI().delete(deleteContentlets, user, false);
//		} catch (DotContentletStateException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotDataException e) {
//			throw new RuntimeException(e.toString(), e);
//		} catch (DotSecurityException e) {
//			throw new RuntimeException(e.toString(), e);
//		}
//	}
// 
//	/**
//	 * Get value based on key and languageId. If contentlets == null then the factory will get 
//	 * a new list with all the current contentlets
//	 * @param languageId
//	 * @param key
//	 * @param contentlets
//	 * @return
//	 */
//	public Key getKey(String key){
//		List<Key> keys = factory.getAllKeyObjects(null);
//		
//		for(Key keyEntry : keys) {
//			if(keyEntry.getKey().equalsIgnoreCase(key)) {
//				return keyEntry;
//			}
//		}
//		return null;
//	}
//	
//	private List<Contentlet> getAllContentletsByKey(String[] keys) {
//		List<Contentlet> allContentlets = factory.getAllContentlets(null);
//		List<Contentlet> selectedContentlets = new ArrayList<Contentlet>();
//		
//		for(Contentlet c : allContentlets) {
//			for(String key : keys) {
//				if(c.getStringProperty("key").equalsIgnoreCase(key)) {
//					selectedContentlets.add(c);
//					break;
//				}
//			}
//		}
//		return selectedContentlets;
//	}
//	
//
//	
//	//BIJ RELEASE IS ONDERSTAANDE NIET MEER NODIG
//	
//	//Tijdelijk tot dat het omgebouwd is door front-end
//	public List<Contentlet> getLanguageKeysTemp() {
//		return getLanguageKeys(null, null);
//	} 
//	
//	/**
//	 * Get all LanguageVariables contentlets based on LanguageId. When contentlets == null the factory will
//	 * get the current set of contentlets.
//	 * @param languageId
//	 * @param contentlets
//	 * @return List<Contentlet>
//	 */
//	public List<Contentlet> getLanguageKeys(Long languageId, List<Contentlet> contentlets) {
//		List<Contentlet> keys = new ArrayList<Contentlet>();
//		List<String> stringList = new ArrayList<String>();
//
//		if(null == contentlets) {
//			contentlets = factory.getAllContentlets(languageId);
//		}
//		
//		for(Contentlet contentlet : contentlets) {
//			
//			if(!stringList.contains(contentlet.getStringProperty("key"))) {
//				keys.add(contentlet);
//				stringList.add(contentlet.getStringProperty("key"));
//			}
//		} 
//		
//		return keys;
//	}

	public String getValue(String key) {
		LanguageVariableCacheKey cacheKey = new LanguageVariableCacheKey(key, languageId, hostIdentifier, live);
		LanguageVariablesCacheGroupHandler cache = LanguageVariablesCacheGroupHandler.getInstance();
		
		String value = cache.get(cacheKey.getKey());
		
		return value;
	}
	
	public List<KeyValuePair<String, String>> getKeysWithPrefixes(List<String> prefixes) {
		List<KeyValuePair<String, String>> keyValuePairs = new ArrayList<KeyValuePair<String, String>>();
		
		List<Contentlet> results = new ArrayList<Contentlet>();
		
		for(String prefix : prefixes) {
			
			// prevent searching for '*' if an empty prefix is given
			if(UtilMethods.isSet(prefix)) {
				results.addAll(getContentletsWithKey(prefix + "*"));
			}
		}
		
		//convert them to key-value pairs
		for(Contentlet contentlet: results) {
			KeyValuePair<String, String> keyValuePair = new KeyValuePair<String, String>(
					contentlet.getStringProperty(Configuration.getStructureKeyField()), 
					contentlet.getStringProperty(Configuration.getStructureValueField()));
			keyValuePairs.add(keyValuePair);
		}
		
		return keyValuePairs;
	}

	public List<Contentlet> getContentletsWithKey(String key) {
		//retrieve all the contentlets with the prefix
		ContentletQuery contentletQuery = new ContentletQuery(Configuration.getStructureVelocityVarName());
		contentletQuery.addHostAndIncludeSystemHost(hostIdentifier);
		contentletQuery.addFieldLimitation(true, Configuration.getStructureKeyField(), key);
		contentletQuery.addLanguage(languageId);

		if(live) {
			contentletQuery.addLive(true);
		} else {
			contentletQuery.addWorking(true);
		}
		
		List<Contentlet> results = contentletQuery.executeSafe();
		return results;
	}
}
