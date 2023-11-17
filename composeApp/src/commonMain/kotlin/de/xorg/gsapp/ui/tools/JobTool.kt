package de.xorg.gsapp.ui.tools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class JobTool : KoinComponent {
    private val jobMap: MutableMap<String, Job> = mutableMapOf()

    /**
     * Creates a job that will be cancelled if a job with the same name is already running.
     * @param jobName The name of the job
     * @param scope The scope to launch the job in
     * @param func The function to execute
     */
    fun singletonReplacingJob(jobName: String, scope: CoroutineScope, func: suspend () -> Unit) {
        jobMap[jobName]?.cancel()
        jobMap[jobName] = scope.launch { func() }
        jobMap[jobName]?.let { job ->
            job.invokeOnCompletion {
                jobMap.remove(jobName)
            }
        }
    }

    /**
     * Creates a job that won't be executed if a job with the same name is already running.
     * Returns true if the job was created, false if it was ignored.
     * @param name The name of the job
     * @param scope The scope to launch the job in
     * @param func The function to execute
     * @return true if the job was created, false if it was ignored
     */
    fun singletonIgnoringJob(name: String, scope: CoroutineScope, func: suspend () -> Unit): Boolean {
        if(jobMap.containsKey(name)) return false

        jobMap[name] = scope.launch { func() }
        jobMap[name]?.let { job ->
            job.invokeOnCompletion {
                jobMap.remove(name)
            }
        }
        return true
    }

    /**
     * Cancels a job with the given name.
     * @param name The name of the job
     */
    fun cancelJob(name: String) {
        jobMap[name]?.cancel()
        jobMap.remove(name)
    }


}