package cc.unitmesh.core.arch

/**
 *
 */
interface LayeredArchitecture {
    val layerDefinitions: LayerDefinitions
}

data class LayerDefinition(
    val name: String,
    val optional: Boolean = false,
)

class LayerDefinitions {
    private val layerDefinitions: MutableMap<String, LayerDefinition> = LinkedHashMap()

    fun add(definition: LayerDefinition) {
        layerDefinitions[definition.name] = definition
    }

    fun containLayer(layerName: String): Boolean {
        return layerDefinitions.containsKey(layerName)
    }

    private fun get(layerNames: Collection<String>): List<LayerDefinition> {
        return layerNames.mapNotNull(layerDefinitions::get)
    }

    operator fun iterator(): Iterator<LayerDefinition> {
        return layerDefinitions.values.iterator()
    }
}