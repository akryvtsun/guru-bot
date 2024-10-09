package guru

import com.google.gson.Gson

typealias UserId = Long
typealias MaterialIdx = Int
typealias Material = String

data class Course(val items: List<String>)

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
class CourseState(configFile: String) : Registrar, Preparer {

    private val materials: List<Material>

    // users course progress
    private val users = mutableMapOf<UserId, MaterialIdx>()

    init {
        val jsonString = this.javaClass.getResource(configFile)?.readText()
        val course = Gson().fromJson(jsonString, Course::class.java)
        materials = course.items
    }

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