package com.portingdeadmods.researchd.client.screens.research.graph;

import com.portingdeadmods.researchd.api.client.ResearchGraph;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;

import java.util.*;

/**
 * TW: This is not for the faint of heart
 *
 * Sugiyama-style layered graph layout with 4 phases:
 *
 * <h3>Phase 1 — Layer Assignment</h3>
 * Topological sort (Kahn's algorithm). Each node's layer = max(parent layers) + 1.
 * Root is layer 0, children go deeper. Guarantees all parents are above their children.
 *
 * <h3>Phase 2 — Crossing Minimization</h3>
 * Barycenter heuristic: for each layer, position each node at the average index of its
 * connected nodes in the adjacent layer, then sort. Alternates top-down and bottom-up
 * passes (3 iterations) to converge on a low-crossing ordering.
 *
 * <h3>Phase 3 — X Coordinate Assignment</h3>
 * Assigns X positions from the ordering in Phase 2 with guaranteed minimum spacing.
 * Then centers parents over their children's midpoint and resolves any resulting overlaps
 * with a left-to-right scan. Finally centers the whole graph.
 *
 * <h3>Phase 4 — Y Coordinate Assignment (dynamic spacing)</h3>
 * Computes the number of routing channels needed between each pair of adjacent layers
 * (one channel per non-straight edge). Y positions are set so the gap between layers
 * grows to accommodate channels. Channel Y positions are stored in {@link LayoutResult}
 * for the edge router in {@code ResearchGraphWidget}.
 *
 * <h3>Edge routing</h3>
 * Done externally by {@code ResearchGraphWidget.calculateLines()}, which reads the
 * channel assignments from {@link LayoutResult}. Each non-straight edge gets its own
 * horizontal channel (1px line, 1px gap) — Factorio style.
 */
public class GraphLayoutManager {
	// Sizes
	public static final int NODE_WIDTH = ResearchScreenWidget.PANEL_WIDTH;   // 20
	public static final int NODE_HEIGHT = ResearchScreenWidget.PANEL_HEIGHT; // 24
	public static final int HORIZONTAL_SPACING = 10;

	// Vertical layout: gap between layers is HEAD_GAP + channels + HEAD_GAP
	public static final int HEAD_GAP = 6;        // 3px head stub + 3px padding
	public static final int CHANNEL_SIZE = 2;   // 1px line + 1px gap per channel
	public static final int MIN_VERTICAL_GAP = HEAD_GAP * 2 + 2; // minimum gap when 0 channels

	/**
	 * Result of layout computation. Consumed by the edge router.
	 */
	public static class LayoutResult {
		/** Ordered nodes per layer (index = layer number). */
		public final List<List<ResearchNode>> layers;
		/** Y position of each layer's nodes. */
		public final int[] layerY;
		/** Number of routing channels between layer i and layer i+1. Index i corresponds to zone between layer i and i+1. */
		public final int[] channelsPerZone;
		/** Y position of channel 0 in each routing zone. Channels go downward: channelY = zoneBaseY[i] + channelIndex * CHANNEL_PITCH. */
		public final int[] zoneBaseY;

		/** For each edge (parent→child), which channel it's assigned in each routing zone it passes through.
		 *  Key = edgeKey(parent, child), Value = map from zone index to channel index. */
		public final Map<Long, Map<Integer, Integer>> edgeChannelAssignments;

		public LayoutResult(List<List<ResearchNode>> layers, int[] layerY, int[] channelsPerZone, int[] zoneBaseY, Map<Long, Map<Integer, Integer>> edgeChannelAssignments) {
			this.layers = layers;
			this.layerY = layerY;
			this.channelsPerZone = channelsPerZone;
			this.zoneBaseY = zoneBaseY;
			this.edgeChannelAssignments = edgeChannelAssignments;
		}
	}

	/**
	 * Applies Sugiyama layout to the graph and returns routing info for the edge router.
	 */
	public static LayoutResult applyLayout(ResearchGraph graph, int offsetX, int offsetY) {
		if (graph == null || graph.nodes().isEmpty()) {
			return new LayoutResult(List.of(), new int[0], new int[0], new int[0], Map.of());
		}

		Collection<ResearchNode> allNodes = graph.nodes().values();

		// Phase 1: Layer assignment
		Map<ResearchNode, Integer> nodeToLayer = assignLayers(allNodes);
		List<List<ResearchNode>> layers = buildLayerLists(nodeToLayer);

		// Set layer field on each node
		for (var entry : nodeToLayer.entrySet()) {
			entry.getKey().setLayer(entry.getValue());
		}

		// Phase 2: Crossing minimization (barycenter, 3 iterations)
		minimizeCrossings(layers, 3);

		// Phase 3: X coordinate assignment
		assignXCoordinates(layers, offsetX);

		// Phase 4: Compute channels and Y coordinates
		int numZones = Math.max(0, layers.size() - 1);
		int[] channelsPerZone = new int[numZones];
		Map<Long, Map<Integer, Integer>> edgeChannelAssignments = new HashMap<>();

		// First pass: count channels needed per zone and assign them
		for (int zone = 0; zone < numZones; zone++) {
			assignChannelsForZone(zone, layers, channelsPerZone, edgeChannelAssignments);
		}

		// Compute Y positions
		int[] layerY = new int[layers.size()];
		layerY[0] = offsetY;
		for (int i = 1; i < layers.size(); i++) {
			int gapForChannels = channelsPerZone[i - 1] * CHANNEL_SIZE;
			int totalGap = Math.max(MIN_VERTICAL_GAP, HEAD_GAP * 2 + gapForChannels);
			layerY[i] = layerY[i - 1] + NODE_HEIGHT + totalGap;
		}

		// Compute zone base Y (where channel 0 starts in each zone)
		int[] zoneBaseY = new int[numZones];
		for (int zone = 0; zone < numZones; zone++) {
			// Zone starts after the parent layer's bottom + head gap
			zoneBaseY[zone] = layerY[zone] + NODE_HEIGHT + HEAD_GAP;
		}

		// Apply Y positions to nodes
		for (int layerIdx = 0; layerIdx < layers.size(); layerIdx++) {
			for (ResearchNode node : layers.get(layerIdx)) {
				node.setYExt(layerY[layerIdx]);
			}
		}

		return new LayoutResult(layers, layerY, channelsPerZone, zoneBaseY, edgeChannelAssignments);
	}

	// ========================
	// Phase 1: Layer Assignment
	// ========================

	/**
	 * Assigns layers via topological sort (Kahn's algorithm).
	 * Each node's layer = max(parent layers) + 1. Root nodes (no parents) = layer 0.
	 */
	private static Map<ResearchNode, Integer> assignLayers(Collection<ResearchNode> nodes) {
		Map<ResearchNode, Integer> layerOf = new HashMap<>();
		Map<ResearchNode, Integer> inDegree = new HashMap<>();
		Deque<ResearchNode> queue = new ArrayDeque<>();

		for (ResearchNode node : nodes) {
			int parentCount = 0;
			for (ResearchNode parent : node.getParents()) {
				if (nodes.contains(parent)) parentCount++;
			}
			inDegree.put(node, parentCount);
			if (parentCount == 0) {
				queue.add(node);
				layerOf.put(node, 0);
			}
		}

		while (!queue.isEmpty()) {
			ResearchNode node = queue.poll();
			int nodeLayer = layerOf.get(node);

			for (ResearchNode child : node.getChildren()) {
				if (!inDegree.containsKey(child)) continue; // not in this graph

				// Child's layer = max of all parent layers + 1
				int childLayer = Math.max(layerOf.getOrDefault(child, 0), nodeLayer + 1);
				layerOf.put(child, childLayer);

				int remaining = inDegree.get(child) - 1;
				inDegree.put(child, remaining);
				if (remaining == 0) {
					queue.add(child);
				}
			}
		}

		// Safety: assign any unvisited nodes to layer 0 (shouldn't happen in valid DAG)
		for (ResearchNode node : nodes) {
			layerOf.putIfAbsent(node, 0);
		}

		return layerOf;
	}

	/**
	 * Builds ordered layer lists from the layer assignment map.
	 */
	private static List<List<ResearchNode>> buildLayerLists(Map<ResearchNode, Integer> nodeToLayer) {
		int maxLayer = 0;
		for (int layer : nodeToLayer.values()) {
			maxLayer = Math.max(maxLayer, layer);
		}

		List<List<ResearchNode>> layers = new ArrayList<>(maxLayer + 1);
		for (int i = 0; i <= maxLayer; i++) {
			layers.add(new ArrayList<>());
		}

		for (var entry : nodeToLayer.entrySet()) {
			layers.get(entry.getValue()).add(entry.getKey());
		}

		return layers;
	}

	// ================================
	// Phase 2: Crossing Minimization
	// ================================

	/**
	 * Barycenter heuristic: alternates top-down and bottom-up sweeps.
	 * Each sweep reorders a layer by the average position of connected nodes in the adjacent layer.
	 */
	private static void minimizeCrossings(List<List<ResearchNode>> layers, int iterations) {
		for (int iter = 0; iter < iterations; iter++) {
			// Top-down: order each layer by average parent position in layer above
			for (int i = 1; i < layers.size(); i++) {
				List<ResearchNode> aboveLayer = layers.get(i - 1);
				reorderByBarycenter(layers.get(i), aboveLayer, true);
			}

			// Bottom-up: order each layer by average child position in layer below
			for (int i = layers.size() - 2; i >= 0; i--) {
				List<ResearchNode> belowLayer = layers.get(i + 1);
				reorderByBarycenter(layers.get(i), belowLayer, false);
			}
		}
	}

	/**
	 * Reorders {@code layer} so that each node is positioned near the barycenter of its
	 * connected nodes in {@code adjacentLayer}.
	 *
	 * @param useParents true = look at parents (top-down), false = look at children (bottom-up)
	 */
	private static void reorderByBarycenter(List<ResearchNode> layer, List<ResearchNode> adjacentLayer, boolean useParents) {
		// Build position index for adjacent layer
		Map<ResearchNode, Integer> positionOf = new HashMap<>();
		for (int i = 0; i < adjacentLayer.size(); i++) {
			positionOf.put(adjacentLayer.get(i), i);
		}

		// Compute barycenter for each node
		Map<ResearchNode, Double> barycenter = new HashMap<>();
		for (ResearchNode node : layer) {
			var connected = useParents ? node.getParents() : node.getChildren();
			double sum = 0;
			int count = 0;
			for (ResearchNode adj : connected) {
				Integer pos = positionOf.get(adj);
				if (pos != null) {
					sum += pos;
					count++;
				}
			}
			// Nodes with no connections keep their current index as barycenter
			barycenter.put(node, count > 0 ? sum / count : (double) layer.indexOf(node));
		}

		layer.sort(Comparator.comparingDouble(barycenter::get));
	}

	// ==============================
	// Phase 3: X Coordinate Assignment
	// ==============================

	/**
	 * Assigns X positions based on layer ordering, then centers parents over children.
	 */
	private static void assignXCoordinates(List<List<ResearchNode>> layers, int offsetX) {
		// Initial assignment: sequential by order index
		for (List<ResearchNode> layer : layers) {
			for (int i = 0; i < layer.size(); i++) {
				layer.get(i).setXExt(offsetX + i * (NODE_WIDTH + HORIZONTAL_SPACING));
			}
		}

		// Center parents over their children (top-down)
		for (int layerIdx = 0; layerIdx < layers.size() - 1; layerIdx++) {
			for (ResearchNode parent : layers.get(layerIdx)) {
				List<ResearchNode> visibleChildren = new ArrayList<>();
				for (ResearchNode child : parent.getChildren()) {
					if (child.getLayer() >= 0) visibleChildren.add(child);
				}
				if (visibleChildren.isEmpty()) continue;

				// Compute children midpoint
				int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
				for (ResearchNode child : visibleChildren) {
					minX = Math.min(minX, child.getX());
					maxX = Math.max(maxX, child.getX() + NODE_WIDTH);
				}
				int childrenCenter = (minX + maxX) / 2;
				int desiredX = childrenCenter - NODE_WIDTH / 2;
				parent.setXExt(desiredX);
			}

			// Resolve overlaps in this layer after centering
			resolveOverlapsInLayer(layers.get(layerIdx));
		}

		// Center the whole graph horizontally around offsetX
		centerGraph(layers, offsetX);
	}

	/**
	 * Scans left-to-right and pushes overlapping nodes to the right.
	 */
	private static void resolveOverlapsInLayer(List<ResearchNode> layer) {
		// Current X first
		layer.sort(Comparator.comparingInt(ResearchNode::getX));

		for (int i = 1; i < layer.size(); i++) {
			ResearchNode prev = layer.get(i - 1);
			ResearchNode curr = layer.get(i);
			int minX = prev.getX() + NODE_WIDTH + HORIZONTAL_SPACING;
			if (curr.getX() < minX) {
				curr.setXExt(minX);
			}
		}
	}

	/**
	 * Centers all layers so the graph midpoint aligns with offsetX.
	 */
	private static void centerGraph(List<List<ResearchNode>> layers, int offsetX) {
		int globalMinX = Integer.MAX_VALUE;
		int globalMaxX = Integer.MIN_VALUE;

		for (List<ResearchNode> layer : layers) {
			for (ResearchNode node : layer) {
				globalMinX = Math.min(globalMinX, node.getX());
				globalMaxX = Math.max(globalMaxX, node.getX() + NODE_WIDTH);
			}
		}

		if (globalMinX == Integer.MAX_VALUE) return;

		int graphCenter = (globalMinX + globalMaxX) / 2;
		int dx = offsetX - graphCenter;
		for (List<ResearchNode> layer : layers) {
			for (ResearchNode node : layer) {
				node.setXExt(node.getX() + dx);
			}
		}
	}

	// ======================================
	// Phase 4: Channel Assignment per Zone
	// ======================================

	/**
	 * For a routing zone between layer[zone] and layer[zone+1], determines which edges
	 * pass through, assigns each non-straight edge a unique channel, and records the count.
	 */
	private static void assignChannelsForZone(
			int zone,
			List<List<ResearchNode>> layers,
			int[] channelsPerZone,
			Map<Long, Map<Integer, Integer>> edgeChannelAssignments
	) {
		// Collect all edges passing through this zone
		// An edge (parent → child) passes through zone i if parent.layer <= i and child.layer > i
		List<long[]> edgesInZone = new ArrayList<>(); // [edgeKey, horizontalSpan]

		for (int layerIdx = 0; layerIdx <= zone; layerIdx++) {
			for (ResearchNode parent : layers.get(layerIdx)) {
				for (ResearchNode child : parent.getChildren()) {
					if (child.getLayer() > zone) {
						int parentCenterX = parent.getX() + NODE_WIDTH / 2;
						int childCenterX = child.getX() + NODE_WIDTH / 2;
						int span = Math.abs(parentCenterX - childCenterX);

						// Straight edges (same X center) don't need a channel
						if (span == 0) continue;

						long key = edgeKey(parent, child);
						edgesInZone.add(new long[]{key, span, parentCenterX, childCenterX});
					}
				}
			}
		}

		// Sort by horizontal span (shortest first — greedy assignment)
		edgesInZone.sort(Comparator.comparingLong(e -> e[1]));

		int channelCount = 0;
		for (long[] edge : edgesInZone) {
			long key = edge[0];
			Map<Integer, Integer> zoneMap = edgeChannelAssignments.computeIfAbsent(key, k -> new HashMap<>());
			zoneMap.put(zone, channelCount);
			channelCount++;
		}

		channelsPerZone[zone] = channelCount;
	}

	/**
	 * Unique key for a parent -> child edge
	 */
	public static long edgeKey(ResearchNode parent, ResearchNode child) {
		return ((long) System.identityHashCode(parent) << 32) | (System.identityHashCode(child) & 0xFFFFFFFFL);
	}
}
