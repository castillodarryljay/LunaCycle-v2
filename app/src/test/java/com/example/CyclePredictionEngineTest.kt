package com.example

import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.engine.CyclePredictionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CyclePredictionEngineTest {

    @Test
    fun testRegularPeriodPrediction() {
        val today = LocalDate.now()
        val user = UserEntity(
            cycleLength = 28,
            periodLength = 5,
            isIrregular = false
        )
        val cycles = listOf(
            CycleEntity(id = "1", startDate = today.minusDays(14).toString(), length = 28)
        )
        val logs = emptyList<LogEntity>()

        val prediction = CyclePredictionEngine.calculateAdvancedPrediction(user, cycles, logs)

        assertNotNull(prediction)
        assertEquals(14, prediction.daysUntil)
        assertEquals("Ovulatory", prediction.phase)
        assertTrue(prediction.fertilityStatus in listOf("High", "Peak"))
    }

    @Test
    fun testDailyTips() {
        val tip = CyclePredictionEngine.getDailyTip("Menstrual Phase")
        assertTrue(tip.contains("iron-rich"))
    }
}
