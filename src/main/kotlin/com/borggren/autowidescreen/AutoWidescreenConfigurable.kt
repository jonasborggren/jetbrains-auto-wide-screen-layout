package com.borggren.autowidescreen

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class AutoWidescreenConfigurable : Configurable {
    private var mySettingsComponent: AutoWidescreenSettingsComponent? = null

    override fun getDisplayName(): String = "Auto Widescreen Layout"

    override fun createComponent(): JComponent {
        mySettingsComponent = AutoWidescreenSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = AutoWidescreenSettings.instance
        return mySettingsComponent!!.enabledStatus != settings.enabled ||
                mySettingsComponent!!.thresholdValue != settings.threshold ||
                mySettingsComponent!!.triggerModeValue != settings.triggerMode ||
                mySettingsComponent!!.aspectRatioValue != settings.aspectRatioThreshold
    }

    override fun apply() {
        val settings = AutoWidescreenSettings.instance
        settings.enabled = mySettingsComponent!!.enabledStatus
        settings.threshold = mySettingsComponent!!.thresholdValue
        settings.triggerMode = mySettingsComponent!!.triggerModeValue
        settings.aspectRatioThreshold = mySettingsComponent!!.aspectRatioValue

        // Trigger updates across all active projects
        AutoWidescreenManager.instance.updateAllProjects()
    }

    override fun reset() {
        val settings = AutoWidescreenSettings.instance
        mySettingsComponent!!.enabledStatus = settings.enabled
        mySettingsComponent!!.thresholdValue = settings.threshold
        mySettingsComponent!!.triggerModeValue = settings.triggerMode
        mySettingsComponent!!.aspectRatioValue = settings.aspectRatioThreshold
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}

class AutoWidescreenSettingsComponent {
    val panel: JPanel
    
    private val enabledCheckBox = JBCheckBox("Enable auto-switching of widescreen layout")
    
    private val widthModeRadioButton = JRadioButton("Trigger by Screen Width (pixels)")
    private val aspectModeRadioButton = JRadioButton("Trigger by Aspect Ratio (width / height)")
    private val modeButtonGroup = ButtonGroup()

    private val thresholdSlider = JSlider(JSlider.HORIZONTAL, 800, 3840, 1920)
    private val thresholdLabel = JBLabel("Widescreen width threshold: 1920px")

    private val aspectSlider = JSlider(JSlider.HORIZONTAL, 100, 300, 177)
    private val aspectLabel = JBLabel("Aspect ratio threshold: 1.77 (~16:9 Widescreen)")
    
    private val currentSizeLabel = JBLabel("Current IDE window: Calculating...")

    init {
        modeButtonGroup.add(widthModeRadioButton)
        modeButtonGroup.add(aspectModeRadioButton)

        // Setup width slider
        thresholdSlider.majorTickSpacing = 500
        thresholdSlider.paintTicks = true
        thresholdSlider.paintLabels = true
        thresholdSlider.addChangeListener(object : ChangeListener {
            override fun stateChanged(e: ChangeEvent) {
                thresholdLabel.text = "Widescreen width threshold: ${thresholdSlider.value}px"
            }
        })

        // Setup aspect slider (values represent 1.00 to 3.00, stored * 100)
        aspectSlider.majorTickSpacing = 50
        aspectSlider.paintTicks = true
        aspectSlider.paintLabels = true
        
        // Custom labels for aspect ratio slider
        val labelTable = java.util.Hashtable<Int, JLabel>()
        labelTable[100] = JLabel("1.00")
        labelTable[150] = JLabel("1.50")
        labelTable[200] = JLabel("2.00")
        labelTable[250] = JLabel("2.50")
        labelTable[300] = JLabel("3.00")
        aspectSlider.labelTable = labelTable

        aspectSlider.addChangeListener(object : ChangeListener {
            override fun stateChanged(e: ChangeEvent) {
                val aspectValue = aspectSlider.value / 100.0
                val formatted = String.format("%.2f", aspectValue)
                aspectLabel.text = "Aspect ratio threshold: $formatted${getAspectRatioDescription(aspectValue)}"
            }
        })

        // Interactive enabling/disabling of controls
        val stateListener = ActionListener { updateEnabledStates() }
        enabledCheckBox.addActionListener(stateListener)
        widthModeRadioButton.addActionListener(stateListener)
        aspectModeRadioButton.addActionListener(stateListener)

        // Fetch current IDE window size and ratio to assist the user
        val activeFrame = WindowManager.getInstance().findVisibleFrame()
        if (activeFrame != null) {
            val w = activeFrame.width
            val h = activeFrame.height
            val ratio = if (h > 0) w.toDouble() / h.toDouble() else 1.0
            val ratioFormatted = String.format("%.2f", ratio)
            currentSizeLabel.text = "Current IDE window: ${w}px x ${h}px (Aspect ratio: $ratioFormatted${getAspectRatioDescription(ratio)})"
        } else {
            currentSizeLabel.text = "Current IDE window: Unknown (No active project window)"
        }

        panel = FormBuilder.createFormBuilder()
            .addComponent(enabledCheckBox)
            .addVerticalGap(15)
            .addSeparator()
            .addVerticalGap(5)
            .addComponent(JBLabel("Trigger Mode Selection:"))
            .addComponent(widthModeRadioButton)
            .addVerticalGap(5)
            .addComponent(thresholdLabel)
            .addComponent(thresholdSlider)
            .addVerticalGap(15)
            .addComponent(aspectModeRadioButton)
            .addVerticalGap(5)
            .addComponent(aspectLabel)
            .addComponent(aspectSlider)
            .addVerticalGap(20)
            .addSeparator()
            .addVerticalGap(5)
            .addComponent(currentSizeLabel)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        // Apply initial state updates
        updateEnabledStates()
    }

    private fun getAspectRatioDescription(ratio: Double): String {
        return when {
            kotlin.math.abs(ratio - 1.333) < 0.05 -> " (4:3 Standard)"
            kotlin.math.abs(ratio - 1.50) < 0.03 -> " (3:2)"
            kotlin.math.abs(ratio - 1.60) < 0.03 -> " (16:10)"
            kotlin.math.abs(ratio - 1.778) < 0.05 -> " (~16:9 Widescreen)"
            kotlin.math.abs(ratio - 2.333) < 0.05 -> " (~21:9 Ultrawide)"
            kotlin.math.abs(ratio - 3.556) < 0.05 -> " (~32:9 Super Ultrawide)"
            else -> ""
        }
    }

    private fun updateEnabledStates() {
        val pluginEnabled = enabledCheckBox.isSelected
        widthModeRadioButton.isEnabled = pluginEnabled
        aspectModeRadioButton.isEnabled = pluginEnabled

        val widthMode = widthModeRadioButton.isSelected && pluginEnabled
        thresholdLabel.isEnabled = widthMode
        thresholdSlider.isEnabled = widthMode

        val aspectMode = aspectModeRadioButton.isSelected && pluginEnabled
        aspectLabel.isEnabled = aspectMode
        aspectSlider.isEnabled = aspectMode
    }

    var enabledStatus: Boolean
        get() = enabledCheckBox.isSelected
        set(newStatus) {
            enabledCheckBox.isSelected = newStatus
            updateEnabledStates()
        }

    var triggerModeValue: String
        get() = if (aspectModeRadioButton.isSelected) "ASPECT" else "WIDTH"
        set(newValue) {
            if (newValue == "ASPECT") {
                aspectModeRadioButton.isSelected = true
            } else {
                widthModeRadioButton.isSelected = true
            }
            updateEnabledStates()
        }

    var thresholdValue: Int
        get() = thresholdSlider.value
        set(newValue) {
            thresholdSlider.value = newValue
        }

    var aspectRatioValue: Double
        get() = aspectSlider.value / 100.0
        set(newValue) {
            aspectSlider.value = (newValue * 100.0).toInt().coerceIn(100, 300)
        }
}
