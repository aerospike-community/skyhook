package com.aerospike.redispike

import com.aerospike.redispike.config.ServerConfiguration
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.inject.Guice
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import picocli.CommandLine
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.Callable
import kotlin.system.exitProcess

/**
 * Main entry point that sets up and starts the server.
 */
@CommandLine.Command(
    name = "redispike",
    description = ["Redis to Aerospike proxy server"]
)
class App : Callable<Unit> {

    companion object {
        internal val log = KotlinLogging.logger {}
    }

    @CommandLine.Option(
        names = ["-f", "--config-file"],
        description = ["yaml formatted configuration file"]
    )
    private var configFile: File? = null

    @CommandLine.Option(
        names = ["-h", "--help"], usageHelp = true,
        description = ["display this help and exit"]
    )
    private var help: Boolean = false

    override fun call() {
        val config = if (configFile != null) {
            // Parse the configuration.
            val configYaml = FileUtils.readFileToString(
                configFile,
                Charset.defaultCharset()
            )

            val yamlParser = getYamlParser()
            yamlParser.readValue(configYaml, ServerConfiguration::class.java)
        } else {
            ServerConfiguration()
        }

        val injector = Guice.createInjector(RedispikeModule(config))
        val server = injector.getInstance(RedispikeServer::class.java)

        // Add a shutdown hook.
        Runtime.getRuntime().addShutdownHook(Thread { server.stop() })

        // Start the server.
        server.start()
    }

    private fun getYamlParser(): ObjectMapper {
        val mapper = ObjectMapper(YAMLFactory())

        // Setup deserializer options.
        mapper.factory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        return mapper
    }
}

fun main(args: Array<String>) {
    try {
        CommandLine(App()).execute(*args)
    } catch (e: Exception) {
        App.log.error(e) {
            "Server stopped unexpectedly"
        }

        exitProcess(1)
    }
}
