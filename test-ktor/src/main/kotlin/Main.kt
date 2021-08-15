import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.call

import io.ktor.http.content.static
import io.ktor.http.content.files
import io.ktor.http.content.file

import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.thymeleaf.*
import io.ktor.http.content.*
import io.ktor.request.receive
import io.ktor.request.receiveParameters

import org.koin.dsl.module
import org.koin.dsl.single
import org.koin.dsl.bind


import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import io.ktor.http.Parameters
import kotlin.reflect.KClass


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)



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
        single {Config()}
        single {DatabaseMock(get())}  bind Database::class
        single {KeiyakuList(get())}
    }
    install(Koin) {
        modules(serviceModule)
    }
    routing {
        println("DEBUG: routing!!!")

        
        get("/"){
            call.respond(ThymeleafContent("test",mapOf("param1" to "xxxx")))            
        }
        val keiyakuList by inject <KeiyakuList>()
        get("/keiyakuList") {
            //val keiyakuList = get<KeiyakuList>()
            println("DEBUG: call.parameters=${call.parameters}")
            var req = HTTP.requestMapping<KeiyakuList.Request>(call.parameters)
            call.respond(keiyakuList.processRequest(req))
        }

        //Thymelef のtemplateファイルでは相対パス指定しないと動作しないので注意
        // ×　th:src="@{/js/script.js}"
        // 〇 th:src="@{js/script.js}"
        static("static") {
            //方式１：　files("static")を使う
            //結論：　fat-jar内の静的リソースにアクセスできないため、却下
            //デフォルトのstaticRootFolderはワーキングディレクトリ
            //VSCodeだと、VSCodeの実行ファイルがあるフォルダを指すのでうまく動作しない
            //代わりにMainKtのクラスが含まれるフォルダをルートとして設定する
            //var path = ClassLoader.getSystemClassLoader().getResource(".")?.getPath()
            //println("Load static files(*.css *.js etc..) under $path")
            //staticRootFolder = java.io.File(path)
            //println("DEBUG: staticRootFolder $staticRootFolder ")
            //files("static") 
            
            //方式２： resources("static")
            //結論：fat-jar内の静的リソースにもアクセスできるので、こちらを採用
            //public fun Route.resources(resourcePackage: String? = null) {
            //https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/http/content/StaticContent.kt
            //public fun ApplicationCall.resolveResource
            //https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/http/content/StaticContentResolution.kt
            //public fun Route.get(path: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
            //https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/routing/RoutingBuilder.kt

            resources("static")
            println("DEBUG: staticBasePacakge $staticBasePackage ")
        }
        //Routingクラス内のchildrenはget()やstatic()などで指定したルーティング情報を持つリスト。
        //正しく設定できたかを確認したい場合に参照
        println("DEBUG: Routing.children = [$children]")

    }
    println("Starting v3")
    val path = System.getProperty("user.dir")

    println("Working Directory = $path")
}

