
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.call

import io.ktor.http.content.static
import io.ktor.http.content.files
import io.ktor.http.content.file

import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.thymeleaf.*
import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import io.ktor.http.content.*
import io.ktor.request.receive

import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    //注入するインスタンスを定義する
    val serviceModule = module() {
        single { Config() }
        single { DatabaseImpl(get()) } 
        single { KeiyakuList(get()) }
    }
    install(Koin) {
        modules(serviceModule)
    }
    routing {
        val keiyakuList by inject <KeiyakuList>()
        get("/"){
            call.respond(ThymeleafContent("test",mapOf("param1" to "xxx")))            
        }
        get("/keiyakuList") {
            //val req = call.receive<KeiyakuList.Request>()
            call.respond(keiyakuList.processRequest())
        }

        //Thymelef のtemplateファイルでは相対パス指定しないと動作しないので注意
        // ×　th:src="@{/js/script.js}"
        // 〇 th:src="@{js/script.js}"
        static("static") {
            //デフォルトのstaticRootFolderはワーキングディレクトリ
            //VSCodeだと、VSCodeの実行ファイルがあるフォルダを指すのでうまく動作しない
            //代わりにMainKtのクラスが含まれるフォルダをルートとして設定する
            var path = ClassLoader.getSystemClassLoader().getResource(".").getPath()
            println("Load static files(*.css *.js etc..) under $path")
            staticRootFolder = java.io.File(path)
            files("static") 
        }
    }
    println("Starting v2")
    val path = System.getProperty("user.dir")

    println("Working Directory = $path")
}

