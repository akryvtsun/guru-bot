package guru

typealias UserId = Long
typealias MaterialIdx = Int
typealias Material = String

interface Registrar {
    fun register(user: UserId)
    fun unregister(user: UserId)
}

interface Preparer {
    fun snapshot(): List<Pair<UserId, Material>>
    fun isCourseFinished(user: UserId): Boolean
}

/**
 * Holds users state and progress in course
 */
class CourseState : Registrar, Preparer {
    // users course progress
    private val users = mutableMapOf<UserId, MaterialIdx>()
    // TODO externalize learning materials
    private val materials = listOf(
        "\uD83C\uDF9E Матеріал 1",
        "\uD83C\uDF9E Матеріал 2",
        "\uD83C\uDF9E Матеріал 3",
        "\uD83C\uDF9E Матеріал 4",
        "\uD83C\uDF9E Матеріал 5"
    )

    @Synchronized
    override fun register(user: UserId) {
        users[user] = 0
    }

    @Synchronized
    override fun unregister(user: UserId) {
        users -= user
    }

    @Synchronized
    override fun snapshot(): List<Pair<UserId, Material>> {
        val snapshot = mutableListOf<Pair<UserId, Material>>()
        users.entries.forEach {
            if (!isCourseFinished(it.key)) {
                snapshot.add(it.key to materials[it.value])
                // make course progress
                users[it.key] = it.value + 1
            }
        }
        return snapshot
    }

    @Synchronized
    override fun isCourseFinished(user: UserId) = users[user] == materials.size
}