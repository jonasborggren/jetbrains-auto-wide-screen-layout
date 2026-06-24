package com.borggren.autowidescreen

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class AutoWidescreenProjectCloseListener : ProjectManagerListener {
    override fun projectClosing(project: Project) {
        AutoWidescreenManager.instance.unregisterFrame(project)
    }
}
