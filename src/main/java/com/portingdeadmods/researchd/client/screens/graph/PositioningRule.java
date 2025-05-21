package com.portingdeadmods.researchd.client.screens.graph;

    import javax.annotation.Nullable;
    import java.util.List;
    import java.util.function.BiFunction;
    import java.util.stream.Collectors;

public enum PositioningRule {
        CENTER_NODE_TO(
                (nodes, node) -> {
                    node.setXExt(nodes.stream().mapToInt(ResearchNode::getX).sum() / nodes.size());
                    for (ResearchNode n : nodes) {
                        n.setYExt(node.getY() + node.getHeight() / 2 - n.getHeight() / 2);
                    }
                    return null;
                }
        );

        private final BiFunction<List<ResearchNode>, ResearchNode, Void> ruleFunction;

        /**
         * A simple positioning rule that the graph layout will use. The first function argument can be ignored
         * since it is used for rules that may use other nodes for calculations.
         * @param ruleFunction
         */
        PositioningRule(BiFunction<List<ResearchNode>, ResearchNode, Void> ruleFunction) {
            this.ruleFunction = ruleFunction;
        }

        public Void apply(List<ResearchNode> nodes, ResearchNode node) {
            return ruleFunction.apply(nodes, node);
        }
    }