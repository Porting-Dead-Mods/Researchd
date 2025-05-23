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

		// Third step: Apply node positioning logic
		doNodeShifting();

		// Final pass: Resolve any node overlaps
		resolveOverlaps();
	}

	public static int calculateDepth(ResearchNode node) {
		return _calculateDepth(node, 0);
	}

	public static Point centerOf2Nodes(ResearchNode node1, ResearchNode node2) {
		return new Point((node1.getX() + node2.getX() + NODE_WIDTH) / 2, (node1.getY() + node2.getY() + NODE_HEIGHT) / 2);
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
		layerMap.computeIfAbsent(layer, k -> new ArrayList<>()).addLast(node);
		nodeLayerMap.put(node, layer);
	}

	// Thinking that [0] would be leftMost and [.length - 1] would be rightMost
	private static void _addRightToLayer(int layer, ResearchNode node) {
		layerMap.computeIfAbsent(layer, k -> new ArrayList<>()).addFirst(node);
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
	 * General node movement for better coherent layout following the 𝒫𝑜𝓈𝒾𝓉𝒾𝑜𝓃𝒾𝓃𝑔 𝓇𝓊𝓁𝑒𝓈<br><br>
	 *
	 * Note: Any change made to a node's position should be downstreamed to all the other nodes
	 * locked to the node's position per the other 𝒫𝑜𝓈𝒾𝓉𝒾𝑜𝓃𝒾𝓃𝑔 𝓇𝓊𝓁𝑒𝓈. (So if a node is linked
	 * to it's parents, and a change is done to the parents, then the position should be refreshed,
	 * or if the children are directly tied to a node's X, moving that node should move the children.
	 * A field should be implemented in the ResearchNode class to set the tied nodes and rule.)
	 */
	private static void doNodeShifting() {
		System.out.println("Step 3 - Node shifting");
		List<Integer> layerNumbers = new ArrayList<>(layerMap.keySet());
		Collections.sort(layerNumbers, (a, b) -> Integer.compare(b, a)); // Start from the bottom layer

		for (Integer layer : layerNumbers) {
			List<ResearchNode> nodes = layerMap.get(layer);

			for (ResearchNode node : nodes) {
				if (!node.shouldMove()) continue;

				// Calculations
				System.out.println("Checking: " + node.getInstance().getResearch());

				UniqueArray<ResearchNode> parents = node.getParents();
				UniqueArray<ResearchNode> children = node.getChildren();

				HashMap<Integer, UniqueArray<ResearchNode>> parentToSubsequentChildren = new HashMap<>();
				UniqueArray<ResearchNode> parentsOnLayerAbove = parents.stream().filter(parent -> parent.getLayer() == node.getLayer() + 1).collect(Collectors.toCollection(UniqueArray::new));

				int _parentIndex = 0;
				for (ResearchNode parent : parents) {
					UniqueArray<ResearchNode> _children = new UniqueArray<>(parent.getChildren());
					UniqueArray<ResearchNode> _subsequentChildren = new UniqueArray<>(_children.stream().filter(child -> child.getLayer() == parent.getLayer() + 1).toList());

					parentToSubsequentChildren.put(_parentIndex, _subsequentChildren);
					_parentIndex++;
				}

				// Apply

				if (children.isEmpty()) {
					
				}

//				// A: If the node doesn't have children, move the node to the parents - APPLIES ONLY TO SUBSEQUENT LAYERS
//				if (!parentsOnLayerAbove.isEmpty() && children.isEmpty()) {
//					parentsOnLayerAbove.sort(Comparator.comparingInt(ResearchNode::getX));
//					node.downStreamSetX(centerOf2Nodes(parentsOnLayerAbove.getFirst(), parentsOnLayerAbove.getLast()).x - NODE_WIDTH / 2);
//					for (ResearchNode parent : parentsOnLayerAbove) {
//						parent.lockNodeTo(node);
//					}
//				}
//
//				// B: If the node has only one parent and child, lock to the child
//				if (parents.size() == 1 && children.size() == 1) {
//					node.downStreamSetX(children.get(0).getX());
//					node.lockNodeTo(children.get(0));
//				}
//
//				if (parents.size() == 1) {
//					ResearchNode parent = parents.get(0);
//					List<ResearchNode> subsequentChildren = parentToSubsequentChildren.get(0);
//					subsequentChildren.sort(Comparator.comparingInt(ResearchNode::getX));
//
//					boolean checkA = true;
//					for (ResearchNode child : subsequentChildren) {
//						if (!(child.getParents().size() == 1)) {
//							checkA = false;
//							break;
//						}
//					}
//					if (checkA) {
//						int childrenWidth = subsequentChildren.size() * NODE_WIDTH + (subsequentChildren.size() - 1) * HORIZONTAL_SPACING;
//						int startX = parent.getX() + (NODE_WIDTH / 2) - (childrenWidth / 2);
//
//						for (ResearchNode child : subsequentChildren) {
//							child.lockNodeTo(parent);
//						}
//						for (int i = 0; i < subsequentChildren.size(); i++) {
//							ResearchNode child = subsequentChildren.get(i);
//							child.setXExt(startX + (i * (NODE_WIDTH + HORIZONTAL_SPACING)));
//							System.out.println("A: Moved child: " + child.getInstance().getResearch() + " to parent: " + parent.getInstance().getResearch());
//						}
//						continue;
//					}
//				}
//
//				// B:
//				if (parents.size() == 1) {
//					parents.sort(Comparator.comparingInt(ResearchNode::getX));
//
//					node.setXExt(centerOf2Nodes(parents.getFirst(), parents.getLast()).x - NODE_WIDTH / 2);
//
//					for (ResearchNode parent : parents) {
//						parent.lockNodeTo(node);
//					}
//					node.lockNode();
//					System.out.println("B: Moved node: " + node.getInstance().getResearch() + " to parents: " + parents.getFirst().getInstance().getResearch() + " and " + parents.getLast().getInstance().getResearch());
//					continue;
//				}
//
//				// C:
//				if (node.getParents().size() == 1) {
//					if (node.getParents().get(0).getChildren())
//					ResearchNode parent = node.getParents().get(0);
//					node.setXExt(parent.getX());
//					node.lockNodeTo(parent);
//
//					System.out.println("Moved child: " + node.getInstance().getResearch() + " to parent: " + parent.getInstance().getResearch());
//					continue;
//				}
			}
		}

//		for (Map.Entry<Integer, List<ResearchNode>> layer : layerMap.int2ObjectEntrySet()) {
//			for (ResearchNode node : layer.getValue()) {
//				System.out.println("Node: " + node.getInstance().getResearch() + " Layer: " + layer.getKey() + " X: " + node.getX() + " Y: " + node.getY() + " Pos locks: " + node.getPositionLocks().size());
//			}
//		}
	}

	/**
	 * Resolve node overlaps by shifting
	 */
	private static void resolveOverlaps() {
		// Get all layer numbers sorted
		List<Integer> layerNumbers = new ArrayList<>(layerMap.keySet());
		Collections.sort(layerNumbers);

		// Process each layer
		for (int layerNum : layerNumbers) {
			List<ResearchNode> nodesInLayer = layerMap.get(layerNum);

			// Sort nodes by X position
			nodesInLayer.sort(Comparator.comparingInt(ResearchNode::getX));

			// Check for overlaps
			for (int i = 1; i < nodesInLayer.size(); i++) {
				ResearchNode leftNode = nodesInLayer.get(i - 1);
				ResearchNode rightNode = nodesInLayer.get(i);

				int leftEdge = leftNode.getX() + NODE_WIDTH;
				int rightEdge = rightNode.getX();
				int minSpace = HORIZONTAL_SPACING / 2;

				// If nodes overlap or are too close
				if (rightEdge - leftEdge < minSpace) {
					// Calculate adjustment needed
					int adjustment = minSpace - (rightEdge - leftEdge);

					// Shift the right node and all subsequent nodes
					for (int j = i; j < nodesInLayer.size(); j++) {
						ResearchNode nodeToShift = nodesInLayer.get(j);
						nodeToShift.setXExt(nodeToShift.getX() + adjustment);
					}
				}
			}
		}
	}
}

