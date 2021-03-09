# ChiliFlakeKt

RubyのSnowflake実装のChiliflakeのKotlin実装  
ref: https://github.com/ma2shita/chiliflake/blob/master/lib/chiliflake.rb

## 使い方
```
object GenerateId {
    var sequenceCounter: Long = 0
    fun call(): Long {
        // マシンID
        val machineId = 1L % 1024
        // シーケンスカウンター
        sequenceCounter += 1
        // 注意: 1ms以内に4096個以上作成される場合は同じsecがID生成に使われるのでIDが衝突する
        val sec = (sequenceCounter) % 4096
        return ChiliFlakeKt(machineId, sec).generate()
    }
}
```