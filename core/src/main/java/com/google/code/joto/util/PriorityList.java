package com.google.code.joto.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * extension of java.util.List for supporting sorting with priority 
 */
public class PriorityList<T> implements Iterable<T> {

    private final Set<PrioritizedItem<T>> set = new TreeSet<PrioritizedItem<T>>();

    private int idGenerator = 0;

    //-------------------------------------------------------------------------

	public PriorityList() {
	}

	//-------------------------------------------------------------------------

    public void add(T item, int priority) {
        this.set.add(new PrioritizedItem<T>(item, priority, ++idGenerator));
    }

    /**
     * implements Iterable<T> ... return a sorted iterator (on a temporary copy)
     */
    @Override
    public Iterator<T> iterator() {
    	List<T> tmp = new ArrayList<T>(set.size());
    	for(PrioritizedItem<T> item : set) {
    		tmp.add(item.value);
    	}
    	return tmp.iterator();
    }

    // -------------------------------------------------------------------------
    
    /**
     * internal
     */
    private static class PrioritizedItem<T> implements Comparable<PrioritizedItem<T>> {

        private final T value;
        private final int priority;
        private final int id; // for comparing with equals priority

        public PrioritizedItem(T value, int priority, int id) {
            this.value = value;
            this.priority = priority;
            this.id = id;
        }

        public int compareTo(PrioritizedItem<T> other) {
            if (this.priority != other.priority) {
                return (other.priority - this.priority);
            }
            return (other.id - this.id);
        }

        public boolean equals(Object obj) {
        	if (obj == this) return true;
        	if (!(obj instanceof PrioritizedItem<?>)) return false;
            return this.id == ((PrioritizedItem<?>)obj).id;
        }

    }
}
