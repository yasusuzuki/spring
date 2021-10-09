package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller
class AsyncTaskList(var config: ConfigDef, var dic: Dictionary,var db:Database, var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(AsyncTaskList::class.java)

    data class Request(
        var policyNumber: String = "",
        var hideManualEntry: Boolean = false,
        var asyncJobId: String = "",
        var maxFetchRows: Int = 30,
        var businessTaskId: String = "", //POST専用
        var verboseMode: Boolean = false
    )
    
    @GetMapping("/asyncTaskList")
    fun executeEnquiry(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process GET asyncTaskList:  req = ${req}")
        //画面描画
        return execute(req,model,false);
    }

    @PostMapping("/asyncTaskList")
    fun executeUpdate(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process POST asyncTaskList:  req = ${req}")
        //画面描画。POST引数をtrueにすると、businessTaskIdパラメータにもとづいて非同期投入する
        //F5で二重ＰＯＳＴしても、主キーの重複登録でSQLがエラーになるので、複雑な二重登録防止措置は実装しない
        //ただし、わかりやすさのため、ＰＯＳＴ時に「登録」ボタンを非活性にする程度の簡単な措置は実装する
        return execute(req,model,true);
    }

    fun execute(@ModelAttribute req:Request, model:Model, POST:Boolean ): String {
        var sql1 = "SELECT * FROM TASK_MANAGEMENT WHERE 1 = 1 "
        if ( req.asyncJobId != "" ){
            sql1 += " AND ASC_JOB_ID LIKE '%${req.asyncJobId}%' "
        }
        if ( req.policyNumber != "" ){
            sql1 += " AND ASC_PRM LIKE '%policyNm=%${req.policyNumber}%,policynumberEdabanNm=%' "
        }
        if ( req.hideManualEntry ){
            sql1 += " AND DTA_RGT_PRC_KN not like '%AsyncTestSupport%' "
        }
        sql1 += " ORDER BY PRC_DT DESC "
        sql1 += " FETCH FIRST ${req.maxFetchRows} ROWS ONLY "

        var sql2 = "SELECT * FROM ASYNC_PROCESS_REGISTRATION_PARAMETER WHERE 1 = 1 "
        if ( req.asyncJobId != "" ){
            sql2 += " AND ASC_JOB_ID LIKE '%${req.asyncJobId}%' "
        }        
        if ( req.policyNumber != "" ){
            sql2 += " AND ASC_PRM LIKE '%policyNm=%${req.policyNumber}%,policynumberEdabanNm=%' "
        }
        sql2 += " FETCH FIRST ${req.maxFetchRows} ROWS ONLY "
        //callback
        var toBeRegistered = mutableListOf<Pair<String,String>>() //
        var callback = mapOf(
            "PREPEND" to fun(_:String?,_:String?,_:List<String>?,values:Map<String,Any?>?):String{
                //注意：　key と valがブランクになるのでvaluesから取得する
                require( values != null ) { "values must be non-null, but was null" }
                //TODO: 他にいい方法はないか。。values.get("xx")をそのまま`as String`でキャストしていると
                // values.get("xx")が仮にnullのときにキャストに失敗して異常終了する
                var bntId = values.get("ビジネスタスクID")?.let{ it as String } ?: "" 
                var ascJobId = values.get("非同期ジョブID")?.let{ it as String } ?: ""
                var asyncParameter = values.get("非同期パラメータ")?.let{ it as String } ?: ""
                //ASC_PRMから証券番号と計上番号を取得し、テストケースIDを生成する
                var matches = "policyNm=([A-Z0-9]+),policynumberEdabanNm=,contractWrittenEdabanNm=([0-9]+)".toRegex().find(asyncParameter)
                var testcaseId = ""
                if ( matches != null ) {
                    testcaseId = ascJobId + "-" + matches.groupValues.getOrElse(1,{""}) + "-" + matches.groupValues.getOrElse(2,{""})
                }                    
                var checked = ""
                var disabled = ""
                if ( testcaseId == "" || POST ) {
                    //証券番号と計上番号を取得できずテストケースIDを生成できない場合は、登録不可能と判断しチェックボックスを非活性
                    //二重サブミット防止のため、POST時はチェックボックスを非活性
                    disabled = "disabled"  
                }
                
                if ( POST && req.businessTaskId.split(',').contains(bntId) ) {
                    //POST時に、どの行を選択したかを利用者に見せるため、POSTパラメータからチェック有無を再現する
                    checked = "checked"
                    //非同期処理登録の対象として追加
                    toBeRegistered.add(Pair(testcaseId,bntId))
                }
                return "<INPUT TYPE='checkbox'  NAME='businessTaskId' value='${bntId}' ${checked} ${disabled}>${testcaseId}"
            },
            "H_PREPEND" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                var disabled = if ( POST ) {"disabled"} else {""}
                var html = "<INPUT TYPE='checkbox' onClick='toggleAllMsg(this, \"businessTaskId\");' ${disabled}>&nbsp;Test Case ID"
                return html
            },
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )
        var callback2 = mapOf(
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )

        model["dataTable1"] = query.buildHTMLFromSQL(dic.L2P_SQL(sql1),callback)
        //非同期処理登録を行う         
        if ( POST == true ) {
            //登録ボタンの非活性などの描画制御用のフラグをセット
            model["hasBeenPosted"] = POST
            //チェックされた行に対して、非同期ジョブの登録を行う
            model["registrationMessages"] = toBeRegistered.map{ insertDBRecord(it.first, it.second) }.joinToString("")
        }
        model["dataTable2"] = query.buildHTMLFromSQL(dic.L2P_SQL(sql2),callback2)
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        return "asyncTaskList"
    }

    fun insertDBRecord(testcaseId:String, businessTaskId:String):String{
        var sql = """ 
        | INSERT INTO ASYNC_PROCESS_REGISTRATION_PARAMETER 
        | SELECT
        |  '${testcaseId}' AS TST_CAS_ID, 
        |  0 AS REC_NO,
        |  T.ASC_PRM ,
        |  T.ASC_JOB_ID,
        |  'L' AS ASC_PRP,
        |  ''  AS JOB_SCD_JOB_ID,
        |  1 AS SYS_ERR_PRC_PTN,
        |  1 AS FST_CAL_FLG,
        |  '' AS LGC_DEL_CD,
        |  T.DTA_RGT_TS  AS DTA_RGT_TS,
        |  'RETRY' AS DTA_RGT_USR_ID,
        |  0 AS DTA_RGT_PRC_KN,
        |  T.DTA_UPD_TS, 
        |  'RETRY'  AS DTA_UPD_USR_ID, 
        |  0 AS DTA_UPD_PRC_KN,
        |  1 AS VER_NO,
        |  CAST(NULL AS CHAR)  AS BNT_IDA_EK, 
        |  CAST(NULL AS CHAR)  AS EVN_IDN_TMS_DA
        | FROM TASK_MANAGEMENT T WHERE BNT_ID = '${businessTaskId}'
        """.trimMargin()
        val con = db.getConnection(config.getCurrentEnvironment())
        val resultMsg = db.execute(con, sql)
        con.close()
        return "<H5>TST_CAS_ID=[${testcaseId}] を BNT_ID=[${businessTaskId}]をもとに登録します</H5>" + resultMsg
    }
}