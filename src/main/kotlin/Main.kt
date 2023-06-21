import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import module.MainModule

fun main() = runBlocking {
    resourceScope {
        MainModule.modules.map {
            val timeout = it.timeout
            launch(Dispatchers.Default) {
                runProgram(
                    Program(it.checkerResource.bind(), it.notifierResource.bind()),
                    timeout
                )
            }
        }.joinAll()
    }
}
