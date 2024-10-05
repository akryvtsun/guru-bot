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
    private val materials = listOf("Lecture 1", "Lecture 2", "Lecture 3", "Lecture 4", "Lecture 5")

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