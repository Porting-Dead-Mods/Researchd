package com.portingdeadmods.researchd.api.client.editor;

/**
 * An editor object that has a type, most useful
 * when working with objects that are not standalone
 */
public interface TypedEditorObject<O, T> extends EditorObject<O> {
    T type();
}
