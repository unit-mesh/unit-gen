package org.unimesh.eval.picker.bs

class BadSmellGraphCall {
    companion object {
        var totalPath: MutableList<String> = mutableListOf()
    }

    fun analysisGraphCallPath(nodes: Map<String, List<String>>): List<String> {
        for (k in nodes.keys) {
            getConnectedPath(k, nodes)
        }
        return totalPath
    }

    private fun getConnectedPath(startNode: String, nodes: Map<String, List<String>>) {
        val relatedNodes = nodes[startNode] ?: emptyList()
        val currentPath: MutableList<String> = mutableListOf()
        for (i in relatedNodes.indices) {
            for (j in i + 1 until relatedNodes.size) {
                getPath(startNode, nodes, currentPath, relatedNodes[i], relatedNodes[j])
                getPath(startNode, nodes, currentPath, relatedNodes[j], relatedNodes[i])
            }
        }
    }

    private fun getPath(startNode: String, nodes: Map<String, List<String>>, currentPath: List<String>, currentNode: String, endNode: String) {
        val nextNodes = nodes[currentNode] ?: emptyList()
        if (nextNodes.isEmpty() || currentNode == startNode || currentNode == endNode) {
            return
        }
        if (nextNodes.contains(endNode)) {
            val path = listOf(startNode) + currentPath + listOf(currentNode, endNode)
            totalPath.add(path.joinToString("->") + ";$startNode->$endNode")
        }
        for (node in nextNodes) {
            if (currentPath.contains(node)) {
                continue
            }
            getPath(startNode, nodes, currentPath + currentNode, node, endNode)
        }
    }

    private fun contains(list: List<String>, element: String): Boolean {
        return list.contains(element)
    }
}
