package com.portingdeadmods.researchd.client.screens;

/** Every Z offset used for {@code PoseStack#translate}. */
public final class RdZIndex {
    // Research Screen //

    /** Behind the rest of the screen */
    public static final int EDITOR_SIDEBAR = -1000;

    /** Page title, description and completion etc. (above nodes) */
    public static final int GRAPH_HEADER = 250;

    /** Base of the popup stack */
    public static final int POPUP_BASE = 300;

    /** Added once per popup */
    public static final int POPUP_STEP = 100;

    /** Tooltip of the selected research panel. Needs to be above the graph and the popup stack */
    public static final int SELECTED_RESEARCH_TOOLTIP = 400;

    /** The "x" drawn over a hovered research in the queue */
    public static final int QUEUE_REMOVE_ICON = 1000;

    // Lab Screen //

    public static final int LAB_RESEARCH_TOOLTIP = 100;

    // Shared Widgets //

    /** Relative */
    public static final int TOOLTIP = 10;

    /** Relative */
    public static final int DROP_DOWN = 10;

    /** Hover overlays on editor list entries and item selectors etc. */
    public static final int EDITOR_HOVER_OVERLAY = 160;

    // Team Screen //

    /** Background  */
    public static final int DRAGGABLE_WINDOW = 500;

    public static final int DRAGGABLE_WINDOW_ROW = DRAGGABLE_WINDOW + 1;

    /** Face + name */
    public static final int DRAGGABLE_WINDOW_ROW_LABEL = DRAGGABLE_WINDOW + 2;

    public static final int DRAGGABLE_WINDOW_ROW_BUTTONS = DRAGGABLE_WINDOW + 3;

    /** Relative */
    public static final int WARNING_POPUP = 1;

    private RdZIndex() {}
}
