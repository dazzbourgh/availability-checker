import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import module.MainModule

fun main() = runBlocking {
    MainModule.modules.map {
        val timeout = it.timeout
        val checkerResource = it.checkerResource
        val notifierResource = it.notifierResource

        resourceScope {
            val checker = checkerResource.bind()
            val notifier = notifierResource.bind()
            launch(Dispatchers.Default) { runProgram(Program(checker, notifier), timeout) }
        }
    }.forEach {
        it.join()
    }
}
