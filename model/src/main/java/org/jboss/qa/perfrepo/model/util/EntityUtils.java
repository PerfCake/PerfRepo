package org.jboss.qa.perfrepo.model.util;

import org.jboss.qa.perfrepo.model.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Operations on {@link Entity}.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class EntityUtils {

	public static class UpdateSet<T extends Entity<T>> {
		public Collection<T> toRemove = new ArrayList<T>();
		public Collection<T> toAdd = new ArrayList<T>();
		public Collection<T> toUpdate = new ArrayList<T>();
		// entities that we're trying to add/update but were removed meanwhile
		// we need to decide what to do with these entities on higher level
		public Collection<T> removed = new ArrayList<T>();
	}

	/**
	 * Clone collection of entities.
	 *
	 * @param entities
	 * @return cloned list of entities
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity<?>> List<T> clone(Collection<T> entities) {
		if (entities == null) {
			return null;
		}
		List<T> result = new ArrayList<T>(entities.size());
		for (T entity : entities) {
			result.add((T) entity.clone());
		}
		return result;
	}

	/**
	 * Finds an entity with given ID in the collection.
	 *
	 * @param entities
	 * @param id
	 * @return found entity
	 */
	public static <T extends Entity<T>> T findById(Collection<T> entities, Long id) {
		if (entities == null) {
			return null;
		}
		for (T entity : entities) {
			if (id.equals(entity.getId())) {
				return entity;
			}
		}
		return null;
	}

	/**
	 * Removes the entity with given ID from the collection.
	 *
	 * @param entities
	 * @param id
	 * @return Removed entity, null if nothing was removed.
	 */
	public static <T extends Entity<T>> T removeById(Collection<T> entities, Long id) {
		if (entities == null) {
			return null;
		}
		for (T entity : entities) {
			if (id.equals(entity.getId())) {
				entities.remove(entity);
				return entity;
			}
		}
		return null;
	}

	/**
	 * Removes all entities from entitiesToRemove from allEntities collection. The allEntities
	 * collection is modified by this operation.
	 *
	 * @param allEntities
	 * @param entitiesToRemove
	 * @return Modified allEntities collection.
	 */
	public static <T extends Entity<T>, V extends Collection<T>> V removeAllById(V allEntities, Collection<T> entitiesToRemove) {
		for (T entityToRemove : entitiesToRemove) {
			removeById(allEntities, entityToRemove.getId());
		}
		return allEntities;
	}

	/**
	 * Extract ids from id holders.
	 *
	 * @param entities
	 * @return List of extracted IDs
	 */
	public static List<Long> extractIds(Collection<? extends Entity<?>> entities) {
		if (entities == null) {
			return null;
		}
		List<Long> r = new ArrayList<Long>(entities.size());
		for (Entity<?> entity : entities) {
			r.add(entity.getId());
		}
		return r;
	}

	public static <T extends Entity<T>> UpdateSet<T> updateSet(Collection<T> oldSet, Collection<T> newSet) {
		UpdateSet<T> diff = new UpdateSet<T>();
		Set<Long> oldIds = oldSet == null ? Collections.<Long>emptySet() : new HashSet<Long>(extractIds(oldSet));
		Set<Long> newIds = newSet == null ? Collections.<Long>emptySet() : new HashSet<Long>(extractIds(newSet));
		for (T newEntity : newSet) {
			if (newEntity.getId() == null) {
				diff.toAdd.add(newEntity);
			} else if (oldIds.contains(newEntity.getId())) {
				diff.toUpdate.add(newEntity);
			} else {
				diff.removed.add(newEntity);
			}
		}
		for (T oldEntity : oldSet) {
			if (!newIds.contains(oldEntity.getId())) {
				diff.toRemove.add(oldEntity);
			}
		}
		return diff;
	}
}
