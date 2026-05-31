package com.portingdeadmods.researchd.data.saved;

public interface SavedDataMap {
    void setOnChangedFunction(Runnable onChangeFunction);

    void setChanged();
}
