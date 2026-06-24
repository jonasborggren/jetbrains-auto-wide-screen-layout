package com.borggren.autowidescreen

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

@State(
    name = "AutoWidescreenSettings",
    storages = [Storage("autoWidescreenSettings.xml")]
)
@Service(Service.Level.APP)
class AutoWidescreenSettings : PersistentStateComponent<AutoWidescreenSettings.State> {

    class State {
        var enabled: Boolean = true
        var threshold: Int = 1920
        var triggerMode: String = "WIDTH" // "WIDTH" or "ASPECT"
        var aspectRatioThreshold: Double = 1.77
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    var enabled: Boolean
        get() = myState.enabled
        set(value) {
            myState.enabled = value
        }

    var threshold: Int
        get() = myState.threshold
        set(value) {
            myState.threshold = value
        }

    var triggerMode: String
        get() = myState.triggerMode
        set(value) {
            myState.triggerMode = value
        }

    var aspectRatioThreshold: Double
        get() = myState.aspectRatioThreshold
        set(value) {
            myState.aspectRatioThreshold = value
        }

    companion object {
        val instance: AutoWidescreenSettings
            get() = ApplicationManager.getApplication().getService(AutoWidescreenSettings::class.java)
    }
}
