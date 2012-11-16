package com.hazelcast.hibernate4.region;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate4.RegionCache;
import com.hazelcast.hibernate4.access.NonStrictReadWriteAccessDelegate;
import com.hazelcast.hibernate4.access.ReadOnlyAccessDelegate;
import com.hazelcast.hibernate4.access.ReadWriteAccessDelegate;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;

import java.util.Properties;

/**
 * @mdogan 11/9/12
 */
public final class HazelcastCollectionRegion<Cache extends RegionCache> extends AbstractTransactionalDataRegion<Cache>
        implements CollectionRegion {

    public HazelcastCollectionRegion(final HazelcastInstance instance,
                                     final String regionName, final Properties props,
                                     final CacheDataDescription metadata, final Cache cache) {
        super(instance, regionName, props, metadata, cache);
    }

    public CollectionRegionAccessStrategy buildAccessStrategy(final AccessType accessType) throws CacheException {
        if (null == accessType) {
            throw new CacheException(
                    "Got null AccessType while attempting to build CollectionRegionAccessStrategy. This can't happen!");
        }
        if (AccessType.READ_ONLY.equals(accessType)) {
            return new CollectionRegionAccessStrategyAdapter(
                    new ReadOnlyAccessDelegate<HazelcastCollectionRegion>(this, props));
        }
        if (AccessType.NONSTRICT_READ_WRITE.equals(accessType)) {
            return new CollectionRegionAccessStrategyAdapter(
                    new NonStrictReadWriteAccessDelegate<HazelcastCollectionRegion>(this, props));
        }
        if (AccessType.READ_WRITE.equals(accessType)) {
            return new CollectionRegionAccessStrategyAdapter(
                    new ReadWriteAccessDelegate<HazelcastCollectionRegion>(this, props));
        }
        if (AccessType.TRANSACTIONAL.equals(accessType)) {
            throw new CacheException("Transactional access is not currently supported by Hazelcast.");
        }
        throw new CacheException("Got unknown AccessType " + accessType
                                 + " while attempting to build CollectionRegionAccessStrategy.");
    }
}
