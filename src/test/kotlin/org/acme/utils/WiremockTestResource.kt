package org.acme.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.http.JvmProxyConfigurer.configureFor
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

open class WiremockTestResource : QuarkusTestResourceLifecycleManager {
    companion object {
        var wiremock: WireMockServer =
            WireMockServer(
                wireMockConfig().dynamicPort().dynamicHttpsPort()
            )
    }

    override fun start(): MutableMap<String, String> {
        startServer()
        return mutableMapOf(
            "quarkus.rest-client.\"org.acme.clients.MyRemoteService\".url" to wiremock.baseUrl()
        )
    }

    private fun startServer() {
        if (!wiremock.isRunning) {
            wiremock.start()
            configureFor(wiremock.port())
        }
    }

    override fun stop() {
        if (wiremock.isRunning) {
            wiremock.stop()
        }
    }
}
