package com.example.engine

import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class PredictionResult(
    val nextPeriodDate: LocalDate,
    val daysUntil: Int,
    val phase: String, // "Menstrual", "Follicular", "Ovulatory", "Luteal"
    val phaseProgress: Float, // 0.0 to 1.0
    val fertilityStatus: String, // "Low", "Medium", "High", "Peak"
    val ovulationDate: LocalDate
)

data class DetailedPrediction(
    val start: LocalDate,
    val menstrualEnd: LocalDate,
    val proliferativeStart: LocalDate,
    val proliferativeEnd: LocalDate,
    val ovulationStart: LocalDate,
    val ovulationEnd: LocalDate,
    val secretoryStart: LocalDate,
    val secretoryEnd: LocalDate,
    val ovulation: LocalDate,
    val fertileStart: LocalDate,
    val fertileEnd: LocalDate,
    val nextStart: LocalDate
)

data class DayStatusInfo(
    val status: String?, // "period-start", "period-end", "period-body", "predicted-start", etc.
    val phaseName: String,
    val isSafe: Boolean,
    val hasSymptoms: Boolean,
    val log: LogEntity?,
    val isConfirmedFlow: Boolean,
    val isToday: Boolean,
    val isPast: Boolean
)

object CyclePredictionEngine {

    fun calculateRegularPeriod(lastPeriodStart: String, cycleLength: Int): Pair<LocalDate, LocalDate> {
        val lastDate = LocalDate.parse(lastPeriodStart)
        val nextPeriod = lastDate.plusDays(cycleLength.toLong())
        val ovulationDate = nextPeriod.minusDays(14)
        return Pair(nextPeriod, ovulationDate)
    }

    fun getFullPrediction(
        startStr: String,
        user: UserEntity,
        history: List<CycleEntity>,
        isCurrentCycle: Boolean = false
    ): DetailedPrediction {
        val latestCycle = history.lastOrNull()
        var mEndDays = user.periodLength

        if (isCurrentCycle && latestCycle != null && latestCycle.startDate == startStr) {
            val today = LocalDate.now()
            val cStart = try { LocalDate.parse(startStr) } catch (_: Exception) { today }
            val daysSinceCycleStart = ChronoUnit.DAYS.between(cStart, today).toInt()

            if (daysSinceCycleStart in 0 until 40) {
                if (!latestCycle.endDate.isNullOrEmpty()) {
                    try {
                        val cEnd = LocalDate.parse(latestCycle.endDate)
                        mEndDays = ChronoUnit.DAYS.between(cStart, cEnd).toInt() + 1
                    } catch (_: Exception) {}
                } else {
                    val currentDayOfPeriod = daysSinceCycleStart + 1
                    if (currentDayOfPeriod > mEndDays) {
                        mEndDays = currentDayOfPeriod
                    }
                }
            }
        }

        var fDuration = 6
        val oDuration = 3
        var sDuration = 12

        if (!user.isIrregular) {
            val totalPhaseDays = user.cycleLength - mEndDays
            sDuration = 12
            fDuration = maxOf(1, totalPhaseDays - oDuration - sDuration)
            sDuration = totalPhaseDays - fDuration - oDuration
        }

        val normalizedStart = try { LocalDate.parse(startStr) } catch (_: Exception) { LocalDate.now() }
        val ovulationDate = normalizedStart.plusDays((mEndDays + fDuration + 1).toLong())

        return DetailedPrediction(
            start = normalizedStart,
            menstrualEnd = normalizedStart.plusDays((mEndDays - 1).toLong()),
            proliferativeStart = normalizedStart.plusDays(mEndDays.toLong()),
            proliferativeEnd = normalizedStart.plusDays((mEndDays + fDuration - 1).toLong()),
            ovulationStart = normalizedStart.plusDays((mEndDays + fDuration).toLong()),
            ovulationEnd = normalizedStart.plusDays((mEndDays + fDuration + oDuration - 1).toLong()),
            secretoryStart = normalizedStart.plusDays((mEndDays + fDuration + oDuration).toLong()),
            secretoryEnd = normalizedStart.plusDays((mEndDays + fDuration + oDuration + sDuration - 1).toLong()),
            ovulation = ovulationDate,
            fertileStart = normalizedStart.plusDays((mEndDays + fDuration).toLong()),
            fertileEnd = normalizedStart.plusDays((mEndDays + fDuration + oDuration - 1).toLong()),
            nextStart = normalizedStart.plusDays((mEndDays + fDuration + oDuration + sDuration).toLong())
        )
    }

    fun calculateIrregularPeriod(
        lastPeriodStart: String,
        periodLength: Int = 5,
        userCycleLength: Int = 28,
        isIrregular: Boolean = false
    ): Pair<LocalDate, LocalDate> {
        val lastDate = try { LocalDate.parse(lastPeriodStart) } catch (_: Exception) { LocalDate.now() }
        val effectiveCycleLength = if (isIrregular) (periodLength + 21) else (if (userCycleLength > 0) userCycleLength else 28)
        val nextPeriod = lastDate.plusDays(effectiveCycleLength.toLong())

        val mEnd = periodLength
        var fDuration = 6
        val oDuration = 3
        var sDuration = 12

        if (!isIrregular) {
            val totalPhaseDays = effectiveCycleLength - mEnd
            fDuration = maxOf(1, totalPhaseDays - oDuration - sDuration)
            sDuration = totalPhaseDays - fDuration - oDuration
        }

        val ovulationDate = lastDate.plusDays((mEnd + fDuration + 1).toLong())
        return Pair(nextPeriod, ovulationDate)
    }

    fun calculateAdvancedPrediction(
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>
    ): PredictionResult {
        val lastCycleEntry = cycles.lastOrNull()
        val lastPeriodLog = logs.filter { it.isPeriod }.maxByOrNull { it.date }
        val lastStartDate = lastCycleEntry?.startDate ?: lastPeriodLog?.date

        if (lastStartDate == null) {
            val cycleLength = if (user.cycleLength > 0) user.cycleLength else 28
            val today = LocalDate.now()
            return PredictionResult(
                nextPeriodDate = today.plusDays(cycleLength.toLong()),
                daysUntil = cycleLength,
                phase = "Follicular",
                phaseProgress = 0f,
                fertilityStatus = "Low",
                ovulationDate = today.plusDays((cycleLength / 2).toLong())
            )
        }

        val (predictedNextStart, ovulationDate) = calculateIrregularPeriod(
            lastPeriodStart = lastStartDate,
            periodLength = user.periodLength,
            userCycleLength = user.cycleLength,
            isIrregular = user.isIrregular
        )

        val lastStart = try { LocalDate.parse(lastStartDate) } catch (_: Exception) { LocalDate.now() }
        val today = LocalDate.now()
        val daysSinceStart = ChronoUnit.DAYS.between(lastStart, today).toInt()

        var phase = "Follicular"
        var phaseProgress = 0f

        val mEnd = user.periodLength
        var fDuration = 6
        val oDuration = 3
        var sDuration = 12

        if (!user.isIrregular) {
            val totalPhaseDays = user.cycleLength - mEnd
            fDuration = maxOf(1, totalPhaseDays - oDuration - sDuration)
            sDuration = totalPhaseDays - fDuration - oDuration
        }

        val fEnd = mEnd + fDuration
        val oEnd = mEnd + fDuration + oDuration

        when {
            daysSinceStart < mEnd -> {
                phase = "Menstrual"
                phaseProgress = if (mEnd > 0) daysSinceStart.toFloat() / mEnd.toFloat() else 0f
            }
            daysSinceStart < fEnd -> {
                phase = "Follicular"
                phaseProgress = if (fDuration > 0) (daysSinceStart - mEnd).toFloat() / fDuration.toFloat() else 0f
            }
            daysSinceStart < oEnd -> {
                phase = "Ovulatory"
                phaseProgress = if (oDuration > 0) (daysSinceStart - fEnd).toFloat() / oDuration.toFloat() else 0f
            }
            else -> {
                phase = "Luteal"
                phaseProgress = if (sDuration > 0) (daysSinceStart - oEnd).toFloat() / sDuration.toFloat() else 0f
            }
        }

        val diffToOvulation = abs(ChronoUnit.DAYS.between(today, ovulationDate).toInt())
        val fertilityStatus = when {
            diffToOvulation == 0 -> "Peak"
            diffToOvulation <= 2 -> "High"
            diffToOvulation <= 4 -> "Medium"
            else -> "Low"
        }

        return PredictionResult(
            nextPeriodDate = predictedNextStart,
            daysUntil = ChronoUnit.DAYS.between(today, predictedNextStart).toInt(),
            phase = phase,
            phaseProgress = phaseProgress.coerceIn(0f, 1f),
            fertilityStatus = fertilityStatus,
            ovulationDate = ovulationDate
        )
    }

    fun getFuturePredictions(
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>,
        count: Int = 6
    ): List<DetailedPrediction> {
        val lastCycleEntry = cycles.lastOrNull()
        val lastPeriodLog = logs.filter { it.isPeriod }.maxByOrNull { it.date }
        val lastStartDate = lastCycleEntry?.startDate ?: lastPeriodLog?.date ?: return emptyList()

        val results = mutableListOf<DetailedPrediction>()
        var currentStart = lastStartDate

        val first = getFullPrediction(currentStart, user, cycles, isCurrentCycle = true)
        results.add(first)
        currentStart = first.nextStart.toString()

        for (i in 1..count) {
            val nextPred = getFullPrediction(currentStart, user, cycles, isCurrentCycle = false)
            results.add(nextPred)
            currentStart = nextPred.nextStart.toString()
        }

        return results
    }

    fun getDayStatus(
        day: LocalDate,
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>,
        futureCycles: List<DetailedPrediction>
    ): DayStatusInfo {
        val today = LocalDate.now()
        val dateStr = day.toString()
        val log = logs.find { it.date == dateStr }

        var status: String? = null
        if (log?.isPeriod == true) status = "period-start"
        else if (log?.isPeriodEnd == true) status = "period-end"

        val isPeriodDay = cycles.any { c ->
            try {
                val start = LocalDate.parse(c.startDate)
                var endCandidate = if (!c.endDate.isNullOrEmpty()) {
                    LocalDate.parse(c.endDate)
                } else {
                    start.plusDays((user.periodLength - 1).toLong())
                }

                if (c.endDate.isNullOrEmpty() && (day >= start && day <= today)) {
                    val predictedEnd = start.plusDays((user.periodLength - 1).toLong())
                    if (today.isAfter(predictedEnd)) {
                        endCandidate = today
                    }
                }
                day >= start && day <= endCandidate
            } catch (_: Exception) {
                false
            }
        }

        if (isPeriodDay && status == null) {
            status = "period-body"
        }

        if (status == null) {
            val allowedRangeStart = today.minusMonths(2).withDayOfMonth(1)
            val allowedRangeEnd = today.plusMonths(6)

            if (day >= allowedRangeStart && day <= allowedRangeEnd) {
                for (fc in futureCycles) {
                    val s = fc.start
                    val mE = fc.menstrualEnd
                    val pS = fc.proliferativeStart
                    val pE = fc.proliferativeEnd
                    val oS = fc.ovulationStart
                    val oE = fc.ovulationEnd
                    val secS = fc.secretoryStart
                    val secE = fc.secretoryEnd

                    if (day in s..mE) status = "predicted-body"
                    else if (day in pS..pE) status = "predicted-follicular"
                    else if (day in oS..oE) status = "predicted-ovulation"
                    else if (day in secS..secE) status = "predicted-luteal"

                    if (day == s) status = "predicted-start"
                }
            }
        }

        var isSafe = false
        val isPeriodStatus = status?.contains("period") == true || status?.contains("menstrual") == true || status == "predicted-body" || status == "predicted-start"
        val isFertileStatus = status?.contains("fertile") == true || status?.contains("ovulation") == true || status?.contains("follicular") == true

        if (!isPeriodStatus && !isFertileStatus) {
            if (status == "predicted-luteal" || log?.mood == "Safe Day") {
                isSafe = true
            }
        }

        var phaseName = "Follicular Phase"
        when {
            isPeriodStatus -> phaseName = "Menstrual Phase"
            status?.contains("ovulation") == true -> phaseName = "Ovulation Phase"
            status?.contains("luteal") == true -> phaseName = "Luteal Phase"
            status?.contains("follicular") == true -> phaseName = "Follicular Phase"
        }

        return DayStatusInfo(
            status = status,
            phaseName = phaseName,
            isSafe = isSafe,
            hasSymptoms = (log?.symptoms?.isNotEmpty() == true),
            log = log,
            isConfirmedFlow = (log?.intensity != null && log.intensity != "None"),
            isToday = (day == today),
            isPast = day.isBefore(today)
        )
    }

    fun getDailyTip(phase: String): String {
        val p = phase.lowercase()
        return when {
            p.contains("menstrual") -> "Focus on rest and iron-rich foods today. Gentle stretching can help with discomfort."
            p.contains("follicular") -> "Energy levels are rising! Great time for new projects and more intense workouts."
            p.contains("ovulat") -> "You're at your peak social energy. Stay hydrated and enjoy your natural glow."
            p.contains("luteal") -> "Listen to your body. Magnesium-rich foods can help reduce bloating and mood shifts."
            else -> "Track your symptoms daily to get more personalized health insights."
        }
    }
}
