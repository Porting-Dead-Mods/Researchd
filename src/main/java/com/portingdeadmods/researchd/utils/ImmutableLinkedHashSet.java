package com.portingdeadmods.researchd.utils;

import java.util.Collection;
import java.util.LinkedHashSet;

// TODO: Move to PDL
public class ImmutableLinkedHashSet<E> extends LinkedHashSet<E> {
    private final boolean locked;

    public ImmutableLinkedHashSet(Collection<? extends E> c) {
        super(c);
        this.locked = true;
    }

    @Override
    public boolean add(E e) {
        if (locked) throw new UnsupportedOperationException("Attempted to add to immutable set");
        return super.add(e);
    }

    @Override
    public boolean remove(Object o) {
        if (locked) throw new UnsupportedOperationException("Attempted to remove from immutable set");
        return super.remove(o);
    }
}
