# Read Me First

# Getting Started
* テスト
  * mvn spring-boot:run
* 配布事前準備
  * ビルドする
    * mvn clean package
  * 必要ライブラリをダウンロードする
    * java -jar target\keiyaku-viewer-java-1.0.0.jar --thin.dryrun=true --thin.root=libs
  * ZIPにまとめる
    * mv target\keiyaku-viewer-java-1.0.0.jar .
    * copy keiyaku-viewer-java-1.0.0.jar templates/ libs/ data/ main.cmd to dist_folder(*)
* 起動する
  * cd dist_folder(*)
  * java -jar keiyaku-viewer-java-1.0.0.jar --thin.offline=true --thin.root=libs
  * OR .\main.cmd

* dist_folder --- 名前は任意

# PEND items

* [済]  設定ファイルの外部化。
* [  ]  CodeMasterEnquiry と
* [済]  不正なSQLの場合、HikariCPがエラーを返さず無限ループに入ってしまう問題を解決する
  * HikariCPのバージョンを2.5.0から3.4.5に変更したことで解決。
  * 参考：https://github.com/brettwooldridge/HikariCP/blob/HikariCP-3.4.5/src/main/java/com/zaxxer/hikari/pool/ProxyConnection.java
* [  ] DB2のテーブル定義ができていない
* [済] Requestをクラスで受け取ると、汎用性がない
  * ``MultiValueMap<String,String>``で受け取れることがわかったが、Value部分が常にListになっていて参照する側がわかりにくい
  * https://qiita.com/yuji38kwmt/items/516d00fb7f0b360bd7c9
```Kotlin
    fun sql(@RequestParam req: MultiValueMap<String, String>, model:Model ): String? { ... }

data class Request
fun sql(@RequestParam req:HashMap<String, String>, model:Model ): String? { ... }
```
* [済] サービス側で作成したHTMLのDataTableをThymeleafで表示しようとすると、そのまま出力されてしまう
  * Thymeleafの``th:utext``を使えばよい
  * https://stackoverflow.com/questions/23156585/process-thymeleaf-variable-as-html-code-and-not-text

* [済] HTMLを吐き出すときに、コードマスタなどを変換した処理をいれる
  * HTML側の変更は以下
```dtd
 <!-- 修正前 -->
 <table class="DataTable">
      <tr>
          <th th:each="cell : ${data.columnList}" > [[ ${cell} ]] </th>
      </tr>
      <tr th:each="row : ${data.recordList}" >
          <td th:each="cell : ${row}"> [[ ${cell.value} ]] </td>
      </tr>
  </table>
```
```dtd
<!-- 修正後 -->
<div th:utext="${dataTable}"></div>
```
* [済] JUnit5に移行する
* [済] codemaster.csvのコード値の前ゼロが削除されているのにあわせて、検索ロジックを修正。CSVをExcelで開くだけで、前ゼロ削除されてしまうので、CSVを修正するのではなく、検索ロジックを修正するほうが適切という判断。2021/7/8
* [済] Javaプログラムが出力したJSONファイルのサポート。(E0001)
   * DictionaryクラスでJava物理名もサポート。data_dictionary.csvの更新が必要。
   * JsonをDBテーブルのような形式で表示するJson詳細画面を作成。JsonクラスとJsonEnquiryクラスを追加
* [済] SQLの実行をThymeleafのテンプレート処理時まで実行遅延させることで、体感速度を向上 (E0002)
   * すべてのSQLの実行を待たずに、ブラウザの描画が始まるので表示が始まるまで速くなった
* [済] buildの高速化。kotlin incremental compilerの導入。pom.xmlを修正した(E0003)
* [済] DB接続のアカウントがリボークされてしまう問題への対応。(E0004)
   * DBコネクションプールにためておく接続数をデフォルトの10から1へ変更した

# Release Procedure


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.5/maven-plugin/reference/html/#build-image)


* Hikari CP のREAMD.md とてもわかりやすい
  * https://github.com/brettwooldridge/HikariCP/blob/dev/README.md
