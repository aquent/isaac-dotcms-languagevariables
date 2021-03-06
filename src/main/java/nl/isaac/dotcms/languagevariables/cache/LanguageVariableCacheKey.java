package nl.isaac.dotcms.languagevariables.cache;


/**
 * Immutable class to uniquely identify a Language Variable file.
 * 
 * @author jorith.vandenheuvel
 *
 */
public class LanguageVariableCacheKey {
	private static final String SEPARATOR = "-xXx-";
	private final String propertyKey;
	private final String languageId;
	private final String hostId;
	private final Boolean live;
	
	public LanguageVariableCacheKey(String propertyKey, String languageId, String hostId, boolean live) {
		this.propertyKey = propertyKey;
		this.languageId = languageId;
		this.hostId = hostId;
		this.live = live;
	}

	public static LanguageVariableCacheKey createInstanceWithKey(String key) {
		String[] parts = key.split(SEPARATOR);  
		if(parts.length != 4) {
			throw new RuntimeException("Illegal key: " + key);
		}
		return new LanguageVariableCacheKey(parts[0], parts[1], parts[2], Boolean.valueOf(parts[3]));
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public String getLanguageId() {
		return languageId;
	}

	public String getHostId() {
		return hostId;
	}

	public Boolean getLive() {
		return live;
	}

	public String getKey() {
		return propertyKey + SEPARATOR + languageId + SEPARATOR + hostId + SEPARATOR + live;		
	}
	
	public String getReadableString() {
		return propertyKey + " - " + languageId + " - " + hostId + " - " + live;		
	}

	public boolean equals(Object obj) {
		if(obj instanceof LanguageVariableCacheKey) {
			return ((LanguageVariableCacheKey)obj).toString().equals(toString());
		}
		return false;
	}
	
	public int hashCode() {
		return getKey().hashCode();
	}
}
