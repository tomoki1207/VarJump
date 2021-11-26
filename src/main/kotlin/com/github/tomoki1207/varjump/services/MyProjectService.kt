package com.github.tomoki1207.varjump.services

import com.intellij.openapi.project.Project
import com.github.tomoki1207.varjump.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
