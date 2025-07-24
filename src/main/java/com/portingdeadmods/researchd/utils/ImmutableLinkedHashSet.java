package com.portingdeadmods.researchd.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class ImmutableLinkedHashSet<E> extends LinkedHashSet<E> {
    private boolean locked = false;

    public ImmutableLinkedHashSet(Collection<? extends E> c) {
        super(c);
        this.locked = true;
    }

    @Override
    public boolean add(E e) {
        if (locked) throw new UnsupportedOperationException("This set is immutable");
        return super.add(e);
    }

    @Override
    public boolean remove(Object o) {
        if (locked) throw new UnsupportedOperationException("This set is immutable");
        return super.remove(o);
    }

    // Optionally override other mutators like addAll, removeAll, clear...
}
