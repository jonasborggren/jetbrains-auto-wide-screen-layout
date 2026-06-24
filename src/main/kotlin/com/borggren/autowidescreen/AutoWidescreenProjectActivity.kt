package com.borggren.autowidescreen

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.WindowManager
import javax.swing.SwingUtilities

class AutoWidescreenProjectActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        var frame = WindowManager.getInstance().getFrame(project)
        var retries = 0
        // Wait up to 5 seconds (10 retries * 500ms) for the frame to be initialized
        while (frame == null && retries < 10) {
            kotlinx.coroutines.delay(500)
            frame = WindowManager.getInstance().getFrame(project)
            retries++
        }

        if (frame != null) {
            SwingUtilities.invokeLater {
                AutoWidescreenManager.instance.registerFrame(project, frame!!)
            }
        }
    }
}
