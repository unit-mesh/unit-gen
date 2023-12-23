package cc.unitmesh.pick.ext

import org.archguard.scanner.core.sca.CompositionDependency

fun CompositionDependency.Companion.from(
    name: String,
    group: String,
    artifactId: String,
): CompositionDependency {
    return CompositionDependency(
        name = name,
        depName = name,
        depGroup = group,
        depArtifact = artifactId,
        packageManager = "",
        depVersion = "",
        version = "",
        path = "",
        depScope = "",
        id = "",
        parentId = "",
    )
}