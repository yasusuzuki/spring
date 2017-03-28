Springのサンプルプログラム集
========================


* SpringBoot Thymeleaf Groovy
* 開発スピード
* 保守性
* 型安全


test-boot-web-02
-------------------

* Thymeleaf
* 選択式

```
    <body th:object="${parentObject}">
      <div th:object="*{hoge.name}">
        <p>Name: <span th:text="*{name}">ななし</span></p>
      </div>
    </body>
```


test-boot-web-04
------------------
* 以下のように、SpringBeanを使って、動的に入力フォームを出力する汎用的なメソッドを作成したが、あくまで文字列としての入力フォームが作り出されるだけで、SpringMVCに入力フォームに指定された値が連携されたなかった。

```
<div th:utext="${@inputFieldBean.html('policyNo','証券番号','SK0000001')}">Header</div>
<div th:utext="${@inputFieldBean.html('customerName','契約者氏名ｶﾅ','損保太郎')}">Header</div>
<div th:utext="${@inputFieldBean.html('customerAddress','契約者住所ｶﾅ ','東京都％')}">Header</div>
<div th:utext="${@inputFieldBean.html('contractStartDate','保険始期日','2017/4/1')}">Header</div>
<div th:utext="${@inputFieldBean.html('productCode','商品コード','THEカラダ')}">Header</div>
↓

```
