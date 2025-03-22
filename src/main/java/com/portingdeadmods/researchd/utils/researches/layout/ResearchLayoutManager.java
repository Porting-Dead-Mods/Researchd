package com.portingdeadmods.researchd.utils.researches.layout;

import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.*;

/**
 * Handles layout calculations for research graphs with parents centered over children.
 */
public class ResearchLayoutManager {
	// Layout constants
	private static final int HORIZONTAL_SPACING = 20;
	private static final int VERTICAL_SPACING = 30;
	private static final int NODE_WIDTH = ResearchScreenWidget.PANEL_WIDTH;
	private static final int NODE_HEIGHT = ResearchScreenWidget.PANEL_HEIGHT;

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

		// Calculate node layers (bottom-up approach)
		Int2ObjectMap<List<ResearchNode>> layerMap = calculateLayers(graph);

		// First pass: Position nodes in each layer
		positionNodesInLayers(layerMap, offsetX, offsetY);

		// Second pass: Center parents over their children
		centerParentsOverChildren(layerMap);

		// Final pass: Resolve any node overlaps
		resolveOverlaps(layerMap);
	}

	/**
	 * Calculate which layer each node belongs to using a bottom-up approach.
	 * A node's layer is determined by the maximum layer of its children + 1.
	 */
	private static Int2ObjectMap<List<ResearchNode>> calculateLayers(ResearchGraph graph) {
		// Map from layer number to list of nodes in that layer (0 = bottom layer)
		Int2ObjectMap<List<ResearchNode>> layerMap = new Int2ObjectOpenHashMap<>();

		// Map to track which layer each node is assigned to
		Map<ResearchNode, Integer> nodeLayerMap = new HashMap<>();

		// First, find all leaf nodes (nodes with no children)
		List<ResearchNode> leafNodes = new ArrayList<>();
		for (ResearchNode node : graph.nodes()) {
			if (node.getChildren().isEmpty()) {
				leafNodes.add(node);
			}
		}

		// Start with leaf nodes at layer 0 (bottom)
		for (ResearchNode leaf : leafNodes) {
			assignNodeToLayer(leaf, 0, nodeLayerMap, layerMap);
		}

		// Process remaining nodes (working up the tree)
		Set<ResearchNode> processed = new HashSet<>(leafNodes);
		Set<ResearchNode> remaining = new HashSet<>(graph.nodes());
		remaining.removeAll(processed);

		// Continue until all nodes are assigned layers
		while (!remaining.isEmpty()) {
			Set<ResearchNode> nextBatch = new HashSet<>();

			// Find nodes where all children have been processed
			for (ResearchNode node : remaining) {
				boolean allChildrenProcessed = true;
				for (ResearchNode child : node.getChildren()) {
					if (!processed.contains(child)) {
						allChildrenProcessed = false;
						break;
					}
				}

				if (allChildrenProcessed) {
					// Calculate layer based on children's layers
					int maxChildLayer = -1;
					for (ResearchNode child : node.getChildren()) {
						maxChildLayer = Math.max(maxChildLayer, nodeLayerMap.getOrDefault(child, 0));
					}

					// Place node one layer above its highest child
					int nodeLayer = maxChildLayer + 1;
					assignNodeToLayer(node, nodeLayer, nodeLayerMap, layerMap);
					nextBatch.add(node);
				}
			}

			// If we couldn't process any nodes, we might have a cycle
			if (nextBatch.isEmpty() && !remaining.isEmpty()) {
				// Handle any remaining nodes (place them at a default layer)
				for (ResearchNode node : remaining) {
					assignNodeToLayer(node, 1, nodeLayerMap, layerMap);
				}
				break;
			}

			// Update processed sets
			processed.addAll(nextBatch);
			remaining.removeAll(nextBatch);
		}

		return layerMap;
	}

	/**
	 * Assign a node to a specific layer
	 */
	private static void assignNodeToLayer(ResearchNode node, int layer,
	                                      Map<ResearchNode, Integer> nodeLayerMap,
	                                      Int2ObjectMap<List<ResearchNode>> layerMap) {
		// Assign node to layer
		nodeLayerMap.put(node, layer);

		// Add to layer map
		layerMap.computeIfAbsent(layer, k -> new ArrayList<>()).add(node);
	}

	/**
	 * Position nodes in their layers (initial positioning)
	 */
	private static void positionNodesInLayers(Int2ObjectMap<List<ResearchNode>> layerMap, int startX, int startY) {
		// Get all layer numbers sorted (lowest first - furthest from root)
		List<Integer> layerNumbers = new ArrayList<>(layerMap.keySet());
		Collections.sort(layerNumbers); // Ascending order - bottom to top

		// Position nodes in each layer, starting from the bottom
		int currentY = startY + layerNumbers.size() * (NODE_HEIGHT + VERTICAL_SPACING);

		for (int layerNum : layerNumbers) {
			List<ResearchNode> nodesInLayer = layerMap.get(layerNum);

			// Position this layer
			currentY -= (NODE_HEIGHT + VERTICAL_SPACING);

			// Initialize X position
			int currentX = startX;

			// Position each node in this layer
			for (ResearchNode node : nodesInLayer) {
				// Set node position
				node.setXExt(currentX);
				node.setYExt(currentY);

				// Move to next position
				currentX += NODE_WIDTH + HORIZONTAL_SPACING;
			}
		}
	}

	/**
	 * Center parents over their children
	 */
	private static void centerParentsOverChildren(Int2ObjectMap<List<ResearchNode>> layerMap) {
		// Get all layer numbers sorted (highest first - closest to root)
		List<Integer> layerNumbers = new ArrayList<>(layerMap.keySet());
		Collections.sort(layerNumbers, Collections.reverseOrder()); // Descending order - top to bottom

		// Skip the bottom layer as it has no children
		if (!layerNumbers.isEmpty()) {
			layerNumbers.remove(layerNumbers.size() - 1);
		}

		// Process each layer (except bottom layer)
		for (int layerNum : layerNumbers) {
			List<ResearchNode> nodesInLayer = layerMap.get(layerNum);

			// Center each node over its children
			for (ResearchNode node : nodesInLayer) {
				// Skip nodes with no children
				if (node.getChildren().isEmpty()) {
					continue;
				}

				// Calculate center X position of all children
				double avgChildX = calculateAverageChildX(node);

				// Center node over children
				int newX = (int) (avgChildX - (NODE_WIDTH / 2));
				node.setXExt(newX);
			}
		}
	}

	/**
	 * Calculate the average X center position of a node's children
	 */
	private static double calculateAverageChildX(ResearchNode node) {
		if (node.getChildren().isEmpty()) {
			return node.getX() + (NODE_WIDTH / 2);
		}

		// Calculate sum of center X positions
		double sumX = 0;
		for (ResearchNode child : node.getChildren()) {
			sumX += (child.getX() + (NODE_WIDTH / 2));
		}

		// Return average
		return sumX / node.getChildren().size();
	}

	/**
	 * Resolve node overlaps by shifting
	 */
	private static void resolveOverlaps(Int2ObjectMap<List<ResearchNode>> layerMap) {
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