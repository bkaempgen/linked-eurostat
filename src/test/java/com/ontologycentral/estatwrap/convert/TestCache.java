package com.ontologycentral.estatwrap.convert;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;

import junit.framework.TestCase;

/**
 * @author aharth
 */
public class TestCache extends TestCase {
	Logger _log = Logger.getLogger(this.getClass().getName());

	public void testCache() throws Exception {
			//      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			CacheManager cmanager = Caching.getCachingProvider().getCacheManager();
			
			MutableConfiguration<String, Date> config = new MutableConfiguration<String, Date>();
			config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_DAY)).setStatisticsEnabled(true);

			Cache cache = cmanager.createCache("cache", config);
			
			cache.put("foo", "bar");
			String result = (String)cache.get("foo");
			_log.log(Level.INFO, "{0}", result);
	}
}
