package ru.dantalian.copvoc.persist.api;

import java.util.Map;

public interface PersistCacheManager {

	public static final String ID = "id";

	/**
	 * Store an arbitrary data in cache
	 * @param aHashCode
	 * @param aMap
	 * @throws PersistException any connection issue
	 */
	void save(String aHashCode, Map<String, Object> aMap) throws PersistException;

	/**
	 * Retrieve cached data
	 * @param aHashCode
	 * @return data related to requested hash code or null if not {@link #save}d before
	 * @throws PersistException in any connection issue
	 */
	Map<String, Object> load(String aHashCode) throws PersistException;

}
