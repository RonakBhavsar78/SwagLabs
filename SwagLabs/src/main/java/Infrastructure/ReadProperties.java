package Infrastructure;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;


public class ReadProperties {
	
	String propertyFileName;
	static InputStream inputStream;
	static Properties property = new Properties();
	
	/**
	 * LoadProperties() function loads property file. 
	 * <p>
	 * If the given property file name is new file or differs from the current file,
	 * then this method attempts to load property file as a resource from the class path.
	 * If the property file is found, it clears the existing properties and loads new ones.  
	 * @param propertyName the name of the property file to load (should be accessible from the class path).
	 */
	public String LoadProperties(String propertyName) {
		try {
			if(this.propertyFileName == null || this.propertyFileName.equalsIgnoreCase(propertyName)) {
				if(inputStream != null) {
					inputStream = null;
				}
				inputStream  = getClass().getClassLoader().getResourceAsStream(propertyName);
			}
			
			if(inputStream != null) {
				property.clear();
				property.load(inputStream);
			}else {
				throw new FileNotFoundException("Property File '"+propertyName+"' not found in classpath");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception --" + e);
		}
		
		return "";
	}
	
	/**
	 * getpropertyValues() function retrieves the trim "value" of a specific "property key" from a given property file.
	 * <p>
	 * This method first load the properties from LoadProperties function,
	 * then look up the value for the provided key as ('webControlVarible')
	 * If the property key exists, it returns the trimmed value , otherwise returns null. 
	 * @param propertyFileName : The name of the property file load in inputStream.
	 * @param webControlVariable : The key of the property to retrieve.
	 */
	
	public String getpropertyValues(String propertyFileName, String webControlVariable) {
		LoadProperties(propertyFileName);
		if(property.getProperty(webControlVariable) != null) {
			return property.getProperty(webControlVariable).trim();
		}else {
			return property.getProperty(webControlVariable);
		}
	}

}
