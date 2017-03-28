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

