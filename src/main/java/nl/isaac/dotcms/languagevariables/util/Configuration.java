package nl.isaac.dotcms.languagevariables.util;

public class Configuration {
	public static final String CacheListKeysWithoutValue = "CacheListKeys." + Configuration.class.getName();

	public static String getStructureVelocityVarName() {
		return "LanguageVariables";
	}

	public static String getStructureKeyField() {
		return "key";
	}

	public static String getStructureValueField() {
		return "value";
	}
	
	public static String getStructureKeyFieldLabel() {
		return "Key";
	}
	
	public static String getStructureValueFieldLabel() {
		return "Value";
	}
	
	public static boolean isValueOfKeyEmptyShowKey() {
		return true;
	}
	
	public static boolean isReplacementValueAnEmptyString() {
		return true;
	}

	public static String getReplacementValueIfValueIsEmpty() {
		return "";
	}

	public static String getDisplayKeysParameterName() {
		return "displaykeys";
	}
}