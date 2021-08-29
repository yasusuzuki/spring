# Read Me First

# Getting Started
* 配布されたzipファイルを解凍する
* application.yml内データベース接続設定を修正する
* main.cmdを実行する
* データディクショナリが古い場合、dat/フォルダ内のcsvファイルを更新する

# Release Procedure
* テスト
  * コンテナが必要な場合は、以下でアプリケーションを起動させる
    * mvn spring-boot:run　で起動。ホットデプロイ状態なので、その後の修正はmvn compileを実行するだけ
  * コンテナが不要の場合は、特定のテストケースを実行させる
    * mvn test "-Dtest=com.github.yasusuzuki.spring.testkotlinboot.TestDictionary
* 配布事前準備
  * ビルドする
    * mvn clean package
    * mv target\keiyaku-viewer-java-1.0.0.jar .
  * 必要ライブラリをダウンロードする
    * java -jar keiyaku-viewer-java-1.0.0.jar --thin.dryrun=true --thin.root=libs
  * 必要ライブラリに追加があったか確認する
    * ls -r libs | where {$_.LastWriteTime.Date -eq (Get-Date).Date} | Resolve-Path -relative 
  * 起動する
    * java -jar keiyaku-viewer-java-1.0.0.jar --thin.offline=true --thin.root=libs
    * OR .\main.cmd
  * ZIPにまとめる
    * copy keiyaku-viewer-java-1.0.0.jar templates/ libs/ data/ main.cmd to dist_folder(*)
    * ※　dist_folder --- 名前は任意

# PEND items

* [済]  設定ファイルの外部化。
* [済]  CodeMasterEnquiry で、順番が描画のたびに変わってしまう問題の対応。
  * 単純なMapではなくLinkedHashMapを用いることで解消
* [済]  不正なSQLの場合、HikariCPがエラーを返さず無限ループに入ってしまう問題を解決する
  * HikariCPのバージョンを2.5.0から3.4.5に変更したことで解決。
  * 参考：https://github.com/brettwooldridge/HikariCP/blob/HikariCP-3.4.5/src/main/java/com/zaxxer/hikari/pool/ProxyConnection.java
* [  ] テスト環境をAccess DBからDB2（もしくはhsqldb)へ変更
* [済] Requestをクラスで受け取ると、汎用性がない
  * 各ページごとにRequestクラスを作成することにした。各Controllerクラス内で"Request"という内部クラス名とすることにし、統一性を持たせた
  * ``MultiValueMap<String,String>``で受け取れることがわかったが、Value部分が常にListになっていて参照する側がわかりにくい
  * https://qiita.com/yuji38kwmt/items/516d00fb7f0b360bd7c9
```Kotlin
//こちらは廃案
fun sql(@RequestParam req: MultiValueMap<String, String>, model:Model ): String? { ... }

//こちらを採用
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
   * アサーションはAssertKを採用。モックはKotlin-mockitoを採用。
* [済] codemaster.csvのコード値の前ゼロが削除されているのにあわせて、検索ロジックを修正。CSVをExcelで開くだけで、前ゼロ削除されてしまうので、CSVを修正するのではなく、検索ロジックを修正するほうが適切という判断。2021/7/8
* [済] Javaプログラムが出力したJSONファイルのサポート。(E0001)
   * DictionaryクラスでJava物理名もサポート。data_dictionary.csvの更新が必要。
   * JsonをDBテーブルのような形式で表示するJsonEnquiry画面を作成。JsonクラスとJsonEnquiryクラスを追加
   * jackson-annotations-2.11.4.jar jackson-core-2.11.4.jar jackson-databind-2.11.4.jarをランタイムに追加する必要あり
* [済] SQLの実行をThymeleafのテンプレート処理時まで実行遅延させることで、体感速度を向上 (E0002)
   * Controller内でSQLを実行せず、Thymeleaf内でSQLを呼び出すラムダ式をinvoke()するように修正
* [済] buildの高速化。kotlin incremental compilerの導入。pom.xmlを修正した(E0003)
* [済] DB接続のアカウントがリボークされてしまう問題への対応。(E0004)
   * DBコネクションプールにためておく接続数をデフォルトの10から1へ変更した
* [済] 契約変更を変更内容で絞り込みできるように検索条件を変更する
   * SQLの変更、検索条件への追加、列を集約処理する
* [  ] JsonEnquiry画面で、もともとのJava物理名が表示されていない問題の対応
   * L2P(),P2L()の派生で、L2J(),J2L()を作成する案
* [  ] CodeMasterEnquiry画面に、Java物理名も表示する変更
* [済] 団体情報取得のための団体詳細画面を開発
   * GroupClientEnquiry.kt groupClientEnquiry.htmlの追加
* [済] 代理店情報取得のための代理店詳細画面を開発
   * GroupClientEnquiry.kt groupClientEnquiry.htmlの追加 a
* [済] 提案一覧や契約一覧で、システム項目を非表示にしてしまっていた問題を解決
* [済] 提案一覧にデータ登録ユーザIDの検索項目を追加
* [済] Utilクラス内の関数はすべてstaticなので、シングルトンクラスに変更した
    * クラス定義の``class Util``を``object Util`` に修正
    * クラス内の``companion object``を削除
* [済] リファクタリング：べた書きにしていたSQLのWHERE条件を生成する処理をQueryCriteriaクラスにまとめた。
* [済] リファクタリング：Field Injectionを辞めて、Constructor Injectionへ修正。
* [済] リファクタリング：テストコードをJavaのMockitoを使わず、Kotlin-mockitoの関数に統一
```
修正前：
        //以降は、テスト対象メソッドが内部でbuildHTMLFromSQL()を呼出した後
        val sqlCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(sqlCaptor),any())
        assertThat(sqlCaptor.getValue()).isEqualTo("....")        
修正後：
        //以降は、テスト対象メソッドが内部でbuildHTMLFromSQL()を呼出した後        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table1 WHERE 代理店＿コード = '12345'")
        }
※参考：https://qiita.com/wrongwrong/items/bf2fc29a63a848c041d0
```
* [済] リファクタリング：初期化処理をinit()からpostConstruct()に名前を変更。Kotlinのコンストラクタ名と被るため。
* [済] Loggerを統一する
  * すべてのprintlnをlog.info()に変更。logback.xmlとlogback-test.xmlの設定を追加。
* [  ] BusinessTermを外に切り出す。SQLはさすがにBusinessTermをきりだせない
* [済] 提案詳細画面：提案枝番も検索キーとして追加する
* [済] 提案詳細画面：最新の提案設計データバージョン番号＿数のみ表示する
  * 提案設計情報など、データバージョンを持たないケースもある
  * 提案設計情報、枝番、連続番号、データバージョンをハイフンでつなぎ、さらに、コンマ区切りで複数案件対応できるようにQueryCriteriaを修正
* [  ] 案：詳細画面の先頭のリンクをプルダウンにして、各エンティティへジャンプできるようにする
* [  ] 案：取引口座エンティティ詳細ページ作成
* [  ] 案：代理店コード・サブコードのように１カラムに統合した場合、サブコードのカラムを非表示にしたい
* [  ] keiyakuListSQL.txtの明細エンティティが、明細のみ変更の場合に抽出できないが、そんなことはありえるか？




### Reference Documentation
For further reference, please consider the following sections:

* Hikari CP のREAMD.md とてもわかりやすい
  * https://github.com/brettwooldridge/HikariCP/blob/dev/README.md

