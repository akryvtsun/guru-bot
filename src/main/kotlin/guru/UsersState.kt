package guru

typealias UserId = Long
typealias MaterialIdx = Int
typealias Material = String

interface Registrar {
    fun register(user: UserId)
    fun unregister(user: UserId)
}

interface Preparer {
    fun usersSnapshot(): List<Pair<UserId, Material>>
    fun isLastMaterial(user: UserId): Boolean
}

class UsersState : Registrar, Preparer {
    private val users = mutableMapOf<UserId, MaterialIdx>()
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
    override fun usersSnapshot(): List<Pair<UserId, Material>> {
        val snapshot = mutableListOf<Pair<UserId, Material>>()
        users.entries.forEach {
            if (!isLastMaterial(it.key)) {
                snapshot.add(Pair(it.key, materials[it.value]))
                users[it.key] = it.value + 1
            }
        }
        return snapshot
    }

    @Synchronized
    override fun isLastMaterial(user: UserId): Boolean {
        return users[user] == materials.size
    }
}