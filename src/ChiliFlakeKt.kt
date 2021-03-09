import java.util.*

class ChiliFlakeKt(_machineId: Long, _seq: Long = 0) {
    companion object {
        const val OFFSET_TS_W_MILLIS = 1288834974657
        const val TS_WIDTH = 41
        const val GEN_ID_WIDTH = 10
        const val SEQ_WIDTH = 12
    }
    private var lastTs: Long = nowWMillis()
    private var generateId = _machineId
    private var seq = _seq

    // validate input
    init {
        chiliFlake(lastTs, generateId, seq)
    }

    fun generate(): Long {
        var ts = nowWMillis()
        if (ts < lastTs) {
            throw ChiliFlakeInvalidSystemClockError("Last timestamp was bigger than now")
        }
        lastTs = ts
        seq = (seq + 1L) % (1L.shl(SEQ_WIDTH))
        return chiliFlake(lastTs, generateId, seq)
    }

    private fun parse(flakeId: Long): Map<String, Long> {
        val tsWMillis: Long = flakeId.shr(SEQ_WIDTH + GEN_ID_WIDTH)
        var countOfBinaryString: Int = flakeId.shr(SEQ_WIDTH).toString(2).count()
        val generateId = flakeId.shr(SEQ_WIDTH).toString(2).substring(countOfBinaryString - GEN_ID_WIDTH, countOfBinaryString).toLong(2)
        countOfBinaryString = flakeId.toString(2).count()
        val sequence = flakeId.toString(2).substring(countOfBinaryString - SEQ_WIDTH, countOfBinaryString).toLong(2)
        return mapOf("tsWMillis" to tsWMillis, "generateId" to generateId, "sequence" to sequence)
    }

    // return time of id generated
    fun time(flakeId: Long): Date {
        val ts = OFFSET_TS_W_MILLIS + parse(flakeId)["tsWMillis"]!!
        return Date(ts)
    }

    private fun chiliFlake(tsWMillis: Long, generateId: Long, seq: Long): Long {
        if (tsWMillis > (1L.shl(TS_WIDTH) - 1L)) {
            throw ChiliFlakeOverflowError("Timestamp limit is 2080-07-11 02:30:30.999 +0900")
        }
        val t = (tsWMillis - OFFSET_TS_W_MILLIS).shl(GEN_ID_WIDTH + SEQ_WIDTH)
        if (generateId > (1L.shl(GEN_ID_WIDTH) - 1L)) {
            throw ChiliFlakeInvalidSystemClockError("Generator ID limit is between 0 and 1023")
        }
        val m = generateId.shl(SEQ_WIDTH)
        val s = seq % (1L.shl(SEQ_WIDTH))
        return t + m + s
    }

    // return current unixtime
    private fun nowWMillis(): Long {
        return System.currentTimeMillis()
    }
}

// Exeption
class ChiliFlakeOverflowError(msg: String): Exception(msg)
class ChiliFlakeInvalidSystemClockError(msg: String): Exception(msg)