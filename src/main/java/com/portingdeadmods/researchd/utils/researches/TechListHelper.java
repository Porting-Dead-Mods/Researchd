package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.client.screens.list.TechListEntry;

import java.util.*;

public class TechListHelper {
    public static void setEntryCoordinates(TechList list, int cols, int paddingX, int paddingY) {
        int col = 0;
        int row = 0;
        for (TechListEntry entry : list.entries()) {
            entry.setX(paddingX + col * TechListEntry.WIDTH);
            entry.setY(paddingY + row * TechListEntry.HEIGHT);
            if (col < cols) {
                col++;
            } else {
                col = 0;
                row++;
            }
        }
    }
}
