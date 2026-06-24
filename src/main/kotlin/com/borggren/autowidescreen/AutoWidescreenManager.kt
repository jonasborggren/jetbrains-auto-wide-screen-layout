package com.borggren.autowidescreen

import com.intellij.ide.ui.UISettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.awt.Frame
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

@Service(Service.Level.APP)
class AutoWidescreenManager : Disposable {
    private val listeners = mutableMapOf<Project, FrameResizeListener>()

    fun registerFrame(project: Project, frame: Frame) {
        // Unregister existing first to prevent duplicates
        unregisterFrame(project)

        val listener = FrameResizeListener(project, frame)
        frame.addComponentListener(listener)
        listeners[project] = listener

        // Initial check immediately
        checkAndApply(frame)
    }

    fun unregisterFrame(project: Project) {
        val listener = listeners.remove(project)
        listener?.cleanup()
    }

    fun checkAndApply(frame: Frame) {
        val settings = AutoWidescreenSettings.instance
        if (!settings.enabled) return

        val width = frame.width
        val height = frame.height
        val uiSettings = UISettings.getInstance()
        
        val aspect = if (height > 0) width.toDouble() / height.toDouble() else 1.0
        val shouldBeWidescreen = if (settings.triggerMode == "ASPECT") {
            aspect >= settings.aspectRatioThreshold
        } else {
            width >= settings.threshold
        }

        LOG.info("AutoWidescreen - checkAndApply: width=$width, height=$height, aspect=$aspect, threshold=${settings.aspectRatioThreshold}, triggerMode=${settings.triggerMode}, shouldBeWidescreen=$shouldBeWidescreen, currentSupport=${uiSettings.wideScreenSupport}")

        // Only update and fire event if value changes
        if (uiSettings.wideScreenSupport != shouldBeWidescreen) {
            LOG.info("AutoWidescreen - Toggling wideScreenSupport from ${uiSettings.wideScreenSupport} to $shouldBeWidescreen")
            uiSettings.wideScreenSupport = shouldBeWidescreen
            ApplicationManager.getApplication().invokeLater {
                uiSettings.fireUISettingsChanged()
            }
        }
    }


    fun updateAllProjects() {
        for (listener in listeners.values) {
            checkAndApply(listener.frame)
        }
    }

    override fun dispose() {
        for (listener in listeners.values) {
            listener.cleanup()
        }
        listeners.clear()
    }

    private inner class FrameResizeListener(val project: Project, val frame: Frame) : ComponentAdapter() {
        override fun componentResized(e: ComponentEvent?) {
            checkAndApply(frame)
        }

        fun cleanup() {
            frame.removeComponentListener(this)
        }
    }

    companion object {
        val LOG = com.intellij.openapi.diagnostic.Logger.getInstance(AutoWidescreenManager::class.java)

        val instance: AutoWidescreenManager
            get() = ApplicationManager.getApplication().getService(AutoWidescreenManager::class.java)
    }
}
