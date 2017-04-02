package com.example


class GroovyHeader{
	String render(){
		return html
	}
String html="""
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.6/semantic.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.6/semantic.min.css"  />
<script>
  console.log("ok1");
  \$( function(){
	  //サイドバー開閉のイベント登録
     \$('#js-sidebar').click(function() {
   	  console.log("ok")
       \$('.ui.sidebar').sidebar('toggle');
     });
     //ドロップダウンの初期化
     \$('.ui.dropdown').dropdown();
  });
</script>
<style>
.ui.fluid.container {
	margin-top: 90 !important;
    margin-left: 30 !important;
    margin-right: 0 !important;
}
.ui.labeled.input {
    margin-top: 5 px;
    margin-bottom: 5px;
}
.column {
    /*border: 1px solid blue;*/
}

</style>
</head>
	
"""	
}


class GroovyMenu {
	String render(){
		return html
	}
String html="""
<div class="ui sidebar vertical menu">

<div class="ui list">
  <div class="item">
    <i class="folder icon"></i>
    <div class="content">
      <div class="header">保険設計</div>
      <div class="description">Source files for project</div>
      <div class="list">
        <div class="item">
          <i class="folder icon"></i>
          <div class="content">
            <div class="header">満期</div>
            <div class="description">Your site's theme</div>
          </div>
        </div>
        <div class="item">
          <i class="folder icon"></i>
          <div class="content">
            <div class="header">契約照会</div>
            <div class="description">Packaged theme files</div>
            <div class="list">
              <div class="item">
                <i class="folder icon"></i>
                <div class="content">
                  <div class="header">清算入金</div>
                  <div class="description">Default packaged theme</div>
                </div>
              </div>
              <div class="item">
                <i class="folder icon"></i>
                <div class="content">
                  <div class="header">経理月例	</div>
                  <div class="description">Packaged themes are also available in this folder</div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</div>
</div>


"""
}
