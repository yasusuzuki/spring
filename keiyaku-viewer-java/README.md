# Read Me First


# PEND items

* [  ]  設定ファイルの外部化。
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
* [  ] JUnit5に移行する。pom.xmlの設定だけでよいはずだが、やりかたがわからない

# Getting Started

* TODO: これから

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.5/maven-plugin/reference/html/#build-image)


* Hikari CP のREAMD.md とてもわかりやすい
  * https://github.com/brettwooldridge/HikariCP/blob/dev/README.md
