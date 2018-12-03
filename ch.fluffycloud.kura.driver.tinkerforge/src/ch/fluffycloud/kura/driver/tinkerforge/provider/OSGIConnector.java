package ch.fluffycloud.kura.driver.tinkerforge.provider;

import java.util.Map;

public interface OSGIConnector {
	
	void activate(Map<String, Object> properties);
	
	void deactivate();
	
	void modified(Map<String, Object> properties);

}
