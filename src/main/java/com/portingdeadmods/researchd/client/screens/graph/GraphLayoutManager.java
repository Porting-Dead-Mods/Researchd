package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.utils.UniqueArray;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles layout calculations for research graphs with parents centered over children.
 */
public class GraphLayoutManager {
	// Layout constants
	private static final int HORIZONTAL_SPACING = 10;
	private static final int VERTICAL_SPACING = 20;
	private static final int NODE_WIDTH = ResearchScreenWidget.PANEL_WIDTH;
	private static final int NODE_HEIGHT = ResearchScreenWidget.PANEL_HEIGHT;

	// Map from layer number to list of nodes in that layer (0 = bottom layer)
	public static Int2ObjectMap<List<ResearchNode>> layerMap = new Int2ObjectOpenHashMap<>();

	// Map to track which layer each node is assigned to
	public static Map<ResearchNode, Integer> nodeLayerMap = new HashMap<>();

	/**
	 * Apply layout to a research graph
	 *
	 * @param graph The research graph to layout
	 * @param offsetX Top-left X coordinate for the layout
	 * @param offsetY Top-left Y coordinate for the layout
	 */
	public static void applyLayout(ResearchGraph graph, int offsetX, int offsetY) {
		if (graph == null || graph.nodes().isEmpty()) {
			return;
		}

		nodeLayerMap.clear();
		layerMap.clear();

		_addRightToLayer(0, graph.rootNode()); // Add root node manually since only children get added
		calculateLayers(graph, graph.rootNode());

		// First step: Position nodes in each layer
		positionNodesInLayers(offsetX, offsetY);

		// Second step: Center nodes based on layer size
		centerNodesBasedOnLayers();

		// 2.5'th step: Sort the nodes in each layer based on their parents on the layer above
		sortNodesBasedOnParents();

		// Third step: Shifting Step 1/2;
		handleNodeShiftingFromAbove();

		// Fourth pass: Resolve any node overlaps
		resolveOverlaps(4);

		// Fifth step: Shifting Step 2/2;
		handleNodeShiftingFromBelow();

		// Final pass: Resolve any node overlaps #2
		resolveOverlaps(6);
	}

	public static int calculateDepth(ResearchNode node) {
		return _calculateDepth(node, 0);
	}

	public static Point centerOf2Nodes(ResearchNode node1, ResearchNode node2) {
		return new Point((node1.getX() + node2.getX() + NODE_WIDTH) / 2, (node1.getY() + node2.getY() + NODE_HEIGHT) / 2);
	}


	/**
	 * Utility method to stop the [0] and [size() - 1] spam
	 *
	 * @param nodes
	 * @return
	 */
	public static Point centerOf2Nodes(List<ResearchNode> nodes) {
		int MIN_X = nodes.stream().mapToInt(ResearchNode::getX).min().orElse(0);
		int MAX_X = nodes.stream().mapToInt(ResearchNode::getX).max().orElse(0) + NODE_WIDTH;
		int MIN_Y = nodes.stream().mapToInt(ResearchNode::getY).min().orElse(0);
		int MAX_Y = nodes.stream().mapToInt(ResearchNode::getY).max().orElse(0) + NODE_HEIGHT;

		return new Point((MIN_X + MAX_X) / 2, (MIN_Y + MAX_Y) / 2);
	}

	public static int widthOf(List<ResearchNode> nodes) {
		return nodes.size() * (NODE_WIDTH + HORIZONTAL_SPACING) - HORIZONTAL_SPACING;
	}

	private static int _calculateDepth(ResearchNode node, int depth) {
		if (node.getParents().isEmpty()) {
			return depth;
		} else {
			int maxDepth = depth;
			for (ResearchNode parent : node.getParents()) {
				maxDepth = Math.max(maxDepth, _calculateDepth(parent, depth + 1));
			}
			return maxDepth;
		}
	}

	// Thinking that [0] would be leftMost and [.length - 1] would be rightMost
	private static void _addLeftToLayer(int layer, ResearchNode node) {
		layerMap.computeIfAbsent(layer, k -> new ArrayList<>()).addFirst(node);
		nodeLayerMap.put(node, layer);
	}

	// Thinking that [0] would be leftMost and [.length - 1] would be rightMost
	private static void _addRightToLayer(int layer, ResearchNode node) {
		layerMap.computeIfAbsent(layer, k -> new ArrayList<>()).addLast(node);
		nodeLayerMap.put(node, layer);
	}

	/**
	 * Start from root, every child should be placed from center outwards on layer - 1.
	 */
	private static void calculateLayers(ResearchGraph graph, ResearchNode node) {
		UniqueArray<ResearchNode> children = node.getChildren();
		int size = children.size();

		/*
		* Logic here is to work from the center outwards by adding child by child recursively (down the tree first).
		*
		* If the children are even, we go directly to the left and right of the center splitting evenly,
		* otherwise take the center node out then resume the same logic as the even route.
		*/
		if (size % 2 == 0 ) {
			// Even number of children
			for (int i = 0; i < size / 2; i++) {
				ResearchNode child = children.get(size / 2 - 1 - i);
				_addRightToLayer(calculateDepth(child), child);
				calculateLayers(graph, child);
			}
			for (int i = size / 2; i < size; i++) {
				ResearchNode child = children.get(i);
				_addLeftToLayer(calculateDepth(child), child);
				calculateLayers(graph, child);
			}
		} else {
			// Odd number of children
			int center = size / 2;
			ResearchNode centerChild = children.get(center);
			_addLeftToLayer(calculateDepth(centerChild), centerChild); // Or Right doesn't matter
			calculateLayers(graph, centerChild);

			for (int i = 0; i < size / 2; i++) {
				ResearchNode child = children.get(size / 2 - 1 - i);
				_addRightToLayer(calculateDepth(child), child);
				calculateLayers(graph, child);
			}
			for (int i = size / 2 + 1; i < size; i++) {
				ResearchNode child = children.get(i);
				_addLeftToLayer(calculateDepth(child), child);
				calculateLayers(graph, child);
			}
		}
	}

	/**
	 * Position nodes in their layers (initial positioning)
	 */
	private static void positionNodesInLayers(int startX, int startY) {
		System.out.println("Step 1 - Positioning nodes in layers");
		for (Map.Entry<Integer, List<ResearchNode>> layer : layerMap.int2ObjectEntrySet()) {
			for (ResearchNode node : layer.getValue()) {
				// Set node position
				node.setYExt(startY + (layer.getKey() * (VERTICAL_SPACING + NODE_HEIGHT)));
				node.setXExt(startX + ((layer.getValue().indexOf(node) + 1) * (NODE_WIDTH + HORIZONTAL_SPACING)));
				System.out.println("Node: " + node.getInstance().getResearch() + " Layer: " + layer.getKey() + " X: " + node.getX() + " Y: " + node.getY());
			}
		}
	}

	/**
	 * Center parents over their children
	 */
	private static void centerNodesBasedOnLayers() {
		System.out.println("Step 2 - Repositioning nodes in layers");

		// Calculations
		int _maxNodes = 0;
		for (List<ResearchNode> nodesInLayer : layerMap.values()) {
			_maxNodes = Math.max(_maxNodes, nodesInLayer.size());
		}
		int _centerX = (_maxNodes * (NODE_WIDTH + HORIZONTAL_SPACING)) / 2;

		// Process each layer
		for (Integer layerNum : layerMap.keySet()) {
			List<ResearchNode> nodesInLayer = layerMap.get(layerNum);

			// Calculate total width of this layer
			int totalWidth = (nodesInLayer.size() * NODE_WIDTH) +
				((nodesInLayer.size() - 1) * HORIZONTAL_SPACING);

			// Calculate starting X position to center the layer
			int startX = _centerX - (totalWidth / 2);

			// Position all nodes in this layer
			for (int i = 0; i < nodesInLayer.size(); i++) {
				ResearchNode node = nodesInLayer.get(i);
				node.setXExt(startX + (i * (NODE_WIDTH + HORIZONTAL_SPACING)));
			}
		}

		for (Map.Entry<Integer, List<ResearchNode>> layer : layerMap.int2ObjectEntrySet()) {
			for (ResearchNode node : layer.getValue()) {
				System.out.println("Node: " + node.getInstance().getResearch() + " Layer: " + layer.getKey() + " X: " + node.getX() + " Y: " + node.getY());
			}
		}
	}

	/**
	 * Gets every parent on the layer above, then literally puts each node in order for their parents <br><br>
	 *
	 * Iteration #11: 25-May-2025 - Please God, may this be the last refactor of this method.<br>
	 */
	private static void sortNodesBasedOnParents() {
		System.out.println("Step 2.5 - Sorting nodes based on parents");

		List<Integer> layerNumbers = new ArrayList<>(layerMap.keySet());
		layerNumbers.sort(Comparator.naturalOrder()); // Ascending order
		layerNumbers.removeLast(); // Remove the last layer since it has no children

		for (Integer layer : layerNumbers) {
			System.out.println("Sorting layer: " + (layer + 1));
			List<ResearchNode> layerNodes = layerMap.get(layer);
			HashMap<ResearchNode, UniqueArray<ResearchNode>> subsequentChildrenMap = new HashMap<>();

			for (ResearchNode node : layerNodes) {
				// Calculations
				System.out.println("Checking: " + node.getInstance().getResearch());

				UniqueArray<ResearchNode> children = node.getChildren();

				// One layer below, sorted by X position
				UniqueArray<ResearchNode> subsequentChildren = children.stream().filter((child) -> child.getLayer() == node.getLayer() + 1).collect(Collectors.toCollection(UniqueArray::new));
				subsequentChildren.sort(Comparator.comparingInt(ResearchNode::getX));
				subsequentChildrenMap.put(node, subsequentChildren);
			}

			int currentNodeIdx = 0;
			int firstNodeX = layerMap.get(layer + 1).getFirst().getX();

		}
	}

	/**
	 * General node movement for better coherent layout following the 𝒫𝑜𝓈𝒾𝓉𝒾𝑜𝓃𝒾𝓃𝑔 𝓇𝓊𝓁𝑒𝓈 <br><br>
	 *
	 * Iteration #11: 25-May-2025 - Please God, may this be the last refactor of this method.<br>
	 */
	private static void handleNodeShiftingFromAbove() {
		System.out.println("Step 3 - Node shifting from above");
		List<Integer> layerNumbers = new ArrayList<>(layerMap.keySet());
		layerNumbers.sort(Comparator.naturalOrder()); // Start from the top

		for (Integer layer : layerNumbers) {
			List<ResearchNode> nodes = layerMap.get(layer);

			for (ResearchNode node : nodes) {
				if (!node.shouldMove()) continue;

				// Calculations
				System.out.println("Checking: " + node.getInstance().getResearch());

				UniqueArray<ResearchNode> parents = node.getParents();
				UniqueArray<ResearchNode> children = node.getChildren();

				// One layer below, sorted by X position
				UniqueArray<ResearchNode> subsequentChildren = children.stream().filter((child) -> child.getLayer() == node.getLayer() + 1).collect(Collectors.toCollection(UniqueArray::new));
				subsequentChildren.sort(Comparator.comparingInt(ResearchNode::getX));

				// One layer above, sorted by X position
				UniqueArray<ResearchNode> parentsOnLayerAbove = parents.stream().filter(parent -> parent.getLayer() == node.getLayer() + 1).collect(Collectors.toCollection(UniqueArray::new));
				parentsOnLayerAbove.sort(Comparator.comparingInt(ResearchNode::getX));

				// Apply

				// If the node's subsequent children only have this one node as parent, we shift the node to their center
				if (subsequentChildren.stream().filter(child -> child.getParents().size() != 1).toList().isEmpty()) {
					node.setXExt(centerOf2Nodes(subsequentChildren).x - NODE_WIDTH / 2);

					subsequentChildren.forEach(child -> child.lockNodeTo(node));

					System.out.println("Shifted node: " + node.getInstance().getResearch() + " to center of its children: " + subsequentChildren.stream().map(ResearchNode::getInstance).map(instance -> instance.getResearch().toString()).collect(Collectors.joining(", ")));
					continue;
				}
			}
		}

		for (Map.Entry<Integer, List<ResearchNode>> layer : layerMap.int2ObjectEntrySet()) {
			for (ResearchNode node : layer.getValue()) {
				System.out.println("Node: " + node.getInstance().getResearch() + " Layer: " + layer.getKey() + " X: " + node.getX() + " Y: " + node.getY() + " Pos locks: " + node.getPositionLocks().size());
			}
		}
	}

	/**
	 * General node movement for better coherent layout following the 𝒫𝑜𝓈𝒾𝓉𝒾𝑜𝓃𝒾𝓃𝑔 𝓇𝓊𝓁𝑒𝓈 <br><br>
	 *
	 * Iteration #11: 25-May-2025 - Please God, may this be the last refactor of this method.<br>
	 */
	private static void handleNodeShiftingFromBelow() {
		System.out.println("Step 5 - Node shifting from below");
		List<Integer> layerNumbers = new ArrayList<>(layerMap.keySet());
		layerNumbers.sort((a, b) -> Integer.compare(b, a)); // Start from the bottom layer

		for (Integer layer : layerNumbers) {
			List<ResearchNode> nodes = layerMap.get(layer);

			for (ResearchNode node : nodes) {
				if (!node.shouldMove()) continue;

				// Calculations
				System.out.println("Checking: " + node.getInstance().getResearch());

				UniqueArray<ResearchNode> parents = node.getParents();
				UniqueArray<ResearchNode> children = node.getChildren();

				HashMap<Integer, UniqueArray<ResearchNode>> parentToSubsequentChildren = new HashMap<>();
				UniqueArray<ResearchNode> parentsOnLayerBelow = parents.stream().filter(parent -> parent.getLayer() == node.getLayer() - 1).collect(Collectors.toCollection(UniqueArray::new));

				int _parentIndex = 0;
				for (ResearchNode parent : parents) {
					UniqueArray<ResearchNode> _children = new UniqueArray<>(parent.getChildren());
					UniqueArray<ResearchNode> _subsequentChildren = new UniqueArray<>(_children.stream().filter(child -> child.getLayer() == parent.getLayer() - 1).toList());

					parentToSubsequentChildren.put(_parentIndex, _subsequentChildren);
					_parentIndex++;
				}

				if (children.isEmpty()) {
					continue;
				}
			}
		}

		for (Map.Entry<Integer, List<ResearchNode>> layer : layerMap.int2ObjectEntrySet()) {
			for (ResearchNode node : layer.getValue()) {
				System.out.println("Node: " + node.getInstance().getResearch() + " Layer: " + layer.getKey() + " X: " + node.getX() + " Y: " + node.getY() + " Pos locks: " + node.getPositionLocks().size());
			}
		}
	}

	/**
	 * Resolve node overlaps by shifting
	 */
	private static void resolveOverlaps(int step) {
		System.out.println("Step %d - Resolving overlaps".formatted(step));

		for (Map.Entry<Integer, List<ResearchNode>> layer : layerMap.int2ObjectEntrySet()) {
			for (ResearchNode node : layer.getValue()) {
				System.out.println("Node: " + node.getInstance().getResearch() + " Layer: " + layer.getKey() + " X: " + node.getX() + " Y: " + node.getY() + " Pos locks: " + node.getPositionLocks().size());
			}
		}
	}
}

