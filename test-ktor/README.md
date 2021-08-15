== SUMMARY ==

* ktor(1.5.4) & koin(3.0.2)を使用したテストアプリです。
* よいところ
  * いまのところなし。SpringBootと比べて
* 期待通りではなかったところ
  * Kotlinのコンパイルはとにかく時間がかかる。KtorやKoinをいれるとだいたい20秒ぐらい。
  * 開発中はMvn経由で起動するので、起動時間が思ったよりかかる。SpringBootの7秒と比べて
  * Uber jarも17MBぐらいになるので(JDBCドライバなどなしで)、実行ファイルサイズも大きい。SpringBootの30MBと比較して
  * Thymeleafなどのライブラリとの統合がうまく行かないことが多く時間がかかった。特にstaticのフォルダパス指定のところ。SpringBootと比べて
  * テストライブラリがkoin-test, koin-test-junit4, koin-test-junit5など種類が多く、それぞれ書き方のルールが違うのでせっかくサンプルがあっても引用できないことが多い
  * Thin jarが今のところできない。
  * VS Code(IDE)の"Kotlin"というエクステンションでも、入力したクラスのパッケージを依存関係にあるJARから探し出すことまではできない。きちんとimport文を入力しても、Ctrl+クリックで、指定したクラスのソースコードまでジャンプできない。

== 注意 ==
* Java1.8でないとうまくいかない。AOPを使っているので、Kotlin関係のライブラリが1.8でビルドされている以上、1.8でしか動作しないと思われる。

== TODO ==

* [済]インジェクションの定義で、実装クラスのみを設定するだけでよいか？確認する
        single { DatabaseImpl(get()) } 　　これでよい？
        singleBy<DatabaseImpl,Database>)()
        single { DatabaseImpl(get()) as Database } 
  * Beanの生成時は具体的な実装クラスのコンストラクタを呼ぶ、でよい。適宜 bindを使う。
* [済] コンテナ管理コンポーネントは、KoinComponentを継承しなくてもよいか？確認する
  いまのところ、DatabaseやConfigはKoinComponentを継承しなくてもsingle{}で登録
  しておけばインジェクトしてくれている。
  * インジェクトを受けるほうはKoinComponentを継承しなくてよい
* [済] CSSやJSなどの静的ファイルの格納パスを指定する箇所が、これよりもっと良い方法はないか？
　　var path = ClassLoader.getSystemClassLoader().getResource(".").getPath()
  * resources()を使う
* Databaseのモック実装を作成し、テスト実行時に簡単に実装をモックに差し替えられるか確認する

* [済]HTTP Requestパラメータをデシリアライズする。

* 
