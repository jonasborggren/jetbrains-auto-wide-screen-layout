package com.borggren.autowidescreen

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.WindowManager
import javax.swing.SwingUtilities

class AutoWidescreenProjectActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        SwingUtilities.invokeLater {
            val frame = WindowManager.getInstance().getFrame(project)
            if (frame != null) {
                AutoWidescreenManager.instance.registerFrame(project, frame)
            }
        }
    }
}
