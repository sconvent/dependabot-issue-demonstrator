import java.text.SimpleDateFormat
import java.util.Date
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

open class CreateMigrationTask : DefaultTask() {
    // STRICT_MIGRATION_NAME
    // if true, the migration name MUST be given
    // otherwise it can be omitted and 'Rename_This_Migration' will be used.
    @get:Input
    var strictMigrationNameParameter = false

    @get:Input
    var migrationPath = "src/main/resources/db/migration"

    @get:Input
    var allowedCharacters = "a-zA-Z0-9_"

    private val regex = "[^$allowedCharacters]".toRegex()

    @TaskAction
    fun createMigration() {
        val filename = (project.properties["migrationName"] as String?)?.let {
            it.replace(" ", "_").replace(regex, "")
        } ?: kotlin.run {
            if (strictMigrationNameParameter) throw Exception(
                    "ERROR: Missing parameter 'migrationName'. Please add -PmigrationName=\"Your Migration Name\""
            ).also { println(it.message) }
            "Rename_This_Migration"
        }

        val timestamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val target = "$migrationPath/V${timestamp}__$filename.sql"
        File(target).writeText("--\n-- Description:\n--\n")
        println("\nCreated new Migration-File: $target")
    }
}

open class CheckAllMigrationsTask : DefaultTask() {
    @get:Input
    var migrationPath = "modbus-backend/src/main/resources/db/migration"

    @get:Input
    var allowedCharacters = "a-zA-Z0-9_"

    private val regex = "V[0-9]{14}__[$allowedCharacters]+.sql".toRegex()

    @TaskAction
    fun checkAllMigrations() {
        getAllMigrationNames().forEach {
            assertCorrectFilenamePattern(it)
            assertTimestampInPast(it)
        }
    }

    private fun assertTimestampInPast(filename: String) {
        val tomorrow = LocalDate.now().plusDays(1)
        val extractedTimestamp = filename.split("__").first().replace("V", "")

        val year = extractedTimestamp.subSequence(0, 4).toString().toInt()
        val month = extractedTimestamp.subSequence(4, 6).toString().toInt()
        val day = extractedTimestamp.subSequence(6, 8).toString().toInt()

        val migrationDate = LocalDate.of(year, month, day)

        require(migrationDate.isBefore(tomorrow), {
            "Migration timestamp must be in the past: $filename"
        })
    }

    private fun assertCorrectFilenamePattern(filename: String) {
        require(regex.containsMatchIn(filename), {
            "Migration filename does not match ruleset: $filename"
        })
    }

    private fun getAllMigrationNames(): MutableList<String> {
        val projectDirAbsolutePath = project.properties["rootDir"]
        val resourcesPath = Paths.get(projectDirAbsolutePath.toString(), migrationPath)

        val listOfMigrations = mutableListOf<String>()
        val dirName = resourcesPath.fileName

        Files.walk(resourcesPath)
            .filter { file -> file.fileName != dirName }
            .forEach {
                listOfMigrations.add(it.fileName.toString())
            }

        return listOfMigrations
    }
}

tasks.register<CreateMigrationTask>("createMigration") {
    description = "Creates a new migration file."
    group = "Migration Helper"
}
tasks.register<CheckAllMigrationsTask>("checkMigrations") {
    description = "Runs a check against all migration files to ensure that filenames match the guidelines"
    group = "Migration Helper"
}
